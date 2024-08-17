package rest.matcher.condition;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.Condition;

/**
 * Класс, представляющий условие проверки типа контента в ответе.
 * Данный класс реализует интерфейс {@link Condition}, предоставляя функциональность
 * проверки того, соответствует ли тип контента ответа ожидаемому {@link #contentType}.
 */
@AllArgsConstructor
public class ContentTypeCondition implements Condition {

    private ContentType contentType;

    @Override
    public void check(Response response) {
        response.then().assertThat().contentType(contentType);
    }

    @Override
    public String toString() {
        return "Ожидаемый тип контента: " + contentType.toString();
    }
}
