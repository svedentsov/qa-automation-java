package db.matcher;

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
}
