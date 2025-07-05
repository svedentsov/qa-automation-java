package com.svedentsov.kafka.service;

import com.svedentsov.kafka.enums.StartStrategyType;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

/**
 * Интерфейс, определяющий высокоуровневый сервис для работы с консьюмерами Kafka.
 * Абстрагирует сложность управления слушателями и предоставляет простые методы
 * для запуска, остановки и получения данных.
 */
public interface KafkaConsumerService {

    /**
     * Запускает прослушивание топика с заданными параметрами.
     *
     * @param topic            Название топика.
     * @param pollTimeout      Максимальное время ожидания в poll-запросе.
     * @param startStrategy    Стратегия, определяющая, с какого места начать чтение.
     * @param lookBackDuration Длительность для поиска сообщений, если используется стратегия {@code FROM_TIMESTAMP}.
     */
    void startListening(String topic, Duration pollTimeout, StartStrategyType startStrategy, Duration lookBackDuration);

    /**
     * Запускает прослушивание топика с параметрами по умолчанию.
     * Используется стратегия {@code FROM_TIMESTAMP} с периодом 2 минуты.
     *
     * @param topic       Название топика.
     * @param pollTimeout Максимальное время ожидания в poll-запросе.
     */
    default void startListening(String topic, Duration pollTimeout) {
        startListening(topic, pollTimeout, StartStrategyType.FROM_TIMESTAMP, Duration.ofMinutes(2));
    }

    /**
     * Останавливает прослушивание указанного топика.
     *
     * @param topic Название топика.
     */
    void stopListening(String topic);

    /**
     * Возвращает все записи, полученные из топика с момента запуска слушателя.
     * Для Avro-топиков значение записи (value) будет представлено в виде JSON-строки.
     *
     * @param topic Название топика.
     * @return Список записей {@link ConsumerRecord}.
     */
    List<ConsumerRecord<String, String>> getAllRecords(String topic);

    /**
     * Возвращает все записи из топика, преобразуя их значения с помощью предоставленной функции.
     *
     * @param topic  Название топика.
     * @param mapper Функция для преобразования строкового значения записи в нужный тип {@code T}.
     * @param <T>    Целевой тип данных.
     * @return Список объектов типа {@code T}.
     */
    <T> List<T> getAllRecordsAs(String topic, Function<String, T> mapper);
}
