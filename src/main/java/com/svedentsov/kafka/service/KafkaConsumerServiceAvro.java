package com.svedentsov.kafka.service;

import com.svedentsov.kafka.config.KafkaListenerConfig;
import com.svedentsov.kafka.helper.KafkaListenerManager;
import com.svedentsov.kafka.helper.KafkaRecordsManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса потребителя Kafka для данных в формате Avro.
 * Использует KafkaListenerManager для управления listener-ами.
 */
@Slf4j
public class KafkaConsumerServiceAvro implements KafkaConsumerService {

    private final KafkaListenerManager listenerManager;

    /**
     * Конструктор, создаёт собственный KafkaListenerManager на базе переданного конфига.
     *
     * @param config конфигурация listener-ов, не null
     * @throws IllegalArgumentException если config == null
     */
    public KafkaConsumerServiceAvro(KafkaListenerConfig config) {
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
    public KafkaConsumerServiceAvro(KafkaListenerManager listenerManager) {
        if (listenerManager == null) {
            throw new IllegalArgumentException("KafkaListenerManager не может быть null");
        }
        this.listenerManager = listenerManager;
    }

    /**
     * Запускает прослушивание указанного топика для данных в формате Avro.
     *
     * @param topic   название топика, не null/пустое
     * @param timeout таймаут polling'а
     */
    @Override
    public void startListening(String topic, Duration timeout) {
        try {
            listenerManager.startListening(topic, timeout, true);
            log.info("Запущено прослушивание Avro-топика '{}'", topic);
        } catch (Exception e) {
            log.error("Не удалось запустить прослушивание Avro-топика '{}'", topic, e);
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
                log.info("Остановлено прослушивание Avro-топика '{}'", topic);
            } else {
                log.warn("При попытке остановки прослушивания Avro-топика '{}': listener не найден", topic);
            }
        } catch (Exception e) {
            log.error("Ошибка при остановке прослушивания Avro-топика '{}'", topic, e);
            throw e;
        }
    }

    /**
     * Получает все уникальные записи из указанного топика и преобразует их в строки.
     *
     * @param topic название топика, не null/пустое
     * @return список строковых представлений Avro-записей; пустой список, если записей нет
     */
    @Override
    public List<ConsumerRecord<String, String>> getAllRecords(String topic) {
        return KafkaRecordsManager.getRecords(topic).stream()
                .map(this::convertToStringRecord)
                .collect(Collectors.toList());
    }

    /**
     * Преобразует запись Avro (GenericRecord) в строковое представление.
     *
     * @param avroRecord запись с ключом неизвестного типа и значением GenericRecord
     * @return ConsumerRecord<String, String> с тем же topic/partition/offset и value.toString()
     * @throws IllegalArgumentException если avroRecord == null или значение не GenericRecord
     */
    @SuppressWarnings("unchecked")
    private ConsumerRecord<String, String> convertToStringRecord(ConsumerRecord<?, ?> avroRecord) {
        if (avroRecord == null) {
            throw new IllegalArgumentException("Avro record не может быть null");
        }
        Object value = avroRecord.value();
        if (!(value instanceof GenericRecord)) {
            log.error("Неверный тип записи для Avro-консьюмера: {}", value == null ? "null" : value.getClass().getName());
            throw new IllegalArgumentException("Неверный тип записи для Avro-консьюмера: ожидается GenericRecord");
        }
        GenericRecord genericRecord = (GenericRecord) value;
        String valueString;
        try {
            valueString = genericRecord.toString();
        } catch (Exception e) {
            log.error("Ошибка при конвертации GenericRecord в строку для топика '{}': {}", avroRecord.topic(), e.getMessage(), e);
            throw new RuntimeException("Ошибка при конвертации Avro-записи в строку", e);
        }
        String keyString = null;
        if (avroRecord.key() != null) {
            Object keyObj = avroRecord.key();
            keyString = keyObj instanceof String ? (String) keyObj : keyObj.toString();
        }
        return new ConsumerRecord<>(
                avroRecord.topic(),
                avroRecord.partition(),
                avroRecord.offset(),
                keyString,
                valueString
        );
    }
}
