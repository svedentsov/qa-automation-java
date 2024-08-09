package rest.matcher;

import io.restassured.http.ContentType;
import io.restassured.specification.Argument;
import org.hamcrest.Matcher;
import rest.matcher.condition.*;

import java.util.List;

/**
 * Класс предоставляет статические методы для создания различных условий,
 * которые могут быть использованы для проверки содержимого HTTP-ответа.
 */
public class RestMatcher {

    /**
     * Создаёт условие для проверки типа содержимого ответа.
     *
     * @param contentType тип содержимого ответа
     * @return условие для проверки типа содержимого
     */
    public static Condition contentType(ContentType contentType) {
        return new ContentTypeCondition(contentType);
    }

    /**
     * Создаёт условие для проверки кода статуса ответа.
     *
     * @param code код статуса ответа
     * @return условие для проверки кода статуса
     */
    public static Condition statusCode(int code) {
        return new StatusCodeCondition(code);
    }

    /**
     * Создаёт условие для проверки строки состояния ответа.
     *
     * @param line строка состояния ответа
     * @return условие для проверки строки состояния
     */
    public static Condition statusLine(String line) {
        return new StatusLineCondition(line);
    }

    /**
     * Создаёт условие для проверки заголовка ответа с ожидаемым значением.
     *
     * @param headerName    имя заголовка
     * @param expectedValue ожидаемое значение заголовка
     * @return условие для проверки заголовка с ожидаемым значением
     */
    public static Condition header(String headerName, String expectedValue) {
        return new HeaderStringCondition(headerName, expectedValue);
    }

    /**
     * Создаёт условие для проверки заголовка ответа с использованием матчера.
     *
     * @param headerName имя заголовка
     * @param matcher    матчер для значения заголовка
     * @return условие для проверки заголовка с использованием матчера
     */
    public static Condition header(String headerName, Matcher matcher) {
        return new HeaderMatcherCondition(headerName, matcher);
    }

    /**
     * Создаёт условие для проверки наличия заголовка в ответе.
     *
     * @param expectedHeaderName ожидаемое имя заголовка
     * @return условие для проверки наличия заголовка
     */
    public static Condition header(String expectedHeaderName) {
        return new HeaderContainsCondition(expectedHeaderName);
    }

    /**
     * Создаёт условие для проверки тела ответа с использованием матчера.
     *
     * @param matcher матчер для тела ответа
     * @return условие для проверки тела ответа
     */
    public static Condition body(Matcher matcher) {
        return new BodyMatcherCondition(matcher);
    }

    /**
     * Создаёт условие для проверки тела ответа по указанному пути с использованием матчера.
     *
     * @param path    путь к полю в теле ответа
     * @param matcher матчер для значения поля
     * @return условие для проверки тела ответа по указанному пути
     */
    public static Condition body(String path, Matcher matcher) {
        return new BodyMatcherPathCondition(path, matcher);
    }

    /**
     * Создаёт условие для проверки тела ответа с использованием списка аргументов и матчера.
     *
     * @param arguments список аргументов для проверки
     * @param matcher   матчер для значения
     * @return условие для проверки тела ответа с аргументами
     */
    public static Condition body(List<Argument> arguments, Matcher matcher) {
        return new BodyMatcherListArgsCondition(arguments, matcher);
    }

    /**
     * Создаёт условие для проверки тела ответа по указанному пути с использованием списка аргументов и матчера.
     *
     * @param path      путь к полю в теле ответа
     * @param arguments список аргументов для проверки
     * @param matcher   матчер для значения
     * @return условие для проверки тела ответа по указанному пути с аргументами
     */
    public static Condition body(String path, List<Argument> arguments, Matcher matcher) {
        return new BodyMatcherPathArgsCondition(path, arguments, matcher);
    }

    /**
     * Создаёт условие для проверки, что тело ответа содержит заданный текст.
     *
     * @param text ожидаемый текст в теле ответа
     * @return условие для проверки наличия текста в теле ответа
     */
    public static Condition bodyContains(String text) {
        return new BodyStringCondition(text);
    }
}
