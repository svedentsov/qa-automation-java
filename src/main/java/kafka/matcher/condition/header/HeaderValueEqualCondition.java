package kafka.matcher.condition.header;

import kafka.matcher.condition.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка условия соответствия значения заголовка записи Kafka.
 * Условие используется для валидации значения заголовка записи.
 */
@RequiredArgsConstructor
public class HeaderValueEqualCondition implements Condition {

    private final String headerKey;
    private final String expectedValue;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        var header = record.headers().lastHeader(headerKey);
        Assertions.assertThat(header)
                .as("Заголовок с ключом '%s' не должен быть null", headerKey)
                .isNotNull();

        String actualValue = new String(header.value());
        Assertions.assertThat(actualValue)
                .as("Значение заголовка Kafka")
                .isEqualTo(expectedValue);
    }

    @Override
    public String toString() {
        return String.format("Условие значения заголовка Kafka с ожидаемым значением: '%s'", expectedValue);
    }
}
