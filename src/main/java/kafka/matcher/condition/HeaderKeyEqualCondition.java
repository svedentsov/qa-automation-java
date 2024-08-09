package kafka.matcher.condition;

import kafka.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка условия соответствия ключа заголовка записи Kafka.
 * Условие используется для валидации ключа заголовка записи.
 */
@RequiredArgsConstructor
public class HeaderKeyEqualCondition implements Condition {

    private final String expectedKey;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        boolean keyExists = false;
        for (var header : record.headers()) {
            if (header.key().equals(expectedKey)) {
                keyExists = true;
                break;
            }
        }
        Assertions.assertThat(keyExists)
                .as("Проверка ключа заголовка")
                .isTrue();
    }

    @Override
    public String toString() {
        return "Условие ключа заголовка Kafka с ожидаемым значением: " + expectedKey;
    }
}
