package com.svedentsov.kafka.factory;

import com.svedentsov.kafka.enums.TopicType;
import com.svedentsov.kafka.helper.KafkaListenerManager;
import com.svedentsov.kafka.helper.KafkaRecordsManager;
import com.svedentsov.kafka.service.*;

import static java.util.Objects.requireNonNull;

/**
 * Фабрика для создания высокоуровневых сервисов для работы с Kafka.
 */
public class KafkaServiceFactory {

    private final ProducerFactory producerFactory;

    /**
     * Создает экземпляр фабрики сервисов Kafka.
     *
     * @param producerFactory Фабрика для создания Kafka Producers. Не может быть {@code null}.
     */
    public KafkaServiceFactory(ProducerFactory producerFactory) {
        this.producerFactory = requireNonNull(producerFactory, "ProducerFactory не может быть null");
    }

    /**
     * Создает сервис для отправки сообщений.
     *
     * @param type Тип контента (STRING или AVRO).
     * @return Экземпляр {@link KafkaProducerService}.
     */
    public KafkaProducerService createProducer(TopicType type) {
        requireNonNull(type, "ContentType для Producer не может быть null");
        return switch (type) {
            case STRING -> new KafkaProducerServiceString(producerFactory.createStringProducer());
            case AVRO -> new KafkaProducerServiceAvro(producerFactory.createAvroProducer());
        };
    }

    /**
     * Создает сервис для получения сообщений.
     *
     * @param type            Тип контента (STRING или AVRO).
     * @param listenerManager Менеджер слушателей для управления жизненным циклом консьюмеров.
     * @param recordsManager  Менеджер для хранения полученных записей.
     * @return Экземпляр {@link KafkaConsumerService}.
     */
    public KafkaConsumerService createConsumer(TopicType type, KafkaListenerManager listenerManager, KafkaRecordsManager recordsManager) {
        requireNonNull(type, "ContentType для Consumer не может быть null");
        requireNonNull(listenerManager, "KafkaListenerManager не может быть null");
        requireNonNull(recordsManager, "KafkaRecordsManager не может быть null");
        return switch (type) {
            case STRING -> new KafkaConsumerServiceString(listenerManager, recordsManager);
            case AVRO -> new KafkaConsumerServiceAvro(listenerManager, recordsManager);
        };
    }
}
