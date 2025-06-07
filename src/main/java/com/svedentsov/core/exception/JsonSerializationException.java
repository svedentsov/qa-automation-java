package com.svedentsov.core.exception;

/**
 * Исключение, возникающее при ошибках сериализации JSON.
 */
public class JsonSerializationException extends RuntimeException {

    /**
     * Конструктор для создания исключения с сообщением и причиной.
     *
     * @param message сообщение об ошибке
     * @param cause   причина возникновения исключения
     */
    public JsonSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
