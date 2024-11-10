package kafka.matcher.condition;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка условия, что заголовок записи Kafka содержит заданный текст.
 */
@RequiredArgsConstructor
public class HeaderContainCondition implements Condition {

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
                .as("Проверка, что заголовок содержит текст: " + text)
                .contains(text);
    }

    @Override
    public String toString() {
        return String.format("Условие проверки, что заголовок записи содержит текст: %s", text);
    }
}
