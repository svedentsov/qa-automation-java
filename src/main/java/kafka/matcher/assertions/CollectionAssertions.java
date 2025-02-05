package kafka.matcher.assertions;

import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Утилитный класс для создания различных условий, применяемых к списку записей Kafka.
 */
@UtilityClass
public class CollectionAssertions {

    /**
     * Функциональный интерфейс для условий проверки списка записей.
     */
    @FunctionalInterface
    public interface CollectionCondition {
        /**
         * Проверяет условие для списка записей.
         *
         * @param records список записей для проверки
         */
        void check(List<ConsumerRecord<String, String>> records);
    }

    /**
     * Проверяет, что значение  является массивом.
     */
    public static CollectionCondition isArray() {
        return records -> Assertions.assertThat(records)
                .as("Значение должно быть массивом")
                .isInstanceOf(List.class);
    }

    /**
     * Проверяет, что в списке есть хотя бы одна запись.
     */
    public static CollectionCondition exists() {
        return records -> Assertions.assertThat(records)
                .as("Должна быть хотя бы одна запись")
                .isNotEmpty();
    }

    /**
     * Проверяет, что количество записей равно указанному значению.
     */
    public static CollectionCondition countEqual(int count) {
        return records -> Assertions.assertThat(records)
                .as("Количество записей должно быть равно %d", count)
                .hasSize(count);
    }

    /**
     * Проверяет, что количество записей больше указанного значения.
     */
    public static CollectionCondition countGreater(int count) {
        return records -> Assertions.assertThat(records)
                .as("Количество записей должно быть больше %d", count)
                .hasSizeGreaterThan(count);
    }

    /**
     * Проверяет, что количество записей меньше указанного значения.
     */
    public static CollectionCondition countLess(int count) {
        return records -> Assertions.assertThat(records)
                .as("Количество записей должно быть меньше %d", count)
                .hasSizeLessThan(count);
    }

    /**
     * Проверяет, что все ключи записей уникальны.
     */
    public static CollectionCondition allKeysUnique() {
        return records -> Assertions.assertThat(records)
                .extracting(ConsumerRecord::key)
                .as("Все ключи записей должны быть уникальными")
                .doesNotHaveDuplicates();
    }

    /**
     * Проверяет, что все значения записей уникальны.
     */
    public static CollectionCondition allValuesUnique() {
        return records -> Assertions.assertThat(records)
                .extracting(ConsumerRecord::value)
                .as("Все значения записей должны быть уникальными")
                .doesNotHaveDuplicates();
    }

    /**
     * Проверяет, что записи упорядочены по заданному полю.
     *
     * @param fieldExtractor функция извлечения поля
     * @param ascending      true, если по возрастанию, false – по убыванию
     * @param <T>            тип сравниваемого значения
     */
    public static <T extends Comparable<T>> CollectionCondition recordsOrdered(Function<ConsumerRecord<String, String>, T> fieldExtractor, boolean ascending) {
        return records -> {
            List<T> extracted = records.stream()
                    .map(fieldExtractor)
                    .collect(Collectors.toList());

            Comparator<T> comparator = ascending ? Comparator.naturalOrder() : Comparator.reverseOrder();
            String orderDescription = ascending ? "возрастанию" : "убыванию";

            Assertions.assertThat(extracted)
                    .as("Записи должны быть упорядочены по " + orderDescription)
                    .isSortedAccordingTo(comparator);
        };
    }

