package rest.matcher.condition.status;

import io.restassured.response.Response;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки, что код состояния является успешным (2xx).
 */
public class ResponseIsSuccessCondition implements Condition {

    @Override
    public void check(Response response) {
        int statusCode = response.getStatusCode();
        if (statusCode < 200 || statusCode >= 300) {
            throw new AssertionError(String.format("Ожидался успешный код состояния (2xx), но получен %d", statusCode));
        }
    }

    @Override
    public String toString() {
        return "Код состояния является успешным (2xx)";
    }
}