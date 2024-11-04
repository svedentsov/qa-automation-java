package rest.matcher.condition;

import io.restassured.response.Response;
import lombok.NoArgsConstructor;
import org.assertj.core.api.Assertions;
import rest.matcher.Condition;

/**
 * Условие для проверки, что тело ответа пустое.
 */
@NoArgsConstructor
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
