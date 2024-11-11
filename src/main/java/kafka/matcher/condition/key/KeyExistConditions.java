package kafka.matcher.condition.key;

import kafka.matcher.condition.Conditions;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Проверка наличия ключа в записях Kafka.
 * Условие используется для валидации присутствия ключа в списке записей.
 */
@RequiredArgsConstructor
public class KeyExistConditions implements Conditions {

    private final String key;

    @Override
    public void check(List<ConsumerRecord<String, String>> records) {
        boolean recordFound = records.stream()
                .anyMatch(record -> record.key().equals(key));
        Assertions.assertThat(recordFound)
                .as("Проверка, что в записях присутствует хотя бы одна запись с ключом " + key)
                .isTrue();
    }

    @Override
    public String toString() {
        return String.format("Условие, что в записях присутствует хотя бы одна запись с ключом %s", key);
    }
}
