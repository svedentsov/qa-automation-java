package rest.matcher.condition.header;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки значения заголовка в ответе.
 */
@AllArgsConstructor
public class HeaderStringCondition implements Condition {

    private final String headerName;
    private final String expectedValue;

    @Override
    public void check(Response response) {
        String headerValue = response.getHeader(headerName);
        Assertions.assertThat(headerValue)
                .as("Значение заголовка '%s' должно быть '%s'", headerName, expectedValue)
                .isEqualTo(expectedValue);
    }

    @Override
    public String toString() {
        return String.format("Значение заголовка '%s' равно '%s'", headerName, expectedValue);
    }
}
