package com.svedentsov.kafka.factory;

import com.svedentsov.kafka.config.KafkaConfigBuilder;
import com.svedentsov.kafka.utils.ValidationUtils;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Реализация ConsumerFactory, создающая реальные KafkaConsumer через логику пула.
 */
@Slf4j
public class DefaultConsumerFactory implements ConsumerFactory {

    private final Map<String, KafkaConsumer<?, ?>> consumers = new ConcurrentHashMap<>();

    @Override
    public KafkaConsumer<String, String> createStringConsumer(String topicName) {
        ValidationUtils.requireNonBlank(topicName, "Имя топика не может быть null или пустым.");
        String key = topicName + ":string";
        return (KafkaConsumer<String, String>) consumers.computeIfAbsent(key, k -> createConsumerInternal(topicName, StringDeserializer.class.getName(), StringDeserializer.class.getName()));
    }

    @Override
    public KafkaConsumer<String, Object> createAvroConsumer(String topicName) {
        ValidationUtils.requireNonBlank(topicName, "Имя топика не может быть null или пустым.");
        String key = topicName + ":avro";
        return (KafkaConsumer<String, Object>) consumers.computeIfAbsent(key, k -> createConsumerInternal(topicName, StringDeserializer.class.getName(), KafkaAvroDeserializer.class.getName()));
    }

    private KafkaConsumer<?, ?> createConsumerInternal(String topicName, String keyDeserializer, String valueDeserializer) {
        Properties props = KafkaConfigBuilder.getConsumerConfig(topicName);
        props.put("key.deserializer", keyDeserializer);
        props.put("value.deserializer", valueDeserializer);
        log.info("Создание нового Consumer для топика: {} (keyDeser={}, valueDeser={})", topicName, keyDeserializer, valueDeserializer);
        return new KafkaConsumer<>(props);
    }

    @Override
    public void closeAll() {
        consumers.forEach((key, consumer) -> {
            try {
                consumer.close();
                log.info("Consumer {} закрыт.", key);
            } catch (Exception e) {
                log.error("Ошибка при закрытии Consumer {}: {}", key, e.getMessage(), e);
            }
        });
        consumers.clear();
    }
}
