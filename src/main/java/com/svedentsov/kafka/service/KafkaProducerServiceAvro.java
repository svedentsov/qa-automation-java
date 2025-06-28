package com.svedentsov.kafka.service;

import com.svedentsov.kafka.factory.ProducerFactory;
import com.svedentsov.kafka.model.Record;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;

import static java.util.Objects.requireNonNull;

/**
 * Реализация {@link KafkaProducerService} для отправки сообщений в формате Avro.
 */
public class KafkaProducerServiceAvro extends KafkaProducerServiceAbstract<GenericRecord> {

    /**
     * Создает сервис, используя предоставленную фабрику продюсеров.
     *
     * @param producerFactory фабрика для создания Kafka продюсеров.
     */
    public KafkaProducerServiceAvro(ProducerFactory producerFactory) {
        super(producerFactory);
    }

    @Override
    protected void validateRecord(Record record) {
        super.validateRecord(record);
        requireNonNull(record.getAvroValue(), "Avro-value не может быть null.");
    }

    @Override
    protected GenericRecord getValueFromRecord(Record record) {
        Object avroValue = record.getAvroValue();
        if (avroValue instanceof GenericRecord) {
            return (GenericRecord) avroValue;
        }
        throw new IllegalArgumentException("Неверный тип Avro-value: ожидался GenericRecord, но получен "
                + avroValue.getClass().getName());
    }

    @Override
    protected KafkaProducer<String, GenericRecord> getProducer(String topic) {
        return producerFactory.createAvroProducer(topic);
    }
}