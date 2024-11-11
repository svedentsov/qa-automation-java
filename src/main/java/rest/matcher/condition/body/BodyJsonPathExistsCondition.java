package rest.matcher.condition.body;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки существования JSON-пути в теле ответа.
 */
@AllArgsConstructor
public class BodyJsonPathExistsCondition implements Condition {

    private final String jsonPath;

    @Override
    public void check(Response response) {
        Object value = response.getBody().path(jsonPath);
        Assertions.assertThat(value)
                .as("JSON-путь '%s' не найден в теле ответа", jsonPath)
                .isNotNull();
    }

    @Override
    public String toString() {
        return String.format("JSON-путь '%s' существует в теле ответа", jsonPath);
    }
}
