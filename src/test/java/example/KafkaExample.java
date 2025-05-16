package example;

import kafka.matcher.KafkaValidator;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Instant;
import java.util.List;

import static kafka.matcher.KafkaMatcher.*;
import static kafka.matcher.assertions.BooleanAssertions.isBoolean;
import static kafka.matcher.assertions.BooleanAssertions.isTrue;
import static kafka.matcher.assertions.CompositeAssertions.and;
import static kafka.matcher.assertions.InstantAssertions.before;
import static kafka.matcher.assertions.NumberAssertions.greaterThan;
import static kafka.matcher.assertions.NumberAssertions.lessOrEqualTo;
import static kafka.matcher.assertions.StringAssertions.*;

/**
 * Пример использования DSL для проверки записей Apache Kafka.
 * Демонстрируются различные проверки полей записей, включая проверки JSON-полей, временных меток,
 * партиций, смещений и составных условий.
 */
public class KafkaExample {

    public static void main(String[] args) {
        ConsumerRecord<String, String> record1 = new ConsumerRecord<>(
                "topic", 0, 0L, "key1", "{\"name\":\"John\",\"age\":30,\"active\":true,\"items\":[1,2,3]}");
        ConsumerRecord<String, String> record2 = new ConsumerRecord<>(
                "topic", 1, 1L, "key2", "{\"name\":\"Jane\",\"age\":25,\"active\":false,\"items\":[4,5,6]}");

        record1.headers().add("headerKey", "headerValue".getBytes());
        record2.headers().add("headerKey", "headerValue2".getBytes());

        List<ConsumerRecord<String, String>> records = List.of(record1, record2);

        KafkaValidator<ConsumerRecord<String, String>> validateRecord = new KafkaValidator<>(record1);
        KafkaValidator<ConsumerRecord<String, String>> validateRecords = new KafkaValidator<>(records);

        validateRecords.shouldHave(
                key(contains("key")));

        // Проверки ключа записи
        validateRecord.shouldHave(
                key(contains("key")),
                key(isNotBlank()),
                key(isNotNull()));

        // Пример составных условий можно оставить без изменений, если они применяются к отдельной записи
        validateRecord.shouldHave(and(
                value(contains("John")),
                value(contains("30"))
        ));

        // Примеры условий по JSONPath и времени остаются без изменений
        validateRecord.shouldHave(
                timestamp(before(Instant.now().plusSeconds(60))),
                value("$.name", isString()),
                value("$.name", equalsTo("John")),
                value("$.active", isBoolean()),
                value("$.active", isTrue()),
                value("$.age", greaterThan(18), Integer.class),
                value("$.age", lessOrEqualTo(30), Integer.class));
    }
}
