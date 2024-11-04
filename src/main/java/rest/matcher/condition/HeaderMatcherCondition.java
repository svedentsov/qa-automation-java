package rest.matcher.condition;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.hamcrest.Matcher;
import rest.matcher.Condition;

/**
 * Условие для проверки значения заголовка с использованием Hamcrest Matcher.
 */
@AllArgsConstructor
public class HeaderMatcherCondition implements Condition {

    private final String headerName;
    private final Matcher<?> matcher;

    @Override
    public void check(Response response) {
        response.then().header(headerName, matcher);
    }

    @Override
    public String toString() {
        return String.format("Значение заголовка '%s' соответствует условию: %s", headerName, matcher);
    }
}
