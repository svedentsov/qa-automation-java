package com.svedentsov.kafka.helper;

import com.svedentsov.kafka.config.KafkaListenerConfig;
import com.svedentsov.kafka.exception.KafkaListenerException.LifecycleException;
import com.svedentsov.kafka.factory.ConsumerFactory;
import com.svedentsov.kafka.helper.KafkaTopicListener.ConsumerStartStrategy;
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
        // Добавляем хук завершения работы JVM для корректного останова всех слушателей
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "kafka-listener-shutdown-hook"));
    }

    /**
     * Асинхронно запускает прослушивание указанного топика с заданной стратегией старта.
     *
     * @param topic            Имя топика. Не может быть пустым.
     * @param pollTimeout      Таймаут для опроса Kafka.
     * @param isAvro           {@code true}, если используется формат Avro, иначе {@code false}.
     * @param recordsManager   Экземпляр менеджера для хранения полученных записей.
     * @param startStrategy    Стратегия, определяющая, с какого смещения начать чтение.
     * @param lookBackDuration Продолжительность, на которую нужно "оглянуться" назад,
     *                         если {@code startStrategy} - {@link ConsumerStartStrategy#FROM_TIMESTAMP}.
     *                         Может быть {@code null} для других стратегий.
     * @return {@link CompletableFuture<Void>}, который завершается, когда слушатель успешно запущен.
     * @throws IllegalStateException    если менеджер находится в процессе завершения работы.
     * @throws IllegalArgumentException если {@code topic} пустой, {@code recordsManager} равен {@code null},
     *                                  или {@code startStrategy} равен {@link ConsumerStartStrategy#FROM_TIMESTAMP}, но {@code lookBackDuration} равен {@code null}.
     */
    public CompletableFuture<Void> startListeningAsync(String topic, Duration pollTimeout, boolean isAvro, KafkaRecordsManager recordsManager, ConsumerStartStrategy startStrategy, Duration lookBackDuration) {
        requireNonBlank(topic, "Имя топика не может быть null или пустым.");
        requireNonNull(recordsManager, "KafkaRecordsManager не может быть null.");
        ensureNotShutdown(); // Проверяем, не был ли менеджер уже остановлен

        return CompletableFuture.runAsync(() -> {
            // Используем computeIfAbsent для атомарного создания и запуска слушателя, если его еще нет
            listeners.computeIfAbsent(topic, t -> {
                log.info("Создание и запуск нового слушателя для топика '{}' со стратегией: {}.", t, startStrategy);
                KafkaTopicListener newListener = new KafkaTopicListener(t, pollTimeout, isAvro, config, consumerFactory, recordsManager, startStrategy, lookBackDuration);
                newListener.start(executorService); // Запускаем слушатель
                return newListener;
            });
        }, executorService).exceptionally(ex -> {
            log.error("Не удалось запустить слушатель для топика '{}'", topic, ex);
            listeners.remove(topic); // Удаляем слушателя из карты в случае ошибки запуска
            throw new LifecycleException("Ошибка при асинхронном запуске слушателя для " + topic, ex);
        });
    }

    /**
     * Блокирует выполнение до тех пор, пока прослушивание указанного топика не будет запущено
     * с заданной стратегией. Это синхронный обертка над {@link #startListeningAsync}.
     *
     * @param topic            Имя топика.
     * @param pollTimeout      Таймаут для опроса.
     * @param isAvro           Используется ли Avro.
     * @param recordsManager   Менеджер записей.
     * @param startStrategy    Стратегия, определяющая, с какого смещения начать чтение.
     * @param lookBackDuration Продолжительность, на которую нужно "оглянуться" назад.
     * @throws RuntimeException если при запуске слушателя возникает какая-либо ошибка.
     */
    public void startListening(String topic, Duration pollTimeout, boolean isAvro, KafkaRecordsManager recordsManager, ConsumerStartStrategy startStrategy, Duration lookBackDuration) {
        try {
            startListeningAsync(topic, pollTimeout, isAvro, recordsManager, startStrategy, lookBackDuration).join();
        } catch (CompletionException e) {
            // Разворачиваем CompletionException, чтобы выбросить оригинальное исключение
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw e;
        }
    }

    /**
     * Останавливает прослушивание указанного топика.
     * Если слушатель для данного топика найден, он будет корректно остановлен и удален из менеджера.
     *
     * @param topic Имя топика.
     * @return {@code true}, если слушатель был найден и успешно остановлен; иначе {@code false}.
     * @throws IllegalArgumentException если {@code topic} пустой.
     */
    public boolean stopListening(String topic) {
        requireNonBlank(topic, "Имя топика не может быть null или пустым.");
        KafkaTopicListener listener = listeners.remove(topic); // Удаляем слушатель из карты
        if (listener == null) {
            log.warn("Слушатель для топика '{}' не найден или уже был остановлен.", topic);
            return false;
        }
        try {
            log.info("Остановка слушателя для топика '{}'...", topic);
            listener.shutdown(); // Вызываем метод остановки слушателя
            log.info("Слушатель для топика '{}' успешно остановлен.", topic);
            return true;
        } catch (Exception e) {
            log.error("Ошибка во время остановки слушателя для топика '{}'", topic, e);
            return false;
        }
    }

    /**
     * Инициирует процесс завершения работы всех активных слушателей и базового ExecutorService.
     * Метод является идемпотентным, то есть повторные вызовы не приведут к дополнительным эффектам.
     * Вызывается автоматически при завершении работы JVM через хук завершения работы.
     */
    public void shutdown() {
        if (shutdownInitiated.compareAndSet(false, true)) { // Гарантируем однократное выполнение
            log.info("Начало процесса завершения работы KafkaListenerManager. Активных слушателей: {}", listeners.size());
            // Останавливаем всех активных слушателей
            for (String topic : Set.copyOf(listeners.keySet())) { // Создаем копию ключей, чтобы избежать ConcurrentModificationException
                stopListening(topic);
            }
            shutdownExecutorService(); // Завершаем работу ExecutorService
            log.info("KafkaListenerManager и все его ресурсы были успешно освобождены.");
        }
    }

    /**
     * Завершает работу внутреннего {@link ExecutorService}, используемого для запуска слушателей.
     * Ожидает завершения всех задач в течение заданного таймаута, после чего принудительно останавливает их.
     */
    private void shutdownExecutorService() {
        executorService.shutdown(); // Инициируем корректное завершение работы
        try {
            // Ожидаем завершения задач
            if (!executorService.awaitTermination(config.getShutdownTimeout().toMillis(), TimeUnit.MILLISECONDS)) {
                log.warn("Тайм-аут ожидания завершения задач в ExecutorService. Принудительная остановка.");
                executorService.shutdownNow(); // Принудительно останавливаем невыполненные задачи
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Восстанавливаем флаг прерывания
            log.error("Процесс ожидания завершения ExecutorService был прерван.", e);
            executorService.shutdownNow(); // Принудительно останавливаем в случае прерывания
        }
    }

    /**
     * Закрывает менеджер слушателей, вызывая метод {@link #shutdown()}.
     * Позволяет использовать {@code KafkaListenerManager} в конструкции try-with-resources.
     */
    @Override
    public void close() {
        shutdown();
    }

    /**
     * Проверяет, не находится ли менеджер в процессе завершения работы.
     *
     * @throws IllegalStateException если менеджер уже инициировал процесс завершения работы.
     */
    private void ensureNotShutdown() {
        if (shutdownInitiated.get()) {
            throw new IllegalStateException("Менеджер уже в состоянии shutdown. Невозможно запустить новые слушатели.");
        }
    }

    /**
     * Проверяет, активен ли слушатель для указанного топика.
     *
     * @param topic Имя топика.
     * @return {@code true}, если слушатель для данного топика существует и активен; иначе {@code false}.
     */
    public boolean isListening(String topic) {
        KafkaTopicListener listener = listeners.get(topic);
        return listener != null && listener.isRunning();
    }

    /**
     * Возвращает количество активных слушателей, управляемых этим менеджером.
     * Активным считается слушатель, который запущен и не находится в процессе остановки.
     *
     * @return Количество активных слушателей.
     */
    public int getActiveCount() {
        return (int) listeners.values().stream().filter(KafkaTopicListener::isRunning).count();
    }
}
