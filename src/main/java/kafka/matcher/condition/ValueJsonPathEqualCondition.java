package kafka.matcher.condition;

import com.jayway.jsonpath.JsonPath;
import kafka.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка условия соответствия значения по JSONPath для записи Kafka.
 * Условие используется для валидации, что значение, полученное из JSON по указанному пути,
 * соответствует ожидаемому значению.
 */
@RequiredArgsConstructor
public class ValueJsonPathEqualCondition implements Condition {

    private final String jsonPath;
    private final String expectedValue;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        String actualValue = JsonPath.read(record.value(), jsonPath);
        Assertions.assertThat(actualValue)
                .as("Проверка значения JSONPath")
                .isEqualTo(expectedValue);
    }

    @Override
    public String toString() {
        return "Условие JSONPath с ожидаемым значением: " + expectedValue;
    }
}
