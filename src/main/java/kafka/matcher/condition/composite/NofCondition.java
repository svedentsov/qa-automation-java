package kafka.matcher.condition.composite;

import kafka.matcher.condition.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.Arrays;

/**
 * Условие, которое успешно проходит, если выполняются хотя бы N из заданных условий.
 */
@RequiredArgsConstructor
public class NofCondition implements Condition {

    private final int n;
    private final Condition[] conditions;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        long successCount = Arrays.stream(conditions)
                .filter(condition -> {
                    try {
                        condition.check(record);
                        return true;
                    } catch (AssertionError e) {
                        return false;
                    }
                })
                .count();

        Assertions.assertThat(successCount)
                .as("Ожидалось, что хотя бы '%d' условий выполнятся, но выполнено '%d'", n, successCount)
                .isGreaterThanOrEqualTo(n);
    }

    @Override
    public String toString() {
        return String.format("Условие: хотя бы '%d' из следующих условий должны выполниться: '%s'", n,
                Arrays.toString(conditions));
    }
}
