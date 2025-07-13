package com.svedentsov.kafka.processor;

import com.svedentsov.kafka.config.KafkaConfigListener;
import com.svedentsov.kafka.exception.KafkaListenerException.ProcessingException;
import com.svedentsov.kafka.helper.KafkaRecordsManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import static java.util.Objects.requireNonNull;

/**
 * Реализация {@link RecordProcessor} для обработки записей со строковым значением (String).
 * Основная задача - добавить полученные записи в {@link KafkaRecordsManager}.
 */
@Slf4j
public final class RecordProcessorString implements RecordProcessor<String> {

    private final String topicName;
    private final KafkaConfigListener config;
    private final KafkaRecordsManager recordsManager;

    /**
     * Создает экземпляр обработчика.
     *
     * @param topicName      Имя топика, из которого приходят записи.
     * @param config         Конфигурация слушателя.
     * @param recordsManager Менеджер для сохранения записей.
     */
    public RecordProcessorString(String topicName, KafkaConfigListener config, KafkaRecordsManager recordsManager) {
        this.topicName = requireNonNull(topicName, "Имя топика не может быть null.");
        this.config = requireNonNull(config, "KafkaListenerConfig не может быть null.");
        this.recordsManager = requireNonNull(recordsManager, "KafkaRecordsManager не может быть null.");
    }

    @Override
    public void processRecords(ConsumerRecords<String, String> records) {
        requireNonNull(records, "ConsumerRecords не может быть null.");
        log.debug("Обработка {} строковых записей для топика '{}'...", records.count(), topicName);
        for (ConsumerRecord<String, String> record : records) {
            try {
                recordsManager.addRecord(topicName, record);
            } catch (Exception e) {
                log.error("Не удалось обработать запись из топика '{}', offset={}, partition={}", topicName, record.offset(), record.partition(), e);
                if (config.shouldStopOnError()) {
                    throw new ProcessingException("Критическая ошибка при обработке записи в топике " + topicName, e);
                }
            }
        }
    }
}
