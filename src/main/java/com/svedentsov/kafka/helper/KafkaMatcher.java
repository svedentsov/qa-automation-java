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
 * Утилитный класс, предоставляющий DSL для создания условий ({@link Condition}) для проверки записей Kafka.
 * Все специфичные проверки сводятся к одному универсальному методу {@link #value(Function, Condition)}.
 */
@UtilityClass
public class KafkaMatcher {

    /**
     * Создает условие для проверки ключа записи.
     *
     * @param conditions Массив строковых условий для проверки ключа.
     * @return {@link Condition} для проверки {@link ConsumerRecord#key()}.
     */
    @SafeVarargs
    public static Condition<ConsumerRecord<String, String>> key(@NonNull StringCondition... conditions) {
        StringCondition compositeCondition = (StringCondition) CompositeAssertions.and(conditions);
        return value(ConsumerRecord::key, compositeCondition);
    }

    /**
     * Создает условие для проверки имени топика записи.
     *
     * @param conditions Массив строковых условий для проверки имени топика.
     * @return {@link Condition} для проверки {@link ConsumerRecord#topic()}.
     */
    @SafeVarargs
    public static Condition<ConsumerRecord<String, String>> topic(@NonNull StringCondition... conditions) {
        StringCondition compositeCondition = (StringCondition) CompositeAssertions.and(conditions);
        return value(ConsumerRecord::topic, compositeCondition);
    }

    /**
     * Создает условие для проверки номера партиции записи.
     *
     * @param conditions Массив числовых условий для проверки номера партиции.
     * @return {@link Condition} для проверки {@link ConsumerRecord#partition()}.
     */
    @SafeVarargs
    public static Condition<ConsumerRecord<String, String>> partition(@NonNull NumberCondition<Integer>... conditions) {
        @SuppressWarnings("unchecked")
        NumberCondition<Integer> compositeCondition = (NumberCondition<Integer>) CompositeAssertions.and(conditions);
        return value(ConsumerRecord::partition, compositeCondition);
    }

    /**
     * Создает условие для проверки смещения (offset) записи.
     *
     * @param conditions Массив числовых условий для проверки смещения.
     * @return {@link Condition} для проверки {@link ConsumerRecord#offset()}.
     */
    @SafeVarargs
    public static Condition<ConsumerRecord<String, String>> offset(@NonNull NumberCondition<Long>... conditions) {
        @SuppressWarnings("unchecked")
        NumberCondition<Long> compositeCondition = (NumberCondition<Long>) CompositeAssertions.and(conditions);
        return value(ConsumerRecord::offset, compositeCondition);
    }

    /**
     * Создает условие для проверки временной метки записи.
     * Преобразует {@code record.timestamp()} (тип long) в {@link Instant} перед проверкой.
     *
     * @param conditions Массив условий для проверки {@link Instant}.
     * @return {@link Condition} для проверки времени записи.
     */
    @SafeVarargs
    public static Condition<ConsumerRecord<String, String>> timestamp(@NonNull InstantCondition... conditions) {
        InstantCondition compositeCondition = (InstantCondition) CompositeAssertions.and(conditions);
        return value(record -> Instant.ofEpochMilli(record.timestamp()), compositeCondition);
    }

    /**
     * Создает условие для проверки строкового значения (value) записи.
     *
     * @param conditions Массив строковых условий для проверки.
     * @return {@link Condition} для проверки {@link ConsumerRecord#value()}.
     */
    @SafeVarargs
    public static Condition<ConsumerRecord<String, String>> value(@NonNull StringCondition... conditions) {
        StringCondition compositeCondition = (StringCondition) CompositeAssertions.and(conditions);
        return value(ConsumerRecord::value, compositeCondition);
    }

    /**
     * Создает условие для проверки булева значения, извлеченного из JSON-тела сообщения по JSONPath.
     *
     * @param jsonPath  Путь JSONPath к полю (например, "$.user.active").
     * @param condition Булево условие для проверки.
     * @return {@link Condition} для проверки значения из JSON.
     */
    public static Condition<ConsumerRecord<String, String>> value(@NonNull String jsonPath, @NonNull BooleanCondition condition) {
        return value(record -> JsonReader.extractValue(record.value(), jsonPath, Boolean.class), condition);
    }

    /**
     * Создает условие для проверки строкового значения, извлеченного из JSON-тела сообщения по JSONPath.
     *
     * @param jsonPath  Путь JSONPath к полю (например, "$.user.name").
     * @param condition Строковое условие для проверки.
     * @return {@link Condition} для проверки значения из JSON.
     */
    public static Condition<ConsumerRecord<String, String>> value(@NonNull String jsonPath, @NonNull StringCondition condition) {
        return value(record -> JsonReader.extractValue(record.value(), jsonPath, String.class), condition);
    }

    /**
     * Создает условие для проверки произвольного свойства (Object), извлеченного из JSON-тела сообщения по JSONPath.
     *
     * @param jsonPath  Путь JSONPath к полю.
     * @param condition Условие для проверки свойства.
     * @return {@link Condition} для проверки значения из JSON.
     */
    public static Condition<ConsumerRecord<String, String>> value(@NonNull String jsonPath, @NonNull PropertyCondition<?> condition) {
        // Проверка для Object.class должна использовать PropertyCondition<?>, а не PropertyCondition<Object>
        @SuppressWarnings({"unchecked", "rawtypes"})
        PropertyCondition rawCondition = condition;
        return value(record -> JsonReader.extractValue(record.value(), jsonPath, Object.class), rawCondition);
    }

    /**
     * Создает условие для проверки числового значения, извлеченного из JSON-тела сообщения по JSONPath.
     *
     * @param jsonPath  Путь JSONPath к полю.
     * @param condition Числовое условие для проверки.
     * @param type      Класс ожидаемого числового типа (например, Integer.class, BigDecimal.class).
     * @param <T>       Тип числа (должен быть {@link Number} и {@link Comparable}).
     * @return {@link Condition} для проверки значения из JSON.
     */
    public static <T extends Number & Comparable<T>> Condition<ConsumerRecord<String, String>> value(
            @NonNull String jsonPath,
            @NonNull NumberCondition<T> condition,
            @NonNull Class<T> type) {
        return value(record -> JsonReader.extractValue(record.value(), jsonPath, type), condition);
    }

    /**
     * Универсальный метод для создания условия на основе функции-экстрактора и другого условия.
     *
     * @param getter    Функция для извлечения значения (поля) из {@link ConsumerRecord}.
     * @param condition Условие для проверки извлечённого значения.
     * @param <R>       Тип проверяемого свойства.
     * @return {@link Condition}, которое применяет {@code condition} к результату {@code getter}.
     * @throws NullPointerException если {@code getter} или {@code condition} равны null.
     */
    public static <R> Condition<ConsumerRecord<String, String>> value(
            @NonNull Function<? super ConsumerRecord<String, String>, ? extends R> getter,
            @NonNull Condition<? super R> condition) {
        return record -> PropertyMatcher.value(getter, condition).check(record);
    }
}
