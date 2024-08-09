package kafka.matcher;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Класс для валидации списка записей Kafka.
 */
@Slf4j
public class ValidateRecords {

    private final List<ConsumerRecord<String, String>> records;

    /**
     * Конструктор ValidateRecords.
     *
     * @param records список записей Kafka для валидации
     * @throws IllegalArgumentException если список записей null или пуст
     */
    public ValidateRecords(@NonNull List<ConsumerRecord<String, String>> records) {
        Assertions.assertThat(records)
                .as("Список записей не должен быть null или пустым")
                .isNotNull()
                .isNotEmpty();
        this.records = records;
    }

    /**
     * Проверяет, соответствуют ли записи указанному условию.
     *
     * @param condition условие для проверки
     * @return текущий экземпляр ValidateRecords
     * @throws IllegalArgumentException если условие null
     */
    public ValidateRecords shouldHave(@NonNull Condition condition) {
        log.debug("Проверка условия '{}' для {} записей", condition, records.size());
        for (ConsumerRecord<String, String> record : records) {
            log.debug("Проверка записи с ключом '{}'", record.key());
            try {
                condition.check(record);
            } catch (Exception e) {
                log.error("Ошибка при проверке записи с ключом '{}': {}", record.key(), e.getMessage(), e);
                throw e;
            }
        }
        return this;
    }
}
