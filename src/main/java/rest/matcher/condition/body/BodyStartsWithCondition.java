package rest.matcher.condition.body;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки, что тело ответа начинается с указанного префикса.
 */
@AllArgsConstructor
public class BodyStartsWithCondition implements Condition {

    private final String prefix;

    @Override
    public void check(Response response) {
        String body = response.getBody().asString();
        Assertions.assertThat(body)
                .as("Тело ответа должно начинаться с %s", prefix)
                .startsWith(prefix);
    }

    @Override
    public String toString() {
        return String.format("Тело ответа начинается с %s", prefix);
    }
}
