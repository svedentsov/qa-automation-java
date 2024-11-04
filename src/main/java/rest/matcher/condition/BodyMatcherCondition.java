package rest.matcher.condition;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.hamcrest.Matcher;
import rest.matcher.Condition;

/**
 * Условие для проверки тела ответа с использованием Hamcrest Matcher.
 */
@AllArgsConstructor
public class BodyMatcherCondition implements Condition {

    private final Matcher<?> matcher;

    @Override
    public void check(Response response) {
        response.then().body(matcher);
    }

    @Override
    public String toString() {
        return String.format("Тело ответа соответствует условию: %s", matcher);
    }
}
