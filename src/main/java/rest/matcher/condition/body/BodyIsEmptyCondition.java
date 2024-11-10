package rest.matcher.condition.body;

import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки, что тело ответа пустое.
 */
public class BodyIsEmptyCondition implements Condition {

    @Override
    public void check(Response response) {
        String body = response.getBody().asString();
        Assertions.assertThat(body)
                .isEmpty();
    }

    @Override
    public String toString() {
        return "Тело ответа пустое";
    }
}
