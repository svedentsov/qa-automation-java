package com.svedentsov.rest.helper;

import com.svedentsov.matcher.Condition;
import com.svedentsov.matcher.JsonReader;
import com.svedentsov.matcher.PropertyMatcher;
import com.svedentsov.matcher.assertions.BooleanAssertions.BooleanCondition;
import com.svedentsov.matcher.assertions.CollectionAssertions;
import com.svedentsov.matcher.assertions.CollectionAssertions.CollectionCondition;
import com.svedentsov.matcher.assertions.CompositeAssertions;
import com.svedentsov.matcher.assertions.InstantAssertions;
import com.svedentsov.matcher.assertions.InstantAssertions.InstantCondition;
import com.svedentsov.matcher.assertions.ListAssertions;
import com.svedentsov.matcher.assertions.ListAssertions.ListCondition;
import com.svedentsov.matcher.assertions.NumberAssertions.NumberCondition;
import com.svedentsov.matcher.assertions.PropertyAssertions.PropertyCondition;
import com.svedentsov.matcher.assertions.StringAssertions.StringCondition;
import com.svedentsov.matcher.assertions.rest.BodyAssertions.BodyCondition;
import com.svedentsov.matcher.assertions.rest.CookieAssertions.CookieCondition;
import com.svedentsov.matcher.assertions.rest.HeaderAssertions.HeaderCondition;
import com.svedentsov.matcher.assertions.rest.StatusAssertions.StatusCondition;
import com.svedentsov.matcher.assertions.rest.TimeAssertions.TimeCondition;
import io.restassured.response.Response;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.util.List;
import java.util.function.Function;

/**
 * Утилитный класс, предоставляющий DSL для создания условий ({@link Condition}) для проверки HTTP-ответов.
 * Предоставляет методы для создания различных условий проверки:
 * статусного кода, заголовков, cookie, тела ответа, времени ответа и значений внутри JSON-ответа по JSONPath.
 */
@UtilityClass
public class RestMatcher {

    /**
     * Создает условие для проверки статусного кода ответа.
     *
     * @param conditions Перечень условий для проверки статусного кода.
     * @return {@link StatusCondition} для проверки статусного кода.
     */
    @SafeVarargs
    public static StatusCondition status(@NonNull StatusCondition... conditions) {
        return response -> CompositeAssertions.and(conditions).check(response);
    }

    /**
     * Создает условие для проверки заголовков ответа.
     *
     * @param conditions Перечень условий для проверки заголовков.
     * @return {@link HeaderCondition} для проверки заголовков.
     */
    @SafeVarargs
    public static HeaderCondition header(@NonNull HeaderCondition... conditions) {
        return response -> CompositeAssertions.and(conditions).check(response);
    }

    /**
     * Создает условие для проверки cookie в ответе.
     *
     * @param conditions Перечень условий для проверки cookie.
     * @return {@link CookieCondition} для проверки cookie.
     */
    @SafeVarargs
    public static CookieCondition cookie(@NonNull CookieCondition... conditions) {
        return response -> CompositeAssertions.and(conditions).check(response);
    }

    /**
     * Создает условие для проверки времени ответа.
     *
     * @param conditions Перечень условий для проверки времени ответа.
     * @return {@link TimeCondition} для проверки времени ответа.
     */
    @SafeVarargs
    public static TimeCondition time(@NonNull TimeCondition... conditions) {
        return response -> CompositeAssertions.and(conditions).check(response);
    }

    /**
     * Создает условие для проверки тела ответа.
     *
     * @param conditions Перечень условий для проверки тела ответа.
     * @return {@link BodyCondition} для проверки тела ответа.
     */
    @SafeVarargs
    public static BodyCondition body(@NonNull BodyCondition... conditions) {
        return response -> CompositeAssertions.and(conditions).check(response);
    }

    /**
     * Создает условие для проверки всего тела ответа как строки.
     *
     * @param condition Строковое условие для проверки текста тела.
     * @return {@link BodyCondition} для проверки тела ответа.
     */
    public static BodyCondition body(@NonNull StringCondition condition) {
        return value(Response::asString, condition);
    }

    /**
     * Создает условие для проверки строкового значения, извлеченного из JSON-ответа по JSONPath.
     *
     * @param jsonPath  Путь JSONPath к полю.
     * @param condition Строковое условие для проверки.
     * @return {@link BodyCondition} для проверки значения по JSONPath.
     */
    public static BodyCondition body(@NonNull String jsonPath, @NonNull StringCondition condition) {
        return value(response -> JsonReader.extractValue(response, jsonPath, String.class), condition);
    }

    /**
     * Создает условие для проверки булева значения, извлеченного из JSON-ответа по JSONPath.
     *
     * @param jsonPath  Путь JSONPath к полю.
     * @param condition Булево условие для проверки.
     * @return {@link BodyCondition} для проверки значения по JSONPath.
     */
    public static BodyCondition body(@NonNull String jsonPath, @NonNull BooleanCondition condition) {
        return value(response -> JsonReader.extractValue(response, jsonPath, Boolean.class), condition);
    }

