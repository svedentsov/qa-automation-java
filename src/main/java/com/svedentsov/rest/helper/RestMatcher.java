package com.svedentsov.rest.helper;

import com.svedentsov.matcher.Condition;
import com.svedentsov.matcher.JsonReader;
import com.svedentsov.matcher.PropertyMatcher;
import com.svedentsov.matcher.assertions.BooleanAssertions.BooleanCondition;
import com.svedentsov.matcher.assertions.CollectionAssertions.CollectionCondition;
import com.svedentsov.matcher.assertions.CompositeAssertions;
import com.svedentsov.matcher.assertions.InstantAssertions.InstantCondition;
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
 * Утилитный класс для создания условий проверки HTTP-ответов.
 * Предоставляет методы для создания различных условий проверки:
 * статусного кода, заголовков, куки, тела ответа, времени ответа и значений внутри JSON-ответа по JSONPath.
 */
@UtilityClass
public class RestMatcher {

    /**
     * Создаёт условие для проверки статусного кода ответа.
     *
     * @param conditions перечень условий для проверки статусного кода
     * @return {@link StatusCondition} для проверки статусного кода
     */
    @SafeVarargs
    public static StatusCondition status(@NonNull StatusCondition... conditions) {
        return response -> CompositeAssertions.and(conditions).check(response);
    }

    /**
     * Создаёт условие для проверки заголовков ответа.
     *
     * @param conditions перечень условий для проверки заголовков.
     * @return {@link HeaderCondition} для проверки заголовков.
     */
    @SafeVarargs
    public static HeaderCondition header(@NonNull HeaderCondition... conditions) {
        return response -> CompositeAssertions.and(conditions).check(response);
    }

    /**
     * Создаёт условие для проверки куки в ответе.
     *
     * @param conditions перечень условий для проверки куки.
     * @return {@link CookieCondition} для проверки куки.
     */
    @SafeVarargs
    public static CookieCondition cookie(@NonNull CookieCondition... conditions) {
        return response -> CompositeAssertions.and(conditions).check(response);
    }

    /**
     * Создаёт условие для проверки времени ответа.
     *
     * @param conditions перечень условий для проверки времени ответа.
     * @return {@link TimeCondition} для проверки времени ответа.
     */
    @SafeVarargs
    public static TimeCondition time(@NonNull TimeCondition... conditions) {
        return response -> CompositeAssertions.and(conditions).check(response);
    }

    /**
     * Создаёт условие для проверки тела ответа.
     *
     * @param conditions перечень условий для проверки тела ответа.
     * @return {@link BodyCondition} для проверки тела ответа.
     */
    @SafeVarargs
    public static BodyCondition body(@NonNull BodyCondition... conditions) {
        return response -> CompositeAssertions.and(conditions).check(response);
    }

    /**
     * Создаёт условие для проверки всего тела ответа как строки.
     *
     * @param condition строковое условие для проверки текста тела.
     * @return {@link BodyCondition} для проверки тела ответа.
     */
    public static BodyCondition body(@NonNull StringCondition condition) {
        return value(Response::asString, condition);
    }

    /**
     * Создаёт условие для проверки строкового значения из JSON-ответа по JSONPath.
     *
     * @param jsonPath  путь JSONPath к полю
     * @param condition строковое условие для проверки
     * @return {@link BodyCondition} для проверки значения по JSONPath
     */
    public static BodyCondition body(@NonNull String jsonPath, @NonNull StringCondition condition) {
        return value(response -> JsonReader.extractValue(response, jsonPath, String.class), condition);
    }

    /**
     * Создаёт условие для проверки булевого значения из JSON-ответа по JSONPath.
     *
     * @param jsonPath  путь JSONPath к полю
     * @param condition булевое условие для проверки
     * @return {@link BodyCondition} для проверки значения по JSONPath
     */
    public static BodyCondition body(@NonNull String jsonPath, @NonNull BooleanCondition condition) {
        return value(response -> JsonReader.extractValue(response, jsonPath, Boolean.class), condition);
    }

