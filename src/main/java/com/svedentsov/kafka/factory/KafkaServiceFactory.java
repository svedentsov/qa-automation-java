package com.svedentsov.kafka.factory;

import com.svedentsov.kafka.config.KafkaListenerConfig;
import com.svedentsov.kafka.enums.ContentType;
import com.svedentsov.kafka.helper.KafkaListenerManager;
import com.svedentsov.kafka.service.*;
import org.apache.kafka.clients.producer.KafkaProducer;

import static java.util.Objects.requireNonNull;

/**
 * Фабрика для создания экземпляров сервисов Kafka (продюсеров и консюмеров).
 * Этот класс инкапсулирует логику "сборки" сервисов, управляя внедрением зависимостей.
 * Он использует {@link ProducerFactory} для получения необходимых {@link KafkaProducer}
 * и передает их в конкретные реализации сервисов, такие как {@link KafkaProducerServiceString}.
 */
public class KafkaServiceFactory {

    private final ProducerFactory producerFactory;

    /**
     * Создает фабрику сервисов с указанной фабрикой продюсеров.
     *
     * @param producerFactory фабрика, ответственная за жизненный цикл {@link KafkaProducer}.
     */
    public KafkaServiceFactory(ProducerFactory producerFactory) {
        this.producerFactory = requireNonNull(producerFactory, "ProducerFactory не может быть null.");
    }

    /**
     * Создаёт полностью сконфигурированный {@link KafkaProducerService} для указанного типа контента.
     *
     * @param type Тип контента ({@link ContentType#STRING_FORMAT} или {@link ContentType#AVRO_FORMAT}).
     * @return Готовая к использованию реализация {@link KafkaProducerService}.
     * @throws IllegalArgumentException если {@code type} равен {@code null} или является неизвестным.
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
