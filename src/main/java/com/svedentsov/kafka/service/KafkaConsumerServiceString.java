package com.svedentsov.kafka.service;

import com.svedentsov.kafka.config.KafkaListenerConfig;
import com.svedentsov.kafka.helper.KafkaListenerManager;
import com.svedentsov.kafka.helper.KafkaRecordsManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса потребителя Kafka для строковых данных.
 * Использует KafkaListenerManager для управления listener-ами.
 */
@Slf4j
public class KafkaConsumerServiceString implements KafkaConsumerService {

    private final KafkaListenerManager listenerManager;

    /**
     * Конструктор, создаёт собственный KafkaListenerManager на базе переданного конфига.
     *
     * @param config конфигурация listener-ов, не null
     * @throws IllegalArgumentException если config == null
     */
    public KafkaConsumerServiceString(KafkaListenerConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("KafkaListenerConfig не может быть null");
        }
        this.listenerManager = new KafkaListenerManager(config);
    }

    /**
     * Альтернативный конструктор: если менеджер создаётся или управляется извне.
     *
     * @param listenerManager готовый менеджер, не null
     * @throws IllegalArgumentException если listenerManager == null
     */
    public KafkaConsumerServiceString(KafkaListenerManager listenerManager) {
        if (listenerManager == null) {
            throw new IllegalArgumentException("KafkaListenerManager не может быть null");
        }
        this.listenerManager = listenerManager;
    }

    /**
     * Запускает прослушивание указанного топика для строковых данных.
     *
     * @param topic   название топика, не null/пустое
     * @param timeout таймаут polling'а
     */
    @Override
    public void startListening(String topic, Duration timeout) {
        try {
            listenerManager.startListening(topic, timeout, false);
            log.info("Запущено прослушивание топика '{}'", topic);
        } catch (Exception e) {
            log.error("Не удалось запустить прослушивание топика '{}'", topic, e);
            throw e;
        }
    }

    /**
     * Останавливает прослушивание указанного топика.
     *
     * @param topic название топика, не null/пустое
     */
    @Override
    public void stopListening(String topic) {
        try {
            boolean stopped = listenerManager.stopListening(topic);
            if (stopped) {
                log.info("Остановлено прослушивание топика '{}'", topic);
            } else {
                log.warn("При попытке остановки прослушивания топика '{}': listener не найден", topic);
            }
        } catch (Exception e) {
            log.error("Ошибка при остановке прослушивания топика '{}'", topic, e);
            throw e;
        }
    }

    /**
     * Получает все уникальные записи из указанного топика в формате строк.
     *
     * @param topic название топика, не null/пустое
     * @return список уникальных записей в формате строк; если записей нет, возвращается пустой список
     */
    @Override
    public List<ConsumerRecord<String, String>> getAllRecords(String topic) {
        return KafkaRecordsManager.getRecords(topic).stream()
                .map(record -> (ConsumerRecord<String, String>) record)
                .collect(Collectors.toList());
    }
}
