package db.matcher;

import java.util.Collection;

/**
 * Функциональный интерфейс для проверки одной сущности.
 *
 * @param <T> тип проверяемой сущности
 */
@FunctionalInterface
public interface Condition<T> {

    /**
     * Проверяет одну сущность.
     *
     * @param entity проверяемая сущность
     */
    void check(T entity);

    /**
     * Дефолтный метод для проверки коллекции сущностей.
     * Вызывает метод {@link #check(Object)} для каждой сущности в коллекции.
     *
     * @param entities коллекция сущностей для проверки
     */
    default void checkAll(Collection<T> entities) {
        entities.forEach(this::check);
    }
}
