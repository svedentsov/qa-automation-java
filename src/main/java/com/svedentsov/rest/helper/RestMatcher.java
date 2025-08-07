package com.svedentsov.rest.helper;

import com.svedentsov.matcher.Condition;
import com.svedentsov.matcher.JsonReader;
import com.svedentsov.matcher.PropertyMatcher;
import com.svedentsov.matcher.assertions.BooleanAssertions.BooleanCondition;
import com.svedentsov.matcher.assertions.CompositeAssertions;
import com.svedentsov.matcher.assertions.InstantAssertions.InstantCondition;
import com.svedentsov.matcher.assertions.ListAssertions.ListCondition;
import com.svedentsov.matcher.assertions.NumberAssertions.NumberCondition;
import com.svedentsov.matcher.assertions.PropertyAssertions.PropertyCondition;
import com.svedentsov.matcher.assertions.StringAssertions.StringCondition;
import io.restassured.response.Response;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.util.List;
import java.util.function.Function;

/**
 * Утилитарный класс, предоставляющий унифицированный DSL для создания условий ({@link Condition})
 * для проверки HTTP-ответов. Этот класс является мостом между объектом Response и
 * общей библиотекой матчеров в `com.svedentsov.matcher.assertions`.
 */
@UtilityClass
public class RestMatcher {

    /**
     * Создает условие для проверки статусного кода ответа.
     *
     * @param condition Условие из {@link com.svedentsov.matcher.assertions.NumberAssertions} для проверки кода (Integer).
     * @return {@link Condition} для проверки статусного кода.
     */
    public static Condition<Response> status(@NonNull NumberCondition<Integer> condition) {
        return PropertyMatcher.value(Response::getStatusCode, condition);
    }

    /**
     * Создает условие для проверки значения заголовка.
     *
     * @param headerName Имя заголовка.
     * @param condition  Условие из {@link com.svedentsov.matcher.assertions.StringAssertions} для проверки значения заголовка.
     * @return {@link Condition} для проверки заголовка.
     */
    public static Condition<Response> header(@NonNull String headerName, @NonNull StringCondition condition) {
        return PropertyMatcher.value(response -> response.getHeader(headerName), condition);
    }

    /**
     * Создает условие для проверки значения cookie.
     *
     * @param cookieName Имя cookie.
     * @param condition  Условие из {@link com.svedentsov.matcher.assertions.StringAssertions} для проверки значения cookie.
     * @return {@link Condition} для проверки cookie.
     */
    public static Condition<Response> cookie(@NonNull String cookieName, @NonNull StringCondition condition) {
        return PropertyMatcher.value(response -> response.getCookie(cookieName), condition);
    }

    /**
     * Создает условие для проверки времени ответа в миллисекундах.
     *
     * @param condition Условие из {@link com.svedentsov.matcher.assertions.NumberAssertions} для проверки времени (Long).
     * @return {@link Condition} для проверки времени ответа.
     */
    public static Condition<Response> time(@NonNull NumberCondition<Long> condition) {
        return PropertyMatcher.value(Response::getTime, condition);
    }

    /**
     * Создает условие для проверки всего тела ответа как единой строки.
     *
     * @param condition Условие из {@link com.svedentsov.matcher.assertions.StringAssertions} для проверки тела.
     * @return {@link Condition} для проверки тела ответа.
     */
    public static Condition<Response> body(@NonNull StringCondition condition) {
        return PropertyMatcher.value(response -> response.getBody().asString(), condition);
    }

    /**
     * Создает условие для проверки строкового значения, извлеченного из JSON-ответа по JSONPath.
     *
     * @param jsonPath  Путь JSONPath к полю.
     * @param condition Условие из {@link com.svedentsov.matcher.assertions.StringAssertions}.
     * @return {@link Condition} для проверки значения по JSONPath.
     */
    public static Condition<Response> body(@NonNull String jsonPath, @NonNull StringCondition condition) {
        return value(response -> JsonReader.extractValue(response, jsonPath, String.class), condition);
    }

