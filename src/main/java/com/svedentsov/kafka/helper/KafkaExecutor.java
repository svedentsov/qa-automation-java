package com.svedentsov.kafka.helper;

import com.svedentsov.kafka.config.KafkaConfig;
import com.svedentsov.kafka.config.KafkaConfigProvider;
import com.svedentsov.kafka.enums.ContentType;
import com.svedentsov.kafka.factory.ConsumerFactoryDefault;
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
import org.aeonbits.owner.ConfigFactory;
import org.apache.avro.Schema;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.svedentsov.kafka.config.KafkaListenerConfig.EnvConfig.testing;
import static com.svedentsov.kafka.utils.ValidationUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * Главный класс-фасад для выполнения операций с Kafka в рамках автоматизированных тестов.
 * <p>
 * Предоставляет текучий (fluent) API для настройки, отправки и получения сообщений.
 * Каждый экземпляр {@code KafkaExecutor} является изолированной сессией, управляющей
 * собственными менеджерами и сервисами, что предотвращает конфликты при параллельном
 * выполнении тестов.
 * <p>
 * Класс реализует {@link AutoCloseable}, что позволяет использовать его в блоке
 * try-with-resources для автоматического освобождения ресурсов.
 * <p>
 * <b>Пример использования:</b>
 * <pre>{@code
 * try (KafkaExecutor executor = new KafkaExecutor()) {
 * // Отправка сообщения
 * executor.setProducerType(ContentType.STRING_FORMAT)
 * .setTopic("my-topic")
 * .setRecordKey("my-key")
 * .setRecordBody("Hello, Kafka!")
 * .sendRecord();
 *
 * // Получение и валидация
 * List<String> messages = executor.setConsumerType(ContentType.STRING_FORMAT)
 * .setTopic("my-topic")
 * .startListening()
 * // ... подождать получения сообщений ...
 * .stopListening()
 * .getRecordsAsList(String.class);
 *
 * assertThat(messages).contains("Hello, Kafka!");
 * }
 * }</pre>
 */
@Slf4j
public class KafkaExecutor implements AutoCloseable {

    private final KafkaServiceFactory serviceFactory;
    private final KafkaListenerManager listenerManager;
    private final KafkaRecordsManager recordsManager;
    private KafkaProducerService producer;
    private KafkaConsumerService consumer;
    private final Record record = new Record();
    private Duration pollTimeout = Duration.ofMillis(1000);

    /**
     * Создает экземпляр с конфигурацией по умолчанию.
     */
    public KafkaExecutor() {
        KafkaConfig kafkaConfig = ConfigFactory.create(KafkaConfig.class, System.getProperties());
        KafkaConfigProvider configProvider = new KafkaConfigProvider(kafkaConfig);
        ProducerFactoryDefault producerFactory = new ProducerFactoryDefault(configProvider);
        ConsumerFactoryDefault consumerFactory = new ConsumerFactoryDefault(configProvider);

        this.serviceFactory = new KafkaServiceFactory(producerFactory);
        this.recordsManager = new KafkaRecordsManager();
        this.listenerManager = new KafkaListenerManager(testing(), consumerFactory, this.recordsManager);
    }

    /**
     * Создает экземпляр KafkaExecutor с предоставленными зависимостями.
     * Используется для тестирования самого KafkaExecutor.
     *
     * @param serviceFactory  фабрика для создания Kafka-сервисов.
     * @param listenerManager менеджер для управления жизненным циклом слушателей.
     * @param recordsManager  менеджер для хранения полученных записей.
     */
    public KafkaExecutor(KafkaServiceFactory serviceFactory, KafkaListenerManager listenerManager, KafkaRecordsManager recordsManager) {
        this.serviceFactory = requireNonNull(serviceFactory, "KafkaServiceFactory не может быть null.");
        this.listenerManager = requireNonNull(listenerManager, "KafkaListenerManager не может быть null.");
        this.recordsManager = requireNonNull(recordsManager, "KafkaRecordsManager не может быть null.");
    }

    /**
     * Устанавливает тип продюсера. Этот метод должен быть вызван перед отправкой сообщений.
     *
     * @param type Тип контента (например, {@link ContentType#STRING_FORMAT}).
     * @return текущий экземпляр {@code KafkaExecutor} для построения цепочки вызовов.
     */
    public KafkaExecutor setProducerType(ContentType type) {
        requireNonNull(type, "ContentType для продюсера не может быть null");
        this.producer = serviceFactory.createProducer(type);
        return this;
    }

