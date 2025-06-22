package com.svedentsov.kafka.helper;

import com.svedentsov.kafka.config.KafkaListenerConfig;
import com.svedentsov.kafka.exception.KafkaListenerException;
import com.svedentsov.kafka.exception.KafkaListenerException.ProcessingException;
import com.svedentsov.kafka.pool.KafkaClientPool;
import com.svedentsov.kafka.processor.RecordProcessor;
import com.svedentsov.kafka.processor.RecordProcessorAvro;
import com.svedentsov.kafka.processor.RecordProcessorString;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Представляет отдельный listener (слушатель) для конкретного Kafka-топика.
 * Инкапсулирует весь жизненный цикл {@link KafkaConsumer}:
 * инициализацию, подписку на топик, обработку записей, обработку ошибок,
 * retry-логику и корректное завершение работы (graceful shutdown).
 * Каждый экземпляр этого класса предназначен для прослушивания ОДНОГО топика.
 */
@Slf4j
public class KafkaTopicListener {

    private final String topicName;
    private final Duration pollTimeout;
    private final boolean isAvro;
    private final KafkaListenerConfig config;
    private final RecordProcessor<?> recordProcessor;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicBoolean isShutdownRequested = new AtomicBoolean(false);

    private volatile KafkaConsumer<String, ?> consumer;
    private volatile CompletableFuture<Void> listeningTask;

    /**
     * Конструктор для создания экземпляра KafkaTopicListener.
     *
     * @param topicName   название топика, который будет прослушиваться; не может быть {@code null} или пустым.
     * @param pollTimeout {@link Duration} - максимальное время ожидания записей от Kafka брокера в одном вызове {@code consumer.poll()}.
     * @param isAvro      {@code true}, если ожидаются Avro-сообщения, {@code false} для строковых сообщений.
     * @param config      конфигурация listener'а ({@link KafkaListenerConfig}); не может быть {@code null}.
     * @throws IllegalArgumentException если {@code topicName} или {@code config} некорректны.
     */
    public KafkaTopicListener(String topicName, Duration pollTimeout, boolean isAvro, KafkaListenerConfig config) {
        if (topicName == null || topicName.isBlank()) {
            throw new IllegalArgumentException("Название топика не может быть null или пустым.");
        }
        Objects.requireNonNull(config, "KafkaListenerConfig не может быть null.");
        Objects.requireNonNull(pollTimeout, "Poll timeout не может быть null.");

        if (pollTimeout.isNegative() || pollTimeout.isZero()) {
            log.warn("pollTimeout отрицательный или нулевой ({}). Установлен минимальный таймаут 1 мс для корректной работы.", pollTimeout);
            this.pollTimeout = Duration.ofMillis(1);
        } else {
            this.pollTimeout = pollTimeout;
        }
        this.topicName = topicName;
        this.isAvro = isAvro;
        this.config = config;
        this.recordProcessor = createRecordProcessor();
    }

    /**
     * Запускает прослушивание топика в отдельном потоке.
     *
     * @throws IllegalStateException  если listener уже запущен.
     * @throws KafkaListenerException если произошла ошибка при запуске задачи.
     */
    public void start() {
        if (!isRunning.compareAndSet(false, true)) {
            throw new IllegalStateException("Listener уже запущен для топика: " + topicName);
        }
        isShutdownRequested.set(false);
        listeningTask = CompletableFuture.runAsync(this::listen, config.getExecutorService())
                .exceptionally(throwable -> {
                    log.error("Критическая ошибка при выполнении задачи listener для топика {}", topicName, throwable);
                    isRunning.set(false);
                    isShutdownRequested.set(true);
                    throw new KafkaListenerException.LifecycleException("Ошибка в основном цикле прослушивания для топика " + topicName, throwable);
                });
        log.info("Listener для топика '{}' инициирован к запуску.", topicName);
    }

