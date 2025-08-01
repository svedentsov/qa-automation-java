package com.svedentsov.kafka.service;

import com.svedentsov.kafka.enums.StartStrategyType;
import com.svedentsov.kafka.helper.strategy.StartStrategyOptions;
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
     * @param topic           Название топика.
     * @param pollTimeout     Максимальное время ожидания в poll-запросе.
     * @param strategyOptions Объект, содержащий тип стратегии и её параметры.
     */
    void startListening(String topic, Duration pollTimeout, StartStrategyOptions strategyOptions);

    /**
     * Запускает прослушивание топика с параметрами по умолчанию.
     * Используется стратегия {@code FROM_TIMESTAMP} с периодом 2 минуты.
     *
     * @param topic       Название топика.
     * @param pollTimeout Максимальное время ожидания в poll-запросе.
     */
    default void startListening(String topic, Duration pollTimeout) {
        startListening(topic, pollTimeout, StartStrategyOptions.builder()
                .strategyType(StartStrategyType.FROM_TIMESTAMP)
                .lookBackDuration(Duration.ofMinutes(2))
                .build());
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
