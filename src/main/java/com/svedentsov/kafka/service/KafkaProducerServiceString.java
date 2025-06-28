package com.svedentsov.kafka.service;

import com.svedentsov.kafka.model.Record;
import org.apache.kafka.clients.producer.KafkaProducer;

import static java.util.Objects.requireNonNull;

/**
 * Реализация {@link KafkaProducerService} для отправки сообщений в строковом формате.
 */
public class KafkaProducerServiceString extends KafkaProducerServiceAbstract<String> {

    /**
     * Создает сервис, используя предоставленный продюсер для строковых данных.
     *
     * @param producer настроенный экземпляр {@link KafkaProducer} для отправки {@link String}.
     */
    public KafkaProducerServiceString(KafkaProducer<String, String> producer) {
        super(producer);
    }

    @Override
    protected void validateRecord(Record record) {
        super.validateRecord(record);
        requireNonNull(record.getValue(), "Поле String-value в записи не может быть null.");
    }

    @Override
    protected String getValueFromRecord(Record record) {
        return record.getValue();
    }
}
