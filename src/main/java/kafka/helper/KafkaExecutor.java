package kafka.helper;

import core.matcher.Condition;
import kafka.enums.ContentType;
import kafka.factory.KafkaServiceFactory;
import kafka.matcher.KafkaValidator;
import kafka.model.Record;
import kafka.service.KafkaConsumerService;
import kafka.service.KafkaProducerService;
import kafka.utils.JsonUtils;
import kafka.utils.RecordLoader;
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
 * Позволяет настраивать, отправлять и получать записи, а также управлять их жизненным циклом.
 */
@Slf4j
public class KafkaExecutor {

    private KafkaProducerService producer;
    private KafkaConsumerService consumer;
    private final Record record = new Record();
    private Duration timeout = Duration.ofMillis(300);

    /**
     * Устанавливает тип продюсера.
     *
     * @param type тип продюсера (например, String или Avro)
     * @return экземпляр текущего объекта {@code KafkaExecutor} для цепочки вызовов
     */
    public KafkaExecutor setProducerType(ContentType type) {
        this.producer = KafkaServiceFactory.createProducer(type);
        return this;
    }

    /**
     * Устанавливает тип консьюмера.
     *
     * @param type тип консьюмера (например, String или Avro)
     * @return экземпляр текущего объекта {@code KafkaExecutor} для цепочки вызовов
     */
    public KafkaExecutor setConsumerType(ContentType type) {
        this.consumer = KafkaServiceFactory.createConsumer(type);
        return this;
    }

    /**
     * Устанавливает тайм-аут для получения записей.
     *
     * @param millis тайм-аут в миллисекундах
     * @return экземпляр текущего объекта {@code KafkaExecutor} для цепочки вызовов
     */
    public KafkaExecutor setTimeout(long millis) {
        this.timeout = Duration.ofMillis(millis);
        return this;
    }

    /**
     * Устанавливает имя топика.
     *
     * @param topicName имя топика
     * @return экземпляр текущего объекта {@code KafkaExecutor} для цепочки вызовов
     */
    public KafkaExecutor setTopic(String topicName) {
        this.record.setTopic(topicName);
        return this;
    }

    /**
     * Устанавливает номер партиции.
     *
     * @param partition номер партиции
     * @return экземпляр текущего объекта {@code KafkaExecutor} для цепочки вызовов
     */
    public KafkaExecutor setPartition(int partition) {
        this.record.setPartition(partition);
        return this;
    }

    /**
     * Устанавливает ключ записи.
     *
     * @param key ключ записи
     * @return экземпляр текущего объекта {@code KafkaExecutor} для цепочки вызовов
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
     * @return экземпляр текущего объекта {@code KafkaExecutor} для цепочки вызовов
     */
    public KafkaExecutor setRecordHeader(String name, Object value) {
        this.record.getHeaders().put(name, value);
        return this;
    }

    /**
     * Устанавливает заголовки записи.
     *
     * @param headers список заголовков
     * @return экземпляр текущего объекта {@code KafkaExecutor} для цепочки вызовов
     */
    public KafkaExecutor setRecordHeaders(List<Header> headers) {
        headers.forEach(header -> record.getHeaders().put(header.key(), new String(header.value())));
        return this;
    }

    /**
     * Устанавливает Avro-схему для записи.
     *
     * @param schema Avro-схема
     * @return экземпляр текущего объекта {@code KafkaExecutor} для цепочки вызовов
     */
    public KafkaExecutor setAvroSchema(Schema schema) {
        this.record.setAvroSchema(schema);
        return this;
    }

    /**
     * Устанавливает тело записи.
     *
     * @param value значение записи
     * @return экземпляр текущего объекта {@code KafkaExecutor} для цепочки вызовов
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
     * Загружает значение записи из источника.
     *
     * @param source источник данных
     * @return экземпляр текущего объекта {@code KafkaExecutor} для цепочки вызовов
     */
    public KafkaExecutor loadRecordBody(String source) {
        this.record.setValue(RecordLoader.loadRecordValue(source));
        return this;
    }

