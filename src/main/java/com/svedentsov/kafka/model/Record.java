package com.svedentsov.kafka.model;

import lombok.Data;
import org.apache.avro.Schema;

import java.util.HashMap;
import java.util.Map;

/**
 * Модель записи Kafka, содержащая информацию о топике, ключе, значении и заголовках.
 * Этот класс используется для представления записи, которая может быть отправлена в Kafka.
 */
@Data
public class Record {
    /**
     * Имя топика, в который будет отправлена запись
     */
    private String topic;
    /**
     * Avro-схема, используемая для сериализации записи
     */
    private Schema avroSchema;
    /**
     * Номер партиции, в которую будет отправлена запись
     */
    private Integer partition;
    /**
     * Ключ записи
     */
    private String key;
    /**
     * Значение записи в строковом формате
     */
    private String value;
    /**
     * Значение записи в формате Avro
     */
    private Object avroValue;
    /**
     * Заголовки записи в формате ключ-значение
     */
    private Map<String, Object> headers = new HashMap<>();

    /**
     * Очищает все поля записи, устанавливая их в {@code null} или пустые значения.
     * Этот метод используется для сброса состояния записи после отправки или при необходимости очистки.
     */
    public void clear() {
        this.partition = null;
        this.key = null;
        this.value = null;
        this.avroSchema = null;
        this.avroValue = null;
        this.headers.clear();
    }
}
