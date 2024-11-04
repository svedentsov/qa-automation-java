package rest.matcher.condition;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.Condition;

/**
 * Условие для проверки наличия куки в ответе.
 */
@AllArgsConstructor
public class CookieExistsCondition implements Condition {

    private final String cookieName;

    @Override
    public void check(Response response) {
        boolean hasCookie = response.getCookies().containsKey(cookieName);
        if (!hasCookie) {
            throw new AssertionError(String.format("Кука '%s' не найдена в ответе.", cookieName));
        }
    }

    @Override
    public String toString() {
        return String.format("Кука '%s' присутствует", cookieName);
    }
}
