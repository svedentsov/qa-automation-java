package kafka.matcher;

import kafka.matcher.condition.Condition;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Класс для валидации записи Kafka.
 */
@Slf4j
public class ValidateRecord {

    private final ConsumerRecord<String, String> record;

    /**
     * Конструктор ValidateRecord.
     *
     * @param record запись Kafka для валидации
     * @throws IllegalArgumentException если запись null
     */
    public ValidateRecord(@NonNull ConsumerRecord<String, String> record) {
        Assertions.assertThat(record)
                .as("Запись не должна быть null")
                .isNotNull();
        this.record = record;
    }

    /**
     * Проверяет, соответствует ли запись указанному условию.
     *
     * @param condition условие для проверки
     * @return текущий экземпляр ValidateRecord
     * @throws IllegalArgumentException если условие null
     */
    public ValidateRecord shouldHave(@NonNull Condition condition) {
        log.debug("Проверка условия '{}' для записи с ключом '{}'", condition, record.key());
        try {
            condition.check(record);
        } catch (Exception e) {
            log.error("Ошибка при проверке условия '{}' для записи с ключом '{}'", condition, record.key(), e);
            throw e;
        }
        return this;
    }
}
