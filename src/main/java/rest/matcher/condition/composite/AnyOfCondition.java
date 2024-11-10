package rest.matcher.condition.composite;

import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import rest.matcher.condition.Condition;

/**
 * Композитное условие, которое проходит, если выполнено любое из указанных условий (логическое ИЛИ).
 */
@RequiredArgsConstructor
public class AnyOfCondition implements Condition {

    private final Condition[] conditions;

    @Override
    public void check(Response response) {
        AssertionError lastError = null;
        for (Condition condition : conditions) {
            try {
                condition.check(response);
                return; // Если какое-либо условие выполнено, возвращаемся без исключения
            } catch (AssertionError e) {
                lastError = e;
            }
        }
        throw new AssertionError("Ни одно из условий не выполнено", lastError);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Любое из условий: ");
        for (Condition condition : conditions) {
            sb.append(condition).append("; ");
        }
        return sb.toString();
    }
}
