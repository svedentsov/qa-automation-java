package rest.matcher.condition;

import io.restassured.response.Response;

/**
 * Функциональный интерфейс для условия проверки HTTP ответа.
 */
@FunctionalInterface
public interface Condition {
    /**
     * Проверяет условие на заданном ответе.
     *
     * @param response ответ HTTP для проверки
     */
    void check(Response response);
}
