package kafka.matcher.condition;

import kafka.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка условия, что значение записи Kafka не содержит заданный текст.
 */
@RequiredArgsConstructor
public class ValueContainNotCondition implements Condition {

    private final String text;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        Assertions.assertThat(record.value())
                .as("Проверка, что значение записи не содержит текст: " + text)
                .doesNotContain(text);
    }

    @Override
    public String toString() {
        return String.format("Условие проверки, что значение записи не содержит текст: %s", text);
    }
}
