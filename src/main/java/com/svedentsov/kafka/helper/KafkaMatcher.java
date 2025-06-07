package com.svedentsov.kafka.helper;

import com.svedentsov.matcher.Condition;
import com.svedentsov.matcher.JsonReader;
import com.svedentsov.matcher.PropertyMatcher;
import com.svedentsov.matcher.assertions.BooleanAssertions.BooleanCondition;
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
 * Утилитный класс, предоставляющий DSL для создания условий (Condition)
 * для Kafka-записей. Все специфичные проверки сводятся к одному универсальному
 * методу {@link #value(Function, Condition)}, что упрощает поддержку и расширение.
 */
@UtilityClass
public class KafkaMatcher {

    /**
     * Проверка ключа записи.
     *
     * @param condition строковое условие для ключа
     * @return {@link Condition} для проверки {@link ConsumerRecord#key()}
     */
    public static Condition<ConsumerRecord<String, String>> key(@NonNull StringCondition condition) {
        return value(ConsumerRecord::key, condition);
    }

    /**
     * Проверка имени топика записи.
     *
     * @param condition строковое условие для топика
     * @return {@link Condition} для проверки {@link ConsumerRecord#topic()}
     */
    public static Condition<ConsumerRecord<String, String>> topic(@NonNull StringCondition condition) {
        return value(ConsumerRecord::topic, condition);
    }

    /**
     * Проверка номера партиции записи.
     *
     * @param condition числовое условие для проверки партиции
     * @return {@link Condition} для проверки {@link ConsumerRecord#partition()}
     */
    public static Condition<ConsumerRecord<String, String>> partition(@NonNull NumberCondition<Integer> condition) {
        return value(ConsumerRecord::partition, condition);
    }

    /**
     * Проверка смещения записи.
     *
     * @param condition числовое условие для проверки смещения
     * @return {@link Condition} для проверки {@link ConsumerRecord#offset()}
     */
    public static Condition<ConsumerRecord<String, String>> offset(@NonNull NumberCondition<Long> condition) {
        return value(ConsumerRecord::offset, condition);
    }

    /**
     * Проверка временной метки записи: преобразует {@code record.timestamp()} в {@link Instant}.
     *
     * @param condition условие для проверки {@link Instant}
     * @return {@link Condition} для проверки времени записи
     */
    public static Condition<ConsumerRecord<String, String>> timestamp(@NonNull InstantCondition condition) {
        return value(record -> Instant.ofEpochMilli(record.timestamp()), condition);
    }

    /**
     * Проверка строкового значения всего тела записи.
     *
     * @param condition строковое условие для проверки
     * @return {@link Condition} для проверки {@link ConsumerRecord#value()}
     */
    public static Condition<ConsumerRecord<String, String>> value(@NonNull StringCondition condition) {
        return value(ConsumerRecord::value, condition);
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
        return PropertyMatcher.value(getter, condition);
    }
}
