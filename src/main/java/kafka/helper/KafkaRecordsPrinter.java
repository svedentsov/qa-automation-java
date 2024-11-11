package kafka.helper;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Утилитарный класс для печати записей, полученных из Kafka.
 * Этот класс предоставляет методы для вывода на экран всех записей из всех топиков или из конкретного топика.
 */
@Slf4j
public class KafkaRecordsPrinter {

    /**
     * Печатает все записи для всех топиков.
     * Для каждого топика отображается его название, а затем печатаются все записи.
     */
    public static void printAllRecords() {
        KafkaRecordsManager.getAllRecords().forEach((topic, recordsList) -> {
            System.out.println("Топик: " + topic);
            printRecords(recordsList);
        });
    }

    /**
     * Печатает все записи для указанного топика.
     * Если для указанного топика нет записей, выводится соответствующее сообщение.
     *
     * @param topic название топика, записи для которого нужно напечатать
     */
    public static void printAllRecords(String topic) {
        List<ConsumerRecords<?, ?>> recordsList = KafkaRecordsManager.getRecords(topic);
        if (recordsList.isEmpty()) {
            System.out.println("Нет записей для топика: " + topic);
        } else {
            System.out.println("Топик: " + topic);
            printRecords(recordsList);
        }
    }

    /**
     * Печатает записи из списка записей.
     * Для каждой записи отображаются заголовки, смещение, ключ и значение.
     *
     * @param recordsList список записей, которые нужно напечатать
     */
    private static void printRecords(List<ConsumerRecords<?, ?>> recordsList) {
        for (ConsumerRecords<?, ?> records : recordsList) {
            records.forEach(KafkaRecordsPrinter::printRecord);
        }
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
            System.out.printf("Смещение: '%d', ключ: %s, значение: %s%n", record.offset(), record.key(), record.value());
        } catch (Exception e) {
            System.err.println("Ошибка при печати записи: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
