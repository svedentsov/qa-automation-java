package kafka.factory;

import kafka.enums.ContentType;
import kafka.service.*;

/**
 * Фабрика для создания экземпляров сервисов Kafka.
 * Этот класс предоставляет статические методы для создания объектов, реализующих интерфейсы
 * {@link KafkaProducerService} и {@link KafkaConsumerService}, в зависимости от переданного типа.
 */
public class KafkaServiceFactory {

    /**
     * Создает экземпляр {@link KafkaProducerService} на основе указанного типа продюсера.
     *
     * @param type тип продюсера, определяющий формат данных, который будет использоваться
     * @return экземпляр {@link KafkaProducerService} соответствующий указанному типу
     * @throws IllegalArgumentException если передан неизвестный тип продюсера
     */
    public static KafkaProducerService createProducer(ContentType type) {
        return switch (type) {
            case STRING_FORMAT -> new KafkaProducerServiceString();
            case AVRO_FORMAT -> new KafkaProducerServiceAvro();
            default -> throw new IllegalArgumentException("Неизвестный тип Producer: " + type);
        };
    }

    /**
     * Создает экземпляр {@link KafkaConsumerService} на основе указанного типа потребителя.
     *
     * @param type тип потребителя, определяющий формат данных, который будет использоваться
     * @return экземпляр {@link KafkaConsumerService} соответствующий указанному типу
     * @throws IllegalArgumentException если передан неизвестный тип потребителя
     */
    public static KafkaConsumerService createConsumer(ContentType type) {
        return switch (type) {
            case STRING_FORMAT -> new KafkaConsumerServiceString();
            case AVRO_FORMAT -> new KafkaConsumerServiceAvro();
            default -> throw new IllegalArgumentException("Неизвестный тип Consumer: " + type);
        };
    }
}
