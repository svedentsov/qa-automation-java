package rest.matcher.condition.body;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.HamcrestCondition;
import org.hamcrest.Matcher;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки значения по JSON-пути с использованием Hamcrest Matcher.
 */
@AllArgsConstructor
public class BodyJsonPathValueCondition implements Condition {

    private final String jsonPath;
    private final Matcher<?> matcher;

    @Override
    public void check(Response response) {
        Object value = response.getBody().path(jsonPath);
        Assertions.assertThat(value)
                .as("Значение по JSON-пути '%s' не соответствует ожидаемому", jsonPath)
                .is(new HamcrestCondition<>(matcher));
    }

    @Override
    public String toString() {
        return String.format("Значение по JSON-пути '%s' соответствует условию: '%s'", jsonPath, matcher);
    }
}
