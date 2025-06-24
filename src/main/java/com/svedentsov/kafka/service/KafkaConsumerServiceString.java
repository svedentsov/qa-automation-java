package com.svedentsov.kafka.service;

import com.svedentsov.kafka.config.KafkaListenerConfig;
import com.svedentsov.kafka.helper.KafkaListenerManager;
import com.svedentsov.kafka.helper.KafkaRecordsManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Реализация сервиса потребителя Kafka для строковых данных.
 * Использует {@link KafkaListenerManager} для управления подпиской и получением записей.
 */
@Slf4j
public class KafkaConsumerServiceString implements KafkaConsumerService {

    private final KafkaListenerManager listenerManager;

    /**
     * Конструктор, создающий менеджер KafkaListener на основе переданной конфигурации.
     *
     * @param config конфигурация KafkaListener'а (не null)
     * @throws IllegalArgumentException если {@code config} равен {@code null}
     */
    public KafkaConsumerServiceString(KafkaListenerConfig config) {
        requireNonNull(config, "KafkaListenerConfig не может быть null.");
        this.listenerManager = new KafkaListenerManager(config);
    }

    /**
     * Конструктор, принимающий готовый {@link KafkaListenerManager}.
     *
     * @param listenerManager менеджер KafkaListener'а (не null)
     * @throws IllegalArgumentException если {@code listenerManager} равен {@code null}
     */
    public KafkaConsumerServiceString(KafkaListenerManager listenerManager) {
        requireNonNull(listenerManager, "KafkaListenerManager не может быть null.");
        this.listenerManager = listenerManager;
    }

    /**
     * Запускает прослушивание указанного топика для строковых данных.
     *
     * @param topic   название Kafka-топика
     * @param timeout таймаут ожидания записи (poll timeout)
     */
    @Override
    public void startListening(String topic, Duration timeout) {
        listenerManager.startListening(topic, timeout, false);
        log.info("Начато прослушивание строкового топика '{}'", topic);
    }

    /**
     * Останавливает прослушивание указанного топика.
     *
     * @param topic название Kafka-топика
     */
    @Override
    public void stopListening(String topic) {
        listenerManager.stopListening(topic);
        log.info("Завершено прослушивание строкового топика '{}'", topic);
    }

    /**
     * Получает все записи из указанного топика.
     *
     * @param topic название Kafka-топика
     * @return список записей {@link ConsumerRecord} с ключами и значениями в виде строк
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<ConsumerRecord<String, String>> getAllRecords(String topic) {
        return KafkaRecordsManager.getRecords(topic).stream()
                .map(record -> (ConsumerRecord<String, String>) record)
                .collect(Collectors.toList());
    }

    /**
     * Получает все значения записей из топика и преобразует их через указанный маппер.
     *
     * @param topic  название Kafka-топика
     * @param mapper функция преобразования строки в объект типа {@code T}
     * @param <T>    тип возвращаемых объектов
     * @return список объектов, полученных из значений записей
     */
    @Override
    public <T> List<T> getAllRecordsAs(String topic, Function<String, T> mapper) {
        return getAllRecords(topic).stream()
                .map(ConsumerRecord::value)
                .map(mapper)
                .collect(Collectors.toList());
    }
}
