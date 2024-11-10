package kafka.matcher.condition;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Проверка условия на количество записей Kafka.
 * Условие используется для валидации, что количество записей превышает заданное значение.
 */
@RequiredArgsConstructor
public class RecordCountGreaterConditions implements Conditions {

    private final int count;

    @Override
    public void check(List<ConsumerRecord<String, String>> records) {
        Assertions.assertThat(records)
                .as("Проверка, что количество записей больше " + count)
                .hasSizeGreaterThan(count);
    }

    @Override
    public String toString() {
        return String.format("Условие, что количество записей больше %d", count);
    }
}
