package kafka.exception;

/**
 * Исключение, возникающее при ошибках десериализации JSON.
 */
public class JsonDeserializationException extends RuntimeException {
    /**
     * Конструктор для создания исключения с сообщением и причиной.
     *
     * @param message сообщение об ошибке
     * @param cause   причина возникновения исключения
     */
    public JsonDeserializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
