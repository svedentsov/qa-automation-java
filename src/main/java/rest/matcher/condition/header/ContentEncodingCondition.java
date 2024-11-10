package rest.matcher.condition.header;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки заголовка Content-Encoding.
 */
@AllArgsConstructor
public class ContentEncodingCondition implements Condition {

    private final String expectedEncoding;

    @Override
    public void check(Response response) {
        String encoding = response.getHeader("Content-Encoding");
        if (encoding == null || !encoding.equalsIgnoreCase(expectedEncoding)) {
            throw new AssertionError(String.format("Content-Encoding '%s' не соответствует ожидаемому '%s'", encoding, expectedEncoding));
        }
    }

    @Override
    public String toString() {
        return String.format("Content-Encoding равен '%s'", expectedEncoding);
    }
}
