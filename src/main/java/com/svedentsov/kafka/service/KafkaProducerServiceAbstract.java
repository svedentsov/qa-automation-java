package com.svedentsov.kafka.service;

import com.svedentsov.kafka.exception.KafkaSendingException;
import com.svedentsov.kafka.model.Record;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static java.util.Objects.requireNonNull;

/**
 * Абстрактный базовый класс для {@link KafkaProducerService}, реализующий общую логику отправки.
 * Этот класс использует {@link KafkaProducer}, внедренный через конструктор, для отправки сообщений.
 * Он реализует "Шаблонный метод" (Template Method Pattern), делегируя специфичные для формата данных
 * операции (валидацию и извлечение значения) дочерним классам.
 *
 * @param <V> тип значения сообщения Kafka (например, {@code String} или {@code GenericRecord}).
 */
@Slf4j
public abstract class KafkaProducerServiceAbstract<V> implements KafkaProducerService {

    protected final KafkaProducer<String, V> producer;

    /**
     * Конструктор для внедрения зависимости {@link KafkaProducer}.
     * Сервис получает уже сконфигурированный и готовый к работе продюсер.
     *
     * @param producer экземпляр Kafka продюсера, не может быть {@code null}.
     */
    protected KafkaProducerServiceAbstract(KafkaProducer<String, V> producer) {
        this.producer = requireNonNull(producer, "KafkaProducer не может быть null.");
    }

    /**
     * Шаблонный метод для валидации записи перед отправкой.
     * Базовая реализация проверяет, что сама запись, её топик не являются {@code null} или пустыми.
     * Классы-наследники <strong>обязаны</strong> вызывать {@code super.validateRecord(record)}
     * и могут добавлять собственные, специфичные для формата, проверки.
     *
     * @param record запись для валидации.
     * @throws IllegalArgumentException если обязательные поля не заполнены или имеют неверный формат.
     */
    protected void validateRecord(Record record) {
        requireNonNull(record, "Record не может быть null.");
        requireNonNull(record.getTopic(), "Topic в записи не может быть null.");
        if (record.getTopic().isBlank()) {
            throw new IllegalArgumentException("Topic в записи не может быть пустым.");
        }
    }

    /**
     * Абстрактный шаблонный метод для извлечения типизированного значения из объекта {@link Record}.
     * Реализация в дочернем классе должна извлечь и вернуть значение, соответствующее типу {@code V}.
     * Предполагается, что на момент вызова этого метода запись уже прошла валидацию.
     *
     * @param record объект записи, из которого извлекается значение.
     * @return значение типа {@code V}.
     */
    protected abstract V getValueFromRecord(Record record);

    @Override
    public void sendRecord(Record record) {
        try {
            sendRecordAsync(record).join();
        } catch (CompletionException e) {
            if (e.getCause() instanceof KafkaSendingException) {
                throw (KafkaSendingException) e.getCause();
            }
            throw new KafkaSendingException("Не удалось дождаться завершения отправки записи.", e);
        }
    }

    @Override
    public CompletableFuture<Void> sendRecordAsync(Record record) {
        CompletableFuture<Void> resultFuture = new CompletableFuture<>();
        try {
            validateRecord(record);
            V value = getValueFromRecord(record);
            ProducerRecord<String, V> producerRecord = buildProducerRecord(record, value);
            log.debug("Асинхронная отправка записи в топик '{}': {}", record.getTopic(), producerRecord);
            producer.send(producerRecord, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Ошибка при асинхронной отправке записи в топик '{}'", record.getTopic(), exception);
                    resultFuture.completeExceptionally(new KafkaSendingException("Ошибка при отправке записи в Kafka.", exception));
                } else {
                    log.info("Запись успешно отправлена: topic={}, partition={}, offset={}",
                            metadata.topic(), metadata.partition(), metadata.offset());
                    resultFuture.complete(null);
                }
            });

        } catch (Exception e) {
            log.error("Ошибка на этапе подготовки записи к отправке в топик '{}'", record.getTopic(), e);
            // Если ошибка произошла до вызова producer.send(), мы должны завершить Future с исключением.
            resultFuture.completeExceptionally(new KafkaSendingException("Ошибка подготовки записи к отправке.", e));
        }
        // Гарантированно выполняем очистку после завершения операции (успешного или нет)
        return resultFuture.whenComplete((res, ex) -> cleanupRecord(record));
    }

    /**
     * Собирает объект {@link ProducerRecord} из доменной модели {@link Record}.
     * Также выполняет преобразование заголовков.
     *
     * @param record входная доменная запись.
     * @param value  типизированное значение для отправки.
     * @return готовый к отправке {@link ProducerRecord}.
     */
    private ProducerRecord<String, V> buildProducerRecord(Record record, V value) {
        ProducerRecord<String, V> producerRecord = new ProducerRecord<>(
                record.getTopic(),
                record.getPartition(),
                record.getKey(),
                value);

        if (record.getHeaders() != null) {
            record.getHeaders().forEach((k, v) -> {
                if (k != null && v != null) {
                    producerRecord.headers().add(new RecordHeader(k, v.toString().getBytes(StandardCharsets.UTF_8)));
                } else {
                    log.warn("Пропущен заголовок с null ключом или значением для записи в топик '{}'", record.getTopic());
                }
            });
        }
        return producerRecord;
    }

    /**
     * Выполняет безопасную очистку ресурсов, удерживаемых объектом {@link Record}, после завершения отправки.
     * Например, может обнулять большие поля (value), чтобы помочь сборщику мусора.
     * Ошибки во время очистки логируются, но не влияют на результат основной операции.
     *
     * @param record Запись для очистки.
     */
    private void cleanupRecord(Record record) {
        try {
            if (record != null && record instanceof AutoCloseable) {
                ((AutoCloseable) record).close();
            }
        } catch (Exception ex) {
            log.warn("Не удалось корректно очистить объект Record после отправки: {}", ex.getMessage(), ex);
        }
    }
}
