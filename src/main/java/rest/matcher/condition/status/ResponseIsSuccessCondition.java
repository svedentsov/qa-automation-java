package rest.matcher.condition.status;

import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки, что код состояния является успешным (2xx).
 */
public class ResponseIsSuccessCondition implements Condition {

    @Override
    public void check(Response response) {
        int statusCode = response.getStatusCode();
        Assertions.assertThat(statusCode)
                .as("Ожидался успешный код состояния (2xx), но получен '%d'", statusCode)
                .isBetween(200, 299);
    }

    @Override
    public String toString() {
        return "Код состояния является успешным (2xx)";
    }
}
