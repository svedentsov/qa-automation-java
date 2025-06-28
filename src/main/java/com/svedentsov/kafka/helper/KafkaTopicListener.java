package com.svedentsov.kafka.helper;

import com.svedentsov.kafka.config.KafkaListenerConfig;
import com.svedentsov.kafka.exception.KafkaListenerException;
import com.svedentsov.kafka.exception.KafkaListenerException.ProcessingException;
import com.svedentsov.kafka.factory.ConsumerFactory;
import com.svedentsov.kafka.factory.ConsumerFactoryDefault;
import com.svedentsov.kafka.processor.RecordProcessor;
import com.svedentsov.kafka.processor.RecordProcessorAvro;
import com.svedentsov.kafka.processor.RecordProcessorString;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Objects.requireNonNull;

/**
 * Представляет отдельный listener (слушатель) для конкретного Kafka-топика.
 * Инкапсулирует весь жизненный цикл {@link KafkaConsumer}:
 * инициализацию, подписку на топик, обработку записей, обработку ошибок,
 * retry-логику и корректное завершение работы (graceful shutdown).
 * Каждый экземпляр этого класса предназначен для прослушивания ОДНОГО топика.
 * Класс является потокобезопасным и поддерживает корректное завершение работы.
 */
@Slf4j
public class KafkaTopicListener implements AutoCloseable {

    private static final Duration MIN_POLL_TIMEOUT = Duration.ofMillis(1);
    private static final Duration INITIAL_POLL_TIMEOUT = Duration.ofMillis(100);
    private final String topicName;
    private final Duration pollTimeout;
    private final boolean isAvro;
    private final KafkaListenerConfig config;
    private final RecordProcessor<?> recordProcessor;
    private final ConsumerFactory consumerFactory = new ConsumerFactoryDefault();
    // Состояние lifecycle
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicBoolean isShutdownRequested = new AtomicBoolean(false);
    private final ReentrantLock stateLock = new ReentrantLock();
    // Ресурсы
    private volatile KafkaConsumer<String, ?> consumer;
    private volatile CompletableFuture<Void> listeningTask;

    /**
     * Конструктор для создания экземпляра KafkaTopicListener.
     *
     * @param topicName   название топика, который будет прослушиваться; не может быть {@code null} или пустым
     * @param pollTimeout максимальное время ожидания записей от Kafka брокера в одном вызове poll()
     * @param isAvro      {@code true}, если ожидаются Avro-сообщения, {@code false} для строковых сообщений
     * @param config      конфигурация listener'а; не может быть {@code null}
     * @throws IllegalArgumentException если параметры некорректны
     */
    public KafkaTopicListener(String topicName, Duration pollTimeout, boolean isAvro, KafkaListenerConfig config) {
        this.topicName = validateTopicName(topicName);
        this.config = requireNonNull(config, "KafkaListenerConfig не может быть null.");
        this.pollTimeout = validateAndNormalizePollTimeout(pollTimeout);
        this.isAvro = isAvro;
        this.recordProcessor = createRecordProcessor();
    }

    /**
     * Запускает прослушивание топика в отдельном потоке.
     *
     * @throws IllegalStateException  если listener уже запущен
     * @throws KafkaListenerException если произошла ошибка при запуске задачи
     */
    public void start() {
        stateLock.lock();
        try {
            if (!isRunning.compareAndSet(false, true)) {
                throw new IllegalStateException("Listener уже запущен для топика: " + topicName);
            }
            isShutdownRequested.set(false);
            startListeningTask();
            log.info("Listener для топика '{}' успешно запущен.", topicName);
        } finally {
            stateLock.unlock();
        }
    }

