package com.svedentsov.kafka.pool;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import com.svedentsov.kafka.config.KafkaConfigBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.*;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Пул клиентов Kafka для управления производителями и потребителями.
 * Этот класс предоставляет методы для создания, получения и закрытия экземпляров KafkaProducer и KafkaConsumer
 * для различных типов данных и топиков.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaClientPool {

    private static final Map<String, KafkaProducer<?, ?>> PRODUCERS = new ConcurrentHashMap<>();
    private static final Map<String, KafkaConsumer<?, ?>> CONSUMERS = new ConcurrentHashMap<>();

    /**
     * Получает KafkaProducer для строковых данных.
     * Если экземпляр для данного топика не существует, он создается.
     *
     * @param topic название топика
     * @return KafkaProducer для строковых данных
     */
    public static KafkaProducer<String, String> getStringProducer(String topic) {
        return getProducer(topic + ":string", String.class, String.class);
    }

    /**
     * Получает KafkaProducer для данных в формате Avro.
     * Если экземпляр для данного топика не существует, он создается.
     *
     * @param topic название топика
     * @return KafkaProducer для данных в формате Avro
     */
    public static KafkaProducer<String, Object> getAvroProducer(String topic) {
        return getProducer(topic + ":avro", String.class, Object.class);
    }

    /**
     * Получает KafkaConsumer для строковых данных.
     * Если экземпляр для данного топика не существует, он создается.
     *
     * @param topic название топика
     * @return KafkaConsumer для строковых данных
     */
    public static KafkaConsumer<String, String> getStringConsumer(String topic) {
        return getConsumer(topic + ":string", String.class, String.class);
    }

    /**
     * Получает KafkaConsumer для данных в формате Avro.
     * Если экземпляр для данного топика не существует, он создается.
     *
     * @param topic название топика
     * @return KafkaConsumer для данных в формате Avro
     */
    public static KafkaConsumer<String, Object> getAvroConsumer(String topic) {
        return getConsumer(topic + ":avro", String.class, Object.class);
    }

    /**
     * Закрывает KafkaProducer для указанного топика и удаляет его из пула.
     * Если производитель не был найден, метод ничего не делает.
     *
     * @param topic название топика
     */
    public static void closeProducer(String topic) {
        KafkaProducer<?, ?> producer = PRODUCERS.remove(topic);
        closeResource(producer, "Producer", topic);
    }

    /**
     * Закрывает KafkaConsumer для указанного топика и удаляет его из пула.
     * Если потребитель не был найден, метод ничего не делает.
     *
     * @param topic название топика
     */
    public static void closeConsumer(String topic) {
        KafkaConsumer<?, ?> consumer = CONSUMERS.remove(topic);
        closeResource(consumer, "Consumer", topic);
    }

    /**
     * Закрывает все KafkaProducer'ы в пуле и очищает пул.
     */
    public static void closeAllProducers() {
        closeAllResources(PRODUCERS, "Producer");
    }

    /**
     * Закрывает все KafkaConsumer'ы в пуле и очищает пул.
     */
    public static void closeAllConsumers() {
        closeAllResources(CONSUMERS, "Consumer");
    }

    /**
     * Получает KafkaProducer для заданных типов ключа и значения.
     * Если экземпляр для данного ключа не существует, он создается.
     *
     * @param key       ключ для идентификации producer
     * @param keyType   класс типа ключа
     * @param valueType класс типа значения
     * @param <K>       тип ключа
     * @param <V>       тип значения
     * @return KafkaProducer для заданных типов ключа и значения
     */
    private static <K, V> KafkaProducer<K, V> getProducer(String key, Class<K> keyType, Class<V> valueType) {
        return (KafkaProducer<K, V>) PRODUCERS.computeIfAbsent(key, k -> createProducer(key, keyType, valueType));
    }

    /**
     * Получает KafkaConsumer для заданных типов ключа и значения.
     * Если экземпляр для данного ключа не существует, он создается.
     *
     * @param key       ключ для идентификации consumer
     * @param keyType   класс типа ключа
     * @param valueType класс типа значения
     * @param <K>       тип ключа
     * @param <V>       тип значения
     * @return KafkaConsumer для заданных типов ключа и значения
     */
    private static <K, V> KafkaConsumer<K, V> getConsumer(String key, Class<K> keyType, Class<V> valueType) {
        return (KafkaConsumer<K, V>) CONSUMERS.computeIfAbsent(key, k -> createConsumer(key, keyType, valueType));
    }

    /**
     * Создает KafkaProducer для заданных типов ключа и значения.
     *
     * @param key       ключ для конфигурации
     * @param keyType   класс типа ключа
     * @param valueType класс типа значения
     * @param <K>       тип ключа
     * @param <V>       тип значения
     * @return созданный KafkaProducer
     */
    private static <K, V> KafkaProducer<K, V> createProducer(String key, Class<K> keyType, Class<V> valueType) {
        Properties props = KafkaConfigBuilder.getProducerConfig(key.split(":")[0]);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, getSerializer(keyType).getClass().getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, getSerializer(valueType).getClass().getName());
        log.info("Создание нового Producer для ключа: {}", key);
        return new KafkaProducer<>(props);
    }

    /**
     * Создает KafkaConsumer для заданных типов ключа и значения.
     *
     * @param key       ключ для конфигурации
     * @param keyType   класс типа ключа
     * @param valueType класс типа значения
     * @param <K>       тип ключа
     * @param <V>       тип значения
     * @return созданный KafkaConsumer
     */
    private static <K, V> KafkaConsumer<K, V> createConsumer(String key, Class<K> keyType, Class<V> valueType) {
        Properties props = KafkaConfigBuilder.getConsumerConfig(key.split(":")[0]);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, getDeserializer(keyType).getClass().getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, getDeserializer(valueType).getClass().getName());
        log.info("Создание нового Consumer для ключа: {}", key);
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

    /**
     * Закрывает все ресурсы в пуле и очищает пул.
     *
     * @param resources    пул ресурсов
     * @param resourceType тип ресурса (например, "Producer" или "Consumer")
     */
    private static void closeAllResources(Map<String, ? extends AutoCloseable> resources, String resourceType) {
        resources.forEach((key, resource) -> closeResource(resource, resourceType, key));
        resources.clear();
    }

    /**
     * Закрывает указанный ресурс и логирует результат.
     *
     * @param resource     ресурс для закрытия
     * @param resourceType тип ресурса (например, "Producer" или "Consumer")
     * @param topic        название топика, к которому относится ресурс
     */
    private static void closeResource(AutoCloseable resource, String resourceType, String topic) {
        if (Objects.nonNull(resource)) {
            try {
                resource.close();
                log.info("{} для топика {} закрыт", resourceType, topic);
            } catch (Exception e) {
                log.error("Ошибка при закрытии {} для топика {}: {}", resourceType, topic, e.getMessage(), e);
            }
        }
    }
}