    /**
     * Устанавливает тип консьюмера. При вызове создается соответствующий сервис,
     * которому передаются менеджеры, принадлежащие этому экземпляру KafkaExecutor.
     *
     * @param type Тип контента (например, {@link ContentType#AVRO_FORMAT}).
     * @return текущий экземпляр {@code KafkaExecutor} для построения цепочки вызовов.
     */
    public KafkaExecutor setConsumerType(ContentType type) {
        requireNonNull(type, "ContentType для консьюмера не может быть null");
        this.consumer = serviceFactory.createConsumer(type, this.listenerManager, this.recordsManager);
        return this;
    }

    /**
     * Устанавливает тайм-аут для операции получения записей (poll).
     *
     * @param millis тайм-аут в миллисекундах. Должен быть неотрицательным.
     * @return текущий экземпляр {@code KafkaExecutor}.
     * @throws IllegalArgumentException если {@code millis} отрицательный.
     */
    public KafkaExecutor setTimeout(long millis) {
        if (millis < 0) throw new IllegalArgumentException("Таймаут не может быть отрицательным.");
        this.pollTimeout = Duration.ofMillis(millis);
        return this;
    }

    /**
     * Устанавливает имя топика для последующих операций.
     *
     * @param topicName имя топика, не может быть {@code null} или пустым.
     * @return текущий экземпляр {@code KafkaExecutor}.
     */
    public KafkaExecutor setTopic(String topicName) {
        requireNonBlank(topicName, "Имя топика не может быть null или пустым.");
        this.record.setTopic(topicName);
        return this;
    }

    /**
     * Устанавливает номер партиции для отправки записи.
     *
     * @param partition номер партиции.
     * @return текущий экземпляр {@code KafkaExecutor}.
     */
    public KafkaExecutor setPartition(int partition) {
        this.record.setPartition(partition);
        return this;
    }

    /**
     * Устанавливает ключ для отправляемой записи.
     *
     * @param key ключ записи.
     * @return текущий экземпляр {@code KafkaExecutor}.
     */
    public KafkaExecutor setRecordKey(String key) {
        this.record.setKey(key);
        return this;
    }

    /**
     * Добавляет один заголовок к отправляемой записи.
     *
     * @param name  имя заголовка, не может быть пустым.
     * @param value значение заголовка.
     * @return текущий экземпляр {@code KafkaExecutor}.
     */
    public KafkaExecutor setRecordHeader(String name, Object value) {
        requireNonBlank(name, "Имя заголовка не может быть null или пустым.");
        this.record.getHeaders().put(name, value);
        return this;
    }

    /**
     * Устанавливает заголовки для отправляемой записи.
     *
     * @param headers список заголовков Kafka.
     * @return текущий экземпляр {@code KafkaExecutor}.
     */
    public KafkaExecutor setRecordHeaders(List<Header> headers) {
        requireNonNull(headers, "Список заголовков не может быть null.");
        headers.forEach(header -> this.record.getHeaders().put(header.key(), new String(header.value())));
        return this;
    }

    /**
     * Устанавливает Avro-схему для отправляемой записи.
     *
     * @param schema Avro-схема.
     * @return текущий экземпляр {@code KafkaExecutor}.
     */
    public KafkaExecutor setAvroSchema(Schema schema) {
        requireNonNull(schema, "Avro схема не может быть null.");
        this.record.setAvroSchema(schema);
        return this;
    }

    /**
     * Устанавливает тело (value) для отправляемой записи.
     *
     * @param value значение: {@link String} или Avro-объект (например, {@code GenericRecord}).
     * @return текущий экземпляр {@code KafkaExecutor}.
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
     * Загружает тело записи из файла или ресурса.
     *
     * @param source путь к файлу или ресурсу.
     * @return текущий экземпляр {@code KafkaExecutor}.
     */
    public KafkaExecutor loadRecordBody(String source) {
        this.record.setValue(RecordLoader.loadRecordValue(source));
        return this;
    }

