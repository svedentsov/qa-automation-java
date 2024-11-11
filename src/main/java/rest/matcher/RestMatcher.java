package rest.matcher;

import io.restassured.http.ContentType;
import io.restassured.specification.Argument;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.hamcrest.Matcher;
import rest.matcher.condition.Condition;
import rest.matcher.condition.body.*;
import rest.matcher.condition.composite.AllOfCondition;
import rest.matcher.condition.composite.AnyOfCondition;
import rest.matcher.condition.composite.NOfCondition;
import rest.matcher.condition.composite.NotCondition;
import rest.matcher.condition.cookie.CookieExistsCondition;
import rest.matcher.condition.cookie.CookieMatcherCondition;
import rest.matcher.condition.cookie.CookieStringCondition;
import rest.matcher.condition.header.*;
import rest.matcher.condition.status.*;
import rest.matcher.condition.time.ResponseTimeCondition;
import rest.matcher.condition.time.ResponseTimeMatchesCondition;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Утилитный класс для создания условий проверки HTTP-ответов.
 * Предоставляет методы для создания различных условий проверки заголовков, тела ответа, куки и прочего.
 */
@UtilityClass
public class RestMatcher {

    // ------------------- Status Code Conditions -------------------

    /**
     * Проверяет код состояния ответа.
     *
     * @param code ожидаемый код состояния
     * @return условие для проверки кода состояния
     */
    public static Condition statusCode(int code) {
        return new StatusCodeCondition(code);
    }

    /**
     * Проверяет, что код состояния находится в заданном диапазоне.
     *
     * @param startInclusive начало диапазона (включительно)
     * @param endInclusive   конец диапазона (включительно)
     * @return условие для проверки диапазона кода состояния
     */
    public static Condition statusCodeInRange(int startInclusive, int endInclusive) {
        return new StatusCodeRangeCondition(startInclusive, endInclusive);
    }

    /**
     * Проверяет, что код состояния принадлежит заданному классу (1xx, 2xx, 3xx, 4xx, 5xx).
     *
     * @param statusClass класс кода состояния (например, 2 для 2xx)
     * @return условие для проверки класса кода состояния
     */
    public static Condition statusCodeClass(int statusClass) {
        return new StatusCodeClassCondition(statusClass);
    }

    /**
     * Проверяет, что код состояния является успешным (2xx).
     *
     * @return условие для проверки успешного кода состояния
     */
    public static Condition isSuccessful2xx() {
        return new ResponseIsSuccessCondition();
    }

    /**
     * Проверяет, что ответ является перенаправлением (код состояния 3xx).
     *
     * @return условие для проверки перенаправления
     */
    public static Condition isRedirect3xx() {
        return new ResponseIsRedirectCondition();
    }

    /**
     * Проверяет, что код состояния является ошибкой клиента (4xx).
     *
     * @return условие для проверки кода состояния ошибки клиента
     */
    public static Condition isClientError4xx() {
        return new ResponseIsClientErrorCondition();
    }

    /**
     * Проверяет, что код состояния является ошибкой сервера (5xx).
     *
     * @return условие для проверки кода состояния ошибки сервера
     */
    public static Condition isServerError5xx() {
        return new ResponseIsServerErrorCondition();
    }

    /**
     * Проверяет строку состояния ответа.
     *
     * @param line ожидаемая строка состояния
     * @return условие для проверки строки состояния
     */
    public static Condition statusLine(String line) {
        return new StatusLineCondition(line);
    }

    // ------------------- Header Conditions -------------------

    /**
     * Проверяет значение заголовка.
     *
     * @param headerName    имя заголовка
     * @param expectedValue ожидаемое значение
     * @return условие для проверки значения заголовка
     */
    public static Condition header(String headerName, String expectedValue) {
        return new HeaderStringCondition(headerName, expectedValue);
    }

    /**
     * Проверяет значение заголовка с использованием Hamcrest Matcher.
     *
     * @param headerName имя заголовка
     * @param matcher    Hamcrest Matcher для проверки значения
     * @return условие для проверки значения заголовка
     */
    public static Condition header(String headerName, Matcher<?> matcher) {
        return new HeaderMatcherCondition(headerName, matcher);
    }

    /**
     * Проверяет наличие заголовка в ответе.
     *
     * @param headerName имя заголовка
     * @return условие для проверки наличия заголовка
     */
    public static Condition headerExists(String headerName) {
        return new HeaderExistsCondition(headerName);
    }

    /**
     * Проверяет отсутствие заголовка в ответе.
     *
     * @param headerName имя заголовка
     * @return условие для проверки отсутствия заголовка
     */
    public static Condition headerAbsent(String headerName) {
        return new HeaderAbsentCondition(headerName);
    }

    /**
     * Проверяет, что значение заголовка соответствует заданному регулярному выражению.
     *
     * @param headerName имя заголовка
     * @param pattern    регулярное выражение
     * @return условие для проверки значения заголовка по шаблону
     */
    public static Condition headerMatchesPattern(String headerName, Pattern pattern) {
        return new HeaderMatchesPatternCondition(headerName, pattern);
    }

