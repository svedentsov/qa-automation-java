package kafka.helper;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import kafka.pool.KafkaClientPool;

import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Утилитарный класс для управления прослушиванием топиков Kafka.
 * Этот класс предоставляет методы для запуска и остановки процессов прослушивания сообщений из топиков Kafka.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaListener {

    private static final Map<String, ExecutorService> LISTENER_EXECUTORS = new ConcurrentHashMap<>();
    private static final int SHUTDOWN_TIMEOUT = 10;

    /**
     * Запускает процесс прослушивания топика.
     * Если топик уже прослушивается, выводит предупреждение.
     */
    public static void startListening(String topic, Duration timeout, boolean isAvro) {
        if (LISTENER_EXECUTORS.containsKey(topic)) {
            log.warn("Попытка повторного запуска прослушивания для топика {}. Топик уже прослушивается.", topic);
            return;
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(createListenerTask(topic, timeout, isAvro));
        LISTENER_EXECUTORS.put(topic, executor);
        log.info("Запущено прослушивание топика {}", topic);
    }

    /**
     * Останавливает процесс прослушивания сообщений из указанного топика.
     * Если процесс прослушивания не был запущен, метод ничего не делает.
     *
     * @param topic название топика, для которого нужно остановить прослушивание
     */
    public static void stopListening(String topic) {
        ExecutorService executor = LISTENER_EXECUTORS.remove(topic); // Получаем и удаляем ExecutorService для указанного топика
        shutdownExecutorService(executor, topic); // Завершаем работу executor
    }

    /**
     * Завершает работу ExecutorService, ожидая его завершения.
     *
     * @param executorService ExecutorService, который нужно завершить
     * @param topic           название топика, для которого происходит завершение
     */
    private static void shutdownExecutorService(ExecutorService executorService, String topic) {
        if (Objects.isNull(executorService)) {
            log.warn("ExecutorService для топика {} не найден.", topic);
            return;
        }
        executorService.shutdown(); // Запрашиваем завершение работы executor
        try {
            // Ожидаем завершения работы executor в течение заданного таймаута
            if (!executorService.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.SECONDS)) {
                log.warn("Поток прослушивания топика {} не завершился вовремя.", topic);
            } else {
                log.info("Поток прослушивания топика {} завершен успешно.", topic);
            }
        } catch (InterruptedException e) {
            log.error("Ожидание завершения потока было прервано для топика: {}", topic, e);
            Thread.currentThread().interrupt(); // Устанавливаем флаг прерывания потока
        }
    }

    /**
     * Создает задачу для прослушивания сообщений топика Kafka.
     * Задача выполняется в отдельном потоке и обрабатывает сообщения до тех пор, пока поток не будет прерван.
     *
     * @param topic   название топика, из которого нужно слушать сообщения
     * @param timeout максимальная продолжительность ожидания сообщений от Kafka
     * @param isAvro  указывает, используется ли формат Avro для сообщений (если {@code true}, иначе используется строковый формат)
     * @return задача, которая будет выполнять прослушивание сообщений
     */
    private static Runnable createListenerTask(String topic, Duration timeout, boolean isAvro) {
        return () -> {
            KafkaConsumer<String, ?> consumer = null;
            try {
                consumer = isAvro
                        ? KafkaClientPool.getAvroConsumer(topic)
                        : KafkaClientPool.getStringConsumer(topic);

                consumer.subscribe(Collections.singletonList(topic));
                // Обеспечиваем начало чтения с текущего конца топика
                consumer.poll(Duration.ZERO); // Первоначальный poll для назначения разделов
                Set<TopicPartition> partitions = consumer.assignment(); // Получаем назначенные разделы
                consumer.seekToEnd(partitions); // Устанавливаем смещение на конец разделов

                processRecords(topic, consumer, timeout);
            } catch (WakeupException e) {
                handleWakeupException(topic, e);
            } catch (Exception e) {
                handleException(topic, e);
            }
        };
    }

    /**
     * Обрабатывает записи, полученные из Kafka.
     * Записи добавляются в {@link KafkaRecordsManager}. Процесс продолжается до тех пор, пока поток не будет прерван.
     *
     * @param topic    название топика, из которого были получены записи
     * @param consumer экземпляр {@link KafkaConsumer} для получения сообщений
     * @param timeout  максимальная продолжительность ожидания сообщений от Kafka
     * @param <V>      тип значений сообщений
     */
    private static <V> void processRecords(String topic, KafkaConsumer<String, V> consumer, Duration timeout) {
        try {
            // Продолжаем работать, пока текущий поток не будет прерван
            while (!Thread.currentThread().isInterrupted()) {
                // Пытаемся получить записи из Kafka в течение заданного времени ожидания
                ConsumerRecords<String, V> records = consumer.poll(timeout);
                // Добавляем полученные записи в менеджер записей Kafka
                KafkaRecordsManager.addRecords(topic, records);
            }
        } catch (WakeupException e) {
            handleWakeupException(topic, e);
        } catch (Exception e) {
            handleException(topic, e);
        } finally {
            consumer.close();
        }
    }

    /**
     * Обрабатывает WakeupException, возникающие при работе с KafkaConsumer.
     *
     * @param topic название топика, для которого возникло исключение
     * @param e     экземпляр WakeupException
     */
    private static void handleWakeupException(String topic, WakeupException e) {
        log.info("Consumer для топика {} был пробужден: {}. Завершаем работу.", topic, e.getMessage());
        Thread.currentThread().interrupt(); // Устанавливаем флаг прерывания потока
    }

    /**
     * Обрабатывает общие исключения, возникающие при работе с KafkaConsumer.
     *
     * @param topic название топика, для которого возникло исключение
     * @param e     экземпляр Exception
     */
    private static void handleException(String topic, Exception e) {
        log.error("Ошибка при обработке топика {}: {}", topic, e.getMessage(), e);
    }
}
