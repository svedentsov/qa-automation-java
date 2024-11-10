package db.matcher.condition;

/**
 * Интерфейс Condition для проверки одной сущности базы данных.
 *
 * @param <T> тип сущности
 */
public interface Condition<T> {

    /**
     * Метод для проверки сущности.
     *
     * @param entity сущность для проверки
     */
    void check(T entity);
}
