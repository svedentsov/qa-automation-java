package rest.matcher.condition.composite;

import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import rest.matcher.condition.Condition;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Композитное условие, которое проходит, если выполнены хотя бы N из указанных условий.
 */
@RequiredArgsConstructor
public class NOfCondition implements Condition {

    private final int n;
    private final Condition[] conditions;

    @Override
    public void check(Response response) {
        long passedCount = Arrays.stream(conditions)
                .filter(condition -> {
                    try {
                        condition.check(response);
                        return true;
                    } catch (AssertionError e) {
                        return false;
                    }
                })
                .count();

        if (passedCount < n) {
            throw new AssertionError(String.format("Только '%d' из '%d' условий выполнено, а требуется как минимум '%d'.",
                    passedCount, conditions.length, n));
        }
    }

    @Override
    public String toString() {
        return String.format("Как минимум '%d' из условий: '%s'",
                n,
                Arrays.stream(conditions)
                        .map(Condition::toString)
                        .collect(Collectors.joining("; ")));
    }
}
