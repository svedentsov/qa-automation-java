package com.svedentsov.kafka.factory;

import com.svedentsov.kafka.config.DefaultKafkaConfigProvider;
import com.svedentsov.kafka.config.KafkaListenerConfig;
import com.svedentsov.kafka.enums.ContentType;
import com.svedentsov.kafka.helper.KafkaListenerManager;
import com.svedentsov.kafka.service.*;

import static java.util.Objects.requireNonNull;

/**
 * Фабрика для создания сервисов Kafka (ProducerService и ConsumerService).
 * Управляет жизненным циклом базовых фабрик ProducerFactory и ConsumerFactory,
 * обеспечивая их переиспользование.
 */
public class KafkaServiceFactory {

    private final ProducerFactory producerFactory;
    private final ConsumerFactory consumerFactory;

    /**
     * Создает экземпляр KafkaServiceFactory с указанным провайдером конфигураций.
     *
     * @param configProvider провайдер конфигураций Kafka, не может быть null.
     */
    public KafkaServiceFactory(DefaultKafkaConfigProvider configProvider) {
        this.producerFactory = new ProducerFactoryDefault(configProvider);
        this.consumerFactory = new ConsumerFactoryDefault(configProvider);
    }

    /**
     * Создает новый экземпляр KafkaProducerService на основе указанного типа контента.
     *
     * @param type тип контента (STRING_FORMAT, AVRO_FORMAT).
     * @return настроенный KafkaProducerService.
     * @throws IllegalArgumentException если тип контента не поддерживается.
     */
    public KafkaProducerService createProducer(ContentType type) {
        requireNonNull(type, "ContentType для Producer не может быть null");
        return switch (type) {
            case STRING_FORMAT -> new KafkaProducerServiceString(producerFactory.createStringProducer());
            case AVRO_FORMAT -> new KafkaProducerServiceAvro(producerFactory.createAvroProducer());
        };
    }

    /**
     * Создаёт {@link KafkaConsumerService} по типу контента и уже существующему менеджеру слушателей.
     *
     * @param type    тип контента ({@link ContentType#STRING_FORMAT} или {@link ContentType#AVRO_FORMAT}).
     * @param manager экземпляр {@link KafkaListenerManager}, не может быть {@code null}.
     * @return реализация {@link KafkaConsumerService}, соответствующая указанному типу.
     * @throws IllegalArgumentException если {@code type} или {@code manager} равен {@code null}, либо тип неизвестен.
     */
    public KafkaConsumerService createConsumer(ContentType type, KafkaListenerManager manager) {
        requireNonNull(type, "ContentType для Consumer не может быть null");
        requireNonNull(manager, "KafkaListenerManager не может быть null");
        return switch (type) {
            case STRING_FORMAT -> new KafkaConsumerServiceString(manager);
            case AVRO_FORMAT -> new KafkaConsumerServiceAvro(manager);
        };
    }

    /**
     * Создаёт {@link KafkaConsumerService} по типу контента и конфигурации слушателя.
     * В этом случае {@link KafkaConsumerService} самостоятельно создаст и будет управлять своим экземпляром {@link KafkaListenerManager}.
     *
     * @param type   тип контента ({@link ContentType#STRING_FORMAT} или {@link ContentType#AVRO_FORMAT}).
     * @param config конфигурация слушателя {@link KafkaListenerConfig}, не может быть {@code null}.
     * @return реализация {@link KafkaConsumerService}, соответствующая указанному типу.
     * @throws IllegalArgumentException если {@code type} или {@code config} равен {@code null}, либо тип неизвестен.
     */
    public KafkaConsumerService createConsumer(ContentType type, KafkaListenerConfig config) {
        requireNonNull(type, "ContentType для Consumer не может быть null");
        requireNonNull(config, "KafkaListenerConfig не может быть null");
        return switch (type) {
            case STRING_FORMAT -> new KafkaConsumerServiceString(config);
            case AVRO_FORMAT -> new KafkaConsumerServiceAvro(config);
        };
    }
}
