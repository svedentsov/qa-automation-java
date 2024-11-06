package db.steps;

import db.entity.MyEntity;
import db.enums.DatabaseType;
import db.helper.DbExecutor;
import db.factory.SessionFactoryProvider;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.LockModeType;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Класс DbSteps демонстрирует использование всех методов класса DbExecutor.
 */
@Slf4j
public class DbSteps {

    private final SessionFactory sessionFactory;

    /**
     * Конструктор класса DbSteps.
     * Инициализирует SessionFactory для работы с базой данных.
     */
    public DbSteps() {
        // Настройка Hibernate и создание SessionFactory
        Configuration configuration = new Configuration().configure();

        // Добавление аннотированных классов сущностей
        configuration.addAnnotatedClass(MyEntity.class);
//        sessionFactory = configuration.buildSessionFactory();
        sessionFactory = SessionFactoryProvider.getSessionFactory(DatabaseType.DB1);
    }

    @Step("Демонстрация CRUD операций")
    public void demonstrateCrudOperations() {
        DbExecutor<MyEntity> dbExecutor = new DbExecutor<>(sessionFactory, MyEntity.class);

        // Создание новой сущности
        MyEntity newEntity = new MyEntity();
        newEntity.name("Test Entity");
        newEntity.status("ACTIVE");

        // Сохранение сущности
        MyEntity savedEntity = dbExecutor.save(newEntity);
        log.info("Сохраненная сущность: {}", savedEntity);

        // Получение сущности по ID
        Optional<MyEntity> optionalEntity = dbExecutor.getById(savedEntity.id());
        optionalEntity.ifPresent(entity -> {
            log.info("Полученная сущность: {}", entity);

            // Обновление сущности
            entity.name("Updated Entity");
            MyEntity updatedEntity = dbExecutor.update(entity);
            log.info("Обновленная сущность: {}", updatedEntity);

            // Удаление сущности
            dbExecutor.delete(updatedEntity);
            log.info("Сущность удалена");
        });
    }

    @Step("Демонстрация выполнения HQL запроса")
    public void demonstrateHqlQuery() {
        DbExecutor<MyEntity> dbExecutor = new DbExecutor<>(sessionFactory, MyEntity.class);

        List<MyEntity> entities = dbExecutor
                .setHqlQuery("FROM MyEntity WHERE status = :status")
                .addParameter("status", "ACTIVE")
                .setFirstResult(0)
                .setMaxResults(10)
                .executeHqlQuery();

        entities.forEach(entity -> log.info("Получена сущность: {}", entity));
    }

    @Step("Демонстрация выполнения SQL запроса")
    public void demonstrateSqlQuery() {
        DbExecutor<MyEntity> dbExecutor = new DbExecutor<>(sessionFactory, MyEntity.class);

        List<MyEntity> entities = dbExecutor
                .setSqlQuery("SELECT * FROM my_entity WHERE status = :status")
                .addParameter("status", "ACTIVE")
                .executeSqlQuery();

        entities.forEach(entity -> log.info("Получена сущность: {}", entity));
    }

    @Step("Демонстрация выполнения именованного запроса")
    public void demonstrateNamedQuery() {
        DbExecutor<MyEntity> dbExecutor = new DbExecutor<>(sessionFactory, MyEntity.class);

        List<MyEntity> entities = dbExecutor
                .setNamedQuery("MyEntity.findByStatus")
                .addParameter("status", "ACTIVE")
                .executeNamedQuery();

        entities.forEach(entity -> log.info("Получена сущность: {}", entity));
    }

    @Step("Демонстрация выполнения критериального запроса")
    public void demonstrateCriteriaQuery() {
        DbExecutor<MyEntity> dbExecutor = new DbExecutor<>(sessionFactory, MyEntity.class);

        List<MyEntity> entities = dbExecutor
                .addOrderBy("name", true)
                .executeCriteriaQuery();

        entities.forEach(entity -> log.info("Получена сущность: {}", entity));
    }

