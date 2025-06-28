package com.svedentsov.kafka.factory;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;

/**
 * Реализация {@link ProducerFactory} по умолчанию.
 * Кэширует созданные экземпляры {@link KafkaProducer} для повторного использования,
 * чтобы избежать накладных расходов на создание новых соединений с Kafka.
 */
@Slf4j
public class ProducerFactoryDefault implements ProducerFactory {

    private final AtomicReference<KafkaProducer<String, String>> stringProducerRef = new AtomicReference<>();
    private final AtomicReference<KafkaProducer<String, GenericRecord>> avroProducerRef = new AtomicReference<>();
    private final Properties baseProperties;

    /**
     * Конструктор по умолчанию.
     * Использует базовые настройки для подключения к Kafka на {@code localhost:9092}.
     * Не рекомендуется для production-окружений.
     */
    public ProducerFactoryDefault() {
        this(createDefaultProperties());
    }

    /**
     * Рекомендуемый конструктор, создающий фабрику с предопределенной конфигурацией.
     *
     * @param baseProperties базовые свойства для всех создаваемых продюсеров (например, bootstrap.servers).
     *                       Не может быть {@code null}.
     */
    public ProducerFactoryDefault(Properties baseProperties) {
        this.baseProperties = requireNonNull(baseProperties, "Базовые свойства (baseProperties) не могут быть null.");
    }

    @Override
    public KafkaProducer<String, String> createStringProducer() {
        return stringProducerRef.updateAndGet(existingProducer ->
                existingProducer != null ? existingProducer : createProducerInternal(StringSerializer.class, StringSerializer.class));
    }

    @Override
    public KafkaProducer<String, GenericRecord> createAvroProducer() {
        return avroProducerRef.updateAndGet(existingProducer ->
                existingProducer != null ? existingProducer : createProducerInternal(StringSerializer.class, KafkaAvroSerializer.class));
    }

    /**
     * Внутренний метод для создания нового экземпляра {@link KafkaProducer}.
     *
     * @param keySerializerClass   класс сериализатора для ключа.
     * @param valueSerializerClass класс сериализатора для значения.
     * @return новый экземпляр {@link KafkaProducer}.
     */
    private <K, V> KafkaProducer<K, V> createProducerInternal(Class<?> keySerializerClass, Class<?> valueSerializerClass) {
        log.info("Создание нового экземпляра KafkaProducer [KeySerializer: {}, ValueSerializer: {}]",
                keySerializerClass.getSimpleName(), valueSerializerClass.getSimpleName());
        // Создаем копию, чтобы не модифицировать исходный объект свойств
        Properties props = new Properties();
        props.putAll(baseProperties);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializerClass.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerClass.getName());
        return new KafkaProducer<>(props);
    }

    @Override
    public void closeAll() {
        log.info("Начало закрытия всех кэшированных продюсеров...");
        closeProducer(stringProducerRef.getAndSet(null), "String");
        closeProducer(avroProducerRef.getAndSet(null), "Avro");
        log.info("Все кэшированные продюсеры были успешно закрыты.");
    }

    /**
     * Вспомогательный метод для безопасного закрытия одного продюсера.
     *
     * @param producer Продюсер для закрытия. Может быть {@code null}.
     * @param type     Тип продюсера (для логирования).
     */
    private void closeProducer(KafkaProducer<?, ?> producer, String type) {
        if (producer != null) {
            try {
                producer.close();
                log.info("{} продюсер успешно закрыт.", type);
            } catch (Exception e) {
                log.error("Ошибка при закрытии {} продюсера.", type, e);
            }
        }
    }

    private static Properties createDefaultProperties() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // Здесь можно добавить другие общие свойства по умолчанию
        return props;
    }
}
