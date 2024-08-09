package kafka.service;

import kafka.helper.KafkaListener;
import kafka.helper.KafkaRecordsManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Реализация сервиса потребителя Kafka для данных в формате Avro.
 * Этот класс предоставляет методы для запуска и остановки прослушивания топиков,
 * а также для получения всех записей в формате Avro из топиков и преобразования их в строки.
 */
@Slf4j
public class KafkaConsumerServiceAvro implements KafkaConsumerService {

    /**
     * Запускает прослушивание указанного топика для данных в формате Avro.
     *
     * @param topic   название топика, который нужно слушать
     * @param timeout продолжительность ожидания новых сообщений
     */
    @Override
    public void startListening(String topic, Duration timeout) {
        KafkaListener.startListening(topic, timeout, true);
    }

    /**
     * Останавливает прослушивание указанного топика.
     *
     * @param topic название топика, для которого нужно остановить прослушивание
     */
    @Override
    public void stopListening(String topic) {
        KafkaListener.stopListening(topic);
    }

    /**
     * Получает все записи из указанного топика и преобразует их в строки.
     *
     * @param topic название топика, из которого нужно получить записи
     * @return список записей в формате строк, полученных из топика
     */
    @Override
    public List<ConsumerRecord<String, String>> getAllRecords(String topic) {
        return KafkaRecordsManager.getRecords(topic).stream()
                .flatMap(records -> {
                    if (!(records instanceof ConsumerRecords<?, ?>)) {
                        log.error("Неверный тип записей для топика {}: {}", topic, records.getClass().getName());
                        return StreamSupport.stream(new ConsumerRecords<String, GenericRecord>(null).spliterator(), false);
                    }
                    return StreamSupport.stream(((ConsumerRecords<String, GenericRecord>) records).spliterator(), false);
                })
                .map(this::convertToStringRecord)
                .collect(Collectors.toList());
    }

    /**
     * Преобразует запись Avro в запись строкового формата.
     *
     * @param avroRecord запись в формате Avro
     * @return запись в формате строк
     */
    private ConsumerRecord<String, String> convertToStringRecord(ConsumerRecord<String, GenericRecord> avroRecord) {
        String valueString = avroRecord.value() != null ? avroRecord.value().toString() : "";
        return new ConsumerRecord<>(avroRecord.topic(),
                avroRecord.partition(),
                avroRecord.offset(),
                avroRecord.key(),
                valueString);
    }
}