    /**
     * Инициирует корректное завершение работы (graceful shutdown) listener-а.
     * Метод является идемпотентным - повторные вызовы игнорируются.
     */
    public void shutdown() {
        if (!isShutdownRequested.compareAndSet(false, true)) {
            log.debug("Shutdown уже запрошен для топика '{}', игнорируем повторный вызов.", topicName);
            return;
        }
        log.info("Инициация остановки listener для топика '{}'.", topicName);
        stateLock.lock();
        try {
            interruptConsumer();
            awaitTaskCompletion();
            isRunning.set(false);
            log.info("Listener для топика '{}' успешно остановлен.", topicName);
        } finally {
            stateLock.unlock();
        }
    }

    /**
     * Проверяет, запущен ли listener и не запрошен ли shutdown.
     *
     * @return {@code true}, если listener активно работает; {@code false} в противном случае
     */
    public boolean isRunning() {
        return isRunning.get() && !isShutdownRequested.get();
    }

    /**
     * Возвращает название топика, который прослушивает данный listener.
     *
     * @return название топика
     */
    public String getTopicName() {
        return topicName;
    }

    /**
     * Проверяет, был ли запрошен shutdown.
     *
     * @return {@code true}, если shutdown был запрошен
     */
    public boolean isShutdownRequested() {
        return isShutdownRequested.get();
    }

    @Override
    public void close() {
        shutdown();
    }

    /**
     * Основной цикл работы listener-а.
     * Инициализирует consumer, подписывается, затем в цикле poll/process до запроса shutdown.
     */
    @SuppressWarnings("unchecked")
    private void listen() {
        try {
            log.debug("Начало цикла прослушивания для топика '{}'", topicName);
            initializeConsumer();
            subscribeAndSeekToEnd();
            processMessagesLoop();
        } catch (WakeupException e) {
            log.info("Consumer для топика '{}' получил wakeup-сигнал. Завершение цикла прослушивания.", topicName);
        } catch (Exception e) {
            log.error("Неожиданная ошибка в listener для топика {}. Завершение работы.", topicName, e);
            handleCriticalError(e);
        } finally {
            cleanupResources();
        }
    }

    /**
     * Инициализирует {@link KafkaConsumer}.
     */
    private void initializeConsumer() {
        consumer = isAvro
                ? consumerFactory.createAvroConsumer(topicName)
                : consumerFactory.createStringConsumer(topicName);
        log.debug("Consumer инициализирован для топика '{}' (Avro: {})", topicName, isAvro);
    }

    /**
     * Подписывается на топик и переводит смещение на конец, чтобы читать только новые сообщения.
     */
    private void subscribeAndSeekToEnd() {
        consumer.subscribe(List.of(topicName));
        // Первичный poll для получения назначенных партиций
        consumer.poll(INITIAL_POLL_TIMEOUT);
        var partitions = consumer.assignment();
        if (!partitions.isEmpty()) {
            consumer.seekToEnd(partitions);
            log.info("Consumer подписан на {} партиций топика '{}' и установлен на конец.", partitions.size(), topicName);
        } else {
            log.warn("Не получены партиции для топика '{}' после подписки.", topicName);
        }
    }

    /**
     * Основной цикл обработки сообщений.
     * Продолжается до тех пор, пока не запрошен shutdown.
     */
    @SuppressWarnings("unchecked")
    private void processMessagesLoop() {
        while (shouldContinueProcessing()) {
            try {
                var records = consumer.poll(pollTimeout);
                if (!records.isEmpty()) {
                    ((RecordProcessor) recordProcessor).processRecords(records);
                    log.debug("Обработано {} записей из топика '{}'", records.count(), topicName);
                }
            } catch (WakeupException e) {
                log.info("Получен wakeup-сигнал для топика '{}'. Завершаем цикл обработки.", topicName);
                break;
            } catch (Exception e) {
                handleProcessingError(e);
            }
        }
    }

    /**
     * Проверяет, следует ли продолжать обработку сообщений.
     */
    private boolean shouldContinueProcessing() {
        return isRunning.get()
                && !isShutdownRequested.get()
                && !Thread.currentThread().isInterrupted();
    }

