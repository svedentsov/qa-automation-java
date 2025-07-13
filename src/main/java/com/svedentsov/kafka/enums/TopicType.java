package com.svedentsov.kafka.enums;

/**
 * Перечисление определяет типы консюмеров и продюсеров Kafka.
 */
public enum TopicType {
    /**
     * Работающий с записями, где ключ и значение представлены строками.
     */
    STRING,
    /**
     * Работающий с записями в формате Avro, где ключ строка и значение - Avro GenericRecord.
     */
    AVRO
}
