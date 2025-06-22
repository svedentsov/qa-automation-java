package com.svedentsov.kafka.processor;

import com.svedentsov.kafka.config.KafkaListenerConfig;
import com.svedentsov.kafka.exception.KafkaListenerException.ProcessingException;
import com.svedentsov.kafka.helper.KafkaRecordsManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.Objects;

/**
 * Обработка строковых сообщений: сохраняет записи в KafkaRecordsManager.
 */
@Slf4j
@RequiredArgsConstructor
public class RecordProcessorString implements RecordProcessor<String> {

    private final String topicName;
    private final KafkaListenerConfig config;

    /**
     * Обрабатывает пакет строковых записей, сохраняет их и логирует ошибки.
     *
     * @param records набор записей для обработки
     */
    @Override
    public void processRecords(ConsumerRecords<String, String> records) {
        Objects.requireNonNull(records, "records не может быть null.");
        int processedCount = 0;
        int errorCount = 0;
        for (ConsumerRecord<String, String> record : records) {
            if (record == null) {
                log.warn("Обнаружена null-запись в топике '{}', пропуск.", topicName);
                errorCount++;
                continue;
            }
            try {
                KafkaRecordsManager.addRecord(topicName, record);
                processedCount++;
            } catch (Exception e) {
                errorCount++;
                log.warn("Ошибка при обработке записи из топика '{}', offset={}, partition={}: {}", topicName, record.offset(), record.partition(), e.getMessage(), e);
                if (config.shouldStopOnError()) {
                    throw new ProcessingException("Критическая ошибка при обработке записи в топике " + topicName, e);
                }
            }
        }
        if (config.isEnableMetrics()) {
            log.debug("Обработано {} строковых записей (ошибок: {}) для топика '{}'", processedCount, errorCount, topicName);
        }
    }
}
