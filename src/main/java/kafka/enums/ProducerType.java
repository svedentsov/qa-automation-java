package kafka.enums;

/**
 * Перечисление ProducerType определяет типы продюсеров Kafka.
 */
public enum ProducerType {
    /**
     * Продюсер, работающий с записями в формате строки.
     */
    STRING_FORMAT,
    /**
     * Продюсер, работающий с записями в формате Avro.
     */
    AVRO_FORMAT
}
