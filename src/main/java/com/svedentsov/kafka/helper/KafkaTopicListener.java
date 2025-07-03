package com.svedentsov.kafka.helper;

import com.svedentsov.kafka.config.KafkaListenerConfig;
import com.svedentsov.kafka.exception.KafkaListenerException;
import com.svedentsov.kafka.factory.ConsumerFactory;
import com.svedentsov.kafka.processor.RecordProcessor;
import com.svedentsov.kafka.processor.RecordProcessorAvro;
import com.svedentsov.kafka.processor.RecordProcessorString;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final ConsumerStartStrategy startStrategy;
    private final Duration lookBackDuration;

    /**
     * Стратегии запуска консьюмера.
     */
    public enum ConsumerStartStrategy {
        /**
         * Начинать читать только новые записи (текущее поведение seekToEnd).
         */
        LATEST,
        /**
         * Начинать читать с самого начала топика.
         */
        EARLIEST,
        /**
         * Начинать читать с определенной временной метки (или ближайшего смещения).
         */
        FROM_TIMESTAMP
    }

    /**
     * Создает экземпляр слушателя топика.
     *
     * @param topicName        Имя топика для прослушивания.
     * @param pollTimeout      Таймаут для операции poll.
     * @param isAvro           {@code true}, если контент в формате Avro.
     * @param config           Общая конфигурация слушателей.
     * @param consumerFactory  Фабрика для создания Kafka Consumer.
     * @param recordsManager   Менеджер для сохранения полученных записей.
     * @param startStrategy    Стратегия, определяющая, с какого смещения начать чтение.
     * @param lookBackDuration Если startStrategy - {@link ConsumerStartStrategy#FROM_TIMESTAMP}, то это продолжительность,
     *                         на которую нужно "оглянуться" назад от текущего момента. Может быть {@code null} для других стратегий.
     * @throws IllegalArgumentException если {@code topicName} пустой, {@code config} или {@code consumerFactory} равны {@code null},
     *                                  или {@code startStrategy} равен {@link ConsumerStartStrategy#FROM_TIMESTAMP}, но {@code lookBackDuration} равен {@code null}.
     */
    public KafkaTopicListener(String topicName, Duration pollTimeout, boolean isAvro, KafkaListenerConfig config, ConsumerFactory consumerFactory, KafkaRecordsManager recordsManager, ConsumerStartStrategy startStrategy, Duration lookBackDuration) {
        this.topicName = requireNonBlank(topicName, "Название топика не может быть null или пустым.");
        this.pollTimeout = validatePollTimeout(pollTimeout);
        this.isAvro = isAvro;
        this.config = requireNonNull(config, "KafkaListenerConfig не может быть null.");
        this.consumerFactory = requireNonNull(consumerFactory, "ConsumerFactory не может быть null.");
        this.recordProcessor = createRecordProcessor(requireNonNull(recordsManager, "KafkaRecordsManager не может быть null."));
        this.startStrategy = requireNonNull(startStrategy, "Стратегия запуска не может быть null.");
        this.lookBackDuration = lookBackDuration;

        if (startStrategy == ConsumerStartStrategy.FROM_TIMESTAMP && lookBackDuration == null) {
            throw new IllegalArgumentException("lookBackDuration не может быть null, если startStrategy - FROM_TIMESTAMP.");
        }
        if (startStrategy != ConsumerStartStrategy.FROM_TIMESTAMP && lookBackDuration != null) {
            log.warn("lookBackDuration указан, но не будет использован, так как startStrategy не FROM_TIMESTAMP.");
        }
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

    /**
     * Основной метод, выполняющий цикл прослушивания Kafka.
     * Включает создание и подписку на консьюмера, применение стратегии старта
     * и непрерывную обработку сообщений до запроса на остановку или возникновения критической ошибки.
     */
    private void runListeningLoop() {
        try (KafkaConsumer<String, ?> consumer = createAndSubscribeConsumer()) {
            this.consumer = consumer; // Делаем консьюмер доступным для метода shutdown()
            applyStartStrategy(); // Применяем стратегию запуска после подписки и назначения партиций
            log.info("Начало цикла прослушивания для топика '{}'", topicName);
            while (shouldContinueProcessing()) {
                processMessages();
            }
        } catch (WakeupException e) {
            log.info("Цикл прослушивания для топика '{}' прерван вызовом wakeup().", topicName);
        } catch (Exception e) {
            log.error("Критическая ошибка в слушателе для топика '{}'. Работа будет остановлена.", topicName, e);
            throw new KafkaListenerException.LifecycleException("Критическая ошибка в жизненном цикле слушателя " + topicName, e);
        } finally {
            cleanupAfterLoop();
        }
    }

    /**
     * Выполняет операцию poll для получения сообщений из Kafka и их последующую обработку.
     * В случае ошибок обработки, логирует их и, при необходимости, приостанавливает работу
     * в зависимости от конфигурации.
     *
     * @throws WakeupException если был вызван {@code wakeup()} на консьюмере.
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
            throw e; // Это ожидаемое исключение для выхода из цикла, бросаем его наверх.
        } catch (Exception e) {
            log.error("Ошибка при обработке сообщений из топика '{}'", topicName, e);
            if (config.shouldStopOnError()) {
                throw new KafkaListenerException.ProcessingException("Критическая ошибка обработки в " + topicName, e);
            }
            sleepOnError(); // Если не нужно останавливаться, просто логируем и продолжаем
        }
    }

    /**
     * Инициирует остановку слушателя.
     * Метод является идемпотентным, то есть повторные вызовы не приведут к дополнительным эффектам.
     * Пытается корректно завершить работу консьюмера и ожидающей задачи.
     */
    public void shutdown() {
        if (shutdownRequested.compareAndSet(false, true)) {
            log.info("Инициация остановки слушателя для топика '{}'...", topicName);
            if (consumer != null) { // Прерываем poll() в консьюмере, если он активен
                consumer.wakeup();
            }
            if (listeningTask != null) { // Ожидаем завершения задачи прослушивания
                try {
                    listeningTask.get(config.getShutdownTimeout().toMillis(), TimeUnit.MILLISECONDS);
                    log.info("Слушатель для топика '{}' успешно остановлен.", topicName);
                } catch (Exception e) {
                    log.warn("Не удалось корректно остановить слушатель для топика '{}' за таймаут. Принудительное завершение.", topicName, e);
                    listeningTask.cancel(true); // Принудительно отменяем задачу
                }
            }
            isRunning.set(false); // Устанавливаем флаг, что слушатель неактивен
        }
    }

    /**
     * Закрывает слушатель, вызывая метод {@link #shutdown()}.
     * Позволяет использовать {@code KafkaTopicListener} в конструкции try-with-resources.
     */
    @Override
    public void close() {
        shutdown();
    }

    /**
     * Создает новый экземпляр {@link KafkaConsumer} и подписывается на указанный топик.
     * Выполняет начальный опрос для назначения партиций консьюмеру.
     *
     * @return Настроенный и подписанный {@link KafkaConsumer}.
     */
    private KafkaConsumer<String, ?> createAndSubscribeConsumer() {
        KafkaConsumer<String, ?> newConsumer = isAvro
                ? consumerFactory.createAvroConsumer(topicName)
                : consumerFactory.createStringConsumer(topicName);
        newConsumer.subscribe(List.of(topicName));
        // Начальный poll для назначения партиций
        newConsumer.poll(INITIAL_POLL_TIMEOUT); // Важно, чтобы партиции были назначены до применения стратегии смещения
        return newConsumer;
    }

    /**
     * Применяет выбранную стратегию запуска консьюмера ({@link ConsumerStartStrategy}).
     * В зависимости от стратегии, консьюмер будет смещен на начало, конец или на определенную временную метку.
     * Если партиции не назначены, выводит предупреждение.
     */
    private void applyStartStrategy() {
        var partitions = consumer.assignment();
        if (partitions.isEmpty()) {
            log.warn("Для топика '{}' не назначено ни одной партиции. Невозможно применить стратегию смещения.", topicName);
            return;
        }

        switch (startStrategy) {
            case LATEST:
                consumer.seekToEnd(partitions);
                log.info("Consumer для топика '{}' смещен в конец всех {} партиций (LATEST).", topicName, partitions.size());
                break;
            case EARLIEST:
                consumer.seekToBeginning(partitions);
                log.info("Consumer для топика '{}' смещен в начало всех {} партиций (EARLIEST).", topicName, partitions.size());
                break;
            case FROM_TIMESTAMP:
                // Вычисляем целевую временную метку для поиска смещений
                long targetTimestamp = Instant.now().minus(lookBackDuration).toEpochMilli();
                Map<TopicPartition, Long> timestampsToSearch = new HashMap<>();
                for (TopicPartition partition : partitions) {
                    timestampsToSearch.put(partition, targetTimestamp);
                }

                // Запрашиваем смещения по временным меткам
                Map<TopicPartition, OffsetAndTimestamp> offsets = consumer.offsetsForTimes(timestampsToSearch);
                for (TopicPartition partition : partitions) {
                    OffsetAndTimestamp offsetAndTimestamp = offsets.get(partition);
                    if (offsetAndTimestamp != null) {
                        consumer.seek(partition, offsetAndTimestamp.offset());
                        log.info("Consumer для топика '{}', партиция {} смещен на смещение {} (timestamp: {}).",
                                topicName, partition.partition(), offsetAndTimestamp.offset(), offsetAndTimestamp.timestamp());
                    } else {
                        // Если для timestamp не найдено смещения (например, timestamp раньше самой первой записи),
                        // смещаемся в начало, чтобы не пропустить возможные старые записи.
                        consumer.seekToBeginning(Collections.singleton(partition));
                        log.warn("Для топика '{}', партиция {} не найдено смещения по timestamp {}. Смещен в начало.",
                                topicName, partition.partition(), targetTimestamp);
                    }
                }
                break;
            default:
                log.warn("Неизвестная стратегия запуска консьюмера: {}. Используется стратегия LATEST.", startStrategy);
                consumer.seekToEnd(partitions);
                break;
        }
    }

    /**
     * Выполняет очистку ресурсов после завершения цикла прослушивания.
     * Устанавливает флаг {@code isRunning} в {@code false} и обнуляет ссылку на консьюмера.
     */
    private void cleanupAfterLoop() {
        isRunning.set(false);
        this.consumer = null; // Убираем ссылку на закрытый консьюмер
        log.info("Цикл прослушивания для топика '{}' завершен, ресурсы освобождены.", topicName);
    }

    /**
     * Обрабатывает критические неперехваченные ошибки, возникшие в задаче прослушивания.
     * Логирует ошибку, устанавливает флаги остановки и возвращает {@code null} для завершения {@link CompletableFuture}.
     *
     * @param throwable Исключение, которое привело к критической ошибке.
     * @return Всегда {@code null}.
     */
    private Void handleCriticalError(Throwable throwable) {
        log.error("Критическая неперехваченная ошибка в задаче прослушивания для топика '{}'", topicName, throwable);
        isRunning.set(false);
        shutdownRequested.set(true);
        return null;
    }

    /**
     * Создает соответствующий {@link RecordProcessor} (Avro или String) на основе флага {@code isAvro}.
     *
     * @param recordsManager Менеджер для сохранения полученных записей.
     * @return Экземпляр {@link RecordProcessor}.
     */
    private RecordProcessor<?> createRecordProcessor(KafkaRecordsManager recordsManager) {
        return isAvro
                ? new RecordProcessorAvro(topicName, config, recordsManager)
                : new RecordProcessorString(topicName, config, recordsManager);
    }

    /**
     * Приостанавливает выполнение потока слушателя на заданное время после возникновения ошибки.
     * Это помогает избежать "спама" ошибками и снизить нагрузку при временных проблемах.
     */
    private void sleepOnError() {
        try {
            log.warn("Ожидание {} перед следующей попыткой...", config.getErrorRetryDelay());
            Thread.sleep(config.getErrorRetryDelay().toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Восстанавливаем флаг прерывания
            log.warn("Поток слушателя был прерван во время ожидания после ошибки. Завершение работы.");
            shutdownRequested.set(true); // Устанавливаем флаг для выхода из цикла
        }
    }

    /**
     * Проверяет, следует ли продолжать обработку сообщений.
     * Возвращает {@code true}, если слушатель запущен, остановка не запрошена и поток не прерван.
     *
     * @return {@code true}, если цикл прослушивания должен продолжаться; иначе {@code false}.
     */
    private boolean shouldContinueProcessing() {
        return isRunning.get() && !shutdownRequested.get() && !Thread.currentThread().isInterrupted();
    }

    /**
     * Проверяет, активен ли слушатель в данный момент.
     *
     * @return {@code true}, если слушатель запущен и не находится в процессе остановки; иначе {@code false}.
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
