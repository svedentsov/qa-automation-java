package com.svedentsov.kafka.helper;

import com.svedentsov.kafka.enums.ContentType;
import com.svedentsov.kafka.factory.KafkaServiceFactory;
import com.svedentsov.kafka.factory.ProducerFactoryDefault;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.svedentsov.kafka.config.KafkaListenerConfig.EnvConfig.testing;
import static com.svedentsov.kafka.utils.ValidationUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * Вспомогательный класс для работы с Kafka, обеспечивающий удобное управление продюсерами и консьюмерами Kafka.
 * Предоставляет текучий (fluent) API для настройки, отправки и получения сообщений, а также их валидации.
 * Ожидается, что перед использованием consumer-а будет установлена конфигурация слушателей через KafkaListenerConfig.
 */
@Slf4j
public class KafkaExecutor implements AutoCloseable {
    private final KafkaServiceFactory serviceFactory;
    private final KafkaListenerManager listenerManager;
    private KafkaProducerService producer;
    private KafkaConsumerService consumer;
    private ContentType contentType;
    private final Record record = new Record();
    private Duration pollTimeout = Duration.ofMillis(1000);

    public KafkaExecutor() {
        this.serviceFactory = new KafkaServiceFactory(new ProducerFactoryDefault());
        this.listenerManager = new KafkaListenerManager(testing());
    }

    /**
     * Создает экземпляр KafkaExecutor с необходимыми зависимостями.
     *
     * @param serviceFactory  фабрика для создания экземпляров Kafka-сервисов, не может быть {@code null}.
     * @param listenerManager менеджер для управления жизненным циклом Kafka-слушателей, не может быть {@code null}.
     */
    public KafkaExecutor(KafkaServiceFactory serviceFactory, KafkaListenerManager listenerManager) {
        this.serviceFactory = requireNonNull(serviceFactory, "KafkaServiceFactory не может быть null.");
        this.listenerManager = requireNonNull(listenerManager, "KafkaListenerManager не может быть null.");
    }

    /**
     * Устанавливает тип продюсера. Этот метод должен быть вызван перед отправкой сообщений.
     *
     * @param type тип продюсера (например, {@link ContentType#STRING_FORMAT} или {@link ContentType#AVRO_FORMAT}).
     * @return текущий экземпляр KafkaExecutor для цепочки вызовов.
     */
    public KafkaExecutor setProducerType(ContentType type) {
        requireNonNull(type, "ContentType для продюсера не может быть null");
        this.contentType = type;
        this.producer = serviceFactory.createProducer(type);
        return this;
    }

    /**
     * Устанавливает тип консьюмера. Этот метод должен быть вызван перед получением сообщений.
     *
     * @param type тип консьюмера (например, {@link ContentType#STRING_FORMAT} или {@link ContentType#AVRO_FORMAT}).
     * @return текущий экземпляр KafkaExecutor для цепочки вызовов.
     */
    public KafkaExecutor setConsumerType(ContentType type) {
        requireNonNull(type, "ContentType для консьюмера не может быть null");
        this.contentType = type;
        this.consumer = serviceFactory.createConsumer(type, listenerManager);
        return this;
    }

    /**
     * Устанавливает тайм-аут для операции получения записей (poll timeout).
     *
     * @param millis тайм-аут в миллисекундах. Должен быть неотрицательным.
     * @return текущий экземпляр KafkaExecutor для цепочки вызовов.
     * @throws IllegalArgumentException если {@code millis} отрицательный.
     */
    public KafkaExecutor setTimeout(long millis) {
        if (millis < 0) throw new IllegalArgumentException("Таймаут не может быть отрицательным.");
        this.pollTimeout = Duration.ofMillis(millis);
        return this;
    }

    /**
     * Устанавливает имя топика для операций отправки или получения.
     *
     * @param topicName имя топика, не может быть {@code null} или пустым.
     * @return текущий экземпляр KafkaExecutor для цепочки вызовов.
     * @throws IllegalArgumentException если {@code topicName} {@code null} или пустой.
     */
    public KafkaExecutor setTopic(String topicName) {
        requireNonBlank(topicName, "Имя топика не может быть null или пустым.");
        this.record.setTopic(topicName);
        return this;
    }

