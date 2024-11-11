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
 * Класс для валидации записей Kafka.
 * Поддерживает проверку условий для списка записей и одной записи.
 */
@Slf4j
public class KafkaValidator {

    private final List<ConsumerRecord<String, String>> records;

    /**
     * Конструктор для списка записей.
     *
     * @param records Список записей Kafka для валидации.
     * @throws IllegalArgumentException если список null или пуст.
     */
    public KafkaValidator(@NonNull List<ConsumerRecord<String, String>> records) {
        validateRecords(records);
        this.records = records;
    }

    /**
     * Конструктор для одной записи.
     *
     * @param record Одна запись Kafka для валидации.
     */
    public KafkaValidator(@NonNull ConsumerRecord<String, String> record) {
        this(Collections.singletonList(record));
    }

    /**
     * Проверяет, что каждая запись удовлетворяет условию.
     *
     * @param condition Условие для проверки записи.
     * @return Экземпляр KafkaValidator для дальнейших проверок.
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
     * Проверяет, что список записей удовлетворяет набору условий.
     *
     * @param conditions Набор условий для проверки.
     * @return Экземпляр KafkaValidator для дальнейших проверок.
     */
    public KafkaValidator shouldHave(@NonNull Conditions conditions) {
        log.debug("Проверка условий '{}' для {} записей", conditions, records.size());
        executeCheck(() -> conditions.check(records), conditions, null);
        return this;
    }

    /**
     * Выполняет проверку и обрабатывает исключения.
     *
     * @param check     Лямбда-выражение для выполнения проверки.
     * @param condition Условие или набор условий.
     * @param recordKey Ключ записи (если применимо).
     */
    private void executeCheck(Runnable check, Object condition, String recordKey) {
        try {
            check.run();
        } catch (AssertionError e) {
            String errorMessage = String.format("Условие '%s' не выполнено для записи с ключом '%s': '%s'", condition, recordKey, e.getMessage());
            log.error(errorMessage);
            throw new ValidationException(errorMessage, e);
        } catch (Exception e) {
            String errorMessage = String.format("Ошибка при проверке условия '%s' для записи с ключом '%s': '%s'", condition, recordKey, e.getMessage());
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
