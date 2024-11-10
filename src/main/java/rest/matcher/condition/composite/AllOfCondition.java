package rest.matcher.condition.composite;

import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import rest.matcher.condition.Condition;

/**
 * Композитное условие, которое проходит, если выполнены все указанные условия (логическое И).
 */
@RequiredArgsConstructor
public class AllOfCondition implements Condition {

    private final Condition[] conditions;

    @Override
    public void check(Response response) {
        for (Condition condition : conditions) {
            condition.check(response);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("All of conditions: ");
        for (Condition condition : conditions) {
            sb.append(condition).append("; ");
        }
        return sb.toString();
    }
}
