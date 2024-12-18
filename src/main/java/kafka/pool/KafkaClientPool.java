package kafka.pool;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import kafka.config.KafkaConfigBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.*;

import java.util.Properties;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaClientPool {

    public static KafkaProducer<String, String> getStringProducer(String topic) {
        return createProducer(topic, String.class, String.class);
    }

    public static KafkaProducer<String, Object> getAvroProducer(String topic) {
        return createProducer(topic, String.class, Object.class);
    }

    public static KafkaConsumer<String, String> createStringConsumer(String topic) {
        return createConsumer(topic, String.class, String.class);
    }

    public static KafkaConsumer<String, Object> createAvroConsumer(String topic) {
        return createConsumer(topic, String.class, Object.class);
    }

    public static void closeProducer(KafkaProducer<?, ?> producer, String topic) {
        if (producer != null) {
            try {
                producer.close();
                log.info("Producer для топика {} закрыт", topic);
            } catch (Exception e) {
                log.error("Ошибка при закрытии Producer для топика {}: {}", topic, e.getMessage(), e);
            }
        }
    }

    private static <K, V> KafkaProducer<K, V> createProducer(String topic, Class<K> keyType, Class<V> valueType) {
        Properties props = KafkaConfigBuilder.getProducerConfig(topic);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, getSerializer(keyType).getClass().getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, getSerializer(valueType).getClass().getName());
        log.info("Создание нового Producer для топика: {}", topic);
        return new KafkaProducer<>(props);
    }

    private static <K, V> KafkaConsumer<K, V> createConsumer(String topic, Class<K> keyType, Class<V> valueType) {
        Properties props = KafkaConfigBuilder.getConsumerConfig(topic);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, getDeserializer(keyType).getClass().getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, getDeserializer(valueType).getClass().getName());
        log.info("Создание нового Consumer для топика: {}", topic);
        return new KafkaConsumer<>(props);
    }

    /**
     * Получает сериализатор для указанного типа.
     *
     * @param clazz класс типа данных
     * @return сериализатор для указанного типа
     * @throws IllegalArgumentException если тип не поддерживается
     */
    private static Serializer<?> getSerializer(Class<?> clazz) {
        if (clazz.equals(String.class)) {
            return new StringSerializer();
        } else if (clazz.equals(byte[].class)) {
            return new ByteArraySerializer();
        } else if (clazz.equals(Object.class)) {
            return new KafkaAvroSerializer();
        } else {
            throw new IllegalArgumentException("Неподдерживаемый тип сериализатора: " + clazz.getName());
        }
    }

    /**
     * Получает десериализатор для указанного типа.
     *
     * @param clazz класс типа данных
     * @return десериализатор для указанного типа
     * @throws IllegalArgumentException если тип не поддерживается
     */
    private static Deserializer<?> getDeserializer(Class<?> clazz) {
        if (clazz.equals(String.class)) {
            return new StringDeserializer();
        } else if (clazz.equals(byte[].class)) {
            return new ByteArrayDeserializer();
        } else if (clazz.equals(Object.class)) {
            return new KafkaAvroDeserializer();
        } else {
            throw new IllegalArgumentException("Неподдерживаемый тип десериализатора: " + clazz.getName());
        }
    }
}
