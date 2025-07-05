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
import java.nio.charset.StandardCharsets;

import static java.util.Objects.requireNonNull;

/**
 * Реализация {@link RecordProcessor} для обработки записей со значением в формате Avro (представленным как {@link Object}).
 * Выполняет валидацию типа и добавляет валидные записи в {@link KafkaRecordsManager}.
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
        this.topicName = requireNonNull(topicName, "Имя топика не может быть null.");
        this.config = requireNonNull(config, "KafkaListenerConfig не может быть null.");
        this.recordsManager = requireNonNull(recordsManager, "KafkaRecordsManager не может быть null.");
    }

    @Override
    public void processRecords(ConsumerRecords<String, Object> records) {
        requireNonNull(records, "ConsumerRecords не может быть null.");
        log.debug("Обработка {} Avro-записей для топика '{}'...", records.count(), topicName);
        for (ConsumerRecord<String, Object> record : records) {
            if (!isValid(record)) {
                continue; // Пропускаем невалидные записи, isValid уже залогировал проблему
            }
            try {
                recordsManager.addRecord(topicName, record);
            } catch (Exception e) {
                log.error("Не удалось обработать Avro-запись из топика '{}', offset={}, partition={}", topicName, record.offset(), record.partition(), e);
                if (config.shouldStopOnError()) {
                    throw new ProcessingException("Критическая ошибка при обработке Avro-записи в топике " + topicName, e);
                }
            }
        }
    }

    /**
     * Проверяет, является ли запись валидной для обработки.
     *
     * @param record Запись для проверки.
     * @return {@code true}, если запись и ее значение не null, и значение является {@link GenericRecord}.
     */
    private boolean isValid(ConsumerRecord<String, Object> record) {
        if (record == null) {
            log.warn("Обнаружена null-запись в топике '{}', пропуск.", topicName);
            return false;
        }
        Object value = record.value();
        if (value == null) {
            log.warn("Значение записи (value) is null в топике '{}', offset={}, partition={}", topicName, record.offset(), record.partition());
            return false; // Пропускаем записи с null-значением
        }
        if (!(value instanceof GenericRecord)) {
            log.warn("Значение не является GenericRecord в топике '{}', offset={}, тип={}", topicName, record.offset(), value.getClass().getName());
            return false;
        }
        return true;
    }

    /**
     * Статический утилитный метод для преобразования {@link GenericRecord} в JSON-строку.
     *
     * @param genericRecord Avro-запись для преобразования.
     * @param schema        Схема Avro, соответствующая записи.
     * @return JSON-представление записи.
     * @throws RuntimeException если происходит ошибка ввода-вывода во время сериализации.
     */
    public static String genericRecordToJson(GenericRecord genericRecord, Schema schema) {
        requireNonNull(genericRecord, "GenericRecord не может быть null.");
        requireNonNull(schema, "Schema не может быть null.");
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            GenericDatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
            JsonEncoder encoder = EncoderFactory.get().jsonEncoder(schema, out);
            writer.write(genericRecord, encoder);
            encoder.flush();
            return out.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при преобразовании GenericRecord в JSON", e);
        }
    }
}
