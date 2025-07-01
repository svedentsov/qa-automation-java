package com.svedentsov.kafka.helper;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static com.svedentsov.kafka.utils.ValidationUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * Потокобезопасный менеджер для хранения и доступа к записям (records), полученным из Kafka.
 * В отличие от статической реализации, этот класс создает экземпляр для каждой сессии
 * прослушивания, что позволяет изолировать данные между разными тестами или потоками
 * и избежать проблем с глобальным состоянием.
 */
public final class KafkaRecordsManager {

    // Ключ - топик, значение - карта записей для этого топика.
    // Внутренняя карта: ключ - смещение в партиции, значение - сама запись.
    private final ConcurrentMap<String, ConcurrentMap<PartitionOffset, ConsumerRecord<?, ?>>> recordsByTopic = new ConcurrentHashMap<>();

    /**
     * Уникальный идентификатор записи в рамках топика, состоящий из партиции и смещения.
     */
    private record PartitionOffset(int partition, long offset) {
        public static PartitionOffset from(ConsumerRecord<?, ?> record) {
            return new PartitionOffset(record.partition(), record.offset());
        }
    }

    /**
     * Добавляет одну запись в менеджер.
     * Если запись с таким же {@code partition} и {@code offset} уже существует, она не будет перезаписана.
     *
     * @param topic  Топик, из которого пришла запись. Не может быть пустым.
     * @param record Запись Kafka. Не может быть {@code null}.
     */
    public void addRecord(String topic, ConsumerRecord<?, ?> record) {
        requireNonBlank(topic, "Topic не может быть null или пустым.");
        requireNonNull(record, "ConsumerRecord не должен быть null.");
        var topicRecords = recordsByTopic.computeIfAbsent(topic, t -> new ConcurrentHashMap<>());
        topicRecords.putIfAbsent(PartitionOffset.from(record), record);
    }

    /**
     * Добавляет пачку записей в менеджер.
     *
     * @param topic   Топик, из которого пришли записи.
     * @param records Коллекция записей Kafka.
     */
    public void addRecords(String topic, ConsumerRecords<?, ?> records) {
        requireNonBlank(topic, "Topic не может быть null или пустым.");
        requireNonNull(records, "ConsumerRecords не должен быть null.");
        if (records.isEmpty()) {
            return;
        }
        var topicRecords = recordsByTopic.computeIfAbsent(topic, t -> new ConcurrentHashMap<>());
        for (ConsumerRecord<?, ?> record : records) {
            if (record != null) {
                topicRecords.putIfAbsent(PartitionOffset.from(record), record);
            }
        }
    }

    /**
     * Возвращает неизменяемый список всех записей для указанного топика.
     *
     * @param topic Имя топика.
     * @return Неизменяемый список записей или пустой список, если записей нет.
     */
    public List<ConsumerRecord<?, ?>> getRecords(String topic) {
        requireNonBlank(topic, "Topic не может быть null или пустым.");
        var topicRecords = recordsByTopic.get(topic);
        if (topicRecords == null || topicRecords.isEmpty()) {
            return Collections.emptyList();
        }
        return List.copyOf(topicRecords.values());
    }

    /**
     * Возвращает неизменяемую карту всех записей, сгруппированных по топикам.
     *
     * @return Неизменяемая карта, где ключ - имя топика, а значение - список его записей.
     */
    public Map<String, List<ConsumerRecord<?, ?>>> getAllRecords() {
        return recordsByTopic.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entry -> List.copyOf(entry.getValue().values())));
    }

    /**
     * Очищает все записи для указанного топика.
     *
     * @param topic Имя топика.
     */
    public void clearRecords(String topic) {
        requireNonBlank(topic, "Topic не может быть null или пустым.");
        recordsByTopic.remove(topic);
    }

    /**
     * Очищает все записи из всех топиков.
     */
    public void clearAllRecords() {
        recordsByTopic.clear();
    }

    /**
     * Возвращает количество записей для указанного топика.
     *
     * @param topic Имя топика.
     * @return Количество записей.
     */
    public int getRecordCount(String topic) {
        requireNonBlank(topic, "Topic не может быть null или пустым.");
        var topicRecords = recordsByTopic.get(topic);
        return (topicRecords != null) ? topicRecords.size() : 0;
    }

    /**
     * Находит последнюю запись (с наибольшим offset) для указанного топика.
     *
     * @param topic Имя топика.
     * @return {@link Optional} с последней записью или пустой Optional, если записей нет.
     */
    public Optional<ConsumerRecord<?, ?>> getLatestRecord(String topic) {
        var topicRecords = recordsByTopic.get(topic);
        if (topicRecords == null || topicRecords.isEmpty()) {
            return Optional.empty();
        }
        return topicRecords.values().stream().max(Comparator.comparingLong(ConsumerRecord::offset));
    }

    /**
     * Возвращает неизменяемый список записей для топика, отсортированный по смещению (offset).
     *
     * @param topic Имя топика.
     * @return Отсортированный неизменяемый список записей.
     */
    public List<ConsumerRecord<?, ?>> getRecordsSortedByOffset(String topic) {
        var records = getRecords(topic);
        if (records.isEmpty()) {
            return Collections.emptyList();
        }
        return records.stream()
                .sorted(Comparator.comparingLong(ConsumerRecord::offset))
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Проверяет, есть ли записи для указанного топика.
     *
     * @param topic Имя топика.
     * @return {@code true}, если есть хотя бы одна запись.
     */
    public boolean hasRecords(String topic) {
        return getRecordCount(topic) > 0;
    }

    /**
     * Возвращает неизменяемое множество имен топиков, для которых есть записи.
     *
     * @return Неизменяемое множество имен топиков.
     */
    public Set<String> getTopicsWithRecords() {
        return recordsByTopic.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());
    }
}
