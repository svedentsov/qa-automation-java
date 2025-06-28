package com.svedentsov.kafka.service;

import com.svedentsov.kafka.model.Record;

import java.util.concurrent.CompletableFuture;

/**
 * Интерфейс сервиса для отправки записей в Kafka.
 * Определяет контракт для синхронной и асинхронной отправки.
 */
public interface KafkaProducerService {

    /**
     * Отправляет запись в Kafka в синхронном (блокирующем) режиме.
     * Метод вернет управление только после подтверждения получения брокером.
     *
     * @param record объект записи для отправки.
     */
    void sendRecord(Record record);

    /**
     * Отправляет запись в Kafka в асинхронном режиме.
     * Возвращает {@link CompletableFuture}, который можно использовать для отслеживания
     * результата операции.
     *
     * @param record запись для отправки.
     * @return {@link CompletableFuture<Void>} который завершается null при успехе
     * или исключением при ошибке.
     */
    CompletableFuture<Void> sendRecordAsync(Record record);
}
