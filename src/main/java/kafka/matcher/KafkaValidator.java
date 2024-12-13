package kafka.matcher;

import kafka.exception.ValidationException;
import kafka.matcher.condition.Condition;
import kafka.matcher.condition.Conditions;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.Collections;
import java.util.List;

/**
 * Класс для валидации Kafka записей с помощью DSL условий.
 * Позволяет последовательно накладывать условия на одну или несколько записей
 * и выбрасывать {@link ValidationException} в случае несоответствия условиям.
 */
@Slf4j
public class KafkaValidator {

    private final List<ConsumerRecord<String, String>> records;

    /**
     * Конструктор для проверки списка записей.
     *
     * @param records список записей для проверки
     * @throws ValidationException если список пуст или null
     */
    public KafkaValidator(@NonNull List<ConsumerRecord<String, String>> records) {
        validateRecords(records);
        this.records = records;
    }

    /**
     * Конструктор для проверки одной записи.
     *
     * @param record одна запись для проверки
     * @throws ValidationException если запись null
     */
    public KafkaValidator(@NonNull ConsumerRecord<String, String> record) {
        this(Collections.singletonList(record));
    }

    /**
     * Проверяет одну или несколько записей на соответствие одиночному условию.
     *
     * @param condition условие для проверки
     * @return текущий {@link KafkaValidator}
     * @throws ValidationException если условие не выполнено
     */
    public KafkaValidator shouldHave(@NonNull Condition condition) {
        log.debug("Проверка условия '{}' для {} записей", condition, records.size());
        for (ConsumerRecord<String, String> record : records) {
            log.debug("Проверка записи с ключом '{}'", record.key());
            executeCheck(() -> condition.check(record), condition, record.key());
        }
        return this;
    }

    /**
     * Проверяет список записей на соответствие условиям для набора записей.
     *
     * @param conditions условия для проверки списка записей
     * @return текущий {@link KafkaValidator}
     * @throws ValidationException если условие не выполнено
     */
    public KafkaValidator shouldHave(@NonNull Conditions conditions) {
        log.debug("Проверка условий '{}' для {} записей", conditions, records.size());
        executeCheck(() -> conditions.check(records), conditions, null);
        return this;
    }

    /**
     * Выполняет проверку и обрабатывает исключения.
     *
     * @param check     лямбда-выражение для выполнения проверки
     * @param condition условие или набор условий
     * @param recordKey ключ записи (может быть null)
     * @throws ValidationException если условие не выполнено или произошла ошибка при проверке
     */
    private void executeCheck(Runnable check, Object condition, String recordKey) {
        try {
            check.run();
        } catch (AssertionError e) {
            String errorMessage = String.format("Условие %s не выполнено для записи с ключом %s: %s", condition, recordKey, e.getMessage());
            log.error(errorMessage);
            throw new ValidationException(errorMessage, e);
        } catch (Exception e) {
            String errorMessage = String.format("Ошибка при проверке условия %s для записи с ключом %s: %s", condition, recordKey, e.getMessage());
            log.error(errorMessage, e);
            throw new ValidationException(errorMessage, e);
        }
    }

    /**
     * Проверяет, что список записей не является null или пустым.
     *
     * @param records Список записей для проверки.
     */
    private void validateRecords(@NonNull List<ConsumerRecord<String, String>> records) {
        Assertions.assertThat(records)
                .as("Список записей не должен быть null или пустым")
                .isNotNull()
                .isNotEmpty();
    }
}
