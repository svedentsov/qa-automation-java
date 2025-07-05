package com.svedentsov.kafka.helper;

import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Утилитный класс для вывода записей Kafka в консоль в читаемом формате.
 * Предназначен для отладки и анализа содержимого {@link KafkaRecordsManager}.
 */
@UtilityClass
public final class KafkaRecordsPrinter {

    private static final String SEPARATOR = "==================================================";
    private static final String SUB_SEPARATOR = "--------------------------------------------------";

    /**
     * Печатает все записи из всех топиков, содержащихся в менеджере.
     *
     * @param recordsManager Менеджер записей.
     */
    public static void printAllRecords(KafkaRecordsManager recordsManager) {
        if (recordsManager == null) {
            System.out.println("KafkaRecordsManager не предоставлен (null).");
            return;
        }
        Map<String, List<ConsumerRecord<?, ?>>> allRecords = recordsManager.getAllRecords();
        if (allRecords.isEmpty()) {
            System.out.println("В менеджере нет записей для вывода.");
            return;
        }
        allRecords.forEach((topic, recordsList) -> {
            System.out.println(SEPARATOR);
            System.out.printf("Топик: %s (найдено %d записей)%n", topic, recordsList.size());
            System.out.println(SUB_SEPARATOR);
            printRecords(recordsList);
        });
    }

    /**
     * Печатает все записи для конкретного топика из менеджера.
     *
     * @param topic          Имя топика.
     * @param recordsManager Менеджер записей.
     */
    public static void printAllRecords(String topic, KafkaRecordsManager recordsManager) {
        if (recordsManager == null) {
            System.out.printf("KafkaRecordsManager не предоставлен (null) для топика: %s%n", topic);
            return;
        }
        List<ConsumerRecord<?, ?>> recordsList = recordsManager.getRecordsSortedByOffset(topic);
        if (recordsList.isEmpty()) {
            System.out.printf("Нет записей для топика: %s%n", topic);
        } else {
            System.out.println(SEPARATOR);
            System.out.printf("Топик: %s (найдено %d записей)%n", topic, recordsList.size());
            System.out.println(SUB_SEPARATOR);
            printRecords(recordsList);
        }
    }

    /**
     * Печатает содержимое списка записей.
     *
     * @param recordsList Список записей для печати.
     */
    public static void printRecords(List<ConsumerRecord<?, ?>> recordsList) {
        if (recordsList == null || recordsList.isEmpty()) {
            System.out.println("Список записей пуст или не предоставлен.");
            return;
        }
        for (int i = 0; i < recordsList.size(); i++) {
            System.out.printf("--- Запись #%d ---\n", i + 1);
            printRecord(recordsList.get(i));
        }
    }

    /**
     * Печатает детальную информацию об одной записи.
     *
     * @param record Запись для печати.
     */
    public static void printRecord(ConsumerRecord<?, ?> record) {
        if (record == null) {
            System.out.println("  Запись: null");
            return;
        }
        try {
            System.out.printf("  Партиция: %d%n", record.partition());
            System.out.printf("  Смещение: %d%n", record.offset());
            System.out.printf("  Ключ: %s%n", record.key());
            System.out.printf("  Значение: %s%n", record.value());
            if (record.headers() != null) {
                System.out.println("  Заголовки:");
                boolean hasHeaders = false;
                for (Header header : record.headers()) {
                    hasHeaders = true;
                    System.out.printf("    - %s: %s%n", header.key(), new String(header.value(), StandardCharsets.UTF_8));
                }
                if (!hasHeaders) {
                    System.out.println("    (пусто)");
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при печати записи: " + e.getMessage());
        }
    }
}
