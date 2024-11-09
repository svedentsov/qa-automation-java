package kafka.matcher;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

/**
 * Интерфейс Conditions для проверки списка записей Kafka.
 */
public interface Conditions {

    /**
     * Метод для проверки списка записей.
     *
     * @param records список записей Kafka для проверки
     */
    void check(List<ConsumerRecord<String, String>> records);
}
