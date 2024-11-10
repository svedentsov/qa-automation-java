package kafka.matcher.condition;

import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка, что значение записи не содержит указанный текст по выражению JSONPath.
 */
@RequiredArgsConstructor
public class ValueJsonPathContainNotCondition implements Condition {

    private final String jsonPath;
    private final String text;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        String value = record.value();
        Object json = JsonPath.parse(value).read(jsonPath);
        Assertions.assertThat(json.toString())
                .as("Значение по JSONPath '%s' содержит '%s'", jsonPath, text)
                .doesNotContain(text);
    }

    @Override
    public String toString() {
        return String.format("Значение по JSONPath %s не должно содержать: %s", jsonPath, text);
    }
}
