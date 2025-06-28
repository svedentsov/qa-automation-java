package com.svedentsov.kafka.factory;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Реализация {@link ProducerFactory} по умолчанию.
 * Кэширует созданные экземпляры {@link KafkaProducer} для повторного использования,
 * чтобы избежать накладных расходов на создание новых соединений с Kafka.
 * Является потокобезопасной.
 */
@Slf4j
public class ProducerFactoryDefault implements ProducerFactory {

    private final Map<String, KafkaProducer<String, String>> stringProducers = new ConcurrentHashMap<>();
    private final Map<String, KafkaProducer<String, GenericRecord>> avroProducers = new ConcurrentHashMap<>();
    private final Function<String, Properties> configProvider;

    public ProducerFactoryDefault() {
        this(topic -> {
            Properties props = new Properties();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            return props;
        });
    }

    /**
     * Создает фабрику, используя предоставленный провайдер конфигураций.
     *
     * @param configProvider функция, которая принимает имя топика и возвращает {@link Properties} для продюсера.
     *                       Не может быть {@code null}.
     */
    public ProducerFactoryDefault(Function<String, Properties> configProvider) {
        this.configProvider = requireNonNull(configProvider, "Провайдер конфигураций не может быть null.");
    }

    @Override
    public KafkaProducer<String, String> createStringProducer(String topicName) {
        requireNonNull(topicName, "Имя топика не может быть null или пустым.");
        return stringProducers.computeIfAbsent(topicName,
                key -> createProducerInternal(key, StringSerializer.class, StringSerializer.class));
    }

    @Override
    public KafkaProducer<String, GenericRecord> createAvroProducer(String topicName) {
        requireNonNull(topicName, "Имя топика не может быть null или пустым.");
        return avroProducers.computeIfAbsent(topicName,
                key -> createProducerInternal(key, StringSerializer.class, KafkaAvroSerializer.class));
    }

    /**
     * Внутренний метод для создания нового экземпляра {@link KafkaProducer}.
     *
     * @param topicName       имя топика.
     * @param keySerializer   класс сериализатора для ключа.
     * @param valueSerializer класс сериализатора для значения.
     * @return новый экземпляр {@link KafkaProducer}.
     */
    private <K, V> KafkaProducer<K, V> createProducerInternal(String topicName, Class<?> keySerializer, Class<?> valueSerializer) {
        log.info("Создание нового KafkaProducer для топика '{}' [KeySerializer: {}, ValueSerializer: {}]",
                topicName, keySerializer.getSimpleName(), valueSerializer.getSimpleName());
        Properties props = configProvider.apply(topicName);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer.getName());
        return new KafkaProducer<>(props);
    }

    @Override
    public void closeAll() {
        log.info("Начало закрытия всех кэшированных продюсеров...");
        closeProducersInMap(stringProducers);
        closeProducersInMap(avroProducers);
        log.info("Все кэшированные продюсеры были успешно закрыты.");
    }

    private void closeProducersInMap(Map<String, ? extends KafkaProducer<?, ?>> producersToClose) {
        if (producersToClose.isEmpty()) {
            return;
        }
        producersToClose.forEach((key, producer) -> {
            try {
                producer.close();
                log.info("Producer для топика '{}' успешно закрыт.", key);
            } catch (Exception e) {
                log.error("Ошибка при закрытии Producer для топика '{}'.", key, e);
            }
        });
        producersToClose.clear();
    }
}
