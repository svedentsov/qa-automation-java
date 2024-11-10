package rest.matcher.condition.body;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.hamcrest.Matcher;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки значения по JSON-пути с использованием Hamcrest Matcher.
 */
@AllArgsConstructor
public class BodyJsonPathValueCondition implements Condition {

    private final String jsonPath;
    private final Matcher<?> matcher;

    @Override
    public void check(Response response) {
        response.then().body(jsonPath, matcher);
    }

    @Override
    public String toString() {
        return String.format("Значение по JSON-пути '%s' соответствует условию: %s", jsonPath, matcher);
    }
}