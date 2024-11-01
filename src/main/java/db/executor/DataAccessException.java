package db.executor;

/**
 * Исключение, выбрасываемое при ошибках доступа к данным.
 */
public class DataAccessException extends RuntimeException {
    /**
     * Конструктор исключения DataAccessException.
     *
     * @param message сообщение об ошибке
     * @param cause   причина исключения
     */
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}