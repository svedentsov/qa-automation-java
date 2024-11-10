package kafka.steps;

import io.qameta.allure.Step;
import kafka.enums.ConsumerType;
import kafka.enums.ProducerType;
import kafka.helper.KafkaExecutor;
import kafka.matcher.KafkaValidator;
import kafka.matcher.condition.Condition;
import kafka.matcher.condition.Conditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;

import java.util.List;

import static kafka.matcher.KafkaMatcher.*;

/**
 * Класс {@code KafkaSteps} предоставляет методы для взаимодействия с Kafka,
 * включая отправку записей и проверку их состояния в топиках.
 */
@Slf4j
public class KafkaSteps {

    private final KafkaExecutor kafkaExecutor = new KafkaExecutor();

    @Step("Установить продюсер с типом '{producerType}'")
    public KafkaSteps setProducerType(ProducerType producerType) {
        kafkaExecutor.setProducerType(producerType);
        return this;
    }

    @Step("Установить консюмер с типом '{consumerType}'")
    public KafkaSteps setConsumerType(ConsumerType consumerType) {
        kafkaExecutor.setConsumerType(consumerType);
        return this;
    }

    @Step("Установить таймаут '{millis}' миллисекунд")
    public KafkaSteps setTimeout(long millis) {
        kafkaExecutor.setTimeout(millis);
        return this;
    }

    @Step("Установить Avro-схему")
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

    @Step("Печать всех записей из топика '{topic}'")
    public KafkaSteps printAllRecords(String topic) {
        kafkaExecutor.printAllRecords(topic);
        return this;
    }

    @Step("Отправить в топик '{topic}' запись")
    public KafkaSteps sendRecord(String topic, String record) {
        kafkaExecutor.setTopic(topic).setRecordBody(record).sendRecord();
        return this;
    }

    @Step("Отправить в топик '{topic}' запись с ключом '{key}'")
    public KafkaSteps sendRecordWithKey(String topic, String record, String key) {
        kafkaExecutor.setTopic(topic).setRecordKey(key).setRecordBody(record).sendRecord();
        return this;
    }

    @Step("Отправить в топик '{topic}' запись с заголовком '{headerKey}' со значением '{headerValue}'")
    public KafkaSteps sendRecordWithHeader(String topic, String record, String headerKey, String headerValue) {
        kafkaExecutor.setTopic(topic).setRecordHeader(headerKey, headerValue).setRecordBody(record).sendRecord();
        return this;
    }

    @Step("Отправить в топик '{topic}' запись с заголовками")
    public KafkaSteps sendRecordWithHeaders(String topic, String record, List<Header> headers) {
        kafkaExecutor.setTopic(topic).setRecordHeaders(headers).setRecordBody(record).sendRecord();
        return this;
    }

    @Step("Проверить в топике '{topic}', что есть записи")
    public KafkaSteps checkRecordsPresence(String topic) {
        kafkaExecutor.setTopic(topic).receiveRecords().shouldHave(recordsExists());
        return this;
    }

    @Step("Проверить в топике '{topic}', что число записей равно '{count}'")
    public KafkaSteps checkRecordsCountEquals(String topic, int count) {
        kafkaExecutor.setTopic(topic).receiveRecords().shouldHave(recordsCountEqual(count));
        return this;
    }

    @Step("Проверить в топике '{topic}', что число записей больше '{count}'")
    public KafkaSteps checkRecordsCountGreaterThan(String topic, int count) {
        kafkaExecutor.setTopic(topic).receiveRecords().shouldHave(recordsCountGreater(count));
        return this;
    }

    @Step("Проверить в топике '{topic}', что есть запись с ключом '{key}'")
    public KafkaSteps checkRecordPresenceByKey(String topic, String key) {
        kafkaExecutor.setTopic(topic).receiveRecords().shouldHave(keysExists(key));
        return this;
    }

    @Step("Проверить в топике '{topic}', что все записи имеют текст '{value}'")
    public KafkaSteps checkRecordsValueEquals(String topic, String value) {
        kafkaExecutor.setTopic(topic).receiveRecords().shouldHave(valueEquals(value));
        return this;
    }

    @Step("Проверить в топике '{topic}', что все записи содержат текст '{text}'")
    public KafkaSteps checkRecordsContainText(String topic, String text) {
        kafkaExecutor.setTopic(topic).receiveRecords().shouldHave(valueContains(text));
        return this;
    }

