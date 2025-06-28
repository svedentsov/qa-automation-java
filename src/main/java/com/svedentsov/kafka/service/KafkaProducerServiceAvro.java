package com.svedentsov.kafka.service;

import com.svedentsov.kafka.model.Record;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;

import static java.util.Objects.requireNonNull;

/**
 * Реализация {@link KafkaProducerService} для отправки сообщений в формате Avro ({@link GenericRecord}).
 */
public class KafkaProducerServiceAvro extends KafkaProducerServiceAbstract<GenericRecord> {

    /**
     * Создает сервис, используя предоставленный продюсер для Avro.
     *
     * @param producer настроенный экземпляр {@link KafkaProducer} для отправки {@link GenericRecord}.
     */
    public KafkaProducerServiceAvro(KafkaProducer<String, GenericRecord> producer) {
        super(producer);
    }

    @Override
    protected void validateRecord(Record record) {
        super.validateRecord(record);
        requireNonNull(record.getAvroValue(), "Поле Avro-value в записи не может быть null.");
    }

    @Override
    protected GenericRecord getValueFromRecord(Record record) {
        Object avroValue = record.getAvroValue();
        if (avroValue instanceof GenericRecord) {
            return (GenericRecord) avroValue;
        }
        throw new IllegalArgumentException("Неверный тип Avro-value: ожидался GenericRecord, но получен "
                + (avroValue != null ? avroValue.getClass().getName() : "null"));
    }
}
