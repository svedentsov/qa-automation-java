package kafka.matcher.condition.value;

import kafka.matcher.condition.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка, что значение записи Kafka начинается с указанного текста.
 */
@RequiredArgsConstructor
public class ValueStartsWithCondition implements Condition {

    private final String prefix;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        Assertions.assertThat(record.value())
                .as("Проверка, что значение записи начинается с текста: '%s'", prefix)
                .startsWith(prefix);
    }

    @Override
    public String toString() {
        return String.format("Условие: значение записи должно начинаться с текста '%s'", prefix);
    }
}
