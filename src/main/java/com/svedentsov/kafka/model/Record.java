package com.svedentsov.kafka.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.avro.Schema;

import java.util.HashMap;
import java.util.Map;

/**
 * Модель данных для представления записи Kafka в обобщенном виде.
 * Этот класс является изменяемым (mutable) и предназначен для многократного использования,
 * например, в тестовых фреймворках, где объект может быть очищен
 * с помощью метода {@link #clear()} и заполнен новыми данными.
 */
@Data
@NoArgsConstructor
public class Record {

    /**
     * Имя топика, из которого была получена запись.
     */
    private String topic;
    /**
     * Схема Avro, если запись в формате Avro.
     */
    private Schema avroSchema;
    /**
     * Номер партиции, из которой была получена запись.
     */
    private Integer partition;
    /**
     * Ключ записи.
     */
    private String key;
    /**
     * Значение записи в виде строки (для не-Avro формата).
     */
    private String value;
    /**
     * Значение записи в виде объекта Avro (например, GenericRecord).
     */
    private Object avroValue;
    /**
     * Заголовки (headers) записи.
     */
    private Map<String, Object> headers = new HashMap<>();

    /**
     * Сбрасывает все поля объекта в их начальное состояние (null или пустая коллекция).
     * Полезно для переиспользования одного и того же объекта {@code Record}.
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
