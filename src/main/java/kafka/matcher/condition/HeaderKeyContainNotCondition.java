package kafka.matcher.condition;

import kafka.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка условия отсутствия текста в ключе заголовка записи Kafka.
 * Условие используется для валидации, что ключ заголовка не содержит заданный текст.
 */
@RequiredArgsConstructor
public class HeaderKeyContainNotCondition implements Condition {

    private final String text;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        boolean textNotFound = true;
        for (var header : record.headers()) {
            if (header.key().contains(text)) {
                textNotFound = false;
                break;
            }
        }
        Assertions.assertThat(textNotFound)
                .as("Проверка, что ключ заголовка не содержит текст: " + text)
                .isTrue();
    }

    @Override
    public String toString() {
        return "Условие ключа заголовка Kafka, не содержащего текст: " + text;
    }
}
