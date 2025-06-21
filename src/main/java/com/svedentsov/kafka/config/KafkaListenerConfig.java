package com.svedentsov.kafka.config;

import lombok.Builder;
import lombok.Value;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Конфигурация для KafkaListenerManager.
 * Содержит настройки таймаутов, поведение при ошибках, пул потоков и метрики.
 * Класс является неизменяемым (immutable).
 */
@Value
public class KafkaListenerConfig {

    private static final Duration DEFAULT_SHUTDOWN_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration DEFAULT_CONSUMER_CLOSE_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration DEFAULT_ERROR_RETRY_DELAY = Duration.ofSeconds(5);
    private static final boolean DEFAULT_STOP_ON_ERROR = false;
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final boolean DEFAULT_ENABLE_METRICS = true;

    /**
     * Таймаут ожидания завершения задач при shutdown общего ExecutorService.
     */
    private final Duration shutdownTimeout;
    /**
     * Таймаут при закрытии Kafka consumer.
     */
    private final Duration consumerCloseTimeout;
    /**
     * Задержка перед повторной попыткой после ошибки в listener.
     */
    private final Duration errorRetryDelay;
    /**
     * Флаг: останавливать ли listener при критической ошибке.
     */
    private final boolean stopOnError;
    /**
     * Максимальное число попыток обработки (может использоваться в custom-логике).
     */
    private final int maxRetries;
    /**
     * Флаг: включать ли сбор метрик/логирование количества обработанных записей.
     */
    private final boolean enableMetrics;
    /**
     * ExecutorService для запуска асинхронных задач listenerов.
     * Должен быть передан извне для большей гибкости и удобства тестирования.
     * Важно: ExecutorService должен управляться внешним кодом (shutdown и т.д.).
     */
    private final ExecutorService executorService;

    @Builder
    private KafkaListenerConfig(
            Duration shutdownTimeout,
            Duration consumerCloseTimeout,
            Duration errorRetryDelay,
            boolean stopOnError,
            int maxRetries,
            boolean enableMetrics,
            ExecutorService executorService) {
        this.shutdownTimeout = shutdownTimeout != null ? shutdownTimeout : DEFAULT_SHUTDOWN_TIMEOUT;
        this.consumerCloseTimeout = consumerCloseTimeout != null ? consumerCloseTimeout : DEFAULT_CONSUMER_CLOSE_TIMEOUT;
        this.errorRetryDelay = errorRetryDelay != null ? errorRetryDelay : DEFAULT_ERROR_RETRY_DELAY;
        this.stopOnError = stopOnError; // boolean не может быть null, так что не нужно проверять
        this.maxRetries = maxRetries;
        this.enableMetrics = enableMetrics; // boolean не может быть null, так что не нужно проверять
        this.executorService = executorService != null ? executorService : KafkaExecutorServiceFactory.createDefaultExecutorService();
    }

    /**
     * Проверяет, нужно ли останавливать listener при ошибке.
     *
     * @return true, если слушатель должен остановиться при ошибке; false — продолжать попытки.
     */
    public boolean shouldStopOnError() {
        return stopOnError;
    }

    /**
     * Фабрика для создания экземпляров KafkaListenerConfig для различных окружений.
     */
    public static class EnvConfig {
        /**
         * Конфигурация для режима разработки.
         * Таймауты короткие, метрики включены, не останавливаем при ошибках.
         * Использует стандартный ExecutorService.
         *
         * @return экземпляр KafkaListenerConfig для dev
         */
        public static KafkaListenerConfig development() {
            return KafkaListenerConfig.builder()
                    .shutdownTimeout(Duration.ofSeconds(10))
                    .errorRetryDelay(Duration.ofSeconds(2))
                    .stopOnError(false)
                    .enableMetrics(true)
                    .build();
        }

        /**
         * Конфигурация для продакшена.
         * Удлинённый shutdownTimeout, останавливаем при ошибках, больше maxRetries.
         * Использует стандартный ExecutorService.
         *
         * @return экземпляр KafkaListenerConfig для prod
         */
        public static KafkaListenerConfig production() {
            return KafkaListenerConfig.builder()
                    .shutdownTimeout(Duration.ofSeconds(60))
                    .errorRetryDelay(Duration.ofSeconds(10))
                    .stopOnError(true)
                    .maxRetries(5)
                    .enableMetrics(true)
                    .build();
        }

        /**
         * Конфигурация для тестирования.
         * Быстрые таймауты, метрики отключены, фиксированный пул из 2 потоков.
         * Создает свой ExecutorService, чтобы он был изолирован для тестов.
         *
         * @return экземпляр KafkaListenerConfig для тестов
         */
        public static KafkaListenerConfig testing() {
            return KafkaListenerConfig.builder()
                    .shutdownTimeout(Duration.ofSeconds(5))
                    .consumerCloseTimeout(Duration.ofSeconds(2))
                    .errorRetryDelay(Duration.ofMillis(100))
                    .stopOnError(true)
                    .enableMetrics(false)
                    .executorService(KafkaExecutorServiceFactory.createFixedThreadPool(2))
                    .build();
        }
    }

    /**
     * Фабрика для создания и управления ExecutorService, связанного с KafkaListener.
     */
    public static class KafkaExecutorServiceFactory {
        private static final String THREAD_PREFIX = "kafka-listener-";

        /**
         * Создает ExecutorService по умолчанию (cached thread pool) для Kafka Listener-ов.
         *
         * @return новый CachedThreadPool с именованными потоками.
         */
        public static ExecutorService createDefaultExecutorService() {
            return Executors.newCachedThreadPool(new KafkaListenerThreadFactory(THREAD_PREFIX));
        }

        /**
         * Создает FixedThreadPool для Kafka Listener-ов.
         * Используется для контролируемого поведения, например, в тестах.
         *
         * @param nThreads количество потоков в пуле.
         * @return новый FixedThreadPool с именованными потоками.
         */
        public static ExecutorService createFixedThreadPool(int nThreads) {
            return Executors.newFixedThreadPool(nThreads, new KafkaListenerThreadFactory(THREAD_PREFIX));
        }
    }

    /**
     * Фабрика потоков для listener-ов с читаемым именем.
     */
    private static class KafkaListenerThreadFactory implements ThreadFactory {
        private final AtomicInteger counter = new AtomicInteger(1);
        private final String prefix;

        public KafkaListenerThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, prefix + counter.getAndIncrement());
            t.setDaemon(false); // Обычно потоки пула не должны быть демонами
            t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}