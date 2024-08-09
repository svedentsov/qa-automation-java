package rest.matcher.condition;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import rest.matcher.Condition;

/**
 * Класс, представляющий условие проверки наличия определенного текста в теле ответа.
 * <p>
 * Данный класс реализует интерфейс {@link Condition}, предоставляя функциональность
 * проверки того, содержит ли тело ответа заданный текст {@link #bodyText}.
 */
@AllArgsConstructor
public class BodyStringCondition implements Condition {

    private String bodyText;

    @Override
    public void check(Response response) {
        String body = response.then().assertThat().extract().body().asString();
        Assertions.assertTrue(body.contains(bodyText), this::toString);
    }

    @Override
    public String toString() {
        return "Тело ответа должно содержать: " + bodyText;
    }
}
