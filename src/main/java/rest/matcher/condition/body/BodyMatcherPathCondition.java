package rest.matcher.condition.body;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.HamcrestCondition;
import org.hamcrest.Matcher;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки определенного пути в теле ответа с использованием Hamcrest Matcher.
 */
@AllArgsConstructor
public class BodyMatcherPathCondition implements Condition {

    private final String path;
    private final Matcher<?> matcher;

    @Override
    public void check(Response response) {
        Object value = response.getBody().path(path);
        Assertions.assertThat(value)
                .as("Значение по пути '%s' не соответствует ожидаемому условию", path)
                .is(new HamcrestCondition<>(matcher));
    }

    @Override
    public String toString() {
        return String.format("Путь в теле ответа '%s' соответствует условию: '%s'", path, matcher);
    }
}
