package kafka.matcher.condition.value;

import com.jayway.jsonpath.JsonPath;
import kafka.matcher.condition.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Проверка, что JSON значение содержит указанные ключи.
 */
@RequiredArgsConstructor
public class ValueJsonContainsKeysCondition implements Condition {

    private final List<String> keys;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        String value = record.value();
        for (String key : keys) {
            try {
                Object json = JsonPath.parse(value).read("$." + key);
                Assertions.assertThat(json)
                        .as("Проверка, что ключ '%s' присутствует в JSON", key)
                        .isNotNull();
            } catch (Exception e) {
                Assertions.fail("Ключ '%s' отсутствует в JSON", key);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("Условие: JSON должен содержать ключи %s", keys);
    }
}
