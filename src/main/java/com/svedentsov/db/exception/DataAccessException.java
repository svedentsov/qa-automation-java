package com.svedentsov.db.exception;

/**
 * Непроверяемое (unchecked) исключение, выбрасываемое при ошибках доступа к данным.
 * Используется как обертка для специфичных исключений от провайдера персистентности (например, HibernateException),
 * чтобы скрыть детали реализации от вышестоящих слоев приложения.
 */
public class DataAccessException extends RuntimeException {
    /**
     * Конструктор исключения DataAccessException.
     *
     * @param message сообщение об ошибке.
     * @param cause   причина исключения (оригинальное исключение от ORM или JDBC).
     */
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}