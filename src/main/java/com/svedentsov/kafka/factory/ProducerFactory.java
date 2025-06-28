package com.svedentsov.kafka.factory;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;

/**
 * Интерфейс фабрики для создания экземпляров {@link KafkaProducer}.
 * Абстрагирует процесс создания продюсеров, что позволяет легко подменять
 * реализации в тестах (например, на mock-объекты или in-memory продюсеры из kafka-streams-test-utils).
 * Фабрика отвечает за управление жизненным циклом созданных продюсеров.
 */
public interface ProducerFactory {

    /**
     * Создаёт или получает из кэша единственный экземпляр {@link KafkaProducer} для отправки строковых сообщений.
     *
     * @return настроенный и готовый к использованию экземпляр {@link KafkaProducer<String, String>}.
     */
    KafkaProducer<String, String> createStringProducer();

    /**
     * Создаёт или получает из кэша единственный экземпляр {@link KafkaProducer} для отправки сообщений в формате Avro.
     *
     * @return настроенный и готовый к использованию экземпляр {@link KafkaProducer<String, GenericRecord>}.
     */
    KafkaProducer<String, GenericRecord> createAvroProducer();

    /**
     * Закрывает все созданные и кэшированные этой фабрикой продюсеры.
     * Этот метод необходимо вызывать при завершении работы приложения для корректного
     * освобождения ресурсов (сетевых соединений, потоков).
     */
    void closeAll();
}
