Данный каталог обеспечивает удобный и гибкий подход к работе с Kafka при написании автоматизированных тестов. Он включает в себя конфигурацию, фабрики клиентов, сервисы продюсеров и консюмеров, вспомогательные классы для управления сообщениями, а также удобный DSL для проверки данных (matcher-ы, condition-ы).

Основная идея – скрыть низкоуровневые детали взаимодействия с Kafka за удобным и читаемым интерфейсом, предоставив возможность быстро писать понятные и поддерживаемые автотесты для сложных сценариев, в том числе UI, API или комплексных интеграционных тестов.

## Структура проекта

Структура пакетов и классов разделена на логические модули:

- **kafka.config**:  
  Хранение и загрузка конфигурации Kafka (адреса брокеров, SSL-настройки, идентификаторы групп и т.д.) из внешних файлов.  
  - *KafkaConfig*  
  - *KafkaConfigBuilder*
  
- **kafka.enums**:  
  Общие перечисления для удобной работы (типы контента, имена топиков).  
  - *ContentType*  
  - *Topics*

- **kafka.exception**:  
  Специальные исключения для обработки ошибок сериализации/десериализации и валидации.  
  - *JsonDeserializationException*  
  - *JsonSerializationException*  
  - *ValidationException*

- **kafka.factory**:  
  Фабрика для создания продюсеров и консюмеров нужного типа (строковый, Avro).  
  - *KafkaServiceFactory*

- **kafka.helper**:  
  Вспомогательные классы для запуска и остановки прослушивания топиков, хранения и фильтрации полученных сообщений, упрощения отправки/получения данных.  
  - *KafkaExecutor* – основной класс для взаимодействия с топиками  
  - *KafkaListener*, *KafkaRecordsManager*, *KafkaRecordsPrinter*

- **kafka.matcher** и вложенные пакеты:  
  Предоставляют DSL для проверки сообщений. Можно задавать условия для ключей, значений, заголовков и проч. Есть готовые Assertions для удобных проверок.  
  - *Condition*, *Conditions* – интерфейсы для условий  
  - Набор классов с Assertions для строк, чисел, JsonPath, Timestamp и пр.  
  - *KafkaMatcher* – класс-DSL для быстрого составления проверок

- **kafka.model**:  
  Модель описывающая запись (Record): топик, ключ, значение, заголовки, Avro-схема.  
  - *Record*

- **kafka.pool**:  
  Пул клиентов Kafka (продюсеры и консюмеры), чтобы переиспользовать и не создавать каждый раз заново.  
  - *KafkaClientPool*

- **kafka.service**:  
  Интерфейсы и реализации для работы с продюсером и консюмером.  
  - *KafkaProducerService* (String и Avro)  
  - *KafkaConsumerService* (String и Avro)

- **kafka.steps**:  
  Класс шагов для тестов, представляющий удобный уровень абстракции. Можно вызывать методы вроде `sendRecord()`, `startListening()`, `checkRecordsContainText()` без погружения в детали.  
  - *KafkaSteps*

- **kafka.utils**:  
  Утилиты для сериализации в JSON, загрузки файлов, генерации тестовых данных.  
  - *JsonUtils*  
  - *RecordLoader*

## Подход к тестированию

1. **Модульность и читаемость**:  
   Весь низкоуровневый код (создание продюсеров/консюмеров, сериализация, фильтрация) скрыт за понятными методами. Тесты можно писать, оперируя бизнес-логикой.

2. **Гибкость**:  
   Можно быстро переключиться между строковым и Avro-форматом. Легко менять конфигурацию, добавлять новые условия проверки.

3. **Повторное использование**:  
   Шаги, условия, утилиты, производители и потребители сообщений созданы так, чтобы их можно было использовать в различных проектах и тестовых сценариях.

4. **Интеграция с другими подходами**:  
   Проект можно встроить в любой фреймворк для запуска тестов (TestNG, JUnit), использовать совместно с UI-тестами или интеграционными тестами.

## Пример использования для простого теста

Предположим, нужно проверить, что при отправке сообщения в топик `my-topic`, оно успешно сохраняется и содержит нужный текст:

