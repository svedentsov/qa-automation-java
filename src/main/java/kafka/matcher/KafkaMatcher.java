package kafka.matcher;

import com.jayway.jsonpath.JsonPath;
import kafka.matcher.assertions.BooleanAssertions.BooleanCondition;
import kafka.matcher.assertions.InstantAssertions.InstantCondition;
import kafka.matcher.assertions.NumberAssertions.NumberCondition;
import kafka.matcher.assertions.StringAssertions.StringCondition;
import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Instant;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Утилитный класс, предоставляющий DSL для создания проверок (Condition)
 * для Kafka-записей. Позволяет создавать проверки для различных полей записи
 * (ключ, значение, топик, партиция, смещение, временная метка) и для значений,
 * извлекаемых по JSONPath.
 */
@UtilityClass
public class KafkaMatcher {

    /**
     * Создаёт условие для проверки свойства Kafka-записи с использованием функции-геттера
     * и условия для проверяемого свойства.
     *
     * @param getter функция для получения свойства из записи
     * @param cond   условие для проверки полученного значения
     * @param <R>    тип свойства
     * @return условие для проверки Kafka-записи
     */
    public static <R> Condition<ConsumerRecord<String, String>> value(Function<ConsumerRecord<String, String>, R> getter, Condition<R> cond) {
        return valueInternal(getter, cond::check);
    }

    /**
     * Создаёт проверку для значения записи с использованием строкового условия.
     *
     * @param sc строковое условие для проверки значения
     * @return условие для проверки значения записи
     */
    public static Condition<ConsumerRecord<String, String>> value(StringCondition sc) {
        return property(ConsumerRecord::value, sc);
    }

    /**
     * Создаёт проверку для числового значения, извлечённого из JSON-записи по указанному пути.
     *
     * @param jsonPath путь JSONPath для извлечения значения
     * @param nc       числовое условие для проверки
     * @param type     класс ожидаемого числового типа
     * @param <T>      тип числа, наследующий Number и Comparable
     * @return условие для проверки числового значения
     */
    public static <T extends Number & Comparable<T>> Condition<ConsumerRecord<String, String>> value(String jsonPath, NumberCondition<T> nc, Class<T> type) {
        return jsonValue(jsonPath, nc::check, type);
    }

    /**
     * Создаёт проверку для булевого значения, извлечённого из JSON-записи по указанному пути.
     *
     * @param jsonPath путь JSONPath для извлечения значения
     * @param bc       булевое условие для проверки
     * @return условие для проверки булевого значения
     */
    public static Condition<ConsumerRecord<String, String>> value(String jsonPath, BooleanCondition bc) {
        return jsonValue(jsonPath, bc::check, Boolean.class);
    }

    /**
     * Создаёт проверку для строкового значения, извлечённого из JSON-записи по указанному пути.
     *
     * @param jsonPath путь JSONPath для извлечения значения
     * @param sc       строковое условие для проверки
     * @return условие для проверки строкового значения
     */
    public static Condition<ConsumerRecord<String, String>> value(String jsonPath, StringCondition sc) {
        return jsonValue(jsonPath, sc::check, String.class);
    }

    /**
     * Создаёт проверку для ключа записи с использованием строкового условия.
     *
     * @param sc строковое условие для проверки ключа
     * @return условие для проверки ключа записи
     */
    public static Condition<ConsumerRecord<String, String>> key(StringCondition sc) {
        return property(ConsumerRecord::key, sc);
    }

    /**
     * Создаёт проверку для имени топика записи с использованием строкового условия.
     *
     * @param sc строковое условие для проверки топика
     * @return условие для проверки топика записи
     */
    public static Condition<ConsumerRecord<String, String>> topic(StringCondition sc) {
        return property(ConsumerRecord::topic, sc);
    }

    /**
     * Создаёт проверку для номера партиции записи с использованием числового условия.
     *
     * @param nc числовое условие для проверки партиции
     * @return условие для проверки номера партиции записи
     */
    public static Condition<ConsumerRecord<String, String>> partition(NumberCondition<Integer> nc) {
        return property(ConsumerRecord::partition, nc::check);
    }

    /**
     * Создаёт проверку для смещения записи с использованием числового условия.
     *
     * @param nc числовое условие для проверки смещения
     * @return условие для проверки смещения записи
     */
    public static Condition<ConsumerRecord<String, String>> offset(NumberCondition<Long> nc) {
        return property(ConsumerRecord::offset, nc::check);
    }

    /**
     * Создаёт проверку для временной метки записи с использованием условия для {@link Instant}.
     *
     * @param tc условие для проверки временной метки
     * @return условие для проверки временной метки записи
     */
    public static Condition<ConsumerRecord<String, String>> timestamp(InstantCondition tc) {
        return property(record -> Instant.ofEpochMilli(record.timestamp()), tc::check);
    }

    // =================== Вспомогательные методы ===================

    /**
     * Вспомогательный метод для извлечения значения из JSON по указанному пути с проверкой типа.
     *
     * @param json         строка в формате JSON
     * @param jsonPath     путь JSONPath для извлечения значения
     * @param expectedType класс ожидаемого типа значения
     * @param <T>          тип извлекаемого значения
     * @return извлечённое значение, приведённое к ожидаемому типу
     * @throws AssertionError если извлечённое значение не соответствует ожидаемому типу
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

    /**
     * Внутренний универсальный метод для создания условия на основе функции-геттера
     * и потребителя, выполняющего проверку.
     *
     * @param getter  функция для получения свойства из Kafka-записи
     * @param checker потребитель, выполняющий проверку извлечённого свойства
     * @param <R>     тип свойства
     * @return условие для проверки Kafka-записи
     */
    private static <R> Condition<ConsumerRecord<String, String>> valueInternal(Function<ConsumerRecord<String, String>, R> getter, Consumer<R> checker) {
        return record -> checker.accept(getter.apply(record));
    }

    /**
     * Внутренний универсальный метод для создания условия на основе функции-геттера и условия для проверки.
     *
     * @param getter функция для получения свойства из Kafka-записи
     * @param cond   условие для проверки полученного значения
     * @param <R>    тип свойства
     * @return условие для проверки Kafka-записи
     */
    private static <R> Condition<ConsumerRecord<String, String>> property(Function<ConsumerRecord<String, String>, R> getter, Condition<R> cond) {
        return record -> cond.check(getter.apply(record));
    }

    /**
     * Внутренний универсальный метод для создания условия на основе JSONPath,
     * потребителя для проверки и ожидаемого типа.
     *
     * @param jsonPath     путь JSONPath для извлечения значения
     * @param checker      потребитель, выполняющий проверку извлечённого значения
     * @param expectedType класс ожидаемого типа значения
     * @param <T>          тип извлекаемого значения
     * @return условие для проверки значения из JSON
     */
    private static <T> Condition<ConsumerRecord<String, String>> jsonValue(String jsonPath, Consumer<T> checker, Class<T> expectedType) {
        return record -> {
            T val = getJsonValue(record.value(), jsonPath, expectedType);
            checker.accept(val);
        };
    }
}
