package rest.matcher.condition;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.Condition;

/**
 * Условие для проверки, что тело ответа содержит заданный текст.
 */
@AllArgsConstructor
public class BodyStringCondition implements Condition {

    private final String expectedText;

    @Override
    public void check(Response response) {
        String body = response.getBody().asString();
        if (!body.contains(expectedText)) {
            throw new AssertionError(String.format("Тело ответа не содержит ожидаемый текст: '%s'", expectedText));
        }
    }

    @Override
    public String toString() {
        return String.format("Тело ответа содержит '%s'", expectedText);
    }
}