    /**
     * Устанавливает номер партиции, в которую будет отправлена запись (для продюсера).
     *
     * @param partition номер партиции.
     * @return текущий экземпляр KafkaExecutor для цепочки вызовов.
     */
    public KafkaExecutor setPartition(int partition) {
        this.record.setPartition(partition);
        return this;
    }

    /**
     * Устанавливает ключ записи.
     *
     * @param key ключ записи, может быть {@code null}.
     * @return текущий экземпляр KafkaExecutor для цепочки вызовов.
     */
    public KafkaExecutor setRecordKey(String key) {
        this.record.setKey(key);
        return this;
    }

    /**
     * Устанавливает один заголовок записи.
     *
     * @param name  имя заголовка, не может быть {@code null} или пустым.
     * @param value значение заголовка, может быть {@code null}.
     * @return текущий экземпляр KafkaExecutor для цепочки вызовов.
     * @throws IllegalArgumentException если имя заголовка {@code null} или пустое.
     */
    public KafkaExecutor setRecordHeader(String name, Object value) {
        requireNonBlank(name, "Имя заголовка не может быть null или пустым.");
        this.record.getHeaders().put(name, value);
        return this;
    }

    /**
     * Устанавливает заголовки записи из списка Kafka {@link Header}.
     * Значения заголовков преобразуются в строки.
     *
     * @param headers список заголовков Kafka, не может быть {@code null}.
     * @return текущий экземпляр KafkaExecutor для цепочки вызовов.
     * @throws IllegalArgumentException если переданный список заголовков {@code null}.
     */
    public KafkaExecutor setRecordHeaders(List<Header> headers) {
        requireNonNull(headers, "Список заголовков не может быть null.");
        headers.forEach(header -> this.record.getHeaders().put(header.key(), new String(header.value())));
        return this;
    }

    /**
     * Устанавливает Avro-схему для записи. Используется, когда тип продюсера {@link ContentType#AVRO_FORMAT}.
     *
     * @param schema Avro-схема, не может быть {@code null}.
     * @return текущий экземпляр KafkaExecutor для цепочки вызовов.
     * @throws IllegalArgumentException если переданная схема {@code null}.
     */
    public KafkaExecutor setAvroSchema(Schema schema) {
        requireNonNull(schema, "Avro схема не может быть null.");
        this.record.setAvroSchema(schema);
        return this;
    }

    /**
     * Устанавливает тело записи. Тип значения должен соответствовать типу продюсера:
     * {@code String} для {@link ContentType#STRING_FORMAT} или Avro-объект для {@link ContentType#AVRO_FORMAT}.
     *
     * @param value значение записи: строка или Avro-объект.
     * @return текущий экземпляр KafkaExecutor для цепочки вызовов.
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
     * Загружает значение записи из файла или ресурса через {@link RecordLoader}.
     * Предполагается, что загруженное значение будет строкой.
     *
     * @param source путь к файлу или ресурсу, откуда загрузить значение.
     * @return текущий экземпляр KafkaExecutor для цепочки вызовов.
     */
    public KafkaExecutor loadRecordBody(String source) {
        this.record.setValue(RecordLoader.loadRecordValue(source));
        return this;
    }

    /**
     * Отправляет сконфигурированную запись в Kafka.
     * Требует предварительной настройки продюсера через {@link #setProducerType(ContentType)} и топика через {@link #setTopic(String)}.
     * После отправки сбрасывает состояние Record (для повторного использования).
     *
     * @return текущий экземпляр KafkaExecutor для цепочки вызовов.
     * @throws IllegalStateException если продюсер не установлен или топик не задан.
     */
    public KafkaExecutor sendRecord() {
        validateProducerAndTopic();
        producer.sendRecord(record);
        record.clear(); // Очищаем Record для следующего использования
        return this;
    }

    /**
     * Запускает прослушивание записей из Kafka для текущего топика, ожидает заданный pollTimeout,
     * затем останавливает прослушивание и возвращает управление.
     * Используется обычно в тестовых сценариях для ожидания появления сообщений.
     *
     * @return текущий экземпляр KafkaExecutor для цепочки вызовов.
     * @throws IllegalStateException если консьюмер не установлен или топик не задан.
     */
    public KafkaExecutor receiveRecords() {
        validateConsumerAndTopic();
        String topic = record.getTopic();
        listenerManager.startListening(topic, pollTimeout, contentType == ContentType.AVRO_FORMAT);
        listenerManager.stopListening(topic);
        return this;
    }

