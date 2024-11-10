package kafka.matcher.condition;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка, что запись Kafka принадлежит указанному разделу.
 */
@RequiredArgsConstructor
public class PartitionEqualCondition implements Condition {

    private final int partition;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        Assertions.assertThat(record.partition())
                .as("Проверка, что запись принадлежит разделу %d", partition)
                .isEqualTo(partition);
    }

    @Override
    public String toString() {
        return String.format("Условие: запись должна принадлежать разделу %d", partition);
    }
}
