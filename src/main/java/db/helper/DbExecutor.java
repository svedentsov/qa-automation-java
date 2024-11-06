package db.helper;

import db.exception.DataAccessException;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.*;
import org.hibernate.query.NativeQuery;

import javax.persistence.LockModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Класс для выполнения операций с базой данных с использованием Hibernate.
 * Предоставляет методы для выполнения CRUD операций, запросов и управления транзакциями.
 *
 * @param <T> Тип сущности, с которой работает DbExecutor
 */
@Slf4j
public class DbExecutor<T> {

    private static final int DEFAULT_BATCH_SIZE = 50;

    private final SessionFactory sessionFactory;
    private final Class<T> entityClass;
    private final Map<String, Object> parameters = new HashMap<>();
    private final Map<String, Object> hints = new HashMap<>();
    private final List<OrderByClause> orderByClauses = new ArrayList<>();

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
    private FlushMode flushMode;
    private int isolationLevel = Connection.TRANSACTION_READ_COMMITTED;

    /**
     * Класс для представления условий сортировки.
     */
    public static class OrderByClause {
        private final String propertyName;
        private final boolean ascending;

        /**
         * Конструктор класса OrderByClause.
         *
         * @param propertyName имя свойства для сортировки
         * @param ascending    флаг сортировки по возрастанию (true) или убыванию (false)
         */
        public OrderByClause(String propertyName, boolean ascending) {
            this.propertyName = propertyName;
            this.ascending = ascending;
        }
    }

    /**
     * Конструктор класса DbExecutor.
     *
     * @param sessionFactory фабрика сессий Hibernate
     * @param entityClass    класс сущности, с которой работает DbExecutor
     */
    public DbExecutor(SessionFactory sessionFactory, Class<T> entityClass) {
        this.sessionFactory = Objects.requireNonNull(sessionFactory, "SessionFactory не должен быть null");
        this.entityClass = Objects.requireNonNull(entityClass, "Класс сущности не должен быть null");
    }

    // Методы конфигурации для запроса

    /**
     * Устанавливает HQL запрос для выполнения.
     *
     * @param hqlQuery строка с HQL запросом
     * @return текущий экземпляр DbExecutor для цепочки вызовов
     */
    public DbExecutor<T> setHqlQuery(String hqlQuery) {
        this.hqlQuery = hqlQuery;
        return this;
    }

