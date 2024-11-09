package kafka.matcher.condition;

import com.jayway.jsonpath.JsonPath;
import kafka.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка, что числовое значение по JSONPath больше заданного.
 */
@RequiredArgsConstructor
public class ValueJsonPathNumberGreaterCondition implements Condition {

    private final String jsonPath;
    private final Number threshold;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        Number value = JsonPath.read(record.value(), jsonPath);
        Assertions.assertThat(value.doubleValue())
                .as("Значение по JSONPath '%s' должно быть больше %s", jsonPath, threshold)
                .isGreaterThan(threshold.doubleValue());
    }

    @Override
    public String toString() {
        return String.format("Условие: значение по JSONPath %s должно быть больше %s", jsonPath, threshold);
    }
}
