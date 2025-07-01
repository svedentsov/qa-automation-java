package com.svedentsov.kafka.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

/**
 * Определяет контракт для сервиса-потребителя (consumer) сообщений из Kafka.
 * Сервис предоставляет высокоуровневые методы для управления прослушиванием топиков
 * и получения обработанных данных.
 */
public interface KafkaConsumerService {

    /**
     * Запускает прослушивание указанного топика.
     * Метод должен быть неблокирующим и запускать прослушивание в фоновом режиме.
     *
     * @param topic   Имя топика для прослушивания.
     * @param timeout Таймаут для операции опроса (poll) брокера Kafka.
     */
    void startListening(String topic, Duration timeout);

    /**
     * Останавливает прослушивание указанного топика.
     *
     * @param topic Имя топика, прослушивание которого нужно прекратить.
     */
    void stopListening(String topic);

    /**
     * Возвращает все полученные и сохраненные записи из указанного топика.
     * <b>Важно:</b> Для Avro-сообщений значение (value) записи будет представлено в виде JSON-строки.
     *
     * @param topic Имя топика.
     * @return Список записей {@link ConsumerRecord} с ключом и значением в виде строки.
     */
    List<ConsumerRecord<String, String>> getAllRecords(String topic);

    /**
     * Возвращает все полученные записи из топика, преобразуя их значения
     * в заданный тип с помощью предоставленной функции-маппера.
     *
     * @param topic  Имя топика.
     * @param mapper Функция для преобразования строкового значения записи в объект типа {@code T}.
     * @param <T>    Целевой тип данных.
     * @return Список объектов типа {@code T}.
     */
    <T> List<T> getAllRecordsAs(String topic, Function<String, T> mapper);
}
