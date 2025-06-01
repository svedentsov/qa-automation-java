package com.svedentsov.kafka.enums;

/**
 * Перечисление определяет типы консюмеров и продюсеров Kafka.
 */
public enum ContentType {
    /**
     * Работающий с записями в формате строки.
     */
    STRING_FORMAT,
    /**
     * Работающий с записями в формате Avro.
     */
    AVRO_FORMAT
}
