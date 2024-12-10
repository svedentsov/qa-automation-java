package rest.matcher.condition.status;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки, что код состояния находится в заданном диапазоне.
 */
@AllArgsConstructor
public class StatusCodeRangeCondition implements Condition {

    private final int startInclusive;
    private final int endInclusive;

    @Override
    public void check(Response response) {
        int statusCode = response.getStatusCode();
        Assertions.assertThat(statusCode)
                .as("Код состояния %d не находится в диапазоне [%d, %d]", statusCode, startInclusive, endInclusive)
                .isBetween(startInclusive, endInclusive);
    }

    @Override
    public String toString() {
        return String.format("Код состояния находится в диапазоне от %d до %d", startInclusive, endInclusive);
    }
}
