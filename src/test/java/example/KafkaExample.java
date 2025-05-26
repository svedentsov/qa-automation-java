package example;

import kafka.matcher.KafkaValidator;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Instant;
import java.util.List;

import static core.matcher.assertions.BooleanAssertions.isTrue;
import static core.matcher.assertions.CompositeAssertions.*;
import static core.matcher.assertions.InstantAssertions.before;
import static core.matcher.assertions.ListAssertions.*;
import static core.matcher.assertions.NumberAssertions.equalTo;
import static core.matcher.assertions.NumberAssertions.greaterThan;
import static core.matcher.assertions.StringAssertions.*;
import static kafka.matcher.KafkaMatcher.*;

/**
 * Пример класса, демонстрирующего использование валидатора и всех доступных матчеров для Apache Kafka.
 */
public class KafkaExample {

    // Валидация списка записей Kafka с применением различных проверок списка.
    public void validateRecords(List<ConsumerRecord<String, String>> records) {
        KafkaValidator.forRecords(records).shouldHaveList(
                isNotEmpty(), // список не пуст
                countGreaterThan(1), // больше одной записи
                countLessThan(100), // меньше 100 записей
                hasSizeBetween(1, 10)); // размер списка от 1 до 10 включительно
    }

    // Валидация отдельной записи Kafka с применением проверок ключа, значения, партиции, смещения, заголовков и временной метки.
    public void validateRecord(ConsumerRecord<String, String> record) {
        KafkaValidator.forRecords(record).shouldHave(
                key(startsWith("key")),// ключ начинается с "key"
                key(isNotBlank()), // ключ не пуст и не только пробелы
                topic(equalToStr("topic")), // название топика == "topic"
                partition(equalTo(0)),// партиция == 0
                offset(greaterThan(0L)), // смещение > 0
                timestamp(before(Instant.now().plusSeconds(60))), // временная метка не позже, чем через 60 секунд
                value(contains("\"name\":\"John\""))); // value содержит JSON-поле "name":"John"
    }

    // Пример составных проверок (AND, OR, NOT, nOf) для одной записи Kafka.
    public void validateCompositeConditions(ConsumerRecord<String, String> record) {
        KafkaValidator.forRecords(record).shouldHave(
                and( // все условия должны быть верны
                        topic(equalToStr("topic")), // — топик == "topic"
                        key(contains("key1")) // — ключ содержит "key1"
                ),
                or( // одно из условий должно быть верно
                        partition(equalTo(0)), // — партиция == 0
                        partition(equalTo(1)) // — или партиция == 1
                ),
                not( // ни одно из условий не должно сработать
                        value(contains("error")) // — значение не должно содержать "error"
                ),
                nOf(2, // из трёх условий должны выполниться любые два
                        key(isNotBlank()), // ключ не пуст и не только пробелы
                        value(contains("John")), // значение содержит "John"
                        timestamp(before(Instant.now().plusSeconds(120))))); // временная метка не позже, чем через 120 секунд
    }

    public void validateJsonStringField(ConsumerRecord<String, String> record) {
        KafkaValidator.forRecords(record).shouldHave(
                value("$.user.name", equalToStr("Alice")), // из JSON-path "$.user.name" извлечена строка "Alice"
                value("$.metrics.count", greaterThan(100), Integer.class), // извлечено число по "$.metrics.count" и проверено > 100
                value("$.flags.active", isTrue()), // извлечено булево по "$.flags.active" и проверка true
                value("$.users[*].id", equalTo(1), Integer.class)); // пример одного элемента; для всей коллекции нужны ListAssertions
    }
}
