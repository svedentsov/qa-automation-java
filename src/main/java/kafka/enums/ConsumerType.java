package kafka.enums;

/**
 * Перечисление ConsumerType определяет типы консюмеров Kafka.
 */
public enum ConsumerType {
    /**
     * Консюмер, работающий с записями в формате строки.
     */
    STRING_FORMAT,
    /**
     * Консюмер, работающий с записями в формате Avro.
     */
    AVRO_FORMAT
}
