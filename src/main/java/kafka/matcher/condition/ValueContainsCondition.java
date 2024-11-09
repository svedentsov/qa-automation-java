package kafka.matcher.condition;

import kafka.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Проверка условия наличия нескольких текстов в значении сообщения Kafka.
 * Условие используется для валидации, что значение сообщения содержит все указанные тексты.
 */
@RequiredArgsConstructor
public class ValueContainsCondition implements Condition {

    private final List<String> texts;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        for (String text : texts) {
            Assertions.assertThat(record.value())
                    .as("Проверка, что сообщение содержит текст: " + text)
                    .contains(text);
        }
    }

    @Override
    public String toString() {
        return String.format("Условие проверки, что сообщение содержит все тексты: %s", texts);
    }
}
