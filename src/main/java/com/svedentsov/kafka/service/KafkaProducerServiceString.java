package com.svedentsov.kafka.service;

import com.svedentsov.kafka.model.Record;
import org.apache.kafka.clients.producer.KafkaProducer;

import static java.util.Objects.requireNonNull;

/**
 * Реализация {@link KafkaProducerService} для отправки сообщений, где значением является {@link String}.
 * Является конкретной реализацией шаблонного метода, определенного в {@link KafkaProducerServiceAbstract}.
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

    /**
     * {@inheritDoc}
     * <p>
     * Дополнительно проверяет, что строковое значение в записи не является {@code null}.
     */
    @Override
    protected void validateRecord(Record record) {
        super.validateRecord(record);
        requireNonNull(record.getValue(), "Строковое значение (value) в записи не может быть null.");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Возвращает строковое значение из записи.
     */
    @Override
    protected String getValueFromRecord(Record record) {
        return record.getValue();
    }
}
