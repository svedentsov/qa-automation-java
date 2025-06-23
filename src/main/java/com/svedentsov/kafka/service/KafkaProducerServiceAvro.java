package com.svedentsov.kafka.service;

import com.svedentsov.kafka.factory.ProducerFactoryDefault;
import com.svedentsov.kafka.model.Record;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;

import static com.svedentsov.kafka.utils.ValidationUtils.requireNonNull;

/**
 * Реализация KafkaProducerService для Avro-сообщений.
 */
public class KafkaProducerServiceAvro extends KafkaProducerServiceAbstract<GenericRecord> {

    @Override
    protected void validateRecord(Record record) {
        super.validateRecord(record);
        requireNonNull(record.getAvroValue(), "Avro-value не может быть null");
        if (record.getAvroValue() instanceof GenericRecord) {
            return;
        }
        throw new IllegalArgumentException("Неверный тип Avro-value: ожидался GenericRecord, получен " + record.getAvroValue().getClass().getName());
    }

    @Override
    protected GenericRecord getValueFromRecord(Record record) {
        return (GenericRecord) record.getAvroValue();
    }

    @Override
    protected KafkaProducer<String, GenericRecord> getProducer(String topic) {
        return new ProducerFactoryDefault().createAvroProducer(topic);
    }
}
