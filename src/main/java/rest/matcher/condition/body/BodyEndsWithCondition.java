package rest.matcher.condition.body;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки, что тело ответа заканчивается на указанный суффикс.
 */
@AllArgsConstructor
public class BodyEndsWithCondition implements Condition {

    private final String suffix;

    @Override
    public void check(Response response) {
        String body = response.getBody().asString();
        Assertions.assertThat(body)
                .as("Тело ответа должно заканчиваться на '%s'", suffix)
                .endsWith(suffix);
    }

    @Override
    public String toString() {
        return String.format("Тело ответа заканчивается на '%s'", suffix);
    }
}
