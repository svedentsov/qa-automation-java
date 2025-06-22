package com.svedentsov.kafka.factory;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;

/**
 * Фабрика для создания KafkaProducer.
 * Позволяет заменять реализацию для тестов (in-memory, mock).
 */
public interface ProducerFactory {

    /**
     * Создаёт KafkaProducer для строкового формата.
     *
     * @param topicName имя топика
     * @return KafkaProducer<String, String>
     */
    KafkaProducer<String, String> createStringProducer(String topicName);

    /**
     * Создаёт KafkaProducer для Avro-формата.
     *
     * @param topicName имя топика
     * @return KafkaProducer<String, Object>
     */
    KafkaProducer<String, Object> createAvroProducer(String topicName);

    /**
     * Явно закрывает все ресурсы продюсеров, созданных данной фабрикой.
     * Вызывать при завершении работы.
     */
    void closeAll();
}
