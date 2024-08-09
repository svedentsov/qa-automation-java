package rest.matcher.condition;

import io.restassured.response.Response;
import io.restassured.specification.Argument;
import lombok.AllArgsConstructor;
import org.hamcrest.Matcher;
import rest.matcher.Condition;

import java.util.List;

/**
 * Класс, представляющий условие проверки тела ответа с использованием списка аргументов и Hamcrest Matcher.
 * <p>
 * Данный класс реализует интерфейс {@link Condition}, предоставляя функциональность
 * проверки тела ответа с использованием переданного списка {@link Argument} и {@link Matcher}.
 */
@AllArgsConstructor
public class BodyMatcherListArgsCondition implements Condition {

    private List<Argument> arguments;
    private Matcher matcher;

    @Override
    public void check(Response response) {
        response.then().assertThat().body(arguments, matcher);
    }

    @Override
    public String toString() {
        return "Аргументы: \"" + arguments.toString() + "\" должны соответствовать условию: " + matcher;
    }
}
