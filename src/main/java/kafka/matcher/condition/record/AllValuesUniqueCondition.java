package kafka.matcher.condition.record;

import kafka.matcher.condition.Conditions;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Проверка, что все значения записей уникальны.
 */
public class AllValuesUniqueCondition implements Conditions {

    @Override
    public void check(List<ConsumerRecord<String, String>> records) {
        Assertions.assertThat(records)
                .extracting(ConsumerRecord::value)
                .as("Проверка уникальности значений записей")
                .doesNotHaveDuplicates()
                .withFailMessage("Значения записей должны быть уникальными, но найдены дубликаты.");
    }

    @Override
    public String toString() {
        return "Условие: все значения записей должны быть уникальными";
    }
}