    /**
     * Проверяет, что существует запись с указанным ключом.
     *
     * @param key ключ для поиска
     */
    public static CollectionCondition keysExists(String key) {
        return records -> {
            boolean found = records.stream().anyMatch(r -> Objects.equals(r.key(), key));
            Assertions.assertThat(found)
                    .as("Должна существовать запись с ключом %s", key)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что ни в одной записи не встречается указанный ключ.
     *
     * @param key ключ для проверки отсутствия
     */
    public static CollectionCondition keysNotExists(String key) {
        return records -> {
            boolean found = records.stream().anyMatch(r -> Objects.equals(r.key(), key));
            Assertions.assertThat(found)
                    .as("Не должно существовать записи с ключом %s", key)
                    .isFalse();
        };
    }

    /**
     * Проверяет, что существует запись с указанным значением.
     *
     * @param value значение для поиска
     */
    public static CollectionCondition valuesExists(String value) {
        return records -> {
            boolean found = records.stream().anyMatch(r -> Objects.equals(r.value(), value));
            Assertions.assertThat(found)
                    .as("Должна существовать запись со значением %s", value)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что ни в одной записи не встречается указанное значение.
     *
     * @param value значение для проверки отсутствия
     */
    public static CollectionCondition valuesNotExists(String value) {
        return records -> {
            boolean found = records.stream().anyMatch(r -> Objects.equals(r.value(), value));
            Assertions.assertThat(found)
                    .as("Не должно существовать записи со значением %s", value)
                    .isFalse();
        };
    }

    /**
     * Проверяет, что хотя бы один ключ содержит указанную подстроку.
     *
     * @param substring подстрока для поиска
     * @return условие для одной записи
     */
    public static CollectionCondition anyKeyContains(String substring) {
        return records -> {
            boolean found = records.stream()
                    .map(ConsumerRecord::key)
                    .filter(Objects::nonNull)
                    .anyMatch(k -> k.contains(substring));
            Assertions.assertThat(found)
                    .as("Хотя бы один ключ должен содержать %s", substring)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что хотя бы одно значение содержит указанную подстроку.
     *
     * @param substring подстрока для поиска
     * @return условие для одной записи
     */
    public static CollectionCondition anyValueContains(String substring) {
        return records -> {
            boolean found = records.stream()
                    .map(ConsumerRecord::value)
                    .filter(Objects::nonNull)
                    .anyMatch(v -> v.contains(substring));
            Assertions.assertThat(found)
                    .as("Хотя бы одно значение должно содержать %s", substring)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что все ключи соответствуют заданному регулярному выражению.
     *
     * @param regex регулярное выражение
     */
    public static CollectionCondition allKeysMatchRegex(String regex) {
        Pattern pattern = Pattern.compile(regex);
        return records -> {
            boolean allMatch = records.stream()
                    .map(ConsumerRecord::key)
                    .filter(Objects::nonNull)
                    .allMatch(k -> pattern.matcher(k).matches());
            Assertions.assertThat(allMatch)
                    .as("Все ключи должны соответствовать рег. выражению %s", regex)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что все значения соответствуют заданному регулярному выражению.
     *
     * @param regex регулярное выражение
     */
    public static CollectionCondition allValuesMatchRegex(String regex) {
        Pattern pattern = Pattern.compile(regex);
        return records -> {
            boolean allMatch = records.stream()
                    .map(ConsumerRecord::value)
                    .filter(Objects::nonNull)
                    .allMatch(v -> pattern.matcher(v).matches());
            Assertions.assertThat(allMatch)
                    .as("Все значения должны соответствовать рег. выражению %s", regex)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что все записи принадлежат указанной партиции.
     *
     * @param partition номер партиции
     */
    public static CollectionCondition partitionsAllEqual(int partition) {
        return records -> {
            boolean allEqual = records.stream().allMatch(r -> r.partition() == partition);
            Assertions.assertThat(allEqual)
                    .as("Все записи должны принадлежать партиции %d", partition)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что все записи имеют партицию в указанном диапазоне.
     *
     * @param startInclusive начало диапазона (включительно)
     * @param endInclusive   конец диапазона (включительно)
     */
    public static CollectionCondition partitionsAllInRange(int startInclusive, int endInclusive) {
        return records -> {
            boolean allInRange = records.stream()
                    .allMatch(r -> r.partition() >= startInclusive && r.partition() <= endInclusive);
            Assertions.assertThat(allInRange)
                    .as("Все партиции должны быть в диапазоне [%d, %d]", startInclusive, endInclusive)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что все смещения (offset) больше указанного значения.
     *
     * @param offset пороговое значение
     */
    public static CollectionCondition offsetsAllGreaterThan(long offset) {
        return records -> {
            boolean allGreater = records.stream().allMatch(r -> r.offset() > offset);
            Assertions.assertThat(allGreater)
                    .as("Все смещения должны быть больше %d", offset)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что все смещения (offset) меньше указанного значения.
     *
     * @param offset пороговое значение
     */
    public static CollectionCondition offsetsAllLessThan(long offset) {
        return records -> {
            boolean allLess = records.stream().allMatch(r -> r.offset() < offset);
            Assertions.assertThat(allLess)
                    .as("Все смещения должны быть меньше %d", offset)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что все смещения (offset) находятся в указанном диапазоне.
     *
     * @param startInclusive начало диапазона (включительно)
     * @param endInclusive   конец диапазона (включительно)
     */
    public static CollectionCondition offsetsAllInRange(long startInclusive, long endInclusive) {
        return records -> {
            boolean allInRange = records.stream()
                    .allMatch(r -> r.offset() >= startInclusive && r.offset() <= endInclusive);
            Assertions.assertThat(allInRange)
                    .as("Все смещения должны быть в диапазоне [%d, %d]", startInclusive, endInclusive)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что все записи принадлежат указанному топику.
     *
     * @param topic имя топика
     */
    public static CollectionCondition topicsAllEqual(String topic) {
        return records -> {
            boolean allEqual = records.stream().allMatch(r -> Objects.equals(r.topic(), topic));
            Assertions.assertThat(allEqual)
                    .as("Все записи должны принадлежать топику %s", topic)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что в списке записей все ключи не пусты.
     */
    public static CollectionCondition allKeysNotEmpty() {
        return records -> {
            boolean allNonEmpty = records.stream()
                    .map(ConsumerRecord::key)
                    .filter(Objects::nonNull)
                    .allMatch(k -> !k.isEmpty());
            Assertions.assertThat(allNonEmpty)
                    .as("Все ключи должны быть непустыми")
                    .isTrue();
        };
    }

    /**
     * Проверяет, что в списке записей все значения не пусты.
     */
    public static CollectionCondition allValuesNotEmpty() {
        return records -> {
            boolean allNonEmpty = records.stream()
                    .map(ConsumerRecord::value)
                    .filter(Objects::nonNull)
                    .allMatch(v -> !v.isEmpty());
            Assertions.assertThat(allNonEmpty)
                    .as("Все значения должны быть непустыми")
                    .isTrue();
        };
    }
}
