package com.svedentsov.core.exception;

/**
 * Исключение, выбрасываемое при несоответствии записи условиям валидации.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
