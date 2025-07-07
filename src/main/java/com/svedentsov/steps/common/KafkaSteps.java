package com.svedentsov.steps.common;

import com.svedentsov.kafka.enums.TopicType;
import com.svedentsov.kafka.helper.KafkaExecutor;
import com.svedentsov.matcher.Condition;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;

import java.util.List;

import static com.svedentsov.kafka.helper.KafkaMatcher.key;
import static com.svedentsov.kafka.helper.KafkaMatcher.value;
import static com.svedentsov.matcher.assertions.StringAssertions.*;
import static java.util.Objects.requireNonNull;

/**
 * Класс {@code KafkaSteps} предоставляет обертку над {@link KafkaExecutor}
 * для использования в тестовых сценариях (например, в Cucumber или Allure).
 * Каждый публичный метод аннотирован {@link Step} для генерации наглядных отчетов.
 * Экземпляр этого класса должен получать уже сконфигурированный {@link KafkaExecutor} через конструктор.
 */
@Slf4j
public class KafkaSteps {

    private final KafkaExecutor kafkaExecutor;

    public KafkaSteps() {
        this.kafkaExecutor = new KafkaExecutor();
    }

    /**
     * Создает экземпляр шагов, используя предоставленный исполнитель.
     *
     * @param kafkaExecutor сконфигурированный экземпляр {@link KafkaExecutor}.
     */
    public KafkaSteps(KafkaExecutor kafkaExecutor) {
        this.kafkaExecutor = requireNonNull(kafkaExecutor, "KafkaExecutor не может быть null.");
    }

    @Step("Установить продюсер с типом контента '{producerType}'")
    public KafkaSteps setProducerType(TopicType producerType) {
        kafkaExecutor.setProducerType(producerType);
        return this;
    }

    @Step("Установить консьюмер с типом контента '{consumerType}'")
    public KafkaSteps setConsumerType(TopicType consumerType) {
        kafkaExecutor.setConsumerType(consumerType);
        return this;
    }

    @Step("Установить таймаут ожидания сообщений: '{millis}' мс")
    public KafkaSteps setTimeout(long millis) {
        kafkaExecutor.setTimeout(millis);
        return this;
    }

    @Step("Установить Avro-схему для записи")
    public KafkaSteps setAvroSchema(Schema schema) {
        kafkaExecutor.setAvroSchema(schema);
        return this;
    }

    @Step("Загрузить тело записи из источника '{source}'")
    public KafkaSteps loadRecordBody(String source) {
        kafkaExecutor.loadRecordBody(source);
        return this;
    }

    @Step("Начать прослушивание топика '{topic}'")
    public KafkaSteps startListening(String topic) {
        kafkaExecutor.setTopic(topic).startListening();
        return this;
    }

    @Step("Завершить прослушивание топика '{topic}'")
    public KafkaSteps stopListening(String topic) {
        kafkaExecutor.setTopic(topic).stopListening();
        return this;
    }

    @Step("Напечатать все полученные записи для топика '{topic}'")
    public KafkaSteps printAllRecords(String topic) {
        kafkaExecutor.printAllRecords();
        log.info("Шаг печати записей. В реальной реализации здесь был бы вызов KafkaRecordsPrinter.");
        return this;
    }

    @Step("Отправить в топик '{topic}' запись")
    public KafkaSteps sendRecord(String topic, String record) {
        kafkaExecutor.setTopic(topic)
                .setRecordBody(record)
                .sendRecord();
        return this;
    }

    @Step("Отправить в топик '{topic}' запись с ключом '{key}'")
    public KafkaSteps sendRecordWithKey(String topic, String record, String key) {
        kafkaExecutor.setTopic(topic)
                .setRecordKey(key)
                .setRecordBody(record)
                .sendRecord();
        return this;
    }

    @Step("Отправить в топик '{topic}' запись с заголовком '{headerKey}' = '{headerValue}'")
    public KafkaSteps sendRecordWithHeader(String topic, String record, String headerKey, String headerValue) {
        kafkaExecutor.setTopic(topic)
                .setRecordHeader(headerKey, headerValue)
                .setRecordBody(record)
                .sendRecord();
        return this;
    }

