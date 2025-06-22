package com.svedentsov.kafka.service;

import com.svedentsov.kafka.config.KafkaListenerConfig;
import com.svedentsov.kafka.helper.KafkaListenerManager;
import com.svedentsov.kafka.helper.KafkaRecordsManager;
import com.svedentsov.kafka.processor.RecordProcessorAvro;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.svedentsov.kafka.utils.ValidationUtils.requireNonNull;

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
        requireNonNull(config, "KafkaListenerConfig не может быть null.");
        this.listenerManager = new KafkaListenerManager(config);
    }

    /**
     * Альтернативный конструктор: если менеджер создаётся или управляется извне.
     *
     * @param listenerManager готовый менеджер, не null
     * @throws IllegalArgumentException если listenerManager == null
     */
    public KafkaConsumerServiceAvro(KafkaListenerManager listenerManager) {
        requireNonNull(listenerManager, "KafkaListenerManager не может быть null.");
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
        listenerManager.startListening(topic, timeout, true);
        log.info("Начато прослушивание AVRO-топика '{}'", topic);
    }

    /**
     * Останавливает прослушивание указанного топика.
     *
     * @param topic название топика, не null/пустое
     */
    @Override
    public void stopListening(String topic) {
        listenerManager.stopListening(topic);
        log.info("Завершено прослушивание AVRO-топика '{}'", topic);
    }

    /**
     * Получает все уникальные записи из указанного топика и преобразует их в строки.
     *
     * @param topic название топика, не null/пустое
     * @return список строковых представлений Avro-записей; пустой список, если записей нет
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<ConsumerRecord<String, String>> getAllRecords(String topic) {
        // Преобразуем GenericRecord → JSON с помощью Avro JSON encoder
        return KafkaRecordsManager.getRecords(topic).stream()
                .map(record -> {
                    Object val = record.value();
                    if (!(val instanceof GenericRecord)) {
                        log.error("Ожидался GenericRecord, но получен {} для топика {}", val == null ? "null" : val.getClass().getName(), topic);
                        throw new IllegalArgumentException("Неверный тип записи для Avro-потребителя");
                    }
                    GenericRecord gr = (GenericRecord) val;
                    String json = RecordProcessorAvro.genericRecordToJson(gr, gr.getSchema());
                    String keyStr = record.key() != null ? record.key().toString() : null;
                    return new ConsumerRecord<>(record.topic(), record.partition(), record.offset(), keyStr, json);
                })
                .collect(Collectors.toList());
    }

    @Override
    public <T> List<T> getAllRecordsAs(String topic, Function<String, T> mapper) {
        return getAllRecords(topic).stream()
                .map(ConsumerRecord::value)
                .map(mapper)
                .collect(Collectors.toList());
    }
}
