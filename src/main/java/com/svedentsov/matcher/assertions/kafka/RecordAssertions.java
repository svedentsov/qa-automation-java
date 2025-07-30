package com.svedentsov.matcher.assertions.kafka;

import com.svedentsov.matcher.Condition;
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
 * Утилитный класс для создания различных условий (проверок), применяемых к списку записей Kafka.
 * Предоставляет статические методы-фабрики для удобного формирования цепочек проверок.
 */
@UtilityClass
public class RecordAssertions {

    /**
     * Функциональный интерфейс для условий, проверяющих список записей Kafka целиком.
     * Является частным случаем общего интерфейса {@link Condition}.
     */
    @FunctionalInterface
    public interface RecordCondition extends Condition<List<ConsumerRecord<String, String>>> {
    }

    /**
     * Проверяет, что в списке есть хотя бы одна запись.
     *
     * @return {@link RecordCondition} для проверки непустого списка.
     */
    public static RecordCondition recordExists() {
        return records -> Assertions.assertThat(records)
                .as("Ожидалось, что в топике будет хотя бы одна запись, но список пуст")
                .isNotEmpty();
    }

    /**
     * Проверяет, что количество записей в списке равно указанному значению.
     *
     * @param count Ожидаемое количество записей.
     * @return {@link RecordCondition} для проверки точного количества записей.
     */
    public static RecordCondition recordCountEqual(int count) {
        return records -> Assertions.assertThat(records)
                .as("Ожидалось, что количество записей будет равно %d", count)
                .hasSize(count);
    }

    /**
     * Проверяет, что количество записей в списке больше указанного значения.
     *
     * @param count Значение, которое количество записей должно превышать.
     * @return {@link RecordCondition} для проверки, что записей больше, чем {@code count}.
     */
    public static RecordCondition recordCountGreater(int count) {
        return records -> Assertions.assertThat(records)
                .as("Ожидалось, что количество записей будет больше %d", count)
                .hasSizeGreaterThan(count);
    }

    /**
     * Проверяет, что количество записей в списке меньше указанного значения.
     *
     * @param count Значение, меньше которого должно быть количество записей.
     * @return {@link RecordCondition} для проверки, что записей меньше, чем {@code count}.
     */
    public static RecordCondition recordCountLess(int count) {
        return records -> Assertions.assertThat(records)
                .as("Ожидалось, что количество записей будет меньше %d", count)
                .hasSizeLessThan(count);
    }

    /**
     * Проверяет, что все ключи в списке записей уникальны.
     *
     * @return {@link RecordCondition} для проверки уникальности ключей.
     */
    public static RecordCondition recordAllKeysUnique() {
        return records -> Assertions.assertThat(records)
                .as("Ожидалось, что все ключи записей будут уникальными")
                .extracting(ConsumerRecord::key)
                .doesNotHaveDuplicates();
    }

    /**
     * Проверяет, что все значения в списке записей уникальны.
     *
     * @return {@link RecordCondition} для проверки уникальности значений.
     */
    public static RecordCondition recordAllValuesUnique() {
        return records -> Assertions.assertThat(records)
                .as("Ожидалось, что все значения записей будут уникальными")
                .extracting(ConsumerRecord::value)
                .doesNotHaveDuplicates();
    }

    /**
     * Проверяет, что записи в списке упорядочены по извлекаемому полю.
     *
     * @param fieldExtractor Функция для извлечения поля из {@link ConsumerRecord}, по которому будет производиться сортировка.
     * @param ascending      {@code true} для проверки порядка по возрастанию, {@code false} — по убыванию.
     * @param <T>            Тип извлекаемого поля, должен реализовывать {@link Comparable}.
     * @return {@link RecordCondition} для проверки порядка записей.
     */
    public static <T extends Comparable<T>> RecordCondition recordRecordsOrdered(Function<ConsumerRecord<String, String>, T> fieldExtractor, boolean ascending) {
        return records -> {
            List<T> extracted = records.stream()
                    .map(fieldExtractor)
                    .collect(Collectors.toList());

            Comparator<T> comparator = ascending ? Comparator.naturalOrder() : Comparator.reverseOrder();
            String orderDescription = ascending ? "возрастанию" : "убыванию";

            Assertions.assertThat(extracted)
                    .as("Ожидалось, что записи будут упорядочены по %s", orderDescription)
                    .isSortedAccordingTo(comparator);
        };
    }

