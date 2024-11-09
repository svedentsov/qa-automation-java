package kafka.matcher.condition;

import kafka.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.time.Instant;

/**
 * Проверка, что временная метка записи находится в заданном диапазоне.
 */
@RequiredArgsConstructor
public class TimestampInRangeCondition implements Condition {

    private final Instant startTime;
    private final Instant endTime;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        Instant timestamp = Instant.ofEpochMilli(record.timestamp());
        Assertions.assertThat(timestamp)
                .as("Временная метка записи должна быть в диапазоне от %s до %s", startTime, endTime)
                .isBetween(startTime, endTime);
    }

    @Override
    public String toString() {
        return String.format("Условие: временная метка записи должна быть в диапазоне от %s до %s", startTime, endTime);
    }
}
