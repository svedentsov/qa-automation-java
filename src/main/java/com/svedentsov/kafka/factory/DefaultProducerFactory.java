package com.svedentsov.kafka.factory;

import com.svedentsov.kafka.config.KafkaConfigBuilder;
import com.svedentsov.kafka.utils.ValidationUtils;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Реализация ProducerFactory, создающая реальные KafkaProducer через KafkaClientPool-подобную логику.
 * Сохраняет продюсеры в пуле, чтобы переиспользовать их.
 */
@Slf4j
public class DefaultProducerFactory implements ProducerFactory {

    private final Map<String, KafkaProducer<?, ?>> producers = new ConcurrentHashMap<>();

    @Override
    public KafkaProducer<String, String> createStringProducer(String topicName) {
        ValidationUtils.requireNonBlank(topicName, "Имя топика не может быть null или пустым.");
        String key = topicName + ":string";
        return (KafkaProducer<String, String>) producers.computeIfAbsent(key, k -> createProducerInternal(topicName, StringSerializer.class.getName(), StringSerializer.class.getName()));
    }

    @Override
    public KafkaProducer<String, Object> createAvroProducer(String topicName) {
        ValidationUtils.requireNonBlank(topicName, "Имя топика не может быть null или пустым.");
        String key = topicName + ":avro";
        return (KafkaProducer<String, Object>) producers.computeIfAbsent(key, k -> createProducerInternal(topicName, StringSerializer.class.getName(), KafkaAvroSerializer.class.getName()));
    }

    private KafkaProducer<?, ?> createProducerInternal(String topicName, String keySerializer, String valueSerializer) {
        Properties props = KafkaConfigBuilder.getProducerConfig(topicName);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        log.info("Создание нового Producer для топика: {} (keySer={}, valueSer={})", topicName, keySerializer, valueSerializer);
        return new KafkaProducer<>(props);
    }

    @Override
    public void closeAll() {
        producers.forEach((key, producer) -> {
            try {
                producer.close();
                log.info("Producer {} закрыт.", key);
            } catch (Exception e) {
                log.error("Ошибка при закрытии Producer {}: {}", key, e.getMessage(), e);
            }
        });
        producers.clear();
    }
}
