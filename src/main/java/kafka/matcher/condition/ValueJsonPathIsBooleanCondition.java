package kafka.matcher.condition;

import com.jayway.jsonpath.JsonPath;
import kafka.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка, что значение по выражению JSONPath является булевым значением.
 */
@RequiredArgsConstructor
public class ValueJsonPathIsBooleanCondition implements Condition {

    private final String jsonPath;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        String value = record.value();
        Object json = JsonPath.parse(value).read(jsonPath);
        Assertions.assertThat(json)
                .as("Значение по JSONPath '%s' не является булевым", jsonPath)
                .isInstanceOf(Boolean.class);
    }

    @Override
    public String toString() {
        return String.format("Значение по JSONPath %s должно быть булевым значением", jsonPath);
    }
}
