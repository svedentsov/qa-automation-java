package rest.matcher.condition.cookie;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.hamcrest.Matcher;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки значения куки с использованием Hamcrest Matcher.
 */
@AllArgsConstructor
public class CookieMatcherCondition implements Condition {

    private final String cookieName;
    private final Matcher<?> matcher;

    @Override
    public void check(Response response) {
        response.then().cookie(cookieName, matcher);
    }

    @Override
    public String toString() {
        return String.format("Значение куки '%s' соответствует условию: %s", cookieName, matcher);
    }
}
