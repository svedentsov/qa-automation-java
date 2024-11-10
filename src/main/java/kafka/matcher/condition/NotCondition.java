package kafka.matcher.condition;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Условие, инвертирующее результат других условий.
 */
@RequiredArgsConstructor
public class NotCondition implements Condition {

    private final Condition[] conditions;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        for (Condition condition : conditions) {
            try {
                condition.check(record);
                Assertions.fail("Условие должно быть не выполнено, но оно выполнено: " + condition);
            } catch (AssertionError e) {
                // Ожидаемый результат: условие не выполнено
            }
        }
    }

    @Override
    public String toString() {
        return "NOT (" + Arrays.stream(conditions)
                .map(Condition::toString)
                .collect(Collectors.joining(", ")) + ")";
    }
}
