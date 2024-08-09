package kafka.matcher.condition;

import com.jayway.jsonpath.JsonPath;
import kafka.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Проверка, что размер массива по выражению JSONPath равен ожидаемому.
 */
@RequiredArgsConstructor
public class ValueJsonPathArraySizeCondition implements Condition {

    private final String jsonPath;
    private final int expectedSize;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        String value = record.value();
        List<?> json = JsonPath.parse(value).read(jsonPath);
        Assertions.assertThat(json.size())
                .as("Размер массива по JSONPath '%s' должен быть %d", jsonPath, expectedSize)
                .isEqualTo(expectedSize);
    }

    @Override
    public String toString() {
        return "Размер массива по JSONPath " + jsonPath + " должен быть " + expectedSize;
    }
}
