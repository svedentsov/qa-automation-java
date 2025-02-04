package db.matcher.condition;

/**
 * Функциональный интерфейс для проверки одной сущности.
 *
 * @param <T> тип проверяемой сущности
 */
@FunctionalInterface
public interface Condition<T> {
    /**
     * Выполняет проверку сущности.
     *
     * @param entity сущность для проверки
     */
    void check(T entity);
}
