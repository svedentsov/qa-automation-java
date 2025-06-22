package com.svedentsov.kafka.processor;

import org.apache.kafka.clients.consumer.ConsumerRecords;

/**
 * Интерфейс для обработки пакетов записей Kafka.
 *
 * @param <V> тип значения сообщения
 */
public interface RecordProcessor<V> {
    /**
     * Обрабатывает пакет записей.
     *
     * @param records набор записей
     */
    void processRecords(ConsumerRecords<String, V> records);
}
