package rest.matcher.condition;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.hamcrest.Matcher;
import rest.matcher.Condition;

/**
 * Класс, представляющий условие проверки конкретного поля тела ответа с использованием пути и Hamcrest Matcher.
 * <p>
 * Данный класс реализует интерфейс {@link Condition}, предоставляя функциональность
 * проверки значения поля тела ответа, определенного путем {@link #path}, с использованием переданного {@link Matcher}.
 */
@AllArgsConstructor
public class BodyMatcherPathCondition implements Condition {

    private String path;
    private Matcher matcher;

    @Override
    public void check(Response response) {
        response.then().assertThat().body(path, matcher);
    }

    @Override
    public String toString() {
        return "Поле тела ответа \'" + path + "\' должно соответствовать условию: " + matcher;
    }
}
