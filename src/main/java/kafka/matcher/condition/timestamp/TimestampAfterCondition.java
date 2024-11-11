package kafka.matcher.condition.timestamp;

import kafka.matcher.condition.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.time.Instant;

/**
 * Проверка, что временная метка записи Kafka позже заданного времени.
 */
@RequiredArgsConstructor
public class TimestampAfterCondition implements Condition {

    private final Instant time;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        Assertions.assertThat(Instant.ofEpochMilli(record.timestamp()))
                .as("Временная метка записи должна быть позже %s", time)
                .isAfter(time);
    }

    @Override
    public String toString() {
        return String.format("Условие: временная метка записи должна быть позже %s", time);
    }
}
