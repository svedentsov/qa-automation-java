package com.svedentsov.kafka.service;

import com.svedentsov.kafka.model.Record;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;

import static java.util.Objects.requireNonNull;

/**
 * Реализация {@link KafkaProducerService} для отправки сообщений в формате Avro ({@link GenericRecord}).
 * Является конкретной реализацией шаблонного метода, определенного в {@link KafkaProducerServiceAbstract}.
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

    /**
     * {@inheritDoc}
     * <p>Дополнительно проверяет, что Avro-значение в записи не является {@code null}
     * и имеет корректный тип {@link GenericRecord}.
     */
    @Override
    protected void validateRecord(Record record) {
        super.validateRecord(record);
        Object avroValue = record.getAvroValue();
        requireNonNull(avroValue, "Avro-значение (avroValue) в записи не может быть null.");
        if (!(avroValue instanceof GenericRecord)) {
            throw new IllegalArgumentException("Неверный тип Avro-значения: ожидался GenericRecord, но получен " + avroValue.getClass().getName());
        }
    }

    /**
     * {@inheritDoc}
     * <p>Возвращает значение в формате {@link GenericRecord} из записи.
     * Выполняет безопасное приведение типа, так как проверка была произведена на этапе валидации.
     */
    @Override
    protected GenericRecord getValueFromRecord(Record record) {
        return (GenericRecord) record.getAvroValue();
    }
}
