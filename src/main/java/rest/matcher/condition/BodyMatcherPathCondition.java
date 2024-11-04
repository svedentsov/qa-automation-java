package rest.matcher.condition;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.hamcrest.Matcher;
import rest.matcher.Condition;

/**
 * Условие для проверки определенного пути в теле ответа с использованием Hamcrest Matcher.
 */
@AllArgsConstructor
public class BodyMatcherPathCondition implements Condition {

    private final String path;
    private final Matcher<?> matcher;

    @Override
    public void check(Response response) {
        response.then().body(path, matcher);
    }

    @Override
    public String toString() {
        return String.format("Путь в теле ответа '%s' соответствует условию: %s", path, matcher);
    }
}