    /**
     * Проверяет, что в списке существует запись с указанным ключом.
     *
     * @param key Ключ для поиска.
     * @return {@link RecordCondition} для проверки наличия записи с ключом.
     */
    public static RecordCondition recordKeysExists(String key) {
        return records -> Assertions.assertThat(records)
                .as("Ожидалось наличие записи с ключом '%s'", key)
                .extracting(ConsumerRecord::key)
                .contains(key);
    }

    /**
     * Проверяет, что в списке отсутствует запись с указанным ключом.
     *
     * @param key Ключ, которого не должно быть в списке.
     * @return {@link RecordCondition} для проверки отсутствия записи с ключом.
     */
    public static RecordCondition recordKeysNotExists(String key) {
        return records -> Assertions.assertThat(records)
                .as("Ожидалось отсутствие записи с ключом '%s'", key)
                .extracting(ConsumerRecord::key)
                .doesNotContain(key);
    }

    /**
     * Проверяет, что в списке существует запись с указанным значением.
     *
     * @param value Значение для поиска.
     * @return {@link RecordCondition} для проверки наличия записи со значением.
     */
    public static RecordCondition recordValuesExists(String value) {
        return records -> Assertions.assertThat(records)
                .as("Ожидалось наличие записи со значением '%s'", value)
                .extracting(ConsumerRecord::value)
                .contains(value);
    }

    /**
     * Проверяет, что в списке отсутствует запись с указанным значением.
     *
     * @param value Значение, которого не должно быть в списке.
     * @return {@link RecordCondition} для проверки отсутствия записи со значением.
     */
    public static RecordCondition recordValuesNotExists(String value) {
        return records -> Assertions.assertThat(records)
                .as("Ожидалось отсутствие записи со значением '%s'", value)
                .extracting(ConsumerRecord::value)
                .doesNotContain(value);
    }

    /**
     * Проверяет, что хотя бы один ключ в списке записей содержит указанную подстроку.
     *
     * @param substring Подстрока для поиска в ключах.
     * @return {@link RecordCondition} для проверки содержания подстроки в ключах.
     */
    public static RecordCondition recordAnyKeyContains(String substring) {
        return records -> Assertions.assertThat(records)
                .as("Ожидалось, что хотя бы один ключ содержит '%s'", substring)
                .anyMatch(r -> r.key() != null && r.key().contains(substring));
    }

    /**
     * Проверяет, что хотя бы одно значение в списке записей содержит указанную подстроку.
     *
     * @param substring Подстрока для поиска в значениях.
     * @return {@link RecordCondition} для проверки содержания подстроки в значениях.
     */
    public static RecordCondition recordAnyValueContains(String substring) {
        return records -> Assertions.assertThat(records)
                .as("Ожидалось, что хотя бы одно значение содержит '%s'", substring)
                .anyMatch(r -> r.value() != null && r.value().contains(substring));
    }

    /**
     * Проверяет, что все ключи записей в списке соответствуют заданному регулярному выражению.
     *
     * @param regex Регулярное выражение для проверки ключей.
     * @return {@link RecordCondition} для проверки соответствия ключей регулярному выражению.
     */
    public static RecordCondition recordAllKeysMatchRegex(String regex) {
        Pattern pattern = Pattern.compile(regex);
        return records -> Assertions.assertThat(records)
                .as("Ожидалось, что все ключи соответствуют регулярному выражению '%s'", regex)
                .allMatch(r -> r.key() != null && pattern.matcher(r.key()).matches());
    }

    /**
     * Проверяет, что все значения записей в списке соответствуют заданному регулярному выражению.
     *
     * @param regex Регулярное выражение для проверки значений.
     * @return {@link RecordCondition} для проверки соответствия значений регулярному выражению.
     */
    public static RecordCondition recordAllValuesMatchRegex(String regex) {
        Pattern pattern = Pattern.compile(regex);
        return records -> Assertions.assertThat(records)
                .as("Ожидалось, что все значения соответствуют регулярному выражению '%s'", regex)
                .allMatch(r -> r.value() != null && pattern.matcher(r.value()).matches());
    }

