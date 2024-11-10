package rest.matcher.condition.header;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки отсутствия заголовка в ответе.
 */
@AllArgsConstructor
public class HeaderAbsentCondition implements Condition {

    private final String headerName;

    @Override
    public void check(Response response) {
        boolean hasHeader = response.headers().hasHeaderWithName(headerName);
        if (hasHeader) {
            throw new AssertionError(String.format("Заголовок '%s' должен отсутствовать, но он присутствует.", headerName));
        }
    }

    @Override
    public String toString() {
        return String.format("Заголовок '%s' отсутствует", headerName);
    }
}
