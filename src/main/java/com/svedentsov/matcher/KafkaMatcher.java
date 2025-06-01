package com.svedentsov.matcher;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.svedentsov.matcher.assertions.BooleanAssertions.BooleanCondition;
import com.svedentsov.matcher.assertions.InstantAssertions.InstantCondition;
import com.svedentsov.matcher.assertions.NumberAssertions.NumberCondition;
import com.svedentsov.matcher.assertions.PropertyAssertions.PropertyCondition;
import com.svedentsov.matcher.assertions.StringAssertions.StringCondition;
import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Instant;
import java.util.Objects;
import java.util.function.Function;

/**
 * Утилитный класс, предоставляющий DSL для создания условий (Condition)
 * для Kafka-записей. Все специфичные проверки сводятся к одному универсальному
 * методу {@link #value(Function, Condition)}, что упрощает поддержку и расширение.
 */
@UtilityClass
public class KafkaMatcher {

    /**
     * Проверка строкового значения всего тела записи.
     *
     * @param sc строковое условие для проверки
     * @return {@link Condition} для проверки {@link ConsumerRecord#value()}
     */
    public static Condition<ConsumerRecord<String, String>> value(
            StringCondition sc) {
        return value(ConsumerRecord::value, sc);
    }

    /**
     * Проверка строкового значения из JSON по JSONPath.
     *
     * @param jsonPath путь JSONPath
     * @param sc       строковое условие для проверки
     * @return {@link Condition} для проверки значения из JSON
     */
    public static Condition<ConsumerRecord<String, String>> value(
            String jsonPath,
            StringCondition sc) {
        return value(record -> getJsonValue(record.value(), jsonPath, String.class), sc);
    }

    /**
     * Проверка булевого значения из JSON по JSONPath.
     *
     * @param jsonPath путь JSONPath
     * @param bc       булевое условие для проверки
     * @return {@link Condition} для проверки значения из JSON
     */
    public static Condition<ConsumerRecord<String, String>> value(
            String jsonPath,
            BooleanCondition bc) {
        return value(record -> getJsonValue(record.value(), jsonPath, Boolean.class), bc);
    }

    /**
     * Проверка числового значения, извлечённого из JSON по JSONPath.
     *
     * @param jsonPath путь JSONPath
     * @param nc       числовое условие для проверки
     * @param type     класс ожидаемого числового типа
     * @param <T>      тип числа (Number & Comparable)
     * @return {@link Condition} для проверки значения из JSON
     */
    public static <T extends Number & Comparable<T>> Condition<ConsumerRecord<String, String>> value(
            String jsonPath,
            NumberCondition<T> nc,
            Class<T> type) {
        return value(record -> getJsonValue(record.value(), jsonPath, type), nc);
    }

    /**
     * Проверка произвольного свойства из JSON по JSONPath.
     *
     * @param jsonPath путь JSONPath
     * @param pc       условие для проверки свойства
     * @return {@link Condition} для проверки значения из JSON
     */
    public static Condition<ConsumerRecord<String, String>> value(
            String jsonPath,
            PropertyCondition pc) {
        return value(record -> getJsonValue(record.value(), jsonPath, Object.class), pc);
    }

    /**
     * Проверка ключа записи.
     *
     * @param sc строковое условие для ключа
     * @return {@link Condition} для проверки {@link ConsumerRecord#key()}
     */
    public static Condition<ConsumerRecord<String, String>> key(
            StringCondition sc) {
        return value(ConsumerRecord::key, sc);
    }

    /**
     * Проверка имени топика записи.
     *
     * @param sc строковое условие для топика
     * @return {@link Condition} для проверки {@link ConsumerRecord#topic()}
     */
    public static Condition<ConsumerRecord<String, String>> topic(
            StringCondition sc) {
        return value(ConsumerRecord::topic, sc);
    }

    /**
     * Проверка номера партиции записи.
     *
     * @param nc числовое условие для проверки партиции
     * @return {@link Condition} для проверки {@link ConsumerRecord#partition()}
     */
    public static Condition<ConsumerRecord<String, String>> partition(
            NumberCondition<Integer> nc) {
        return value(ConsumerRecord::partition, nc);
    }

    /**
     * Проверка смещения записи.
     *
     * @param nc числовое условие для проверки смещения
     * @return {@link Condition} для проверки {@link ConsumerRecord#offset()}
     */
    public static Condition<ConsumerRecord<String, String>> offset(
            NumberCondition<Long> nc) {
        return value(ConsumerRecord::offset, nc);
    }

    /**
     * Проверка временной метки записи.
     *
     * @param ic условие для проверки {@link Instant}
     * @return {@link Condition} для проверки времени записи
     */
    public static Condition<ConsumerRecord<String, String>> timestamp(
            InstantCondition ic) {
        return value(record -> Instant.ofEpochMilli(record.timestamp()), ic);
    }

    /**
     * Универсальный метод для создания условия на основе функции-геттера и другого условия.
     *
     * @param getter функция для извлечения значения из {@link ConsumerRecord}
     * @param cond   условие для проверки извлечённого значения
     * @param <R>    тип проверяемого свойства
     * @return {@link Condition}, применяющее {@code cond} к результату {@code getter}
     * @throws NullPointerException если getter или cond равны null
     */
    public static <R> Condition<ConsumerRecord<String, String>> value(
            Function<? super ConsumerRecord<String, String>, ? extends R> getter,
            Condition<? super R> cond) {
        Objects.requireNonNull(getter, "getter не может быть null");
        Objects.requireNonNull(cond, "condition не может быть null");
        return record -> cond.check(getter.apply(record));
    }

    /**
     * Извлекает значение из JSON-строки по JSONPath и проверяет его тип.
     *
     * @param json         исходная JSON-строка
     * @param jsonPath     путь JSONPath
     * @param expectedType ожидаемый класс значения
     * @param <T>          тип значения
     * @return извлечённое и приведённое к {@code expectedType} значение или null
     * @throws AssertionError       если значение не соответствует expectedType
     * @throws NullPointerException если любой из аргументов null
     */
    private static <T> T getJsonValue(String json, String jsonPath, Class<T> expectedType) {
        Objects.requireNonNull(json, "JSON-строка не может быть null");
        Objects.requireNonNull(jsonPath, "JSONPath не может быть null");
        Objects.requireNonNull(expectedType, "Ожидаемый тип не может быть null");
        Configuration conf = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
        Object val = JsonPath.using(conf).parse(json).read(jsonPath);
        if (val == null) {
            return null;
        }
        if (!expectedType.isInstance(val)) {
            String actual = val.getClass().getSimpleName();
            throw new AssertionError(String.format(
                    "Ожидалось, что значение по пути %s будет типа %s, но было: %s (%s)",
                    jsonPath, expectedType.getSimpleName(), val, actual));
        }
        return expectedType.cast(val);
    }
}
