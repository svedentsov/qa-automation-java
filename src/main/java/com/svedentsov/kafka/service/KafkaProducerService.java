package com.svedentsov.kafka.service;

import com.svedentsov.kafka.exception.KafkaSendingException;
import com.svedentsov.kafka.model.Record;

import java.util.concurrent.CompletableFuture;

/**
 * Определяет контракт сервиса для отправки записей в Apache Kafka.
 * Интерфейс предоставляет два режима отправки:
 * <ul>
 * <li><b>Синхронный (блокирующий)</b>: для сценариев, где требуется дождаться подтверждения отправки.</li>
 * <li><b>Асинхронный (неблокирующий)</b>: для высокопроизводительных сценариев, где ожидание не требуется.</li>
 * </ul>
 */
public interface KafkaProducerService {

    /**
     * Отправляет запись в Kafka в синхронном (блокирующем) режиме.
     * Метод возвращает управление только после получения подтверждения от брокера Kafka
     * о том, что сообщение успешно принято, или после возникновения ошибки.
     *
     * @param record объект записи для отправки. Не может быть {@code null}.
     * @throws KafkaSendingException    если отправка завершилась ошибкой.
     * @throws IllegalArgumentException если запись не прошла валидацию (например, обязательные поля не заполнены).
     */
    void sendRecord(Record record);

    /**
     * Отправляет запись в Kafka в асинхронном (неблокирующем) режиме.
     * Метод инициирует отправку и немедленно возвращает {@link CompletableFuture},
     * который будет завершен после получения ответа от брокера Kafka.
     * Это предпочтительный способ для приложений, требующих высокой пропускной способности.
     *
     * @param record запись для отправки. Не может быть {@code null}.
     * @return {@link CompletableFuture<Void>}, который:
     * <ul>
     * <li>завершается со значением {@code null} при успешной отправке;</li>
     * <li>завершается исключением {@link KafkaSendingException} в случае ошибки.</li>
     * </ul>
     */
    CompletableFuture<Void> sendRecordAsync(Record record);
}
