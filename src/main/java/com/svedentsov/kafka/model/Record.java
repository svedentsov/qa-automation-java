package com.svedentsov.kafka.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.avro.Schema;

import java.util.HashMap;
import java.util.Map;

/**
 * Модель записи Kafka, предназначенная для отправки в топик.
 * Используется как изменяемый объект (builder-like) для формирования записи перед отправкой.
 */
@Data
@NoArgsConstructor
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
     * Номер партиции Kafka. Если null, Kafka сама определит партицию.
     */
    private Integer partition;

    /**
     * Ключ записи Kafka. Может быть null.
     */
    private String key;

    /**
     * Значение записи в строковом формате.
     * Используется, если avroValue равно null.
     */
    private String value;

    /**
     * Значение записи в формате Avro (обычно GenericRecord).
     * Имеет приоритет над полем 'value'.
     */
    private Object avroValue;

    /**
     * Заголовки записи Kafka. Инициализируется пустой картой.
     */
    private Map<String, Object> headers = new HashMap<>();

    /**
     * Очищает все поля записи, устанавливая их в null или пустые значения.
     * Позволяет переиспользовать объект Record для создания новой записи.
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
}
