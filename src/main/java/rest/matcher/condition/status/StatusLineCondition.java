package rest.matcher.condition.status;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки строки состояния ответа.
 */
@AllArgsConstructor
public class StatusLineCondition implements Condition {

    private final String statusLine;

    @Override
    public void check(Response response) {
        response.then().statusLine(statusLine);
    }

    @Override
    public String toString() {
        return String.format("Строка состояния: '%s'", statusLine);
    }
}
