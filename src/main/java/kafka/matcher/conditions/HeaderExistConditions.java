package kafka.matcher.conditions;

import kafka.matcher.Conditions;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Проверка условий наличия заголовков в записях Kafka.
 * Условие используется для валидации наличия определенного заголовка с заданным значением во всех записях.
 */
@RequiredArgsConstructor
public class HeaderExistConditions implements Conditions {

    private final String headerKey;
    private final String headerValue;

    @Override
    public void check(List<ConsumerRecord<String, String>> records) {
        boolean exists = records.stream()
                .anyMatch(record -> headerValue.equals(getHeaderValue(record, headerKey)));
        Assertions.assertThat(exists)
                .as("Проверка наличия записи с заголовком %s и значением %s", headerKey, headerValue)
                .isTrue();
    }

    private String getHeaderValue(ConsumerRecord<String, String> record, String key) {
        Header header = StreamSupport.stream(record.headers().spliterator(), false)
                .filter(h -> h.key().equals(key))
                .findFirst()
                .orElse(null);
        return header != null ? new String(header.value()) : null;
    }

    @Override
    public String toString() {
        return "Условие наличия записи с заголовком: " + headerKey + " и значением: " + headerValue;
    }
}
