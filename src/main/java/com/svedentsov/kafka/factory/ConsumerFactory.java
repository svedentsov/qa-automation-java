package com.svedentsov.kafka.factory;

import org.apache.kafka.clients.consumer.KafkaConsumer;

/**
 * Фабрика для создания KafkaConsumer.
 * Позволяет заменять реализацию для тестов.
 */
public interface ConsumerFactory {

    /**
     * Создаёт KafkaConsumer для строкового формата.
     *
     * @param topicName имя топика
     * @return KafkaConsumer<String, String>
     */
    KafkaConsumer<String, String> createStringConsumer(String topicName);

    /**
     * Создаёт KafkaConsumer для Avro-формата.
     *
     * @param topicName имя топика
     * @return KafkaConsumer<String, Object>
     */
    KafkaConsumer<String, Object> createAvroConsumer(String topicName);

    /**
     * Явно закрывает все ресурсы консьюмеров, созданных данной фабрикой.
     * Вызывать при завершении работы.
     */
    void closeAll();
}
