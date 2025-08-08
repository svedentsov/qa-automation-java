package com.svedentsov.db.helper;

import com.svedentsov.db.exception.DataAccessException;
import io.qameta.allure.Step;
import jakarta.persistence.*;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.PessimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.*;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import java.io.Serializable;
import java.sql.Connection;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Продвинутый универсальный исполнитель для выполнения операций с базой данных с использованием Hibernate.
 * <p>
 * Предоставляет текучий (fluent) DSL для выполнения CRUD-операций, запросов (HQL, SQL, Named Query),
 * массовых операций и управления транзакциями с поддержкой повторных попыток (retries).
 * <p>
 * <b>Важно:</b> Экземпляр {@code DbExecutor} является stateful и <b>не потокобезопасен</b>.
 * Рекомендуется создавать новый экземпляр для каждой логической операции или использовать метод {@link #clear()}
 * для сброса состояния перед повторным использованием в рамках одного теста.
 * <p>
 * <b>Пример использования:</b>
 * <pre>{@code
 * DbExecutor<User> userExecutor = DbExecutor.create(sessionFactory, User.class);
 *
 * // Поиск пользователей по статусу с сортировкой
 * List<User> activeUsers = userExecutor
 *     .setHqlQuery("FROM User WHERE status = :status ORDER BY registrationDate DESC")
 *     .addParameter("status", "ACTIVE")
 *     .setMaxResults(10)
 *     .getResultList();
 *
 * // Обновление в одной транзакции с ретраями
 * userExecutor.clear()
 *     .withRetries(3, Duration.ofMillis(200))
 *     .executeInTransaction(session -> {
 *         User user = session.get(User.class, 1L);
 *         user.setLastLogin(Instant.now());
 *         session.merge(user);
 *     });
 * }</pre>
 *
 * @param <T> Тип сущности, с которой работает данный экземпляр исполнителя.
 */
@Slf4j
public final class DbExecutor<T> {

    // Константы
    private static final int DEFAULT_BATCH_SIZE = 50;
    private static final int DEFAULT_RETRIES = 0;
    private static final Duration DEFAULT_RETRY_DELAY = Duration.ofMillis(100);

    // Состояние экземпляра
    private final SessionFactory sessionFactory;
    private final Class<T> entityClass;
    private final Map<String, Object> parameters = new HashMap<>();
    private final Map<String, Object> hints = new HashMap<>();
    private final List<String> joinFetchPaths = new ArrayList<>();
    private String hqlQuery;
    private String sqlQuery;
    private String namedQuery;
    private int batchSize = DEFAULT_BATCH_SIZE;
    private int fetchSize;
    private int maxResults;
    private int firstResult;
    private LockModeType lockModeType;
    private boolean readOnly;
    private boolean cacheable;
    private String cacheRegion;
    private FlushModeType flushMode;
    private int isolationLevel = Connection.TRANSACTION_READ_COMMITTED;
    private int retries = DEFAULT_RETRIES;
    private Duration retryDelay = DEFAULT_RETRY_DELAY;

    /**
     * Статический фабричный метод для создания экземпляра DbExecutor.
     *
     * @param sessionFactory Фабрика сессий Hibernate.
     * @param entityClass    Класс сущности.
     * @param <E>            Тип сущности.
     * @return Новый экземпляр DbExecutor.
     */
    public static <E> DbExecutor<E> create(final SessionFactory sessionFactory, final Class<E> entityClass) {
        return new DbExecutor<>(sessionFactory, entityClass);
    }

    /**
     * Приватный конструктор. Используйте статический метод {@link #create}.
     */
    private DbExecutor(final SessionFactory sessionFactory, final Class<T> entityClass) {
        this.sessionFactory = requireNonNull(sessionFactory, "SessionFactory не должен быть null");
        this.entityClass = requireNonNull(entityClass, "Класс сущности не должен быть null");
    }

    // Конфигурация состояния

    /**
     * Сбрасывает внутреннее состояние исполнителя, позволяя переиспользовать один экземпляр
     * для выполнения серии независимых запросов. Очищает HQL/SQL запросы, параметры,
     * настройки кэширования и другие опции.
     *
     * @return Текущий экземпляр для построения цепочки вызовов.
     */
    public DbExecutor<T> clear() {
        hqlQuery = null;
        sqlQuery = null;
        namedQuery = null;
        parameters.clear();
        hints.clear();
        joinFetchPaths.clear();
        maxResults = 0;
        firstResult = 0;
        fetchSize = 0;
        readOnly = false;
        cacheable = false;
        cacheRegion = null;
        lockModeType = null;
        flushMode = null;
        retries = DEFAULT_RETRIES;
        retryDelay = DEFAULT_RETRY_DELAY;
        log.trace("Состояние DbExecutor для {} было сброшено.", entityClass.getSimpleName());
        return this;
    }

    /**
     * Устанавливает HQL-запрос для выполнения.
     *
     * @param hqlQuery HQL-строка запроса.
     * @return Текущий экземпляр для построения цепочки вызовов.
     */
    public DbExecutor<T> setHqlQuery(String hqlQuery) {
        this.hqlQuery = hqlQuery;
        return this;
    }

    /**
     * Устанавливает нативный SQL-запрос для выполнения.
     *
     * @param sqlQuery SQL-строка запроса.
     * @return Текущий экземпляр для построения цепочки вызовов.
     */
    public DbExecutor<T> setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
        return this;
    }

    /**
     * Устанавливает имя именованного запроса (Named Query) для выполнения.
     *
     * @param namedQuery Имя запроса, определенного в аннотациях сущности.
     * @return Текущий экземпляр для построения цепочки вызовов.
     */
    public DbExecutor<T> setNamedQuery(String namedQuery) {
        this.namedQuery = namedQuery;
        return this;
    }

    /**
     * Устанавливает все параметры для запроса разом из карты.
     *
     * @param parameters Карта с именами и значениями параметров.
     * @return Текущий экземпляр для построения цепочки вызовов.
     */
    public DbExecutor<T> setParameters(Map<String, Object> parameters) {
        this.parameters.putAll(requireNonNull(parameters, "Карта параметров не должна быть null"));
        return this;
    }

    /**
     * Добавляет один именованный параметр в запрос.
     *
     * @param name  Имя параметра.
     * @param value Значение параметра.
     * @return Текущий экземпляр для построения цепочки вызовов.
     */
    public DbExecutor<T> addParameter(String name, Object value) {
        this.parameters.put(requireNonNull(name, "Имя параметра не должно быть null"), value);
        return this;
    }

    /**
     * Устанавливает размер пакета для пакетных операций (batch).
     *
     * @param batchSize Размер пакета. Если <= 0, используется значение по умолчанию.
     * @return Текущий экземпляр для построения цепочки вызовов.
     */
    public DbExecutor<T> setBatchSize(int batchSize) {
        this.batchSize = batchSize > 0 ? batchSize : DEFAULT_BATCH_SIZE;
        return this;
    }

    /**
     * Устанавливает размер выборки (fetch size) для JDBC-драйвера.
     *
     * @param fetchSize Количество строк, извлекаемых из БД за один раз.
     * @return Текущий экземпляр для построения цепочки вызовов.
     */
    public DbExecutor<T> setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
        return this;
    }

    /**
     * Устанавливает максимальное количество возвращаемых результатов (пагинация).
     *
     * @param maxResults Максимальное количество записей.
     * @return Текущий экземпляр для построения цепочки вызовов.
     */
    public DbExecutor<T> setMaxResults(int maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    /**
     * Устанавливает позицию первого результата для выборки (пагинация).
     *
     * @param firstResult Индекс первой записи (начиная с 0).
     * @return Текущий экземпляр для построения цепочки вызовов.
     */
    public DbExecutor<T> setFirstResult(int firstResult) {
        this.firstResult = firstResult;
        return this;
    }

    /**
     * Устанавливает режим блокировки для извлекаемых сущностей.
     *
     * @param lockModeType Тип блокировки из {@link LockModeType}.
     * @return Текущий экземпляр для построения цепочки вызовов.
     */
    public DbExecutor<T> setLockMode(LockModeType lockModeType) {
        this.lockModeType = lockModeType;
        return this;
    }

    /**
     * Устанавливает режим "только для чтения" для сессии или запроса.
     *
     * @param readOnly {@code true} для включения режима "только для чтения".
     * @return Текущий экземпляр для построения цепочки вызовов.
     */
    public DbExecutor<T> setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }

    /**
     * Управляет кэшированием запроса (кэш второго уровня).
     *
     * @param cacheable {@code true} для включения кэширования.
     * @return Текущий экземпляр для построения цепочки вызовов.
     */
    public DbExecutor<T> setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
        return this;
    }

    /**
     * Устанавливает имя региона кэша второго уровня для запроса.
     *
     * @param cacheRegion Имя региона кэша.
     * @return Текущий экземпляр для построения цепочки вызовов.
     */
    public DbExecutor<T> setCacheRegion(String cacheRegion) {
        this.cacheRegion = cacheRegion;
        return this;
    }

    /**
     * Устанавливает режим очистки (flush) для сессии.
     *
     * @param flushMode Режим flush из {@link FlushModeType}.
     * @return Текущий экземпляр для построения цепочки вызовов.
     */
    public DbExecutor<T> setFlushMode(FlushModeType flushMode) {
        this.flushMode = flushMode;
        return this;
    }

    /**
     * Устанавливает все подсказки (hints) для JPA-провайдера разом.
     *
     * @param hints Карта с подсказками.
     * @return Текущий экземпляр для построения цепочки вызовов.
     */
    public DbExecutor<T> setHints(Map<String, Object> hints) {
        this.hints.putAll(requireNonNull(hints, "Карта подсказок не должна быть null"));
        return this;
    }

    /**
     * Добавляет одну подсказку (hint) для JPA-провайдера.
     *
     * @param name  Имя подсказки.
     * @param value Значение подсказки.
     * @return Текущий экземпляр для построения цепочки вызовов.
     */
    public DbExecutor<T> addHint(String name, Object value) {
        this.hints.put(requireNonNull(name, "Имя подсказки не должно быть null"), value);
        return this;
    }

    /**
     * Устанавливает уровень изоляции для транзакции.
     *
     * @param isolationLevel Уровень изоляции из {@link java.sql.Connection}.
     * @return Текущий экземпляр для построения цепочки вызовов.
     */
    public DbExecutor<T> setTransactionIsolationLevel(int isolationLevel) {
        this.isolationLevel = isolationLevel;
        return this;
    }

    /**
     * Настраивает механизм повторных попыток для транзакций.
     * Полезно для обработки временных ошибок, таких как deadlock или optimistic lock.
     *
     * @param count Количество повторных попыток (0 - без повторов).
     * @param delay Задержка между попытками.
     * @return Текущий экземпляр для построения цепочки вызовов.
     */
    public DbExecutor<T> withRetries(int count, Duration delay) {
        this.retries = Math.max(0, count);
        this.retryDelay = requireNonNull(delay, "Задержка не может быть null");
        return this;
    }

    /**
     * Добавляет `JOIN FETCH` к HQL запросу для решения проблемы N+1
     * путем жадной загрузки связанных сущностей или коллекций.
     *
     * @param associationPath Путь к ассоциации (например, "user.roles").
     * @return Текущий экземпляр для построения цепочки вызовов.
     */
    public DbExecutor<T> addJoinFetch(String associationPath) {
        this.joinFetchPaths.add(associationPath);
        return this;
    }

    // CRUD-операции

    /**
     * Сохраняет новую сущность в базе данных (операция persist).
     *
     * @param entity Сущность для сохранения. Не должна быть null.
     * @return Сохраненная сущность (может содержать сгенерированный ID).
     */
    @Step("Сохранение сущности: {entity}")
    public T save(final T entity) {
        requireNonNull(entity, "Сущность для сохранения не должна быть null");
        return executeInTransaction(session -> {
            session.persist(entity);
            log.info("Сущность успешно сохранена: {}", entity);
            return entity;
        });
    }

    /**
     * Обновляет состояние существующей detached-сущности (операция merge).
     *
     * @param entity Сущность для обновления. Не должна быть null.
     * @return Обновленная, управляемая (managed) сущность.
     */
    @Step("Обновление сущности: {entity}")
    public T update(final T entity) {
        requireNonNull(entity, "Сущность для обновления не должна быть null");
        return executeInTransaction(session -> {
            T mergedEntity = session.merge(entity);
            log.info("Сущность успешно обновлена: {}", mergedEntity);
            return mergedEntity;
        });
    }

    /**
     * Сохраняет новую или обновляет существующую (detached) сущность.
     *
     * @param entity Сущность для сохранения или обновления. Не должна быть null.
     * @return Сохраненная или обновленная сущность.
     * @deprecated Метод {@code saveOrUpdate} является специфичным для Hibernate и может быть удален в будущих версиях.
     * Предпочтительно использовать {@link #save(Object)} или {@link #update(Object)} в зависимости от контекста.
     */
    @Deprecated(since = "6.0", forRemoval = true)
    @Step("Сохранение или обновление сущности: {entity}")
    public T saveOrUpdate(final T entity) {
        requireNonNull(entity, "Сущность для сохранения или обновления не должна быть null");
        return executeInTransaction(session -> {
            session.saveOrUpdate(entity);
            log.info("Сущность успешно сохранена или обновлена: {}", entity);
            return entity;
        });
    }

    /**
     * Сливает состояние переданной сущности с состоянием в текущем контексте персистентности.
     *
     * @param entity Сущность для слияния. Не должна быть null.
     * @return Управляемая (managed) сущность после слияния.
     */
    @Step("Слияние сущности с состоянием в базе данных: {entity}")
    public T merge(final T entity) {
        requireNonNull(entity, "Сущность для слияния не должна быть null");
        return executeInTransaction(session -> {
            T mergedEntity = session.merge(entity);
            log.info("Сущность успешно слита: {}", mergedEntity);
            return mergedEntity;
        });
    }

    /**
     * Находит сущность по ее первичному ключу.
     *
     * @param id Идентификатор сущности. Не должен быть null.
     * @return {@link Optional} с найденной сущностью или пустой, если сущность не найдена.
     */
    @Step("Получение сущности из базы данных по идентификатору: {id}")
    public Optional<T> getById(final Object id) {
        requireNonNull(id, "Идентификатор сущности не должен быть null");
        return executeInTransaction(session -> {
            T entity = session.get(entityClass, (Serializable) id);
            if (entity != null) {
                applyOptionsToEntity(session, entity);
                log.info("Сущность с ID '{}' успешно получена.", id);
            } else {
                log.info("Сущность с ID '{}' не найдена.", id);
            }
            return Optional.ofNullable(entity);
        });
    }

    /**
     * Удаляет сущность из базы данных.
     *
     * @param entity Сущность для удаления. Не должна быть null.
     */
    @Step("Удаление сущности: {entity}")
    public void delete(final T entity) {
        requireNonNull(entity, "Сущность для удаления не должна быть null");
        executeInTransaction(session -> {
            session.remove(session.contains(entity) ? entity : session.merge(entity));
            log.info("Сущность успешно удалена: {}", entity);
            return null;
        });
    }

    /**
     * Удаляет сущность по ее первичному ключу без предварительной загрузки.
     *
     * @param id Идентификатор сущности для удаления.
     */
    @Step("Удаление сущности по ID: {id}")
    public void deleteById(final Serializable id) {
        requireNonNull(id, "ID для удаления не должен быть null");
        String hql = String.format("DELETE FROM %s WHERE id = :id", entityClass.getSimpleName());
        clear().setHqlQuery(hql).addParameter("id", id).executeUpdateOrDelete();
    }

    // Методы выполнения запросов

    /**
     * Выполняет сконфигурированный запрос (HQL, SQL или Named Query) и возвращает список результатов.
     *
     * @return Список сущностей. Может быть пустым.
     * @throws IllegalStateException если тип запроса (HQL, SQL, Named) не был установлен.
     */
    @Step("Получение списка сущностей")
    public List<T> getResultList() {
        if (hqlQuery != null) return executeHqlQuery();
        if (sqlQuery != null) return executeSqlQuery();
        if (namedQuery != null) return executeNamedQuery();
        throw new IllegalStateException("Тип запроса (HQL, SQL или Named Query) не был задан. Невозможно выполнить getResultList().");
    }

    /**
     * Выполняет сконфигурированный запрос и возвращает единственный результат.
     *
     * @return Найденная сущность.
     * @throws NoResultException        если результат не найден.
     * @throws NonUniqueResultException если найдено более одного результата.
     */
    @Step("Получение одной сущности")
    public T getSingleResult() {
        List<T> results = getResultList();
        if (results.isEmpty()) {
            throw new NoResultException("Ожидался один результат, но не найдено ни одного.");
        }
        if (results.size() > 1) {
            throw new NonUniqueResultException("Ожидался один результат, но найдено " + results.size());
        }
        return results.get(0);
    }

    /**
     * Выполняет сконфигурированный запрос и возвращает результат в виде {@link Optional}.
     *
     * @return {@link Optional} с найденной сущностью или пустой.
     * @throws NonUniqueResultException если найдено более одного результата.
     */
    @Step("Получение опциональной сущности")
    public Optional<T> getOptionalResult() {
        List<T> results = getResultList();
        if (results.size() > 1) {
            throw new NonUniqueResultException("Ожидался один или ноль результатов, но найдено " + results.size());
        }
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Проверяет существование хотя бы одной записи, удовлетворяющей условиям запроса.
     *
     * @return {@code true}, если запись существует, иначе {@code false}.
     */
    @Step("Проверка существования записи")
    public boolean exists() {
        setMaxResults(1);
        return !getResultList().isEmpty();
    }

    /**
     * Выполняет скалярный запрос (например, COUNT, SUM, AVG) и возвращает результат.
     *
     * @param type Класс ожидаемого результата (e.g., Long.class, BigDecimal.class).
     * @param <S>  Тип скалярного результата.
     * @return {@link Optional} с результатом или пустой, если результат null.
     */
    @Step("Получение скалярного результата запроса")
    public <S> Optional<S> getScalarResult(final Class<S> type) {
        requireNonNull(type, "Тип скалярного результата не должен быть null");
        String queryStr = Objects.requireNonNullElseGet(hqlQuery, () -> sqlQuery);
        requireNonNull(queryStr, "Для скалярного запроса должен быть указан HQL или SQL");

        log.info("Выполнение скалярного запроса: [{}], параметры: {}", queryStr, parameters);
        return executeInTransaction(session -> {
            Query<S> query = (hqlQuery != null)
                    ? session.createQuery(queryStr, type)
                    : session.createNativeQuery(queryStr, type);
            applyQuerySettings(query);
            return query.uniqueResultOptional();
        });
    }

    /**
     * Выполняет HQL-запрос и проецирует результаты в список DTO (Data Transfer Objects).
     * Требует наличия в HQL-запросе `SELECT new com.example.MyDto(...)`.
     *
     * @param dtoClass Класс DTO, в который будут спроецированы результаты.
     * @param <D>      Тип DTO.
     * @return Список DTO.
     */
    @Step("Проекция результата запроса на DTO: {dtoClass.simpleName}")
    public <D> List<D> projectTo(final Class<D> dtoClass) {
        requireNonNull(hqlQuery, "Проекция поддерживается только для HQL-запросов");
        log.info("Выполнение HQL-проекции на DTO '{}': [{}], параметры: {}", dtoClass.getSimpleName(), hqlQuery, parameters);

        return executeInTransaction(session -> {
            Query<D> query = session.createQuery(hqlQuery, dtoClass);
            applyQuerySettings(query);
            return query.getResultList();
        });
    }

    // Массовые операции

    /**
     * Выполняет пакетную операцию сохранения или обновления для списка сущностей.
     *
     * @param entities Список сущностей для обработки.
     */
    @Step("Выполнение пакетной операции (batch merge)")
    public void executeBatchOperation(final List<T> entities) {
        requireNonNull(entities, "Список сущностей для пакетной операции не должен быть null");
        if (entities.isEmpty()) {
            log.warn("Список сущностей для пакетной операции пуст.");
            return;
        }
        executeInTransaction(session -> {
            for (int i = 0; i < entities.size(); i++) {
                session.merge(entities.get(i)); // Используем merge для пакетных операций
                if ((i + 1) % batchSize == 0 || i == entities.size() - 1) {
                    session.flush();
                    session.clear();
                }
            }
            log.info("Пакетная операция успешно выполнена для {} сущностей", entities.size());
            return null;
        });
    }

    /**
     * Выполняет HQL-запрос на обновление или удаление (DML).
     *
     * @return Количество затронутых строк.
     */
    @Step("Выполнение HQL-запроса на обновление/удаление: {hqlQuery}")
    public int executeUpdateOrDelete() {
        requireNonNull(hqlQuery, "HQL запрос для обновления/удаления не должен быть null");
        log.info("Выполнение HQL (update/delete): [{}], параметры: {}", hqlQuery, parameters);
        return executeInTransaction(session -> {
            Query<?> query = session.createQuery(hqlQuery);
            applyQuerySettings(query);
            int affectedRows = query.executeUpdate();
            log.info("HQL (update/delete) выполнен, затронуто {} записей", affectedRows);
            return affectedRows;
        });
    }

    // Асинхронные операции и управление транзакциями

    /**
     * Выполняет операцию асинхронно в фоновом потоке.
     *
     * @param action Функция, принимающая сессию и возвращающая результат.
     * @param <R>    Тип результата.
     * @return {@link CompletableFuture} с результатом операции.
     */
    @Step("Асинхронное выполнение операции в транзакции")
    public <R> CompletableFuture<R> executeAsync(final Function<Session, R> action) {
        return CompletableFuture.supplyAsync(() -> executeInTransaction(action));
    }

    /**
     * Выполняет произвольный набор действий внутри одной управляемой транзакции.
     * Полезно для сложных операций, требующих нескольких вызовов к БД.
     *
     * @param action Действие для выполнения, принимающее активную сессию.
     */
    @Step("Выполнение кастомной операции в транзакции")
    public void executeInTransaction(final Consumer<Session> action) {
        executeInTransaction(session -> {
            action.accept(session);
            return null; // Возвращаем Void
        });
    }

    /**
     * Выполняет операцию в транзакции "только для чтения". Это может дать прирост производительности
     * для некоторых СУБД и предотвращает случайные изменения данных.
     *
     * @param action Функция, принимающая сессию и возвращающая результат.
     * @param <R>    Тип результата.
     * @return Результат выполнения функции.
     */
    @Step("Выполнение операции в транзакции только для чтения")
    public <R> R executeInReadOnlyTransaction(final Function<Session, R> action) {
        final boolean originalReadOnly = this.readOnly;
        this.readOnly = true;
        try {
            return executeInTransaction(action);
        } finally {
            this.readOnly = originalReadOnly;
        }
    }

    // Приватные вспомогательные методы

    private <R> R executeInTransaction(Function<Session, R> action) {
        int attempt = 0;
        while (true) {
            Transaction transaction = null;
            try (Session session = sessionFactory.openSession()) {
                session.doWork(connection -> connection.setTransactionIsolation(isolationLevel));
                if (flushMode != null) {
                    session.setFlushMode(flushMode);
                }
                if (readOnly) {
                    session.setDefaultReadOnly(true);
                }
                transaction = session.beginTransaction();
                R result = action.apply(session);
                transaction.commit();
                return result;
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    try {
                        transaction.rollback();
                    } catch (Exception rbEx) {
                        log.error("Критическая ошибка при откате транзакции", rbEx);
                    }
                }

                if (isRetryable(e) && ++attempt <= retries) {
                    log.warn("Операция не удалась (попытка {} из {}), ошибка: {}. Повтор через {}...",
                            attempt, retries, e.getMessage(), retryDelay);
                    try {
                        Thread.sleep(retryDelay.toMillis());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new DataAccessException("Поток прерван во время ожидания перед повторной попыткой", ie);
                    }
                } else {
                    log.error("Ошибка при выполнении операции в транзакции (попытка {} из {}): {}",
                            attempt, retries, e.getMessage(), e);
                    throw new DataAccessException("Ошибка при выполнении операции в транзакции", e);
                }
            }
        }
    }

    private List<T> executeHqlQuery() {
        requireNonNull(hqlQuery, "HQL запрос не должен быть null");
        String finalHql = hqlQuery;
        if (!joinFetchPaths.isEmpty()) {
            String fetches = joinFetchPaths.stream().map(path -> "LEFT JOIN FETCH " + path).collect(Collectors.joining(" "));
            finalHql = finalHql.replaceFirst("(?i)from", fetches + " FROM");
        }
        log.info("Выполнение HQL запроса: [{}], параметры: {}", finalHql, parameters);
        final String effectiveHql = finalHql;
        return executeInTransaction(session -> {
            Query<T> query = session.createQuery(effectiveHql, entityClass);
            applyQuerySettings(query);
            List<T> results = query.getResultList();
            log.info("HQL запрос выполнен, получено {} результатов", results.size());
            return results;
        });
    }

    private List<T> executeSqlQuery() {
        requireNonNull(sqlQuery, "SQL запрос не должен быть null");
        log.info("Выполнение нативного SQL запроса: [{}], параметры: {}", sqlQuery, parameters);
        return executeInTransaction(session -> {
            NativeQuery<T> query = session.createNativeQuery(sqlQuery, entityClass);
            applyQuerySettings(query);
            List<T> results = query.getResultList();
            log.info("Нативный SQL запрос выполнен, получено {} результатов", results.size());
            return results;
        });
    }

    private List<T> executeNamedQuery() {
        requireNonNull(namedQuery, "Имя именованного запроса не должно быть null");
        log.info("Выполнение именованного запроса: '{}', параметры: {}", namedQuery, parameters);
        return executeInTransaction(session -> {
            Query<T> query = session.createNamedQuery(namedQuery, entityClass);
            if (!joinFetchPaths.isEmpty()) {
                log.warn("Join fetch не может быть применен к именованным запросам динамически. Определите его в самом запросе.");
            }
            applyQuerySettings(query);
            List<T> results = query.getResultList();
            log.info("Именованный запрос выполнен, получено {} результатов", results.size());
            return results;
        });
    }

    private void applyQuerySettings(Query<?> query) {
        if (!parameters.isEmpty()) {
            parameters.forEach(query::setParameter);
        }
        if (maxResults > 0) {
            query.setMaxResults(maxResults);
        }
        if (firstResult > 0) {
            query.setFirstResult(firstResult);
        }
        if (fetchSize > 0) {
            query.setFetchSize(fetchSize);
        }
        query.setReadOnly(readOnly);
        query.setCacheable(cacheable);
        if (cacheRegion != null && !cacheRegion.isBlank()) {
            query.setCacheRegion(cacheRegion);
        }
        if (!hints.isEmpty()) {
            hints.forEach(query::setHint);
        }
    }

    private void applyOptionsToEntity(Session session, T entity) {
        if (entity != null && session.contains(entity)) {
            if (lockModeType != null) {
                LockMode lockMode = convertToHibernateLockMode(lockModeType);
                session.buildLockRequest(new LockOptions(lockMode)).lock(entity);
            }
            session.setReadOnly(entity, readOnly);
        }
    }

    private boolean isRetryable(Exception e) {
        return e instanceof LockAcquisitionException || e instanceof PessimisticLockException
                || (e.getCause() != null && e.getCause() instanceof LockAcquisitionException);
    }

    private LockMode convertToHibernateLockMode(LockModeType jpaLockMode) {
        return switch (jpaLockMode) {
            case READ -> LockMode.READ;
            case WRITE -> LockMode.WRITE;
            case OPTIMISTIC -> LockMode.OPTIMISTIC;
            case OPTIMISTIC_FORCE_INCREMENT -> LockMode.OPTIMISTIC_FORCE_INCREMENT;
            case PESSIMISTIC_READ -> LockMode.PESSIMISTIC_READ;
            case PESSIMISTIC_WRITE -> LockMode.PESSIMISTIC_WRITE;
            case PESSIMISTIC_FORCE_INCREMENT -> LockMode.PESSIMISTIC_FORCE_INCREMENT;
            default -> LockMode.NONE;
        };
    }
}
