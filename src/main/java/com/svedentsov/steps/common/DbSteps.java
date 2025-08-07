package com.svedentsov.steps.common;

import com.svedentsov.db.entity.MyEntity;
import com.svedentsov.db.enums.DatabaseType;
import com.svedentsov.db.factory.SessionFactoryProvider;
import com.svedentsov.db.helper.DbExecutor;
import io.qameta.allure.Step;
import jakarta.persistence.LockModeType;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;

import java.sql.Connection;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.svedentsov.matcher.PropertyMatcher.value;
import static com.svedentsov.matcher.assertions.ListAssertions.listCountEqual;
import static com.svedentsov.matcher.assertions.ListAssertions.listIsNotEmpty;
import static com.svedentsov.matcher.assertions.PropertyAssertions.propertyEqualsTo;
import static com.svedentsov.matcher.assertions.StringAssertions.contains;


/**
 * Класс-демонстрация для всех возможностей обновленного класса {@link DbExecutor}.
 * Каждый метод представляет собой пример использования определенной функциональности:
 * от базовых CRUD-операций до сложных транзакционных сценариев и встроенной валидации.
 */
@Slf4j
public class DbSteps {

    private final SessionFactory sessionFactory;

    /**
     * Конструктор класса DbSteps.
     * Инициализирует {@link SessionFactory} для работы с базой данных через провайдер.
     */
    public DbSteps() {
        sessionFactory = SessionFactoryProvider.getSessionFactory(DatabaseType.DB1);
    }

    @Step("Демонстрация CRUD-операций и удаления по ID")
    public void demonstrateCrudOperations() {
        DbExecutor<MyEntity> dbExecutor = DbExecutor.create(sessionFactory, MyEntity.class);
        // Создание новой сущности
        MyEntity newEntity = new MyEntity();
        newEntity.id(UUID.randomUUID().toString());
        newEntity.name("Test Entity");
        newEntity.status("ACTIVE");
        // 1. Сохранение
        MyEntity savedEntity = dbExecutor.save(newEntity);
        log.info("Сохраненная сущность: {}", savedEntity);
        // 2. Получение по ID
        Optional<MyEntity> optionalEntity = dbExecutor.clear().getById(savedEntity.id());
        optionalEntity.ifPresent(entity -> {
            log.info("Полученная сущность: {}", entity);
            // 3. Обновление
            entity.name("Updated Entity");
            MyEntity updatedEntity = dbExecutor.clear().update(entity);
            log.info("Обновленная сущность: {}", updatedEntity);
            // 4. Удаление по объекту
            dbExecutor.clear().delete(updatedEntity);
            log.info("Сущность удалена.");
        });
        // 5. Демонстрация удаления по ID без предварительной загрузки
        MyEntity entityToDelete = new MyEntity().id(UUID.randomUUID().toString()).name("To be deleted by ID");
        dbExecutor.clear().save(entityToDelete);
        log.info("Сохранена сущность для удаления по ID: {}", entityToDelete);
        dbExecutor.clear().deleteById(entityToDelete.id());
        log.info("Сущность с ID {} удалена.", entityToDelete.id());
    }

    @Step("Демонстрация выполнения HQL-запроса")
    public void demonstrateHqlQuery() {
        DbExecutor<MyEntity> dbExecutor = DbExecutor.create(sessionFactory, MyEntity.class);

        List<MyEntity> entities = dbExecutor
                .setHqlQuery("FROM MyEntity WHERE status = :status")
                .addParameter("status", "ACTIVE")
                .setFirstResult(0)
                .setMaxResults(10)
                .getResultList();

        entities.forEach(entity -> log.info("Получена сущность (HQL): {}", entity));
    }

    @Step("Демонстрация выполнения нативного SQL-запроса")
    public void demonstrateSqlQuery() {
        DbExecutor<MyEntity> dbExecutor = DbExecutor.create(sessionFactory, MyEntity.class);

        List<MyEntity> entities = dbExecutor
                .setSqlQuery("SELECT * FROM my_entity WHERE status = :status")
                .addParameter("status", "ACTIVE")
                .getResultList();

        entities.forEach(entity -> log.info("Получена сущность (SQL): {}", entity));
    }

    @Step("Демонстрация выполнения именованного запроса")
    public void demonstrateNamedQuery() {
        DbExecutor<MyEntity> dbExecutor = DbExecutor.create(sessionFactory, MyEntity.class);

        List<MyEntity> entities = dbExecutor
                .setNamedQuery("MyEntity.findByStatus")
                .addParameter("status", "ACTIVE")
                .getResultList();

        entities.forEach(entity -> log.info("Получена сущность (Named Query): {}", entity));
    }

