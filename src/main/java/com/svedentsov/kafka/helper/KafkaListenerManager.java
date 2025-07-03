package com.svedentsov.kafka.helper;

import com.svedentsov.kafka.config.KafkaListenerConfig;
import com.svedentsov.kafka.exception.KafkaListenerException.LifecycleException;
import com.svedentsov.kafka.factory.ConsumerFactory;
import com.svedentsov.kafka.helper.strategy.ConsumerStartStrategy;
import com.svedentsov.kafka.helper.strategy.EarliestStartStrategy;
import com.svedentsov.kafka.helper.strategy.FromTimestampStartStrategy;
import com.svedentsov.kafka.helper.strategy.LatestStartStrategy;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.svedentsov.kafka.utils.ValidationUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * {@code KafkaListenerManager} управляет жизненным циклом нескольких {@link KafkaTopicListener}s.
 * Он предоставляет методы для запуска и остановки прослушивания топиков Kafka,
 * а также для корректного завершения работы всех активных слушателей.
 */
@Slf4j
public class KafkaListenerManager implements AutoCloseable {

    private final ConcurrentMap<String, KafkaTopicListener> listeners = new ConcurrentHashMap<>();
    private final AtomicBoolean shutdownInitiated = new AtomicBoolean(false);
    private final KafkaListenerConfig config;
    private final ConsumerFactory consumerFactory;
    private final ExecutorService executorService;