    /**
     * Проверяет, что все записи в списке принадлежат указанной партиции.
     *
     * @param partition Номер партиции.
     * @return {@link RecordCondition} для проверки номера партиции.
     */
    public static RecordCondition recordPartitionsAllEqual(int partition) {
        return records -> Assertions.assertThat(records)
                .as("Ожидалось, что все записи принадлежат партиции %d", partition)
                .allMatch(r -> r.partition() == partition);
    }

    /**
     * Проверяет, что номера партиций всех записей находятся в указанном диапазоне (включительно).
     *
     * @param startInclusive Начало диапазона.
     * @param endInclusive   Конец диапазона.
     * @return {@link RecordCondition} для проверки диапазона партиций.
     */
    public static RecordCondition recordPartitionsAllInRange(int startInclusive, int endInclusive) {
        return records -> Assertions.assertThat(records)
                .as("Ожидалось, что все партиции находятся в диапазоне [%d, %d]", startInclusive, endInclusive)
                .allSatisfy(r -> Assertions.assertThat(r.partition()).isBetween(startInclusive, endInclusive));
    }

    /**
     * Проверяет, что смещения (offset) всех записей больше указанного значения.
     *
     * @param offset Пороговое значение смещения.
     * @return {@link RecordCondition} для проверки смещений.
     */
    public static RecordCondition recordOffsetsAllGreaterThan(long offset) {
        return records -> Assertions.assertThat(records)
                .as("Ожидалось, что все смещения (offset) будут больше %d", offset)
                .allMatch(r -> r.offset() > offset);
    }

    /**
     * Проверяет, что смещения (offset) всех записей меньше указанного значения.
     *
     * @param offset Пороговое значение смещения.
     * @return {@link RecordCondition} для проверки смещений.
     */
    public static RecordCondition recordOffsetsAllLessThan(long offset) {
        return records -> Assertions.assertThat(records)
                .as("Ожидалось, что все смещения (offset) будут меньше %d", offset)
                .allMatch(r -> r.offset() < offset);
    }

    /**
     * Проверяет, что смещения (offset) всех записей находятся в указанном диапазоне (включительно).
     *
     * @param startInclusive Начало диапазона.
     * @param endInclusive   Конец диапазона.
     * @return {@link RecordCondition} для проверки диапазона смещений.
     */
    public static RecordCondition recordOffsetsAllInRange(long startInclusive, long endInclusive) {
        return records -> Assertions.assertThat(records)
                .as("Ожидалось, что все смещения находятся в диапазоне [%d, %d]", startInclusive, endInclusive)
                .allSatisfy(r -> Assertions.assertThat(r.offset()).isBetween(startInclusive, endInclusive));
    }

    /**
     * Проверяет, что все записи в списке принадлежат указанному топику.
     *
     * @param topic Имя топика.
     * @return {@link RecordCondition} для проверки имени топика.
     */
    public static RecordCondition recordTopicsAllEqual(String topic) {
        return records -> Assertions.assertThat(records)
                .as("Ожидалось, что все записи принадлежат топику '%s'", topic)
                .allMatch(r -> Objects.equals(r.topic(), topic));
    }

    /**
     * Проверяет, что все ключи в списке записей не являются пустыми строками.
     * Записи с ключом {@code null} игнорируются.
     *
     * @return {@link RecordCondition} для проверки непустых ключей.
     */
    public static RecordCondition recordAllKeysNotEmpty() {
        return records -> Assertions.assertThat(records)
                .as("Ожидалось, что все ключи будут непустыми")
                .filteredOn(r -> r.key() != null)
                .allMatch(r -> !r.key().isEmpty());
    }

    /**
     * Проверяет, что все значения в списке записей не являются пустыми строками.
     * Записи со значением {@code null} игнорируются.
     *
     * @return {@link RecordCondition} для проверки непустых значений.
     */
    public static RecordCondition recordAllValuesNotEmpty() {
        return records -> Assertions.assertThat(records)
                .as("Ожидалось, что все значения будут непустыми")
                .filteredOn(r -> r.value() != null)
                .allMatch(r -> !r.value().isEmpty());
    }
}
