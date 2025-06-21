package com.svedentsov.kafka.helper;

import com.svedentsov.kafka.config.KafkaListenerConfig;
import com.svedentsov.kafka.exception.KafkaListenerException;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Менеджер для управления жизненным циклом KafkaTopicListener.
 * Позволяет асинхронно и синхронно запускать и останавливать прослушивание топиков.
 */
@Slf4j
public class KafkaListenerManager {

    /**
     * Активные listener-ы по имени топика.
     */
    private final ConcurrentMap<String, KafkaTopicListener> listeners = new ConcurrentHashMap<>();

    /**
     * Конфигурация listener-ов.
     */
    private final KafkaListenerConfig config;

    /**
     * Флаг, что shutdown уже запрошен.
     */
    private volatile boolean shutdownRequested = false;

    /**
     * Конструктор.
     *
     * @param config конфигурация KafkaListenerConfig, не null
     * @throws IllegalArgumentException если config == null
     */
    public KafkaListenerManager(KafkaListenerConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("KafkaListenerConfig не может быть null");
        }
        this.config = config;
        // Регистрируем shutdown hook для graceful shutdown при завершении JVM
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "kafka-listener-shutdown-hook"));
    }

    /**
     * Асинхронно запускает прослушивание топика.
     *
     * @param topic   имя топика, не null/не пустое
     * @param timeout Duration для poll
     * @param isAvro  true если Avro-формат, false для строкового
     * @return CompletableFuture, который завершается после старта listener
     * @throws IllegalStateException    если уже выполнен shutdown менеджера
     * @throws IllegalArgumentException если topic некорректно
     */
    public CompletableFuture<Void> startListeningAsync(String topic, Duration timeout, boolean isAvro) {
        ensureNotShutdown();
        validateTopic(topic);
        return CompletableFuture.runAsync(() -> safeStart(topic, timeout, isAvro), config.getExecutorService());
    }

    /**
     * Синхронно запускает прослушивание топика (ожидает завершения старта).
     *
     * @param topic   имя топика
     * @param timeout Duration для poll
     * @param isAvro  true если Avro
     */
    public void startListening(String topic, Duration timeout, boolean isAvro) {
        startListeningAsync(topic, timeout, isAvro).join();
    }

    /**
     * Останавливает прослушивание топика.
     *
     * @param topic имя топика
     * @return true если listener был найден и остановлен, false если не найден
     * @throws IllegalArgumentException если topic некорректно
     * @throws KafkaListenerException   при ошибке остановки
     */
    public boolean stopListening(String topic) {
        validateTopic(topic);
        KafkaTopicListener listener = listeners.remove(topic);
        if (listener == null) {
            log.warn("Listener для топика '{}' не найден", topic);
            return false;
        }
        try {
            listener.shutdown();
            log.info("Listener для топика '{}' остановлен", topic);
            return true;
        } catch (Exception e) {
            log.error("Ошибка остановки listener для топика '{}'", topic, e);
            throw new KafkaListenerException("Ошибка остановки listener для " + topic, e);
        }
    }

    /**
     * Останавливает все активные listener-ы и завершает ExecutorService.
     * Если уже вызывался, повторный вызов игнорируется.
     */
    public void shutdown() {
        if (!shutdownRequested) {
            shutdownRequested = true;
            log.info("Начинаем остановку всех listener-ов: активных {}", listeners.size());
            listeners.keySet().forEach(this::stopListening);
            try {
                var es = config.getExecutorService();
                es.shutdown();
                if (!es.awaitTermination(config.getShutdownTimeout().toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS)) {
                    es.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Interrupted при shutdown ExecutorService", e);
            }
            log.info("Все listener-ы остановлены");
        }
    }

    /**
     * Проверяет, запущен ли listener для данного топика.
     *
     * @param topic имя топика
     * @return true, если listener активен
     */
    public boolean isListening(String topic) {
        return listeners.containsKey(topic);
    }

    /**
     * Возвращает число активных listener-ов.
     *
     * @return количество активных listener-ов
     */
    public int getActiveCount() {
        return listeners.size();
    }

    /**
     * Внутренний метод запуска listener: создаёт и запускает KafkaTopicListener.
     *
     * @param topic   имя топика
     * @param timeout Duration для poll
     * @param isAvro  true если Avro
     * @throws KafkaListenerException при ошибках запуска
     */
    private void safeStart(String topic, Duration timeout, boolean isAvro) {
        try {
            startListeningInternal(topic, timeout, isAvro);
        } catch (Exception e) {
            log.error("Не удалось запустить listener для '{}'", topic, e);
            throw new KafkaListenerException("Не удалось запустить прослушивание " + topic, e);
        }
    }

    /**
     * Внутренний запуск listener. Бросает, если listener уже активен или ошибка при старте.
     *
     * @param topic   имя топика
     * @param timeout Duration для poll
     * @param isAvro  true если Avro
     */
    private void startListeningInternal(String topic, Duration timeout, boolean isAvro) {
        if (listeners.containsKey(topic)) {
            throw new KafkaListenerException("Listener уже активен для топика: " + topic);
        }
        var listener = new KafkaTopicListener(topic, timeout, isAvro, config);
        listeners.put(topic, listener);
        try {
            listener.start();
            log.info("Listener для топика '{}' успешно запущен", topic);
        } catch (Exception e) {
            listeners.remove(topic);
            throw new KafkaListenerException("Ошибка запуска listener для " + topic, e);
        }
    }

    /**
     * Проверяет, что shutdownManager ещё не вызывался.
     */
    private void ensureNotShutdown() {
        if (shutdownRequested) {
            throw new IllegalStateException("Менеджер уже в состоянии shutdown");
        }
    }

    /**
     * Проверяет корректность имени топика.
     *
     * @param topic имя топика
     * @throws IllegalArgumentException если topic null или пустой
     */
    private void validateTopic(String topic) {
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("Неверное имя топика");
        }
    }
}
