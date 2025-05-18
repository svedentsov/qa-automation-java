package kafka.matcher;

import com.jayway.jsonpath.JsonPath;
import core.matcher.Condition;
import core.matcher.assertions.BooleanAssertions.BooleanCondition;
import core.matcher.assertions.InstantAssertions.InstantCondition;
import core.matcher.assertions.NumberAssertions.NumberCondition;
import core.matcher.assertions.StringAssertions.StringCondition;
import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Instant;
import java.util.Objects;
import java.util.function.Function;

/**
 * Утилитный класс, предоставляющий DSL для создания проверок (Condition)
 * для Kafka-записей. Все специфичные проверки сводятся к одному универсальному
 * методу {@link #value(Function, Condition)}, что упрощает поддержку и расширение.
 */
@UtilityClass
public class KafkaMatcher {

    /**
     * Проверка строкового значения всего тела записи.
     *
     * @param sc строковое условие для проверки
     * @return условие для проверки {@link ConsumerRecord#value()}
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
     * @return условие для проверки
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
     * @return условие для проверки
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
     * @return условие для проверки
     */
    public static <T extends Number & Comparable<T>> Condition<ConsumerRecord<String, String>> value(
            String jsonPath,
            NumberCondition<T> nc,
            Class<T> type) {
        return value(record -> getJsonValue(record.value(), jsonPath, type), nc);
    }

    /**
     * Проверка ключа записи.
     *
     * @param sc строковое условие для ключа
     * @return условие для проверки {@link ConsumerRecord#key()}
     */
    public static Condition<ConsumerRecord<String, String>> key(
            StringCondition sc) {
        return value(ConsumerRecord::key, sc);
    }

    /**
     * Проверка имени топика записи.
     *
     * @param sc строковое условие для топика
     * @return условие для проверки {@link ConsumerRecord#topic()}
     */
    public static Condition<ConsumerRecord<String, String>> topic(
            StringCondition sc) {
        return value(ConsumerRecord::topic, sc);
    }

    /**
     * Проверка номера партиции записи.
     *
     * @param nc числовое условие для проверки партиции
     * @return условие для проверки {@link ConsumerRecord#partition()}
     */
    public static Condition<ConsumerRecord<String, String>> partition(
            NumberCondition<Integer> nc) {
        return value(ConsumerRecord::partition, nc);
    }

    /**
     * Проверка смещения записи.
     *
     * @param nc числовое условие для проверки смещения
     * @return условие для проверки {@link ConsumerRecord#offset()}
     */
    public static Condition<ConsumerRecord<String, String>> offset(
            NumberCondition<Long> nc) {
        return value(ConsumerRecord::offset, nc);
    }

    /**
     * Проверка временной метки записи.
     *
     * @param ic условие для проверки {@link Instant}
     * @return условие для проверки {@code Instant.ofEpochMilli(record.timestamp())}
     */
    public static Condition<ConsumerRecord<String, String>> timestamp(
            InstantCondition ic) {
        return value(record -> Instant.ofEpochMilli(record.timestamp()), ic);
    }

    /**
     * Универсальный метод для создания условия на основе функции-геттера и другого условия для проверяемого значения.
     *
     * @param getter функция для извлечения свойства из записи
     * @param cond   условие для проверки извлечённого значения
     * @param <R>    тип проверяемого свойства
     * @return условие, которое применяет переданное {@code cond} к результату {@code getter}
     * @throws NullPointerException если {@code getter} или {@code cond} равны null
     */
    public static <R> Condition<ConsumerRecord<String, String>> value(
            Function<? super ConsumerRecord<String, String>, ? extends R> getter,
            Condition<? super R> cond
    ) {
        Objects.requireNonNull(getter, "getter не может быть null");
        Objects.requireNonNull(cond, "condition не может быть null");
        return record -> cond.check(getter.apply(record));
    }

    /**
     * Извлекает значение из JSON строки по JSONPath и проверяет его тип.
     *
     * @param json         исходная JSON-строка
     * @param jsonPath     путь JSONPath для извлечения
     * @param expectedType ожидаемый класс значения
     * @param <T>          тип значения
     * @return извлечённое и приведённое к {@code expectedType} значение
     * @throws AssertionError       если значение не совпало по типу
     * @throws NullPointerException если любой из аргументов null
     */
    private static <T> T getJsonValue(String json, String jsonPath, Class<T> expectedType) {
        Objects.requireNonNull(json, "JSON строка не может быть null");
        Objects.requireNonNull(jsonPath, "JSONPath не может быть null");
        Objects.requireNonNull(expectedType, "Ожидаемый тип не может быть null");

        Object val = JsonPath.parse(json).read(jsonPath);
        if (!expectedType.isInstance(val)) {
            String actualType = val != null ? val.getClass().getSimpleName() : "null";
            throw new AssertionError(String.format("Ожидалось, что значение по пути '%s' будет типа %s, но было: %s (%s)",
                    jsonPath, expectedType.getSimpleName(), val, actualType));
        }
        return expectedType.cast(val);
    }
}
