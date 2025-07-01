package com.svedentsov.kafka.factory;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.io.Closeable;

/**
 * Интерфейс фабрики для создания и управления жизненным циклом экземпляров {@link KafkaProducer}.
 * Абстрагирует процесс создания продюсеров, что позволяет легко подменять
 * реализации в приложении (например, на mock-объекты в тестах).
 * Фабрика выступает "владельцем" созданных продюсеров и отвечает за их корректное закрытие,
 * так как продюсеры являются потокобезопасными и могут переиспользоваться.
 */
public interface ProducerFactory extends Closeable {

    /**
     * Создаёт или возвращает из кэша экземпляр {@link KafkaProducer} для отправки строковых сообщений.
     *
     * @return настроенный и готовый к использованию экземпляр {@link KafkaProducer<String, String>}.
     */
    KafkaProducer<String, String> createStringProducer();

    /**
     * Создаёт или возвращает из кэша экземпляр {@link KafkaProducer} для отправки сообщений в формате Avro.
     *
     * @return настроенный и готовый к использованию экземпляр {@link KafkaProducer<String, GenericRecord>}.
     */
    KafkaProducer<String, GenericRecord> createAvroProducer();

    /**
     * Закрывает все созданные и кэшированные этой фабрикой продюсеры.
     * Этот метод <b>необходимо</b> вызывать при завершении работы приложения для корректного
     * освобождения ресурсов (сетевых соединений, потоков). Реализует метод {@link Closeable#close()}
     * для удобного использования в конструкциях try-with-resources.
     */
    @Override
    void close();
}
