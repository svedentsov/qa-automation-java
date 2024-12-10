package rest.matcher.condition.body;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.HamcrestCondition;
import org.hamcrest.Matcher;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки размера тела ответа с использованием Hamcrest Matcher.
 */
@AllArgsConstructor
public class BodySizeCondition implements Condition {

    private final Matcher<Integer> matcher;

    @Override
    public void check(Response response) {
        int bodySize = response.getBody().asByteArray().length;
        Assertions.assertThat(bodySize)
                .as("Размер тела %d не соответствует условию %s", bodySize, matcher)
                .is(new HamcrestCondition<>(matcher));
    }

    @Override
    public String toString() {
        return String.format("Размер тела соответствует условию %s", matcher);
    }
}
