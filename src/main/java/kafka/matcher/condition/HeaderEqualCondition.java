package kafka.matcher.condition;

import kafka.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Проверка условия для заголовка записи Kafka.
 * Условие используется для валидации наличия и значения заголовка.
 */
@RequiredArgsConstructor
public class HeaderEqualCondition implements Condition {

    private final String headerKey;
    private final String expectedValue;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        var header = record.headers().lastHeader(headerKey);
        assertThat(header)
                .as("Заголовок с ключом '%s' не должен быть null", headerKey)
                .isNotNull();

        String actualValue = new String(header.value());
        assertThat(actualValue)
                .as("Значение заголовка Kafka")
                .isEqualTo(expectedValue);
    }

    @Override
    public String toString() {
        return "Условие заголовка Kafka с ожидаемым значением: " + expectedValue;
    }
}
