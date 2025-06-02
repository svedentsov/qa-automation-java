package com.svedentsov.matcher;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.svedentsov.matcher.assertions.BooleanAssertions.BooleanCondition;
import com.svedentsov.matcher.assertions.CollectionAssertions.CollectionCondition;
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
import java.util.Objects;
import java.util.function.Function;

/**
 * Утилитный класс для создания условий проверки HTTP-ответов.
 * Предоставляет методы для создания различных условий проверки:
 * статусного кода, заголовков, куки, тела ответа, времени ответа
 * и значений внутри JSON-ответа по JSONPath.
 */
@UtilityClass
public class RestMatcher {

    /**
     * Создаёт условие для проверки статусного кода ответа.
     *
     * @param sc условие для проверки статусного кода
     * @return {@link StatusCondition} для проверки статусного кода
     */
    public static StatusCondition status(StatusCondition sc) {
        return sc;
    }

    /**
     * Создаёт условие для проверки заголовков ответа.
     *
     * @param hc условие для проверки заголовков
     * @return {@link HeaderCondition} для проверки заголовков
     */
    public static HeaderCondition header(HeaderCondition hc) {
        return hc;
    }

    /**
     * Создаёт условие для проверки куки в ответе.
     *
     * @param cc условие для проверки куки
     * @return {@link CookieCondition} для проверки куки
     */
    public static CookieCondition cookie(CookieCondition cc) {
        return cc;
    }

    /**
     * Создаёт условие для проверки времени ответа.
     *
     * @param tc условие для проверки времени ответа
     * @return {@link TimeCondition} для проверки времени ответа
     */
    public static TimeCondition time(TimeCondition tc) {
        return tc;
    }

    /**
     * Создаёт условие для проверки тела ответа.
     * @param bc условие для проверки тела ответа
     * @return {@link BodyCondition} для проверки тела ответа
     */
    public static BodyCondition body(BodyCondition bc) {
        return bc;
    }

    /**
     * Создаёт условие для проверки всего тела ответа как строки.
     *
     * @param sc строковое условие для проверки текста тела
     * @return {@link BodyCondition} для проверки тела ответа
     */
    public static BodyCondition body(
            @NonNull StringCondition sc) {
        return value(resp -> resp.getBody().asString(), sc);
    }

    /**
     * Создаёт условие для проверки строкового значения из JSON-ответа по JSONPath.
     *
     * @param jsonPath путь JSONPath к полю
     * @param sc       строковое условие для проверки
     * @return {@link BodyCondition} для проверки значения по JSONPath
     */
    public static BodyCondition body(
            @NonNull String jsonPath,
            @NonNull StringCondition sc) {
        return value(response -> getJsonValue(response, jsonPath, String.class), sc);
    }

    /**
     * Создаёт условие для проверки булевого значения из JSON-ответа по JSONPath.
     *
     * @param jsonPath путь JSONPath к полю
     * @param bc       булевое условие для проверки
     * @return {@link BodyCondition} для проверки значения по JSONPath
     */
    public static BodyCondition body(
            @NonNull String jsonPath,
            @NonNull BooleanCondition bc) {
        return value(response -> getJsonValue(response, jsonPath, Boolean.class), bc);
    }

    /**
     * Создаёт условие для проверки числового значения из JSON-ответа по JSONPath.
     *
     * @param jsonPath путь JSONPath к полю
     * @param nc       числовое условие для проверки
     * @param type     класс ожидаемого числового типа
     * @param <T>      тип числа (Number & Comparable)
     * @return {@link BodyCondition} для проверки значения по JSONPath
     */
    public static <T extends Number & Comparable<T>> BodyCondition body(
            @NonNull String jsonPath,
            @NonNull NumberCondition<T> nc,
            @NonNull Class<T> type) {
        return value(response -> getJsonValue(response, jsonPath, type), nc);
    }

    /**
     * Создаёт условие для проверки произвольного свойства из JSON-ответа по JSONPath.
     *
     * @param jsonPath путь JSONPath к полю
     * @param pc       условие для проверки свойства
     * @return {@link BodyCondition} для проверки значения по JSONPath
     */
    public static BodyCondition body(
            @NonNull String jsonPath,
            @NonNull PropertyCondition pc) {
        return value(response -> getJsonValue(response, jsonPath, Object.class), pc);
    }

