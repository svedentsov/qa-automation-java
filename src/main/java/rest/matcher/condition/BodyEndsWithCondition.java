package rest.matcher.condition;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.Condition;

/**
 * Условие для проверки, что тело ответа заканчивается на указанный суффикс.
 */
@AllArgsConstructor
public class BodyEndsWithCondition implements Condition {

    private final String suffix;

    @Override
    public void check(Response response) {
        String body = response.getBody().asString();
        if (!body.endsWith(suffix)) {
            throw new AssertionError(String.format("Тело ответа не заканчивается на '%s'", suffix));
        }
    }

    @Override
    public String toString() {
        return String.format("Тело ответа заканчивается на '%s'", suffix);
    }
}
