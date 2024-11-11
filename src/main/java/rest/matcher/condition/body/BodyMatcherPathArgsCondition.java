package rest.matcher.condition.body;

import io.restassured.response.Response;
import io.restassured.specification.Argument;
import lombok.AllArgsConstructor;
import org.hamcrest.Matcher;
import rest.matcher.condition.Condition;

import java.util.List;

/**
 * Условие для проверки определенного пути в теле ответа с использованием списка аргументов и Hamcrest Matcher.
 */
@AllArgsConstructor
public class BodyMatcherPathArgsCondition implements Condition {

    private final String path;
    private final List<Argument> arguments;
    private final Matcher<?> matcher;

    @Override
    public void check(Response response) {
        response.then().body(path, arguments, matcher);
    }

    @Override
    public String toString() {
        return String.format("Путь в теле ответа '%s' с аргументами '%s' соответствует условию: '%s'", path, arguments, matcher);
    }
}
