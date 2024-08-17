package rest.matcher.condition;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.Condition;
/**
 * Класс, представляющий условие проверки кода состояния HTTP-ответа.
 * Этот класс реализует интерфейс {@link Condition}, предоставляя функциональность
 * проверки того, что код состояния ответа равен ожидаемому значению {@link #statusCode}.
 */
@AllArgsConstructor
public class StatusCodeCondition implements Condition {

    private int statusCode;

    @Override
    public void check(Response response) {
        response.then().assertThat().statusCode(statusCode);
    }

    @Override
    public String toString() {
        return "Код состояния: " + statusCode;
    }
}
