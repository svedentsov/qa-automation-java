package kafka.matcher.condition;

import kafka.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка условия соответствия ключа записи Kafka.
 * Условие используется для валидации ключа записи.
 */
@RequiredArgsConstructor
public class KeyEqualCondition implements Condition {

    private final String expectedKey;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        Assertions.assertThat(record.key())
                .as("Проверка ключа")
                .isEqualTo(expectedKey);
    }

    @Override
    public String toString() {
        return String.format("Условие ключа Kafka с ожидаемым значением: %s", expectedKey);
    }
}
