package kafka.matcher.condition;

import kafka.matcher.Conditions;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Проверка условия наличия записей в списке.
 * Условие используется для валидации, что в списке записей имеется хотя бы одна запись.
 */
public class RecordExistConditions implements Conditions {

    @Override
    public void check(List<ConsumerRecord<String, String>> records) {
        Assertions.assertThat(records)
                .as("Проверка наличия хотя бы одной записи")
                .isNotEmpty();
    }

    @Override
    public String toString() {
        return "Условие наличия хотя бы одной записи";
    }
}
