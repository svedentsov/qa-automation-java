package com.svedentsov.kafka.factory;

import com.svedentsov.kafka.config.KafkaConfigProvider;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;

import static com.svedentsov.kafka.utils.ValidationUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * Стандартная реализация {@link ConsumerFactory}, которая создает консьюмеры
 * для строковых или Avro сообщений на основе конфигурации из {@link KafkaConfigProvider}.
 */
@Slf4j
public class ConsumerFactoryDefault implements ConsumerFactory {

    private final KafkaConfigProvider configProvider;

    /**
     * Создает экземпляр ConsumerFactoryDefault с указанным провайдером конфигураций.
     *
     * @param configProvider провайдер конфигураций Kafka, не может быть null.
     */
    public ConsumerFactoryDefault(KafkaConfigProvider configProvider) {
        this.configProvider = requireNonNull(configProvider, "DefaultKafkaConfigProvider не может быть null.");
    }

    /**
     * Создает новый экземпляр {@link KafkaConsumer} для строковых сообщений.
     *
     * @param topicName Имя топика, для которого создается консьюмер.
     * @return Новый экземпляр {@link KafkaConsumer} со строковыми ключами и значениями.
     */
    @Override
    public KafkaConsumer<String, String> createStringConsumer(String topicName) {
        requireNonBlank(topicName, "Имя топика не может быть null или пустым.");
        return createConsumerInternal(topicName, StringDeserializer.class, StringDeserializer.class);
    }

    /**
     * Создает новый экземпляр {@link KafkaConsumer} для Avro сообщений.
     *
     * @param topicName Имя топика, для которого создается консьюмер.
     * @return Новый экземпляр {@link KafkaConsumer} со строковыми ключами и Avro значениями.
     */
    @Override
    public KafkaConsumer<String, Object> createAvroConsumer(String topicName) {
        requireNonBlank(topicName, "Имя топика не может быть null или пустым.");
        return createConsumerInternal(topicName, StringDeserializer.class, KafkaAvroDeserializer.class);
    }

    /**
     * Обобщенный внутренний метод для создания нового экземпляра {@link KafkaConsumer}.
     */
    private <K, V> KafkaConsumer<K, V> createConsumerInternal(String topicName, Class<?> keyDeserializer, Class<?> valueDeserializer) {
        Properties props = configProvider.getConsumerConfig(topicName);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer.getName());
        log.info("Создание нового KafkaConsumer для топика '{}' [KeyDeserializer: {}, ValueDeserializer: {}]", topicName, keyDeserializer.getSimpleName(), valueDeserializer.getSimpleName());
        return new KafkaConsumer<>(props);
    }

    @Override
    public void close() {
        log.debug("ConsumerFactoryDefault.close() вызван. Нет ресурсов для освобождения на уровне фабрики.");
    }
}
