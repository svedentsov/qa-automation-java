package kafka.matcher;

import com.jayway.jsonpath.JsonPath;
import kafka.matcher.assertions.BooleanAssertions.BooleanCondition;
import kafka.matcher.assertions.CompositeAssertions;
import kafka.matcher.assertions.NumberAssertions.NumberCondition;
import kafka.matcher.assertions.StringAssertions.StringCondition;
import kafka.matcher.assertions.TimeAssertions.TimeCondition;
import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Instant;
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
        return valueInternal(ConsumerRecord::value, sc::check);
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
        return record -> {
            T val = getJsonValue(record.value(), jsonPath, type);
            nc.check(val);
        };
    }

    /**
     * Создаёт проверку для булевого значения, извлечённого из JSON-записи по указанному пути.
     *
     * @param jsonPath путь JSONPath для извлечения значения
     * @param bc       булевое условие для проверки
     * @return условие для проверки булевого значения
     */
    public static Condition<ConsumerRecord<String, String>> value(String jsonPath, BooleanCondition bc) {
        return record -> {
            Boolean val = getJsonValue(record.value(), jsonPath, Boolean.class);
            bc.check(val);
        };
    }

    /**
     * Создаёт проверку для строкового значения, извлечённого из JSON-записи по указанному пути.
     *
     * @param jsonPath путь JSONPath для извлечения значения
     * @param sc       строковое условие для проверки
     * @return условие для проверки строкового значения
     */
    public static Condition<ConsumerRecord<String, String>> value(String jsonPath, StringCondition sc) {
        return record -> {
            String val = getJsonValue(record.value(), jsonPath, String.class);
            sc.check(val);
        };
    }

    /**
     * Создаёт проверку для ключа записи с использованием строкового условия.
     *
     * @param sc строковое условие для проверки ключа
     * @return условие для проверки ключа записи
     */
    public static Condition<ConsumerRecord<String, String>> key(StringCondition sc) {
        return valueInternal(ConsumerRecord::key, sc::check);
    }

    /**
     * Создаёт проверку для имени топика записи с использованием строкового условия.
     *
     * @param sc строковое условие для проверки топика
     * @return условие для проверки топика записи
     */
    public static Condition<ConsumerRecord<String, String>> topic(StringCondition sc) {
        return valueInternal(ConsumerRecord::topic, sc::check);
    }

    /**
     * Создаёт проверку для номера партиции записи с использованием числового условия.
     *
     * @param nc числовое условие для проверки партиции
     * @return условие для проверки номера партиции записи
     */
    public static Condition<ConsumerRecord<String, String>> partition(NumberCondition<Integer> nc) {
        return record -> nc.check(record.partition());
    }

    /**
     * Создаёт проверку для смещения записи с использованием числового условия.
     *
     * @param nc числовое условие для проверки смещения
     * @return условие для проверки смещения записи
     */
    public static Condition<ConsumerRecord<String, String>> offset(NumberCondition<Long> nc) {
        return record -> nc.check(record.offset());
    }

    /**
     * Создаёт проверку для временной метки записи с использованием условия для {@link Instant}.
     *
     * @param tc условие для проверки временной метки
     * @return условие для проверки временной метки записи
     */
    public static Condition<ConsumerRecord<String, String>> timestamp(TimeCondition tc) {
        return record -> tc.check(Instant.ofEpochMilli(record.timestamp()));
    }

    /**
     * Объединяет несколько проверок в одну с помощью логической операции AND.
     *
     * @param conditions набор проверок для объединения
     * @return составное условие, которое считается выполненным, если выполнены все переданные проверки
     */
    @SafeVarargs
    public static Condition<ConsumerRecord<String, String>> and(Condition<ConsumerRecord<String, String>>... conditions) {
        return CompositeAssertions.and(conditions);
    }

    /**
     * Объединяет несколько проверок в одну с помощью логической операции OR.
     *
     * @param conditions набор проверок для объединения
     * @return составное условие, которое считается выполненным, если выполнена хотя бы одна из переданных проверок
     */
    @SafeVarargs
    public static Condition<ConsumerRecord<String, String>> or(Condition<ConsumerRecord<String, String>>... conditions) {
        return CompositeAssertions.or(conditions);
    }

    /**
     * Инвертирует результаты переданных проверок (логическая операция NOT).
     *
     * @param conditions набор проверок для инвертирования
     * @return составное условие, которое считается выполненным, если ни одна из переданных проверок не выполнена
     */
    @SafeVarargs
    public static Condition<ConsumerRecord<String, String>> not(Condition<ConsumerRecord<String, String>>... conditions) {
        return CompositeAssertions.not(conditions);
    }

    /**
     * Объединяет несколько проверок так, что хотя бы n из них должны выполниться.
     *
     * @param n          минимальное число проверок, которые должны выполниться
     * @param conditions набор проверок для объединения
     * @return составное условие, которое считается выполненным, если выполнено хотя бы n проверок
     */
    @SafeVarargs
    public static Condition<ConsumerRecord<String, String>> nOf(int n, Condition<ConsumerRecord<String, String>>... conditions) {
        return CompositeAssertions.nOf(n, conditions);
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
        Object val = JsonPath.parse(json).read(jsonPath);
        if (!expectedType.isInstance(val)) {
            throw new AssertionError(String.format("Ожидалось, что значение по пути '%s' будет типа %s, но было: %s",
                    jsonPath, expectedType.getSimpleName(), val));
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
}
