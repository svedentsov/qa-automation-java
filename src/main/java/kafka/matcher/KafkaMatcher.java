package kafka.matcher;

import com.jayway.jsonpath.JsonPath;
import kafka.matcher.assertions.CollectionAssertions.CollectionCondition;
import kafka.matcher.assertions.CompositeAssertions;
import kafka.matcher.assertions.NumberAssertions.NumberCondition;
import kafka.matcher.assertions.StringAssertions.StringCondition;
import kafka.matcher.assertions.TimeAssertions.TimestampCondition;
import kafka.matcher.assertions.BooleanAssertions.BooleanCondition;
import kafka.matcher.condition.Condition;
import kafka.matcher.condition.Conditions;
import lombok.experimental.UtilityClass;

import java.time.Instant;

/**
 * Основной класс, предоставляющий DSL для проверки Kafka записей.
 * Содержит статические методы для создания условий проверки отдельных полей записи,
 * а также для объединения условий.
 */
@UtilityClass
public class KafkaMatcher {

    // =================== Условия для проверки списка записей ===================

    /**
     * Создает условие для проверки списка записей.
     *
     * @param cc условие для проверки списка записей
     * @return условие, применимое к списку записей
     */
    public static Conditions records(CollectionCondition cc) {
        return cc::check;
    }

    // =================== Условия для проверки значения записи ===================

    /**
     * Создает условие для проверки значения записи с использованием строкового условия.
     * Перед проверкой выполняется проверка, что значение имеет тип {@code String}.
     *
     * @param sc строковое условие
     * @return условие для проверки значения записи
     * @throws AssertionError если значение записи не является строкой
     */
    public static Condition value(StringCondition sc) {
        return record -> {
            Object val = record.value();
            if (!(val instanceof String)) {
                throw new AssertionError("Ожидалось, что значение записи будет строкой, но было: " + val);
            }
            sc.check((String) val);
        };
    }

    /**
     * Создает условие для проверки числового значения, извлеченного по JSONPath.
     * Проводится проверка, что извлеченное значение имеет ожидаемый числовой тип.
     *
     * @param jsonPath путь JSONPath
     * @param nc       числовое условие
     * @param type     класс ожидаемого числового типа
     * @param <T>      тип числа
     * @return условие для проверки числового значения
     * @throws AssertionError если извлеченное значение не соответствует ожидаемому типу
     */
    public static <T extends Number & Comparable<T>> Condition value(String jsonPath, NumberCondition<T> nc, Class<T> type) {
        return record -> {
            T val = getJsonValue(record.value(), jsonPath, type);
            nc.check(val);
        };
    }

    /**
     * Создает условие для проверки строкового значения, извлеченного по JSONPath.
     * Перед проверкой выполняется проверка, что извлеченное значение имеет тип {@code String}.
     *
     * @param jsonPath путь JSONPath
     * @param sc       строковое условие
     * @return условие для проверки строкового значения
     * @throws AssertionError если извлеченное значение не является строкой
     */
    public static Condition value(String jsonPath, StringCondition sc) {
        return record -> {
            String val = getJsonValue(record.value(), jsonPath, String.class);
            sc.check(val);
        };
    }

    /**
     * Создает условие для проверки булевого значения, извлеченного по JSONPath.
     * Перед проверкой выполняется проверка, что извлеченное значение имеет тип {@code Boolean}.
     *
     * @param jsonPath путь JSONPath
     * @param bc       булевое условие
     * @return условие для проверки булевого значения
     * @throws AssertionError если извлеченное значение не является булевым
     */
    public static Condition value(String jsonPath, BooleanCondition bc) {
        return record -> {
            Boolean val = getJsonValue(record.value(), jsonPath, Boolean.class);
            bc.check(val);
        };
    }

    // =================== Условия для проверки других полей записи ===================

    /**
     * Создает условие для проверки ключа записи с использованием строкового условия.
     *
     * @param sc строковое условие
     * @return условие для проверки ключа
     */
    public static Condition key(StringCondition sc) {
        return record -> sc.check(record.key());
    }

    /**
     * Создает условие для проверки имени топика записи с использованием строкового условия.
     *
     * @param sc строковое условие
     * @return условие для проверки топика
     */
    public static Condition topic(StringCondition sc) {
        return record -> sc.check(record.topic());
    }

    /**
     * Создает условие для проверки номера партиции записи с использованием числового условия.
     *
     * @param nc числовое условие
     * @return условие для проверки номера партиции
     */
    public static Condition partition(NumberCondition<Integer> nc) {
        return record -> nc.check(record.partition());
    }

    /**
     * Создает условие для проверки смещения записи с использованием числового условия.
     *
     * @param nc числовое условие
     * @return условие для проверки смещения записи
     */
    public static Condition offset(NumberCondition<Long> nc) {
        return record -> nc.check(record.offset());
    }

    /**
     * Создает условие для проверки временной метки записи с использованием условия для {@code Instant}.
     *
     * @param tc условие для проверки временной метки
     * @return условие для проверки временной метки
     */
    public static Condition timestamp(TimestampCondition tc) {
        return record -> tc.check(Instant.ofEpochMilli(record.timestamp()));
    }

    // =================== Композиционные условия ===================

    /**
     * Объединяет несколько условий логической операцией AND.
     *
     * @param conditions условия для объединения
     * @return составное условие, которое проходит только если все условия истинны
     */
    public static Condition and(Condition... conditions) {
        return CompositeAssertions.and(conditions);
    }

    /**
     * Объединяет несколько условий логической операцией OR.
     *
     * @param conditions условия для объединения
     * @return составное условие, которое проходит, если хотя бы одно условие истинно
     */
    public static Condition or(Condition... conditions) {
        return CompositeAssertions.or(conditions);
    }

    /**
     * Инвертирует результаты указанных условий (логическое НЕ).
     *
     * @param conditions условия для инвертирования
     * @return условие, которое проходит только если все указанные условия не выполнены
     */
    public static Condition not(Condition... conditions) {
        return CompositeAssertions.not(conditions);
    }

    /**
     * Проверяет, что хотя бы n из указанных условий истинны.
     *
     * @param n          минимальное число истинных условий
     * @param conditions условия для проверки
     * @return условие, которое проходит если хотя бы n условий истинны
     */
    public static Condition nOf(int n, Condition... conditions) {
        return CompositeAssertions.nOf(n, conditions);
    }

    // =================== Вспомогательные методы ===================

    /**
     * Вспомогательный метод для извлечения значения из JSON по указанному пути с проверкой типа.
     *
     * @param recordValue  значение записи (в формате JSON)
     * @param jsonPath     путь JSONPath
     * @param expectedType класс ожидаемого типа
     * @param <T>          тип извлекаемого значения
     * @return извлеченное значение, приведенное к ожидаемому типу
     * @throws AssertionError если извлеченное значение не соответствует ожидаемому типу
     */
    private static <T> T getJsonValue(String recordValue, String jsonPath, Class<T> expectedType) {
        Object val = JsonPath.parse(recordValue).read(jsonPath);
        if (!expectedType.isInstance(val)) {
            throw new AssertionError(String.format("Ожидалось, что значение по пути %s будет типа %s, но было: %s",
                    jsonPath, expectedType.getSimpleName(), val));
        }
        return expectedType.cast(val);
    }
}
