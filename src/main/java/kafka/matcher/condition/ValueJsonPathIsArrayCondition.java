package kafka.matcher.condition;

import com.jayway.jsonpath.JsonPath;
import kafka.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Проверка, что значение по выражению JSONPath является массивом.
 */
@RequiredArgsConstructor
public class ValueJsonPathIsArrayCondition implements Condition {

    private final String jsonPath;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        String value = record.value();
        Object json = JsonPath.parse(value).read(jsonPath);
        Assertions.assertThat(json)
                .as("Значение по JSONPath '%s' не является массивом", jsonPath)
                .isInstanceOf(List.class);
    }

    @Override
    public String toString() {
        return "Значение по JSONPath " + jsonPath + " должно быть массивом";
    }
}
