package rest.matcher.condition.header;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.condition.Condition;

import java.util.regex.Pattern;

/**
 * Условие для проверки значения заголовка по регулярному выражению.
 */
@AllArgsConstructor
public class HeaderMatchesPatternCondition implements Condition {

    private final String headerName;
    private final Pattern pattern;

    @Override
    public void check(Response response) {
        String headerValue = response.getHeader(headerName);
        if (headerValue == null || !pattern.matcher(headerValue).matches()) {
            throw new AssertionError(String.format("Заголовок '%s' со значением '%s' не соответствует шаблону '%s'", headerName, headerValue, pattern.pattern()));
        }
    }

    @Override
    public String toString() {
        return String.format("Заголовок '%s' соответствует шаблону '%s'", headerName, pattern.pattern());
    }
}
