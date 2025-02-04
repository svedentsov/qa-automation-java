package db.matcher.condition;

import java.util.List;

/**
 * Функциональный интерфейс для проверки списка сущностей.
 *
 * @param <T> тип проверяемой сущности
 */
@FunctionalInterface
public interface Conditions<T> {
    /**
     * Выполняет проверку списка сущностей.
     *
     * @param entities список сущностей для проверки
     */
    void check(List<T> entities);
}
