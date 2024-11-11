package kafka.matcher.condition.value;

import com.fasterxml.jackson.databind.ObjectMapper;
import kafka.matcher.condition.Condition;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка, что значение записи является валидным JSON.
 */
public class ValueIsValidJsonCondition implements Condition {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void check(ConsumerRecord<String, String> record) {
        try {
            objectMapper.readTree(record.value());
        } catch (Exception e) {
            Assertions.fail("Значение записи не является валидным JSON: %s", e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Условие: значение записи должно быть валидным JSON";
    }
}
