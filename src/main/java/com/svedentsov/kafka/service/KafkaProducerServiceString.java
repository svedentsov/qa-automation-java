package com.svedentsov.kafka.service;

import com.svedentsov.kafka.factory.ProducerFactoryDefault;
import com.svedentsov.kafka.model.Record;
import org.apache.kafka.clients.producer.KafkaProducer;

import static com.svedentsov.kafka.utils.ValidationUtils.requireNonNull;

/**
 * Реализация KafkaProducerService для строковых сообщений.
 */
public class KafkaProducerServiceString extends KafkaProducerServiceAbstract<String> {

    @Override
    protected void validateRecord(Record record) {
        super.validateRecord(record);
        requireNonNull(record.getValue(), "String-value не может быть null");
    }

    @Override
    protected String getValueFromRecord(Record record) {
        return record.getValue();
    }

    @Override
    protected KafkaProducer<String, String> getProducer(String topic) {
        return new ProducerFactoryDefault().createStringProducer(topic);
    }
}
