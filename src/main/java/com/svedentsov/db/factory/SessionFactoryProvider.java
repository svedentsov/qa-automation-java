package com.svedentsov.db.factory;

import com.svedentsov.db.enums.DatabaseType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SessionFactoryProvider {

    // Хранилище SessionFactory по DatabaseType
    private static final Map<DatabaseType, SessionFactory> sessionFactories = new ConcurrentHashMap<>();

    /**
     * Возвращает экземпляр SessionFactory для указанного типа базы данных.
     * Если SessionFactory еще не создан, инициализирует его.
     *
     * @param dbType тип базы данных из перечисления DatabaseType
     * @return SessionFactory
     */
    public static SessionFactory getSessionFactory(DatabaseType dbType) {
        return sessionFactories.computeIfAbsent(dbType, SessionFactoryProvider::createSessionFactory);
    }

    /**
     * Создает SessionFactory для указанного типа базы данных.
     *
     * @param dbType тип базы данных из перечисления DatabaseType
     * @return созданный экземпляр SessionFactory
     */
    private static SessionFactory createSessionFactory(DatabaseType dbType) {
        try {
            // Создание конфигурации Hibernate
            Configuration configuration = new Configuration().configure(dbType.getConfigFilePath());

            // Добавление аннотированных классов сущностей
            for (Class<?> annotatedClass : dbType.getEntityClasses()) {
                configuration.addAnnotatedClass(annotatedClass);
            }

            // Создание ServiceRegistry
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            // Построение SessionFactory
            SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            log.info("SessionFactory для '{}' успешно создан.", dbType.getIdentifier());
            return sessionFactory;
        } catch (Exception e) {
            log.error("Ошибка при создании SessionFactory для '{}': {}", dbType.getIdentifier(), e.getMessage(), e);
            throw new IllegalStateException("Ошибка инициализации SessionFactory для " + dbType.getIdentifier(), e);
        }
    }

    /**
     * Возвращает экземпляр SessionFactory для указанного типа базы данных, если он существует.
     *
     * @param dbType тип базы данных из перечисления DatabaseType
     * @return SessionFactory или null, если не найден
     */
    public static SessionFactory getSessionFactoryIfExists(DatabaseType dbType) {
        return sessionFactories.get(dbType);
    }

    /**
     * Закрывает все SessionFactory при завершении работы приложения.
     */
    public static synchronized void shutdown() {
        sessionFactories.forEach((dbType, sessionFactory) -> {
            try {
                if (sessionFactory != null && !sessionFactory.isClosed()) {
                    sessionFactory.close();
                    log.info("SessionFactory для '{}' закрыт.", dbType.getIdentifier());
                }
            } catch (Exception e) {
                log.error("Ошибка при закрытии SessionFactory для '{}': {}", dbType.getIdentifier(), e.getMessage(), e);
            }
        });
        sessionFactories.clear();
    }
}
