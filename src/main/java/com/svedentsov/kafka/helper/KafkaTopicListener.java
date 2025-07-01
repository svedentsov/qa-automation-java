package com.svedentsov.kafka.helper;

import com.svedentsov.kafka.config.KafkaListenerConfig;
import com.svedentsov.kafka.exception.KafkaListenerException;
import com.svedentsov.kafka.factory.ConsumerFactory;
import com.svedentsov.kafka.processor.RecordProcessor;
import com.svedentsov.kafka.processor.RecordProcessorAvro;
import com.svedentsov.kafka.processor.RecordProcessorString;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.svedentsov.kafka.utils.ValidationUtils.requireNonBlank;
import static com.svedentsov.kafka.utils.ValidationUtils.validatePollTimeout;
import static java.util.Objects.requireNonNull;

/**
 * Реализует логику прослушивания одного топика Kafka в отдельном потоке.
 * Этот класс отвечает за создание консьюмера, подписку на топик,
 * получение и обработку сообщений с помощью соответствующего {@link RecordProcessor}.
 * Он управляет своим жизненным циклом (запуск, остановка) и обеспечивает
 * корректное освобождение ресурсов.
 */
@Slf4j
public class KafkaTopicListener implements AutoCloseable {

    private static final Duration INITIAL_POLL_TIMEOUT = Duration.ofMillis(100);
    private final String topicName;
    private final Duration pollTimeout;
    private final boolean isAvro;
    private final KafkaListenerConfig config;
    private final ConsumerFactory consumerFactory;
    private final RecordProcessor<?> recordProcessor;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicBoolean shutdownRequested = new AtomicBoolean(false);
    private volatile KafkaConsumer<String, ?> consumer;
    private volatile CompletableFuture<Void> listeningTask;

    /**
     * Создает экземпляр слушателя топика.
     *
     * @param topicName       Имя топика для прослушивания.
     * @param pollTimeout     Тайм-аут для операции poll.
     * @param isAvro          {@code true}, если контент в формате Avro.
     * @param config          Общая конфигурация слушателей.
     * @param consumerFactory Фабрика для создания Kafka Consumer.
     * @param recordsManager  Менеджер для сохранения полученных записей.
     */
    public KafkaTopicListener(String topicName, Duration pollTimeout, boolean isAvro, KafkaListenerConfig config,
                              ConsumerFactory consumerFactory, KafkaRecordsManager recordsManager) {
        this.topicName = requireNonBlank(topicName, "Название топика не может быть null или пустым.");
        this.pollTimeout = validatePollTimeout(pollTimeout);
        this.isAvro = isAvro;
        this.config = requireNonNull(config, "KafkaListenerConfig не может быть null.");
        this.consumerFactory = requireNonNull(consumerFactory, "ConsumerFactory не может быть null.");
        requireNonNull(recordsManager, "KafkaRecordsManager не может быть null.");
        this.recordProcessor = createRecordProcessor(recordsManager);
    }

    /**
     * Запускает процесс прослушивания в отдельном потоке, управляемом предоставленным ExecutorService.
     *
     * @param executor Сервис для выполнения задачи прослушивания.
     * @throws IllegalStateException если слушатель уже запущен.
     */
    public void start(ExecutorService executor) {
        if (isRunning.compareAndSet(false, true)) {
            log.info("Запуск слушателя для топика '{}'...", topicName);
            listeningTask = CompletableFuture.runAsync(this::runListeningLoop, executor)
                    .exceptionally(this::handleCriticalError);
            log.info("Слушатель для топика '{}' успешно запущен.", topicName);
        } else {
            throw new IllegalStateException("Слушатель для топика " + topicName + " уже запущен.");
        }
    }

    private void runListeningLoop() {
        try (KafkaConsumer<String, ?> consumer = createAndSubscribeConsumer()) {
            this.consumer = consumer; // Делаем консьюмер доступным для метода shutdown()
            log.info("Начало цикла прослушивания для топика '{}'", topicName);
            while (shouldContinueProcessing()) {
                processMessages();
            }
        } catch (WakeupException e) {
            log.info("Цикл прослушивания для топика '{}' прерван вызовом wakeup().", topicName);
        } catch (Exception e) {
            // Логируем неожиданные ошибки, которые могли произойти вне цикла
            log.error("Критическая ошибка в слушателе для топика '{}'. Работа будет остановлена.", topicName, e);
            throw new KafkaListenerException.LifecycleException("Критическая ошибка в жизненном цикле слушателя " + topicName, e);
        } finally {
            cleanupAfterLoop();
        }
    }

