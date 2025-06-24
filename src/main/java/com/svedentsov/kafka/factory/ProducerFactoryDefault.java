package com.svedentsov.kafka.factory;

import com.svedentsov.kafka.config.KafkaConfigBuilder;
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

import static com.svedentsov.kafka.utils.ValidationUtils.requireNonBlank;

/**
 * Улучшенная реализация ProducerFactory.
 * Хранит Avro-продюсеры как KafkaProducer<String, GenericRecord>.
 * Тестируема и следует принципу DRY.
 */
@Slf4j
public class ProducerFactoryDefault implements ProducerFactory {

    private final Map<String, KafkaProducer<String, String>> stringProducers = new ConcurrentHashMap<>();
    private final Map<String, KafkaProducer<String, GenericRecord>> avroProducers = new ConcurrentHashMap<>();
    private final Function<String, String> stringProducerKey = topicName -> topicName + ":string";
    private final Function<String, String> avroProducerKey = topicName -> topicName + ":avro";

    @Override
    public KafkaProducer<String, String> createStringProducer(String topicName) {
        requireNonBlank(topicName, "Имя топика не может быть null или пустым.");
        return stringProducers.computeIfAbsent(stringProducerKey.apply(topicName),
                key -> createProducerInternal(topicName, StringSerializer.class, StringSerializer.class));
    }

    @Override
    public KafkaProducer<String, GenericRecord> createAvroProducer(String topicName) {
        requireNonBlank(topicName, "Имя топика не может быть null или пустым.");
        return avroProducers.computeIfAbsent(avroProducerKey.apply(topicName),
                key -> createProducerInternal(topicName, StringSerializer.class, KafkaAvroSerializer.class));
    }

    /**
     * Общий метод для создания строкового продюсера.
     */
    private <K, V> KafkaProducer<K, V> createProducerInternal(String topicName, Class<?> keySerializer, Class<?> valueSerializer) {
        Properties props = KafkaConfigBuilder.getProducerConfig(topicName);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer.getName());
        log.info("Создание нового KafkaProducer для топика '{}' [KeySerializer: {}, ValueSerializer: {}]",
                topicName, keySerializer.getSimpleName(), valueSerializer.getSimpleName());
        return new KafkaProducer<>(props);
    }

    @Override
    public void closeAll() {
        log.info("Начало закрытия всех кэшированных продюсеров...");
        closeProducersInMap(stringProducers);
        closeProducersInMap(avroProducers);
        log.info("Все кэшированные продюсеры были закрыты.");
    }

    private void closeProducersInMap(Map<String, ? extends KafkaProducer<?, ?>> producersToClose) {
        if (producersToClose.isEmpty()) {
            return;
        }
        producersToClose.forEach((key, producer) -> {
            try {
                producer.close();
                log.info("Producer для ключа '{}' успешно закрыт.", key);
            } catch (Exception e) {
                log.error("Ошибка при закрытии Producer для ключа '{}'", key, e);
            }
        });
        producersToClose.clear();
    }
}
