package com.svedentsov.kafka.helper;

import com.svedentsov.kafka.config.KafkaConfigListener;
import com.svedentsov.kafka.exception.KafkaListenerException;
import com.svedentsov.kafka.factory.ConsumerFactory;
import com.svedentsov.kafka.helper.strategy.ConsumerStartStrategy;
import com.svedentsov.kafka.processor.RecordProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.svedentsov.kafka.utils.ValidationUtils.requireNonBlank;
import static com.svedentsov.kafka.utils.ValidationUtils.validatePollTimeout;
import static java.util.Objects.requireNonNull;

/**
 * Управляет жизненным циклом прослушивания одного топика Kafka.
 * <p>Этот класс отвечает за создание Kafka-консьюмера, подписку на топик,
 * получение сообщений в отдельном потоке и их обработку с помощью {@link RecordProcessor}.
 * Он также управляет корректным запуском и остановкой процесса прослушивания,
 * используя {@link CompletableFuture} для асинхронного выполнения и {@link ConsumerStartStrategy}
 * для определения начальной позиции чтения в топике.
 * <p>Класс реализует {@link AutoCloseable}, что позволяет использовать его в try-with-resources блоках
 * для гарантированного освобождения ресурсов.
 *
 * @param <V> Тип значения сообщения в Kafka (например, {@code String} или {@code GenericRecord}).
 */
@Slf4j
public class KafkaTopicListener<V> implements AutoCloseable {

    private final String topicName;
    private final Duration pollTimeout;
    private final KafkaConfigListener config;
    private final ConsumerFactory consumerFactory;
    private final RecordProcessor<V> recordProcessor;
    private final ConsumerStartStrategy startStrategy;
    private final boolean isAvroTopic; // Добавляем поле для типа топика

    private final AtomicBoolean shutdownInitiated = new AtomicBoolean(false);
    private volatile KafkaConsumer<String, V> consumer;
    private volatile CompletableFuture<Void> listeningTask;

    /**
     * Создает экземпляр слушателя топика.
     *
     * @param topicName       Название прослушиваемого топика. Не может быть пустым или null.
     * @param pollTimeout     Максимальное время блокировки в методе {@code poll()}.
     * @param config          Общая конфигурация для слушателей.
     * @param consumerFactory Фабрика для создания экземпляров {@link KafkaConsumer}.
     * @param recordProcessor Обработчик для полученных записей.
     * @param startStrategy   Стратегия для определения начального смещения при подключении к топику.
     * @param isAvroTopic     {@code true}, если топик содержит сообщения в формате Avro, иначе {@code false}.
     */
    public KafkaTopicListener(String topicName, Duration pollTimeout, KafkaConfigListener config, ConsumerFactory consumerFactory, RecordProcessor<V> recordProcessor, ConsumerStartStrategy startStrategy, boolean isAvroTopic) {
        this.topicName = requireNonBlank(topicName, "Название топика не может быть null или пустым.");
        this.pollTimeout = validatePollTimeout(pollTimeout);
        this.config = requireNonNull(config, "KafkaListenerConfig не может быть null.");
        this.consumerFactory = requireNonNull(consumerFactory, "ConsumerFactory не может быть null.");
        this.recordProcessor = requireNonNull(recordProcessor, "RecordProcessor не может быть null.");
        this.startStrategy = requireNonNull(startStrategy, "Стратегия запуска не может быть null.");
        this.isAvroTopic = isAvroTopic;
    }

    /**
     * Асинхронно запускает процесс прослушивания топика в указанном пуле потоков.
     *
     * @param executor Пул потоков для выполнения задачи прослушивания.
     * @throws IllegalStateException если слушатель уже запущен или находится в процессе остановки.
     */
    public void start(ExecutorService executor) {
        requireNonNull(executor, "ExecutorService не может быть null.");
        if (listeningTask != null && !listeningTask.isDone()) {
            throw new IllegalStateException("Слушатель для топика " + topicName + " уже запущен.");
        }
        shutdownInitiated.set(false);
        log.info("Запуск слушателя для топика '{}'...", topicName);
        listeningTask = CompletableFuture.runAsync(this::runListeningLoop, executor).exceptionally(this::handleCriticalError);
        log.info("Слушатель для топика '{}' успешно запущен.", topicName);
    }

