package kafka.matcher.conditions;

import kafka.matcher.Conditions;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Проверка условия на количество записей Kafka.
 * Условие используется для валидации, что количество записей равно заданному значению.
 */
@RequiredArgsConstructor
public class RecordCountEqualConditions implements Conditions {

    private final int count;

    @Override
    public void check(List<ConsumerRecord<String, String>> records) {
        Assertions.assertThat(records)
                .as("Проверка, что количество записей равно " + count)
                .hasSize(count);
    }

    @Override
    public String toString() {
        return String.format("Условие, что количество записей равно %d", count);
    }
}
