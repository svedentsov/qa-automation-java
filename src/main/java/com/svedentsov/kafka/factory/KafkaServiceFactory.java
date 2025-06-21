package com.svedentsov.kafka.factory;

import com.svedentsov.kafka.enums.ContentType;
import com.svedentsov.kafka.helper.KafkaListenerManager;
import com.svedentsov.kafka.service.*;
import lombok.experimental.UtilityClass;

/**
 * Фабрика для создания экземпляров сервисов Kafka.
 * Этот класс предоставляет статические методы для создания объектов, реализующих интерфейсы
 * {@link KafkaProducerService} и {@link KafkaConsumerService}, в зависимости от переданного типа.
 */
@UtilityClass
public class KafkaServiceFactory {

    /**
     * Создаёт KafkaProducerService по указанному типу формата.
     *
     * @param type STRING_FORMAT или AVRO_FORMAT
     * @return KafkaProducerService-реализация
     * @throws IllegalArgumentException если type == null или неизвестен
     */
    public static KafkaProducerService createProducer(ContentType type) {
        requireNonNull(type, "ContentType для Producer не может быть null");
        return switch (type) {
            case STRING_FORMAT -> new KafkaProducerServiceString();
            case AVRO_FORMAT -> new KafkaProducerServiceAvro();
            default -> throw new IllegalArgumentException("Неизвестный тип Producer: " + type);
        };
    }

    /**
     * Создаёт KafkaConsumerService по типу и уже существующему менеджеру слушателей.
     *
     * @param type    STRING_FORMAT или AVRO_FORMAT
     * @param manager KafkaListenerManager, не null
     * @return KafkaConsumerService-реализация
     * @throws IllegalArgumentException если type или manager == null, либо неизвестен тип
     */
    public static KafkaConsumerService createConsumer(ContentType type, KafkaListenerManager manager) {
        requireNonNull(type, "ContentType для Consumer не может быть null");
        requireNonNull(manager, "KafkaListenerManager не может быть null");
        return switch (type) {
            case STRING_FORMAT -> new KafkaConsumerServiceString(manager);
            case AVRO_FORMAT -> new KafkaConsumerServiceAvro(manager);
            default -> throw new IllegalArgumentException("Неизвестный тип Consumer: " + type);
        };
    }

    /**
     * Приватный помощник для проверки на null с выбросом IllegalArgumentException.
     *
     * @param obj     проверяемый объект
     * @param message сообщение исключения, если obj == null
     */
    private static void requireNonNull(Object obj, String message) {
        if (obj == null) throw new IllegalArgumentException(message);
    }
}
