package rest.matcher.condition.status;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки кода состояния ответа.
 */
@AllArgsConstructor
public class StatusCodeCondition implements Condition {

    private final int statusCode;

    @Override
    public void check(Response response) {
        int actualStatusCode = response.getStatusCode();
        Assertions.assertThat(actualStatusCode)
                .as("Код состояния должен быть %d, но получен %d", statusCode, actualStatusCode)
                .isEqualTo(statusCode);
    }

    @Override
    public String toString() {
        return String.format("Код состояния: %d", statusCode);
    }
}
