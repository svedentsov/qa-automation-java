package rest.matcher.condition;

import io.restassured.response.Response;
import io.restassured.specification.Argument;
import lombok.AllArgsConstructor;
import org.hamcrest.Matcher;
import rest.matcher.Condition;

import java.util.List;

/**
 * Класс, представляющий условие проверки определенного поля тела ответа с использованием пути, списка аргументов и Hamcrest Matcher.
 * <p>
 * Данный класс реализует интерфейс {@link Condition}, предоставляя функциональность
 * проверки поля тела ответа, определенного путем {@link #path}, с использованием переданного списка {@link Argument} и {@link Matcher}.
 */
@AllArgsConstructor
public class BodyMatcherPathArgsCondition implements Condition {

    private String path;
    private List<Argument> arguments;
    private Matcher matcher;

    @Override
    public void check(Response response) {
        response.then().assertThat().body(path, arguments, matcher);
    }

    @Override
    public String toString() {
        return "Поле тела ответа " + path + " с аргументами: \"" + arguments.toString() + "\" должно соответствовать условию: " + matcher;
    }
}
