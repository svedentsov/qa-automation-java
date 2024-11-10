package rest.matcher.condition.status;

import io.restassured.response.Response;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки, что код состояния является ошибкой клиента (4xx).
 */
public class ResponseIsClientErrorCondition implements Condition {

    @Override
    public void check(Response response) {
        int statusCode = response.getStatusCode();
        if (statusCode < 400 || statusCode >= 500) {
            throw new AssertionError(String.format("Ожидался код состояния ошибки клиента (4xx), но получен %d", statusCode));
        }
    }

    @Override
    public String toString() {
        return "Код состояния является ошибкой клиента (4xx)";
    }
}
