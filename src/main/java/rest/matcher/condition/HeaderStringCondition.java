package rest.matcher.condition;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.Condition;

/**
 * Класс, представляющий условие проверки заголовка в ответе на соответствие
 * ожидаемому значению.
 * <p>
 * Этот класс реализует интерфейс {@link Condition}, предоставляя функциональность
 * проверки того, что значение заголовка с именем {@link #headerName} равно ожидаемому
 * значению {@link #expectedValue}.
 */
@AllArgsConstructor
public class HeaderStringCondition implements Condition {

    private String headerName;
    private String expectedValue;

    @Override
    public void check(Response response) {
        response.then().assertThat().header(headerName, expectedValue);
    }

    @Override
    public String toString() {
        return "Заголовок \"" + headerName + "\" должен быть равен: " + expectedValue;
    }
}
