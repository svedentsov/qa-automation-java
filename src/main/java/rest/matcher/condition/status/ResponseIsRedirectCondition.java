package rest.matcher.condition.status;

import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки, что ответ является перенаправлением (код состояния 3xx).
 */
public class ResponseIsRedirectCondition implements Condition {

    @Override
    public void check(Response response) {
        int statusCode = response.getStatusCode();
        Assertions.assertThat(statusCode)
                .as("Ожидался код состояния 3xx, но получен '%d'", statusCode)
                .isBetween(300, 399);
    }

    @Override
    public String toString() {
        return "Ответ является перенаправлением (код состояния 3xx)";
    }
}