    /**
     * Основной цикл обработки сообщений.
     */
    @SuppressWarnings("unchecked")
    private void processMessages() {
        try {
            var records = consumer.poll(pollTimeout);
            if (!records.isEmpty()) {
                log.trace("Получено {} записей из топика '{}'", records.count(), topicName);
                ((RecordProcessor) recordProcessor).processRecords(records);
            }
        } catch (WakeupException e) {
            // Это ожидаемое исключение для выхода из цикла, бросаем его наверх.
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при обработке сообщений из топика '{}'", topicName, e);
            if (config.shouldStopOnError()) {
                throw new KafkaListenerException.ProcessingException("Критическая ошибка обработки в " + topicName, e);
            }
            // Если не нужно останавливаться, просто логируем и продолжаем
            sleepOnError();
        }
    }

    /**
     * Инициирует остановку слушателя.
     * Метод является идемпотентным.
     */
    public void shutdown() {
        if (shutdownRequested.compareAndSet(false, true)) {
            log.info("Инициация остановки слушателя для топика '{}'...", topicName);
            // Прерываем poll() в консьюмере, если он активен
            if (consumer != null) {
                consumer.wakeup();
            }
            // Ожидаем завершения задачи
            if (listeningTask != null) {
                try {
                    listeningTask.get(config.getShutdownTimeout().toMillis(), TimeUnit.MILLISECONDS);
                    log.info("Слушатель для топика '{}' успешно остановлен.", topicName);
                } catch (Exception e) {
                    log.warn("Не удалось корректно остановить слушатель для топика '{}' за таймаут. Принудительное завершение.", topicName, e);
                    listeningTask.cancel(true);
                }
            }
            isRunning.set(false);
        }
    }

    @Override
    public void close() {
        shutdown();
    }

    private KafkaConsumer<String, ?> createAndSubscribeConsumer() {
        KafkaConsumer<String, ?> newConsumer = isAvro
                ? consumerFactory.createAvroConsumer(topicName)
                : consumerFactory.createStringConsumer(topicName);

        newConsumer.subscribe(List.of(topicName));
        // Начальный poll для назначения партиций
        newConsumer.poll(INITIAL_POLL_TIMEOUT);
        var partitions = newConsumer.assignment();
        if (!partitions.isEmpty()) {
            newConsumer.seekToEnd(partitions); // Начинаем читать с конца
            log.info("Consumer подписан на {} партиций топика '{}' и смещен в конец.", partitions.size(), topicName);
        } else {
            log.warn("Для топика '{}' не назначено ни одной партиции.", topicName);
        }
        return newConsumer;
    }

    private void cleanupAfterLoop() {
        isRunning.set(false);
        this.consumer = null; // Убираем ссылку на закрытый консьюмер
        log.info("Цикл прослушивания для топика '{}' завершен, ресурсы освобождены.", topicName);
    }

    private Void handleCriticalError(Throwable throwable) {
        log.error("Критическая неперехваченная ошибка в задаче прослушивания для топика '{}'", topicName, throwable);
        isRunning.set(false);
        shutdownRequested.set(true);
        return null;
    }

    private RecordProcessor<?> createRecordProcessor(KafkaRecordsManager recordsManager) {
        return isAvro
                ? new RecordProcessorAvro(topicName, config, recordsManager)
                : new RecordProcessorString(topicName, config, recordsManager);
    }

    private void sleepOnError() {
        try {
            log.warn("Ожидание {} перед следующей попыткой...", config.getErrorRetryDelay());
            Thread.sleep(config.getErrorRetryDelay().toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Поток слушателя был прерван во время ожидания после ошибки. Завершение работы.");
            shutdownRequested.set(true); // Устанавливаем флаг для выхода из цикла
        }
    }

    private boolean shouldContinueProcessing() {
        return isRunning.get() && !shutdownRequested.get() && !Thread.currentThread().isInterrupted();
    }

    /**
     * Проверяет, активен ли слушатель в данный момент.
     *
     * @return {@code true}, если слушатель запущен и не в процессе остановки.
     */
    public boolean isRunning() {
        return isRunning.get() && !shutdownRequested.get();
    }

    /**
     * Возвращает имя топика, который прослушивается.
     *
     * @return Имя топика.
     */
    public String getTopicName() {
        return topicName;
    }
}
