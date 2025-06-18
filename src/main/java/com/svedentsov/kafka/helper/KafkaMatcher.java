package com.svedentsov.kafka.helper;

import com.svedentsov.matcher.Condition;
import com.svedentsov.matcher.JsonReader;
import com.svedentsov.matcher.PropertyMatcher;
import com.svedentsov.matcher.assertions.BooleanAssertions.BooleanCondition;
import com.svedentsov.matcher.assertions.CompositeAssertions;
import com.svedentsov.matcher.assertions.InstantAssertions.InstantCondition;
import com.svedentsov.matcher.assertions.NumberAssertions.NumberCondition;
import com.svedentsov.matcher.assertions.PropertyAssertions.PropertyCondition;
import com.svedentsov.matcher.assertions.StringAssertions.StringCondition;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Instant;
import java.util.function.Function;

/**
 * Утилитный класс, предоставляющий DSL для создания условий (Condition) для Kafka-записей.
 * Все специфичные проверки сводятся к одному универсальному методу {@link #value(Function, Condition)}.
 */
@UtilityClass
public class KafkaMatcher {

    /**
     * Проверка ключа записи.
     *
     * @param conditions строковые условия для ключа
     * @return {@link Condition} для проверки {@link ConsumerRecord#key()}
     */
    @SafeVarargs
    public static Condition<ConsumerRecord<String, String>> key(@NonNull StringCondition... conditions) {
        StringCondition compositeCondition = (StringCondition) CompositeAssertions.and(conditions);
        return value(ConsumerRecord::key, compositeCondition);
    }

    /**
     * Проверка имени топика записи.
     *
     * @param conditions строковые условия для топика
     * @return {@link Condition} для проверки {@link ConsumerRecord#topic()}
     */
    @SafeVarargs
    public static Condition<ConsumerRecord<String, String>> topic(@NonNull StringCondition... conditions) {
        StringCondition compositeCondition = (StringCondition) CompositeAssertions.and(conditions);
        return value(ConsumerRecord::topic, compositeCondition);
    }

    /**
     * Проверка номера партиции записи.
     *
     * @param conditions числовые условия для проверки партиции
     * @return {@link Condition} для проверки {@link ConsumerRecord#partition()}
     */
    @SafeVarargs
    public static Condition<ConsumerRecord<String, String>> partition(@NonNull NumberCondition<Integer>... conditions) {
        @SuppressWarnings("unchecked")
        NumberCondition<Integer> compositeCondition = (NumberCondition<Integer>) CompositeAssertions.and(conditions);
        return value(ConsumerRecord::partition, compositeCondition);
    }

    /**
     * Проверка смещения записи.
     *
     * @param conditions числовые условия для проверки смещения
     * @return {@link Condition} для проверки {@link ConsumerRecord#offset()}
     */
    @SafeVarargs
    public static Condition<ConsumerRecord<String, String>> offset(@NonNull NumberCondition<Long>... conditions) {
        @SuppressWarnings("unchecked")
        NumberCondition<Long> compositeCondition = (NumberCondition<Long>) CompositeAssertions.and(conditions);
        return value(ConsumerRecord::offset, compositeCondition);
    }

    /**
     * Проверка временной метки записи: преобразует {@code record.timestamp()} в {@link Instant}.
     *
     * @param conditions условия для проверки {@link Instant}
     * @return {@link Condition} для проверки времени записи
     */
    @SafeVarargs
    public static Condition<ConsumerRecord<String, String>> timestamp(@NonNull InstantCondition... conditions) {
        InstantCondition compositeCondition = (InstantCondition) CompositeAssertions.and(conditions);
        return value(record -> Instant.ofEpochMilli(record.timestamp()), compositeCondition);
    }

    /**
     * Проверка строкового значения всего тела записи.
     *
     * @param conditions строковые условия для проверки
     * @return {@link Condition} для проверки {@link ConsumerRecord#value()}
     */
    public static Condition<ConsumerRecord<String, String>> value(@NonNull StringCondition... conditions) {
        StringCondition compositeCondition = (StringCondition) CompositeAssertions.and(conditions);
        return value(ConsumerRecord::value, compositeCondition);
    }

    /**
     * Проверка булевого значения из JSON по JSONPath.
     *
     * @param jsonPath  путь JSONPath
     * @param condition булевое условие для проверки
     * @return {@link Condition} для проверки значения из JSON
     */
    public static Condition<ConsumerRecord<String, String>> value(@NonNull String jsonPath, @NonNull BooleanCondition condition) {
        return value(record -> JsonReader.extractValue(record.value(), jsonPath, Boolean.class), condition);
    }

    /**
     * Проверка строкового значения из JSON по JSONPath.
     *
     * @param jsonPath  путь JSONPath
     * @param condition строковое условие для проверки
     * @return {@link Condition} для проверки значения из JSON
     */
    public static Condition<ConsumerRecord<String, String>> value(@NonNull String jsonPath, @NonNull StringCondition condition) {
        return value(record -> JsonReader.extractValue(record.value(), jsonPath, String.class), condition);
    }

    /**
     * Проверка произвольного свойства из JSON по JSONPath.
     *
     * @param jsonPath  путь JSONPath
     * @param condition условие для проверки свойства
     * @return {@link Condition} для проверки значения из JSON
     */
    public static Condition<ConsumerRecord<String, String>> value(@NonNull String jsonPath, @NonNull PropertyCondition condition) {
        return value(record -> JsonReader.extractValue(record.value(), jsonPath, Object.class), condition);
    }

    /**
     * Проверка числового значения, извлечённого из JSON по JSONPath.
     *
     * @param jsonPath  путь JSONPath
     * @param condition числовое условие для проверки
     * @param type      класс ожидаемого числового типа
     * @param <T>       тип числа (Number & Comparable)
     * @return {@link Condition} для проверки значения из JSON
     */
    public static <T extends Number & Comparable<T>> Condition<ConsumerRecord<String, String>> value(
            @NonNull String jsonPath,
            @NonNull NumberCondition<T> condition,
            @NonNull Class<T> type) {
        return value(record -> JsonReader.extractValue(record.value(), jsonPath, type), condition);
    }

    /**
     * Универсальный метод для создания условия на основе функции-геттера и другого условия.
     *
     * @param getter    функция для извлечения значения из {@link ConsumerRecord}
     * @param condition условие для проверки извлечённого значения
     * @param <R>       тип проверяемого свойства
     * @return {@link Condition}, применяющее {@code cond} к результату {@code getter}
     * @throws NullPointerException если getter или cond равны null
     */
    public static <R> Condition<ConsumerRecord<String, String>> value(
            @NonNull Function<? super ConsumerRecord<String, String>, ? extends R> getter,
            @NonNull Condition<? super R> condition) {
        return record -> PropertyMatcher.value(getter, condition).check(record);
    }
}
