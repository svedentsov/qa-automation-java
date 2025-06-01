package com.svedentsov.kafka.helper;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Утилитарный класс для печати записей, полученных из Kafka.
 * Этот класс предоставляет методы для вывода на экран всех записей из всех топиков или из конкретного топика.
 */
public class KafkaRecordsPrinter {

    /**
     * Печатает все уникальные записи для всех топиков.
     * Для каждого топика отображается его название, а затем печатаются все уникальные записи.
     */
    public static void printAllRecords() {
        Map<String, List<ConsumerRecord<?, ?>>> allRecords = KafkaRecordsManager.getAllRecords();
        allRecords.forEach((topic, recordsList) -> {
            System.out.println("Топик: " + topic);
            printRecords(recordsList);
        });
    }

    /**
     * Печатает все уникальные записи для указанного топика.
     * Если для указанного топика нет записей, выводится соответствующее сообщение.
     *
     * @param topic название топика, записи для которого нужно напечатать
     */
    public static void printAllRecords(String topic) {
        List<ConsumerRecord<?, ?>> recordsList = KafkaRecordsManager.getRecords(topic);
        if (recordsList.isEmpty()) {
            System.out.println("Нет записей для топика: " + topic);
        } else {
            System.out.println("Топик: " + topic);
            printRecords(recordsList);
        }
    }

    /**
     * Печатает записи из списка уникальных записей.
     * Для каждой записи отображаются заголовки, смещение, ключ и значение.
     *
     * @param recordsList список уникальных записей, которые нужно напечатать
     */
    private static void printRecords(List<ConsumerRecord<?, ?>> recordsList) {
        recordsList.forEach(KafkaRecordsPrinter::printRecord);
    }

    /**
     * Печатает информацию об одной записи.
     * Отображаются заголовки, смещение, ключ и значение записи.
     *
     * @param record запись, информацию о которой нужно напечатать
     */
    private static void printRecord(ConsumerRecord<?, ?> record) {
        try {
            System.out.println("Заголовки:");
            record.headers().forEach(header ->
                    System.out.println(header.key() + ": " + new String(header.value(), StandardCharsets.UTF_8))
            );
            System.out.printf("Смещение: %d, ключ: %s, значение: %s%n", record.offset(), record.key(), record.value());
        } catch (Exception e) {
            System.err.println("Ошибка при печати записи: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
