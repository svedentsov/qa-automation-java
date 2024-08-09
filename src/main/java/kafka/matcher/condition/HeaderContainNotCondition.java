package kafka.matcher.condition;

import kafka.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка условия, что заголовок записи Kafka не содержит заданный текст.
 */
@RequiredArgsConstructor
public class HeaderContainNotCondition implements Condition {

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
                .as("Проверка, что заголовок не содержит текст: " + text)
                .doesNotContain(text);
    }

    @Override
    public String toString() {
        return "Условие проверки, что заголовок записи не содержит текст: " + text;
    }
}
