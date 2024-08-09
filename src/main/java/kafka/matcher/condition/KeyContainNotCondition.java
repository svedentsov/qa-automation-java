package kafka.matcher.condition;

import kafka.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка условия, что ключ записи Kafka не содержит заданный текст.
 */
@RequiredArgsConstructor
public class KeyContainNotCondition implements Condition {

    private final String text;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        Assertions.assertThat(record.key())
                .as("Проверка, что ключ записи не содержит текст: " + text)
                .doesNotContain(text);
    }

    @Override
    public String toString() {
        return "Условие проверки, что ключ записи не содержит текст: " + text;
    }
}
