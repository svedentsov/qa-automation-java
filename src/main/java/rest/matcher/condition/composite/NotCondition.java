package rest.matcher.condition.composite;

import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import rest.matcher.condition.Condition;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Композитное условие, которое инвертирует результат указанных условий (логическое НЕ).
 */
@RequiredArgsConstructor
public class NotCondition implements Condition {

    private final Condition[] conditions;

    @Override
    public void check(Response response) {
        for (Condition condition : conditions) {
            try {
                condition.check(response);
                throw new AssertionError("Условие выполнено, но ожидалось, что оно не пройдет: " + condition);
            } catch (AssertionError e) {
                // Условие не выполнено, как и ожидалось, продолжаем
            }
        }
    }

    @Override
    public String toString() {
        return "Не условия: " + Arrays.stream(conditions)
                .map(Condition::toString)
                .collect(Collectors.joining("; "));
    }
}
