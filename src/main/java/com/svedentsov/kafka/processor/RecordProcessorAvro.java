package com.svedentsov.kafka.processor;

import com.svedentsov.kafka.config.KafkaListenerConfig;
import com.svedentsov.kafka.exception.KafkaListenerException.ProcessingException;
import com.svedentsov.kafka.helper.KafkaRecordsManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * Обработчик, который проверяет и сохраняет Avro записи с помощью {@link KafkaRecordsManager}.
 */
@Slf4j
public final class RecordProcessorAvro implements RecordProcessor<Object> {

    private final String topicName;
    private final KafkaListenerConfig config;
    private final KafkaRecordsManager recordsManager;

    /**
     * Создает экземпляр обработчика.
     *
     * @param topicName      Имя топика, из которого приходят записи.
     * @param config         Конфигурация слушателя.
     * @param recordsManager Менеджер для сохранения записей.
     */
    public RecordProcessorAvro(String topicName, KafkaListenerConfig config, KafkaRecordsManager recordsManager) {
        this.topicName = topicName;
        this.config = config;
        this.recordsManager = recordsManager;
    }

    @Override
    public void processRecords(ConsumerRecords<String, Object> records) {
        requireNonNull(records, "ConsumerRecords не может быть null.");
        if (config.isEnableMetrics()) {
            log.debug("Обработка {} Avro-записей для топика '{}'...", records.count(), topicName);
        }

        for (ConsumerRecord<String, Object> record : records) {
            if (!isValid(record)) {
                continue; // Пропускаем невалидные записи, isValid уже залогировал проблему
            }
            try {
                recordsManager.addRecord(topicName, record);
            } catch (Exception e) {
                log.error("Не удалось обработать Avro-запись из топика '{}', offset={}, partition={}",
                        topicName, record.offset(), record.partition(), e);
                if (config.shouldStopOnError()) {
                    throw new ProcessingException("Критическая ошибка при обработке Avro-записи в топике " + topicName, e);
                }
            }
        }
    }

    /**
     * Проверяет, является ли запись валидной Avro-записью.
     *
     * @param record Запись для проверки.
     * @return {@code true}, если запись валидна.
     */
    private boolean isValid(ConsumerRecord<String, Object> record) {
        if (record == null) {
            log.warn("Обнаружена null-запись в топике '{}', пропуск.", topicName);
            return false;
        }
        Object value = record.value();
        if (value == null) {
            log.warn("Значение записи (value) is null в топике '{}', offset={}, partition={}", topicName, record.offset(), record.partition());
            return false;
        }
        if (!(value instanceof GenericRecord)) {
            log.warn("Значение не является GenericRecord в топике '{}', offset={}, тип={}", topicName, record.offset(), value.getClass().getName());
            return false;
        }
        return true;
    }

    /**
     * Преобразует GenericRecord в JSON-строку (Avro JSON).
     * Может использоваться в тестах для анализа содержимого.
     *
     * @param genericRecord GenericRecord
     * @param schema        Avro Schema
     * @return JSON-строка
     */
    public static String genericRecordToJson(GenericRecord genericRecord, Schema schema) {
        requireNonNull(genericRecord, "GenericRecord не может быть null.");
        requireNonNull(schema, "Schema не может быть null.");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            GenericDatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
            JsonEncoder encoder = EncoderFactory.get().jsonEncoder(schema, out);
            writer.write(genericRecord, encoder);
            encoder.flush();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при преобразовании GenericRecord в JSON", e);
        }
        return out.toString(java.nio.charset.StandardCharsets.UTF_8);
    }
}
