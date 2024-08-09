package kafka.helper;

import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Менеджер для хранения и управления записями, полученными из Kafka.
 * Этот класс предоставляет методы для добавления, получения и очистки записей для разных топиков.
 */
public class KafkaRecordsManager {

    private static final Map<String, List<ConsumerRecords<?, ?>>> RECORDS = new ConcurrentHashMap<>();

    /**
     * Добавляет записи для указанного топика.
     * Если записи для данного топика отсутствуют, они создаются.
     *
     * @param topic   название топика, для которого добавляются записи
     * @param records записи, которые необходимо добавить
     * @throws NullPointerException если {@code records} равен {@code null}
     */
    public static void addRecords(String topic, ConsumerRecords<?, ?> records) {
        Objects.requireNonNull(records, "records не должны быть null");
        RECORDS.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(records);
    }

    /**
     * Получает список всех записей для указанного топика.
     * Если для указанного топика записей нет, возвращается пустой список.
     *
     * @param topic название топика, записи для которого нужно получить
     * @return список записей для указанного топика (или пустой список, если записей нет)
     */
    public static List<ConsumerRecords<?, ?>> getRecords(String topic) {
        return new ArrayList<>(RECORDS.getOrDefault(topic, Collections.emptyList()));
    }

    /**
     * Получает все записи для всех топиков.
     * Возвращает копию карты, где ключи — это названия топиков, а значения — списки записей.
     *
     * @return карта всех записей для всех топиков
     */
    public static Map<String, List<ConsumerRecords<?, ?>>> getAllRecords() {
        Map<String, List<ConsumerRecords<?, ?>>> result = new HashMap<>();
        RECORDS.forEach((key, value) -> result.put(key, new ArrayList<>(value)));
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
}
