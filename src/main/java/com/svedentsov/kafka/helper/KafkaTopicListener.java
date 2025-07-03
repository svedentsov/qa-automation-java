package com.svedentsov.kafka.helper;

import com.svedentsov.kafka.config.KafkaListenerConfig;
import com.svedentsov.kafka.exception.KafkaListenerException;
import com.svedentsov.kafka.factory.ConsumerFactory;
import com.svedentsov.kafka.helper.strategy.ConsumerStartStrategy;
import com.svedentsov.kafka.processor.RecordProcessor;
import com.svedentsov.kafka.processor.RecordProcessorAvro;
import com.svedentsov.kafka.processor.RecordProcessorString;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.svedentsov.kafka.utils.ValidationUtils.requireNonBlank;
import static com.svedentsov.kafka.utils.ValidationUtils.validatePollTimeout;
import static java.util.Objects.requireNonNull;

/**
 * {@code KafkaTopicListener} отвечает за прослушивание одного Kafka топика.
 * Он управляет жизненным циклом KafkaConsumer, обработкой записей и применением
 * стратегий запуска.
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
    private final ConsumerStartStrategy startStrategy; // Используем интерфейс стратегии

    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicBoolean shutdownRequested = new AtomicBoolean(false);

    private volatile KafkaConsumer<String, ?> consumer;
    private volatile CompletableFuture<Void> listeningTask;

    /**
     * Создает новый экземпляр {@code KafkaTopicListener}.
     *
     * @param topicName       Имя Kafka топика для прослушивания. Не может быть null или пустым.
     * @param pollTimeout     Таймаут для операции {@code consumer.poll()}. Не может быть null и должен быть положительным.
     * @param isAvro          Флаг, указывающий, являются ли сообщения в топике Avro или строковыми.
     * @param config          Конфигурация слушателя Kafka. Не может быть null.
     * @param consumerFactory Фабрика для создания экземпляров KafkaConsumer. Не может быть null.
     * @param recordsManager  Менеджер для обработки и хранения полученных записей. Не может быть null.
     * @param startStrategy   Стратегия, определяющая начальное смещение для потребителя. Не может быть null.
     * @throws IllegalArgumentException если {@code topicName} пуст, {@code pollTimeout} недействителен,
     *                                  или если {@code startStrategy} требует дополнительных параметров,
     *                                  которые не были предоставлены или были предоставлены некорректно.
     */
    public KafkaTopicListener(String topicName, Duration pollTimeout, boolean isAvro, KafkaListenerConfig config, ConsumerFactory consumerFactory, KafkaRecordsManager recordsManager, ConsumerStartStrategy startStrategy) {
        this.topicName = requireNonBlank(topicName, "Название топика не может быть null или пустым.");
        this.pollTimeout = validatePollTimeout(pollTimeout);
        this.isAvro = isAvro;
        this.config = requireNonNull(config, "KafkaListenerConfig не может быть null.");
        this.consumerFactory = requireNonNull(consumerFactory, "ConsumerFactory не может быть null.");
        this.recordProcessor = createRecordProcessor(requireNonNull(recordsManager, "KafkaRecordsManager не может быть null."));
        this.startStrategy = requireNonNull(startStrategy, "Стратегия запуска не может быть null.");
    }

    /**
     * Запускает прослушивание топика в асинхронном режиме.
     * Этот метод инициирует цикл прослушивания в отдельном потоке, предоставляемом {@code executor}.
     * Если слушатель уже запущен, будет выброшено исключение {@link IllegalStateException}.
     *
     * @param executor Сервис-исполнитель (ExecutorService) для запуска задачи прослушивания. Не может быть null.
     * @throws IllegalStateException если слушатель уже запущен.
     */
    public void start(ExecutorService executor) {
        requireNonNull(executor, "ExecutorService не может быть null.");
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
     * Основной цикл прослушивания Kafka.
     * Этот метод выполняется в отдельном потоке и содержит логику получения и обработки сообщений.
     * Он автоматически создает и подписывает KafkaConsumer, применяет стратегию запуска,
     * а затем непрерывно опрашивает брокер на наличие новых записей, пока не будет запрошено завершение работы.
     *
     * @throws KafkaListenerException.LifecycleException если происходит критическая ошибка в жизненном цикле слушателя.
     */
    private void runListeningLoop() {
        try (KafkaConsumer<String, ?> consumer = createAndSubscribeConsumer()) {
            this.consumer = consumer; // Делаем консьюмер доступным для метода shutdown()

            // Применяем стратегию запуска после того, как партиции будут назначены консьюмеру
            Set<TopicPartition> assignedPartitions = consumer.assignment();
            if (assignedPartitions.isEmpty()) {
                log.warn("Для топика '{}' не назначено ни одной партиции. Невозможно применить стратегию смещения.", topicName);
            } else {
                startStrategy.apply(consumer, assignedPartitions, topicName);
            }

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
     * Обрабатывает полученные сообщения из Kafka.
     * Вызывает {@code consumer.poll()} для получения записей и передает их в {@code RecordProcessor}.
     * В случае ошибки обработки, логирует её и либо останавливает работу (если настроено), либо ожидает.
     *
     * @throws WakeupException                            если consumer был "разбужен" вызовом {@code wakeup()}.
     * @throws KafkaListenerException.ProcessingException если возникает ошибка обработки и {@code shouldStopOnError} установлен в true.
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
     * Инициирует процесс остановки слушателя.
     * Метод безопасно прерывает цикл прослушивания, вызывая {@code consumer.wakeup()}
     * и ожидает завершения асинхронной задачи прослушивания.
     * Если задача не завершается в течение заданного таймаута, она принудительно отменяется.
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
     * Реализация метода {@code AutoCloseable.close()}, вызывающая {@code shutdown()}.
     * Позволяет использовать {@code KafkaTopicListener} в try-with-resources блоках.
     */
    @Override
    public void close() {
        shutdown();
    }

    /**
     * Создает и подписывает новый KafkaConsumer на указанный топик.
     * Выполняет начальный {@code poll} для принудительного назначения партиций,
     * что важно перед применением стратегий смещения.
     *
     * @return Настроенный и подписанный экземпляр {@link KafkaConsumer}.
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
     * Выполняет очистку ресурсов после завершения цикла прослушивания.
     * Сбрасывает флаги состояния и обнуляет ссылку на consumer.
     */
    private void cleanupAfterLoop() {
        isRunning.set(false);
        this.consumer = null; // Убираем ссылку на закрытый консьюмер
        log.info("Цикл прослушивания для топика '{}' завершен, ресурсы освобождены.", topicName);
    }

    /**
     * Обрабатывает критические ошибки, которые возникают в асинхронной задаче прослушивания.
     * Логирует ошибку, сбрасывает флаги состояния, чтобы сигнализировать о завершении работы.
     *
     * @param throwable Исключение, которое привело к критической ошибке.
     * @return null, так как этот метод используется в {@code CompletableFuture.exceptionally}.
     */
    private Void handleCriticalError(Throwable throwable) {
        log.error("Критическая неперехваченная ошибка в задаче прослушивания для топика '{}'", topicName, throwable);
        isRunning.set(false);
        shutdownRequested.set(true);
        return null;
    }

    /**
     * Создает соответствующий процессор записей на основе типа сообщений (Avro или String).
     *
     * @param recordsManager Менеджер записей, используемый процессором.
     * @return Экземпляр {@link RecordProcessor}.
     */
    private RecordProcessor<?> createRecordProcessor(KafkaRecordsManager recordsManager) {
        return isAvro
                ? new RecordProcessorAvro(topicName, config, recordsManager)
                : new RecordProcessorString(topicName, config, recordsManager);
    }

    /**
     * Приостанавливает выполнение потока на заданный таймаут после возникновения ошибки.
     * Это предотвращает "спам" ошибками в логе и быстрый цикл при непрерывных сбоях.
     * Обрабатывает прерывание потока во время ожидания.
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
     * Проверяет, должен ли цикл прослушивания продолжать работу.
     * Учитывает флаги {@code isRunning}, {@code shutdownRequested} и статус прерывания потока.
     *
     * @return true, если цикл должен продолжаться; false в противном случае.
     */
    private boolean shouldContinueProcessing() {
        return isRunning.get() && !shutdownRequested.get() && !Thread.currentThread().isInterrupted();
    }

    /**
     * Проверяет, активен ли в данный момент слушатель.
     *
     * @return true, если слушатель запущен и не запрошена его остановка; false в противном случае.
     */
    public boolean isRunning() {
        return isRunning.get() && !shutdownRequested.get();
    }

    /**
     * Возвращает имя топика, который прослушивает данный слушатель.
     *
     * @return Имя топика.
     */
    public String getTopicName() {
        return topicName;
    }
}