    @Step("Демонстрация выполнения пакетной операции")
    public void demonstrateBatchOperation() {
        DbExecutor<MyEntity> dbExecutor = new DbExecutor<>(sessionFactory, MyEntity.class);

        List<MyEntity> entities = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            MyEntity entity = new MyEntity();
            entity.name("Entity " + i);
            entity.status("ACTIVE");
            entities.add(entity);
        }

        dbExecutor
                .setBatchSize(20)
                .executeBatchOperation(entities);

        log.info("Пакетная операция выполнена");
    }

    @Step("Демонстрация выполнения обновления или удаления с помощью HQL запроса")
    public void demonstrateUpdateOrDelete() {
        DbExecutor<MyEntity> dbExecutor = new DbExecutor<>(sessionFactory, MyEntity.class);

        int affectedRows = dbExecutor
                .setHqlQuery("UPDATE MyEntity SET status = :newStatus WHERE status = :oldStatus")
                .addParameter("newStatus", "INACTIVE")
                .addParameter("oldStatus", "ACTIVE")
                .executeUpdateOrDelete();

        log.info("Обновлено записей: {}", affectedRows);
    }

    @Step("Демонстрация установки параметров запроса и опций")
    public void demonstrateQuerySettings() {
        DbExecutor<MyEntity> dbExecutor = new DbExecutor<>(sessionFactory, MyEntity.class);

        List<MyEntity> entities = dbExecutor
                .setHqlQuery("FROM MyEntity")
                .setFetchSize(50)
                .setMaxResults(100)
                .setFirstResult(0)
                .setReadOnly(true)
                .setCacheable(true)
                .setCacheRegion("myCacheRegion")
                .executeHqlQuery();

        entities.forEach(entity -> log.info("Получена сущность: {}", entity));
    }

    @Step("Демонстрация асинхронного выполнения операции")
    public void demonstrateAsyncOperation() {
        DbExecutor<MyEntity> dbExecutor = new DbExecutor<>(sessionFactory, MyEntity.class);

        CompletableFuture<List<MyEntity>> futureEntities = dbExecutor.executeAsync(session -> {
            List<MyEntity> entities = session.createQuery("FROM MyEntity", MyEntity.class).getResultList();
            return entities;
        });

        futureEntities.thenAccept(entities -> {
            entities.forEach(entity -> log.info("Получена сущность: {}", entity));
        }).exceptionally(ex -> {
            log.error("Ошибка при выполнении асинхронной операции: {}", ex.getMessage(), ex);
            return null;
        });
    }

    @Step("Демонстрация установки уровня изоляции транзакции")
    public void demonstrateTransactionIsolationLevel() {
        DbExecutor<MyEntity> dbExecutor = new DbExecutor<>(sessionFactory, MyEntity.class);

        List<MyEntity> entities = dbExecutor
                .setTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE)
                .setHqlQuery("FROM MyEntity")
                .executeHqlQuery();

        entities.forEach(entity -> log.info("Получена сущность: {}", entity));
    }

    @Step("Демонстрация использования режима блокировки")
    public void demonstrateLockMode() {
        DbExecutor<MyEntity> dbExecutor = new DbExecutor<>(sessionFactory, MyEntity.class);

        dbExecutor
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getById(1L)
                .ifPresent(entity -> {
                    log.info("Получена заблокированная сущность: {}", entity);
                    // Работа с сущностью
                });
    }

    @Step("Завершение работы и закрытие SessionFactory")
    public void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    /**
     * Основной метод для запуска демонстрации.
     */
    public static void main(String[] args) {
        DbSteps dbSteps = new DbSteps();

        try {
            dbSteps.demonstrateCrudOperations();
            dbSteps.demonstrateHqlQuery();
            dbSteps.demonstrateSqlQuery();
            dbSteps.demonstrateNamedQuery();
            dbSteps.demonstrateCriteriaQuery();
            dbSteps.demonstrateBatchOperation();
            dbSteps.demonstrateUpdateOrDelete();
            dbSteps.demonstrateQuerySettings();
            dbSteps.demonstrateAsyncOperation();
            dbSteps.demonstrateTransactionIsolationLevel();
            dbSteps.demonstrateLockMode();
        } finally {
            dbSteps.shutdown();
        }
    }
}
