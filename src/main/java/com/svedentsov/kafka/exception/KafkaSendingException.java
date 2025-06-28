package com.svedentsov.kafka.exception;

/**
 * Пользовательское непроверяемое исключение (runtime exception),
 * которое является единой точкой отказа для всех операций отправки в Kafka.
 * Использование этого исключения позволяет вызывающему коду ловить один конкретный тип ошибки,
 * не загромождая логику обработкой множества различных технических исключений от клиента Kafka
 * или ошибок валидации. Исходное исключение всегда сохраняется в поле {@code cause}.
 */
public class KafkaSendingException extends RuntimeException {

    /**
     * Конструктор с сообщением об ошибке и исходной причиной.
     *
     * @param message Детальное сообщение, описывающее контекст ошибки.
     * @param cause   Исходное исключение (cause), которое привело к этой ошибке.
     */
    public KafkaSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
