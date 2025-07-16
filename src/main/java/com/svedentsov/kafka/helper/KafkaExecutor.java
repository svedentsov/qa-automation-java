package com.svedentsov.kafka.helper;

import com.svedentsov.kafka.config.KafkaConfig;
import com.svedentsov.kafka.config.KafkaConfigProvider;
import com.svedentsov.kafka.enums.TopicType;
import com.svedentsov.kafka.factory.ConsumerFactoryDefault;
import com.svedentsov.kafka.factory.KafkaServiceFactory;
import com.svedentsov.kafka.factory.ProducerFactory;
import com.svedentsov.kafka.factory.ProducerFactoryDefault;
import com.svedentsov.kafka.helper.strategy.StartStrategyOptions;
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
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.svedentsov.kafka.config.KafkaConfigListener.EnvConfig.testing;
import static com.svedentsov.kafka.utils.ValidationUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * Главный класс-фасад для выполнения операций с Kafka в рамках автоматизированных тестов.
 * <p>Предоставляет текучий (fluent) API для настройки, отправки и получения сообщений.
 * Каждый экземпляр {@code KafkaExecutor} является изолированной сессией, управляющей
 * собственными фабриками и менеджерами, что предотвращает конфликты при параллельном
 * выполнении тестов.
 * <p><b>Важно:</b> Экземпляр этого класса является stateful (хранит состояние) и не потокобезопасен.
 * Рекомендуется создавать новый экземпляр для каждого тестового сценария.
 * <p>Класс реализует {@link AutoCloseable}, что позволяет использовать его в блоке
 * try-with-resources для автоматического и гарантированного освобождения всех ресурсов,
 * включая продюсеров, консьюмеров и пулы потоков.
 *
 * <p><b>Пример использования:</b>
 * <pre>{@code
 * try (KafkaExecutor executor = KafkaExecutor.createDefault()) {
 * // Отправка сообщения
 * executor.setProducerType(TopicType.STRING)
 * .setTopic("my-topic")
 * .setRecordKey("my-key")
 * .setRecordBody("Hello, Kafka!")
 * .sendRecord();
 *
 * // Запуск прослушивания
 * executor.setConsumerType(TopicType.STRING)
 * .setTopic("my-topic")
 * .startListening();
 *
 * // Ожидание и получение записи
 * ConsumerRecord<String, String> received = executor.awaitRecord(key("my-key"), Duration.ofSeconds(10));
 * assertThat(received.value()).isEqualTo("Hello, Kafka!");
 *
 * // Остановка прослушивания
 * executor.stopListening();
 * } catch (Exception e) {
 * // обработка исключений
 * }
 * }</pre>
 */
@Slf4j
public class KafkaExecutor implements AutoCloseable {

    private final ProducerFactory producerFactory;
    private final KafkaServiceFactory serviceFactory;
    private final KafkaListenerManager listenerManager;
    private final KafkaRecordsManager recordsManager;
    private KafkaProducerService producer;
    private KafkaConsumerService consumer;
    private final Record record = new Record();
    private Duration pollTimeout = Duration.ofMillis(200);

    /**
     * Создает экземпляр KafkaExecutor с предоставленными зависимостями.
     * Это основной конструктор, поощряющий использование Dependency Injection.
     *
     * @param producerFactory Фабрика для создания Kafka-продюсеров.
     * @param serviceFactory  Фабрика для создания Kafka-сервисов (продюсер/консьюмер).
     * @param listenerManager Менеджер для управления жизненным циклом фоновых слушателей.
     * @param recordsManager  Менеджер для хранения и доступа к полученным записям.
     */
    public KafkaExecutor(ProducerFactory producerFactory, KafkaServiceFactory serviceFactory, KafkaListenerManager listenerManager, KafkaRecordsManager recordsManager) {
        this.producerFactory = requireNonNull(producerFactory, "ProducerFactory не может быть null.");
        this.serviceFactory = requireNonNull(serviceFactory, "KafkaServiceFactory не может быть null.");
        this.listenerManager = requireNonNull(listenerManager, "KafkaListenerManager не может быть null.");
        this.recordsManager = requireNonNull(recordsManager, "KafkaRecordsManager не может быть null.");
    }

