package com.svedentsov.kafka.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

/**
 * Сервис потребителя Kafka для тестов.
 */
public interface KafkaConsumerService {

    /**
     * Запускает прослушивание topic.
     *
     * @param topic   имя топика
     * @param timeout poll timeout
     */
    void startListening(String topic, Duration timeout);

    /**
     * Останавливает прослушивание.
     *
     * @param topic имя топика
     */
    void stopListening(String topic);

    /**
     * Возвращает все сохранённые ConsumerRecord<String, String> (строка).
     *
     * @param topic имя
     * @return список
     */
    List<ConsumerRecord<String, String>> getAllRecords(String topic);

    /**
     * Для Avro: преобразует GenericRecord → String или POJO.
     *
     * @param topic   имя
     * @param mapper  функция преобразования строки JSON → T
     * @param <T>     тип
     * @return список T
     */
    <T> List<T> getAllRecordsAs(String topic, Function<String, T> mapper);
}
