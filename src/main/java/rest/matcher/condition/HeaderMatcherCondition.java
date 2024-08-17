package rest.matcher.condition;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.hamcrest.Matcher;
import rest.matcher.Condition;

/**
 * Класс, представляющий условие проверки заголовка в ответе с использованием {@link Matcher}.
 * Этот класс реализует интерфейс {@link Condition}, предоставляя функциональность
 * проверки того, соответствует ли заголовок с именем {@link #headerName} ожидаемому значению,
 * заданному с помощью {@link #matcher}.
 */
@AllArgsConstructor
public class HeaderMatcherCondition implements Condition {

    private String headerName;
    private Matcher matcher;

    @Override
    public void check(Response response) {
        response.then().assertThat().header(headerName, matcher);
    }

    @Override
    public String toString() {
        return "Заголовок \"" + headerName + "\" " + matcher.toString();
    }
}
