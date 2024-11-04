package rest.matcher.condition;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.hamcrest.Matcher;
import rest.matcher.Condition;

/**
 * Условие для проверки длины содержимого ответа с использованием Hamcrest Matcher.
 */
@AllArgsConstructor
public class ContentLengthCondition implements Condition {

    private final Matcher<Long> matcher;

    @Override
    public void check(Response response) {
        String contentLengthHeader = response.getHeader("Content-Length");
        if (contentLengthHeader == null) {
            throw new AssertionError("Заголовок Content-Length отсутствует в ответе");
        }
        long contentLength = Long.parseLong(contentLengthHeader);
        if (!matcher.matches(contentLength)) {
            throw new AssertionError(String.format("Длина содержимого %d не соответствует условию %s", contentLength, matcher));
        }
    }

    @Override
    public String toString() {
        return String.format("Длина содержимого соответствует условию %s", matcher);
    }
}
