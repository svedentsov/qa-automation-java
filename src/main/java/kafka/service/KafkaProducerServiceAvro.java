package kafka.service;

import kafka.model.Record;
import kafka.pool.KafkaClientPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;

import java.util.Objects;

/**
 * Реализация сервиса продюсера Kafka для данных в формате Avro.
 * Этот класс отвечает за отправку записей в формате Avro в Kafka.
 */
@Slf4j
public class KafkaProducerServiceAvro implements KafkaProducerService {
    /**
     * Отправляет строковую запись в Kafka.
     *
     * @param message запись, которая содержит топик, раздел, ключ и значение в формате Avro
     * @throws IllegalArgumentException если топик или значение записи не установлены
     * @throws RuntimeException         если произошла ошибка при отправке записи
     */
    @Override
    public void sendRecord(Record message) {

        // Проверяем наличие топика и значения записи
        if (message.getTopic() == null || message.getAvroValue() == null) {
            throw new IllegalArgumentException("Топик и значение записи должны быть установлены");
        }

        // Сериализуем Avro-запись
        GenericRecord avroRecord = (GenericRecord) message.getAvroValue();

        // Создаем ProducerRecord для отправки в Kafka
        ProducerRecord<String, Object> record = new ProducerRecord<>(
                message.getTopic(), message.getPartition(), message.getKey(), avroRecord);

        // Добавляем заголовки, если они есть
        message.getHeaders().forEach((key, value) ->
                record.headers().add(new RecordHeader(key, value.toString().getBytes())));

        // Отправляем запись с использованием KafkaProducer
        try (KafkaProducer<String, Object> producer = KafkaClientPool.getAvroProducer(message.getTopic())) {
            // Используем асинхронный метод send(record) без блокировки потока
            producer.send(record, (metadata, exception) -> {
                if (Objects.nonNull(exception)) {
                    log.error("Не удалось отправить запись", exception);
                    throw new RuntimeException("Не удалось отправить запись", exception);
                }
                log.info("Запись отправлена: {}", record);
            });
        } catch (Exception e) {
            log.error("Не удалось отправить запись: {}", message, e);
            throw new RuntimeException("Не удалось отправить запись", e);
        } finally {
            message.clear(); // Обязательная очистка записи после отправки
        }
    }
}
