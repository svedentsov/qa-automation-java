package kafka.matcher;

import java.util.Collection;

/**
 * Функциональный интерфейс для проверки сущностей типа T.
 *
 * @param <T> тип проверяемой сущности
 */
@FunctionalInterface
public interface Condition<T> {

    /**
     * Проверяет одну сущность типа T.
     *
     * @param t сущность для проверки
     */
    void check(T t);

    /**
     * Дефолтный метод для проверки коллекции сущностей типа T.
     * Для каждой сущности из коллекции вызывается метод {@link #check(Object)}.
     *
     * @param items коллекция сущностей для проверки
     */
    default void checkAll(Collection<T> items) {
        items.forEach(this::check);
    }
}
