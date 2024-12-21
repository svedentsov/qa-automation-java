# Инструкция по работе с матчерами для REST API

Матчеры (matchers) и условия (conditions) в контексте REST API позволяют декларативно описывать проверки HTTP-ответов:
коды состояния, заголовки, куки, время ответа, тело в формате JSON/XML и т.д. Это упрощает тесты, делая их более
понятными и легкими в сопровождении.

## Оглавление

1. [Что такое REST API матчеры и условия?](#что-такое-rest-api-матчеры-и-условия)
2. [Структура пакета](#структура-пакета)
3. [Принципы использования](#принципы-использования)
4. [Пример простого теста с RestValidator](#пример-простого-теста-с-restvalidator)
5. [Примеры типовых проверок](#примеры-типовых-проверок)
   - [Проверка статусного кода](#проверка-статусного-кода)
   - [Проверка заголовков](#проверка-заголовков)
   - [Проверка куки](#проверка-куки)
   - [Проверка времени ответа](#проверка-времени-ответа)
   - [Проверка тела ответа (Body)](#проверка-тела-ответа-body)
   - [Композиция условий (CompositeAssertions)](#композиция-условий-compositeassertions)
6. [Создание собственных условий](#создание-собственных-условий)
7. [Обучение работе с матчерами для REST API](#обучение-работе-с-матчерами-для-rest-api)
8. [Заключение](#заключение)

## Что такое REST API матчеры и условия?

При тестировании REST API часто нужно проверять различные аспекты HTTP-ответа: статусный код, заголовки, формат и
структуру тела ответа (JSON/XML), куки, время отклика и т.д. Чтобы не писать вручную каждый раз логику проверок, удобно
использовать готовые матчеры и условия:

- Вызывая методы вида `statusCode(200)`, `headerEquals("Content-Type", "application/json")`, `bodyIsJson()` и т.д., вы
  декларативно описываете требования к ответу.
- Если условие не выполняется, тест завершится с информативным сообщением об ошибке.

Таким образом, тесты становятся более читаемыми, легко масштабируются за счёт готовой библиотеки проверок и не требуют
написания сложного кода внутри каждого теста.

## Структура пакета

```plaintext
rest.matcher/
├── RestMatcher.java                # DSL-класс для удобного создания условий
├── RestValidator.java              # Класс-валидатор, проверяющий ответ по набору условий
├── condition/
│   └── Condition.java              # Базовый функциональный интерфейс для проверки HTTP-ответа
└── assertions/
    ├── BodyAssertions.java         # Условия, связанные с телом ответа (JSON, XML, текст)
    ├── CookieAssertions.java       # Условия, связанные с куками
    ├── HeaderAssertions.java       # Условия, связанные с заголовками
    ├── StatusAssertions.java       # Условия, связанные с кодами состояния
    ├── TimeAssertions.java         # Условия, связанные со временем ответа
    └── CompositeAssertions.java    # Логические операции (AND, OR, NOT, N из M)
```

### Ключевые классы

- **Condition**: Функциональный интерфейс, описывающий единственную проверку, применяемую к объекту `Response` из RestAssured.
- **RestMatcher**: Содержит статические методы для создания условий в стиле DSL, например:
  ```java
  status(isSuccessful2xx());
  header(headerEquals("Content-Type", "application/json"));
  ```
- **RestValidator** Оборачивает `Response` и позволяет вызвать `shouldHave(Condition...)` для применения набора условий.
- **BodyAssertions** Типовые проверки тела (JSON, XML, текст, Base64, JSONPath, валидатор JSON-схемы и т.д.).
- **HeaderAssertions** Проверки заголовков (наличие, равенство, подстрока, соответствие шаблону, тип контента).
- **CookieAssertions** Проверки куки (наличие, значение, регулярное выражение и т.д.).
- **StatusAssertions** Проверки кодов состояния (равенство, диапазон, принадлежность к классам кодов 2xx, 4xx, 5xx и т.д.).
- **TimeAssertions** Проверки времени ответа (меньше/больше некоторого значения, в диапазоне, соответствует Matcher и т.д.).
- **CompositeAssertions** Логические операции для комбинирования нескольких условий (ALL, ANY, NOT, NOf).

## Принципы использования

1. **Читаемость**: Тест с использованием RestValidator становится декларативным. Пример:
   ```java
   new RestValidator(response)
       .shouldHave(
           status(isSuccessful2xx()),
           body(bodyIsJson())
       );
   ```
   Всё сразу видно — мы хотим, чтобы код ответа был успешным и тело было валидным JSON.
2. **Гибкость**: Большое количество готовых матчеров покрывает большинство типовых сценариев. При необходимости легко создать свои собственные.
3. **Повторное использование**: Один и тот же набор условий можно применять в разных тестах, повышая переиспользуемость кода.
4. **Понятные сообщения об ошибках**: При несоответствии условия тест упадёт с чётким описанием, в чём проблема.

## Пример простого теста с RestValidator

Предположим, у нас есть REST-запрос на создание пользователя. Мы ожидаем, что при успешном создании будет возвращён код `201`, в заголовке будет `Location`, а тело будет валидным JSON:

```java
Response response = given()
    .contentType("application/json")
    .body("{\"name\":\"John\",\"email\":\"john@example.com\"}")
    .when()
    .post("/users");

new RestValidator(response)
    .shouldHave(
        status(statusCode(201)),
        header(headerExists("Location")),
        body(bodyIsJson())
    );
```

## Примеры типовых проверок

### Проверка статусного кода

```java
new RestValidator(response)
    .shouldHave(
        status(isSuccessful2xx()) // Проверка, что код 2xx
    );
```
Можно проверить конкретное значение:
```java
status(statusCode(200));
```
Или диапазон:
```java
status(statusCodeBetween(200, 299));
```

### Проверка заголовков

```java
new RestValidator(response)
    .shouldHave(
        header(headerEquals("Content-Type", "application/json")),
        header(headerContains("Server", "Apache"))
    );
```

Наличие заголовка:
```java
header(headerExists("Content-Encoding"));
```

Соответствие рег. выражению:
```java
header(headerValueMatchesRegex("Content-Type", "application\\/json.*"));
```

### Проверка куки

```java
new RestValidator(response)
    .shouldHave(
        cookie(cookieExists("SESSIONID")),
        cookie(cookieEquals("lang", "en_US"))
    );
```
Проверка домена:
```java
cookie(cookieDomainEquals("SESSIONID", "example.com"));
```
Рег. выражение:
```java
cookie(cookieValueMatchesPattern("auth", Pattern.compile("Bearer\\s+.+")));
```

### Проверка времени ответа

```java
new RestValidator(response)
    .shouldHave(
        responseTime(responseTimeLessThan(Duration.ofSeconds(2)))
    );
```
Или проверка в определённом диапазоне:
```java
responseTime(responseTimeBetween(Duration.ofMillis(500), Duration.ofMillis(2000)));
```

### Проверка тела ответа (Body)

Проверка, что тело валидный JSON:
```java
body(bodyIsJson());
```
Проверка JSONPath:
```java
body(bodyJsonPathEquals("name", "John"));
body(bodyJsonPathListSize("items", 3));
```
Проверка схемы:
```java
body(bodyMatchesJsonSchema(new File("user_schema.json")));
```
Проверка XML:
```java
body(bodyIsXml());
```
Проверка соответствия регулярному выражению:
```java
body(bodyMatchesPattern(Pattern.compile(".*success.*")));
```
Проверка Base64, Dates, Email и многое другое.

### Композиция условий (CompositeAssertions)

Допустим, хотим проверить, что либо тело содержит `error`, либо код состояния равен `400`:
```java
anyOf(
    body(bodyContains("error")),
    status(statusCode(400))
);
```
Проверка, что *все* условия соблюдены:
```java
allOf(
    status(isClientError4xx()),
    body(bodyContains("Invalid"))
);
```
Инверсия (NOT):
```java
not(
    status(isServerError5xx())
);
```
Или «хотя бы `n` из них»:
```java
nOf(2,
    body(bodyContains("error")),
    header(headerEquals("X-ErrorCode", "999")),
    status(statusCode(400))
);
```

## Создание собственных условий

Если в готовых наборах нет нужной проверки, вы можете создать собственную. Нужно лишь реализовать `Condition`:

```java
public class CustomBodyCondition implements Condition {
    @Override
    public void check(Response response) {
        // Логика проверки...
        if (!someCondition) {
            throw new AssertionError("Собственная проверка не пройдена");
        }
    }
}
```
Или использовать лямбду:
```java
Condition myCondition = resp -> {
    if (resp.getHeader("X-Custom") == null) {
        throw new AssertionError("X-Custom header must be present");
    }
};
new RestValidator(response).shouldHave(myCondition);
```

## Обучение работе с матчерами для REST API

1. **Начните с простых проверок**: Проверяйте код ответа, небольшое тело JSON, пару заголовков.
2. **Добавляйте сложность**: Учитесь работать с `JSONPath`, проверять XML, Base64, email-адреса, времени ответа и т.д.
3. **Создавайте свои проверки**: Если чего-то не хватает, напишите свой `Condition`.
4. **Используйте композитные условия**: `allOf`, `anyOf`, `not`, `nOf` помогут объединять проверки.
5. **Пишите декларативные тесты**: Видя код теста `shouldHave(...)`, сразу понятно, что проверяется.

## Заключение

Данный пакет `rest.matcher` предоставляет гибкие и готовые к использованию проверки для HTTP-ответов, повышая читаемость
и сопровождаемость тестов REST API. Вы можете быстро и удобно покрыть типовые сценарии, а при необходимости — дополнить
собственными условиями. Плюсы использования:

- Декларативная запись тестов.
- Большое количество готовых проверок для разных аспектов ответа.
- Возможность легко объединять проверки в сложные сценарии.
- Расширяемость за счёт простого интерфейса `Condition`.

Применяя такой подход, вы ускорите написание тестов и повысите их надёжность, а также облегчите обучение новых коллег,
которые смогут сразу понять, какие проверки выполняются, не копаясь в низкоуровневом коде.
