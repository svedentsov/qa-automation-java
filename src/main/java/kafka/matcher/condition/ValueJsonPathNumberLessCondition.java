package kafka.matcher.condition;

import com.jayway.jsonpath.JsonPath;
import kafka.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка, что числовое значение по JSONPath меньше заданного.
 */
@RequiredArgsConstructor
public class ValueJsonPathNumberLessCondition implements Condition {

    private final String jsonPath;
    private final Number threshold;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        Number value = JsonPath.read(record.value(), jsonPath);
        Assertions.assertThat(value.doubleValue())
                .as("Значение по JSONPath '%s' должно быть меньше %s", jsonPath, threshold)
                .isLessThan(threshold.doubleValue());
    }

    @Override
    public String toString() {
        return String.format("Условие: значение по JSONPath %s должно быть меньше %s", jsonPath, threshold);
    }
}