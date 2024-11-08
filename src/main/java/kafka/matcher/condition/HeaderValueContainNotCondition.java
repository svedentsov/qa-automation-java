package kafka.matcher.condition;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка условия отсутствия текста в значении заголовка записи Kafka.
 * Условие используется для валидации, что значение заголовка не содержит заданный текст.
 */
@RequiredArgsConstructor
public class HeaderValueContainNotCondition implements Condition {

    private final String headerKey;
    private final String text;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        var header = record.headers().lastHeader(headerKey);
        Assertions.assertThat(header)
                .as("Заголовок с ключом '%s' не должен быть null", headerKey)
                .isNotNull();

        String actualValue = new String(header.value());
        Assertions.assertThat(actualValue)
                .as("Проверка, что значение заголовка не содержит текст: " + text)
                .doesNotContain(text);
    }

    @Override
    public String toString() {
        return String.format("Условие значения заголовка Kafka, не содержащего текст: %s", text);
    }
}
