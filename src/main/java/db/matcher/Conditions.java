package db.matcher;

import java.util.List;

/**
 * Интерфейс Conditions для проверки списка сущностей базы данных.
 *
 * @param <T> тип сущности
 */
public interface Conditions<T> {

    /**
     * Метод для проверки списка сущностей.
     *
     * @param entities список сущностей для проверки
     * @throws Exception если происходит ошибка при проверке
     */
    void check(List<T> entities) throws Exception;
}
