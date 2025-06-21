package com.svedentsov.kafka.helper;

import com.svedentsov.kafka.config.KafkaListenerConfig;
import com.svedentsov.kafka.config.KafkaListenerConfig.EnvConfig;
import com.svedentsov.kafka.enums.ContentType;
import com.svedentsov.kafka.factory.KafkaServiceFactory;
import com.svedentsov.kafka.model.Record;
import com.svedentsov.kafka.service.KafkaConsumerService;
import com.svedentsov.kafka.service.KafkaProducerService;
import com.svedentsov.kafka.utils.RecordLoader;
import com.svedentsov.matcher.Condition;
import com.svedentsov.matcher.EntityValidator;
import com.svedentsov.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Вспомогательный класс для работы с Kafka, обеспечивающий удобное управление продюсерами и консьюмерами Kafka.
 * Теперь ожидается, что перед использованием consumer-а будет установлена конфигурация слушателей через KafkaListenerConfig.
 */
@Slf4j
public class KafkaExecutor {

    private KafkaProducerService producer;
    private KafkaConsumerService consumer;
    private KafkaListenerConfig listenerConfig = EnvConfig.testing();
    private KafkaListenerManager listenerManager = new KafkaListenerManager(listenerConfig);
    private final Record record = new Record();
    private Duration timeout = Duration.ofMillis(300);

    /**
     * Явно установить конфигурацию слушателя.
     * Если не вызывать, будет использоваться KafkaListenerConfig.testing().
     */
    public KafkaExecutor setListenerConfig(KafkaListenerConfig config) {
        this.listenerConfig = config;
        this.listenerManager = new KafkaListenerManager(config);
        return this;
    }

    /**
     * Устанавливает тип продюсера.
     *
     * @param type тип продюсера (например, STRING_FORMAT или AVRO_FORMAT)
     * @return текущий экземпляр KafkaExecutor
     */
    public KafkaExecutor setProducerType(ContentType type) {
        this.producer = KafkaServiceFactory.createProducer(type);
        return this;
    }

    /**
     * Устанавливает тип консьюмера.
     * Если конфиг слушателя не задан явно, используется тестовый.
     */
    public KafkaExecutor setConsumerType(ContentType type) {
        this.consumer = KafkaServiceFactory.createConsumer(type, listenerManager);
        return this;
    }

    /**
     * Устанавливает тайм-аут для получения записей.
     *
     * @param millis тайм-аут в миллисекундах
     * @return текущий экземпляр KafkaExecutor
     */
    public KafkaExecutor setTimeout(long millis) {
        this.timeout = Duration.ofMillis(millis);
        return this;
    }

    /**
     * Устанавливает имя топика.
     *
     * @param topicName имя топика
     * @return текущий экземпляр KafkaExecutor
     */
    public KafkaExecutor setTopic(String topicName) {
        this.record.setTopic(topicName);
        return this;
    }

    /**
     * Устанавливает номер партиции.
     *
     * @param partition номер партиции
     * @return текущий экземпляр KafkaExecutor
     */
    public KafkaExecutor setPartition(int partition) {
        this.record.setPartition(partition);
        return this;
    }

    /**
     * Устанавливает ключ записи.
     *
     * @param key ключ записи
     * @return текущий экземпляр KafkaExecutor
     */
    public KafkaExecutor setRecordKey(String key) {
        this.record.setKey(key);
        return this;
    }

    /**
     * Устанавливает заголовок записи.
     *
     * @param name  имя заголовка
     * @param value значение заголовка
     * @return текущий экземпляр KafkaExecutor
     */
    public KafkaExecutor setRecordHeader(String name, Object value) {
        this.record.getHeaders().put(name, value);
        return this;
    }

    /**
     * Устанавливает заголовки записи.
     *
     * @param headers список заголовков Kafka Header
     * @return текущий экземпляр KafkaExecutor
     */
    public KafkaExecutor setRecordHeaders(List<Header> headers) {
        headers.forEach(header -> record.getHeaders().put(header.key(), new String(header.value())));
        return this;
    }

