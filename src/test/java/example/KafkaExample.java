package example;

import kafka.matcher.KafkaValidator;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Instant;
import java.util.List;

import static kafka.matcher.KafkaMatcher.*;

/**
 * Пример использования всех матчеров из KafkaMatcher.
 */
public class KafkaExample {

    public static void main(String[] args) {
        // Создаем пример записи Kafka
        ConsumerRecord<String, String> record1 = new ConsumerRecord<>(
                "topic", 0, 0L, "key1", "{\"name\":\"John\",\"age\":30,\"active\":true}");

        ConsumerRecord<String, String> record2 = new ConsumerRecord<>(
                "topic", 0, 1L, "key2", "{\"name\":\"Jane\",\"age\":25,\"active\":false}");

        // Добавляем заголовки (headers)
        record1.headers().add("headerKey", "headerValue".getBytes());
        record2.headers().add("headerKey", "headerValue2".getBytes());

        // Создаем список записей
        List<ConsumerRecord<String, String>> records = List.of(record1, record2);

        // Создаем экземпляр KafkaValidator для одной записи
        KafkaValidator validateRecord = new KafkaValidator(record1);

        // Создаем экземпляр KafkaValidator для списка записей
        KafkaValidator validateRecords = new KafkaValidator(records);

        // ------------------- Record-level Conditions -------------------

        // Проверяем наличие записей
        validateRecords.shouldHave(recordsExists());

        // Проверяем, что количество записей равно 2
        validateRecords.shouldHave(recordsCountEqual(2));

        // Проверяем, что количество записей больше 1
        validateRecords.shouldHave(recordsCountGreater(1));

        // Проверяем, что все ключи уникальны
        validateRecords.shouldHave(allKeysUnique());

        // Проверяем, что все значения уникальны
        validateRecords.shouldHave(allValuesUnique());

        // Проверяем, что записи упорядочены по ключу в порядке возрастания
        validateRecords.shouldHave(recordsOrdered(ConsumerRecord::key, true));

        // ------------------- Key Conditions -------------------

        // Проверяем, что ключ равен "key1"
        validateRecord.shouldHave(keyEquals("key1"));

        // Проверяем, что ключ содержит "key"
        validateRecord.shouldHave(keyContains("key"));

        // Проверяем наличие записи с ключом "key1" в списке записей
        validateRecords.shouldHave(keysExists("key1"));

        // ------------------- Value Conditions -------------------

        // Проверяем, что значение равно заданному JSON
        validateRecord.shouldHave(valueEquals("{\"name\":\"John\",\"age\":30,\"active\":true}"));

        // Проверяем, что значение содержит "John"
        validateRecord.shouldHave(valueContains("John"));

        // Проверяем, что значение содержит все указанные тексты
        validateRecord.shouldHave(valueContains(List.of("John", "30")));

        // Проверяем, что значение содержит хотя бы один из указанных текстов
        validateRecord.shouldHave(valueContainsAny(List.of("John", "Doe")));

        // Проверяем, что значение начинается с "{"
        validateRecord.shouldHave(valueStartsWith("{"));

        // Проверяем, что значение заканчивается "}"
        validateRecord.shouldHave(valueEndsWith("}"));

        // Проверяем, что значение соответствует регулярному выражению
        validateRecord.shouldHave(valueMatchesRegex("\\{.*\\}"));

        // Проверяем порядок слов в значении
        validateRecord.shouldHave(valueWordsOrder(List.of("John", "30")));

        // Проверяем, что значение является валидным JSON
        validateRecord.shouldHave(valueIsValidJson());

        // Проверяем, что JSON содержит ключи "name" и "age"
        validateRecord.shouldHave(valueJsonContainsKeys(List.of("name", "age")));

        // ------------------- Value JSONPath Conditions -------------------

        // Проверяем, что значение по JSONPath равняется заданному
        validateRecord.shouldHave(valueJsonPathEquals("$.name", "John"));

        // Проверяем, что значение по JSONPath содержит указанный текст
        validateRecord.shouldHave(valueJsonPathContains("$.name", "Jo"));

        // Проверяем, что значение по JSONPath соответствует регулярному выражению
        validateRecord.shouldHave(valueJsonPathMatchesRegex("$.name", "J.*n"));

        // Проверяем, что значение по JSONPath является строкой
        validateRecord.shouldHave(valueJsonPathIsString("$.name"));

        // Проверяем, что значение по JSONPath является числом
        validateRecord.shouldHave(valueJsonPathIsNumber("$.age"));

        // Проверяем, что значение по JSONPath является булевым
        validateRecord.shouldHave(valueJsonPathIsBoolean("$.active"));

        // Проверяем, что числовое значение по JSONPath больше заданного
        validateRecord.shouldHave(valueJsonPathNumberGreater("$.age", 18));

        // Проверяем, что числовое значение по JSONPath меньше заданного
        validateRecord.shouldHave(valueJsonPathNumberLess("$.age", 65));

        // Проверяем, что значение по JSONPath является массивом
        validateRecord.shouldHave(valueJsonPathIsArray("$.items"));

        // Проверяем, что размер массива по JSONPath равен заданному значению
        validateRecord.shouldHave(valueJsonPathArraySize("$.items", 3));

        // ------------------- Header Conditions -------------------

        // Проверяем, что ключ заголовка существует
        validateRecord.shouldHave(headerKeyExists("headerKey"));

        // Проверяем, что ключ заголовка равен заданному значению
        validateRecord.shouldHave(headerKeyEquals("headerKey"));

        // Проверяем, что ключ заголовка содержит указанный текст
        validateRecord.shouldHave(headerKeyContains("header"));

        // Проверяем, что значение заголовка равно заданному
        validateRecord.shouldHave(headerValueEquals("headerKey", "headerValue"));

        // Проверяем, что значение заголовка содержит указанный текст
        validateRecord.shouldHave(headerValueContains("headerKey", "headerValue"));

        // Проверяем наличие записи с указанным заголовком и значением в списке записей
        validateRecords.shouldHave(headersExists("headerKey", "headerValue"));

        // ------------------- Timestamp Conditions -------------------

        // Проверяем, что временная метка записи раньше текущего времени + 60 секунд
        validateRecord.shouldHave(timestampBefore(Instant.now().plusSeconds(60)));

        // Проверяем, что временная метка записи позже текущего времени - 60 секунд
        validateRecord.shouldHave(timestampAfter(Instant.now().minusSeconds(60)));

        // Проверяем, что временная метка записи в заданном диапазоне
        validateRecord.shouldHave(timestampInRange(
                Instant.now().minusSeconds(60),
                Instant.now().plusSeconds(60)));

        // ------------------- Partition Conditions -------------------

        // Проверяем, что запись принадлежит разделу 0
        validateRecord.shouldHave(partitionEquals(0));

        // ------------------- Offset Conditions -------------------

        // Проверяем, что запись имеет смещение 0
        validateRecord.shouldHave(offsetEquals(0L));

        // ------------------- Topic Conditions -------------------

        // Проверяем, что запись принадлежит топику "topic"
        validateRecord.shouldHave(topicEquals("topic"));

        // ------------------- Composite Conditions -------------------

        // Проверяем, что значение содержит "John" и "30"
        validateRecord.shouldHave(allOf(
                valueContains("John"),
                valueContains("30")));

        // Проверяем, что ключ равен "key1" или "key2"
        validateRecord.shouldHave(anyOf(
                keyEquals("key1"),
                keyEquals("key2")));

        // Проверяем, что значение НЕ содержит "Doe" и НЕ содержит "Smith"
        validateRecord.shouldHave(not(
                valueContains("Doe"),
                valueContains("Smith")));

        // Проверяем, что ключ НЕ равен "invalidKey" и значение НЕ равно "{}"
        validateRecord.shouldHave(not(
                keyEquals("invalidKey"),
                valueEquals("{}")));

        // ------------------- N of M Conditions -------------------

        // Проверяем, что хотя бы 2 из 3 условий выполняются
        validateRecord.shouldHave(nOf(2,
                valueContains("John"),
                valueContains("30"),
                valueContains("active")));
    }
}
