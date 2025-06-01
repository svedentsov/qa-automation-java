package com.svedentsov.kafka.helper;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Менеджер для хранения и управления записями, полученными из Kafka.
 * Обеспечивает хранение уникальных записей для каждого топика.
 */
public class KafkaRecordsManager {

    /**
     * Структура для хранения уникальных записей по топикам.
     * Ключ: название топика
     * Значение: Map, где ключ - уникальная пара (partition, offset), значение - ConsumerRecord.
     * Это гарантирует, что в рамках одного топика не будет дубликатов по сочетанию partition и offset.
     */
    private static final Map<String, Map<PartitionOffset, ConsumerRecord<?, ?>>> RECORDS = new ConcurrentHashMap<>();

    /**
     * Добавляет записи для указанного топика.
     * Каждая запись идентифицируется парой partition-offset.
     * Если такая запись уже существует, она не будет добавлена повторно.
     *
     * @param topic   название топика, для которого добавляются записи
     * @param records записи, которые необходимо добавить
     * @throws NullPointerException если {@code records} равен {@code null}
     */
    public static void addRecords(String topic, ConsumerRecords<?, ?> records) {
        Objects.requireNonNull(records, "records не должны быть null");
        RECORDS.computeIfAbsent(topic, k -> new ConcurrentHashMap<>());
        Map<PartitionOffset, ConsumerRecord<?, ?>> topicRecords = RECORDS.get(topic);

        for (ConsumerRecord<?, ?> record : records) {
            PartitionOffset key = new PartitionOffset(record.partition(), record.offset());
            // putIfAbsent добавляет запись только если ключа нет, гарантируя уникальность
            topicRecords.putIfAbsent(key, record);
        }
    }

    /**
     * Получает список всех уникальных записей для указанного топика.
     * Если для указанного топика записей нет, возвращается пустой список.
     *
     * @param topic название топика, записи для которого нужно получить
     * @return список уникальных записей для указанного топика
     */
    public static List<ConsumerRecord<?, ?>> getRecords(String topic) {
        return new ArrayList<>(RECORDS.getOrDefault(topic, Collections.emptyMap()).values());
    }

    /**
     * Получает все записи для всех топиков в формате Map<topic, список уникальных записей>.
     *
     * @return карта всех уникальных записей для всех топиков
     */
    public static Map<String, List<ConsumerRecord<?, ?>>> getAllRecords() {
        Map<String, List<ConsumerRecord<?, ?>>> result = new HashMap<>();
        RECORDS.forEach((topic, recordsMap) -> result.put(topic, new ArrayList<>(recordsMap.values())));
        return result;
    }

    /**
     * Очищает все записи для всех топиков.
     * После вызова этого метода все данные будут удалены.
     */
    public static void clearRecords() {
        RECORDS.clear();
    }

    /**
     * Очищает записи для указанного топика.
     * Если записи для данного топика отсутствуют, метод не выполняет никаких действий.
     *
     * @param topic название топика, записи для которого нужно очистить
     */
    public static void clearRecords(String topic) {
        RECORDS.remove(topic);
    }

    /**
     * Вспомогательный класс для идентификации записи по partition и offset.
     */
    private static class PartitionOffset {
        private final int partition;
        private final long offset;

        PartitionOffset(int partition, long offset) {
            this.partition = partition;
            this.offset = offset;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PartitionOffset)) return false;
            PartitionOffset that = (PartitionOffset) o;
            return partition == that.partition && offset == that.offset;
        }

        @Override
        public int hashCode() {
            return Objects.hash(partition, offset);
        }
    }
}