    /**
     * Создает новый экземпляр {@code KafkaListenerManager}.
     * Инициализирует внутренние компоненты и регистрирует хук завершения работы JVM
     * для корректного останова всех слушателей при выключении приложения.
     *
     * @param config          Конфигурация слушателей Kafka. Не может быть null.
     * @param consumerFactory Фабрика для создания экземпляров KafkaConsumer. Не может быть null.
     */
    public KafkaListenerManager(KafkaListenerConfig config, ConsumerFactory consumerFactory) {
        this.config = requireNonNull(config, "KafkaListenerConfig не может быть null");
        this.consumerFactory = requireNonNull(consumerFactory, "ConsumerFactory не может быть null");
        this.executorService = config.getExecutorService(); // Используем ExecutorService из конфига
        // Добавляем хук завершения работы JVM для корректного останова всех слушателей
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "kafka-listener-shutdown-hook"));
    }

    /**
     * Асинхронно запускает прослушивание указанного топика Kafka с заданной стратегией.
     * Если слушатель для данного топика уже существует, он не будет перезапущен.
     * Возвращает {@link CompletableFuture}, который завершится, когда слушатель будет запущен,
     * или с ошибкой, если запуск не удастся.
     *
     * @param topic             Имя топика Kafka для прослушивания. Не может быть null или пустым.
     * @param pollTimeout       Таймаут для операции {@code consumer.poll()}.
     * @param isAvro            Флаг, указывающий, являются ли сообщения в топике Avro или строковыми.
     * @param recordsManager    Менеджер для обработки и хранения полученных записей.
     * @param startStrategyEnum Перечисление стратегии запуска (LATEST, EARLIEST, FROM_TIMESTAMP).
     * @param lookBackDuration  Продолжительность, на которую нужно "оглянуться" назад,
     *                          если {@code startStrategyEnum} - {@code FROM_TIMESTAMP}.
     *                          Может быть null для других стратегий.
     * @return {@link CompletableFuture}, представляющий асинхронную операцию запуска.
     * @throws IllegalArgumentException если {@code topic} пуст или {@code recordsManager} null.
     * @throws IllegalStateException    если менеджер уже находится в состоянии завершения работы.
     * @throws LifecycleException       если происходит ошибка при запуске слушателя.
     */
    public CompletableFuture<Void> startListeningAsync(String topic, Duration pollTimeout, boolean isAvro, KafkaRecordsManager recordsManager, KafkaStartStrategyType startStrategyEnum, Duration lookBackDuration) {
        requireNonBlank(topic, "Имя топика не может быть null или пустым.");
        requireNonNull(recordsManager, "KafkaRecordsManager не может быть null.");
        ensureNotShutdown(); // Проверяем, не был ли менеджер уже остановлен

        return CompletableFuture.runAsync(() -> {
            listeners.computeIfAbsent(topic, t -> {
                log.info("Создание и запуск нового слушателя для топика '{}' со стратегией: {}.", t, startStrategyEnum);
                ConsumerStartStrategy strategy = createConsumerStartStrategy(startStrategyEnum, lookBackDuration);
                KafkaTopicListener newListener = new KafkaTopicListener(t, pollTimeout, isAvro, config, consumerFactory, recordsManager, strategy);
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
     * Синхронно запускает прослушивание указанного топика Kafka с заданной стратегией.
     * Этот метод блокируется до тех пор, пока слушатель не будет запущен или не произойдет ошибка.
     *
     * @param topic             Имя топика Kafka для прослушивания.
     * @param pollTimeout       Таймаут для операции {@code consumer.poll()}.
     * @param isAvro            Флаг, указывающий, являются ли сообщения в топике Avro или строковыми.
     * @param recordsManager    Менеджер для обработки и хранения полученных записей.
     * @param startStrategyEnum Перечисление стратегии запуска (LATEST, EARLIEST, FROM_TIMESTAMP).
     * @param lookBackDuration  Продолжительность, на которую нужно "оглянуться" назад,
     *                          если {@code startStrategyEnum} - {@code FROM_TIMESTAMP}.
     *                          Может быть null для других стратегий.
     * @throws RuntimeException если при запуске слушателя происходит ошибка.
     */
    public void startListening(String topic, Duration pollTimeout, boolean isAvro, KafkaRecordsManager recordsManager, KafkaStartStrategyType startStrategyEnum, Duration lookBackDuration) {
        try {
            startListeningAsync(topic, pollTimeout, isAvro, recordsManager, startStrategyEnum, lookBackDuration).join();
        } catch (CompletionException e) {
            // Разворачиваем CompletionException, чтобы выбросить оригинальное исключение
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw e;
        }
    }

    /**
     * Останавливает прослушивание указанного топика Kafka.
     * Метод удаляет слушателя из менеджера и инициирует его корректное завершение работы.
     *
     * @param topic Имя топика, прослушивание которого нужно прекратить. Не может быть null или пустым.
     * @return true, если слушатель был найден и успешно инициирована его остановка; false в противном случае.
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
     * Инициирует корректное завершение работы всего {@code KafkaListenerManager} и всех управляемых им слушателей.
     * Этот метод должен быть вызван при завершении работы приложения для освобождения всех ресурсов.
     * Гарантирует однократное выполнение.
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
     * Завершает работу внутреннего {@link ExecutorService}.
     * Пытается корректно остановить все задачи, ожидая их завершения.
     * Если задачи не завершаются в течение таймаута, принудительно останавливает их.
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
     * Реализация метода {@code AutoCloseable.close()}, вызывающая {@code shutdown()}.
     * Позволяет использовать {@code KafkaListenerManager} в try-with-resources блоках.
     */
    @Override
    public void close() {
        shutdown();
    }

    /**
     * Проверяет, не был ли менеджер уже остановлен.
     *
     * @throws IllegalStateException если менеджер уже находится в состоянии завершения работы.
     */
    private void ensureNotShutdown() {
        if (shutdownInitiated.get()) {
            throw new IllegalStateException("Менеджер уже в состоянии shutdown. Невозможно запустить новые слушатели.");
        }
    }

    /**
     * Проверяет, прослушивает ли менеджер указанный топик и активен ли соответствующий слушатель.
     *
     * @param topic Имя топика для проверки.
     * @return true, если слушатель для топика активен; false в противном случае.
     */
    public boolean isListening(String topic) {
        KafkaTopicListener listener = listeners.get(topic);
        return listener != null && listener.isRunning();
    }

    /**
     * Возвращает количество активных в данный момент слушателей.
     *
     * @return Количество активных слушателей.
     */
    public int getActiveCount() {
        return (int) listeners.values().stream().filter(KafkaTopicListener::isRunning).count();
    }

    /**
     * Фабричный метод для создания экземпляров {@link ConsumerStartStrategy}
     * на основе типа стратегии и опциональной продолжительности "оглядки" назад.
     *
     * @param strategyType     Тип стратегии запуска.
     * @param lookBackDuration Продолжительность, на которую нужно "оглянуться" назад (для FROM_TIMESTAMP). Может быть null для других стратегий.
     * @return Реализация {@link ConsumerStartStrategy}.
     * @throws IllegalArgumentException если {@code strategyType} требует {@code lookBackDuration}, а он null,
     *                                  или если {@code strategyType} неизвестен.
     */
    private ConsumerStartStrategy createConsumerStartStrategy(KafkaStartStrategyType strategyType, Duration lookBackDuration) {
        requireNonNull(strategyType, "Тип стратегии запуска не может быть null.");
        return switch (strategyType) {
            case LATEST -> new LatestStartStrategy();
            case EARLIEST -> new EarliestStartStrategy();
            case FROM_TIMESTAMP -> {
                if (lookBackDuration == null) {
                    throw new IllegalArgumentException("lookBackDuration не может быть null, если startStrategy - FROM_TIMESTAMP.");
                }
                yield new FromTimestampStartStrategy(lookBackDuration);
            }
        };
    }

    /**
     * Перечисление, определяющее доступные типы стратегий запуска Kafka Consumer.
     * Используется для выбора конкретной реализации {@link ConsumerStartStrategy}.
     */
    public enum KafkaStartStrategyType {
        /**
         * Начинать читать только новые записи (аналог seekToEnd).
         */
        LATEST,
        /**
         * Начинать читать с самого начала топика (аналог seekToBeginning).
         */
        EARLIEST,
        /**
         * Начинать читать с определенной временной метки (или ближайшего смещения).
         */
        FROM_TIMESTAMP
    }
}
