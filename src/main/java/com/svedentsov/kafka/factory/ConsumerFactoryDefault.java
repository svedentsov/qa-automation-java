package com.svedentsov.kafka.factory;

import com.svedentsov.kafka.config.DefaultKafkaConfigProvider;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static com.svedentsov.kafka.utils.ValidationUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * Улучшенная, типобезопасная и тестируемая реализация ConsumerFactory,
 * использующая DefaultKafkaConfigProvider для получения конфигураций.
 */
@Slf4j
@NoArgsConstructor
public class ConsumerFactoryDefault implements ConsumerFactory {

    private final Map<String, KafkaConsumer<String, String>> stringConsumers = new ConcurrentHashMap<>();
    private final Map<String, KafkaConsumer<String, Object>> avroConsumers = new ConcurrentHashMap<>();
    private final Function<String, String> stringConsumerKey = topicName -> topicName + ":string";
    private final Function<String, String> avroConsumerKey = topicName -> topicName + ":avro";
    private DefaultKafkaConfigProvider configProvider;

    /**
     * Создает экземпляр ConsumerFactoryDefault с указанным провайдером конфигураций.
     *
     * @param configProvider провайдер конфигураций Kafka, не может быть null.
     */
    public ConsumerFactoryDefault(DefaultKafkaConfigProvider configProvider) {
        this.configProvider = requireNonNull(configProvider, "DefaultKafkaConfigProvider не может быть null.");
    }

    @Override
    public KafkaConsumer<String, String> createStringConsumer(String topicName) {
        requireNonBlank(topicName, "Имя топика не может быть null или пустым.");
        return stringConsumers.computeIfAbsent(stringConsumerKey.apply(topicName),
                key -> createConsumerInternal(topicName, StringDeserializer.class, StringDeserializer.class));
    }

    @Override
    public KafkaConsumer<String, Object> createAvroConsumer(String topicName) {
        requireNonBlank(topicName, "Имя топика не может быть null или пустым.");
        return avroConsumers.computeIfAbsent(avroConsumerKey.apply(topicName),
                key -> createConsumerInternal(topicName, StringDeserializer.class, KafkaAvroDeserializer.class));
    }

    /**
     * Обобщенный внутренний метод для создания KafkaConsumer.
     * Использует константы из ConsumerConfig для надежности.
     */
    private <K, V> KafkaConsumer<K, V> createConsumerInternal(String topicName, Class<?> keyDeserializer, Class<?> valueDeserializer) {
        Properties props = configProvider.getConsumerConfig(topicName);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer.getName());
        log.info("Создание нового KafkaConsumer для топика '{}' [KeyDeserializer: {}, ValueDeserializer: {}]",
                topicName, keyDeserializer.getSimpleName(), valueDeserializer.getSimpleName());
        return new KafkaConsumer<>(props);
    }

    @Override
    public void closeAll() {
        log.info("Начало закрытия всех кэшированных консьюмеров...");
        closeConsumersInMap(stringConsumers);
        closeConsumersInMap(avroConsumers);
        log.info("Все кэшированные консьюмеры были закрыты.");
    }

    /**
     * Хелпер-метод для итеративного закрытия консьюмеров в указанном пуле.
     *
     * @param consumersToClose Карта с консьюмерами для закрытия.
     */
    private void closeConsumersInMap(Map<String, ? extends KafkaConsumer<?, ?>> consumersToClose) {
        if (consumersToClose.isEmpty()) {
            return;
        }
        consumersToClose.forEach((key, consumer) -> {
            try {
                consumer.close();
                log.info("Consumer для ключа '{}' успешно закрыт.", key);
            } catch (Exception e) {
                log.error("Ошибка при закрытии Consumer для ключа '{}'", key, e);
            }
        });
        consumersToClose.clear();
    }
}
