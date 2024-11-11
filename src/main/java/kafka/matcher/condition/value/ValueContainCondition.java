package kafka.matcher.condition.value;

import kafka.matcher.condition.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка условия наличия текста в значении записи Kafka.
 * Условие используется для валидации, что значение записи содержит заданный текст.
 */
@RequiredArgsConstructor
public class ValueContainCondition implements Condition {

    private final String text;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        Assertions.assertThat(record.value())
                .as("Проверка, что значение записи содержит текст: " + text)
                .contains(text);
    }

    @Override
    public String toString() {
        return String.format("Условие проверки, что значение записи содержит текст: %s", text);
    }
}