    /**
     * Обрабатывает ошибки при обработке сообщений.
     */
    private void handleProcessingError(Exception e) {
        log.error("Ошибка при обработке сообщений из топика '{}': {}", topicName, e.getMessage(), e);
        if (config.shouldStopOnError()) {
            log.error("Остановка listener '{}' из-за критической ошибки.", topicName);
            throw new ProcessingException("Критическая ошибка обработки сообщений для топика " + topicName, e);
        } else {
            log.warn("Повторная попытка через {} мс после ошибки в listener для топика '{}'.", config.getErrorRetryDelay().toMillis(), topicName);
            sleepSafely(config.getErrorRetryDelay());
        }
    }

    /**
     * Обрабатывает критические ошибки в основном цикле.
     */
    private void handleCriticalError(Exception e) {
        if (config.shouldStopOnError()) {
            throw new KafkaListenerException.LifecycleException(
                    "Критическая ошибка в основном цикле прослушивания для топика " + topicName, e);
        }
    }

    /**
     * Закрывает KafkaConsumer с указанным таймаутом.
     */
    private void cleanupResources() {
        if (consumer != null) {
            try {
                consumer.close(config.getConsumerCloseTimeout());
                log.debug("Consumer для топика '{}' успешно закрыт.", topicName);
            } catch (Exception e) {
                log.warn("Ошибка при закрытии consumer для топика '{}'.", topicName, e);
            } finally {
                consumer = null;
            }
        }
        isRunning.set(false);
    }

    /**
     * Создаёт экземпляр {@link RecordProcessor} (Avro или String).
     */
    private RecordProcessor<?> createRecordProcessor() {
        return isAvro
                ? new RecordProcessorAvro(topicName, config)
                : new RecordProcessorString(topicName, config);
    }

    /**
     * Безопасная задержка (sleep) на указанный промежуток времени.
     */
    private void sleepSafely(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Поток listener для топика '{}' был прерван во время задержки после ошибки.", topicName);
            isShutdownRequested.set(true);
        }
    }

    /**
     * Запускает задачу прослушивания.
     */
    private void startListeningTask() {
        listeningTask = CompletableFuture.runAsync(this::listen, config.getExecutorService())
                .exceptionally(throwable -> {
                    log.error("Критическая ошибка при выполнении задачи listener для топика {}", topicName, throwable);
                    isRunning.set(false);
                    isShutdownRequested.set(true);
                    return null; // Не выбрасываем исключение из exceptionally
                });
    }

    /**
     * Прерывает работу consumer'а.
     */
    private void interruptConsumer() {
        if (consumer != null) {
            try {
                consumer.wakeup();
            } catch (Exception e) {
                log.warn("Ошибка при вызове wakeup для consumer топика '{}'", topicName, e);
            }
        }
    }

    /**
     * Ожидает завершения задачи прослушивания.
     */
    private void awaitTaskCompletion() {
        if (listeningTask != null) {
            try {
                listeningTask.get(config.getShutdownTimeout().toMillis(), TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.warn("Таймаут ({} мс) при остановке listener для топика '{}'. Принудительное завершение задачи.", config.getShutdownTimeout().toMillis(), topicName, e);
                listeningTask.cancel(true);
            }
        }
    }

    private static String validateTopicName(String topicName) {
        if (topicName == null || topicName.isBlank()) {
            throw new IllegalArgumentException("Название топика не может быть null или пустым.");
        }
        return topicName.trim();
    }

    private static Duration validateAndNormalizePollTimeout(Duration pollTimeout) {
        requireNonNull(pollTimeout, "Poll timeout не может быть null.");
        if (pollTimeout.isNegative() || pollTimeout.isZero()) {
            log.warn("pollTimeout отрицательный или нулевой ({}). Установлен минимальный таймаут {} для корректной работы.", pollTimeout, MIN_POLL_TIMEOUT);
            return MIN_POLL_TIMEOUT;
        }
        return pollTimeout;
    }
}
