package kafka.matcher.condition;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.assertj.core.api.Assertions;

/**
 * Проверка, что запись содержит заголовок с заданным ключом.
 */
@RequiredArgsConstructor
public class HeaderExistsCondition implements Condition {

    private final String headerKey;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        Headers headers = record.headers();
        Header header = headers.lastHeader(headerKey);
        Assertions.assertThat(header)
                .as("Заголовок с ключом '%s' должен присутствовать", headerKey)
                .isNotNull();
    }

    @Override
    public String toString() {
        return String.format("Условие: заголовок с ключом '%s' должен присутствовать", headerKey);
    }
}