    /**
     * Создаёт условие для проверки числового значения из JSON-ответа по JSONPath.
     *
     * @param jsonPath  путь JSONPath к полю
     * @param condition числовое условие для проверки
     * @param type      класс ожидаемого числового типа
     * @param <T>       тип числа (Number & Comparable)
     * @return {@link BodyCondition} для проверки значения по JSONPath
     */
    public static <T extends Number & Comparable<T>> BodyCondition body(
            @NonNull String jsonPath,
            @NonNull NumberCondition<T> condition,
            @NonNull Class<T> type) {
        return value(response -> JsonReader.extractValue(response, jsonPath, type), condition);
    }

    /**
     * Создаёт условие для проверки произвольного свойства из JSON-ответа по JSONPath.
     * Этот метод полезен, когда точный тип значения неизвестен или не важен для общей проверки.
     *
     * @param jsonPath  путь JSONPath к полю
     * @param condition условие для проверки свойства
     * @return {@link BodyCondition} для проверки значения по JSONPath
     */
    public static BodyCondition body(@NonNull String jsonPath, @NonNull PropertyCondition condition) {
        return value(response -> JsonReader.extractValue(response, jsonPath, Object.class), condition);
    }

    /**
     * Создаёт условие для проверки коллекции (или строки, или массива) из JSON-ответа по JSONPath.
     *
     * @param jsonPath  путь JSONPath к значению
     * @param condition условие из CollectionAssertions
     * @param <T>       ожидаемый тип (Collection, String, массив и т.д.)
     * @return {@link BodyCondition} для проверки
     */
    @SuppressWarnings("unchecked")
    public static <T> BodyCondition body(@NonNull String jsonPath, @NonNull CollectionCondition<T> condition) {
        return response -> {
            Object rawValue = JsonReader.extractValue(response, jsonPath, Object.class);
            condition.check((T) rawValue);
        };
    }

    /**
     * Создаёт условие для проверки временной метки (Instant) из JSON-ответа по JSONPath.
     * Ожидается, что значение по {@code jsonPath} - строка в ISO-8601-формате.
     * Если значение отсутствует или не является строкой, проверка будет выполнена с null.
     *
     * @param jsonPath  путь JSONPath к полю с датой.
     * @param condition условие из {@link InstantCondition}.
     * @return {@link BodyCondition} для проверки.
     */
    public static BodyCondition body(@NonNull String jsonPath, @NonNull InstantCondition condition) {
        return value(response -> {
            String stringValue = JsonReader.extractValue(response, jsonPath, String.class);
            return stringValue != null ? Instant.parse(stringValue) : null;
        }, condition);
    }

    /**
     * Создаёт условие для проверки списка сущностей из JSON-ответа по JSONPath
     * Ожидается, что JSONPath вернёт некий JSON-массив, который десериализуется в List<?>.
     *
     * @param jsonPath  путь JSONPath к массиву
     * @param condition условие из ListAssertions
     * @param <T>       тип элементов списка (как правило Map или примитивы)
     * @return {@link BodyCondition} для проверки
     */
    @SuppressWarnings("unchecked")
    public static <T> BodyCondition body(@NonNull String jsonPath, @NonNull ListCondition<T> condition) {
        return response -> {
            List<?> rawList = JsonReader.extractValue(response, jsonPath, List.class);
            condition.check((List<T>) rawList);
        };
    }

    /**
     * Универсальный метод для создания условия проверки значения,
     * извлекаемого из {@link Response}. Этот метод является базовым для многих других
     * {@code body(...)} методов.
     *
     * @param getter    функция для извлечения значения из ответа.
     * @param condition условие для проверки извлечённого значения.
     * @param <R>       тип проверяемого значения.
     * @return {@link BodyCondition} для проверки.
     * @throws NullPointerException если getter или condition равен null (покрывается Lombok @NonNull).
     */
    public static <R> BodyCondition value(
            @NonNull Function<? super Response, ? extends R> getter,
            @NonNull Condition<? super R> condition) {
        return response -> PropertyMatcher.value(getter, condition).check(response);
    }
}
