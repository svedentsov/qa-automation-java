package rest.matcher.condition.status;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки, что код состояния принадлежит определенному классу (1xx, 2xx, 3xx, 4xx, 5xx).
 */
@AllArgsConstructor
public class StatusCodeClassCondition implements Condition {

    private final int statusClass;

    @Override
    public void check(Response response) {
        int statusCode = response.getStatusCode();
        int codeClass = statusCode / 100;
        if (codeClass != statusClass) {
            throw new AssertionError(String.format("Код состояния %d не принадлежит классу %dxx", statusCode, statusClass));
        }
    }

    @Override
    public String toString() {
        return String.format("Код состояния принадлежит классу %dxx", statusClass);
    }
}