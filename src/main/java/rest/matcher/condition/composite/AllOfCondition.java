package rest.matcher.condition.composite;

import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import rest.matcher.condition.Condition;

import java.util.Arrays;
import java.util.stream.Collectors;

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
        return "Все условия: " + Arrays.stream(conditions)
                .map(Condition::toString)
                .collect(Collectors.joining("; "));
    }
}
