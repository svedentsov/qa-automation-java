package db.matcher.condition;

import java.util.List;

/**
 * Интерфейс Conditions для проверки списка сущностей базы данных.
 *
 * @param <T> тип сущности
 */
@FunctionalInterface
public interface Conditions<T> {
    /**
     * Метод для проверки списка сущностей.
     *
     * @param entities список сущностей для проверки
     */
    void check(List<T> entities);
}
