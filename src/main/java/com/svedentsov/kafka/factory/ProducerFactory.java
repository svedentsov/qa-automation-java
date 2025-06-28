package com.svedentsov.kafka.factory;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;

/**
 * Интерфейс фабрики для создания экземпляров {@link KafkaProducer}.
 * Абстрагирует процесс создания продюсеров, что позволяет легко подменять
 * реализации в тестах (например, на mock-объекты или in-memory продюсеры).
 */
public interface ProducerFactory {

    /**
     * Создаёт или получает из кэша {@link KafkaProducer} для отправки строковых сообщений.
     *
     * @param topicName имя топика, не может быть пустым.
     * @return настроенный экземпляр {@link KafkaProducer<String, String>}.
     */
    KafkaProducer<String, String> createStringProducer(String topicName);

    /**
     * Создаёт или получает из кэша {@link KafkaProducer} для отправки сообщений в формате Avro.
     *
     * @param topicName имя топика, не может быть пустым.
     * @return настроенный экземпляр {@link KafkaProducer<String, GenericRecord>}.
     */
    KafkaProducer<String, GenericRecord> createAvroProducer(String topicName);

    /**
     * Закрывает все созданные и кэшированные этой фабрикой продюсеры.
     * Этот метод необходимо вызывать при завершении работы приложения для корректного освобождения ресурсов.
     */
    void closeAll();
}
