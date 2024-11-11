package rest.matcher.condition.cookie;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки наличия куки в ответе.
 */
@AllArgsConstructor
public class CookieExistsCondition implements Condition {

    private final String cookieName;

    @Override
    public void check(Response response) {
        boolean hasCookie = response.getCookies().containsKey(cookieName);
        Assertions.assertThat(hasCookie)
                .as("Кука '%s' не найдена в ответе.", cookieName)
                .isTrue();
    }

    @Override
    public String toString() {
        return String.format("Кука '%s' присутствует", cookieName);
    }
}
