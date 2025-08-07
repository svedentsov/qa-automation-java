package com.svedentsov.db.enums;

import com.svedentsov.db.entity.MyEntity;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Перечисление для централизованного управления конфигурациями различных баз данных.
 * <p>
 * Каждый элемент перечисления представляет собой отдельную БД и содержит:
 * <ul>
 *   <li>Уникальный идентификатор.</li>
 *   <li>Путь к конфигурационному файлу Hibernate.</li>
 *   <li>Массив классов-сущностей, относящихся к этой БД.</li>
 * </ul>
 * Этот подход упрощает инициализацию {@link org.hibernate.SessionFactory}
 * через {@link com.svedentsov.db.factory.SessionFactoryProvider}.
 * </p>
 */
@Getter
public enum DatabaseType {

    /**
     * Конфигурация для первой базы данных.
     */
    DB1("db1", "hibernate-db1.cfg.xml",
            MyEntity.class
    ),

    /**
     * Конфигурация для второй базы данных.
     */
    DB2("db2", "hibernate-db2.cfg.xml",
            MyEntity.class
    );

    private final String identifier;
    private final String configFilePath;
    private final Class<?>[] entityClasses;

    /**
     * Конструктор для DatabaseType.
     *
     * @param identifier     уникальный идентификатор базы данных.
     * @param configFilePath путь к конфигурационному файлу Hibernate.
     * @param entityClasses  массив классов-сущностей, связанных с базой данных.
     */
    DatabaseType(String identifier, String configFilePath, Class<?>... entityClasses) {
        this.identifier = identifier;
        this.configFilePath = configFilePath;
        this.entityClasses = entityClasses; // defensive copy is not strictly needed for class literals
    }

    /**
     * Возвращает строку с описанием конфигурации базы данных для логирования и отладки.
     *
     * @return строковое представление конфигурации.
     */
    @Override
    public String toString() {
        return String.format("DatabaseType{id=%s, configFile=%s, entityClasses=[%s]}",
                identifier, configFilePath, formatEntityClasses());
    }

    /**
     * Форматирует массив классов сущностей в читабельную строку.
     *
     * @return строка, представляющая имена классов сущностей.
     */
    private String formatEntityClasses() {
        if (entityClasses == null || entityClasses.length == 0) {
            return "";
        }
        return Arrays.stream(entityClasses)
                .map(Class::getSimpleName)
                .collect(Collectors.joining(", "));
    }
}
