package kafka.matcher.condition.header;

import kafka.matcher.condition.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка, что запись Kafka содержит указанный ключ заголовка.
 */
@RequiredArgsConstructor
public class HeaderKeyExistCondition implements Condition {

    private final String headerKey;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        Assertions.assertThat(record.headers().lastHeader(headerKey))
                .as("Заголовок с ключом '%s' не найден", headerKey)
                .isNotNull();
    }

    @Override
    public String toString() {
        return String.format("Заголовок должен содержать ключ: %s", headerKey);
    }
}