    /**
     * Создаёт условие для проверки коллекции (или строки, или массива) из JSON-ответа по JSONPath.
     *
     * @param jsonPath путь JSONPath к значению
     * @param cc       условие из CollectionAssertions
     * @param <T>      ожидаемый тип (Collection, String, массив и т.д.)
     * @return {@link BodyCondition} для проверки
     */
    @SuppressWarnings("unchecked")
    public static <T> BodyCondition body(
            @NonNull String jsonPath,
            @NonNull CollectionCondition<T> cc) {
        return response -> {
            Object raw = getJsonValue(response, jsonPath, Object.class);
            cc.check((T) raw);
        };
    }

    /**
     * Создаёт условие для проверки временной метки (Instant) из JSON-ответа по JSONPath.
     * Ожидается, что значение по jsonPath - строка в ISO-8601-формате.
     *
     * @param jsonPath путь JSONPath к полю с датой
     * @param ic       условие из InstantAssertions
     * @return {@link BodyCondition} для проверки
     */
    public static BodyCondition body(
            @NonNull String jsonPath,
            @NonNull InstantCondition ic) {
        return response -> {
            String strVal = getJsonValue(response, jsonPath, String.class);
            Instant instant = (strVal == null) ? null : Instant.parse(strVal);
            ic.check(instant);
        };
    }

    /**
     * Создаёт условие для проверки списка сущностей из JSON-ответа по JSONPath
     * Ожидается, что JSONPath вернёт некий JSON-массив, который десериализуется в List<?>.
     *
     * @param jsonPath путь JSONPath к массиву
     * @param lc       условие из ListAssertions
     * @param <T>      тип элементов списка (как правило Map или примитивы)
     * @return {@link BodyCondition} для проверки
     */
    @SuppressWarnings("unchecked")
    public static <T> BodyCondition body(
            @NonNull String jsonPath,
            @NonNull ListCondition<T> lc) {
        return response -> {
            List<?> rawList = getJsonValue(response, jsonPath, List.class);
            lc.check((List<T>) rawList);
        };
    }

    /**
     * Универсальный метод для создания условия проверки значения,
     * извлекаемого из {@link Response}.
     *
     * @param getter функция для извлечения значения из ответа
     * @param cond   условие для проверки извлечённого значения
     * @param <R>    тип проверяемого значения
     * @return {@link BodyCondition} для проверки
     * @throws NullPointerException если getter или cond равен null
     */
    public static <R> BodyCondition value(
            @NonNull Function<? super Response, ? extends R> getter,
            @NonNull Condition<? super R> cond) {
        Objects.requireNonNull(getter, "getter не может быть null");
        Objects.requireNonNull(cond, "condition не может быть null");
        return response -> cond.check(getter.apply(response));
    }

    /**
     * Извлекает значение из JSON-ответа по JSONPath и проверяет его тип.
     *
     * @param response     HTTP-ответ
     * @param jsonPath     путь JSONPath для извлечения
     * @param expectedType ожидаемый класс значения
     * @param <T>          тип значения
     * @return извлечённое и приведённое к {@code expectedType} значение или null
     * @throws AssertionError       если значение не того типа
     * @throws NullPointerException если любой из аргументов равен null
     */
    private static <T> T getJsonValue(
            @NonNull Response response,
            @NonNull String jsonPath,
            @NonNull Class<T> expectedType) {
        String json = response.getBody().asString();
        Configuration conf = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
        Object val = JsonPath.using(conf).parse(json).read(jsonPath);
        if (val == null) {
            return null;
        }
        if (!expectedType.isInstance(val)) {
            String actualType = val.getClass().getSimpleName();
            throw new AssertionError(String.format(
                    "Ожидалось, что значение по пути '%s' будет типа %s, но было: %s (%s)",
                    jsonPath, expectedType.getSimpleName(), val, actualType));
        }
        return expectedType.cast(val);
    }
}