    /**
     * Устанавливает Avro-схему для записи.
     *
     * @param schema Avro-схема
     * @return текущий экземпляр KafkaExecutor
     */
    public KafkaExecutor setAvroSchema(Schema schema) {
        this.record.setAvroSchema(schema);
        return this;
    }

    /**
     * Устанавливает тело записи.
     *
     * @param value значение записи: строка или Avro-объект
     * @return текущий экземпляр KafkaExecutor
     */
    public KafkaExecutor setRecordBody(Object value) {
        if (value instanceof String) {
            this.record.setValue((String) value);
        } else {
            this.record.setAvroValue(value);
        }
        return this;
    }

    /**
     * Загружает значение записи из источника через RecordLoader.
     *
     * @param source источник данных
     * @return текущий экземпляр KafkaExecutor
     */
    public KafkaExecutor loadRecordBody(String source) {
        this.record.setValue(RecordLoader.loadRecordValue(source));
        return this;
    }

    /**
     * Отправляет запись в Kafka.
     *
     * @return текущий экземпляр KafkaExecutor
     */
    public KafkaExecutor sendRecord() {
        validateProducer();
        producer.sendRecord(record);
        return this;
    }

    /**
     * Получает записи из Kafka.
     *
     * @return текущий экземпляр KafkaExecutor
     */
    public KafkaExecutor receiveRecords() {
        validateConsumer();
        String topic = record.getTopic();
        listenerManager.startListening(topic, timeout, determineAvroForConsumer());
        listenerManager.stopListening(topic);
        return this;
    }

    /**
     * Запускает прослушивание записей из Kafka.
     *
     * @return текущий экземпляр KafkaExecutor
     */
    public KafkaExecutor startListening() {
        validateConsumer();
        listenerManager.startListening(record.getTopic(), timeout, determineAvroForConsumer());
        return this;
    }

    /**
     * Останавливает прослушивание записей из Kafka.
     *
     * @return текущий экземпляр KafkaExecutor
     */
    public KafkaExecutor stopListening() {
        validateConsumer();
        listenerManager.stopListening(record.getTopic());
        return this;
    }

    /**
     * Печатает все записи для указанного топика (через внешний утилитный принтер).
     *
     * @param topic топик, записи которого нужно напечатать
     * @return текущий экземпляр KafkaExecutor
     */
    public KafkaExecutor printAllRecords(String topic) {
        KafkaRecordsPrinter.printAllRecords(topic);
        return this;
    }

    /**
     * Получает все записи для текущего топика.
     *
     * @return список записей для текущего топика
     */
    public List<ConsumerRecord<String, String>> getAllRecords() {
        validateConsumer();
        return consumer.getAllRecords(record.getTopic());
    }

    /**
     * Получает записи по ключу.
     *
     * @param key ключ записи
     * @return список записей с указанным ключом
     */
    public List<ConsumerRecord<String, String>> getRecordsByKey(String key) {
        return filterRecordsBy(records -> key.equals(records.key()));
    }

    /**
     * Получает записи по значению заголовка.
     *
     * @param headerKey   ключ заголовка
     * @param headerValue значение заголовка
     * @return список записей, содержащих указанный заголовок с указанным значением
     */
    public List<ConsumerRecord<String, String>> getRecordsByHeader(String headerKey, String headerValue) {
        return filterRecordsBy(record -> getHeaderValue(record, headerKey).equals(headerValue));
    }

    /**
     * Получает первую запись, удовлетворяющую условию.
     *
     * @param condition условие для фильтрации записей
     * @return первая запись или null, если нет
     */
    public ConsumerRecord<String, String> getRecordByCondition(Condition condition) {
        return getRecordsByCondition(condition).stream().findFirst().orElse(null);
    }

    /**
     * Получает записи, удовлетворяющие условию.
     *
     * @param condition условие для фильтрации записей
     * @return список записей
     */
    public List<ConsumerRecord<String, String>> getRecordsByCondition(Condition condition) {
        validateConsumer();
        return filterRecordsBy(record -> {
            try {
                condition.check(record);
                return true;
            } catch (AssertionError e) {
                return false;
            }
        });
    }

