package com.svedentsov.kafka.processor;

import org.apache.kafka.clients.consumer.ConsumerRecords;

/**
 * Интерфейс для классов, отвечающих за обработку пачки записей (records),
 * полученных из Kafka.
 *
 * @param <V> Тип значения (value) в записи Kafka.
 */
@FunctionalInterface
public interface RecordProcessor<V> {

    /**
     * Обрабатывает пачку записей, полученных из Kafka.
     * Реализации этого метода должны содержать логику по обработке,
     * сохранению или анализу полученных сообщений.
     *
     * @param records Пачка записей для обработки.
     */
    void processRecords(ConsumerRecords<String, V> records);
}