    /**
     * Создает условие для проверки булева значения, извлеченного из JSON-ответа по JSONPath.
     *
     * @param jsonPath  Путь JSONPath к полю.
     * @param condition Условие из {@link com.svedentsov.matcher.assertions.BooleanAssertions}.
     * @return {@link Condition} для проверки значения по JSONPath.
     */
    public static Condition<Response> body(@NonNull String jsonPath, @NonNull BooleanCondition condition) {
        return value(response -> JsonReader.extractValue(response, jsonPath, Boolean.class), condition);
    }

    /**
     * Создает условие для проверки числового значения, извлеченного из JSON-ответа по JSONPath.
     *
     * @param jsonPath  Путь JSONPath к полю.
     * @param condition Условие из {@link com.svedentsov.matcher.assertions.NumberAssertions}.
     * @param type      Класс ожидаемого числового типа (например, Integer.class).
     * @param <T>       Тип числа ({@link Number} и {@link Comparable}).
     * @return {@link Condition} для проверки значения по JSONPath.
     */
    public static <T extends Number & Comparable<T>> Condition<Response> body(
            @NonNull String jsonPath,
            @NonNull NumberCondition<T> condition,
            @NonNull Class<T> type) {
        return value(response -> JsonReader.extractValue(response, jsonPath, type), condition);
    }

    /**
     * Создает условие для проверки произвольного свойства (Object) из JSON-ответа по JSONPath.
     *
     * @param jsonPath  Путь JSONPath к полю.
     * @param condition Условие из {@link com.svedentsov.matcher.assertions.PropertyAssertions}.
     * @return {@link Condition} для проверки значения по JSONPath.
     */
    public static Condition<Response> body(@NonNull String jsonPath, @NonNull PropertyCondition<?> condition) {
        @SuppressWarnings({"unchecked", "rawtypes"})
        PropertyCondition rawCondition = condition;
        return value(response -> JsonReader.extractValue(response, jsonPath, Object.class), rawCondition);
    }

    /**
     * Создает условие для проверки временной метки (Instant), извлеченной из JSON-ответа по JSONPath.
     * Ожидается, что значение по {@code jsonPath} является строкой в формате ISO-8601.
     *
     * @param jsonPath  Путь JSONPath к полю с датой.
     * @param condition Условие из {@link com.svedentsov.matcher.assertions.InstantAssertions}.
     * @return {@link Condition} для проверки.
     */
    public static Condition<Response> body(@NonNull String jsonPath, @NonNull InstantCondition condition) {
        return value(response -> {
            String stringValue = JsonReader.extractValue(response, jsonPath, String.class);
            return stringValue != null ? Instant.parse(stringValue) : null;
        }, condition);
    }

    /**
     * Создает условие для проверки списка сущностей, извлеченного из JSON-ответа по JSONPath.
     *
     * @param jsonPath  Путь JSONPath к массиву.
     * @param condition Условие из {@link com.svedentsov.matcher.assertions.ListAssertions}.
     * @param <T>       Тип элементов списка.
     * @return {@link Condition} для проверки.
     */
    @SuppressWarnings("unchecked")
    public static <T> Condition<Response> body(@NonNull String jsonPath, @NonNull ListCondition<T> condition) {
        return response -> {
            List<?> rawList = JsonReader.extractValue(response, jsonPath, List.class);
            condition.check((List<T>) rawList);
        };
    }

    /**
     * Комбинирует несколько условий для Response с помощью логического "И".
     *
     * @param conditions Перечень условий для проверки Response.
     * @return Составное {@link Condition}.
     */
    @SafeVarargs
    public static Condition<Response> and(@NonNull Condition<Response>... conditions) {
        return CompositeAssertions.and(conditions);
    }

    /**
     * Универсальный метод для создания условия проверки значения, извлекаемого из {@link Response}.
     *
     * @param getter    Функция для извлечения значения из ответа.
     * @param condition Условие для проверки извлеченного значения.
     * @param <R>       Тип проверяемого значения.
     * @return {@link Condition} для проверки.
     */
    public static <R> Condition<Response> value(
            @NonNull Function<? super Response, ? extends R> getter,
            @NonNull Condition<? super R> condition) {
        return response -> PropertyMatcher.value(getter, condition).check(response);
    }
}
