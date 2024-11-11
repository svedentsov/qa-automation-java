package rest.matcher.condition.body;

import io.restassured.response.Response;
import io.restassured.specification.Argument;
import lombok.AllArgsConstructor;
import org.hamcrest.Matcher;
import rest.matcher.condition.Condition;

import java.util.List;

/**
 * Условие для проверки тела ответа с использованием списка аргументов и Hamcrest Matcher.
 */
@AllArgsConstructor
public class BodyMatcherListArgsCondition implements Condition {

    private final List<Argument> arguments;
    private final Matcher<?> matcher;

    @Override
    public void check(Response response) {
        response.then().body(arguments, matcher);
    }

    @Override
    public String toString() {
        return String.format("Тело ответа с аргументами '%s' соответствует условию: '%s'", arguments, matcher);
    }
}
