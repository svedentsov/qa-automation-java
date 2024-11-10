package rest.matcher.condition.status;

import io.restassured.response.Response;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки, что ответ является перенаправлением (код состояния 3xx).
 */
public class ResponseIsRedirectCondition implements Condition {

    @Override
    public void check(Response response) {
        int statusCode = response.getStatusCode();
        if (statusCode < 300 || statusCode >= 400) {
            throw new AssertionError(String.format("Ожидался код состояния 3xx, но получен %d", statusCode));
        }
    }

    @Override
    public String toString() {
        return "Ответ является перенаправлением (код состояния 3xx)";
    }
}