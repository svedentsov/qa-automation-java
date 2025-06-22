package com.svedentsov.kafka.helper;

import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.svedentsov.kafka.utils.ValidationUtils.requireNonBlank;

/**
 * Потокобезопасный менеджер накопления полученных записей для тестов.
 * Хранит по topic→(partition,offset)→ConsumerRecord.
 * Требует явного вызова clearRecords(topic) или clearAllRecords() между тестами.
 */
@UtilityClass
public final class KafkaRecordsManager {

    /**
     * Хранилище записей: топик -> (partition, offset) -> ConsumerRecord
     */
    private static final ConcurrentMap<String, ConcurrentMap<PartitionOffset, ConsumerRecord<?, ?>>> RECORDS = new ConcurrentHashMap<>();

    /**
     * Добавляет одну запись для указанного топика.
     * Если запись с тем же partition+offset уже существует, она не будет добавлена.
     *
     * @param topic  название топика, не должно быть null или пустым
     * @param record запись для добавления, не null
     * @throws NullPointerException     если topic или record == null
     * @throws IllegalArgumentException если topic пустой
     */
    public static void addRecord(String topic, ConsumerRecord<?, ?> record) {
        requireNonBlank(topic, "Topic не может быть null или пустым.");
        Objects.requireNonNull(record, "record не должен быть null.");
        ConcurrentMap<PartitionOffset, ConsumerRecord<?, ?>> topicRecords =
                RECORDS.computeIfAbsent(topic, t -> new ConcurrentHashMap<>());
        PartitionOffset key = new PartitionOffset(record.partition(), record.offset());
        topicRecords.putIfAbsent(key, record);
    }

    /**
     * Добавляет множество записей для указанного топика.
     * Если запись с тем же partition+offset уже существует, она не будет добавлена.
     *
     * @param topic   название топика, не должно быть null или пустым
     * @param records записи для добавления, не null
     * @throws NullPointerException     если topic или records == null
     * @throws IllegalArgumentException если topic пустой
     */
    public static void addRecords(String topic, ConsumerRecords<?, ?> records) {
        requireNonBlank(topic, "Topic не может быть null или пустым.");
        Objects.requireNonNull(records, "records не должны быть null.");
        ConcurrentMap<PartitionOffset, ConsumerRecord<?, ?>> topicRecords =
                RECORDS.computeIfAbsent(topic, t -> new ConcurrentHashMap<>());
        for (ConsumerRecord<?, ?> record : records) {
            if (record == null) continue;
            PartitionOffset key = new PartitionOffset(record.partition(), record.offset());
            // putIfAbsent добавляет запись только если ключа нет, гарантируя уникальность
            topicRecords.putIfAbsent(key, record);
        }
    }

    /**
     * Получает список всех уникальных записей для указанного топика.
     * Возвращается копия списка; изменение её не влияет на внутреннее хранилище.
     *
     * @param topic название топика, не должно быть null или пустым
     * @return список уникальных записей, или пустой список, если записей нет
     * @throws NullPointerException     если topic == null
     * @throws IllegalArgumentException если topic пустой
     */
    public static List<ConsumerRecord<?, ?>> getRecords(String topic) {
        requireNonBlank(topic, "Topic не может быть null или пустым.");
        ConcurrentMap<PartitionOffset, ConsumerRecord<?, ?>> topicMap = RECORDS.get(topic);
        if (topicMap == null || topicMap.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(topicMap.values());
    }

    /**
     * Получает все записи для всех топиков.
     * Возвращается копия внутреннего состояния.
     *
     * @return карта: topic -> список уникальных записей; если нет записей, возвращается пустая карта или пустые списки
     */
    public static Map<String, List<ConsumerRecord<?, ?>>> getAllRecords() {
        Map<String, List<ConsumerRecord<?, ?>>> result = new HashMap<>();
        RECORDS.forEach((topic, recordsMap) -> {
            if (recordsMap == null || recordsMap.isEmpty()) {
                result.put(topic, Collections.emptyList());
            } else {
                result.put(topic, new ArrayList<>(recordsMap.values()));
            }
        });
        return result;
    }

    /**
     * Очищает все записи для всех топиков.
     * После вызова внутреннее хранилище становится пустым.
     */
    public static void clearRecords() {
        RECORDS.clear();
    }

    /**
     * Очищает записи для указанного топика.
     * Если записей нет, метод ничего не делает.
     *
     * @param topic название топика, не должно быть null или пустым
     * @throws NullPointerException     если topic == null
     * @throws IllegalArgumentException если topic пустой
     */
    public static void clearRecords(String topic) {
        requireNonBlank(topic, "Topic не может быть null или пустым.");
        RECORDS.remove(topic);
    }

    public static void clearAllRecords() {
        RECORDS.clear();
    }

    /**
     * Возвращает количество уникальных записей для заданного топика.
     *
     * @param topic название топика, не должно быть null или пустым
     * @return количество записей, или 0 если записей нет
     * @throws NullPointerException     если topic == null
     * @throws IllegalArgumentException если topic пустой
     */
    public static int getRecordCount(String topic) {
        requireNonBlank(topic, "Topic не может быть null или пустым.");
        ConcurrentMap<PartitionOffset, ConsumerRecord<?, ?>> map = RECORDS.get(topic);
        return map != null ? map.size() : 0;
    }

    /**
     * Возвращает последнюю запись (с максимальным offset) для заданного топика.
     *
     * @param topic название топика, не должно быть null или пустым
     * @return Optional с записью или Optional.empty(), если записей нет
     * @throws NullPointerException     если topic == null
     * @throws IllegalArgumentException если topic пустой
     */
    public static Optional<ConsumerRecord<?, ?>> getLatestRecord(String topic) {
        List<ConsumerRecord<?, ?>> records = getRecords(topic);
        if (records.isEmpty()) return Optional.empty();
        return records.stream().max(Comparator.comparingLong(ConsumerRecord::offset));
    }

    /**
     * Получает список записей, отсортированных по возрастанию offset.
     *
     * @param topic название топика, не должно быть null или пустым
     * @return отсортированный список, или пустой, если записей нет
     * @throws NullPointerException     если topic == null
     * @throws IllegalArgumentException если topic пустой
     */
    public static List<ConsumerRecord<?, ?>> getRecordsSortedByOffset(String topic) {
        List<ConsumerRecord<?, ?>> records = getRecords(topic);
        if (records.isEmpty()) return Collections.emptyList();
        records.sort(Comparator.comparingLong(ConsumerRecord::offset));
        return records;
    }

    /**
     * Вспомогательный класс для ключа: пара partition+offset.
     */
    private static final class PartitionOffset {
        private final int partition;
        private final long offset;

        PartitionOffset(int partition, long offset) {
            this.partition = partition;
            this.offset = offset;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PartitionOffset that)) return false;
            return partition == that.partition && offset == that.offset;
        }

        @Override
        public int hashCode() {
            return Objects.hash(partition, offset);
        }
    }
}