    /**
     * Инициирует корректное завершение работы (graceful shutdown) listener-а.
     * Если shutdown уже запрошен, повторный вызов игнорируется.
     */
    public void shutdown() {
        if (!isShutdownRequested.compareAndSet(false, true)) {
            log.debug("Shutdown уже запрошен для топика '{}', игнорируем повторный вызов.", topicName);
            return;
        }
        log.info("Инициация остановки listener для топика '{}'.", topicName);
        if (consumer != null) {
            consumer.wakeup();
        }
        if (listeningTask != null) {
            try {
                listeningTask.get(config.getShutdownTimeout().toMillis(), TimeUnit.MILLISECONDS);
                log.info("Listener для топика '{}' успешно остановлен.", topicName);
            } catch (Exception e) {
                log.warn("Таймаут ({} мс) при остановке listener для топика '{}'. Принудительное завершение задачи.",
                        config.getShutdownTimeout().toMillis(), topicName, e);
                listeningTask.cancel(true);
            }
        }
        isRunning.set(false);
    }

    /**
     * Проверяет, запущен ли listener и не запрошен ли shutdown.
     *
     * @return {@code true}, если listener активно работает; {@code false} в противном случае.
     */
    public boolean isRunning() {
        return isRunning.get() && !isShutdownRequested.get();
    }

    /**
     * Основной цикл работы listener-а.
     * Инициализирует consumer, подписывается, затем в цикле poll/process до запроса shutdown.
     */
    @SuppressWarnings("unchecked")
    private void listen() {
        try {
            initializeConsumer();
            subscribeAndSeekToEnd();
            processMessagesLoop();
        } catch (WakeupException e) {
            log.info("Consumer для топика '{}' получил wakeup-сигнал. Завершение цикла прослушивания.", topicName);
        } catch (Exception e) {
            log.error("Неожиданная ошибка в listener для топика {}. Завершение работы.", topicName, e);
            if (config.shouldStopOnError()) {
                throw new ProcessingException("Критическая ошибка в цикле обработки сообщений для топика " + topicName, e);
            }
        } finally {
            cleanupResources();
        }
    }

    /**
     * Инициализирует {@link KafkaConsumer}.
     */
    private void initializeConsumer() {
        consumer = isAvro
                ? KafkaClientPool.getAvroConsumer(topicName)
                : KafkaClientPool.getStringConsumer(topicName);
        log.debug("Consumer инициализирован для топика '{}' (Avro: {})", topicName, isAvro);
    }

    /**
     * Подписывается на топик и переводит смещение на конец, чтобы читать только новые сообщения.
     */
    private void subscribeAndSeekToEnd() {
        consumer.subscribe(Collections.singletonList(topicName));
        consumer.poll(Duration.ofMillis(100));
        Set<TopicPartition> partitions = consumer.assignment();
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
        while (isRunning.get() && !isShutdownRequested.get() && !Thread.currentThread().isInterrupted()) {
            try {
                ConsumerRecords<String, ?> records = consumer.poll(pollTimeout);
                if (!records.isEmpty()) {
                    ((RecordProcessor) recordProcessor).processRecords(records);
                    log.debug("Обработано {} записей из топика '{}'", records.count(), topicName);
                }
            } catch (WakeupException e) {
                log.info("Получен wakeup-сигнал для топика '{}'. Завершаем цикл обработки.", topicName);
                break;
            } catch (Exception e) {
                log.error("Ошибка при обработке сообщений из топика '{}': {}", topicName, e.getMessage(), e);
                if (config.shouldStopOnError()) {
                    log.error("Остановка listener '{}' из-за критической ошибки.", topicName);
                    throw new ProcessingException("Критическая ошибка обработки сообщений для топика " + topicName, e);
                } else {
                    log.warn("Повторная попытка через {} мс после ошибки в listener для топика '{}'.", config.getErrorRetryDelay().toMillis(), topicName);
                    sleepSafely(config.getErrorRetryDelay());
                }
            }
        }
    }

    /**
     * Закрывает KafkaConsumer с указанным таймаутом.
     */
    private void cleanupResources() {
        if (consumer != null) {
            try {
                consumer.close(config.getConsumerCloseTimeout());
                log.info("Consumer для топика '{}' успешно закрыт.", topicName);
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
}