    @Step("Демонстрация получения одиночного, опционального и скалярного результата")
    public void demonstrateSingleAndScalarResultQuery() {
        DbExecutor<MyEntity> dbExecutor = DbExecutor.create(sessionFactory, MyEntity.class);

        // Подготовка данных
        String uniqueName = "SingleResult_" + UUID.randomUUID();
        MyEntity singleEntity = new MyEntity().id(UUID.randomUUID().toString()).name(uniqueName);
        dbExecutor.save(singleEntity);

        // Получение Optional результата
        Optional<MyEntity> foundEntity = dbExecutor.clear()
                .setHqlQuery("FROM MyEntity WHERE name = :name")
                .addParameter("name", uniqueName)
                .getOptionalResult();
        foundEntity.ifPresent(e -> log.info("Найден Optional результат: {}", e));

        // Получение скалярного результата (COUNT)
        Optional<Long> count = dbExecutor.clear()
                .setHqlQuery("SELECT COUNT(*) FROM MyEntity WHERE name = :name")
                .addParameter("name", uniqueName)
                .getScalarResult(Long.class);
        count.ifPresent(c -> log.info("Скалярный результат (COUNT): {}", c));

        // Демонстрация exists()
        boolean exists = dbExecutor.clear()
                .setHqlQuery("FROM MyEntity WHERE name = :name")
                .addParameter("name", uniqueName)
                .exists();
        log.info("Проверка существования (exists()): {}", exists);

        dbExecutor.clear().delete(singleEntity);
    }

    @Step("Демонстрация встроенной валидации с shouldHave и shouldHaveList")
    public void demonstrateChainedValidation() {
        DbExecutor<MyEntity> dbExecutor = DbExecutor.create(sessionFactory, MyEntity.class);
        String validationStatus = "VALIDATION_TEST";
        List<MyEntity> testEntities = new ArrayList<>();
        // Подготовка данных
        for (int i = 0; i < 3; i++) {
            MyEntity entity = new MyEntity()
                    .id(UUID.randomUUID().toString())
                    .name("Validation Entity " + i)
                    .status(validationStatus);
            testEntities.add(entity);
        }
        dbExecutor.executeBatchOperation(testEntities);
        log.info("Подготовлены 3 сущности для теста валидации.");

        // Выполняем запрос и сразу же применяем проверки
        dbExecutor.clear()
                .setHqlQuery("FROM MyEntity WHERE status = :status")
                .addParameter("status", validationStatus)
                .getResultList(); // Запрос выполнен, результат сохранен в dbExecutor

        log.info("Выполняем цепочку проверок на полученном результате...");
        dbExecutor
                .shouldHaveList(
                        listIsNotEmpty(),
                        listCountEqual(3)
                )
                .shouldHave(
                        value(MyEntity::status, propertyEqualsTo(validationStatus)),
                        value(MyEntity::name, contains("Validation Entity"))
                );

        log.info("Все проверки для цепочки валидации успешно пройдены.");

        // Очистка
        dbExecutor.clear().executeInTransaction(session ->
                testEntities.forEach(session::remove)
        );
        log.info("Тестовые данные для валидации удалены.");
    }

    @Step("Демонстрация выполнения пакетной операции")
    public void demonstrateBatchOperation() {
        DbExecutor<MyEntity> dbExecutor = DbExecutor.create(sessionFactory, MyEntity.class);

        List<MyEntity> entities = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            MyEntity entity = new MyEntity();
            entity.id(UUID.randomUUID().toString());
            entity.name("Batch Entity " + i);
            entity.status("BATCH_CREATED");
            entities.add(entity);
        }

        dbExecutor
                .setBatchSize(20)
                .executeBatchOperation(entities);

