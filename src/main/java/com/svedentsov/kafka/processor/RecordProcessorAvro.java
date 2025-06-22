package com.svedentsov.kafka.processor;

import com.svedentsov.kafka.config.KafkaListenerConfig;
import com.svedentsov.kafka.exception.KafkaListenerException;
import com.svedentsov.kafka.helper.KafkaRecordsManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.SerializationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Обработка Avro-сообщений: фильтрация невалидных, сохранение валидных в KafkaRecordsManager.
 * Также сохраняет JSON-представление значения для QA, если нужно.
 */
@Slf4j
@RequiredArgsConstructor
public class RecordProcessorAvro implements RecordProcessor<Object> {

    private final String topicName;
    private final KafkaListenerConfig config;

    /**
     * Обрабатывает пакет Avro-записей: сначала фильтрует невалидные, затем вызывает логику обработки.
     *
     * @param records набор записей для обработки
     */
    @Override
    public void processRecords(ConsumerRecords<String, Object> records) {
        Objects.requireNonNull(records, "records не могут быть null.");
        ConsumerRecords<String, Object> validRecords = filterValid(records);
        int errorCount = 0, successCount = 0;
        for (ConsumerRecord<String, Object> record : validRecords) {
            try {
                KafkaRecordsManager.addRecord(topicName, record);
                successCount++;
            } catch (Exception e) {
                errorCount++;
                log.error("Ошибка при обработке Avro-записи из топика '{}', offset={}, partition={}: {}", topicName, record.offset(), record.partition(), e.getMessage(), e);
                if (config.shouldStopOnError()) {
                    throw new KafkaListenerException.ProcessingException("Критическая ошибка при обработке Avro-записи в топике " + topicName, e);
                }
            }
        }
        if (config.isEnableMetrics()) {
            log.debug("Обработано {} валидных Avro-записей (ошибок: {}) из {} для топика '{}'", successCount, errorCount, records.count(), topicName);
        }
    }

    private ConsumerRecords<String, Object> filterValid(ConsumerRecords<String, Object> records) {
        Map<TopicPartition, List<ConsumerRecord<String, Object>>> validMap = new HashMap<>();
        int invalidCount = 0;
        for (ConsumerRecord<String, Object> record : records) {
            if (record == null) {
                invalidCount++;
                log.warn("Обнаружена null-запись в топике '{}', пропуск.", topicName);
                continue;
            }
            if (isValid(record)) {
                TopicPartition tp = new TopicPartition(record.topic(), record.partition());
                validMap.computeIfAbsent(tp, k -> new ArrayList<>()).add(record);
            } else {
                invalidCount++;
            }
        }
        if (invalidCount > 0) {
            log.info("Отфильтровано {} невалидных Avro-записей для топика '{}'.", invalidCount, topicName);
        }
        return new ConsumerRecords<>(validMap);
    }

    /**
     * Проверяет валидность записи путём попытки доступа к её значению.
     *
     * @param record запись для проверки
     * @return true, если запись валидна; false иначе
     */
    private boolean isValid(ConsumerRecord<String, Object> record) {
        try {
            Object value = record.value();
            if (value == null) {
                log.warn("Значение Avro null в топике '{}', offset={}, partition={}", topicName, record.offset(), record.partition());
                return false;
            }
            if (!(value instanceof GenericRecord)) {
                log.warn("Значение Avro не является GenericRecord в топике '{}', offset={}, тип={}", topicName, record.offset(), value.getClass().getName());
                return false;
            }
            return true;
        } catch (SerializationException e) {
            log.warn("SerializationException при проверке записи Avro в топике '{}', offset={}, partition={}: {}", topicName, record.offset(), record.partition(), e.getMessage());
            return false;
        } catch (Exception e) {
            log.warn("Неожиданная ошибка при проверке Avro-записи в топике '{}', offset={}, partition={}: {}", topicName, record.offset(), record.partition(), e.getMessage(), e);
            return false;
        }
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
        Objects.requireNonNull(genericRecord, "GenericRecord не может быть null.");
        Objects.requireNonNull(schema, "Schema не может быть null.");
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
