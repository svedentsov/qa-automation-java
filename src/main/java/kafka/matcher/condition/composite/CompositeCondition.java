package kafka.matcher.condition.composite;

import com.jayway.jsonpath.internal.filter.LogicalOperator;
import kafka.matcher.condition.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.Arrays;

/**
 * Композитное условие для комбинирования нескольких условий.
 */
@RequiredArgsConstructor
public class CompositeCondition implements Condition {

    private final LogicalOperator operator;
    private final Condition[] conditions;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        switch (operator) {
            case AND:
                for (Condition condition : conditions) {
                    condition.check(record);
                }
                break;
            case OR:
                boolean atLeastOnePassed = Arrays.stream(conditions).anyMatch(condition -> {
                    try {
                        condition.check(record);
                        return true;
                    } catch (AssertionError e) {
                        return false;
                    }
                });
                Assertions.assertThat(atLeastOnePassed)
                        .as("Ни одно из условий OR не выполнено")
                        .isTrue();
                break;
            default:
                throw new UnsupportedOperationException("Неизвестный логический оператор: " + operator);
        }
    }

    @Override
    public String toString() {
        String conditionsStr = Arrays.stream(conditions)
                .map(Condition::toString)
                .reduce((a, b) -> a + " " + operator.name() + " " + b)
                .orElse("");
        return "Композитное условие: " + conditionsStr;
    }
}
