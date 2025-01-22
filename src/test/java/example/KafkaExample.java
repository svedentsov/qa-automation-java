package example;

import kafka.matcher.KafkaValidator;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Instant;
import java.util.List;

import static kafka.matcher.KafkaMatcher.*;
import static kafka.matcher.assertions.JsonPathConditions.*;
import static kafka.matcher.assertions.NumberAssertions.*;
import static kafka.matcher.assertions.RecordAssertions.*;
import static kafka.matcher.assertions.StringAssertions.equalsTo;
import static kafka.matcher.assertions.StringAssertions.*;
import static kafka.matcher.assertions.TimestampAssertions.equalsTo;
import static kafka.matcher.assertions.TimestampAssertions.inRange;
import static kafka.matcher.assertions.TimestampAssertions.*;

/**
 * Пример использования DSL для проверки Kafka записей.
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

        KafkaValidator validateRecord = new KafkaValidator(record1);
        KafkaValidator validateRecords = new KafkaValidator(records);

        // Проверки списка записей
        validateRecords.shouldHave(records(partitionsAllEqual(1))); // Это условие будет не выполнено
        validateRecords.shouldHave(records(exists()));
        validateRecords.shouldHave(records(countEqual(2)));
        validateRecords.shouldHave(records(countGreater(1)));
        validateRecords.shouldHave(records(allKeysUnique()));
        validateRecords.shouldHave(records(allValuesUnique()));
        validateRecords.shouldHave(records(recordsOrdered(ConsumerRecord::key, true)));

        // Проверки ключа записи
        validateRecord.shouldHave(key(contains("key")));
        validateRecord.shouldHave(key(isNotBlank()));
        validateRecords.shouldHave(records(keysExists("key1")));

        // Проверки значения записи
        validateRecord.shouldHave(value(equalsTo("{\"name\":\"John\",\"age\":30,\"active\":true,\"items\":[1,2,3]}")));
        validateRecord.shouldHave(value(contains("John")));
        validateRecord.shouldHave(value(containsAll("John", "30")));
        validateRecord.shouldHave(value(containsAny("John", "Doe")));
        validateRecord.shouldHave(value(startsWith("{")));
        validateRecord.shouldHave(value(endsWith("}")));
        validateRecord.shouldHave(value(matchesRegex("\\{.*\\}")));
        validateRecord.shouldHave(value(wordsOrder("John", "30")));
        validateRecord.shouldHave(value(isNotEmpty()));

        // Проверки JsonPath
        validateRecord.shouldHave(value("$.name", isString()));
        validateRecord.shouldHave(value("$.name", containsJson("Jo")));
        validateRecord.shouldHave(value("$.name", matchesRegexJson("J.*n")));
        validateRecord.shouldHave(value("$.name", contains("Jo"))); // StringCondition
        validateRecord.shouldHave(value("$.name", equalsTo("John"))); // StringCondition
        validateRecord.shouldHave(value("$.active", isBoolean()));
        validateRecord.shouldHave(value("$.age", isNumber()));
        validateRecord.shouldHave(value("$.age", numberGreater(18)));
        validateRecord.shouldHave(value("$.age", numberLess(65)));
        validateRecord.shouldHave(value("$.age", greaterThan(25), Integer.class)); // NumberCondition
        validateRecord.shouldHave(value("$.age", lessOrEqualTo(30), Integer.class)); // NumberCondition
        validateRecord.shouldHave(value("$.items", isArray()));
        validateRecord.shouldHave(value("$.items", arraySize(3)));

        // Проверки временной метки
        validateRecord.shouldHave(timestamp(before(Instant.now().plusSeconds(60))));
        validateRecord.shouldHave(timestamp(after(Instant.now().minusSeconds(60))));
        validateRecord.shouldHave(timestamp(inRange(Instant.now().minusSeconds(60), Instant.now().plusSeconds(60))));
        validateRecord.shouldHave(timestamp(equalsTo(Instant.ofEpochMilli(record1.timestamp()))));

        // Проверки партиции
        validateRecord.shouldHave(partition(equalTo(0)));
        validateRecord.shouldHave(partition(greaterOrEqualTo(0)));
        validateRecord.shouldHave(partition(lessThan(10)));

        // Проверки смещения
        validateRecord.shouldHave(offset(equalTo(0L)));
        validateRecord.shouldHave(offset(greaterThan(-1L)));
        validateRecord.shouldHave(offset(lessOrEqualTo(0L)));

        // Проверки топика
        validateRecord.shouldHave(topic(equalsTo("topic")));
        validateRecord.shouldHave(topic(startsWith("top")));

        // Составные условия
        validateRecord.shouldHave(allOf(
                value(contains("John")),
                value(contains("30"))));

        validateRecord.shouldHave(anyOf(
                key(equalsTo("key1")),
                key(equalsTo("key2"))));

        validateRecord.shouldHave(not(
                value(contains("Doe")),
                value(contains("Smith"))));

        validateRecord.shouldHave(not(
                key(equalsTo("invalidKey")),
                value(equalsTo("{}"))));

        // N из M условий
        validateRecord.shouldHave(nOf(2,
                value(contains("John")),
                value(contains("30")),
                value(contains("active"))));
    }
}
