package rest.matcher.condition.composite;

import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import rest.matcher.condition.Condition;

import java.util.Arrays;
import java.util.stream.Collectors;

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
        return "Любое из условий: " + Arrays.stream(conditions)
                .map(Condition::toString)
                .collect(Collectors.joining("; "));
    }
}
