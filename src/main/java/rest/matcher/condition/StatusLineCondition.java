package rest.matcher.condition;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.Condition;

/**
 * Класс, представляющий условие проверки строки состояния HTTP-ответа.
 * Этот класс реализует интерфейс {@link Condition}, предоставляя функциональность
 * проверки того, что строка состояния ответа совпадает с ожидаемым значением {@link #statusLine}.
 */
@AllArgsConstructor
public class StatusLineCondition implements Condition {

    private String statusLine;

    @Override
    public void check(Response response) {
        response.then().assertThat().statusLine(statusLine);
    }

    @Override
    public String toString() {
        return "Строка состояния: " + statusLine;
    }
}
