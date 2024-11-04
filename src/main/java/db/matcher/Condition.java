package db.matcher;

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
     * @throws Exception если происходит ошибка при проверке
     */
    void check(T entity) throws Exception;
}