    @Step("Отправить в топик '{topic}' запись с заголовками")
    public KafkaSteps sendRecordWithHeaders(String topic, String record, List<Header> headers) {
        kafkaExecutor.setTopic(topic)
                .setRecordHeaders(headers)
                .setRecordBody(record)
                .sendRecord();
        return this;
    }

    @Step("ПРОВЕРКА: в топике '{topic}' все записи имеют значение, равное '{value}'")
    public KafkaSteps checkRecordsValueEquals(String topic, String value) {
        kafkaExecutor.setTopic(topic).shouldHave(value(equalTo(value)));
        return this;
    }

    @Step("ПРОВЕРКА: в топике '{topic}' все записи содержат текст '{text}'")
    public KafkaSteps checkRecordsContainText(String topic, String text) {
        kafkaExecutor.setTopic(topic).shouldHave(value(contains(text)));
        return this;
    }

    @Step("ПРОВЕРКА: в топике '{topic}' все записи содержат тексты '{texts}'")
    public KafkaSteps checkRecordsContainTexts(String topic, String... texts) {
        kafkaExecutor.setTopic(topic).shouldHave(value(containsAll(texts)));
        return this;
    }

    @Step("ПРОВЕРКА: в топике '{topic}' все записи имеют ключ, равный '{key}'")
    public KafkaSteps checkRecordsKeyEquals(String topic, String key) {
        kafkaExecutor.setTopic(topic).shouldHave(key(equalTo(key)));
        return this;
    }

    @Step("ПРОВЕРКА: в топике '{topic}' все записи имеют ключ, содержащий '{keySubstring}'")
    public KafkaSteps checkRecordsKeyContains(String topic, String keySubstring) {
        kafkaExecutor.setTopic(topic).shouldHave(key(contains(keySubstring)));
        return this;
    }

    @Step("ПОЛУЧИТЬ из топика '{topic}' все записи")
    public List<ConsumerRecord<String, String>> getAllRecords(String topic) {
        return kafkaExecutor.setTopic(topic).getAllRecords();
    }

    @Step("ПОЛУЧИТЬ из топика '{topic}' запись, соответствующую условию")
    public ConsumerRecord<String, String> getRecordByCondition(String topic, Condition condition) {
        return kafkaExecutor.setTopic(topic).getRecordByCondition(condition);
    }

    @Step("ПОЛУЧИТЬ из топика '{topic}' все записи, соответствующие условию")
    public List<ConsumerRecord<String, String>> getRecordsByCondition(String topic, Condition condition) {
        return kafkaExecutor.setTopic(topic).getRecordsByCondition(condition);
    }

    @Step("ПОЛУЧИТЬ из топика '{topic}' все записи с ключом '{key}'")
    public List<ConsumerRecord<String, String>> getRecordsByKey(String topic, String key) {
        return kafkaExecutor.setTopic(topic).getRecordsByKey(key);
    }

    @Step("ПОЛУЧИТЬ из топика '{topic}' все записи с заголовком '{headerKey}' = '{headerValue}'")
    public List<ConsumerRecord<String, String>> getRecordsByHeader(String topic, String headerKey, String headerValue) {
        return kafkaExecutor.setTopic(topic).getRecordsByHeader(headerKey, headerValue);
    }

    @Step("ПОЛУЧИТЬ из топика '{topic}' десериализованную запись типа '{tClass.simpleName}'")
    public <T> T getRecordAs(String topic, Class<T> tClass) {
        return kafkaExecutor.setTopic(topic).getRecordAs(tClass);
    }

    @Step("ПОЛУЧИТЬ из топика '{topic}' десериализованный список записей типа '{tClass.simpleName}'")
    public <T> List<T> getRecordsAsList(String topic, Class<T> tClass) {
        return kafkaExecutor.setTopic(topic).getRecordsAsList(tClass);
    }
}
