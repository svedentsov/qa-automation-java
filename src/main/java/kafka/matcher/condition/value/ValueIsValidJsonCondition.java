package kafka.matcher.condition.value;

import com.fasterxml.jackson.databind.ObjectMapper;
import kafka.matcher.condition.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка, что значение записи является валидным JSON.
 */
@RequiredArgsConstructor
public class ValueIsValidJsonCondition implements Condition {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void check(ConsumerRecord<String, String> record) {
        String value = record.value();
        Assertions.assertThatCode(() -> objectMapper.readTree(value))
                .as("Проверка, что значение записи является валидным JSON")
                .withFailMessage("Значение записи не является валидным JSON: '%s'", value)
                .doesNotThrowAnyException();
    }

    @Override
    public String toString() {
        return "Условие: значение записи должно быть валидным JSON";
    }
}