    /**
     * Проверяет, что заголовок содержит указанный текст.
     *
     * @param headerName имя заголовка
     * @return условие для проверки содержания заголовка
     */
    public static Condition headerContains(String headerName) {
        return new HeaderContainsCondition(headerName);
    }

    /**
     * Проверяет тип содержимого ответа.
     *
     * @param contentType ожидаемый тип содержимого
     * @return условие для проверки типа содержимого
     */
    public static Condition contentType(ContentType contentType) {
        return new ContentTypeCondition(contentType);
    }

    /**
     * Проверяет кодировку содержимого ответа.
     *
     * @param encoding ожидаемая кодировка
     * @return условие для проверки кодировки содержимого
     */
    public static Condition contentEncoding(String encoding) {
        return new ContentEncodingCondition(encoding);
    }

    // ------------------- Cookie Conditions -------------------

    /**
     * Проверяет значение куки.
     *
     * @param cookieName    имя куки
     * @param expectedValue ожидаемое значение
     * @return условие для проверки значения куки
     */
    public static Condition cookie(String cookieName, String expectedValue) {
        return new CookieStringCondition(cookieName, expectedValue);
    }

    /**
     * Проверяет значение куки с использованием Hamcrest Matcher.
     *
     * @param cookieName имя куки
     * @param matcher    Hamcrest Matcher для проверки значения
     * @return условие для проверки значения куки
     */
    public static Condition cookie(String cookieName, Matcher<?> matcher) {
        return new CookieMatcherCondition(cookieName, matcher);
    }

    /**
     * Проверяет наличие куки в ответе.
     *
     * @param cookieName имя куки
     * @return условие для проверки наличия куки
     */
    public static Condition cookieExists(String cookieName) {
        return new CookieExistsCondition(cookieName);
    }

    // ------------------- Body Conditions -------------------

    /**
     * Проверяет тело ответа с использованием Hamcrest Matcher.
     *
     * @param matcher Hamcrest Matcher для проверки тела
     * @return условие для проверки тела ответа
     */
    public static Condition body(Matcher<?> matcher) {
        return new BodyMatcherCondition(matcher);
    }

    /**
     * Проверяет тело ответа с использованием списка аргументов и Hamcrest Matcher.
     *
     * @param arguments список аргументов
     * @param matcher   Hamcrest Matcher для проверки
     * @return условие для проверки тела ответа
     */
    public static Condition body(List<Argument> arguments, Matcher<?> matcher) {
        return new BodyMatcherListArgsCondition(arguments, matcher);
    }

    /**
     * Проверяет значение по указанному пути в теле ответа с использованием Hamcrest Matcher.
     *
     * @param path    путь в теле ответа (JSON или XML)
     * @param matcher Hamcrest Matcher для проверки значения
     * @return условие для проверки значения по пути в теле ответа
     */
    public static Condition body(String path, Matcher<?> matcher) {
        return new BodyMatcherPathCondition(path, matcher);
    }

    /**
     * Проверяет значение по указанному пути в теле ответа с использованием списка аргументов и Hamcrest Matcher.
     *
     * @param path      путь в теле ответа (JSON или XML)
     * @param arguments список аргументов
     * @param matcher   Hamcrest Matcher для проверки значения
     * @return условие для проверки значения по пути с аргументами
     */
    public static Condition body(String path, List<Argument> arguments, Matcher<?> matcher) {
        return new BodyMatcherPathArgsCondition(path, arguments, matcher);
    }

    /**
     * Проверяет, что тело ответа заканчивается на указанный суффикс.
     *
     * @param suffix ожидаемый суффикс
     * @return условие для проверки конца тела ответа
     */
    public static Condition bodyEndsWith(String suffix) {
        return new BodyEndsWithCondition(suffix);
    }

    /**
     * Проверяет, что тело ответа пустое.
     *
     * @return условие для проверки пустоты тела ответа
     */
    public static Condition bodyIsEmpty() {
        return new BodyIsEmptyCondition();
    }

    /**
     * Проверяет, что тело ответа начинается с указанного префикса.
     *
     * @param prefix ожидаемый префикс
     * @return условие для проверки начала тела ответа
     */
    public static Condition bodyStartsWith(String prefix) {
        return new BodyStartsWithCondition(prefix);
    }

    /**
     * Проверяет, что тело ответа содержит указанный текст.
     *
     * @param text текст для проверки
     * @return условие для проверки содержимого тела
     */
    public static Condition bodyContains(String text) {
        return new BodyStringCondition(text);
    }

    /**
     * Проверяет, что тело ответа содержит указанный текст без учета регистра.
     *
     * @param text текст для проверки
     * @return условие для проверки содержимого тела без учета регистра
     */
    public static Condition bodyContainsIgnoringCase(String text) {
        return new BodyContainsStringIgnoringCaseCondition(text);
    }

