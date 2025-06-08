package example;

import com.svedentsov.matcher.EntityValidator;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Instant;
import java.util.List;

import static com.svedentsov.kafka.helper.KafkaMatcher.*;
import static com.svedentsov.matcher.assertions.BooleanAssertions.isTrue;
import static com.svedentsov.matcher.assertions.CompositeAssertions.*;
import static com.svedentsov.matcher.assertions.InstantAssertions.instantBefore;
import static com.svedentsov.matcher.assertions.ListAssertions.*;
import static com.svedentsov.matcher.assertions.NumberAssertions.numberEqualTo;
import static com.svedentsov.matcher.assertions.NumberAssertions.numberGreaterThan;
import static com.svedentsov.matcher.assertions.StringAssertions.*;

public class KafkaExample {

    /**
     * Валидация списка записей Kafka с применением различных проверок списка.
     */
    public void validateRecords(List<ConsumerRecord<String, String>> records) {
        EntityValidator.of(records).shouldHaveList(
                listIsNotEmpty(), // список не пуст
                listCountGreaterThan(1), // больше одной записи
                listCountLessThan(100), // меньше 100 записей
                listHasSizeBetween(1, 10)); // размер списка от 1 до 10 включительно
    }

    /**
     * Валидация отдельной записи Kafka с применением проверок ключа, значения, партиции, смещения, заголовков и временной метки.
     */
    public void validateRecord(ConsumerRecord<String, String> record) {
        EntityValidator.of(record).shouldHave(
                key(startsWith("key")),// ключ начинается с "key"
                key(isNotBlank()), // ключ не пуст и не только пробелы
                topic(equalTo("topic")), // название топика == "topic"
                partition(numberEqualTo(0)),// партиция == 0
                offset(numberGreaterThan(0L)), // смещение > 0
                timestamp(instantBefore(Instant.now().plusSeconds(60))), // временная метка не позже, чем через 60 секунд
                value(contains("\"name\":\"John\""))); // value содержит JSON-поле "name":"John"
    }

    /**
     * Составная проверка (AND, OR, NOT, nOf) для одной записи.
     */
    public void validateCompositeConditions(ConsumerRecord<String, String> record) {
        EntityValidator.of(record).shouldHave(
                and( // все условия должны быть верны
                        topic(equalTo("topic")), // топик == "topic"
                        key(contains("key1"))), // ключ содержит "key1"
                or( // одно из условий должно быть верно
                        partition(numberEqualTo(0)), // партиция == 0
                        partition(numberEqualTo(1))), // или партиция == 1
                not( // ни одно из условий не должно сработать
                        value(contains("error"))), // значение не должно содержать "error"
                nOf(2, // из трёх условий должны выполниться любые два
                        key(isNotBlank()), // ключ не пуст и не только пробелы
                        value(contains("John")), // значение содержит "John"
                        timestamp(instantBefore(Instant.now().plusSeconds(120))))); // временная метка не позже, чем через 120 секунд
    }

    /**
     * Проверка JSON-поля в строковом формате Kafka сообщения.
     */
    public void validateJsonStringField(ConsumerRecord<String, String> record) {
        EntityValidator.of(record).shouldHave(
                value("$.user.name", equalTo("Alice")), // из JSON-path "$.user.name" извлечена строка "Alice"
                value("$.metrics.count", numberGreaterThan(100), Integer.class), // извлечено число по "$.metrics.count" и проверено > 100
                value("$.flags.active", isTrue()), // извлечено булево по "$.flags.active" и проверка true
                value("$.users[*].id", numberEqualTo(1), Integer.class)); // пример одного элемента; для всей коллекции нужны ListAssertions
    }
}
