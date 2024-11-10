package rest.matcher.condition.body;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.condition.Condition;

import java.util.List;

/**
 * Условие для проверки, что тело ответа содержит любую из указанных строк.
 */
@AllArgsConstructor
public class BodyContainsAnyStringCondition implements Condition {

    private final List<String> expectedStrings;

    @Override
    public void check(Response response) {
        String body = response.getBody().asString();
        for (String expected : expectedStrings) {
            if (body.contains(expected)) {
                return;
            }
        }
        throw new AssertionError(String.format("Тело ответа не содержит ни одной из строк %s", expectedStrings));
    }

    @Override
    public String toString() {
        return String.format("Тело ответа содержит любую из строк %s", expectedStrings);
    }
}