    /**
     * Основной цикл прослушивания и обработки сообщений.
     * Выполняется в отдельном потоке.
     */
    private void runListeningLoop() {
        try (KafkaConsumer<String, V> newConsumer = isAvroTopic
                ? (KafkaConsumer<String, V>) consumerFactory.createAvroConsumer(topicName)
                : (KafkaConsumer<String, V>) consumerFactory.createStringConsumer(topicName)) {
            this.consumer = newConsumer; // Присваиваем полю, чтобы shutdown мог вызвать wakeup()
            // Подписываемся на топик с использованием ConsumerRebalanceListener для надежного применения стратегии смещения.
            newConsumer.subscribe(List.of(topicName), new ConsumerRebalanceListener() {
                @Override
                public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                    log.info("Партиции отозваны для топика '{}': {}", topicName, partitions);
                }

                @Override
                public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                    log.info("Назначены новые партиции для топика '{}': {}", topicName, partitions);
                    if (!partitions.isEmpty()) {
                        startStrategy.apply(newConsumer, partitions, topicName);
                    }
                }
            });

            log.info("Начало цикла прослушивания для топика '{}'", topicName);
            while (!shutdownInitiated.get() && !Thread.currentThread().isInterrupted()) {
                processMessages();
            }
        } catch (WakeupException e) {
            log.info("Цикл прослушивания для топика '{}' штатно прерван вызовом wakeup().", topicName);
        } catch (Exception e) {
            log.error("Критическая ошибка в слушателе для топика '{}'. Работа будет остановлена.", topicName, e);
            throw new KafkaListenerException.LifecycleException("Критическая ошибка в жизненном цикле слушателя " + topicName, e);
        } finally {
            if (this.consumer != null) {
                this.consumer.close(); // Закрываем консьюмер в finally блоке
            }
            this.consumer = null; // Убираем ссылку на закрытый консьюмер
            log.info("Цикл прослушивания для топика '{}' завершен, ресурсы освобождены.", topicName);
        }
    }

    /**
     * Получает и обрабатывает пачку сообщений из Kafka.
     */
    private void processMessages() {
        try {
            var records = consumer.poll(pollTimeout);
            if (!records.isEmpty()) {
                log.trace("Получено {} записей из топика '{}'", records.count(), topicName);
                recordProcessor.processRecords(records);
            }
        } catch (WakeupException e) {
            // Это ожидаемое исключение для выхода из цикла, пробрасываем его наверх.
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при обработке сообщений из топика '{}'", topicName, e);
            if (config.shouldStopOnError()) {
                throw new KafkaListenerException.ProcessingException("Критическая ошибка обработки в " + topicName, e);
            }
            sleepOnError();
        }
    }

    /**
     * Инициирует корректную остановку слушателя.
     * Прерывает блокирующий вызов {@code poll()} и ожидает завершения задачи.
     */
    public void shutdown() {
        if (shutdownInitiated.compareAndSet(false, true)) {
            log.info("Инициация остановки слушателя для топика '{}'...", topicName);

            final KafkaConsumer<String, V> currentConsumer = this.consumer;
            if (currentConsumer != null) {
                // Прерываем poll() в консьюмере, если он активен.
                // Это приведет к WakeupException в цикле прослушивания.
                currentConsumer.wakeup();
            }

            if (listeningTask != null) {
                try {
                    // Ожидаем завершения задачи прослушивания
                    listeningTask.get(config.getShutdownTimeout().toMillis(), TimeUnit.MILLISECONDS);
                    log.info("Слушатель для топика '{}' успешно остановлен.", topicName);
                } catch (Exception e) {
                    log.warn("Не удалось корректно остановить слушатель для топика '{}' за таймаут. Принудительное завершение.", topicName, e);
                    // Принудительно отменяем задачу, если она не завершилась сама
                    listeningTask.cancel(true);
                }
            }
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
     * Обрабатывает критические неперехваченные исключения из {@link CompletableFuture}.
     *
     * @param throwable Исключение, вызвавшее сбой.
     * @return null, как того требует {@link CompletableFuture#exceptionally}.
     */
    private Void handleCriticalError(Throwable throwable) {
        log.error("Критическая неперехваченная ошибка в задаче прослушивания для топика '{}'", topicName, throwable);
        shutdownInitiated.set(true); // Предотвращаем перезапуск
        return null;
    }

    /**
     * Выполняет задержку потока в случае некритической ошибки обработки.
     */
    private void sleepOnError() {
        try {
            log.warn("Ожидание {} перед следующей попыткой...", config.getErrorRetryDelay());
            Thread.sleep(config.getErrorRetryDelay().toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Восстанавливаем флаг прерывания
            log.warn("Поток слушателя был прерван во время ожидания после ошибки. Завершение работы.");
            // Устанавливаем флаг для выхода из основного цикла.
            shutdownInitiated.set(true);
        }
    }

    /**
     * Проверяет, активен ли в данный момент слушатель.
     *
     * @return {@code true}, если слушатель запущен и не находится в процессе остановки.
     */
    public boolean isRunning() {
        return listeningTask != null && !listeningTask.isDone() && !shutdownInitiated.get();
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
