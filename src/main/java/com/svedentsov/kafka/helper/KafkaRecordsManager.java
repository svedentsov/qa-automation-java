package com.svedentsov.kafka.helper;

import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

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
        validateTopicAndRecord(topic, record);
        var topicRecords = RECORDS.computeIfAbsent(topic, t -> new ConcurrentHashMap<>());
        var key = PartitionOffset.of(record.partition(), record.offset());
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
        validateTopicAndRecords(topic, records);
        var topicRecords = RECORDS.computeIfAbsent(topic, t -> new ConcurrentHashMap<>());
        records.forEach(record -> {
            if (record != null) {
                var key = PartitionOffset.of(record.partition(), record.offset());
                topicRecords.putIfAbsent(key, record);
            }
        });
    }

    /**
     * Получает неизменяемый список всех уникальных записей для указанного топика.
     *
     * @param topic название топика, не должно быть null или пустым
     * @return неизменяемый список уникальных записей, или пустой список, если записей нет
     * @throws NullPointerException     если topic == null
     * @throws IllegalArgumentException если topic пустой
     */
    public static List<ConsumerRecord<?, ?>> getRecords(String topic) {
        requireNonBlank(topic, "Topic не может быть null или пустым.");
        return Optional.ofNullable(RECORDS.get(topic))
                .filter(map -> !map.isEmpty())
                .map(map -> List.copyOf(map.values()))
                .orElse(List.of());
    }

    /**
     * Получает все записи для всех топиков.
     * Возвращается неизменяемая копия внутреннего состояния.
     *
     * @return неизменяемая карта: topic -> список уникальных записей
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

    /**
     * Очищает все записи для всех топиков.
     * После вызова внутреннее хранилище становится пустым.
     */
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
        return Optional.ofNullable(RECORDS.get(topic))
                .map(ConcurrentMap::size)
                .orElse(0);
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
        return getRecords(topic).stream()
                .max(Comparator.comparingLong(ConsumerRecord::offset));
    }

    /**
     * Получает неизменяемый список записей, отсортированных по возрастанию offset.
     *
     * @param topic название топика, не должно быть null или пустым
     * @return неизменяемый отсортированный список, или пустой, если записей нет
     * @throws NullPointerException     если topic == null
     * @throws IllegalArgumentException если topic пустой
     */
    public static List<ConsumerRecord<?, ?>> getRecordsSortedByOffset(String topic) {
        var records = getRecords(topic);
        if (records.isEmpty()) return List.of();
        return records.stream()
                .sorted(Comparator.comparingLong(ConsumerRecord::offset))
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Проверяет, есть ли записи для указанного топика.
     *
     * @param topic название топика, не должно быть null или пустым
     * @return true, если есть записи для топика
     * @throws NullPointerException     если topic == null
     * @throws IllegalArgumentException если topic пустой
     */
    public static boolean hasRecords(String topic) {
        return getRecordCount(topic) > 0;
    }

    /**
     * Получает список всех топиков, для которых есть записи.
     *
     * @return неизменяемый список названий топиков
     */
    public static Set<String> getTopicsWithRecords() {
        return RECORDS.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());
    }

    private static void validateTopicAndRecord(String topic, ConsumerRecord<?, ?> record) {
        requireNonBlank(topic, "Topic не может быть null или пустым.");
        Objects.requireNonNull(record, "record не должен быть null.");
    }

    private static void validateTopicAndRecords(String topic, ConsumerRecords<?, ?> records) {
        requireNonBlank(topic, "Topic не может быть null или пустым.");
        Objects.requireNonNull(records, "records не должны быть null.");
    }

    /**
     * Вспомогательный record для ключа: пара partition+offset.
     */
    private record PartitionOffset(int partition, long offset) {
        public static PartitionOffset of(int partition, long offset) {
            return new PartitionOffset(partition, offset);
        }
    }
}
