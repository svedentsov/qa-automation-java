package rest.matcher.condition.body;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.condition.Condition;

import java.util.regex.Pattern;

/**
 * Условие для проверки соответствия тела ответа регулярному выражению.
 */
@AllArgsConstructor
public class BodyMatchesPatternCondition implements Condition {

    private final Pattern pattern;

    @Override
    public void check(Response response) {
        String body = response.getBody().asString();
        if (!pattern.matcher(body).matches()) {
            throw new AssertionError(String.format("Тело ответа не соответствует шаблону '%s'", pattern.pattern()));
        }
    }

    @Override
    public String toString() {
        return String.format("Тело ответа соответствует шаблону '%s'", pattern.pattern());
    }
}
