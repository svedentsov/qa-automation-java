package rest.matcher.condition;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.Condition;

import java.time.Duration;

/**
 * Условие для проверки, что время ответа меньше заданного значения.
 */
@AllArgsConstructor
public class ResponseTimeCondition implements Condition {

    private final Duration maxDuration;

    @Override
    public void check(Response response) {
        long responseTime = response.getTime();
        if (responseTime > maxDuration.toMillis()) {
            throw new AssertionError(String.format("Время ответа %d мс превышает максимум %d мс", responseTime, maxDuration.toMillis()));
        }
    }

    @Override
    public String toString() {
        return String.format("Время ответа меньше %d мс", maxDuration.toMillis());
    }
}