    /**
     * Синхронно отправляет сконфигурированную запись в Kafka.
     *
     * @return текущий экземпляр {@code KafkaExecutor}.
     * @throws IllegalStateException если продюсер или топик не настроены.
     */
    public KafkaExecutor sendRecord() {
        validateProducerAndTopic();
        producer.sendRecord(record);
        return this;
    }

    /**
     * Асинхронно отправляет сконфигурированную запись в Kafka.
     *
     * @return текущий экземпляр {@code KafkaExecutor}.
     * @throws IllegalStateException если продюсер или топик не настроены.
     */
    public KafkaExecutor sendRecordAsync() {
        validateProducerAndTopic();
        log.info("Асинхронная отправка записи в топик '{}': {}", record.getTopic(), record);
        producer.sendRecordAsync(record);
        return this;
    }

    /**
     * Запускает прослушивание, ожидает заданный таймаут и останавливает его.
     * Удобно для сценариев, где нужно "собрать" все сообщения за короткий промежуток времени.
     *
     * @return текущий экземпляр {@code KafkaExecutor}.
     * @throws IllegalStateException если консьюмер или топик не настроены.
     */
    public KafkaExecutor receiveRecords() {
        validateConsumerAndTopic();
        String topic = record.getTopic();
        log.info("Начало сессии 'receive-and-stop' для топика '{}' с таймаутом {} мс", topic, pollTimeout.toMillis());
        consumer.startListening(topic, pollTimeout);
        try {
            Thread.sleep(pollTimeout.toMillis() + 500); // Добавляем небольшой запас
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Ожидание в 'receiveRecords' было прервано.", e);
        }
        consumer.stopListening(topic);
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
        consumer.startListening(record.getTopic(), pollTimeout);
        return this;
    }

    /**
     * Останавливает прослушивание топика.
     *
     * @return текущий экземпляр {@code KafkaExecutor}.
     * @throws IllegalStateException если консьюмер или топик не настроены.
     */
    public KafkaExecutor stopListening() {
        validateConsumerAndTopic();
        consumer.stopListening(record.getTopic());
        return this;
    }

    /**
     * Печатает все записи для указанного топика (через внешний утилитный принтер).
     *
     * @return текущий экземпляр KafkaExecutor
     */
    public KafkaExecutor printAllRecords() {
        KafkaRecordsPrinter.printAllRecords(this.recordsManager);
        return this;
    }

    /**
     * Возвращает все полученные записи для текущего топика.
     *
     * @return список записей {@code ConsumerRecord<String, String>}.
     * @throws IllegalStateException если консьюмер или топик не настроены.
     */
    public List<ConsumerRecord<String, String>> getAllRecords() {
        validateConsumerAndTopic();
        return consumer.getAllRecords(record.getTopic());
    }

    /**
     * Возвращает все полученные записи для указанного топика в их "сыром" виде.
     *
     * @param topic Имя топика.
     * @return Список записей {@code ConsumerRecord<?, ?>}.
     */
    public List<ConsumerRecord<?, ?>> getReceivedRecords(String topic) {
        requireNonBlank(topic, "Имя топика не может быть пустым.");
        return this.recordsManager.getRecords(topic);
    }

    /**
     * Возвращает отфильтрованный список записей по ключу.
     *
     * @param key Ключ для фильтрации.
     * @return Список отфильтрованных записей.
     */
    public List<ConsumerRecord<String, String>> getRecordsByKey(String key) {
        return filterRecordsBy(record -> Objects.equals(key, record.key()));
    }

    /**
     * Возвращает отфильтрованный список записей по заголовку.
     *
     * @param headerKey   Ключ заголовка.
     * @param headerValue Значение заголовка.
     * @return Список отфильтрованных записей.
     */
    public List<ConsumerRecord<String, String>> getRecordsByHeader(String headerKey, String headerValue) {
        return filterRecordsBy(record -> Objects.equals(headerValue, getHeaderValue(record, headerKey)));
    }

    /**
     * Находит первую запись, удовлетворяющую условию.
     *
     * @param condition Условие для проверки.
     * @return {@link ConsumerRecord} или {@code null}, если запись не найдена.
     */
    public ConsumerRecord<String, String> getRecordByCondition(Condition condition) {
        return getRecordsByCondition(condition).stream().findFirst().orElse(null);
    }