        log.info("Пакетная операция для 100 сущностей выполнена.");
    }

    @Step("Демонстрация выполнения HQL-запроса на обновление")
    public void demonstrateUpdateOrDelete() {
        DbExecutor<MyEntity> dbExecutor = DbExecutor.create(sessionFactory, MyEntity.class);

        int affectedRows = dbExecutor
                .setHqlQuery("UPDATE MyEntity SET status = :newStatus WHERE status = :oldStatus")
                .addParameter("newStatus", "INACTIVE_BY_HQL")
                .addParameter("oldStatus", "ACTIVE")
                .executeUpdateOrDelete();

        log.info("Обновлено записей через HQL: {}", affectedRows);
    }

    @Step("Демонстрация установки различных параметров запроса")
    public void demonstrateQuerySettings() {
        DbExecutor<MyEntity> dbExecutor = DbExecutor.create(sessionFactory, MyEntity.class);

        List<MyEntity> entities = dbExecutor
                .setHqlQuery("FROM MyEntity")
                .setFetchSize(50)
                .setMaxResults(100)
                .setFirstResult(0)
                .setReadOnly(true)
                .setCacheable(true)
                .setCacheRegion("myEntityCacheRegion")
                .addHint("org.hibernate.comment", "Демонстрационный запрос")
                .getResultList();

        log.info("Запрос с расширенными настройками вернул {} сущностей.", entities.size());
    }

    @Step("Демонстрация использования JOIN FETCH")
    public void demonstrateJoinFetch() {
        DbExecutor<MyEntity> dbExecutor = DbExecutor.create(sessionFactory, MyEntity.class);
        // Предполагается, что у MyEntity есть ленивая коллекция 'roles'
        List<MyEntity> entitiesWithRoles = dbExecutor
                .setHqlQuery("FROM MyEntity e")
                .addJoinFetch("e.roles") // Жадная загрузка коллекции roles
                .getResultList();

        log.info("Получено {} сущностей с предзагруженными ролями, чтобы избежать N+1.", entitiesWithRoles.size());
    }

    @Step("Демонстрация асинхронного выполнения операции")
    public void demonstrateAsyncOperation() {
        DbExecutor<MyEntity> dbExecutor = DbExecutor.create(sessionFactory, MyEntity.class);

        CompletableFuture<List<MyEntity>> futureEntities = dbExecutor.executeAsync(session ->
                session.createQuery("FROM MyEntity", MyEntity.class).getResultList()
        );

        futureEntities.thenAccept(entities -> {
            log.info("Асинхронно получено {} сущностей.", entities.size());
            entities.forEach(entity -> log.info("Асинхронно полученная сущность: {}", entity));
        }).exceptionally(ex -> {
            log.error("Ошибка при выполнении асинхронной операции: {}", ex.getMessage(), ex);
            return null;
        }).join(); // .join() для ожидания завершения в демонстрации
    }

    @Step("Демонстрация установки уровня изоляции транзакции")
    public void demonstrateTransactionIsolationLevel() {
        DbExecutor<MyEntity> dbExecutor = DbExecutor.create(sessionFactory, MyEntity.class);

        List<MyEntity> entities = dbExecutor
                .setTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE)
                .setHqlQuery("FROM MyEntity")
                .getResultList();

        log.info("Запрос с уровнем изоляции SERIALIZABLE вернул {} сущностей.", entities.size());
    }

    @Step("Демонстрация использования режима блокировки")
    public void demonstrateLockMode() {
        DbExecutor<MyEntity> dbExecutor = DbExecutor.create(sessionFactory, MyEntity.class);
        MyEntity entity = new MyEntity().id(UUID.randomUUID().toString()).name("Lockable Entity");
        dbExecutor.save(entity);

        dbExecutor.clear()
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getById(entity.id())
                .ifPresent(lockedEntity -> log.info("Получена сущность с пессимистичной блокировкой: {}", lockedEntity));

        dbExecutor.clear().delete(entity);
    }

    @Step("Демонстрация механизма повторных попыток (retries)")
    public void demonstrateTransactionalRetries() {
        DbExecutor<MyEntity> dbExecutor = DbExecutor.create(sessionFactory, MyEntity.class);
        log.info("Следующая операция будет выполнена с 3 попытками в случае ошибки блокировки.");

        // Этот код просто демонстрирует API. Для реальной проверки нужен сценарий,
        // который гарантированно вызовет LockAcquisitionException.
        dbExecutor
                .withRetries(3, Duration.ofMillis(150))
                .executeInTransaction(session -> {
                    // ... здесь могла бы быть операция, вызывающая конфликт ...
                    log.info("Попытка выполнить операцию с возможными ретраями...");
                    session.createQuery("SELECT COUNT(*) FROM MyEntity", Long.class).getSingleResult();
                });
    }

    @Step("Демонстрация выполнения сложной операции в одной транзакции")
    public void demonstrateComplexTransaction() {
        DbExecutor<MyEntity> dbExecutor = DbExecutor.create(sessionFactory, MyEntity.class);

        dbExecutor.executeInTransaction(session -> {
            log.info("Начало сложной транзакции...");
            // Пытаемся найти существующую сущность (может не найтись, это нормально для демо)
            MyEntity entity1 = session.createQuery("FROM MyEntity", MyEntity.class).setMaxResults(1).uniqueResult();
            if (entity1 != null) {
                entity1.status("PROCESSED_IN_COMPLEX_TX");
                session.merge(entity1);
                log.info("Сущность {} обновлена.", entity1.id());
            }

            MyEntity newEntity = new MyEntity().id(UUID.randomUUID().toString()).name("Created in complex TX");
            session.persist(newEntity);
            log.info("Сущность {} создана.", newEntity.id());
            log.info("Завершение сложной транзакции.");
        });
    }

    /**
     * Закрывает {@link SessionFactory} для корректного освобождения ресурсов.
     * <p><b>Важно:</b> Этот метод является демонстрационным. В реальном проекте
     * управление жизненным циклом {@code SessionFactory} должно быть централизовано
     * и вызываться при завершении всего тестового набора (например, в методе,
     * аннотированном {@code @AfterAll}).</p>
     */
    @Step("Завершение работы и закрытие SessionFactory (демонстрация)")
    public void shutdown() {
        // SessionFactoryProvider.shutdown();
        log.info("Вызов shutdown() для демонстрации. В реальных тестах управляется централизованно.");
    }
}
