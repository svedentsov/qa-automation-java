package com.svedentsov.kafka.factory;

import com.svedentsov.kafka.enums.ContentType;
import com.svedentsov.kafka.helper.KafkaListenerManager;
import com.svedentsov.kafka.helper.KafkaRecordsManager;
import com.svedentsov.kafka.service.*;

import static java.util.Objects.requireNonNull;

/**
 * Фабрика-агрегатор для создания высокоуровневых сервисов Kafka (Producer и Consumer).
 * Эта фабрика инкапсулирует логику создания различных типов сервисов
 * для взаимодействия с Kafka, абстрагируясь от конкретных реализаций.
 * Она использует {@link ProducerFactory} и {@link ConsumerFactory} для делегирования
 * создания низкоуровневых клиентов, следуя принципу единой ответственности.
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
     * Создает сервис для отправки сообщений в Kafka (Producer).
     *
     * @param type Формат контента сообщений (например, STRING, AVRO). Не может быть {@code null}.
     * @return Экземпляр {@link KafkaProducerService}, сконфигурированный для указанного типа контента.
     * @throws NullPointerException если {@code type} равен {@code null}.
     */
    public KafkaProducerService createProducer(ContentType type) {
        requireNonNull(type, "ContentType для Producer не может быть null");
        return switch (type) {
            case STRING_FORMAT -> new KafkaProducerServiceString(producerFactory.createStringProducer());
            case AVRO_FORMAT -> new KafkaProducerServiceAvro(producerFactory.createAvroProducer());
        };
    }

    /**
     * Создает сервис для чтения сообщений из Kafka (Consumer).
     * Этот метод является центральной точкой для сборки консьюмер-сервиса,
     * передавая ему все необходимые зависимости для работы.
     *
     * @param type            Формат контента сообщений (например, STRING, AVRO). Не может быть {@code null}.
     * @param listenerManager Менеджер жизненного цикла слушателей Kafka. Не может быть {@code null}.
     * @param recordsManager  Менеджер для хранения полученных записей. Не может быть {@code null}.
     * @return Экземпляр {@link KafkaConsumerService}, готовый к работе.
     * @throws NullPointerException если один из аргументов равен {@code null}.
     */
    public KafkaConsumerService createConsumer(ContentType type, KafkaListenerManager listenerManager, KafkaRecordsManager recordsManager) {
        requireNonNull(type, "ContentType для Consumer не может быть null");
        requireNonNull(listenerManager, "KafkaListenerManager не может быть null");
        requireNonNull(recordsManager, "KafkaRecordsManager не может быть null");
        return switch (type) {
            case STRING_FORMAT -> new KafkaConsumerServiceString(listenerManager, recordsManager);
            case AVRO_FORMAT -> new KafkaConsumerServiceAvro(listenerManager, recordsManager);
        };
    }
}
