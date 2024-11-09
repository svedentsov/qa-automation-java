package kafka.matcher.condition;

import kafka.matcher.Conditions;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Проверка, что все значения записей уникальны.
 */
public class AllValuesUniqueCondition implements Conditions {

    @Override
    public void check(List<ConsumerRecord<String, String>> records) {
        Set<String> values = new HashSet<>();
        for (ConsumerRecord<String, String> record : records) {
            if (!values.add(record.value())) {
                Assertions.fail("Значение '%s' не уникально среди записей", record.value());
            }
        }
    }

    @Override
    public String toString() {
        return "Условие: все значения записей должны быть уникальными";
    }
}
