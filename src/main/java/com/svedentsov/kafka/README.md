# Документация по Kafka Test Framework

Данный каталог обеспечивает удобный и гибкий подход к работе с Kafka при написании автоматизированных тестов. Он включает в себя конфигурацию, фабрики клиентов, сервисы продюсеров и консьюмеров, вспомогательные классы для управления сообщениями, а также удобный DSL для проверки данных.

Основная идея – скрыть низкоуровневые детали взаимодействия с Kafka за удобным и читаемым fluent-интерфейсом класса KafkaExecutor, предоставив возможность быстро писать понятные и поддерживаемые автотесты для сложных сценариев.

## Структура проекта

Структура пакетов и классов разделена на логические модули:

- **`kafka.config`**:
  Хранение и загрузка конфигурации Kafka.
  - `KafkaConfig`: Интерфейс для доступа к свойствам из `kafka.properties` с помощью библиотеки OWNER.
  - `KafkaConfigProvider`: Собирает и предоставляет `Properties` для нативных клиентов Kafka, применяя в том числе SSL-настройки.
  - `KafkaConfigListener`: Конфигурация для слушателей (такие параметры, как таймауты, политики перезапуска при ошибках и т.д.).

- **`kafka.enums`**:
  Общие перечисления для удобной работы.
  - `TopicType`: Типы содержимого сообщений (`STRING`, `AVRO`).
  - `Topic`: Перечисление доступных топиков.
  - `StartStrategyType`: Стратегии старта чтения сообщений консьюмером (с начала, с конца, с определённого времени и т.д.).

- **`kafka.exception`**:
  Пользовательские исключения для более точной обработки ошибок.
  - `KafkaListenerException`: Общее исключение для слушателей, с вложенными классами для ошибок конфигурации (`ConfigurationException`), жизненного цикла (`LifecycleException`) и обработки сообщений (`ProcessingException`).
  - `KafkaSendingException`: Исключение, возникающее при ошибках отправки сообщений.

- **`kafka.factory`**:
  Фабрики для инкапсуляции логики создания и управления экземплярами клиентов Kafka.
  - `ProducerFactory`, `ConsumerFactory`: Интерфейсы фабрик.
  - `ProducerFactoryDefault`, `ConsumerFactoryDefault`: Реализации, которые создают и кешируют продюсеров или создают консьюмеров по запросу.
  - `KafkaServiceFactory`: Фабрика, создающая сервисы (`KafkaProducerService`, `KafkaConsumerService`), которые являются более высоким уровнем абстракции над клиентами.

- **`kafka.helper`**:
  Ключевые классы, составляющие ядро фреймворка.
  - `KafkaExecutor`: **Основной класс-фасад** для взаимодействия с Kafka в тестах. Предоставляет fluent-API для отправки, получения и проверки сообщений.
  - `KafkaListenerManager`: Управляет жизненным циклом фоновых слушателей топиков (`KafkaTopicListener`).
  - `KafkaTopicListener`: Низкоуровневый "работник", который в отдельном потоке слушает один конкретный топик.
  - `KafkaRecordsManager`: Потокобезопасное хранилище для всех полученных сообщений.
  - `KafkaRecordsPrinter`: Утилита для вывода полученных сообщений в консоль в читаемом виде.
  - `KafkaMatcher`: DSL-класс для удобного создания условий (`Condition`) для поиска и валидации сообщений по ключу, телу (включая JsonPath), заголовкам и другим атрибутам.
  - `helper.strategy`: Пакет с реализациями различных стратегий старта чтения топика.

- **`kafka.model`**:
  Модель, описывающая запись (`Record`) для отправки: топик, ключ, значение, заголовки, Avro-схема.
  - `Record`: Изменяемый (mutable) объект для формирования сообщения перед отправкой.

- **`kafka.processor`**:
  Реализации стратегии обработки полученных сообщений.
  - `RecordProcessor`: Интерфейс обработчика.
  - `RecordProcessorString`, `RecordProcessorAvro`: Конкретные реализации, которые добавляют полученные записи в `KafkaRecordsManager`.

- **`kafka.service`**:
  Сервисный слой, который предоставляет абстракцию над нативными клиентами Kafka.
  - `KafkaProducerService`, `KafkaConsumerService`: Интерфейсы сервисов.
  - Реализации для `STRING` и `AVRO`, инкапсулирующие логику отправки и управления прослушиванием.

- **`kafka.utils`**:
  Утилиты для вспомогательных операций.
  - `RecordLoader`: Загрузка тела сообщения из файла.
  - `ValidationUtils`: Утилиты для валидации входных данных.

