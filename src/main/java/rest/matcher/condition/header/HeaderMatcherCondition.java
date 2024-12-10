package rest.matcher.condition.header;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.HamcrestCondition;
import org.hamcrest.Matcher;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки значения заголовка с использованием Hamcrest Matcher.
 */
@AllArgsConstructor
public class HeaderMatcherCondition implements Condition {

    private final String headerName;
    private final Matcher<?> matcher;

    @Override
    public void check(Response response) {
        String headerValue = response.getHeader(headerName);
        Assertions.assertThat(headerValue)
                .as("Значение заголовка %s не соответствует ожидаемому условию", headerName)
                .is(new HamcrestCondition<>(matcher));
    }

    @Override
    public String toString() {
        return String.format("Значение заголовка %s соответствует условию: %s", headerName, matcher);
    }
}
