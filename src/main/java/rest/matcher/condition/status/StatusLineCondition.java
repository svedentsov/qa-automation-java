package rest.matcher.condition.status;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки строки состояния ответа.
 */
@AllArgsConstructor
public class StatusLineCondition implements Condition {

    private final String statusLine;

    @Override
    public void check(Response response) {
        String actualStatusLine = response.getStatusLine();
        Assertions.assertThat(actualStatusLine)
                .as("Строка состояния должна быть %s, но получена %s", statusLine, actualStatusLine)
                .isEqualTo(statusLine);
    }

    @Override
    public String toString() {
        return String.format("Строка состояния: %s", statusLine);
    }
}
