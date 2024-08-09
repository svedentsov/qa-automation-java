package rest.matcher;

import io.restassured.response.Response;

/**
 * Интерфейс Condition представляет условие проверки ответа HTTP.
 * Классы, реализующие этот интерфейс, могут быть использованы для выполнения различных проверок на ответ HTTP.
 */
public interface Condition {

    /**
     * Проверяет ответ HTTP согласно определенному условию.
     *
     * @param response Объект Response для проверки.
     */
    void check(Response response);
}
