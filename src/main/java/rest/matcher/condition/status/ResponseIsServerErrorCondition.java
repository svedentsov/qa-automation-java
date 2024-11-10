package rest.matcher.condition.status;

import io.restassured.response.Response;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки, что код состояния является ошибкой сервера (5xx).
 */
public class ResponseIsServerErrorCondition implements Condition {

    @Override
    public void check(Response response) {
        int statusCode = response.getStatusCode();
        if (statusCode < 500 || statusCode >= 600) {
            throw new AssertionError(String.format("Ожидался код состояния ошибки сервера (5xx), но получен %d", statusCode));
        }
    }

    @Override
    public String toString() {
        return "Код состояния является ошибкой сервера (5xx)";
    }
}