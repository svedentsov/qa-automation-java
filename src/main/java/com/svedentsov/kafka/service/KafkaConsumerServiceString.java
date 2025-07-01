package com.svedentsov.kafka.service;

import com.svedentsov.kafka.factory.ConsumerFactory;
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
 * Реализация {@link KafkaConsumerService} для работы с записями в строковом формате.
 * Этот сервис управляет жизненным циклом слушателей для топиков со строковыми данными
 * и предоставляет доступ к полученным записям через {@link KafkaRecordsManager}.
 */
@Slf4j
public class KafkaConsumerServiceString implements KafkaConsumerService {

    private final KafkaListenerManager listenerManager;
    private final KafkaRecordsManager recordsManager;
    private final ConsumerFactory consumerFactory;

    /**
     * Создает экземпляр сервиса для строковых сообщений.
     *
     * @param consumerFactory Фабрика для создания низкоуровневых Kafka Consumers.
     * @param listenerManager Менеджер жизненного цикла слушателей. Не может быть {@code null}.
     * @param recordsManager  Менеджер для хранения полученных записей. Не может быть {@code null}.
     */
    public KafkaConsumerServiceString(ConsumerFactory consumerFactory, KafkaListenerManager listenerManager, KafkaRecordsManager recordsManager) {
        this.consumerFactory = requireNonNull(consumerFactory, "ConsumerFactory не может быть null.");
        this.listenerManager = requireNonNull(listenerManager, "KafkaListenerManager не может быть null.");
        this.recordsManager = requireNonNull(recordsManager, "KafkaRecordsManager не может быть null.");
    }

    /**
     * Запускает прослушивание указанного топика для строковых данных.
     *
     * @param topic   название Kafka-топика
     * @param timeout таймаут ожидания записи (poll timeout)
     */
    @Override
    public void startListening(String topic, Duration timeout) {
        log.info("Запрос на запуск прослушивания строкового топика '{}'...", topic);
        listenerManager.startListening(topic, timeout, false, this.recordsManager);
    }

    /**
     * Останавливает прослушивание указанного топика.
     *
     * @param topic название Kafka-топика
     */
    @Override
    public void stopListening(String topic) {
        log.info("Запрос на остановку прослушивания строкового топика '{}'...", topic);
        if (listenerManager.stopListening(topic)) {
            log.info("Прослушивание строкового топика '{}' успешно остановлено.", topic);
        } else {
            log.warn("Не удалось остановить прослушивание строкового топика '{}', возможно, он не был запущен.", topic);
        }
    }

    /**
     * Получает все записи из указанного топика.
     *
     * @param topic название Kafka-топика
     * @return список записей {@link ConsumerRecord} с ключами и значениями в виде строк
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ConsumerRecord<String, String>> getAllRecords(String topic) {
        return recordsManager.getRecords(topic).stream()
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
