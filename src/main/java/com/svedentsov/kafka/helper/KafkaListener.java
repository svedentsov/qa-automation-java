package com.svedentsov.kafka.helper;

import com.svedentsov.kafka.pool.KafkaClientPool;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

                processRecords(topic, consumer, timeout, isAvro);
            } catch (WakeupException e) {
                handleWakeupException(topic, e);
            } catch (Exception e) {
                handleException(topic, e);
            }
        };
    }

    /**
     * Обрабатывает записи, полученные из Kafka с обработкой ошибок десериализации.
     * Записи добавляются в {@link KafkaRecordsManager}. Процесс продолжается до тех пор, пока поток не будет прерван.
     *
     * @param topic    название топика, из которого были получены записи
     * @param consumer экземпляр {@link KafkaConsumer} для получения сообщений
     * @param timeout  максимальная продолжительность ожидания сообщений от Kafka
     * @param isAvro   указывает, используется ли формат Avro для сообщений
     * @param <V>      тип значений сообщений
     */
    private static <V> void processRecords(String topic, KafkaConsumer<String, V> consumer, Duration timeout, boolean isAvro) {
        try {
            while (!Thread.currentThread().isInterrupted()) { // Продолжаем работать, пока текущий поток не будет прерван
                try {
                    ConsumerRecords<String, V> records = consumer.poll(timeout); // Пытаемся получить записи из Kafka в течение заданного времени ожидания
                    if (isAvro) {
                        ConsumerRecords<String, V> validRecords = filterValidAvroRecords(topic, records); // Для Avro-сообщений фильтруем валидные записи
                        KafkaRecordsManager.addRecords(topic, validRecords); // Добавляем полученные записи в менеджер записей Kafka
                    } else {
                        KafkaRecordsManager.addRecords(topic, records); // Для строковых сообщений добавляем все записи
                    }
                } catch (SerializationException e) {
                    handleSerializationException(topic, e); // Обрабатываем ошибки десериализации
                } catch (Exception e) { // Обрабатываем другие исключения, но не останавливаем процесс
                    log.warn("Ошибка при обработке записи из топика {}: {}. Продолжаем обработку.", topic, e.getMessage());
                }
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
     * Фильтрует валидные Avro-записи, пропуская те, которые вызывают ошибки десериализации.
     *
     * @param topic   название топика
     * @param records исходные записи
     * @param <V>     тип значений сообщений
     * @return отфильтрованные записи без невалидных Avro-сообщений
     */
    @SuppressWarnings("unchecked")
    private static <V> ConsumerRecords<String, V> filterValidAvroRecords(String topic, ConsumerRecords<String, V> records) {
        Map<TopicPartition, List<ConsumerRecord<String, V>>> validRecordsMap = new HashMap<>();
        int invalidRecordsCount = 0;

        for (ConsumerRecord<String, V> record : records) {
            try {
                V value = record.value(); // Попытка получить значение записи для проверки десериализации
                validRecordsMap.computeIfAbsent( // Если десериализация прошла успешно, добавляем запись
                        new TopicPartition(record.topic(), record.partition()),
                        k -> new ArrayList<>()
                ).add(record);
            } catch (SerializationException e) {
                invalidRecordsCount++;
                logInvalidRecord(topic, record, e);
            } catch (Exception e) {
                invalidRecordsCount++;
                log.warn("Неожиданная ошибка при обработке записи из топика {} (offset: {}, partition: {}): {}. Запись пропущена.",
                        topic, record.offset(), record.partition(), e.getMessage());
            }
        }

        if (invalidRecordsCount > 0) {
            log.info("Из топика {} отфильтровано {} невалидных Avro-записей", topic, invalidRecordsCount);
        }

        return new ConsumerRecords<>(validRecordsMap);
    }

    /**
     * Логирует информацию о невалидной записи.
     *
     * @param topic  название топика
     * @param record запись с ошибкой
     * @param e      исключение десериализации
     * @param <V>    тип значения записи
     */
    private static <V> void logInvalidRecord(String topic, ConsumerRecord<String, V> record, Exception e) {
        log.warn("Невалидная Avro-запись в топике {} пропущена. Offset: {}, Partition: {}, Timestamp: {}, Key: {}, Error: {}",
                topic, record.offset(), record.partition(), record.timestamp(), record.key(), e.getMessage());
    }

    /**
     * Обрабатывает исключения сериализации.
     *
     * @param topic название топика
     * @param e     исключение сериализации
     */
    private static void handleSerializationException(String topic, SerializationException e) {
        log.warn("Ошибка десериализации в топике {}: {}. Сообщение пропущено.", topic, e.getMessage());
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
