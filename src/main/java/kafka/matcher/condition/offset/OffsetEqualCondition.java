package kafka.matcher.condition.offset;

import kafka.matcher.condition.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка, что запись Kafka имеет указанное смещение.
 */
@RequiredArgsConstructor
public class OffsetEqualCondition implements Condition {

    private final long offset;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        Assertions.assertThat(record.offset())
                .as("Проверка, что запись имеет смещение %d", offset)
                .isEqualTo(offset);
    }

    @Override
    public String toString() {
        return String.format("Условие: запись должна иметь смещение %d", offset);
    }
}
