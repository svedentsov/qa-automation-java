package com.svedentsov.kafka.service;

import com.svedentsov.kafka.helper.KafkaTopicListener.ConsumerStartStrategy;
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
     * Запускает прослушивание указанного топика с заданной стратегией старта.
     * Метод должен быть неблокирующим и запускать прослушивание в фоновом режиме.
     *
     * @param topic            Имя топика для прослушивания.
     * @param timeout          Таймаут для операции опроса (poll) брокера Kafka.
     * @param startStrategy    Стратегия, определяющая, с какого смещения начать чтение.
     * @param lookBackDuration Продолжительность, на которую нужно "оглянуться" назад,
     *                         если startStrategy - {@link ConsumerStartStrategy#FROM_TIMESTAMP}.
     *                         Может быть null для других стратегий.
     */
    void startListening(String topic, Duration timeout, ConsumerStartStrategy startStrategy, Duration lookBackDuration);

    /**
     * Запускает прослушивание указанного топика с стратегией по умолчанию (FROM_TIMESTAMP).
     *
     * @param topic   Имя топика для прослушивания.
     * @param timeout Таймаут для операции опроса (poll) брокера Kafka.
     */
    default void startListening(String topic, Duration timeout) {
        startListening(topic, timeout, ConsumerStartStrategy.FROM_TIMESTAMP, Duration.ofMinutes(2));
    }

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
