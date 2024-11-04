package rest.matcher.condition;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.Condition;

/**
 * Класс, представляющий условие проверки наличия заголовка в ответе.
 */
@AllArgsConstructor
public class HeaderContainsCondition implements Condition {

    private String expectedHeaderName;

    @Override
    public void check(Response response) {
        boolean hasHeader = response.then().extract().headers().hasHeaderWithName(expectedHeaderName);
        if (!hasHeader) {
            throw new AssertionError(String.format("Заголовок с именем '%s' не найден.", expectedHeaderName));
        }
    }

    @Override
    public String toString() {
        return String.format("Заголовок '%s' должен присутствовать.", expectedHeaderName);
    }
}
