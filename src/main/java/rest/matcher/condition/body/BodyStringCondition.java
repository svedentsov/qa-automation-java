package rest.matcher.condition.body;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки, что тело ответа содержит заданный текст.
 */
@AllArgsConstructor
public class BodyStringCondition implements Condition {

    private final String expectedText;

    @Override
    public void check(Response response) {
        String body = response.getBody().asString();
        Assertions.assertThat(body)
                .as("Тело ответа не содержит ожидаемый текст: %s", expectedText)
                .contains(expectedText);
    }

    @Override
    public String toString() {
        return String.format("Тело ответа содержит %s", expectedText);
    }
}
