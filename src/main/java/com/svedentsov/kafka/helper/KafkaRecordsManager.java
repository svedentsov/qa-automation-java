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
 * Потокобезопасный класс для хранения и управления записями (records), полученными из Kafka.
 * Этот класс действует как in-memory хранилище для тестовых сценариев,
 * позволяя накапливать сообщения из разных топиков и предоставляя
 * API для их извлечения и анализа.
 * Уникальность записей гарантируется использованием ключа {@link PartitionOffset}.
 */
public final class KafkaRecordsManager {

    // Ключ - топик, значение - карта записей для этого топика.
    // Внутренняя карта: ключ - смещение в партиции, значение - сама запись.
    private final ConcurrentMap<String, ConcurrentMap<PartitionOffset, ConsumerRecord<?, ?>>> recordsByTopic = new ConcurrentHashMap<>();

    /**
     * Внутренний record для использования в качестве ключа в Map.
     * Гарантирует уникальность записи по паре (партиция, смещение).
     */
    private record PartitionOffset(int partition, long offset) implements Comparable<PartitionOffset> {
        public static PartitionOffset from(ConsumerRecord<?, ?> record) {
            return new PartitionOffset(record.partition(), record.offset());
        }

        @Override
        public int compareTo(PartitionOffset other) {
            int partitionCompare = Integer.compare(this.partition, other.partition);
            if (partitionCompare == 0) {
                return Long.compare(this.offset, other.offset);
            }
            return partitionCompare;
        }
    }

    /**
     * Добавляет одну запись в менеджер.
     * Если запись с такой же партицией и смещением уже существует, она не будет добавлена.
     *
     * @param topic  Имя топика.
     * @param record Запись для добавления.
     */
    public void addRecord(String topic, ConsumerRecord<?, ?> record) {
        requireNonBlank(topic, "Имя топика не может быть null или пустым.");
        requireNonNull(record, "ConsumerRecord не должен быть null.");
        var topicRecords = recordsByTopic.computeIfAbsent(topic, t -> new ConcurrentHashMap<>());
        topicRecords.putIfAbsent(PartitionOffset.from(record), record);
    }

    /**
     * Добавляет пачку записей в менеджер.
     *
     * @param topic   Имя топика.
     * @param records Пачка записей для добавления.
     */
    public void addRecords(String topic, ConsumerRecords<?, ?> records) {
        requireNonBlank(topic, "Имя топика не может быть null или пустым.");
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
     * Порядок записей в списке не гарантирован.
     *
     * @param topic Имя топика.
     * @return Неизменяемый список записей или пустой список, если записей нет.
     */
    public List<ConsumerRecord<?, ?>> getRecords(String topic) {
        requireNonBlank(topic, "Имя топика не может быть null или пустым.");
        var topicRecords = recordsByTopic.get(topic);
        if (topicRecords == null || topicRecords.isEmpty()) {
            return Collections.emptyList();
        }
        return List.copyOf(topicRecords.values());
    }

    /**
     * Возвращает все записи, отсортированные по смещению (offset).
     *
     * @param topic Имя топика.
     * @return Неизменяемый, отсортированный список записей.
     */
    public List<ConsumerRecord<?, ?>> getRecordsSortedByOffset(String topic) {
        return getRecords(topic).stream()
                .sorted(Comparator.comparingLong(ConsumerRecord::offset))
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Возвращает неизменяемую карту всех записей, сгруппированных по топикам.
     *
     * @return Неизменяемая карта, где ключ - имя топика, значение - список записей.
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
        requireNonBlank(topic, "Имя топика не может быть null или пустым.");
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
        requireNonBlank(topic, "Имя топика не может быть null или пустым.");
        var topicRecords = recordsByTopic.get(topic);
        return (topicRecords != null) ? topicRecords.size() : 0;
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
     * Возвращает неизменяемый набор имен топиков, для которых есть записи.
     *
     * @return Набор имен топиков.
     */
    public Set<String> getTopicsWithRecords() {
        return recordsByTopic.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());
    }
}
