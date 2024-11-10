package rest.matcher.condition.cookie;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки значения куки в ответе.
 */
@AllArgsConstructor
public class CookieStringCondition implements Condition {

    private final String cookieName;
    private final String expectedValue;

    @Override
    public void check(Response response) {
        response.then().cookie(cookieName, expectedValue);
    }

    @Override
    public String toString() {
        return String.format("Значение куки '%s' равно '%s'", cookieName, expectedValue);
    }
}
