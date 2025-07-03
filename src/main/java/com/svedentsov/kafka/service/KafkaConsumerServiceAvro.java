package com.svedentsov.kafka.service;

import com.svedentsov.kafka.helper.KafkaListenerManager;
import com.svedentsov.kafka.helper.KafkaRecordsManager;
import com.svedentsov.kafka.helper.KafkaTopicListener;
import com.svedentsov.kafka.processor.RecordProcessorAvro;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Реализация {@link KafkaConsumerService} для работы с записями в формате AVRO.
 * Этот сервис управляет жизненным циклом слушателей для AVRO-топиков и предоставляет
 * доступ к полученным записям, преобразуя их полезную нагрузку в JSON-строку
 * для удобства валидации и обработки в тестах.
 */
@Slf4j
public class KafkaConsumerServiceAvro implements KafkaConsumerService {

    private final KafkaListenerManager listenerManager;
    private final KafkaRecordsManager recordsManager;

    /**
     * Создает экземпляр сервиса для AVRO сообщений.
     *
     * @param listenerManager Менеджер жизненного цикла слушателей. Не может быть {@code null}.
     * @param recordsManager  Менеджер для хранения полученных записей. Не может быть {@code null}.
     */
    public KafkaConsumerServiceAvro(KafkaListenerManager listenerManager, KafkaRecordsManager recordsManager) {
        this.listenerManager = requireNonNull(listenerManager, "KafkaListenerManager не может быть null.");
        this.recordsManager = requireNonNull(recordsManager, "KafkaRecordsManager не может быть null.");
    }

    @Override
    public void startListening(String topic, Duration timeout, KafkaTopicListener.ConsumerStartStrategy startStrategy, Duration lookBackDuration) {
        log.info("Запрос на запуск прослушивания AVRO-топика '{}' со стратегией {}...", topic, startStrategy);
        listenerManager.startListening(topic, timeout, true, this.recordsManager, startStrategy, lookBackDuration);
    }

    @Override
    public void stopListening(String topic) {
        log.info("Запрос на остановку прослушивания AVRO-топика '{}'...", topic);
        if (listenerManager.stopListening(topic)) {
            log.info("Прослушивание AVRO-топика '{}' успешно остановлено.", topic);
        } else {
            log.warn("Не удалось остановить прослушивание AVRO-топика '{}', возможно, он не был запущен.", topic);
        }
    }

    @Override
    public List<ConsumerRecord<String, String>> getAllRecords(String topic) {
        return recordsManager.getRecords(topic).stream()
                .map(this::convertAvroRecordToJsonRecord)
                .collect(Collectors.toList());
    }

    /**
     * Преобразует запись с {@link GenericRecord} в запись со строковым JSON.
     *
     * @param avroRecord Исходная запись.
     * @return Новая запись {@link ConsumerRecord} с JSON-строкой в качестве значения.
     */
    private ConsumerRecord<String, String> convertAvroRecordToJsonRecord(ConsumerRecord<?, ?> avroRecord) {
        Object value = avroRecord.value();
        if (!(value instanceof GenericRecord)) {
            log.error("Ожидался GenericRecord, но получен {} для топика {}", value == null ? "null" : value.getClass().getName(), avroRecord.topic());
            throw new IllegalArgumentException("Неверный тип записи для Avro-потребителя: " + value.getClass().getName());
        }

        GenericRecord genericRecord = (GenericRecord) value;
        String jsonValue = RecordProcessorAvro.genericRecordToJson(genericRecord, genericRecord.getSchema());
        String key = (avroRecord.key() instanceof String) ? (String) avroRecord.key() : null;

        return new ConsumerRecord<>(
                avroRecord.topic(),
                avroRecord.partition(),
                avroRecord.offset(),
                key,
                jsonValue
        );
    }

    @Override
    public <T> List<T> getAllRecordsAs(String topic, Function<String, T> mapper) {
        return getAllRecords(topic).stream()
                .map(ConsumerRecord::value)
                .map(mapper)
                .collect(Collectors.toList());
    }
}
