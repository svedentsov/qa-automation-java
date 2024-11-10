package rest.matcher.condition.body;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки, что тело ответа не содержит заданную строку.
 */
@AllArgsConstructor
public class BodyNotContainsStringCondition implements Condition {

    private final String unexpectedText;

    @Override
    public void check(Response response) {
        String body = response.getBody().asString();
        if (body.contains(unexpectedText)) {
            throw new AssertionError(String.format("Тело ответа не должно содержать '%s'", unexpectedText));
        }
    }

    @Override
    public String toString() {
        return String.format("Тело ответа не содержит '%s'", unexpectedText);
    }
}