    /**
     * Запускает прослушивание записей из Kafka для текущего топика в фоновом режиме.
     * Прослушивание будет продолжаться до явного вызова {@link #stopListening()}.
     *
     * @return текущий экземпляр KafkaExecutor для цепочки вызовов.
     * @throws IllegalStateException если консьюмер не установлен или топик не задан.
     */
    public KafkaExecutor startListening() {
        validateConsumerAndTopic();
        listenerManager.startListening(record.getTopic(), pollTimeout, contentType == ContentType.AVRO_FORMAT);
        return this;
    }

    /**
     * Останавливает ранее запущенное прослушивание записей из Kafka для текущего топика.
     *
     * @return текущий экземпляр KafkaExecutor для цепочки вызовов.
     * @throws IllegalStateException если консьюмер не установлен или топик не задан.
     */
    public KafkaExecutor stopListening() {
        validateConsumerAndTopic();
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
     * Получает все записи для текущего топика, которые были собраны консьюмером.
     *
     * @return список записей {@link ConsumerRecord} для текущего топика.
     * @throws IllegalStateException если консьюмер не установлен или топик не задан.
     */
    public List<ConsumerRecord<String, String>> getAllRecords() {
        validateConsumerAndTopic();
        return consumer.getAllRecords(record.getTopic());
    }

    /**
     * Получает записи для текущего топика по указанному ключу.
     *
     * @param key ключ записи, по которому производится фильтрация.
     * @return список записей {@link ConsumerRecord} с указанным ключом.
     * @throws IllegalStateException если консьюмер не установлен или топик не задан.
     */
    public List<ConsumerRecord<String, String>> getRecordsByKey(String key) {
        return filterRecordsBy(records -> key.equals(records.key()));
    }

    /**
     * Получает записи для текущего топика по значению заголовка.
     *
     * @param headerKey   ключ заголовка.
     * @param headerValue значение заголовка, по которому производится фильтрация.
     * @return список записей {@link ConsumerRecord}, содержащих указанный заголовок с указанным значением.
     * @throws IllegalStateException если консьюмер не установлен или топик не задан.
     */
    public List<ConsumerRecord<String, String>> getRecordsByHeader(String headerKey, String headerValue) {
        return filterRecordsBy(record -> getHeaderValue(record, headerKey).equals(headerValue));
    }

    /**
     * Получает первую запись для текущего топика, удовлетворяющую указанному условию.
     *
     * @param condition условие {@link Condition} для фильтрации записей.
     * @return первая запись {@link ConsumerRecord}, удовлетворяющая условию, или {@code null}, если таких записей нет.
     * @throws IllegalStateException если консьюмер не установлен или топик не задан.
     */
    public ConsumerRecord<String, String> getRecordByCondition(Condition condition) {
        return getRecordsByCondition(condition).stream().findFirst().orElse(null);
    }

    /**
     * Получает все записи для текущего топика, удовлетворяющие указанному условию.
     *
     * @param condition условие {@link Condition} для фильтрации записей.
     * @return список записей {@link ConsumerRecord}, удовлетворяющих условию.
     * @throws IllegalStateException если консьюмер не установлен или топик не задан.
     */
    public List<ConsumerRecord<String, String>> getRecordsByCondition(Condition condition) {
        validateConsumerAndTopic();
        return filterRecordsBy(record -> {
            try {
                condition.check(this.record);
                return true;
            } catch (AssertionError e) {
                return false;
            }
        });
    }

    /**
     * Преобразует значение первой записи для текущего топика в объект указанного типа.
     * Предполагается, что значение записи является JSON-строкой.
     *
     * @param tClass класс типа, в который нужно преобразовать значение записи.
     * @param <T>    тип объекта.
     * @return объект указанного типа или {@code null}, если нет записей или ошибка десериализации.
     * @throws IllegalStateException если консьюмер не установлен или топик не задан.
     */
    public <T> T getRecordAs(Class<T> tClass) {
        validateConsumerAndTopic();
        String firstRecordValue = consumer.getAllRecords(record.getTopic()).getFirst().value();
        return JsonUtils.fromJson(firstRecordValue, tClass);
    }

    /**
     * Преобразует значение первой записи для текущего топика, удовлетворяющей условию, в объект указанного типа.
     * Предполагается, что значение записи является JSON-строкой.
     *
     * @param tClass    класс типа, в который нужно преобразовать значение записи.
     * @param condition условие {@link Condition} для фильтрации записей.
     * @param <T>       тип объекта.
     * @return объект указанного типа или {@code null}, если нет подходящих записей или ошибка десериализации.
     * @throws IllegalStateException если консьюмер не установлен или топик не задан.
     */
    public <T> T getRecordAs(Class<T> tClass, Condition condition) {
        List<ConsumerRecord<String, String>> records = getRecordsByCondition(condition);
        return records.isEmpty() ? null : JsonUtils.fromJson(records.get(0).value(), tClass);
    }

    /**
     * Преобразует значения всех записей для текущего топика в список объектов указанного типа.
     * Предполагается, что значения записей являются JSON-строками.
     *
     * @param tClass класс типа, в который нужно преобразовать значения записей.
     * @param <T>    тип объектов.
     * @return список объектов указанного типа; может быть пустым, если нет записей.
     * @throws IllegalStateException если консьюмер не установлен или топик не задан.
     */
    public <T> List<T> getRecordsAsList(Class<T> tClass) {
        validateConsumerAndTopic();
        return consumer.getAllRecords(record.getTopic()).stream()
                .map(record -> JsonUtils.fromJson(record.value(), tClass))
                .collect(Collectors.toList());
    }

    /**
     * Преобразует значения записей для текущего топика, удовлетворяющих условию, в список объектов указанного типа.
     * Предполагается, что значения записей являются JSON-строками.
     *
     * @param tClass    класс типа, в который нужно преобразовать значения записей.
     * @param condition условие {@link Condition} для фильтрации записей.
     * @param <T>       тип объектов.
     * @return список объектов указанного типа; может быть пустым, если нет подходящих записей.
     * @throws IllegalStateException если консьюмер не установлен или топик не задан.
     */
    public <T> List<T> getRecordsAsList(Class<T> tClass, Condition condition) {
        List<ConsumerRecord<String, String>> records = getRecordsByCondition(condition);
        return records.stream()
                .map(record -> JsonUtils.fromJson(record.value(), tClass))
                .collect(Collectors.toList());
    }

    /**
     * Проверяет, что все записи для текущего топика удовлетворяют указанному набору условий.
     * Использует {@link EntityValidator} для выполнения утверждений.
     *
     * @param conditions массив условий {@link Condition}, которым должны удовлетворять записи.
     * @return текущий экземпляр KafkaExecutor для цепочки вызовов.
     * @throws IllegalStateException если консьюмер не установлен или топик не задан.
     * @throws AssertionError        если одно или несколько условий не выполнены.
     */
    public KafkaExecutor shouldHave(Condition... conditions) {
        validateConsumerAndTopic();
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

    private void validateProducerAndTopic() {
        if (producer == null) {
            throw new IllegalStateException("Продюсер не установлен. Вызовите setProducerType().");
        }
        if (record.getTopic() == null || record.getTopic().isBlank()) {
            throw new IllegalStateException("Топик для отправки не задан. Вызовите setTopic().");
        }
    }

    private void validateConsumerAndTopic() {
        if (consumer == null) {
            throw new IllegalStateException("Консьюмер не установлен. Вызовите setConsumerType().");
        }
        if (record.getTopic() == null || record.getTopic().isBlank()) {
            throw new IllegalStateException("Топик для получения не задан. Вызовите setTopic().");
        }
    }

    /**
     * Общий метод для фильтрации записей по предикату.
     *
     * @param predicate условие фильтрации
     * @return список записей, удовлетворяющих условию
     */
    private List<ConsumerRecord<String, String>> filterRecordsBy(Predicate<ConsumerRecord<String, String>> predicate) {
        validateConsumerAndTopic();
        return consumer.getAllRecords(record.getTopic()).stream()
                .filter(predicate)
                .toList();
    }

    /**
     * Закрывает все активные слушатели и очищает ресурсы, связанные с {@link KafkaListenerManager}.
     * Этот метод вызывается автоматически при использовании try-with-resources или может быть вызван явно.
     */
    @Override
    public void close() {
        if (this.listenerManager != null) {
            log.info("Завершение работы KafkaExecutor: остановка KafkaListenerManager.");
            this.listenerManager.shutdown();
        }
        // здесь можно добавить очистку клиентов, если требуется:
        // KafkaClientPool.closeAllProducers(); KafkaClientPool.closeAllConsumers();
    }
}
