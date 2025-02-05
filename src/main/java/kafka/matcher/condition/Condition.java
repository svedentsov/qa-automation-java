package kafka.matcher.condition;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Функциональный интерфейс для условия проверки одной Kafka записи.
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