    @Step("Проверить в топике '{topic}', что все записи содержат текста '{texts}'")
    public KafkaSteps checkRecordsContainTexts(String topic, List<String> texts) {
        kafkaExecutor.setTopic(topic).receiveRecords().shouldHave(valueContains(texts));
        return this;
    }

    @Step("Проверить в топике '{topic}', что все записи имеют ключ '{key}'")
    public KafkaSteps checkRecordsKeyEquals(String topic, String key) {
        kafkaExecutor.setTopic(topic).receiveRecords().shouldHave(keyEquals(key));
        return this;
    }

    @Step("Проверить в топике '{topic}', что все записи имеют ключ, содержащий текст '{keySubstring}'")
    public KafkaSteps checkRecordsKeyContains(String topic, String keySubstring) {
        kafkaExecutor.setTopic(topic).receiveRecords().shouldHave(keyContains(keySubstring));
        return this;
    }

    @Step("Проверить в топике '{topic}', что все записи имеют заголовок '{headerKey}' со значением '{headerValue}'")
    public KafkaSteps checkRecordsByHeader(String topic, String headerKey, String headerValue) {
        kafkaExecutor.setTopic(topic).receiveRecords().shouldHave(headerEquals(headerKey, headerValue));
        return this;
    }

    @Step("Проверить в топике '{topic}', что все записи имеют заголовок '{headerKey}' содержащий значение '{headerValue}'")
    public KafkaSteps checkRecordsHeaderContains(String topic, String headerKey, String headerValue) {
        kafkaExecutor.setTopic(topic).receiveRecords().shouldHave(headerContains(headerKey, headerValue));
        return this;
    }

    @Step("Проверить в топике '{topic}', что все записи соответствуют условию '{condition}'")
    public KafkaSteps shouldHave(String topic, Conditions condition) {
        kafkaExecutor.setTopic(topic).receiveRecords().shouldHave(condition);
        return this;
    }

    @Step("Проверить запись на соответствие условиям")
    public KafkaValidator validateRecord(ConsumerRecord<String, String> record) {
        return new KafkaValidator(record);
    }

    @Step("Проверить записи на соответствие условиям")
    public KafkaValidator validateRecords(List<ConsumerRecord<String, String>> records) {
        return new KafkaValidator(records);
    }

    @Step("Получить из топика '{topic}' запись соответствующую условию")
    public ConsumerRecord<String, String> getRecordByCondition(String topic, Condition condition) {
        return kafkaExecutor.setTopic(topic).getRecordByCondition(condition);
    }

    @Step("Получить из топика '{topic}' все записи")
    public List<ConsumerRecord<String, String>> getAllRecords(String topic) {
        return kafkaExecutor.setTopic(topic).receiveRecords().getAllRecords();
    }

    @Step("Получить из топика '{topic}' все записи соответствующие условию")
    public List<ConsumerRecord<String, String>> getRecordsByCondition(String topic, Condition condition) {
        return kafkaExecutor.setTopic(topic).getRecordsByCondition(condition);
    }

    @Step("Получить из топика '{topic}' все записи с ключом '{key}'")
    public List<ConsumerRecord<String, String>> getRecordsByKey(String topic, String key) {
        return kafkaExecutor.setTopic(topic).receiveRecords().getRecordsByKey(key);
    }

    @Step("Получить из топика '{topic}' все записи со значением заголовка '{headerKey}'")
    public List<ConsumerRecord<String, String>> getRecordsByHeader(String topic, String headerKey, String headerValue) {
        return kafkaExecutor.setTopic(topic).receiveRecords().getRecordsByHeader(headerKey, headerValue);
    }

    @Step("Получить из топика '{topic}' десериализованную запись")
    public <T> T getRecordAs(String topic, Class<T> tClass) {
        return kafkaExecutor.setTopic(topic).receiveRecords().getRecordAs(tClass);
    }

    @Step("Получить из топика '{topic}' десериализованный список всех записей")
    public <T> List<T> getRecordsAsList(String topic, Class<T> tClass) {
        return kafkaExecutor.setTopic(topic).receiveRecords().getRecordsAsList(tClass);
    }
}
