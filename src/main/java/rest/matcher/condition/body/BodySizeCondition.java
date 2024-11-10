package rest.matcher.condition.body;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
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
        if (!matcher.matches(bodySize)) {
            throw new AssertionError(String.format("Размер тела %d не соответствует условию %s", bodySize, matcher));
        }
    }

    @Override
    public String toString() {
        return String.format("Размер тела соответствует условию %s", matcher);
    }
}
