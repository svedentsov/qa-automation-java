package com.svedentsov.kafka.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Duration;
import java.util.List;

/**
 * Интерфейс для сервиса потребителя Kafka.
 * Предоставляет методы для запуска и остановки прослушивания топиков, а также для получения записей из топиков.
 */
public interface KafkaConsumerService {

    /**
     * Запускает прослушивание указанного топика.
     *
     * @param topic   название топика, который нужно слушать
     * @param timeout продолжительность ожидания новых сообщений
     */
    void startListening(String topic, Duration timeout);

    /**
     * Останавливает прослушивание указанного топика.
     *
     * @param topic название топика, для которого нужно остановить прослушивание
     */
    void stopListening(String topic);

    /**
     * Получает все записи из указанного топика.
     *
     * @param topic название топика, из которого нужно получить записи
     * @return список записей, полученных из топика
     */
    List<ConsumerRecord<String, String>> getAllRecords(String topic);
}
