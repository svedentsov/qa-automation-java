package com.svedentsov.kafka.factory;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.Closeable;

/**
 * Интерфейс фабрики для создания типизированных экземпляров {@link KafkaConsumer}.
 * Позволяет абстрагироваться от конкретной реализации и конфигурации консьюмеров.
 */
public interface ConsumerFactory extends Closeable {

    /**
     * Создает новый экземпляр {@link KafkaConsumer} для строковых сообщений.
     *
     * @param topicName Имя топика, для которого создается консьюмер.
     * @return Новый экземпляр {@link KafkaConsumer} со строковыми ключами и значениями.
     */
    KafkaConsumer<String, String> createStringConsumer(String topicName);

    /**
     * Создает новый экземпляр {@link KafkaConsumer} для Avro сообщений.
     *
     * @param topicName Имя топика, для которого создается консьюмер.
     * @return Новый экземпляр {@link KafkaConsumer} со строковыми ключами и Avro значениями.
     */
    KafkaConsumer<String, Object> createAvroConsumer(String topicName); // Object используется, так как GenericRecord - это тоже Object

    /**
     * Освобождает ресурсы, связанные с фабрикой.
     */
    @Override
    void close();
}
