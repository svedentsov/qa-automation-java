package com.svedentsov.kafka.service;

import com.svedentsov.kafka.model.Record;

/**
 * Интерфейс для сервиса отправки записей в Kafka.
 */
public interface KafkaProducerService {

    /**
     * Отправляет запись в Kafka.
     *
     * @param record объект записи, содержащий информацию о топике, ключе, значении и заголовках
     */
    void sendRecord(Record record);
}
