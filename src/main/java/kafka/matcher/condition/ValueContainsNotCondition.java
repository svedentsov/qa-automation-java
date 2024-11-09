package kafka.matcher.condition;

import kafka.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Проверка условия, что значение записи не содержит указанные тексты.
 */
@RequiredArgsConstructor
public class ValueContainsNotCondition implements Condition {

    private final List<String> texts;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        for (String text : texts) {
            Assertions.assertThat(record.value())
                    .as("Проверка, что значение записи не содержит текст: " + text)
                    .doesNotContain(text);
        }
    }

    @Override
    public String toString() {
        return String.format("Условие проверки, что значение записи не содержит тексты: %s", texts);
    }
}
