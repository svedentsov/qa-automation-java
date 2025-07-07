package com.svedentsov.kafka.config;

import lombok.Builder;
import lombok.Value;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Неизменяемый (immutable) класс конфигурации для {@code KafkaListenerManager}.
 * Содержит настройки таймаутов, поведение при ошибках и пул потоков.
 * Рекомендуется создавать экземпляры с помощью паттерна "Строитель" (Builder), который генерируется Lombok.
 */
@Value
@Builder(toBuilder = true)
public class KafkaConfigListener {

    private static final Duration DEFAULT_SHUTDOWN_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration DEFAULT_CONSUMER_CLOSE_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration DEFAULT_ERROR_RETRY_DELAY = Duration.ofSeconds(5);
    private static final boolean DEFAULT_STOP_ON_ERROR = false;
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final boolean DEFAULT_ENABLE_METRICS = true;

    /**
     * Таймаут ожидания завершения задач при shutdown общего ExecutorService.
     */
    Duration shutdownTimeout;
    /**
     * Таймаут при закрытии Kafka consumer.
     */
    Duration consumerCloseTimeout;
    /**
     * Задержка перед повторной попыткой после ошибки в listener.
     */
    Duration errorRetryDelay;
    /**
     * Флаг: останавливать ли listener при критической ошибке.
     * Если не указан, используется {@value #DEFAULT_STOP_ON_ERROR}.
     */
    boolean stopOnError;
    /**
     * Максимальное число попыток обработки (может использоваться в custom-логике).
     * Если не указан, используется {@value #DEFAULT_MAX_RETRIES}.
     */
    int maxRetries;
    /**
     * Флаг: включать ли сбор метрик/логирование количества обработанных записей.
     * Если не указан, используется {@value #DEFAULT_ENABLE_METRICS}.
     */
    boolean enableMetrics;
    /**
     * ExecutorService для запуска асинхронных задач listenerов.
     * Должен быть передан извне для большей гибкости и удобства тестирования.
     * Важно: ExecutorService должен управляться внешним кодом (shutdown и т.д.),
     * если он не создан через фабрику {@link KafkaExecutorServiceFactory}.
     * Если не указан, используется ExecutorService по умолчанию, созданный через {@link KafkaExecutorServiceFactory#createDefaultExecutorService()}.
     */
    ExecutorService executorService;

    @Builder
    private KafkaConfigListener(Duration shutdownTimeout, Duration consumerCloseTimeout, Duration errorRetryDelay, boolean stopOnError, int maxRetries, boolean enableMetrics, ExecutorService executorService) {
        this.shutdownTimeout = shutdownTimeout != null ? shutdownTimeout : DEFAULT_SHUTDOWN_TIMEOUT;
        this.consumerCloseTimeout = consumerCloseTimeout != null ? consumerCloseTimeout : DEFAULT_CONSUMER_CLOSE_TIMEOUT;
        this.errorRetryDelay = errorRetryDelay != null ? errorRetryDelay : DEFAULT_ERROR_RETRY_DELAY;
        this.stopOnError = stopOnError;
        this.maxRetries = maxRetries;
        this.enableMetrics = enableMetrics;
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
         * @return экземпляр KafkaListenerConfig для dev окружения.
         */
        public static KafkaConfigListener development() {
            return KafkaConfigListener.builder()
                    .shutdownTimeout(Duration.ofSeconds(10))
                    .errorRetryDelay(Duration.ofSeconds(2))
                    .stopOnError(false)
                    .enableMetrics(true)
                    .build();
        }

        /**
         * Конфигурация для тестирования.
         * Быстрые таймауты, метрики отключены, фиксированный пул из 2 потоков.
         * Создает свой ExecutorService, чтобы он был изолирован для тестов.
         *
         * @return экземпляр KafkaListenerConfig для тестового окружения.
         */
        public static KafkaConfigListener testing() {
            return KafkaConfigListener.builder()
                    .shutdownTimeout(Duration.ofSeconds(5))
                    .consumerCloseTimeout(Duration.ofSeconds(2))
                    .errorRetryDelay(Duration.ofMillis(100))
                    .stopOnError(true)
                    .enableMetrics(false)
                    .executorService(KafkaExecutorServiceFactory.createFixedThreadPool(2))
                    .build();
        }

        /**
         * Конфигурация для продакшена.
         * Удлинённый shutdownTimeout, останавливаем при ошибках, больше maxRetries.
         * Использует стандартный ExecutorService.
         *
         * @return экземпляр KafkaListenerConfig для prod окружения.
         */
        public static KafkaConfigListener production() {
            return KafkaConfigListener.builder()
                    .shutdownTimeout(Duration.ofSeconds(60))
                    .errorRetryDelay(Duration.ofSeconds(10))
                    .stopOnError(true)
                    .maxRetries(5)
                    .enableMetrics(true)
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
         * CachedThreadPool создает новые потоки по мере необходимости, но переиспользует существующие.
         *
         * @return новый CachedThreadPool с именованными потоками.
         */
        public static ExecutorService createDefaultExecutorService() {
            return Executors.newCachedThreadPool(new KafkaListenerThreadFactory(THREAD_PREFIX));
        }

        /**
         * Создает FixedThreadPool для Kafka Listener-ов.
         * Используется для контролируемого поведения, например, в тестах,
         * где количество потоков должно быть ограничено.
         *
         * @param nThreads количество потоков в пуле. Должно быть положительным.
         * @return новый FixedThreadPool с именованными потоками.
         * @throws IllegalArgumentException если nThreads меньше или равно 0.
         */
        public static ExecutorService createFixedThreadPool(int nThreads) {
            if (nThreads <= 0) throw new IllegalArgumentException("Количество потоков должно быть положительным");
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
