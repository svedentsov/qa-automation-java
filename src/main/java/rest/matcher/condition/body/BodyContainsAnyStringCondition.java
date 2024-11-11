package rest.matcher.condition.body;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import rest.matcher.condition.Condition;

import java.util.List;

/**
 * Условие для проверки, что тело ответа содержит любую из указанных строк.
 */
@AllArgsConstructor
public class BodyContainsAnyStringCondition implements Condition {

    private final List<String> expectedStrings;

    @Override
    public void check(Response response) {
        String body = response.getBody().asString();
        boolean containsAny = expectedStrings.stream().anyMatch(body::contains);
        Assertions.assertThat(containsAny)
                .as("Тело ответа не содержит ни одной из строк '%s'", expectedStrings)
                .isTrue();
    }

    @Override
    public String toString() {
        return String.format("Тело ответа содержит любую из строк '%s'", expectedStrings);
    }
}
