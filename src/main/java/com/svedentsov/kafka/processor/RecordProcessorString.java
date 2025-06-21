package com.svedentsov.kafka.processor;

import com.svedentsov.kafka.config.KafkaListenerConfig;
import com.svedentsov.kafka.helper.KafkaRecordsManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

/**
 * Обработчик строковых записей Kafka.
 */
@Slf4j
@RequiredArgsConstructor
public class RecordProcessorString implements RecordProcessor<String> {

    private final String topicName;
    private final KafkaListenerConfig config;

    /**
     * Обрабатывает пакет строковых записей, сохраняет и логирует ошибки.
     *
     * @param records набор записей для обработки
     */
    @Override
    public void processRecords(ConsumerRecords<String, String> records) {
        int processedCount = 0;
        int errorCount = 0;
        for (ConsumerRecord<String, String> record : records) {
            try {
                processRecord(record);
                processedCount++;
            } catch (Exception e) {
                errorCount++;
                log.warn("Ошибка при обработке строковой записи из топика '{}' (offset: {}, partition: {}): {}",
                        topicName, record.offset(), record.partition(), e.getMessage());
                if (config.shouldStopOnError()) {
                    throw new RuntimeException("Критическая ошибка обработки записи", e);
                }
            }
        }
        if (config.isEnableMetrics()) {
            log.debug("Обработано записей: {}, ошибок: {} для топика '{}'",
                    processedCount, errorCount, topicName);
        }
    }

    /**
     * Сохраняет запись в менеджер.
     *
     * @param record запись для сохранения
     */
    private void processRecord(ConsumerRecord<String, String> record) {
        KafkaRecordsManager.addRecord(topicName, record);
    }
}
