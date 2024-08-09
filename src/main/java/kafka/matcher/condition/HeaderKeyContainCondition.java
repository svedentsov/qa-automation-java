package kafka.matcher.condition;

import kafka.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка условия наличия текста в ключе заголовка записи Kafka.
 * Условие используется для валидации, что ключ заголовка содержит заданный текст.
 */
@RequiredArgsConstructor
public class HeaderKeyContainCondition implements Condition {

    private final String text;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        boolean textFound = false;
        for (var header : record.headers()) {
            if (header.key().contains(text)) {
                textFound = true;
                break;
            }
        }
        Assertions.assertThat(textFound)
                .as("Проверка, что ключ заголовка содержит текст: " + text)
                .isTrue();
    }

    @Override
    public String toString() {
        return "Условие ключа заголовка Kafka, содержащего текст: " + text;
    }
}
