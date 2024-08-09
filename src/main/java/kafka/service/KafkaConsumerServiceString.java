package kafka.service;

import kafka.helper.KafkaListener;
import kafka.helper.KafkaRecordsManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Реализация сервиса потребителя Kafka для строковых данных.
 * Этот класс предоставляет методы для запуска и остановки прослушивания топиков,
 * а также для получения всех записей в формате строк из топиков.
 */
@Slf4j
public class KafkaConsumerServiceString implements KafkaConsumerService {

    /**
     * Запускает прослушивание указанного топика для строковых данных.
     *
     * @param topic   название топика, который нужно слушать
     * @param timeout продолжительность ожидания новых сообщений
     */
    @Override
    public void startListening(String topic, Duration timeout) {
        KafkaListener.startListening(topic, timeout, false);
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
     * Получает все записи из указанного топика в формате строк.
     *
     * @param topic название топика, из которого нужно получить записи
     * @return список записей в формате строк, полученных из топика
     */
    @Override
    public List<ConsumerRecord<String, String>> getAllRecords(String topic) {
        return KafkaRecordsManager.getRecords(topic).stream()
                .flatMap(records -> StreamSupport.stream(records.spliterator(), false))
                .map(record -> (ConsumerRecord<String, String>) record)
                .collect(Collectors.toList());
    }
}