    /**
     * Отправляет запись в Kafka.
     *
     * @return экземпляр текущего объекта {@code KafkaExecutor} для цепочки вызовов
     */
    public KafkaExecutor sendRecord() {
        validateProducer();
        producer.sendRecord(record);
        return this;
    }

    /**
     * Получает записи из Kafka.
     *
     * @return экземпляр текущего объекта {@code KafkaExecutor} для цепочки вызовов
     */
    public KafkaExecutor receiveRecords() {
        validateConsumer();
        consumer.startListening(record.getTopic(), timeout);
        consumer.stopListening(record.getTopic());
        return this;
    }

    /**
     * Запускает прослушивание записей из Kafka.
     *
     * @return экземпляр текущего объекта {@code KafkaExecutor} для цепочки вызовов
     */
    public KafkaExecutor startListening() {
        validateConsumer();
        consumer.startListening(record.getTopic(), timeout);
        return this;
    }

    /**
     * Останавливает прослушивание записей из Kafka.
     *
     * @return экземпляр текущего объекта {@code KafkaExecutor} для цепочки вызовов
     */
    public KafkaExecutor stopListening() {
        validateConsumer();
        consumer.stopListening(record.getTopic());
        return this;
    }

    /**
     * Печатает все записи для указанного топика.
     *
     * @param topic топик, записи которого нужно напечатать
     * @return экземпляр текущего объекта {@code KafkaExecutor} для цепочки вызовов
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
     * @return первая запись, удовлетворяющая условию, или {@code null}, если таковая не найдена
     */
    public ConsumerRecord<String, String> getRecordByCondition(Condition condition) {
        return getRecordsByCondition(condition).stream().findFirst().orElse(null);
    }

    /**
     * Получает записи, удовлетворяющие условию.
     *
     * @param condition условие для фильтрации записей
     * @return список записей, удовлетворяющих условию
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
     * @return объект указанного типа, созданный из первой записи
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
     * @return объект указанного типа или {@code null}, если подходящих записей нет
     */
    public <T> T getRecordAs(Class<T> tClass, Condition condition) {
        List<ConsumerRecord<String, String>> records = getRecordsByCondition(condition);
        return records.isEmpty() ? null : JsonUtils.fromJson(records.getFirst().value(), tClass);
    }

    /**
     * Преобразует все записи в список объектов указанного типа.
     *
     * @param tClass класс типа, в который нужно преобразовать записи
     * @param <T>    тип объектов
     * @return список объектов указанного типа, созданных из записей
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
     * @return список объектов указанного типа (может быть пустым)
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
     * @param condition условия для проверки записей
     * @return экземпляр текущего объекта {@code KafkaExecutor} для цепочки вызовов
     */
    public KafkaExecutor shouldHave(Condition... condition) {
        validateConsumer();
        List<ConsumerRecord<String, String>> records = consumer.getAllRecords(record.getTopic());
        new KafkaValidator<>(records).shouldHave(condition);
        return this;
    }

    /**
     * Получает значение заголовка записи по его ключу.
     *
     * @param record    запись Kafka
     * @param headerKey ключ заголовка
     * @return значение заголовка или {@code null}, если заголовок не найден
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
            throw new IllegalStateException("Продюсер не установлен. Установите его с помощью метода 'setProducerType()'.");
        }
    }

    /**
     * Проверяет, установлен ли консьюмер.
     *
     * @throws IllegalStateException если консьюмер не установлен
     */
    private void validateConsumer() {
        if (Objects.isNull(consumer)) {
            throw new IllegalStateException("Консьюмер не установлен. Установите его с помощью метода 'setConsumerType()'.");
        }
    }

    /**
     * Фильтрует записи по заданному предикату.
     *
     * @param predicate предикат для фильтрации записей
     * @return список записей, удовлетворяющих предикату
     */
    private List<ConsumerRecord<String, String>> filterRecordsBy(Predicate<ConsumerRecord<String, String>> predicate) {
        return consumer.getAllRecords(record.getTopic()).stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
}
