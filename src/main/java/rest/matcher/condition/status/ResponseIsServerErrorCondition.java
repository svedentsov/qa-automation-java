package rest.matcher.condition.status;

import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки, что код состояния является ошибкой сервера (5xx).
 */
public class ResponseIsServerErrorCondition implements Condition {

    @Override
    public void check(Response response) {
        int statusCode = response.getStatusCode();
        Assertions.assertThat(statusCode)
                .as("Ожидался код состояния ошибки сервера (5xx), но получен '%d'", statusCode)
                .isBetween(500, 599);
    }

    @Override
    public String toString() {
        return "Код состояния является ошибкой сервера (5xx)";
    }
}
