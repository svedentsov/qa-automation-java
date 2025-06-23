package com.svedentsov.kafka.service;

import com.svedentsov.kafka.model.Record;

import java.util.concurrent.CompletableFuture;

/**
 * Интерфейс для сервиса отправки записей в Kafka.
 * Реализация может отправлять синхронно или асинхронно.
 */
public interface KafkaProducerService {

    /**
     * Отправляет запись в Kafka в синхронном режиме.
     * Бросает RuntimeException, если не удалось отправить.
     *
     * @param record объект записи, содержащий информацию о топике, ключе, значении и заголовках
     */
    void sendRecord(Record record);

    /**
     * Отправляет запись в Kafka в асинхронном режиме.
     * Возвращает CompletableFuture, который завершится успешно или с ошибкой.
     *
     * @param record запись для отправки
     * @return CompletableFuture<Void> результат асинхронной отправки.
     */
    CompletableFuture<Void> sendRecordAsync(Record record);
}
