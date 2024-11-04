package rest.matcher.condition;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.Condition;

/**
 * Условие для проверки, что тело ответа начинается с указанного префикса.
 */
@AllArgsConstructor
public class BodyStartsWithCondition implements Condition {

    private final String prefix;

    @Override
    public void check(Response response) {
        String body = response.getBody().asString();
        if (!body.startsWith(prefix)) {
            throw new AssertionError(String.format("Тело ответа не начинается с '%s'", prefix));
        }
    }

    @Override
    public String toString() {
        return String.format("Тело ответа начинается с '%s'", prefix);
    }
}
