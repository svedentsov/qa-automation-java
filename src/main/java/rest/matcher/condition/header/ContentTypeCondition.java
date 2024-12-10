package rest.matcher.condition.header;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки типа содержимого ответа.
 */
@AllArgsConstructor
public class ContentTypeCondition implements Condition {

    private final ContentType contentType;

    @Override
    public void check(Response response) {
        String actualContentType = response.getContentType();
        Assertions.assertThat(actualContentType)
                .as("Тип содержимого %s не соответствует ожидаемому %s", actualContentType, contentType)
                .isEqualToIgnoringCase(contentType.toString());
    }

    @Override
    public String toString() {
        return String.format("Тип содержимого: %s", contentType);
    }
}
