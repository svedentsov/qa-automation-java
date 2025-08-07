package com.svedentsov.db.factory;

import com.svedentsov.db.enums.DatabaseType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Потокобезопасный провайдер для создания и кэширования экземпляров {@link SessionFactory}.
 * <p>
 * Этот класс реализует паттерн "Реестр" для управления фабриками сессий Hibernate.
 * Он гарантирует, что для каждого типа базы данных ({@link DatabaseType}) создается
 * только один экземпляр {@code SessionFactory}, который затем переиспользуется.
 * Инициализация фабрики является ленивой и потокобезопасной.
 * </p>
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SessionFactoryProvider {

    private static final Map<DatabaseType, SessionFactory> sessionFactories = new ConcurrentHashMap<>();

    /**
     * Возвращает экземпляр {@link SessionFactory} для указанного типа базы данных.
     * <p>
     * Если {@code SessionFactory} для данного типа еще не был создан, он будет инициализирован
     * при первом вызове этого метода. Последующие вызовы для того же типа БД будут
     * возвращать уже существующий (закэшированный) экземпляр.
     * </p>
     *
     * @param dbType тип базы данных из перечисления {@link DatabaseType}.
     * @return Готовый к использованию экземпляр {@link SessionFactory}.
     * @throws IllegalStateException если произошла ошибка при создании SessionFactory.
     */
    public static SessionFactory getSessionFactory(DatabaseType dbType) {
        return sessionFactories.computeIfAbsent(dbType, SessionFactoryProvider::createSessionFactory);
    }

    /**
     * Создает новый экземпляр {@link SessionFactory} на основе конфигурации из {@link DatabaseType}.
     *
     * @param dbType тип базы данных.
     * @return созданный {@link SessionFactory}.
     */
    private static SessionFactory createSessionFactory(DatabaseType dbType) {
        log.info("Инициализация SessionFactory для '{}' из файла конфигурации '{}'...",
                dbType.getIdentifier(), dbType.getConfigFilePath());
        try {
            Configuration configuration = new Configuration().configure(dbType.getConfigFilePath());

            for (Class<?> annotatedClass : dbType.getEntityClasses()) {
                configuration.addAnnotatedClass(annotatedClass);
                log.debug("Добавлена аннотированная сущность: {}", annotatedClass.getName());
            }

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            log.info("SessionFactory для '{}' успешно создан.", dbType.getIdentifier());
            return sessionFactory;
        } catch (Exception e) {
            log.error("Критическая ошибка при создании SessionFactory для '{}': {}", dbType.getIdentifier(), e.getMessage(), e);
            throw new IllegalStateException("Ошибка инициализации SessionFactory для " + dbType.getIdentifier(), e);
        }
    }

    /**
     * Возвращает существующий экземпляр {@link SessionFactory} для указанного типа базы данных, если он уже создан.
     * В отличие от {@link #getSessionFactory(DatabaseType)}, этот метод не пытается создать новый экземпляр.
     *
     * @param dbType тип базы данных.
     * @return {@link SessionFactory} или {@code null}, если он еще не был создан.
     */
    public static SessionFactory getSessionFactoryIfExists(DatabaseType dbType) {
        return sessionFactories.get(dbType);
    }

    /**
     * Корректно закрывает все созданные и кэшированные экземпляры {@link SessionFactory}.
     * Этот метод следует вызывать при завершении работы приложения для освобождения ресурсов.
     */
    public static synchronized void shutdown() {
        if (sessionFactories.isEmpty()) {
            log.info("Нет активных SessionFactory для закрытия.");
            return;
        }

        log.info("Начало процесса закрытия всех SessionFactory...");
        sessionFactories.forEach((dbType, sessionFactory) -> {
            try {
                if (sessionFactory != null && !sessionFactory.isClosed()) {
                    sessionFactory.close();
                    log.info("SessionFactory для '{}' успешно закрыт.", dbType.getIdentifier());
                }
            } catch (Exception e) {
                log.error("Ошибка при закрытии SessionFactory для '{}': {}", dbType.getIdentifier(), e.getMessage(), e);
            }
        });
        sessionFactories.clear();
        log.info("Все SessionFactory были закрыты.");
    }
}
