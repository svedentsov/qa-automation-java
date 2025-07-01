package com.svedentsov.kafka.helper;

import com.svedentsov.kafka.config.KafkaListenerConfig;
import com.svedentsov.kafka.exception.KafkaListenerException.LifecycleException;
import com.svedentsov.kafka.factory.ConsumerFactory;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.svedentsov.kafka.utils.ValidationUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * Управляет жизненным циклом слушателей топиков Kafka ({@link KafkaTopicListener}).
 * Этот класс отвечает за асинхронный запуск, остановку и мониторинг активных слушателей.
 * Он обеспечивает корректное завершение работы (graceful shutdown) при остановке JVM
 * или при вызове метода {@link #shutdown()}.
 */
@Slf4j
public class KafkaListenerManager implements AutoCloseable {

    private final ConcurrentMap<String, KafkaTopicListener> listeners = new ConcurrentHashMap<>();
    private final AtomicBoolean shutdownInitiated = new AtomicBoolean(false);
    private final KafkaListenerConfig config;
    private final ConsumerFactory consumerFactory;
    private final ExecutorService executorService;

    /**
     * Создает экземпляр менеджера слушателей.
     *
     * @param config          Конфигурация для слушателей. Не может быть {@code null}.
     * @param consumerFactory Фабрика для создания Kafka Consumers. Не может быть {@code null}.
     */
    public KafkaListenerManager(KafkaListenerConfig config, ConsumerFactory consumerFactory) {
        this.config = requireNonNull(config, "KafkaListenerConfig не может быть null");
        this.consumerFactory = requireNonNull(consumerFactory, "ConsumerFactory не может быть null");
        this.executorService = config.getExecutorService(); // Используем ExecutorService из конфига
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "kafka-listener-shutdown-hook"));
    }

    /**
     * Асинхронно запускает прослушивание указанного топика.
     *
     * @param topic          Имя топика. Не может быть пустым.
     * @param pollTimeout    Тайм-аут для опроса Kafka.
     * @param isAvro         {@code true}, если используется формат Avro, иначе {@code false}.
     * @param recordsManager Экземпляр менеджера для хранения полученных записей.
     * @return {@link CompletableFuture<Void>}, который завершается, когда слушатель успешно запущен.
     * @throws IllegalStateException если менеджер находится в процессе завершения работы.
     */
    public CompletableFuture<Void> startListeningAsync(String topic, Duration pollTimeout, boolean isAvro, KafkaRecordsManager recordsManager) {
        requireNonBlank(topic, "Имя топика не может быть null или пустым.");
        requireNonNull(recordsManager, "KafkaRecordsManager не может быть null.");
        ensureNotShutdown();

        return CompletableFuture.runAsync(() -> {
            listeners.computeIfAbsent(topic, t -> {
                log.info("Создание и запуск нового слушателя для топика '{}'.", t);
                KafkaTopicListener newListener = new KafkaTopicListener(t, pollTimeout, isAvro, config, consumerFactory, recordsManager);
                newListener.start(executorService);
                return newListener;
            });
        }, executorService).exceptionally(ex -> {
            log.error("Не удалось запустить слушатель для топика '{}'", topic, ex);
            listeners.remove(topic); // Удаляем в случае ошибки запуска
            throw new LifecycleException("Ошибка при асинхронном запуске слушателя для " + topic, ex);
        });
    }

    /**
     * Блокируется до тех пор, пока прослушивание не будет запущено.
     *
     * @param topic          Имя топика.
     * @param pollTimeout    Тайм-аут для опроса.
     * @param isAvro         Используется ли Avro.
     * @param recordsManager Менеджер записей.
     */
    public void startListening(String topic, Duration pollTimeout, boolean isAvro, KafkaRecordsManager recordsManager) {
        try {
            startListeningAsync(topic, pollTimeout, isAvro, recordsManager).join();
        } catch (CompletionException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw e;
        }
    }

    /**
     * Останавливает прослушивание указанного топика.
     *
     * @param topic Имя топика.
     * @return {@code true}, если слушатель был найден и остановлен, иначе {@code false}.
     */
    public boolean stopListening(String topic) {
        requireNonBlank(topic, "Имя топика не может быть null или пустым.");
        KafkaTopicListener listener = listeners.remove(topic);
        if (listener == null) {
            log.warn("Слушатель для топика '{}' не найден или уже был остановлен.", topic);
            return false;
        }
        try {
            log.info("Остановка слушателя для топика '{}'...", topic);
            listener.shutdown();
            log.info("Слушатель для топика '{}' успешно остановлен.", topic);
            return true;
        } catch (Exception e) {
            log.error("Ошибка во время остановки слушателя для топика '{}'", topic, e);
            return false;
        }
    }

    /**
     * Инициирует процесс завершения работы всех активных слушателей и ExecutorService.
     * Метод является идемпотентным.
     */
    public void shutdown() {
        if (shutdownInitiated.compareAndSet(false, true)) {
            log.info("Начало процесса завершения работы KafkaListenerManager. Активных слушателей: {}", listeners.size());
            for (String topic : Set.copyOf(listeners.keySet())) {
                stopListening(topic);
            }
            shutdownExecutorService();
            log.info("KafkaListenerManager и все его ресурсы были успешно освобождены.");
        }
    }

    private void shutdownExecutorService() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(config.getShutdownTimeout().toMillis(), TimeUnit.MILLISECONDS)) {
                log.warn("Тайм-аут ожидания завершения задач в ExecutorService. Принудительная остановка.");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Процесс ожидания завершения ExecutorService был прерван.", e);
            executorService.shutdownNow();
        }
    }

    @Override
    public void close() {
        shutdown();
    }

    private void ensureNotShutdown() {
        if (shutdownInitiated.get()) {
            throw new IllegalStateException("Менеджер уже в состоянии shutdown. Невозможно запустить новые слушатели.");
        }
    }

    /**
     * Проверяет, активен ли слушатель для указанного топика.
     *
     * @param topic Имя топика.
     * @return {@code true}, если слушатель активен.
     */
    public boolean isListening(String topic) {
        KafkaTopicListener listener = listeners.get(topic);
        return listener != null && listener.isRunning();
    }

    /**
     * Возвращает количество активных слушателей.
     *
     * @return Количество активных слушателей.
     */
    public int getActiveCount() {
        return (int) listeners.values().stream().filter(KafkaTopicListener::isRunning).count();
    }
}