    /**
     * Проверяет, что тело ответа соответствует заданному регулярному выражению.
     *
     * @param pattern регулярное выражение
     * @return условие для проверки соответствия тела шаблону
     */
    public static Condition bodyMatchesPattern(Pattern pattern) {
        return new BodyMatchesPatternCondition(pattern);
    }

    /**
     * Проверяет, что тело ответа может быть десериализовано в указанный класс.
     *
     * @param clazz класс для десериализации
     * @param <T>   тип класса
     * @return условие для проверки возможности десериализации
     */
    public static <T> Condition canDeserializeTo(Class<T> clazz) {
        return new CanDeserializeCondition<>(clazz);
    }

    /**
     * Проверяет, что тело ответа соответствует заданной JSON-схеме.
     *
     * @param schemaFile файл с JSON-схемой
     * @return условие для проверки соответствия тела JSON-схеме
     */
    public static Condition bodyMatchesJsonSchema(File schemaFile) {
        return new JsonSchemaCondition(schemaFile);
    }

    /**
     * Проверяет, что JSON-путь существует в теле ответа.
     *
     * @param jsonPath JSON-путь для проверки
     * @return условие для проверки существования JSON-пути
     */
    public static Condition bodyJsonPathExists(String jsonPath) {
        return new BodyJsonPathExistsCondition(jsonPath);
    }

    /**
     * Проверяет, что значение по заданному JSON-пути соответствует условию.
     *
     * @param jsonPath JSON-путь
     * @param matcher  Hamcrest Matcher для проверки значения
     * @return условие для проверки значения по JSON-пути
     */
    public static Condition bodyJsonPath(String jsonPath, Matcher<?> matcher) {
        return new BodyJsonPathValueCondition(jsonPath, matcher);
    }

    /**
     * Проверяет, что размер тела ответа соответствует заданному условию.
     *
     * @param matcher Hamcrest Matcher для проверки размера тела
     * @return условие для проверки размера тела ответа
     */
    public static Condition bodySize(Matcher<Integer> matcher) {
        return new BodySizeCondition(matcher);
    }

    /**
     * Проверяет, что тело ответа является валидным JSON.
     *
     * @return условие для проверки валидности JSON тела ответа
     */
    public static Condition bodyIsJson() {
        return new BodyIsJsonCondition();
    }

    /**
     * Проверяет, что тело ответа является валидным XML.
     *
     * @return условие для проверки валидности XML тела ответа
     */
    public static Condition bodyIsXml() {
        return new BodyIsXmlCondition();
    }

    /**
     * Проверяет, что тело ответа содержит все указанные строки.
     *
     * @param strings список строк, которые должны присутствовать
     * @return условие для проверки содержания тела ответа
     */
    public static Condition bodyContainsAll(List<String> strings) {
        return new BodyContainsAllStringsCondition(strings);
    }

    /**
     * Проверяет, что тело ответа содержит любую из указанных строк.
     *
     * @param strings список строк, из которых хотя бы одна должна присутствовать
     * @return условие для проверки содержания тела ответа
     */
    public static Condition bodyContainsAny(List<String> strings) {
        return new BodyContainsAnyStringCondition(strings);
    }

    // ------------------- Response Time Conditions -------------------

    /**
     * Проверяет, что время ответа меньше заданного значения.
     *
     * @param maxDuration максимальная длительность ответа
     * @return условие для проверки времени ответа
     */
    public static Condition responseTimeLessThan(Duration maxDuration) {
        return new ResponseTimeCondition(maxDuration);
    }

    /**
     * Проверяет время ответа с использованием Hamcrest Matcher.
     *
     * @param matcher Hamcrest Matcher для проверки времени ответа в миллисекундах
     * @return условие для проверки времени ответа
     */
    public static Condition responseTime(Matcher<Long> matcher) {
        return new ResponseTimeMatchesCondition(matcher);
    }

    // ------------------- Composite Conditions -------------------

    /**
     * Создает композитное условие, которое проходит только в том случае, если выполнены все указанные условия (логическое И).
     *
     * @param conditions массив условий
     * @return композитное условие
     */
    public static Condition allOf(@NonNull Condition... conditions) {
        return new AllOfCondition(conditions);
    }

    /**
     * Создает композитное условие, которое проходит, если выполнено любое из указанных условий (логическое ИЛИ).
     *
     * @param conditions массив условий
     * @return композитное условие
     */
    public static Condition anyOf(@NonNull Condition... conditions) {
        return new AnyOfCondition(conditions);
    }

    /**
     * Создает композитное условие, которое инвертирует результат указанных условий (логическое НЕ).
     *
     * @param conditions массив условий
     * @return композитное условие
     */
    public static Condition not(@NonNull Condition... conditions) {
        return new NotCondition(conditions);
    }

    /**
     * Создает композитное условие, которое проходит, если выполнены хотя бы N из указанных условий.
     *
     * @param n          минимальное количество условий, которые должны быть выполнены
     * @param conditions массив условий
     * @return композитное условие
     */
    public static Condition nOf(int n, @NonNull Condition... conditions) {
        return new NOfCondition(n, conditions);
    }
}
