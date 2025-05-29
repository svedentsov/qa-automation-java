# Инструкция по работе с матчерами

Матчеры (matchers) и условия (conditions) - инструменты, позволяющие писать автотесты понятным и декларативным образом.
Они позволяют выразить проверки в стиле «все значения соответствуют шаблону» или «число больше заданного порога» без
ручного написания логики проверок и выбросов исключений. В результате тесты становятся проще в чтении и сопровождении, а
также легче расширяются.

## Оглавление

1. [Что такое матчеры и условия?](#что-такое-матчеры-и-условия)
2. [Содержимое пакета](#содержимое-пакета)
3. [Принципы использования матчеров](#принципы-использования-матчеров)
4. [Примеры типовых проверок](#примеры-типовых-проверок)
   - [Проверка строк](#проверка-строк)
   - [Проверка JSON с помощью JsonPath](#проверка-json-с-помощью-jsonpath)
   - [Проверка количества и свойств записей](#проверка-количества-и-свойств-записей)
   - [Проверка чисел](#проверка-чисел)
   - [Проверка временных меток](#проверка-временных-меток)
   - [Комбинации условий](#комбинации-условий)
5. [Создание собственных условий](#создание-собственных-условий)
6. [Обучение работе с матчерами](#обучение-работе-с-матчерами)
   - [Шаг 1: Начните с простых проверок](#шаг-1-начните-с-простых-проверок)
   - [Шаг 2: Добавляйте сложность](#шаг-2-добавляйте-сложность)
   - [Шаг 3: Создавайте собственные условия](#шаг-3-создавайте-собственные-условия)
   - [Шаг 4: Применение в реальных проектах](#шаг-4-применение-в-реальных-проектах)
   - [Шаг 5: Практика и расширение](#шаг-5-практика-и-расширение)
7. [Заключение](#заключение)

## Что такое матчеры и условия?

При тестировании сложных систем часто недостаточно простого равенства значений. Нужно проверять соответствие шаблону,
уникальность, формат JSON, временные промежутки и многое другое. Матчеры скрывают низкоуровневую проверочную логику за
удобными методами. Вы пишете тесты в стиле:

*«У нас есть записи, каждая из которых удовлетворяет следующим условиям: ключ соответствует выражению `user_.*`,
значение содержит `active`, количество записей не менее пяти»* - всё это пишется буквально одним-двумя вызовами методов.

Таким образом, итогом применения матчеров становится более понятный, гибкий и расширяемый тестовый код, не зависящий от
внутренних деталей проверки.

## Содержимое пакета

Пакет `kafka.matcher` предоставляет инфраструктуру для удобных и мощных проверок:

```plaintext
kafka.matcher/
├── KafkaMatcher.java               # DSL-класс для создания условий
├── KafkatValidator.java            # Класс-валидатор, проверяющий ответ по набору условий
├── condition/
│   ├── Condition.java              # Интерфейс для проверки одной записи
│   └── Conditions.java             # Интерфейс для проверки набора записей
└── assertions/
    ├── CompositeAssertions.java    # Логические операции над условиями (AND, OR, NOT, N из M)
    ├── JsonPathConditions.java     # Условия для проверки JSON по JsonPath
    ├── NumberAssertions.java       # Условия для проверки числовых значений
    ├── RecordAssertions.java       # Условия для проверки списков записей
    ├── StringAssertions.java       # Условия для проверки строковых значений
    └── TimestampAssertions.java    # Условия для проверки временных меток
```

### Описание классов

- **Condition.java** Функциональный интерфейс для проверки одной записи.
  ```java
  @FunctionalInterface
  public interface Condition {
      void check(ConsumerRecord<String, String> record);
  }
  ```
- **Conditions.java** Функциональный интерфейс для проверки набора записей.
  ```java
  @FunctionalInterface
  public interface Conditions {
      void check(List<ConsumerRecord<String, String>> records);
  }
  ```
- **KafkaMatcher.java** Предоставляет статические методы для создания условий с удобным DSL.
  ```java
  public class KafkaMatcher {
      public static Condition value(StringCondition sc) { ... }
      public static Condition key(StringCondition sc) { ... }
      public static Conditions records(RecordCondition rc) { ... }
      // Другие методы
  }
  ```
- **assertions/** Пакет содержит различные классы с готовыми условиями для разных типов данных.
    - **StringAssertions.java** Условия для строк: `isEmpty()`, `contains(String)`, `matchesRegex(String)`, и др.
    - **NumberAssertions.java** Условия для чисел: `greaterThan(int)`, `lessThan(int)`, `inRange(int, int)`, и др.
    - **JsonPathConditions.java** Условия для проверки значений, извлеченных по JsonPath: `isString()`, `containsJson(String)`, `matchesRegexJson(String)`, и др.
    - **RecordAssertions.java** Условия для проверки списков записей: `countGreater(int)`, `allKeysUnique()`, `recordsOrdered(...)`, и др.
    - **TimestampAssertions.java** Условия для временных меток: `after(Instant)`, `before(Instant)`, `inRange(Instant, Instant)`, и др.
    - **CompositeAssertions.java** Логические операции над условиями: `and(Condition...)`, `or(Condition...)`, `not(Condition...)`, `nOf(int, Condition...)`.
    - **MatcherUtils.java**  Вспомогательные методы и утилиты для работы с матчерами, например, для объединения условий или преобразования данных.

## Принципы использования матчеров

1. **Читаемость**: Вместо ручных проверок через `if-else` вы используете декларативные методы:
   ```java
   shouldHave(value(matchesRegex("user_.*")));
   ```
2. **Повторное использование**: Однажды созданный matcher можно применять во множестве тестов:
   ```java
   shouldHave(records(allKeysUnique()));
   ```
3. **Гибкость**: Легко комбинировать условия, использовать логические операции:
   ```java
   shouldHave(value(anyOf(equalsTo("admin"), matchesRegex("user_.*"))));
   ```
4. **Понятные сообщения об ошибках**: При неудаче теста вы получите информативное сообщение о том, какая проверка не
   прошла и почему.

## Примеры типовых проверок

### Проверка строк

Пусть нужно проверить, что ключ сообщения начинается с `user_`:
```java
shouldHave(key(matchesRegex("user_.*")));
```
Проверка, что значение сообщения содержит подстроку `testUser`:
```java
shouldHave(value(contains("testUser")));
```

### Проверка JSON с помощью JsonPath

Предположим, в сообщении есть JSON, и нам нужно проверить поле `status`:
```java
shouldHave(value("$.status", containsJson("active")));
```
Если `status` не `active`, получим понятное исключение.

### Проверка количества и свойств записей

Убедимся, что количество записей больше 5:
```java
shouldHave(records(countGreater(5)));
```
Проверка уникальности ключей во всех сообщениях:
```java
shouldHave(records(allKeysUnique()));
```

### Проверка чисел

```java
shouldHave(value("$.age", isNumber()));
shouldHave(value("$.age", numberGreater(17)));
```
Проверяем, что `age` - число и больше 17.

### Проверка временных меток

```java
Instant start = Instant.now().minusSeconds(60);
shouldHave(timestamp(after(start)));
```
Убеждаемся, что временная метка сообщения позже `start`.

### Комбинации условий

```java
shouldHave(value(allOf(isNotEmpty(), contains("test"), lengthGreaterThan(10))));
```
Здесь мы проверяем, что значение не пустое, содержит подстроку `test` и длина больше 10 символов.

## Создание собственных условий

Если готовые матчеры не решают вашу задачу, вы можете создать свой `Condition`:
```java
Condition customCondition = record -> {
    if (!record.value().startsWith("custom_")) {
        throw new AssertionError("Значение должно начинаться с 'custom_'");
    }
};

shouldHave(customCondition);
```

Или оформить её более красиво в виде статического метода:
```java
public static Condition valueStartsWithCustom() {
    return record -> {
        if (!record.value().startsWith("custom_")) {
            throw new AssertionError("Значение должно начинаться с 'custom_'");
        }
    };
}

// Использование
shouldHave(valueStartsWithCustom());
```
Это позволяет выразить любую специфическую для вашего проекта логику.

## Обучение работе с матчерами

### Шаг 1: Начните с простых проверок

Сначала замените ручные проверки в вашем тесте на матчеры.
**Было (императивно):**
```java
if (!record.value().contains("testUser")) {
    throw new AssertionError("Значение не содержит testUser");
}
```
**Стало (декларативно):**
```java
shouldHave(value(contains("testUser")));
```

### Шаг 2: Добавляйте сложность

Когда освоитесь с базовыми проверками, переходите к более сложным:
- Используйте JsonPath для проверки вложенных полей JSON.
- Применяйте логические операции:
 ```java
  shouldHave(value(anyOf(equalsTo("admin"), matchesRegex("user_.*"))));
  ```

### Шаг 3: Создавайте собственные условия

Когда стандартных матчеров будет недостаточно, создайте свой `Condition` или даже целую библиотеку своих Assertions,
подходящих под ваш бизнес-кейс.

```java
public static Condition startsWithCustom(String prefix) {
    return record -> {
        if (!record.value().startsWith(prefix)) {
            throw new AssertionError("Значение должно начинаться с '" + prefix + "'");
        }
    };
}

// Использование
shouldHave(startsWithCustom("custom_"));
```

### Шаг 4: Применение в реальных проектах

Интегрируйте матчеры в ваши тестовые фреймворки (JUnit, TestNG) для проверки различных источников данных.

```java
@Test
public void testKafkaMessage() {
    new KafkaSteps()
        .setProducerType(ContentType.STRING_FORMAT)
        .sendRecord("test-topic", "Hello World", "user_123");
    
    new KafkaSteps()
        .setConsumerType(ContentType.STRING_FORMAT)
        .startListening("test-topic")
        .checkRecordsPresence("test-topic")
        .validateRecords(new KafkaSteps().getAllRecords("test-topic"))
        .shouldHave(
            value(contains("Hello")),
            key(matchesRegex("user_\\d+"))
        );
}
```

### Шаг 5: Практика и расширение

Чем чаще вы используете матчеры, тем более естественным станет такой подход к тестированию. Добавляйте новые проверки,
улучшайте существующие, делитесь опытом с коллегами. Со временем ваш набор условий станет библиотекой полезных
инструментов, ускоряющих написание и поддержку автотестов.

## Примеры комплексных автотестов

### Пример 1: Проверка Avro-сообщения

Предположим, у нас есть Avro-схема для сообщений о пользователях, и мы хотим проверить, что отправленные сообщения
соответствуют определенным критериям.

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
    .sendRecord("avro-topic", /* Avro Record */);

new KafkaSteps()
    .setConsumerType(ContentType.AVRO_FORMAT)
    .startListening("avro-topic")
    .checkRecordsPresence("avro-topic")
    .validateRecords(new KafkaSteps().getAllRecords("avro-topic"))
    .shouldHave(
        value("$.status", containsJson("active")),
        key(matchesRegex("user_.*"))
    );
```

### Пример 2: Проверка строковых сообщений с заголовками

Отправляем строковое сообщение с заголовком и проверяем его наличие и содержание.

```java
new KafkaSteps()
    .setProducerType(ContentType.STRING_FORMAT)
    .sendRecordWithHeader("string-topic", "Test Message", "source", "unit-test");

new KafkaSteps()
    .setConsumerType(ContentType.STRING_FORMAT)
    .startListening("string-topic")
    .checkRecordsPresence("string-topic")
    .validateRecords(new KafkaSteps().getAllRecords("string-topic"))
    .shouldHave(
        value(contains("Test")),
        records(countEqual(1)),
        records(getRecordsByHeader("source", "unit-test"))
    );
```

### Пример 3: Проверка списка записей на уникальность и соответствие шаблону

Убедимся, что все ключи уникальны и соответствуют определенному паттерну.

```java
new KafkaSteps()
    .setProducerType(ContentType.STRING_FORMAT)
    .sendRecord("unique-topic", "First Message", "user_1")
    .sendRecord("unique-topic", "Second Message", "user_2")
    .sendRecord("unique-topic", "Third Message", "user_3");

new KafkaSteps()
    .setConsumerType(ContentType.STRING_FORMAT)
    .startListening("unique-topic")
    .checkRecordsPresence("unique-topic")
    .validateRecords(new KafkaSteps().getAllRecords("unique-topic"))
    .shouldHave(
        records(allKeysUnique()),
        key(matchesRegex("user_\\d+"))
    );
```

## Заключение

Матчеры и условия - это фундамент для декларативных, понятных и гибких проверок в автотестах. Используя готовые
Assertions из пакета `kafka.matcher` и создавая собственные, вы сможете:

- Повысить читаемость и наглядность тестов.
- Избавиться от дублирования и ручной логики проверок.
- Гибко комбинировать и расширять проверки.
- Легко обучать новых участников команды написанию тестов.

Применяйте матчеры в своих проектах, и ваши тесты станут лучше, а процесс их создания - проще и приятнее. Это особенно
ценно на больших проектах, где автотестов много, а требования к валидации данных постоянно расширяются. Построив удобный
и читаемый фреймворк для тестирования с использованием матчеров, вы сможете быстро и надежно покрывать тестами самые
сложные сценарии.
