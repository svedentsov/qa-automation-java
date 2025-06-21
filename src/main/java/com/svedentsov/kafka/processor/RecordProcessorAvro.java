package com.svedentsov.kafka.processor;

import com.svedentsov.kafka.config.KafkaListenerConfig;
import com.svedentsov.kafka.helper.KafkaRecordsManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.SerializationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Обработчик Avro-записей Kafka с фильтрацией невалидных сообщений.
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
        ConsumerRecords<String, Object> validRecords = filterValidRecords(records);
        for (ConsumerRecord<String, Object> record : validRecords) {
            try {
                processRecord(record);
            } catch (Exception e) {
                log.error("Критическая ошибка при обработке Avro записи из топика '{}'", topicName, e);
                if (config.shouldStopOnError()) {
                    throw new RuntimeException("Критическая ошибка обработки Avro записи", e);
                }
            }
        }
        if (config.isEnableMetrics()) {
            log.debug("Обработано {} валидных Avro записей из {} для топика '{}'",
                    validRecords.count(), records.count(), topicName);
        }
    }

    /**
     * Фильтрует валидные записи, исключая повреждённые (SerializationException).
     *
     * @param records все записи
     * @return ConsumerRecords, содержащий только валидные записи
     */
    private ConsumerRecords<String, Object> filterValidRecords(ConsumerRecords<String, Object> records) {
        Map<TopicPartition, List<ConsumerRecord<String, Object>>> validMap = new HashMap<>();
        int invalidCount = 0;
        for (ConsumerRecord<String, Object> record : records) {
            if (isValidRecord(record)) {
                validMap.computeIfAbsent(
                        new TopicPartition(record.topic(), record.partition()),
                        k -> new ArrayList<>()
                ).add(record);
            } else {
                invalidCount++;
            }
        }
        if (invalidCount > 0) {
            log.info("Отфильтровано {} невалидных Avro записей из топика '{}'", invalidCount, topicName);
        }
        return new ConsumerRecords<>(validMap);
    }

    /**
     * Проверяет валидность записи путём попытки доступа к её значению.
     *
     * @param record запись для проверки
     * @return true, если запись валидна; false иначе
     */
    private boolean isValidRecord(ConsumerRecord<String, Object> record) {
        try {
            Object value = record.value();
            return true;
        } catch (SerializationException e) {
            logInvalidRecord(record, e);
            return false;
        } catch (Exception e) {
            log.warn("Неожиданная ошибка при валидации Avro записи из топика '{}' (offset: {}, partition: {}): {}",
                    topicName, record.offset(), record.partition(), e.getMessage());
            return false;
        }
    }

    /**
     * Логирует и сохраняет запись.
     *
     * @param record запись для обработки
     */
    private void processRecord(ConsumerRecord<String, Object> record) {
        KafkaRecordsManager.addRecord(topicName, record);
    }

    /**
     * Логирует невалидную запись.
     *
     * @param record запись
     * @param e      причина ошибки десериализации
     */
    private void logInvalidRecord(ConsumerRecord<String, Object> record, SerializationException e) {
        log.warn("Невалидная Avro запись в топике '{}' пропущена. Offset: {}, Partition: {}, Timestamp: {}, Key: {}, Error: {}",
                topicName, record.offset(), record.partition(),
                record.timestamp(), record.key(), e.getMessage());
    }
}