    /**
     * Статический метод-фабрика для создания экземпляра KafkaExecutor с конфигурацией по умолчанию.
     * Упрощает создание объекта в тестах, скрывая внутреннюю инициализацию зависимостей.
     *
     * @return Новый экземпляр {@code KafkaExecutor}.
     */
    public static KafkaExecutor createDefault() {
        var kafkaConfig = ConfigFactory.create(KafkaConfig.class, System.getProperties());
        var configProvider = new KafkaConfigProvider(kafkaConfig);
        var producerFactory = new ProducerFactoryDefault(configProvider);
        var consumerFactory = new ConsumerFactoryDefault(configProvider);
        var recordsManager = new KafkaRecordsManager();
        var listenerManager = new KafkaListenerManager(testing(), consumerFactory, recordsManager);
        var serviceFactory = new KafkaServiceFactory(producerFactory);
        return new KafkaExecutor(producerFactory, serviceFactory, listenerManager, recordsManager);
    }

    /**
     * Устанавливает тип продюсера. Этот метод должен быть вызван перед отправкой сообщений.
     *
     * @param type Тип контента (например, {@link TopicType#STRING}).
     * @return Текущий экземпляр {@code KafkaExecutor} для построения цепочки вызовов.
     */
    public KafkaExecutor setProducerType(TopicType type) {
        requireNonNull(type, "Тип для продюсера не может быть null");
        this.producer = serviceFactory.createProducer(type);
        return this;
    }

    /**
     * Устанавливает тип консьюмера. При вызове создается соответствующий сервис,
     * которому передаются менеджеры, принадлежащие этому экземпляру KafkaExecutor.
     *
     * @param type Тип контента (например, {@link TopicType#AVRO}).
     * @return Текущий экземпляр {@code KafkaExecutor} для построения цепочки вызовов.
     */
    public KafkaExecutor setConsumerType(TopicType type) {
        requireNonNull(type, "Тип для консьюмера не может быть null");
        this.consumer = serviceFactory.createConsumer(type, this.listenerManager, this.recordsManager);
        return this;
    }

    /**
     * Устанавливает тайм-аут для операции получения записей (poll).
     *
     * @param millis Тайм-аут в миллисекундах. Должен быть неотрицательным.
     * @return Текущий экземпляр {@code KafkaExecutor}.
     * @throws IllegalArgumentException если {@code millis} отрицательный.
     */
    public KafkaExecutor setPollTimeout(long millis) {
        if (millis < 0) throw new IllegalArgumentException("Таймаут не может быть отрицательным.");
        this.pollTimeout = Duration.ofMillis(millis);
        return this;
    }

    /**
     * Устанавливает имя топика для последующих операций.
     *
     * @param topicName Имя топика. Не может быть {@code null} или пустым.
     * @return Текущий экземпляр {@code KafkaExecutor}.
     */
    public KafkaExecutor setTopic(String topicName) {
        requireNonBlank(topicName, "Имя топика не может быть null или пустым.");
        this.record.setTopic(topicName);
        return this;
    }

    /**
     * Устанавливает номер партиции для отправки записи.
     *
     * @param partition Номер партиции.
     * @return Текущий экземпляр {@code KafkaExecutor}.
     */
    public KafkaExecutor setPartition(int partition) {
        this.record.setPartition(partition);
        return this;
    }

    /**
     * Устанавливает ключ для отправляемой записи.
     *
     * @param key Ключ записи.
     * @return Текущий экземпляр {@code KafkaExecutor}.
     */
    public KafkaExecutor setRecordKey(String key) {
        this.record.setKey(key);
        return this;
    }

    /**
     * Добавляет один заголовок к отправляемой записи.
     *
     * @param name  Имя заголовка, не может быть пустым.
     * @param value Значение заголовка.
     * @return Текущий экземпляр {@code KafkaExecutor}.
     */
    public KafkaExecutor setRecordHeader(String name, Object value) {
        requireNonBlank(name, "Имя заголовка не может быть null или пустым.");
        this.record.getHeaders().put(name, value);
        return this;
    }

    /**
     * Устанавливает заголовки для отправляемой записи.
     *
     * @param headers Список заголовков Kafka.
     * @return Текущий экземпляр {@code KafkaExecutor}.
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
     * @return Текущий экземпляр {@code KafkaExecutor}.
     */
    public KafkaExecutor setAvroSchema(Schema schema) {
        requireNonNull(schema, "Avro схема не может быть null.");
        this.record.setAvroSchema(schema);
        return this;
    }

