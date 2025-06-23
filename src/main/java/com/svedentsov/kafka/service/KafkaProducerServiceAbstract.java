package com.svedentsov.kafka.service;

import com.svedentsov.kafka.model.Record;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Базовый обобщенный класс для KafkaProducerService.
 * Реализует общую логику отправки, валидации и обработки CompletableFuture.
 * Использует шаблонный метод для получения специфичных для реализации данных.
 *
 * @param <V> Тип значения сообщения (e.g., String, GenericRecord).
 */
@Slf4j
public abstract class KafkaProducerServiceAbstract<V> implements KafkaProducerService {

    /**
     * Валидирует запись перед отправкой.
     * Базовая реализация проверяет, что record и topic не null.
     * Наследники должны вызвать super.validateRecord(record) и добавить свои проверки.
     *
     * @param record запись для валидации
     * @throws IllegalArgumentException если обязательные поля не заполнены
     */
    protected void validateRecord(Record record) {
        if (record == null) {
            throw new IllegalArgumentException("Record не может быть null");
        }
        if (record.getTopic() == null || record.getTopic().isBlank()) {
            throw new IllegalArgumentException("Topic не может быть null или пустым");
        }
    }

    /**
     * Извлекает типизированное значение из объекта Record.
     *
     * @param record объект записи
     * @return значение нужного типа V
     */
    protected abstract V getValueFromRecord(Record record);

    /**
     * Получает KafkaProducer из пула для конкретного типа значения.
     *
     * @param topic топик, в который будет производиться отправка
     * @return инстанс KafkaProducer<String, V>
     */
    protected abstract KafkaProducer<String, V> getProducer(String topic);

    @Override
    public void sendRecord(Record record) {
        try {
            sendRecordAsync(record).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Поток был прерван во время ожидания синхронной отправки: {}", record, e);
            throw new RuntimeException("Отправка прервана", e);
        } catch (ExecutionException e) {
            log.error("Ошибка при синхронной отправке записи: {}", record, e.getCause());
            throw new RuntimeException("Не удалось отправить запись синхронно", e.getCause());
        }
    }

    @Override
    public CompletableFuture<Void> sendRecordAsync(Record record) {
        try {
            validateRecord(record);
        } catch (Exception e) {
            log.error("Ошибка валидации записи: {}", record, e);
            cleanupRecord(record);
            return CompletableFuture.failedFuture(e);
        }
        return doSend(record)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Ошибка при асинхронной отправке записи: {}", record, ex);
                    } else {
                        log.info("Запись успешно отправлена в асинхронном режиме: {}", record);
                    }
                    cleanupRecord(record);
                });
    }

    /**
     * Основной метод, выполняющий отправку. Он инкапсулирует всю логику работы с KafkaProducer API.
     */
    private CompletableFuture<Void> doSend(Record record) {
        final CompletableFuture<Void> future = new CompletableFuture<>();
        try {
            V value = getValueFromRecord(record);
            ProducerRecord<String, V> producerRecord = buildProducerRecord(record, value);
            KafkaProducer<String, V> producer = getProducer(record.getTopic());
            if (producer == null) {
                throw new IllegalStateException("KafkaProducer, полученный из пула, равен null для топика: " + record.getTopic());
            }
            producer.send(producerRecord, (metadata, exception) -> {
                if (exception != null) {
                    future.completeExceptionally(exception);
                } else {
                    log.debug("Запись успешно отправлена: topic={}, partition={}, offset={}",
                            metadata.topic(), metadata.partition(), metadata.offset());
                    future.complete(null);
                }
            });
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
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
                try {
                    if (k != null && v != null) {
                        producerRecord.headers().add(new RecordHeader(k, v.toString().getBytes(StandardCharsets.UTF_8)));
                    }
                } catch (Exception ex) {
                    log.warn("Не удалось добавить header '{}'='{}' в сообщение: {}", k, v, ex.getMessage());
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
            log.warn("Ошибка при вызове record.clear() после отправки: {}", ex.getMessage(), ex);
        }
    }
}