```java
new KafkaSteps()
    .setProducerType(ContentType.STRING_FORMAT)
    .sendRecord("my-topic", "{\"user\":\"testUser\",\"action\":\"login\"}");

new KafkaSteps()
    .setConsumerType(ContentType.STRING_FORMAT)
    .startListening("my-topic")
    .checkRecordsPresence("my-topic")
    .checkRecordsContainText("my-topic", "testUser");
```

Этот тест:

- Отправляет сообщение в топик `my-topic`.
- Запускает прослушивание топика.
- Проверяет, что в топике есть записи.
- Проверяет, что среди них есть запись с текстом `"testUser"`.

## Пример комплексного автотеста с условием и Avro

Представим более сложный сценарий: у нас есть Avro-схема и мы хотим проверить не только наличие сообщения, но и его структуру по JsonPath, а также соответствие ключа определенному паттерну. Допустим, нам нужно убедиться, что отправленное Avro-сообщение содержит поле `"status": "active"`, а ключ начинается с `"user_"`.

### Шаги теста

1. Настраиваем продюсер на Avro-формат и отправляем Avro-запись.
2. Настраиваем консюмер Avro, запускаем прослушивание топика.
3. Применяем проверку через JsonPath, чтобы убедиться, что `$.status == "active"`.
4. Проверяем, что ключ сообщения соответствует регулярному выражению `user_.*`.

### Пример кода

```java
Schema avroSchema = new Schema.Parser().parse("{\n" +
    "  \"type\": \"record\",\n" +
    "  \"name\": \"UserAction\",\n" +
    "  \"fields\": [\n" +
    "    {\"name\": \"userId\", \"type\": \"string\"},\n" +
    "    {\"name\": \"status\", \"type\": \"string\"}\n" +
    "  ]\n" +
    "}");

new KafkaSteps()
    .setProducerType(ContentType.AVRO_FORMAT)
    .setAvroSchema(avroSchema)
    .setTimeout(5000)
    // предположим, мы заранее подготовили Avro GenericRecord под этот schema
    .sendRecord("avro-topic", /* тут Avro Record, например genericRecord */ );

new KafkaSteps()
    .setConsumerType(ContentType.AVRO_FORMAT)
    .startListening("avro-topic")
    .checkRecordsPresence("avro-topic");

// Теперь более точные проверки через matcher-ы

// Применяем проверку к полученным записям
new KafkaSteps()
    .validateRecords(new KafkaSteps().getAllRecords("avro-topic"))
    .shouldHave(key(matchesRegex("user_.*"))) // Проверить, что ключ должен начинаться на "user_"
    .shouldHave(value("$.status", containsJson("active"))); // Проверить по JsonPath, что значение поле status должно быть "active"
```

Данный комплексный тест:

- Отправляет Avro-сообщение.
- Проверяет, что сообщения появились в топике.
- Используя JsonPath, проверяет значение поля `status`.
- Проверяет, что ключ сообщения соответствует заданному шаблону.

Это демонстрирует мощь и гибкость данного подхода, позволяя проверять как содержимое, так и формат данных, не погружаясь в детали десериализации или парсинга.

## Расширения и дополнительные возможности

- Можно добавить собственные условия (Condition) для специфических проверок.
- Можно использовать Data-Driven подход: генерировать различные данные и проверять их массово.
- Можно интегрироваться с UI-тестами: сперва через API или Kafka-каналы подготовить тестовые данные, а затем проверить их отображение на UI.
- Можно тестировать сложные сценарии, где Kafka – лишь часть цепочки, и проверять итоговое состояние после серии шагов.

## Заключение

Архитектура данного каталога упрощает жизнь авторам тестов, предоставляя:

- Понятный код
- Легкое масштабирование
- Гибкость в работе с форматами данных
- Набор готовых инструментов для проверки сообщений

Это особенно ценно на больших проектах, где автотестов много, а требования к валидации данных постоянно расширяются. Построив один раз удобный и читаемый фреймворк для тестирования Kafka, вы сможете быстро и надежно покрывать тестами самые сложные сценарии.