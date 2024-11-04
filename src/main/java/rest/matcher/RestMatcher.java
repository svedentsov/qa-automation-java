package rest.matcher;

import io.restassured.http.ContentType;
import io.restassured.specification.Argument;
import lombok.experimental.UtilityClass;
import org.hamcrest.Matcher;
import rest.matcher.condition.*;

import java.time.Duration;
import java.util.List;

/**
 * Утилитный класс для создания условий проверки HTTP-ответов.
 * Предоставляет методы для создания различных условий проверки заголовков, тела ответа, куки и прочего.
 */
@UtilityClass
public class RestMatcher {

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
     * Проверяет, что тело ответа равно ожидаемому объекту.
     *
     * @param expectedObject ожидаемый объект
     * @param <T>            тип объекта
     * @return условие для проверки равенства тела ответа
     */
    public static <T> Condition bodyEquals(T expectedObject) {
        return new BodyEqualsCondition<>(expectedObject);
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
     * Проверяет длину содержимого ответа с использованием Hamcrest Matcher.
     *
     * @param matcher Hamcrest Matcher для проверки длины
     * @return условие для проверки длины содержимого
     */
    public static Condition contentLength(Matcher<Long> matcher) {
        return new ContentLengthCondition(matcher);
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
     * Проверяет, что заголовок содержит указанный текст.
     *
     * @param headerName имя заголовка
     * @return условие для проверки содержания заголовка
     */
    public static Condition headerContains(String headerName) {
        return new HeaderContainsCondition(headerName);
    }

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
     * Проверяет строку состояния ответа.
     *
     * @param line ожидаемая строка состояния
     * @return условие для проверки строки состояния
     */
    public static Condition statusLine(String line) {
        return new StatusLineCondition(line);
    }

    /**
     * Проверяет, что время ответа меньше заданного значения.
     *
     * @param maxDuration максимальная длительность ответа
     * @return условие для проверки времени ответа
     */
    public static Condition timeLessThan(Duration maxDuration) {
        return new ResponseTimeCondition(maxDuration);
    }
}
