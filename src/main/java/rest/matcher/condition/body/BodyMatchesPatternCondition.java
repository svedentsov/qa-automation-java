package rest.matcher.condition.body;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
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
        Assertions.assertThat(body)
                .as("Тело ответа не соответствует шаблону '%s'", pattern.pattern())
                .matches(pattern);
    }

    @Override
    public String toString() {
        return String.format("Тело ответа соответствует шаблону '%s'", pattern.pattern());
    }
}
