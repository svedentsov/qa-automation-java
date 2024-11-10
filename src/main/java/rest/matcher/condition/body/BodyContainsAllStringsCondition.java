package rest.matcher.condition.body;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.condition.Condition;

import java.util.List;

/**
 * Условие для проверки, что тело ответа содержит все указанные строки.
 */
@AllArgsConstructor
public class BodyContainsAllStringsCondition implements Condition {

    private final List<String> expectedStrings;

    @Override
    public void check(Response response) {
        String body = response.getBody().asString();
        for (String expected : expectedStrings) {
            if (!body.contains(expected)) {
                throw new AssertionError(String.format("Тело ответа не содержит строку '%s'", expected));
            }
        }
    }

    @Override
    public String toString() {
        return String.format("Тело ответа содержит все строки %s", expectedStrings);
    }
}