    /**
     * Преобразует первую запись в объект указанного типа.
     *
     * @param tClass класс типа, в который нужно преобразовать запись
     * @param <T>    тип объекта
     * @return объект указанного типа или null, если нет записей
     */
    public <T> T getRecordAs(Class<T> tClass) {
        validateConsumer();
        String firstRecordValue = consumer.getAllRecords(record.getTopic()).getFirst().value();
        return JsonUtils.fromJson(firstRecordValue, tClass);
    }

    /**
     * Преобразует первую запись, удовлетворяющую условию, в объект указанного типа.
     *
     * @param tClass    класс типа, в который нужно преобразовать запись
     * @param condition условие для фильтрации записей
     * @param <T>       тип объекта
     * @return объект указанного типа или null, если нет подходящих записей
     */
    public <T> T getRecordAs(Class<T> tClass, Condition condition) {
        List<ConsumerRecord<String, String>> records = getRecordsByCondition(condition);
        return records.isEmpty() ? null : JsonUtils.fromJson(records.get(0).value(), tClass);
    }

    /**
     * Преобразует все записи в список объектов указанного типа.
     *
     * @param tClass класс типа, в который нужно преобразовать записи
     * @param <T>    тип объектов
     * @return список объектов, может быть пустым
     */
    public <T> List<T> getRecordsAsList(Class<T> tClass) {
        validateConsumer();
        return consumer.getAllRecords(record.getTopic()).stream()
                .map(record -> JsonUtils.fromJson(record.value(), tClass))
                .collect(Collectors.toList());
    }

    /**
     * Преобразует записи, удовлетворяющие условию, в список объектов указанного типа.
     *
     * @param tClass    класс типа, в который нужно преобразовать записи
     * @param condition условие для фильтрации записей
     * @param <T>       тип объектов
     * @return список объектов, может быть пустым
     */
    public <T> List<T> getRecordsAsList(Class<T> tClass, Condition condition) {
        List<ConsumerRecord<String, String>> records = getRecordsByCondition(condition);
        return records.stream()
                .map(record -> JsonUtils.fromJson(record.value(), tClass))
                .collect(Collectors.toList());
    }

    /**
     * Проверяет, что все записи удовлетворяют указанному условию.
     *
     * @param conditions массив условий
     * @return текущий экземпляр KafkaExecutor
     */
    public KafkaExecutor shouldHave(Condition... conditions) {
        validateConsumer();
        List<ConsumerRecord<String, String>> records = consumer.getAllRecords(record.getTopic());
        EntityValidator.of(records).shouldHave(conditions);
        return this;
    }

    /**
     * Получает значение заголовка записи по его ключу.
     *
     * @param record    запись Kafka
     * @param headerKey ключ заголовка
     * @return значение заголовка или null
     */
    private String getHeaderValue(ConsumerRecord<String, String> record, String headerKey) {
        return StreamSupport.stream(record.headers().spliterator(), false)
                .filter(header -> header.key().equals(headerKey))
                .findFirst()
                .map(header -> new String(header.value()))
                .orElse(null);
    }

    /**
     * Проверяет, установлен ли продюсер.
     *
     * @throws IllegalStateException если продюсер не установлен
     */
    private void validateProducer() {
        if (Objects.isNull(producer)) {
            throw new IllegalStateException("Продюсер не установлен. Вызовите setProducerType().");
        }
    }

    /**
     * Проверяет, установлен ли консьюмер.
     *
     * @throws IllegalStateException если консьюмер не установлен
     */
    private void validateConsumer() {
        if (Objects.isNull(consumer)) {
            throw new IllegalStateException("Консьюмер не установлен. Вызовите setConsumerType().");
        }
    }

    /**
     * Общий метод для фильтрации записей по предикату.
     *
     * @param predicate условие фильтрации
     * @return список записей, удовлетворяющих условию
     */
    private List<ConsumerRecord<String, String>> filterRecordsBy(Predicate<ConsumerRecord<String, String>> predicate) {
        validateConsumer();
        return consumer.getAllRecords(record.getTopic()).stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * Определяем, Avro ли консьюмер. Можно улучшить, добавив метод в интерфейс.
     */
    private boolean determineAvroForConsumer() {
        return consumer.getClass().getSimpleName().toLowerCase().contains("avro");
    }
}
