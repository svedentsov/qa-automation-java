package com.svedentsov.kafka.service;

import com.svedentsov.kafka.model.Record;

import java.util.concurrent.CompletableFuture;

/**
 * Определяет контракт сервиса для отправки записей в Kafka.
 * Интерфейс предоставляет методы для синхронной (блокирующей) и асинхронной
 * (неблокирующей) отправки сообщений, позволяя гибко управлять процессом
 * взаимодействия с брокером Kafka в зависимости от требований к производительности и надежности.
 */
public interface KafkaProducerService {

    /**
     * Отправляет запись в Kafka в синхронном (блокирующем) режиме.
     * Метод возвращает управление только после получения подтверждения от брокера Kafka о том,
     * что сообщение успешно принято. В случае ошибки отправки будет выброшено исключение
     * {@link com.svedentsov.kafka.exception.KafkaSendingException}.
     *
     * @param record объект записи для отправки. Не может быть {@code null}.
     * @throws com.svedentsov.kafka.exception.KafkaSendingException если отправка завершилась ошибкой.
     * @throws IllegalArgumentException                             если запись не прошла базовую валидацию (например, {@code null}).
     */
    void sendRecord(Record record);

    /**
     * Отправляет запись в Kafka в асинхронном (неблокирующем) режиме.
     * Метод инициирует отправку и немедленно возвращает {@link CompletableFuture},
     * который будет завершен, когда брокер Kafka подтвердит получение сообщения.
     * Это предпочтительный способ для высокопроизводительных приложений.
     *
     * @param record запись для отправки. Не может быть {@code null}.
     * @return {@link CompletableFuture<Void>}, который:
     * <ul>
     * <li>завершается со значением {@code null} при успешной отправке;</li>
     * <li>завершается исключением {@link com.svedentsov.kafka.exception.KafkaSendingException} в случае ошибки.</li>
     * </ul>
     */
    CompletableFuture<Void> sendRecordAsync(Record record);
}
