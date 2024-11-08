package kafka.matcher.condition;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Интерфейс Condition для проверки записи Kafka.
 */
public interface Condition {

    /**
     * Метод для проверки записи.
     *
     * @param record запись Kafka для проверки
     */
    void check(ConsumerRecord<String, String> record);
}
