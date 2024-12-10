package rest.matcher.condition.time;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import rest.matcher.condition.Condition;

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
        Assertions.assertThat(responseTime)
                .as("Время ответа %d мс превышает максимум %d мс", responseTime, maxDuration.toMillis())
                .isLessThanOrEqualTo(maxDuration.toMillis());
    }

    @Override
    public String toString() {
        return String.format("Время ответа меньше %d мс", maxDuration.toMillis());
    }
}
