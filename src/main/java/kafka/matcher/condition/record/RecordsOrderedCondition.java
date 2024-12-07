package kafka.matcher.condition.record;

import kafka.matcher.condition.Conditions;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Проверка, что записи упорядочены по заданному полю.
 *
 * @param <T> тип поля, по которому осуществляется сортировка. Должен реализовывать Comparable.
 */
@RequiredArgsConstructor
public class RecordsOrderedCondition<T extends Comparable<T>> implements Conditions {

    private final Function<ConsumerRecord<String, String>, T> fieldExtractor;
    private final boolean ascending;

    @Override
    public void check(List<ConsumerRecord<String, String>> records) {
        // Извлекаем значения поля из всех записей
        List<T> extractedFields = records.stream()
                .map(fieldExtractor)
                .collect(Collectors.toList());

        // Определяем компаратор в зависимости от направления сортировки
        Comparator<T> comparator = ascending ? Comparator.naturalOrder() : Comparator.reverseOrder();

        // Формируем описание проверки
        String orderDescription = ascending ? "возрастанию" : "убыванию";

        // Выполняем проверку с использованием AssertJ
        Assertions.assertThat(extractedFields)
                .as("Проверка, что записи упорядочены по %s", orderDescription)
                .isSortedAccordingTo(comparator);
    }

    @Override
    public String toString() {
        String orderDescription = ascending ? "возрастанию" : "убыванию";
        return String.format("Условие: записи должны быть упорядочены по %s", orderDescription);
    }
}
