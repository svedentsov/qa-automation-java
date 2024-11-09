package kafka.matcher.condition;

import com.jayway.jsonpath.JsonPath;
import kafka.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка, что значение записи содержит указанный текст по выражению JSONPath.
 */
@RequiredArgsConstructor
public class ValueJsonPathContainCondition implements Condition {

    private final String jsonPath;
    private final String expectedValue;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        String value = record.value();
        Object json = JsonPath.parse(value).read(jsonPath);
        Assertions.assertThat(json.toString())
                .as("Значение по JSONPath '%s' не содержит '%s'", jsonPath, expectedValue)
                .contains(expectedValue);
    }

    @Override
    public String toString() {
        return String.format("Значение по JSONPath %s должно содержать: %s", jsonPath, expectedValue);
    }
}
