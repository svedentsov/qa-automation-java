package rest.matcher.condition.body;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки, что тело ответа содержит заданную строку без учета регистра.
 */
@AllArgsConstructor
public class BodyContainsStringIgnoringCaseCondition implements Condition {

    private final String expectedText;

    @Override
    public void check(Response response) {
        String body = response.getBody().asString();
        if (!body.toLowerCase().contains(expectedText.toLowerCase())) {
            throw new AssertionError(String.format("Тело ответа не содержит '%s' (без учета регистра)", expectedText));
        }
    }

    @Override
    public String toString() {
        return String.format("Тело ответа содержит '%s' (без учета регистра)", expectedText);
    }
}
