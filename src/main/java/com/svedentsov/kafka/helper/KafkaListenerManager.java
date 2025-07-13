package com.svedentsov.kafka.helper;

import com.svedentsov.kafka.config.KafkaConfigListener;
import com.svedentsov.kafka.enums.StartStrategyType;
import com.svedentsov.kafka.exception.KafkaListenerException.LifecycleException;
import com.svedentsov.kafka.factory.ConsumerFactory;
import com.svedentsov.kafka.helper.strategy.*;
import com.svedentsov.kafka.processor.RecordProcessor;
import com.svedentsov.kafka.processor.RecordProcessorAvro;
import com.svedentsov.kafka.processor.RecordProcessorString;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.svedentsov.kafka.utils.ValidationUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * Управляет жизненным циклом нескольких экземпляров {@link KafkaTopicListener}.
 * <p>Этот класс является центральной точкой для запуска, остановки и мониторинга
 * слушателей для различных топиков Kafka. Он управляет собственным пулом потоков
 * {@link ExecutorService} для асинхронной работы слушателей.
 * <p>Реализует {@link AutoCloseable} для гарантированного освобождения всех ресурсов,
 * включая остановку всех активных слушателей и пула потоков. Также устанавливает
 * хук завершения работы (shutdown hook) для корректной остановки при завершении работы JVM.
 */
@Slf4j
public class KafkaListenerManager implements AutoCloseable {

    private final ConcurrentMap<String, KafkaTopicListener<?>> listeners = new ConcurrentHashMap<>();
    private final AtomicBoolean shutdownInitiated = new AtomicBoolean(false);
    private final KafkaConfigListener config;
    private final ConsumerFactory consumerFactory;
    private final KafkaRecordsManager recordsManager;
    private final ExecutorService executorService;

