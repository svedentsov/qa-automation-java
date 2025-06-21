package com.svedentsov.kafka.helper;

import com.svedentsov.kafka.config.KafkaListenerConfig;
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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Представляет отдельный listener для конкретного Kafka-топика.
 * Инкапсулирует:
 * - жизненный цикл Consumer
 * - подписку и seek
 * - обработку записей через RecordProcessor
 * - обработку ошибок и retry
 * - graceful shutdown
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
     * Конструктор.
     *
     * @param topicName   название топика, не null
     * @param pollTimeout таймаут для consumer.poll
     * @param isAvro      true — Avro-режим, false — строковый
     * @param config      конфигурация listener, не null
     * @throws IllegalArgumentException если topicName или config == null/пустой
     */
    public KafkaTopicListener(String topicName, Duration pollTimeout, boolean isAvro, KafkaListenerConfig config) {
        if (topicName == null || topicName.isBlank()) {
            throw new IllegalArgumentException("Название топика не может быть null или пустым");
        }
        Objects.requireNonNull(config, "KafkaListenerConfig не может быть null");
        this.topicName = topicName;
        this.pollTimeout = pollTimeout;
        this.isAvro = isAvro;
        this.config = config;
        this.recordProcessor = createRecordProcessor();
    }

    /**
     * Запускает прослушивание топика в отдельном потоке.
     *
     * @throws IllegalStateException если уже запущен
     */
    public void start() {
        if (!isRunning.compareAndSet(false, true)) {
            throw new IllegalStateException("Listener уже запущен для топика: " + topicName);
        }
        listeningTask = CompletableFuture.runAsync(this::listen, config.getExecutorService())
                .exceptionally(throwable -> {
                    log.error("Критическая ошибка в listener для топика {}", topicName, throwable);
                    isRunning.set(false);
                    return null;
                });
        log.info("Listener для топика '{}' запущен", topicName);
    }

    /**
     * Инициирует graceful shutdown listener'а.
     * Если уже был запрошен shutdown, повторный вызов игнорируется.
     */
    public void shutdown() {
        if (!isShutdownRequested.compareAndSet(false, true)) {
            log.debug("Shutdown уже запрошен для топика {}", topicName);
            return;
        }
        log.info("Инициация остановки listener для топика {}", topicName);
        if (consumer != null) {
            consumer.wakeup();
        }
        if (listeningTask != null) {
            try {
                listeningTask.get(config.getShutdownTimeout().toMillis(),
                        java.util.concurrent.TimeUnit.MILLISECONDS);
                log.info("Listener для топика '{}' успешно остановлен", topicName);
            } catch (Exception e) {
                log.warn("Таймаут при остановке listener для топика {}", topicName, e);
                listeningTask.cancel(true);
            }
        }
        isRunning.set(false);
    }

    /**
     * Проверяет, запущен ли listener (не завершён и не в процессе shutdown).
     *
     * @return true, если работает
     */
    public boolean isRunning() {
        return isRunning.get() && !isShutdownRequested.get();
    }

    // Основной цикл: инициализация, подписка, обработка сообщений, очистка
    private void listen() {
        try {
            initializeConsumer();
            subscribeAndSeekToEnd();
            processMessages();
        } catch (WakeupException e) {
            log.info("Consumer для топика '{}' получил wakeup-сигнал", topicName);
        } catch (Exception e) {
            log.error("Неожиданная ошибка в listener для топика {}", topicName, e);
        } finally {
            cleanupResources();
        }
    }

    /**
     * Инициализирует KafkaConsumer из пула.
     */
    private void initializeConsumer() {
        consumer = isAvro
                ? KafkaClientPool.getAvroConsumer(topicName)
                : KafkaClientPool.getStringConsumer(topicName);
        log.debug("Consumer инициализирован для топика '{}' (Avro: {})", topicName, isAvro);
    }

    /**
     * Подписывается на топик и делает seekToEnd, чтобы читать только новые сообщения.
     */
    private void subscribeAndSeekToEnd() {
        consumer.subscribe(Collections.singletonList(topicName));
        consumer.poll(Duration.ZERO); // чтобы получить assignment
        Set<TopicPartition> partitions = consumer.assignment();
        if (!partitions.isEmpty()) {
            consumer.seekToEnd(partitions);
            log.debug("Consumer подписан на {} партиций топика '{}'", partitions.size(), topicName);
        } else {
            log.warn("Не получены партиции для топика '{}'", topicName);
        }
    }

    /**
     * Основной цикл обработки сообщений.
     * При ошибке: если config.shouldStopOnError() == true, выходим; иначе ждём errorRetryDelay и продолжаем.
     */
    @SuppressWarnings("unchecked")
    private void processMessages() {
        while (isRunning.get() && !isShutdownRequested.get() && !Thread.currentThread().isInterrupted()) {
            try {
                ConsumerRecords<String, ?> records = consumer.poll(pollTimeout);
                if (!records.isEmpty()) {
                    ((RecordProcessor) recordProcessor).processRecords(records);
                    log.debug("Обработано {} записей из топика '{}'", records.count(), topicName);
                }
            } catch (WakeupException e) {
                log.info("Получен wakeup для топика '{}', завершаем обработку", topicName);
                break;
            } catch (Exception e) {
                log.error("Ошибка при обработке сообщений из топика '{}'", topicName, e);
                if (config.shouldStopOnError()) {
                    log.error("Останавливаем listener для '{}' из-за критической ошибки", topicName);
                    break;
                }
                sleepSafely(config.getErrorRetryDelay());
            }
        }
    }

    /**
     * Закрывает consumer с указанным таймаутом.
     */
    private void cleanupResources() {
        if (consumer != null) {
            try {
                consumer.close(config.getConsumerCloseTimeout());
                log.debug("Consumer для топика '{}' закрыт", topicName);
            } catch (Exception e) {
                log.warn("Ошибка при закрытии consumer для топика '{}'", topicName, e);
            }
        }
        isRunning.set(false);
    }

    /**
     * Создаёт RecordProcessor в зависимости от режима Avro/строка.
     */
    private RecordProcessor<?> createRecordProcessor() {
        return isAvro
                ? new RecordProcessorAvro(topicName, config)
                : new RecordProcessorString(topicName, config);
    }

    /**
     * Безопасный sleep между попытками после ошибки.
     *
     * @param duration задержка
     */
    private void sleepSafely(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.debug("Прерван sleep после ошибки для топика {}", topicName);
        }
    }
}
