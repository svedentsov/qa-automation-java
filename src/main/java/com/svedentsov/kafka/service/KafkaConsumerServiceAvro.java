package com.svedentsov.kafka.service;

import com.svedentsov.kafka.helper.KafkaListenerManager;
import com.svedentsov.kafka.helper.KafkaListenerManager.KafkaStartStrategyType;
import com.svedentsov.kafka.helper.KafkaRecordsManager;
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
 * Реализация {@link KafkaConsumerService} для работы с Kafka топиками,
 * содержащими сообщения в формате Avro.
 * Предоставляет методы для запуска/остановки прослушивания и получения
 * Avro-сообщений, преобразованных в JSON-строки.
 */
@Slf4j
public class KafkaConsumerServiceAvro implements KafkaConsumerService {

    private final KafkaListenerManager listenerManager;
    private final KafkaRecordsManager recordsManager;

    /**
     * Создает новый экземпляр {@code KafkaConsumerServiceAvro}.
     *
     * @param listenerManager Менеджер слушателей Kafka. Не может быть null.
     * @param recordsManager  Менеджер для хранения и доступа к полученным записям. Не может быть null.
     */
    public KafkaConsumerServiceAvro(KafkaListenerManager listenerManager, KafkaRecordsManager recordsManager) {
        this.listenerManager = requireNonNull(listenerManager, "KafkaListenerManager не может быть null.");
        this.recordsManager = requireNonNull(recordsManager, "KafkaRecordsManager не может быть null.");
    }

    /**
     * Запускает прослушивание указанного AVRO-топика.
     *
     * @param topic            Имя топика для прослушивания.
     * @param pollTimeout      Таймаут для операции опроса (poll) брокера Kafka.
     * @param startStrategy    Стратегия, определяющая, с какого смещения начать чтение.
     * @param lookBackDuration Продолжительность, на которую нужно "оглянуться" назад,
     *                         если startStrategy - {@link KafkaStartStrategyType#FROM_TIMESTAMP}.
     *                         Может быть null для других стратегий.
     */
    @Override
    public void startListening(String topic, Duration pollTimeout, KafkaStartStrategyType startStrategy, Duration lookBackDuration) {
        log.info("Запрос на запуск прослушивания AVRO-топика '{}' со стратегией {}...", topic, startStrategy);
        listenerManager.startListening(topic, pollTimeout, true, this.recordsManager, startStrategy, lookBackDuration);
    }

    /**
     * Останавливает прослушивание указанного AVRO-топика.
     *
     * @param topic Имя топика, прослушивание которого нужно прекратить.
     */
    @Override
    public void stopListening(String topic) {
        log.info("Запрос на остановку прослушивания AVRO-топика '{}'...", topic);
        if (listenerManager.stopListening(topic)) {
            log.info("Прослушивание AVRO-топика '{}' успешно остановлено.", topic);
        } else {
            log.warn("Не удалось остановить прослушивание AVRO-топика '{}', возможно, он не был запущен.", topic);
        }
    }

    /**
     * Возвращает все полученные и сохраненные записи из указанного AVRO-топика.
     * Значения Avro-записей преобразуются в JSON-строки.
     *
     * @param topic Имя топика.
     * @return Список записей {@link ConsumerRecord} с ключом и значением в виде строки JSON.
     */
    @Override
    public List<ConsumerRecord<String, String>> getAllRecords(String topic) {
        return recordsManager.getRecords(topic).stream()
                .map(this::convertAvroRecordToJsonRecord)
                .collect(Collectors.toList());
    }

    /**
     * Преобразует {@link ConsumerRecord} с Avro-значением в {@link ConsumerRecord} со строковым JSON-значением.
     *
     * @param avroRecord Входная запись Kafka с Avro-значением.
     * @return Новая запись Kafka с тем же ключом, но со значением в формате JSON-строки.
     * @throws IllegalArgumentException если значение записи не является экземпляром {@link GenericRecord}.
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

    /**
     * Возвращает все полученные записи из AVRO-топика, преобразуя их значения
     * (представленные как JSON-строки) в заданный тип с помощью предоставленной функции-маппера.
     *
     * @param topic  Имя топика.
     * @param mapper Функция для преобразования строкового (JSON) значения записи в объект типа {@code T}.
     * @param <T>    Целевой тип данных.
     * @return Список объектов типа {@code T}.
     */
    @Override
    public <T> List<T> getAllRecordsAs(String topic, Function<String, T> mapper) {
        return getAllRecords(topic).stream()
                .map(ConsumerRecord::value)
                .map(mapper)
                .collect(Collectors.toList());
    }
}
