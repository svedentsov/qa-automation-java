package kafka.matcher.condition;

import kafka.matcher.Condition;
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

    /**
     * Проверяет, что значение записи содержит указанный текст.
     *
     * @param record запись Kafka, значение которой будет проверяться
     */
    @Override
    public void check(ConsumerRecord<String, String> record) {
        Assertions.assertThat(record.value())
                .as("Проверка, что значение записи содержит текст: " + text)
                .contains(text);
    }

    @Override
    public String toString() {
        return "Условие проверки, что значение записи содержит текст: " + text;
    }
}
