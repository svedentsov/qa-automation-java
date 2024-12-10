package kafka.matcher.condition;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Базовый функциональный интерфейс для условий проверки одной записи.
 */
@FunctionalInterface
public interface Condition {
    /**
     * Проверяет условие для одной записи.
     *
     * @param record запись для проверки
     */
    void check(ConsumerRecord<String, String> record);
}