    /**
     * Создает новый экземпляр менеджера слушателей.
     *
     * @param config          Общая конфигурация для всех слушателей.
     * @param consumerFactory Фабрика для создания консьюмеров.
     * @param recordsManager  Менеджер для хранения полученных записей.
     */
    public KafkaListenerManager(KafkaConfigListener config, ConsumerFactory consumerFactory, KafkaRecordsManager recordsManager) {
        this.config = requireNonNull(config, "KafkaListenerConfig не может быть null");
        this.consumerFactory = requireNonNull(consumerFactory, "ConsumerFactory не может быть null");
        this.recordsManager = requireNonNull(recordsManager, "KafkaRecordsManager не может быть null");
        this.executorService = Executors.newCachedThreadPool();
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "kafka-listener-shutdown-hook"));
    }

    /**
     * Асинхронно запускает прослушивание топика.
     * Если слушатель для данного топика уже существует, этот метод ничего не делает.
     *
     * @param topic           Имя топика. Не может быть пустым или null.
     * @param pollTimeout     Максимальное время блокировки в методе poll().
     * @param isAvro          {@code true}, если топик содержит сообщения в формате Avro, иначе {@code false}.
     * @param strategyOptions Объект, содержащий тип стратегии и её параметры.
     * @return {@link CompletableFuture}, который завершается, когда слушатель успешно создан и запущен.
     */
    public CompletableFuture<Void> startListeningAsync(String topic, Duration pollTimeout, boolean isAvro, StartStrategyOptions strategyOptions) {
        requireNonBlank(topic, "Имя топика не может быть null или пустым.");
        requireNonNull(strategyOptions, "StartStrategyOptions не может быть null.");
        ensureNotShutdown();
        strategyOptions.validate();

        return CompletableFuture.runAsync(() -> {
            listeners.computeIfAbsent(topic, t -> {
                log.info("Создание и запуск нового слушателя для топика '{}' со стратегией: {}. Avro: {}", t, strategyOptions.getStrategyType(), isAvro);
                ConsumerStartStrategy strategy = createConsumerStartStrategy(strategyOptions);

                KafkaTopicListener<?> newListener;

                if (isAvro) {
                    RecordProcessor<Object> recordProcessor = new RecordProcessorAvro(t, config, recordsManager);
                    newListener = new KafkaTopicListener<>(t, pollTimeout, config, consumerFactory, recordProcessor, strategy, true);
                } else {
                    RecordProcessor<String> recordProcessor = new RecordProcessorString(t, config, recordsManager);
                    newListener = new KafkaTopicListener<>(t, pollTimeout, config, consumerFactory, recordProcessor, strategy, false);
                }

                newListener.start(executorService);
                return newListener;
            });
        }, executorService).exceptionally(ex -> {
            log.error("Не удалось запустить слушатель для топика '{}'", topic, ex);
            listeners.remove(topic); // Удаляем слушателя из карты в случае ошибки запуска
            throw new LifecycleException("Ошибка при асинхронном запуске слушателя для " + topic, ex);
        });
    }

    /**
     * Синхронно запускает прослушивание топика.
     * Блокирует текущий поток до тех пор, пока слушатель не будет запущен.
     *
     * @param topic           Имя топика.
     * @param pollTimeout     Максимальное время блокировки в методе poll().
     * @param isAvro          {@code true}, если топик Avro.
     * @param strategyOptions Объект, содержащий тип стратегии и её параметры.
     * @throws CompletionException если при запуске произошла ошибка.
     */
    public void startListening(String topic, Duration pollTimeout, boolean isAvro, StartStrategyOptions strategyOptions) {
        try {
            startListeningAsync(topic, pollTimeout, isAvro, strategyOptions).join();
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
     *
     * @param topic Имя топика.
     * @return {@code true}, если слушатель был найден и остановлен, иначе {@code false}.
     */
    public boolean stopListening(String topic) {
        requireNonBlank(topic, "Имя топика не может быть null или пустым.");
        KafkaTopicListener<?> listener = listeners.remove(topic);
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
            listeners.put(topic, listener);
            return false;
        }
    }

    /**
     * Останавливает все активные слушатели и освобождает все ресурсы, включая пул потоков.
     * Метод является идемпотентным и потокобезопасным.
     */
    public void shutdown() {
        if (shutdownInitiated.compareAndSet(false, true)) {
            log.info("Начало процесса завершения работы KafkaListenerManager. Активных слушателей: {}", listeners.size());

            // Создаем копию ключей, чтобы избежать ConcurrentModificationException
            for (String topic : Set.copyOf(listeners.keySet())) {
                stopListening(topic);
            }

            shutdownExecutorService();
            log.info("KafkaListenerManager и все его ресурсы были успешно освобождены.");
        }
    }

    /**
     * Корректно завершает работу пула потоков.
     */
    private void shutdownExecutorService() {
        executorService.shutdown(); // Инициируем корректное завершение
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

    /**
     * Реализация метода {@link AutoCloseable}, вызывает {@link #shutdown()}.
     */
    @Override
    public void close() {
        shutdown();
    }

    /**
     * Проверяет, не был ли менеджер уже остановлен.
     *
     * @throws IllegalStateException если менеджер находится в состоянии shutdown.
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
     * @return {@code true}, если слушатель запущен и работает.
     */
    public boolean isListening(String topic) {
        KafkaTopicListener<?> listener = listeners.get(topic);
        return listener != null && listener.isRunning();
    }

    /**
     * Возвращает количество активных слушателей.
     *
     * @return Количество работающих слушателей.
     */
    public int getActiveCount() {
        return (int) listeners.values().stream().filter(KafkaTopicListener::isRunning).count();
    }

    /**
     * Создает экземпляр стратегии начального смещения на основе переданного типа и параметров.
     *
     * @param strategyOptions Объект, содержащий тип стратегии и её параметры.
     * @return Экземпляр {@link ConsumerStartStrategy}.
     * @throws IllegalArgumentException если для выбранной стратегии отсутствуют или некорректны параметры.
     */
    private ConsumerStartStrategy createConsumerStartStrategy(StartStrategyOptions strategyOptions) {
        StartStrategyType strategyType = strategyOptions.getStrategyType();
        requireNonNull(strategyType, "Тип стратегии запуска не может быть null.");
        return switch (strategyType) {
            case DEFAULT -> new DefaultStartStrategy();
            case LATEST -> new LatestStartStrategy();
            case EARLIEST -> new EarliestStartStrategy();
            case FROM_TIMESTAMP -> {
                Duration lookBackDuration = strategyOptions.getLookBackDuration();
                if (lookBackDuration == null) {
                    throw new IllegalArgumentException("lookBackDuration не может быть null для стратегии FROM_TIMESTAMP.");
                }
                yield new FromTimestampStartStrategy(lookBackDuration);
            }
            case FROM_SPECIFIC_OFFSET -> {
                Map<Integer, Long> partitionOffsets = strategyOptions.getPartitionOffsets();
                if (partitionOffsets == null || partitionOffsets.isEmpty()) {
                    throw new IllegalArgumentException("partitionOffsets не может быть null или пустым для стратегии FROM_SPECIFIC_OFFSET.");
                }
                yield new FromSpecificOffsetStartStrategy(partitionOffsets);
            }
            case RELATIVE_FROM_END -> {
                Long offsetFromEnd = strategyOptions.getOffsetFromEnd();
                if (offsetFromEnd == null) {
                    throw new IllegalArgumentException("offsetFromEnd не может быть null для стратегии RELATIVE_FROM_END.");
                }
                yield new RelativeFromEndStartStrategy(offsetFromEnd);
            }
        };
    }
}
