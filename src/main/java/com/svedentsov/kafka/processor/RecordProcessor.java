package com.svedentsov.kafka.processor;

import org.apache.kafka.clients.consumer.ConsumerRecords;

/**
 * Интерфейс для обработки записей Kafka.
 *
 * @param <V> тип значения сообщения
 */
public interface RecordProcessor<V> {
    /**
     * Обрабатывает пакет записей.
     *
     * @param records ConsumerRecords с ключом String и значением типа V
     */
    void processRecords(ConsumerRecords<String, V> records);
}
