package kafka.matcher.condition.record;

import kafka.matcher.condition.Conditions;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Проверка, что все записи имеют уникальные ключи.
 */
public class AllKeysUniqueCondition implements Conditions {

    @Override
    public void check(List<ConsumerRecord<String, String>> records) {
        Assertions.assertThat(records)
                .extracting(ConsumerRecord::key)
                .as("Проверка уникальности ключей записей")
                .doesNotHaveDuplicates()
                .withFailMessage("Ключи записей должны быть уникальными, но найдены дубликаты.");
    }

    @Override
    public String toString() {
        return "Условие: все ключи записей должны быть уникальными";
    }
}
