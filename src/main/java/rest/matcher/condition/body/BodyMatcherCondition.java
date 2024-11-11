package rest.matcher.condition.body;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.HamcrestCondition;
import org.hamcrest.Matcher;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки тела ответа с использованием Hamcrest Matcher.
 */
@AllArgsConstructor
public class BodyMatcherCondition implements Condition {

    private final Matcher<?> matcher;

    @Override
    public void check(Response response) {
        Object body = response.getBody().as(Object.class);
        Assertions.assertThat(body)
                .as("Тело ответа не соответствует ожидаемому условию")
                .is(new HamcrestCondition<>(matcher));
    }

    @Override
    public String toString() {
        return String.format("Тело ответа соответствует условию: '%s'", matcher);
    }
}
