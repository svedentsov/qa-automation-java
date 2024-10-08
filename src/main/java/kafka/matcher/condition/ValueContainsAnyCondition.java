package kafka.matcher.condition;

import kafka.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Проверка, что значение записи содержит хотя бы один из указанных текстов.
 */
@RequiredArgsConstructor
public class ValueContainsAnyCondition implements Condition {

    private final List<String> texts;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        String value = record.value();
        for (String text : texts) {
            if (value.contains(text)) {
                return;
            }
        }
        Assertions.fail("Значение записи не содержит ни одного из текстов: " + texts);
    }

    @Override
    public String toString() {
        return "Значение должно содержать хотя бы один из текстов: " + texts;
    }
}
