package rest.matcher.condition.status;

import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки, что код состояния является ошибкой клиента (4xx).
 */
public class ResponseIsClientErrorCondition implements Condition {

    @Override
    public void check(Response response) {
        int statusCode = response.getStatusCode();
        Assertions.assertThat(statusCode)
                .as("Ожидался код состояния ошибки клиента (4xx), но получен '%d'", statusCode)
                .isBetween(400, 499);
    }

    @Override
    public String toString() {
        return "Код состояния является ошибкой клиента (4xx)";
    }
}