    /**
     * Создает условие для проверки числового значения, извлеченного из JSON-ответа по JSONPath.
     *
     * @param jsonPath  Путь JSONPath к полю.
     * @param condition Числовое условие для проверки.
     * @param type      Класс ожидаемого числового типа (например, Integer.class).
     * @param <T>       Тип числа ({@link Number} и {@link Comparable}).
     * @return {@link BodyCondition} для проверки значения по JSONPath.
     */
    public static <T extends Number & Comparable<T>> BodyCondition body(
            @NonNull String jsonPath,
            @NonNull NumberCondition<T> condition,
            @NonNull Class<T> type) {
        return value(response -> JsonReader.extractValue(response, jsonPath, type), condition);
    }

    /**
     * Создает условие для проверки произвольного свойства (Object) из JSON-ответа по JSONPath.
     * Этот метод полезен, когда точный тип значения неизвестен или не важен для общей проверки.
     *
     * @param jsonPath  Путь JSONPath к полю.
     * @param condition Условие для проверки свойства.
     * @return {@link BodyCondition} для проверки значения по JSONPath.
     */
    public static BodyCondition body(@NonNull String jsonPath, @NonNull PropertyCondition<?> condition) {
        @SuppressWarnings({"unchecked", "rawtypes"})
        PropertyCondition rawCondition = condition;
        return value(response -> JsonReader.extractValue(response, jsonPath, Object.class), rawCondition);
    }

    /**
     * Создает условие для проверки коллекции (или строки, или массива), извлеченной из JSON-ответа по JSONPath.
     * ВНИМАНИЕ: данная проверка выполняет небезопасное приведение типов. Убедитесь, что тип {@code T}
     * в {@code CollectionCondition<T>} соответствует реальному типу данных в JSON.
     *
     * @param jsonPath  Путь JSONPath к значению.
     * @param condition Условие из {@link CollectionAssertions}.
     * @param <T>       Ожидаемый тип (Collection, String, массив и т.д.).
     * @return {@link BodyCondition} для проверки.
     */
    @SuppressWarnings("unchecked")
    public static <T> BodyCondition body(@NonNull String jsonPath, @NonNull CollectionCondition<T> condition) {
        return response -> {
            Object rawValue = JsonReader.extractValue(response, jsonPath, Object.class);
            condition.check((T) rawValue);
        };
    }

    /**
     * Создает условие для проверки временной метки (Instant), извлеченной из JSON-ответа по JSONPath.
     * Ожидается, что значение по {@code jsonPath} является строкой в формате ISO-8601.
     * Если значение отсутствует или не является строкой, проверка будет выполнена с {@code null}.
     *
     * @param jsonPath  Путь JSONPath к полю с датой.
     * @param condition Условие из {@link InstantAssertions}.
     * @return {@link BodyCondition} для проверки.
     */
    public static BodyCondition body(@NonNull String jsonPath, @NonNull InstantCondition condition) {
        return value(response -> {
            String stringValue = JsonReader.extractValue(response, jsonPath, String.class);
            return stringValue != null ? Instant.parse(stringValue) : null;
        }, condition);
    }

    /**
     * Создает условие для проверки списка сущностей, извлеченного из JSON-ответа по JSONPath.
     * Ожидается, что JSONPath вернет JSON-массив, который десериализуется в {@code List<?>}.
     * ВНИМАНИЕ: данная проверка выполняет небезопасное приведение типов. Убедитесь, что тип {@code T}
     * в {@code ListCondition<T>} соответствует реальному типу элементов в JSON-массиве.
     *
     * @param jsonPath  Путь JSONPath к массиву.
     * @param condition Условие из {@link ListAssertions}.
     * @param <T>       Тип элементов списка (как правило, Map или примитивы).
     * @return {@link BodyCondition} для проверки.
     */
    @SuppressWarnings("unchecked")
    public static <T> BodyCondition body(@NonNull String jsonPath, @NonNull ListCondition<T> condition) {
        return response -> {
            List<?> rawList = JsonReader.extractValue(response, jsonPath, List.class);
            condition.check((List<T>) rawList);
        };
    }

    /**
     * Универсальный метод для создания условия проверки значения, извлекаемого из {@link Response}.
     * Этот метод является базовым для многих других {@code body(...)} методов.
     *
     * @param getter    Функция для извлечения значения из ответа.
     * @param condition Условие для проверки извлеченного значения.
     * @param <R>       Тип проверяемого значения.
     * @return {@link BodyCondition} для проверки.
     * @throws NullPointerException если {@code getter} или {@code condition} равен null.
     */
    public static <R> BodyCondition value(
            @NonNull Function<? super Response, ? extends R> getter,
            @NonNull Condition<? super R> condition) {
        return response -> PropertyMatcher.value(getter, condition).check(response);
    }
}
