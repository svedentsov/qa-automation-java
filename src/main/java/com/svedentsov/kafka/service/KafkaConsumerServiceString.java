package com.svedentsov.kafka.service;

import com.svedentsov.kafka.enums.StartStrategyType;
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
 * Реализация {@link KafkaConsumerService} для работы с топиками, содержащими строковые сообщения.
 */
@Slf4j
public class KafkaConsumerServiceString implements KafkaConsumerService {

    private final KafkaListenerManager listenerManager;
    private final KafkaRecordsManager recordsManager;

    /**
     * Создает новый экземпляр {@code KafkaConsumerServiceString}.
     *
     * @param listenerManager Менеджер слушателей Kafka. Не может быть null.
     * @param recordsManager  Менеджер для хранения и доступа к полученным записям. Не может быть null.
     */
    public KafkaConsumerServiceString(KafkaListenerManager listenerManager, KafkaRecordsManager recordsManager) {
        this.listenerManager = requireNonNull(listenerManager, "KafkaListenerManager не может быть null.");
        this.recordsManager = requireNonNull(recordsManager, "KafkaRecordsManager не может быть null.");
    }

    /**
     * Запускает прослушивание указанного строкового топика.
     *
     * @param topic            Имя топика для прослушивания.
     * @param pollTimeout      Таймаут для операции опроса (poll) брокера Kafka.
     * @param startStrategy    Стратегия, определяющая, с какого смещения начать чтение.
     * @param lookBackDuration Продолжительность, на которую нужно "оглянуться" назад,
     *                         если startStrategy - {@link StartStrategyType#FROM_TIMESTAMP}.
     *                         Может быть null для других стратегий.
     */
    @Override
    public void startListening(String topic, Duration pollTimeout, StartStrategyType startStrategy, Duration lookBackDuration) {
        log.info("Запрос на запуск прослушивания строкового топика '{}' со стратегией {}...", topic, startStrategy);
        listenerManager.startListening(topic, pollTimeout, false, startStrategy, lookBackDuration);
    }

    /**
     * Останавливает прослушивание указанного строкового топика.
     *
     * @param topic Имя топика, прослушивание которого нужно прекратить.
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
     * Возвращает все полученные и сохраненные записи из указанного строкового топика.
     *
     * @param topic Имя топика.
     * @return Список записей {@link ConsumerRecord} с ключом и значением в виде строки.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ConsumerRecord<String, String>> getAllRecords(String topic) {
        return recordsManager.getRecords(topic).stream()
                .filter(record -> record.key() instanceof String && record.value() instanceof String)
                .map(record -> (ConsumerRecord<String, String>) record)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает все полученные записи из строкового топика, преобразуя их значения
     * в заданный тип с помощью предоставленной функции-маппера.
     *
     * @param topic  Имя топика.
     * @param mapper Функция для преобразования строкового значения записи в объект типа {@code T}.
     * @param <T>    Целевой тип данных.
     * @return Список объектов типа {@code T}.
     */
    @Override
    public <T> List<T> getAllRecordsAs(String topic, Function<String, T> mapper) {
        return getAllRecords(topic).stream()
                .map(ConsumerRecord::value)
                .map(mapper)
                .collect(Collectors.toList());
    }
}
