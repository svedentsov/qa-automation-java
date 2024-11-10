package rest.matcher.condition.header;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки типа содержимого ответа.
 */
@AllArgsConstructor
public class ContentTypeCondition implements Condition {

    private final ContentType contentType;

    @Override
    public void check(Response response) {
        response.then().contentType(contentType);
    }

    @Override
    public String toString() {
        return String.format("Тип содержимого: '%s'", contentType);
    }
}
