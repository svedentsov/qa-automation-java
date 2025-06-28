package com.svedentsov.kafka.exception;

/**
 * Пользовательское непроверяемое исключение (runtime exception),
 * которое выбрасывается при ошибках во время отправки сообщений в Kafka.
 * Инкапсулирует исходное исключение для сохранения полной информации об ошибке.
 */
public class KafkaSendingException extends RuntimeException {

    /**
     * Конструктор с сообщением об ошибке и исходной причиной.
     *
     * @param message детальное сообщение.
     * @param cause   исходное исключение (cause).
     */
    public KafkaSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
