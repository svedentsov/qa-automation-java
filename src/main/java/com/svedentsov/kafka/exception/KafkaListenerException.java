package com.svedentsov.kafka.exception;

/**
 * Базовое исключение для ошибок в подсистеме Kafka listener.
 */
public class KafkaListenerException extends RuntimeException {

    /**
     * Создаёт исключение с сообщением.
     *
     * @param message текст ошибки
     */
    public KafkaListenerException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение с сообщением и причиной.
     *
     * @param message текст ошибки
     * @param cause   оригинальное исключение
     */
    public KafkaListenerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Создаёт исключение с причиной.
     *
     * @param cause оригинальное исключение
     */
    public KafkaListenerException(Throwable cause) {
        super(cause);
    }

    /**
     * Исключение для ошибок конфигурации listener-ов.
     */
    public static class ConfigurationException extends KafkaListenerException {
        /**
         * @param message текст ошибки конфигурации
         */
        public ConfigurationException(String message) {
            super("Конфигурация: " + message);
        }
    }

    /**
     * Исключение для ошибок в жизненном цикле listener.
     */
    public static class LifecycleException extends KafkaListenerException {
        /**
         * @param message текст ошибки жизненного цикла
         * @param cause   оригинальное исключение
         */
        public LifecycleException(String message, Throwable cause) {
            super("Жизненный цикл: " + message, cause);
        }
    }

    /**
     * Исключение для ошибок при обработке сообщений.
     */
    public static class ProcessingException extends KafkaListenerException {
        /**
         * @param message текст ошибки обработки
         * @param cause   оригинальное исключение
         */
        public ProcessingException(String message, Throwable cause) {
            super("Обработка: " + message, cause);
        }
    }
}
