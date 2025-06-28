package com.svedentsov.kafka.service;

import com.svedentsov.kafka.exception.KafkaSendingException;
import com.svedentsov.kafka.factory.ProducerFactory;
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
 * Абстрактный базовый класс для {@link KafkaProducerService}.
 * Реализует общую логику отправки, валидации и обработки ошибок,
 * делегируя специфичные для формата данных операции дочерним классам
 * через шаблонный метод.
 *
 * @param <V> Тип значения сообщения (e.g., String, GenericRecord).
 */
@Slf4j
public abstract class KafkaProducerServiceAbstract<V> implements KafkaProducerService {

    protected final ProducerFactory producerFactory;

    /**
     * Конструктор для внедрения зависимости {@link ProducerFactory}.
     *
     * @param producerFactory фабрика для создания Kafka продюсеров.
     */
    protected KafkaProducerServiceAbstract(ProducerFactory producerFactory) {
        this.producerFactory = requireNonNull(producerFactory, "ProducerFactory не может быть null.");
    }

    /**
     * Шаблонный метод для валидации записи перед отправкой.
     * Базовая реализация проверяет на null саму запись и ее топик.
     * Наследники должны вызывать {@code super.validateRecord(record)} и добавлять свои проверки.
     *
     * @param record запись для валидации.
     * @throws IllegalArgumentException если обязательные поля не заполнены.
     */
    protected void validateRecord(Record record) {
        requireNonNull(record, "Record не может быть null.");
        requireNonNull(record.getTopic(), "Topic не может быть null или пустым.");
        if (record.getTopic().isBlank()) {
            throw new IllegalArgumentException("Topic не может быть пустым.");
        }
    }

    /**
     * Шаблонный метод для извлечения типизированного значения из объекта {@link Record}.
     *
     * @param record объект записи.
     * @return значение типа {@code V}.
     */
    protected abstract V getValueFromRecord(Record record);

    /**
     * Шаблонный метод для получения {@link KafkaProducer} из фабрики.
     *
     * @param topic топик, в который будет производиться отправка.
     * @return экземпляр {@link KafkaProducer<String, V>}.
     */
    protected abstract KafkaProducer<String, V> getProducer(String topic);

    @Override
    public void sendRecord(Record record) {
        try {
            sendRecordAsync(record).join();
        } catch (CompletionException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            log.error("Ошибка при синхронной отправке записи: {}", record, cause);
            throw new KafkaSendingException("Не удалось отправить запись синхронно", cause);
        }
    }

    @Override
    public CompletableFuture<Void> sendRecordAsync(Record record) {
        CompletableFuture<Void> resultFuture = new CompletableFuture<>();
        try {
            validateRecord(record);
            V value = getValueFromRecord(record);
            ProducerRecord<String, V> producerRecord = buildProducerRecord(record, value);
            KafkaProducer<String, V> producer = getProducer(record.getTopic());
            log.debug("Отправка записи: {}", producerRecord);
            producer.send(producerRecord, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Ошибка при асинхронной отправке записи: {}", record, exception);
                    resultFuture.completeExceptionally(exception);
                } else {
                    log.info("Запись успешно отправлена: topic={}, partition={}, offset={}", metadata.topic(), metadata.partition(), metadata.offset());
                    resultFuture.complete(null);
                }
            });
        } catch (Exception e) {
            log.error("Ошибка на этапе подготовки записи к отправке: {}", record, e);
            resultFuture.completeExceptionally(e);
        }
        return resultFuture.whenComplete((res, ex) -> cleanupRecord(record));
    }

    /**
     * Хелпер-метод для создания ProducerRecord и добавления заголовков.
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
                    log.warn("Пропущен null-header для записи в топик '{}'", record.getTopic());
                }
            });
        }
        return producerRecord;
    }

    /**
     * Безопасная очистка объекта Record.
     */
    private void cleanupRecord(Record record) {
        try {
            if (record != null) {
                record.clear();
            }
        } catch (Exception ex) {
            log.warn("Ошибка при очистке объекта Record после отправки: {}", ex.getMessage(), ex);
        }
    }
}
