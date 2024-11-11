package rest.matcher.condition.body;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import rest.matcher.condition.Condition;

import java.util.List;

/**
 * Условие для проверки, что тело ответа содержит все указанные строки.
 */
@AllArgsConstructor
public class BodyContainsAllStringsCondition implements Condition {

    private final List<String> expectedStrings;

    @Override
    public void check(Response response) {
        String body = response.getBody().asString();
        for (String expected : expectedStrings) {
            Assertions.assertThat(body)
                    .as("Тело ответа должно содержать строку '%s'", expected)
                    .contains(expected);
        }
    }

    @Override
    public String toString() {
        return String.format("Тело ответа содержит все строки '%s'", expectedStrings);
    }
}