    /**
     * Устанавливает тело (value) для отправляемой записи.
     *
     * @param value Значение: {@link String} или Avro-объект (например, {@code GenericRecord}).
     * @return Текущий экземпляр {@code KafkaExecutor}.
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
     * @param filePath Путь к файлу.
     * @return Текущий экземпляр {@code KafkaExecutor}.
     */
    public KafkaExecutor loadRecordBodyFromFile(String filePath) {
        this.record.setValue(RecordLoader.loadRecordValue(filePath));
        return this;
    }

    /**
     * Синхронно отправляет сконфигурированную запись в Kafka.
     *
     * @return Текущий экземпляр {@code KafkaExecutor}.
     * @throws IllegalStateException Если продюсер или топик не настроены.
     */
    public KafkaExecutor sendRecord() {
        validateProducerAndTopic();
        producer.sendRecord(record);
        return this;
    }

    /**
     * Асинхронно отправляет сконфигурированную запись в Kafka.
     *
     * @return Текущий экземпляр {@code KafkaExecutor}.
     * @throws IllegalStateException Если продюсер или топик не настроены.
     */
    public KafkaExecutor sendRecordAsync() {
        validateProducerAndTopic();
        log.info("Асинхронная отправка записи в топик '{}': {}", record.getTopic(), record);
        producer.sendRecordAsync(record);
        return this;
    }

    /**
     * Синхронно отправляет коллекцию записей.
     *
     * @param records Коллекция объектов {@link Record} для отправки.
     * @return Текущий экземпляр {@code KafkaExecutor}.
     */
    public KafkaExecutor sendRecords(Collection<Record> records) {
        validateProducerAndTopic();
        records.forEach(producer::sendRecord);
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
     * @return Текущий экземпляр {@code KafkaExecutor}.
     * @throws IllegalStateException Если консьюмер не установлен или топик не задан.
     */
    public KafkaExecutor startListening() {
        validateConsumerAndTopic();
        consumer.startListening(record.getTopic(), pollTimeout);
        return this;
    }

    /**
     * Останавливает прослушивание топика.
     *
     * @param strategyOptions Опции, определяющие, с какого места начать чтение (например, с начала, с конца, с определенного времени).
     * @return Текущий экземпляр {@code KafkaExecutor}.
     */
    public KafkaExecutor startListening(StartStrategyOptions strategyOptions) {
        validateConsumerAndTopic();
        consumer.startListening(record.getTopic(), pollTimeout, strategyOptions);
        return this;
    }

    /**
     * Останавливает прослушивание топика. Если слушатель не был запущен, ничего не делает.
     *
     * @return Текущий экземпляр {@code KafkaExecutor}.
     * @throws IllegalStateException Если консьюмер или топик не настроены.
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
     * Ожидает появления хотя бы одной записи, удовлетворяющей условию, в течение заданного времени.
     *
     * @param condition Условие для проверки (например, {@code key("my-key")}).
     * @param timeout   Максимальное время ожидания.
     * @return Первая найденная {@link ConsumerRecord}.
     * @throws TimeoutException Если запись не найдена за указанное время.
     */
    public ConsumerRecord<String, String> awaitRecord(Condition condition, Duration timeout) throws TimeoutException {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            ConsumerRecord<String, String> record = getRecordByCondition(condition);
            if (record != null) {
                return record;
            }
            try {
                Thread.sleep(200); // Интервал опроса
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Ожидание записи было прервано", e);
            }
        }
        throw new TimeoutException("Не удалось дождаться записи, удовлетворяющей условию, за " + timeout.toSeconds() + " секунд.");
    }

