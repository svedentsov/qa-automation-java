package db.enums;

import lombok.Getter;

/**
 * Перечисление для идентификации и конфигурации различных баз данных.
 * Обеспечивает централизованное управление идентификаторами, конфигурационными файлами и классами сущностей.
 */
@Getter
public enum DatabaseType {

    DB1("db1", "hibernate-db1.cfg.xml",
            db.entity.MyEntity.class
    ),
    DB2("db2", "hibernate-db2.cfg.xml",
            db.entity.MyEntity.class
    );

    private final String identifier;
    private final String configFilePath;
    private final Class<?>[] entityClasses;

    /**
     * Конструктор для DatabaseType.
     *
     * @param identifier     уникальный идентификатор базы данных
     * @param configFilePath путь к конфигурационному файлу Hibernate
     * @param entityClasses  массив классов сущностей, связанных с базой данных
     */
    DatabaseType(String identifier, String configFilePath, Class<?>... entityClasses) {
        this.identifier = identifier;
        this.configFilePath = configFilePath;
        this.entityClasses = entityClasses.clone(); // Использование clone для защиты от внешней модификации массива
    }

    /**
     * Метод для возврата строки с описанием конфигурации базы данных.
     * Подходит для логирования и отладки.
     *
     * @return строковое представление конфигурации базы данных
     */
    @Override
    public String toString() {
        return String.format("DatabaseType{id=%s, configFile=%s, entityClasses=%s}",
                identifier, configFilePath, formatEntityClasses());
    }

    /**
     * Форматирует массив классов сущностей для более читабельного вывода.
     *
     * @return строка, представляющая классы сущностей
     */
    private String formatEntityClasses() {
        StringBuilder sb = new StringBuilder();
        for (Class<?> entityClass : entityClasses) {
            sb.append(entityClass.getSimpleName()).append(", ");
        }
        // Удаление последней запятой и пробела, если они присутствуют
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }
}