    /**
     * Находит все записи, удовлетворяющие условию.
     *
     * @param condition Условие для проверки.
     * @return Список отфильтрованных записей.
     */
    public List<ConsumerRecord<String, String>> getRecordsByCondition(Condition condition) {
        validateConsumerAndTopic();
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
     * Преобразует тело первой полученной записи в объект заданного класса (через JSON).
     *
     * @param tClass Класс для десериализации.
     * @param <T>    Целевой тип.
     * @return Экземпляр класса {@code T} или {@code null}, если записей нет.
     */
    public <T> T getRecordAs(Class<T> tClass) {
        return getAllRecords().stream()
                .findFirst()
                .map(ConsumerRecord::value)
                .map(json -> JsonUtils.fromJson(json, tClass))
                .orElse(null);
    }

    /**
     * Преобразует тело первой записи, удовлетворяющей условию, в объект (через JSON).
     *
     * @param tClass    Класс для десериализации.
     * @param condition Условие для поиска.
     * @param <T>       Целевой тип.
     * @return Экземпляр класса {@code T} или {@code null}, если запись не найдена.
     */
    public <T> T getRecordAs(Class<T> tClass, Condition condition) {
        return getRecordsByCondition(condition).stream()
                .findFirst()
                .map(ConsumerRecord::value)
                .map(json -> JsonUtils.fromJson(json, tClass))
                .orElse(null);
    }

    /**
     * Преобразует тела всех полученных записей в список объектов (через JSON).
     *
     * @param tClass Класс для десериализации.
     * @param <T>    Целевой тип.
     * @return Список экземпляров класса {@code T}.
     */
    public <T> List<T> getRecordsAsList(Class<T> tClass) {
        return getAllRecords().stream()
                .map(ConsumerRecord::value)
                .map(json -> JsonUtils.fromJson(json, tClass))
                .collect(Collectors.toList());
    }

    /**
     * Преобразует тела записей, удовлетворяющих условию, в список объектов (через JSON).
     *
     * @param tClass    Класс для десериализации.
     * @param condition Условие для фильтрации.
     * @param <T>       Целевой тип.
     * @return Список экземпляров класса {@code T}.
     */
    public <T> List<T> getRecordsAsList(Class<T> tClass, Condition condition) {
        return getRecordsByCondition(condition).stream()
                .map(ConsumerRecord::value)
                .map(json -> JsonUtils.fromJson(json, tClass))
                .collect(Collectors.toList());
    }

    /**
     * Находит записи в указанном топике, используя произвольный десериализатор.
     *
     * @param topic        Имя топика.
     * @param deserializer Функция для преобразования "сырой" записи в объект типа {@code T}.
     * @param <T>          Целевой тип.
     * @return Список объектов типа {@code T}.
     */
    public <T> List<T> getRecordsAs(String topic, Function<ConsumerRecord<?, ?>, T> deserializer) {
        return getReceivedRecords(topic).stream().map(deserializer).collect(Collectors.toList());
    }

    /**
     * Возвращает поток записей из указанного топика, отфильтрованный по предикату.
     *
     * @param topic     Имя топика.
     * @param predicate Условие для фильтрации.
     * @return Поток записей {@code Stream<ConsumerRecord<?, ?>>}.
     */
    public Stream<ConsumerRecord<?, ?>> findRecords(String topic, Predicate<ConsumerRecord<?, ?>> predicate) {
        return getReceivedRecords(topic).stream().filter(predicate);
    }

    /**
     * Выполняет проверку полученных сообщений с помощью набора условий.
     *
     * @param conditions Набор условий для валидации.
     * @return текущий экземпляр {@code KafkaExecutor}.
     * @throws AssertionError если проверка не пройдена.
     */
    public KafkaExecutor shouldHave(Condition... conditions) {
        validateConsumerAndTopic();
        List<ConsumerRecord<String, String>> records = getAllRecords();
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
        return getAllRecords().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * Закрывает все активные слушатели и очищает ресурсы, связанные с {@link KafkaListenerManager}.
     * Этот метод вызывается автоматически при использовании try-with-resources или может быть вызван явно.
     */
    @Override
    public void close() {
        if (this.listenerManager != null) {
            log.info("Завершение работы KafkaExecutor: инициирована остановка KafkaListenerManager.");
            this.listenerManager.shutdown();
            log.info("KafkaListenerManager успешно остановлен.");
        }
    }
}