    /**
     * Устанавливает SQL запрос для выполнения.
     *
     * @param sqlQuery строка с SQL запросом
     * @return текущий экземпляр DbExecutor для цепочки вызовов
     */
    public DbExecutor<T> setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
        return this;
    }

    /**
     * Устанавливает именованный запрос для выполнения.
     *
     * @param namedQuery имя именованного запроса
     * @return текущий экземпляр DbExecutor для цепочки вызовов
     */
    public DbExecutor<T> setNamedQuery(String namedQuery) {
        this.namedQuery = namedQuery;
        return this;
    }

    /**
     * Устанавливает параметры для запроса.
     *
     * @param parameters карта с именами и значениями параметров
     * @return текущий экземпляр DbExecutor для цепочки вызовов
     */
    public DbExecutor<T> setParameters(Map<String, Object> parameters) {
        this.parameters.putAll(Objects.requireNonNull(parameters, "Параметры не должны быть null"));
        return this;
    }

    /**
     * Добавляет параметр для запроса.
     *
     * @param name  имя параметра
     * @param value значение параметра
     * @return текущий экземпляр DbExecutor для цепочки вызовов
     */
    public DbExecutor<T> addParameter(String name, Object value) {
        this.parameters.put(Objects.requireNonNull(name, "Имя параметра не должно быть null"), value);
        return this;
    }

    /**
     * Устанавливает размер пакета для batch-операций.
     *
     * @param batchSize размер пакета
     * @return текущий экземпляр DbExecutor для цепочки вызовов
     */
    public DbExecutor<T> setBatchSize(int batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    /**
     * Устанавливает размер выборки для запросов.
     *
     * @param fetchSize размер выборки
     * @return текущий экземпляр DbExecutor для цепочки вызовов
     */
    public DbExecutor<T> setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
        return this;
    }

    /**
     * Устанавливает максимальное количество результатов запроса.
     *
     * @param maxResults максимальное количество результатов
     * @return текущий экземпляр DbExecutor для цепочки вызовов
     */
    public DbExecutor<T> setMaxResults(int maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    /**
     * Устанавливает начальный результат для пагинации.
     *
     * @param firstResult номер первого результата
     * @return текущий экземпляр DbExecutor для цепочки вызовов
     */
    public DbExecutor<T> setFirstResult(int firstResult) {
        this.firstResult = firstResult;
        return this;
    }

    /**
     * Устанавливает режим блокировки для сущностей.
     *
     * @param lockModeType тип блокировки
     * @return текущий экземпляр DbExecutor для цепочки вызовов
     */
    public DbExecutor<T> setLockMode(LockModeType lockModeType) {
        this.lockModeType = lockModeType;
        return this;
    }

    /**
     * Устанавливает флаг только для чтения.
     *
     * @param readOnly если true, сущности будут помечены как только для чтения
     * @return текущий экземпляр DbExecutor для цепочки вызовов
     */
    public DbExecutor<T> setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }

    /**
     * Устанавливает, будет ли запрос кэшируемым.
     *
     * @param cacheable если true, запрос будет кэшироваться
     * @return текущий экземпляр DbExecutor для цепочки вызовов
     */
    public DbExecutor<T> setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
        return this;
    }

    /**
     * Устанавливает регион кэша для запроса.
     *
     * @param cacheRegion имя региона кэша
     * @return текущий экземпляр DbExecutor для цепочки вызовов
     */
    public DbExecutor<T> setCacheRegion(String cacheRegion) {
        this.cacheRegion = cacheRegion;
        return this;
    }

    /**
     * Устанавливает режим сброса для сессии.
     *
     * @param flushMode режим сброса
     * @return текущий экземпляр DbExecutor для цепочки вызовов
     */
    public DbExecutor<T> setFlushMode(FlushMode flushMode) {
        this.flushMode = flushMode;
        return this;
    }

    /**
     * Устанавливает подсказки для запроса.
     *
     * @param hints карта с именами и значениями подсказок
     * @return текущий экземпляр DbExecutor для цепочки вызовов
     */
    public DbExecutor<T> setHints(Map<String, Object> hints) {
        this.hints.putAll(Objects.requireNonNull(hints, "Подсказки не должны быть null"));
        return this;
    }

    /**
     * Добавляет подсказку для запроса.
     *
     * @param name  имя подсказки
     * @param value значение подсказки
     * @return текущий экземпляр DbExecutor для цепочки вызовов
     */
    public DbExecutor<T> addHint(String name, Object value) {
        this.hints.put(Objects.requireNonNull(name, "Имя подсказки не должно быть null"), value);
        return this;
    }

    /**
     * Добавляет условие сортировки для запроса.
     *
     * @param propertyName имя свойства для сортировки
     * @param ascending    если true, сортировка будет по возрастанию
     * @return текущий экземпляр DbExecutor для цепочки вызовов
     */
    public DbExecutor<T> addOrderBy(String propertyName, boolean ascending) {
        this.orderByClauses.add(new OrderByClause(propertyName, ascending));
        return this;
    }

    /**
     * Устанавливает уровень изоляции транзакции.
     *
     * @param isolationLevel уровень изоляции из класса java.sql.Connection
     * @return текущий экземпляр DbExecutor для цепочки вызовов
     */
    public DbExecutor<T> setTransactionIsolationLevel(int isolationLevel) {
        this.isolationLevel = isolationLevel;
        return this;
    }

    /**
     * Сохраняет сущность в базе данных.
     *
     * @param entity сущность для сохранения
     * @return сохраненная сущность
     */
    @Step("Сохранение сущности в базе данных")
    public T save(T entity) {
        Objects.requireNonNull(entity, "Сущность не должна быть null");
        return executeInTransaction(session -> {
            session.save(entity);
            log.info("Сущность успешно сохранена: {}", entity);
            return entity;
        });
    }

    /**
     * Обновляет сущность в базе данных.
     *
     * @param entity сущность для обновления
     * @return обновленная сущность
     */
    @Step("Обновление сущности в базе данных")
    public T update(T entity) {
        Objects.requireNonNull(entity, "Сущность не должна быть null");
        return executeInTransaction(session -> {
            session.update(entity);
            log.info("Сущность успешно обновлена: {}", entity);
            return entity;
        });
    }

    /**
     * Сохраняет или обновляет сущность в базе данных.
     *
     * @param entity сущность для сохранения или обновления
     * @return сохраненная или обновленная сущность
     */
    @Step("Сохранение или обновление сущности в базе данных")
    public T saveOrUpdate(T entity) {
        Objects.requireNonNull(entity, "Сущность не должна быть null");
        return executeInTransaction(session -> {
            session.saveOrUpdate(entity);
            log.info("Сущность успешно сохранена или обновлена: {}", entity);
            return entity;
        });
    }

    /**
     * Сливает состояние сущности с базой данных.
     *
     * @param entity сущность для слияния
     * @return слитая сущность
     */
    @Step("Слияние сущности с базой данных")
    public T merge(T entity) {
        Objects.requireNonNull(entity, "Сущность не должна быть null");
        return executeInTransaction(session -> {
            T mergedEntity = (T) session.merge(entity);
            log.info("Сущность успешно слита: {}", mergedEntity);
            return mergedEntity;
        });
    }

    /**
     * Получает сущность из базы данных по идентификатору.
     *
     * @param id идентификатор сущности
     * @return Optional с найденной сущностью или пустой Optional
     */
    @Step("Получение сущности из базы данных по идентификатору")
    public Optional<T> getById(Object id) {
        Objects.requireNonNull(id, "Идентификатор сущности не должен быть null");
        return executeInTransaction(session -> {
            T entity = session.get(entityClass, (Serializable) id);
            applyOptionsToEntity(session, entity);
            log.info("Сущность успешно получена: {}", entity);
            return Optional.ofNullable(entity);
        });
    }

    /**
     * Удаляет сущность из базы данных.
     *
     * @param entity сущность для удаления
     */
    @Step("Удаление сущности из базы данных")
    public void delete(T entity) {
        Objects.requireNonNull(entity, "Сущность не должна быть null");
        executeInTransaction(session -> {
            session.delete(entity);
            log.info("Сущность успешно удалена: {}", entity);
            return null;
        });
    }

    /**
     * Выполняет HQL запрос и возвращает список результатов.
     *
     * @return список результатов
     */
    @Step("Выполнение HQL запроса")
    public List<T> executeHqlQuery() {
        Objects.requireNonNull(hqlQuery, "HQL запрос не должен быть null");
        return executeInTransaction(session -> {
            Query<T> query = session.createQuery(hqlQuery, entityClass);
            applyQuerySettings(query);
            List<T> results = query.getResultList();
            log.info("HQL запрос выполнен успешно, получено {} результатов", results.size());
            return results;
        });
    }

    /**
     * Выполняет SQL запрос и возвращает список результатов.
     *
     * @return список результатов
     */
    @Step("Выполнение SQL запроса")
    public List<T> executeSqlQuery() {
        Objects.requireNonNull(sqlQuery, "SQL запрос не должен быть null");
        return executeInTransaction(session -> {
            NativeQuery<T> query = session.createNativeQuery(sqlQuery, entityClass);
            applyQuerySettings(query);
            List<T> results = query.getResultList();
            log.info("SQL запрос выполнен успешно, получено {} результатов", results.size());
            return results;
        });
    }

    /**
     * Выполняет именованный запрос и возвращает список результатов.
     *
     * @return список результатов
     */
    @Step("Выполнение именованного запроса")
    public List<T> executeNamedQuery() {
        Objects.requireNonNull(namedQuery, "Именованный запрос не должен быть null");
        return executeInTransaction(session -> {
            Query<T> query = session.createNamedQuery(namedQuery, entityClass);
            applyQuerySettings(query);
            List<T> results = query.getResultList();
            log.info("Именованный запрос выполнен успешно, получено {} результатов", results.size());
            return results;
        });
    }

    /**
     * Выполняет критериальный запрос и возвращает список результатов.
     *
     * @return список результатов
     */
    @Step("Выполнение критериального запроса")
    public List<T> executeCriteriaQuery() {
        return executeInTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = builder.createQuery(entityClass);
            Root<T> root = criteriaQuery.from(entityClass);

            if (!orderByClauses.isEmpty()) {
                List<Order> orders = new ArrayList<>();
                for (OrderByClause orderBy : orderByClauses) {
                    if (orderBy.ascending) {
                        orders.add(builder.asc(root.get(orderBy.propertyName)));
                    } else {
                        orders.add(builder.desc(root.get(orderBy.propertyName)));
                    }
                }
                criteriaQuery.orderBy(orders);
            }

            Query<T> query = session.createQuery(criteriaQuery);
            applyQuerySettings(query);
            List<T> results = query.getResultList();
            log.info("Критериальный запрос выполнен успешно, получено {} результатов", results.size());
            return results;
        });
    }

    /**
     * Выполняет пакетную операцию сохранения или обновления списка сущностей.
     *
     * @param entities список сущностей
     */
    @Step("Выполнение пакетной операции")
    public void executeBatchOperation(List<T> entities) {
        Objects.requireNonNull(entities, "Список сущностей не должен быть null");
        executeInTransaction(session -> {
            int i = 0;
            for (T entity : entities) {
                session.saveOrUpdate(entity);
                if (i % batchSize == 0) {
                    session.flush();
                    session.clear();
                }
                i++;
            }
            log.info("Пакетная операция выполнена успешно для {} сущностей", entities.size());
            return null;
        });
    }

    /**
     * Выполняет обновление или удаление с помощью HQL запроса.
     *
     * @return количество затронутых записей
     */
    @Step("Выполнение обновления или удаления с помощью HQL запроса")
    public int executeUpdateOrDelete() {
        Objects.requireNonNull(hqlQuery, "HQL запрос не должен быть null");
        return executeInTransaction(session -> {
            Query<?> query = session.createQuery(hqlQuery);
            applyQuerySettings(query);
            int affectedRows = query.executeUpdate();
            log.info("Запрос на обновление или удаление выполнен успешно, затронуто {} записей", affectedRows);
            return affectedRows;
        });
    }

    /**
     * Выполняет функцию внутри транзакции, обеспечивая корректное управление сессией и транзакцией.
     *
     * @param action функция, принимающая Session и возвращающая результат
     * @param <R>    тип результата функции
     * @return результат выполнения функции
     */
    private <R> R executeInTransaction(Function<Session, R> action) {
        try (Session session = sessionFactory.openSession()) {
            setTransactionIsolationLevel(session);
            Transaction transaction = session.beginTransaction();
            try {
                R result = action.apply(session);
                transaction.commit();
                return result;
            } catch (Exception e) {
                transaction.rollback();
                log.error("Ошибка при выполнении операции: {}", e.getMessage(), e);
                throw new DataAccessException("Ошибка при выполнении операции", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Устанавливает уровень изоляции транзакции для текущей сессии.
     *
     * @param session текущая сессия
     * @throws SQLException если происходит ошибка при установке уровня изоляции
     */
    private void setTransactionIsolationLevel(Session session) throws SQLException {
        session.doWork(connection -> connection.setTransactionIsolation(isolationLevel));
    }

    /**
     * Применяет настройки к запросу.
     *
     * @param query запрос для настройки
     */
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
        if (cacheRegion != null) {
            query.setCacheRegion(cacheRegion);
        }
        if (flushMode != null) {
            query.setHibernateFlushMode(flushMode);
        }
        if (!hints.isEmpty()) {
            hints.forEach(query::setHint);
        }
    }

    /**
     * Применяет опции к сущности, такой как режим блокировки и флаг только для чтения.
     *
     * @param session текущая сессия
     * @param entity  сущность для настройки
     */
    private void applyOptionsToEntity(Session session, T entity) {
        if (entity != null && session.contains(entity)) {
            if (lockModeType != null) {
                LockMode lockMode = convertToHibernateLockMode(lockModeType);
                session.buildLockRequest(new LockOptions(lockMode)).lock(entity);
            }
            session.setReadOnly(entity, readOnly);
        }
    }

    /**
     * Преобразует LockModeType из JPA в LockMode из Hibernate.
     *
     * @param lockModeType тип блокировки JPA
     * @return соответствующий тип блокировки Hibernate
     */
    private LockMode convertToHibernateLockMode(LockModeType lockModeType) {
        return switch (lockModeType) {
            case NONE -> LockMode.NONE;
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

    // Методы получения результатов

    /**
     * Выполняет асинхронную операцию и возвращает CompletableFuture.
     *
     * @param action функция, принимающая Session и возвращающая результат
     * @param <R>    тип результата
     * @return CompletableFuture с результатом операции
     */
    public <R> CompletableFuture<R> executeAsync(Function<Session, R> action) {
        return CompletableFuture.supplyAsync(() -> executeInTransaction(action));
    }
}
