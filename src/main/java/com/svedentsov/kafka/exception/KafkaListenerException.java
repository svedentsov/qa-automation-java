package com.svedentsov.kafka.exception;

/**
 * Базовое исключение для ошибок в подсистеме Kafka listener.
 * Все специфические исключения, связанные с listener'ами, должны наследоваться от этого класса.
 */
public class KafkaListenerException extends RuntimeException {

    /**
     * Создаёт исключение с указанным подробным сообщением.
     *
     * @param message текст, описывающий суть ошибки.
     */
    public KafkaListenerException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение с указанным подробным сообщением и причиной.
     *
     * @param message текст, описывающий суть ошибки.
     * @param cause   оригинальное исключение, которое вызвало данное.
     */
    public KafkaListenerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Создаёт исключение с указанной причиной.
     *
     * @param cause оригинальное исключение, которое вызвало данное.
     */
    public KafkaListenerException(Throwable cause) {
        super(cause);
    }

    /**
     * Исключение для ошибок конфигурации listener-ов.
     * Возникает, когда параметры конфигурации неверны или отсутствуют.
     */
    public static class ConfigurationException extends KafkaListenerException {
        /**
         * Создаёт исключение конфигурации с указанным сообщением.
         *
         * @param message текст ошибки конфигурации.
         */
        public ConfigurationException(String message) {
            super("Конфигурация: " + message);
        }
    }

    /**
     * Исключение для ошибок в жизненном цикле listener.
     * Возникает во время запуска, остановки или других операций управления listener'ом.
     */
    public static class LifecycleException extends KafkaListenerException {
        /**
         * Создаёт исключение жизненного цикла с указанным сообщением и причиной.
         *
         * @param message текст ошибки жизненного цикла.
         * @param cause   оригинальное исключение, которое вызвало данное.
         */
        public LifecycleException(String message, Throwable cause) {
            super("Жизненный цикл: " + message, cause);
        }
    }

    /**
     * Исключение для ошибок при обработке сообщений.
     * Возникает, когда происходит сбой при чтении, десериализации или обработке записи Kafka.
     */
    public static class ProcessingException extends KafkaListenerException {
        /**
         * Создаёт исключение обработки сообщения с указанным сообщением и причиной.
         *
         * @param message текст ошибки обработки.
         * @param cause   оригинальное исключение, которое вызвало данное.
         */
        public ProcessingException(String message, Throwable cause) {
            super("Обработка: " + message, cause);
        }
    }
}
