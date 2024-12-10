package rest.matcher.condition.header;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
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
        Assertions.assertThat(encoding)
                .as("Content-Encoding %s не соответствует ожидаемому %s", encoding, expectedEncoding)
                .isEqualToIgnoringCase(expectedEncoding);
    }

    @Override
    public String toString() {
        return String.format("Content-Encoding равен %s", expectedEncoding);
    }
}
