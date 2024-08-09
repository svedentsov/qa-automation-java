package rest.matcher.condition;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.Condition;

/**
 * Класс, представляющий условие проверки наличия заголовка в ответе.
 * <p>
 * Данный класс реализует интерфейс {@link Condition}, предоставляя функциональность
 * проверки того, присутствует ли ожидаемый заголовок с именем {@link #expectedHeaderName} в ответе.
 */
@AllArgsConstructor
public class HeaderContainsCondition implements Condition {

    private String expectedHeaderName;

    @Override
    public void check(Response response) {
        boolean hasHeader = response.then().extract().headers().hasHeaderWithName(expectedHeaderName);
        if (!hasHeader) {
            throw new AssertionError("Заголовок с именем \"" + expectedHeaderName + "\" не найден.");
        }
    }

    @Override
    public String toString() {
        return "Заголовок \"" + expectedHeaderName + "\" должен присутствовать.";
    }
}
