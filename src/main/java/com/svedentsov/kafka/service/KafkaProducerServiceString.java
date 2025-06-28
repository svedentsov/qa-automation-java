package com.svedentsov.kafka.service;

import com.svedentsov.kafka.factory.ProducerFactory;
import com.svedentsov.kafka.model.Record;
import org.apache.kafka.clients.producer.KafkaProducer;

import static java.util.Objects.requireNonNull;

/**
 * Реализация {@link KafkaProducerService} для отправки строковых сообщений.
 */
public class KafkaProducerServiceString extends KafkaProducerServiceAbstract<String> {

    /**
     * Создает сервис, используя предоставленную фабрику продюсеров.
     *
     * @param producerFactory фабрика для создания Kafka продюсеров.
     */
    public KafkaProducerServiceString(ProducerFactory producerFactory) {
        super(producerFactory);
    }

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
        return producerFactory.createStringProducer(topic);
    }
}
