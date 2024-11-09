package kafka.matcher.condition;

import kafka.matcher.Conditions;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.function.Function;

/**
 * Проверка, что записи упорядочены по заданному полю.
 */
@RequiredArgsConstructor
public class RecordsOrderedCondition<T extends Comparable<T>> implements Conditions {

    private final Function<ConsumerRecord<String, String>, T> fieldExtractor;
    private final boolean ascending;

    @Override
    public void check(List<ConsumerRecord<String, String>> records) {
        for (int i = 1; i < records.size(); i++) {
            T previousValue = fieldExtractor.apply(records.get(i - 1));
            T currentValue = fieldExtractor.apply(records.get(i));
            int comparison = previousValue.compareTo(currentValue);
            if (ascending) {
                Assertions.assertThat(comparison)
                        .as("Записи не упорядочены по возрастанию по полю")
                        .isLessThanOrEqualTo(0);
            } else {
                Assertions.assertThat(comparison)
                        .as("Записи не упорядочены по убыванию по полю")
                        .isGreaterThanOrEqualTo(0);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("Условие: записи должны быть упорядочены по %s", ascending ? "возрастанию" : "убыванию");
    }
}
