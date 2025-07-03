package com.svedentsov.kafka.processor;

import org.apache.kafka.clients.consumer.ConsumerRecords;

/**
 * Интерфейс для обработчиков записей, полученных из Kafka.
 *
 * @param <V> Тип значения (value) в записи Kafka.
 */
public interface RecordProcessor<V> {

    /**
     * Обрабатывает пачку записей из Kafka.
     *
     * @param records Записи, полученные от консьюмера.
     */
    void processRecords(ConsumerRecords<String, V> records);
}
