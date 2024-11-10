package rest.matcher.condition.time;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.hamcrest.Matcher;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки времени ответа с использованием Hamcrest Matcher.
 */
@AllArgsConstructor
public class ResponseTimeMatchesCondition implements Condition {

    private final Matcher<Long> matcher;

    @Override
    public void check(Response response) {
        long responseTime = response.getTime();
        if (!matcher.matches(responseTime)) {
            throw new AssertionError(String.format("Время ответа %d мс не соответствует условию %s", responseTime, matcher));
        }
    }

    @Override
    public String toString() {
        return String.format("Время ответа соответствует условию %s", matcher);
    }
}
