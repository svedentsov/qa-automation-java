package rest.matcher.condition;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.hamcrest.Matcher;
import rest.matcher.Condition;

/**
 * Класс, представляющий условие проверки тела ответа с использованием Hamcrest Matcher.
 * <p>
 * Данный класс реализует интерфейс {@link Condition}, предоставляя функциональность
 * проверки тела ответа с использованием переданного {@link Matcher}.
 */
@AllArgsConstructor
public class BodyMatcherCondition implements Condition {

    private Matcher matcher;

    @Override
    public void check(Response response) {
        response.then().assertThat().body(matcher);
    }

    @Override
    public String toString() {
        return "Тело ответа соответствует: " + matcher.toString();
    }
}
