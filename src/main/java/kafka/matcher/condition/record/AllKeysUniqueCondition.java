package kafka.matcher.condition.record;

import kafka.matcher.condition.Conditions;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Проверка, что все записи имеют уникальные ключи.
 */
public class AllKeysUniqueCondition implements Conditions {

    @Override
    public void check(List<ConsumerRecord<String, String>> records) {
        Set<String> keys = new HashSet<>();
        for (ConsumerRecord<String, String> record : records) {
            if (!keys.add(record.key())) {
                Assertions.fail("Ключ '%s' не уникален среди записей", record.key());
            }
        }
    }

    @Override
    public String toString() {
        return "Условие: все ключи записей должны быть уникальными";
    }
}
