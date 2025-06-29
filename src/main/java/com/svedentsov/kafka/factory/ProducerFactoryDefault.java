package com.svedentsov.kafka.factory;

import com.svedentsov.kafka.config.DefaultKafkaConfigProvider; // Импортируем новый провайдер
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
 * Потокобезопасная реализация {@link ProducerFactory} по умолчанию.
 * Кэширует созданные экземпляры {@link KafkaProducer} (по одному на каждый тип сериализатора)
 * для повторного использования. Это стандартная практика, позволяющая избежать
 * накладных расходов на создание новых TCP-соединений с брокерами Kafka при каждой отправке.
 * Использует DefaultKafkaConfigProvider для получения базовых конфигураций.
 */
@Slf4j
public class ProducerFactoryDefault implements ProducerFactory {

    private final AtomicReference<KafkaProducer<String, String>> stringProducerRef = new AtomicReference<>();
    private final AtomicReference<KafkaProducer<String, GenericRecord>> avroProducerRef = new AtomicReference<>();
    private final DefaultKafkaConfigProvider configProvider;

    /**
     * Создает экземпляр ProducerFactoryDefault с указанным провайдером конфигураций.
     *
     * @param configProvider провайдер конфигураций Kafka, не может быть null.
     */
    public ProducerFactoryDefault(DefaultKafkaConfigProvider configProvider) {
        this.configProvider = requireNonNull(configProvider, "DefaultKafkaConfigProvider не может быть null.");
    }

    @Override
    public KafkaProducer<String, String> createStringProducer() {
        return stringProducerRef.updateAndGet(existingProducer ->
                (existingProducer != null) ? existingProducer : createProducerInternal(StringSerializer.class, StringSerializer.class));
    }

    @Override
    public KafkaProducer<String, GenericRecord> createAvroProducer() {
        return avroProducerRef.updateAndGet(existingProducer ->
                (existingProducer != null) ? existingProducer : createProducerInternal(StringSerializer.class, KafkaAvroSerializer.class));
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
        Properties props = configProvider.getProducerConfig(null);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializerClass.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerClass.getName());
        return new KafkaProducer<>(props);
    }

    @Override
    public void close() {
        log.info("Начало закрытия всех кэшированных продюсеров...");
        closeProducer(stringProducerRef.getAndSet(null), "String");
        closeProducer(avroProducerRef.getAndSet(null), "Avro");
        log.info("Все кэшированные продюсеры были закрыты.");
    }

    /**
     * Вспомогательный метод для безопасного закрытия экземпляра продюсера.
     *
     * @param producer Продюсер для закрытия. Может быть {@code null}.
     * @param type     Тип продюсера (используется для логирования).
     */
    private void closeProducer(KafkaProducer<?, ?> producer, String type) {
        if (producer != null) {
            try {
                producer.close(); // Блокирующий вызов, ожидает завершения всех отправок.
                log.info("{} продюсер успешно закрыт.", type);
            } catch (Exception e) {
                log.error("Ошибка при закрытии {} продюсера.", type, e);
            }
        }
    }
}
