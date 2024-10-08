package kafka.matcher.condition;

import kafka.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка значения записи Kafka на соответствие ожидаемому значению.
 * Условие используется для валидации одной записи.
 */
@RequiredArgsConstructor
public class ValueEqualCondition implements Condition {

    private final String expectedValue;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        Assertions.assertThat(record.value())
                .as("Значение записи Kafka")
                .isEqualTo(expectedValue);
    }

    @Override
    public String toString() {
        return "Условие значения Kafka с ожидаемым значением: " + expectedValue;
    }
}
