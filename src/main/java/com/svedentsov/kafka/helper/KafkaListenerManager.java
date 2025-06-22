package com.svedentsov.kafka.helper;

import com.svedentsov.kafka.config.KafkaListenerConfig;
import com.svedentsov.kafka.exception.KafkaListenerException;
import com.svedentsov.kafka.exception.KafkaListenerException.LifecycleException;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.svedentsov.kafka.utils.ValidationUtils.requireNonBlank;

/**
 * Менеджер для управления жизненным циклом {@link KafkaTopicListener}.
 * Позволяет асинхронно и синхронно запускать и останавливать прослушивание топиков.
 * Обеспечивает безопасное завершение работы всех активных слушателей при выключении приложения.
 */
@Slf4j
public class KafkaListenerManager implements AutoCloseable {

    /**
     * Карта активных {@link KafkaTopicListener}-ов, где ключ - имя топика.
     */
    private final ConcurrentMap<String, KafkaTopicListener> listeners = new ConcurrentHashMap<>();

    /**
     * Конфигурация listener-ов, определяющая таймауты, поведение при ошибках и пул потоков.
     */
    private final KafkaListenerConfig config;

    /**
     * Атомарный флаг, указывающий, был ли уже запрошен shutdown менеджера.
     */
    private final AtomicBoolean shutdownRequested = new AtomicBoolean(false);

    /**
     * Конструктор менеджера слушателей.
     *
     * @param config конфигурация {@link KafkaListenerConfig}, не может быть {@code null}.
     * @throws IllegalArgumentException если {@code config} равен {@code null}.
     */
    public KafkaListenerManager(KafkaListenerConfig config) {
        this.config = Objects.requireNonNull(config, "KafkaListenerConfig не может быть null");
        // Регистрируем shutdown hook для graceful shutdown при завершении JVM.
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "kafka-listener-shutdown-hook"));
    }

    /**
     * Синхронно запускает прослушивание топика. Блокируется до запуска или ошибки.
     *
     * @param topic   имя топика.
     * @param timeout {@link Duration} для операции poll.
     * @param isAvro  {@code true}, если ожидаются Avro-сообщения; {@code false} для строковых.
     */
    public void startListening(String topic, Duration timeout, boolean isAvro) {
        startListeningAsync(topic, timeout, isAvro).join();
    }

    /**
     * Асинхронно запускает прослушивание указанного топика.
     *
     * @param topic   имя топика, не может быть {@code null} или пустым.
     * @param timeout {@link Duration} для операции poll в KafkaConsumer.
     * @param isAvro  {@code true}, если ожидаются Avro-сообщения; {@code false} для строковых сообщений.
     * @return {@link CompletableFuture<Void>}, представляющий асинхронную операцию запуска.
     * @throws IllegalStateException    если менеджер уже находится в состоянии shutdown.
     * @throws IllegalArgumentException если {@code topic} некорректно.
     * @throws KafkaListenerException   если listener для данного топика уже активен или произошла внутренняя ошибка запуска.
     */
    public CompletableFuture<Void> startListeningAsync(String topic, Duration timeout, boolean isAvro) {
        requireNonBlank(topic, "Имя топика не может быть null или пустым.");
        ensureNotShutdown();

        if (listeners.containsKey(topic)) {
            log.warn("Попытка повторного запуска слушателя для топика '{}'. Он уже активен.", topic);
            return CompletableFuture.completedFuture(null);
        }

        KafkaTopicListener newListener = new KafkaTopicListener(topic, timeout, isAvro, config);
        KafkaTopicListener existing = listeners.putIfAbsent(topic, newListener);
        if (existing != null) {
            log.warn("Гонка потоков: listener для топика '{}' был добавлен другим потоком.", topic);
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.runAsync(() -> newListener.start(), config.getExecutorService())
                .exceptionally(throwable -> {
                    log.error("Критическая ошибка при запуске слушателя для топика '{}'", topic, throwable);
                    listeners.remove(topic);
                    throw new LifecycleException("Не удалось запустить прослушивание " + topic, throwable);
                });
    }

    /**
     * Останавливает прослушивание указанного топика.
     *
     * @param topic имя топика.
     * @return {@code true}, если listener был найден и инициирован к остановке; {@code false}, если listener не найден.
     * @throws IllegalArgumentException если {@code topic} некорректно.
     * @throws KafkaListenerException   при ошибке остановки listener-а.
     */
    public boolean stopListening(String topic) throws LifecycleException {
        requireNonBlank(topic, "Имя топика не может быть null или пустым.");
        KafkaTopicListener listener = listeners.remove(topic);
        if (listener == null) {
            log.warn("Listener для топика '{}' не найден или уже был остановлен.", topic);
            return false;
        }
        try {
            listener.shutdown();
            log.info("Listener для топика '{}' остановлен", topic);
            return true;
        } catch (Exception e) {
            log.error("Ошибка остановки listener для топика '{}'", topic, e);
            throw new LifecycleException("Ошибка остановки listener для " + topic, e);
        }
    }

    /**
     * Проверяет, что менеджер ещё не находится в состоянии shutdown.
     *
     * @throws IllegalStateException если менеджер уже завершил shutdown.
     */
    private void ensureNotShutdown() {
        if (shutdownRequested.get()) {
            throw new IllegalStateException("Менеджер уже в состоянии shutdown. Невозможно запустить новые слушатели.");
        }
    }

    /**
     * Останавливает все активные listener-ы и завершает ExecutorService.
     * Если уже вызывался ранее, повторный вызов игнорируется.
     */
    @Override
    public void close() {
        shutdown();
    }

    /**
     * Останавливает все активные listener-ы и завершает ExecutorService, если возможно.
     */
    public void shutdown() {
        if (shutdownRequested.compareAndSet(false, true)) {
            log.info("Начинаем остановку всех listener-ов: активных {}", listeners.size());
            for (String topic : listeners.keySet()) {
                stopListening(topic); // Останавливаем каждый listener
            }
            try {
                ExecutorService es = config.getExecutorService();
                es.shutdown();
                if (!es.awaitTermination(config.getShutdownTimeout().toMillis(), TimeUnit.MILLISECONDS)) {
                    log.warn("Таймаут ожидания завершения задач в ExecutorService. Принудительная остановка.");
                    es.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Менеджер KafkaListenerManager был прерван во время ожидания завершения ExecutorService.", e);
            }
            log.info("Все listener-ы остановлены и ExecutorService завершен.");
        } else {
            log.debug("Запрос на остановку уже был выполнен, игнорируем повторный вызов.");
        }
    }

    /**
     * Проверяет, запущен ли listener для данного топика.
     *
     * @param topic имя топика.
     * @return {@code true}, если listener активен; {@code false} иначе.
     */
    public boolean isListening(String topic) {
        KafkaTopicListener listener = listeners.get(topic);
        return listener != null && listener.isRunning();
    }

    /**
     * Возвращает число активных listener-ов.
     *
     * @return количество активных {@link KafkaTopicListener}-ов.
     */
    public int getActiveCount() {
        return listeners.size();
    }
}