## Подход к тестированию

1. **Модульность и читаемость**:
   Весь низкоуровневый код скрыт за понятными методами `KafkaExecutor`. Тесты пишутся в декларативном стиле.
2. **Гибкость**:
   Можно быстро переключиться между `STRING` и `AVRO`. Легко менять конфигурацию и добавлять сложные условия проверки с
   помощью `KafkaMatcher`.
3. **Асинхронность под капотом**:
   Фреймворк полностью асинхронный, но предоставляет простые методы (`awaitRecord`, `receiveRecords`) для работы в
   синхронном стиле, что идеально подходит для тестов.

## Пример использования для простого теста

Предположим, нужно проверить, что при отправке сообщения в `my-topic`, оно успешно доставляется и содержит нужный текст.

```java
try (KafkaExecutor kafka = KafkaExecutor.createDefault()) {
    // Отправка
    kafka.setProducerType(TopicType.STRING)
         .setTopic("my-topic")
         .setRecordKey("user-123")
         .setRecordBody("{\"user\":\"testUser\",\"action\":\"login\"}")
         .sendRecord();

    // Получение и проверка
    List<ConsumerRecord<String, String>> records = kafka.setConsumerType(TopicType.STRING)
        .setTopic("my-topic")
        // Упрощенный метод: запустить, подождать немного, остановить
        .receiveRecords() 
        .getAllRecords();

    // Простая проверка с помощью AssertJ или JUnit
    assertThat(records).anyMatch(r -> r.value().contains("testUser") && r.key().equals("user-123"));
}
```

## Пример комплексного автотеста с Avro и `KafkaMatcher`

Более сложный сценарий: отправляем Avro-сообщение и хотим асинхронно дождаться его появления, а затем проверить его структуру по JsonPath и ключ по регулярному выражению.

### Шаги теста

1. Настроить и отправить Avro-запись.
2. Запустить прослушивание топика в фоновом режиме.
3. Асинхронно ожидать появления сообщения, которое удовлетворяет сложным условиям, в течение таймаута.
4. Проверить, что поле `status` в JSON-представлении Avro равно `"active"`.
5. Проверить, что ключ сообщения начинается с `user-id_`.

### Пример кода

```java
import static com.svedentsov.kafka.helper.KafkaMatcher.*;
import static com.svedentsov.matcher.StringAssertions.matchesRegex;
import static com.svedentsov.matcher.PropertyAssertions.is;

// ...

try (KafkaExecutor kafka = KafkaExecutor.createDefault()) {
    
    // 1. Готовим и отправляем Avro
    Schema schema = new Schema.Parser().parse("..."); // ваша Avro-схема
    GenericRecord avroRecord = new GenericData.Record(schema);
    avroRecord.put("userId", "user-id-abc");
    avroRecord.put("status", "active");

    kafka.setProducerType(TopicType.AVRO)
         .setTopic("avro-topic")
         .setRecordKey("user-id-abc-123")
         .setRecordBody(avroRecord)
         .sendRecordAsync(); // Асинхронная отправка

    // 2. Запускаем прослушивание в фоне
    kafka.setConsumerType(TopicType.AVRO)
         .setTopic("avro-topic")
         .startListening();

    // 3. Формируем условия и асинхронно ждем запись
    Condition<ConsumerRecord<String, String>> expectedRecordCondition = Conditions.and(
        key(matchesRegex("user-id_.*")),
        value("$.status", is("active")) // Проверка JsonPath в теле сообщения
    );

    ConsumerRecord<String, String> receivedRecord = kafka.awaitRecord(expectedRecordCondition, Duration.ofSeconds(10));

    // 4. Если запись найдена, тест успешен. Иначе будет TimeoutException.
    System.out.println("Найдена запись: " + receivedRecord.value());
}
```

Данный комплексный тест:
- Отправляет Avro-сообщение.
- Запускает фоновое прослушивание.
- Используя `awaitRecord` и `KafkaMatcher`, декларативно описывает, какое именно сообщение он ожидает.
- Эффективно проверяет и структуру, и содержимое данных, не погружаясь в детали десериализации или парсинга.

## Заключение

Архитектура данного фреймворка упрощает жизнь авторам тестов, предоставляя:
- Понятный и читаемый fluent-API (`KafkaExecutor`).
- Лёгкое масштабирование и гибкость в работе с форматами данных.
- Мощный набор готовых инструментов для асинхронного ожидания и проверки сообщений.

Это особенно ценно на больших проектах, где автотестов много, а требования к валидации данных постоянно расширяются.
