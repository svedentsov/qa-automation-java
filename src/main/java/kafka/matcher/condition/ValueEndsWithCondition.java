package kafka.matcher.condition;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка, что значение записи Kafka заканчивается указанным текстом.
 */
@RequiredArgsConstructor
public class ValueEndsWithCondition implements Condition {

    private final String suffix;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        Assertions.assertThat(record.value())
                .as("Проверка, что значение записи заканчивается текстом: '%s'", suffix)
                .endsWith(suffix);
    }

    @Override
    public String toString() {
        return String.format("Условие: значение записи должно заканчиваться текстом '%s'", suffix);
    }
}