    /**
     * Ожидает получения минимально необходимого количества записей в течение заданного времени.
     *
     * @param minCount Минимальное количество записей, которое нужно получить.
     * @param timeout  Максимальное время ожидания.
     * @return Список полученных записей.
     * @throws TimeoutException Если не удалось собрать нужное количество записей за указанное время.
     */
    public List<ConsumerRecord<String, String>> awaitRecords(int minCount, Duration timeout) throws TimeoutException {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            List<ConsumerRecord<String, String>> records = getAllRecords();
            if (records.size() >= minCount) {
                return records;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Ожидание записей было прервано", e);
            }
        }
        throw new TimeoutException("Не удалось дождаться " + minCount + " записей за " + timeout.toSeconds() + " секунд.");
    }

    /**
     * Возвращает все полученные записи для текущего топика, которые хранятся в менеджере.
     *
     * @return Список записей {@code ConsumerRecord<String, String>}.
     * @throws IllegalStateException Если консьюмер или топик не настроены.
     */
    public List<ConsumerRecord<String, String>> getAllRecords() {
        validateConsumerAndTopic();
        return consumer.getAllRecords(record.getTopic());
    }

    /**
     * Возвращает "сырые" записи для указанного топика.
     *
     * @param topic Имя топика.
     * @return Список записей {@code ConsumerRecord<?, ?>}.
     */
    public List<ConsumerRecord<?, ?>> getReceivedRecords(String topic) {
        requireNonBlank(topic, "Имя топика не может быть пустым.");
        return this.recordsManager.getRecords(topic);
    }

    /**
     * Возвращает записи, отфильтрованные по ключу.
     *
     * @param key Ключ для фильтрации.
     * @return Список отфильтрованных записей.
     */
    public List<ConsumerRecord<String, String>> getRecordsByKey(String key) {
        return filterRecordsBy(record -> Objects.equals(key, record.key()));
    }

    /**
     * Возвращает записи, отфильтрованные по значению заголовка.
     *
     * @param headerKey   Ключ заголовка.
     * @param headerValue Значение заголовка.
     * @return Список отфильтрованных записей.
     */
    public List<ConsumerRecord<String, String>> getRecordsByHeader(String headerKey, String headerValue) {
        return filterRecordsBy(record -> Objects.equals(headerValue, getHeaderValue(record, headerKey)));
    }

    /**
     * Находит первую запись, удовлетворяющую условию, среди уже полученных.
     *
     * @param condition Условие для проверки.
     * @return {@link ConsumerRecord} или {@code null}, если запись не найдена.
     */
    public ConsumerRecord<String, String> getRecordByCondition(Condition condition) {
        return getRecordsByCondition(condition).stream().findFirst().orElse(null);
    }

    /**
     * Находит все записи, удовлетворяющие условию, среди уже полученных.
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
     * @return Текущий экземпляр {@code KafkaExecutor}.
     * @throws AssertionError Если проверка не пройдена.
     */
    public KafkaExecutor shouldHave(Condition... conditions) {
        validateConsumerAndTopic();
        List<ConsumerRecord<String, String>> records = getAllRecords();
        EntityValidator.of(records).shouldHave(conditions);
        return this;
    }

    /**
     * Очищает все записи, полученные из всех топиков. Полезно для изоляции тестов.
     *
     * @return Текущий экземпляр {@code KafkaExecutor}.
     */
    public KafkaExecutor clearReceivedRecords() {
        log.info("Очистка всех полученных записей.");
        this.recordsManager.clearAllRecords();
        return this;
    }

    /**
     * Очищает записи, полученные из конкретного топика.
     *
     * @param topic Имя топика, записи которого нужно очистить.
     * @return Текущий экземпляр {@code KafkaExecutor}.
     */
    public KafkaExecutor clearReceivedRecords(String topic) {
        requireNonBlank(topic, "Имя топика для очистки не может быть пустым.");
        log.info("Очистка полученных записей для топика '{}'.", topic);
        this.recordsManager.clearRecords(topic);
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
     * Освобождает все занятые ресурсы. Гарантированно останавливает фоновые слушатели
     * и закрывает соединения с Kafka (продюсеры). Этот метод должен вызываться всегда
     * после завершения работы с экземпляром, предпочтительно с использованием try-with-resources.
     */
    @Override
    public void close() {
        log.info("Завершение работы KafkaExecutor: начало освобождения ресурсов...");
        try {
            if (this.listenerManager != null) {
                log.debug("Остановка KafkaListenerManager...");
                this.listenerManager.shutdown();
                log.info("KafkaListenerManager успешно остановлен.");
            }
        } catch (Exception e) {
            log.error("Ошибка при остановке KafkaListenerManager.", e);
        }

        try {
            if (this.producerFactory != null) {
                log.debug("Закрытие ProducerFactory...");
                this.producerFactory.close();
                log.info("ProducerFactory успешно закрыта.");
            }
        } catch (Exception e) {
            log.error("Ошибка при закрытии ProducerFactory.", e);
        }
        log.info("Все ресурсы KafkaExecutor были освобождены.");
    }
}
