package com.svedentsov.kafka.model;

import lombok.Data;
import org.apache.avro.Schema;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Модель записи Kafka, предназначенная для отправки в топик.
 * Содержит информацию: топик, ключ, значение (String или Avro), партицию и заголовки.
 */
@Data
public class Record {
    /**
     * Имя топика Kafka, в который будет отправлена запись.
     */
    private String topic;
    /**
     * Avro-схема, используемая для сериализации записи, когда avroValue не null.
     */
    private Schema avroSchema;
    /**
     * Номер партиции Kafka. Если не указан, Kafka сама определит партицию.
     */
    private Integer partition;
    /**
     * Ключ записи Kafka. Может быть null.
     */
    private String key;
    /**
     * Значение записи в строковом формате.
     */
    private String value;
    /**
     * Значение записи в формате Avro.
     */
    private Object avroValue;
    /**
     * Заголовки записи Kafka в формате "ключ-значение".
     */
    private Map<String, Object> headers = new HashMap<>();

    /**
     * Очищает все поля записи, устанавливая их в null или пустые значения.
     * Используется для сброса состояния объекта Record после отправки.
     */
    public void clear() {
        this.topic = null;
        this.partition = null;
        this.key = null;
        this.value = null;
        this.avroSchema = null;
        this.avroValue = null;
        this.headers.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Record record)) return false;
        return Objects.equals(topic, record.topic)
                && Objects.equals(avroSchema, record.avroSchema)
                && Objects.equals(partition, record.partition)
                && Objects.equals(key, record.key)
                && Objects.equals(value, record.value)
                && Objects.equals(avroValue, record.avroValue)
                && Objects.equals(headers, record.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic, avroSchema, partition, key, value, avroValue, headers);
    }
}
