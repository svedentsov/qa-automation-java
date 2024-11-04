package rest.matcher.condition;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.Condition;

/**
 * Условие для проверки наличия заголовка в ответе.
 */
@AllArgsConstructor
public class HeaderExistsCondition implements Condition {

    private final String headerName;

    @Override
    public void check(Response response) {
        boolean hasHeader = response.headers().hasHeaderWithName(headerName);
        if (!hasHeader) {
            throw new AssertionError(String.format("Заголовок '%s' не найден в ответе.", headerName));
        }
    }

    @Override
    public String toString() {
        return String.format("Заголовок '%s' присутствует", headerName);
    }
}
