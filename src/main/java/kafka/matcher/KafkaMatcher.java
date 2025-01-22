package kafka.matcher;

import com.jayway.jsonpath.JsonPath;
import kafka.matcher.assertions.CompositeAssertions;
import kafka.matcher.assertions.JsonPathConditions.JsonPathCondition;
import kafka.matcher.assertions.NumberAssertions.NumberCondition;
import kafka.matcher.assertions.RecordAssertions.RecordCondition;
import kafka.matcher.assertions.StringAssertions.StringCondition;
import kafka.matcher.assertions.TimestampAssertions.TimestampCondition;
import kafka.matcher.condition.Condition;
import kafka.matcher.condition.Conditions;
import lombok.experimental.UtilityClass;

import java.time.Instant;

/**
 * Основной класс, предоставляющий DSL для проверки Kafka записей.
 * Предоставляет статические методы для создания и комбинирования условий.
 */
@UtilityClass
public class KafkaMatcher {

    /**
     * Создает {@link Conditions} из {@link RecordCondition}.
     *
     * @param rc условие для проверки списка записей
     * @return обертка для проверки списка записей
     */
    public static Conditions records(RecordCondition rc) {
        return rc::check;
    }

    /**
     * Создает {@link Condition} для проверки значения записи строковыми условиями.
     *
     * @param sc строковое условие
     * @return условие для одной записи
     */
    public static Condition value(StringCondition sc) {
        return record -> sc.check(record.value());
    }

    /**
     * Создает {@link Condition} для проверки значения записи, извлеченного по JsonPath.
     *
     * @param jsonPath путь JsonPath
     * @param jc       условие для значения, извлеченного по JsonPath
     * @return условие для одной записи
     */
    public static Condition value(String jsonPath, JsonPathCondition jc) {
        return record -> {
            Object value = JsonPath.parse(record.value()).read(jsonPath);
            jc.check(value, jsonPath);
        };
    }

    /**
     * Создает {@link Condition} для проверки строкового значения по JsonPath.
     */
    public static Condition value(String jsonPath, StringCondition sc) {
        return record -> {
            Object value = JsonPath.parse(record.value()).read(jsonPath);
            sc.check((String) value);
        };
    }

    /**
     * Создает {@link Condition} для проверки числового значения по JsonPath.
     */
    public static <T extends Number & Comparable<T>> Condition value(String jsonPath, NumberCondition<T> nc, Class<T> type) {
        return record -> {
            Object value = JsonPath.parse(record.value()).read(jsonPath);
            nc.check(type.cast(value));
        };
    }

    /**
     * Создает {@link Condition} для проверки ключа записи строковыми условиями.
     *
     * @param sc строковое условие
     * @return условие для одной записи
     */
    public static Condition key(StringCondition sc) {
        return record -> sc.check(record.key());
    }

    /**
     * Создает {@link Condition} для проверки топика записи строковыми условиями.
     *
     * @param sc строковое условие
     * @return условие для одной записи
     */
    public static Condition topic(StringCondition sc) {
        return record -> sc.check(record.topic());
    }

    /**
     * Создает {@link Condition} для проверки партиции записи числовыми условиями.
     *
     * @param nc числовое условие
     * @return условие для одной записи
     */
    public static Condition partition(NumberCondition<Integer> nc) {
        return record -> nc.check(record.partition());
    }

    /**
     * Создает {@link Condition} для проверки смещения записи числовыми условиями.
     *
     * @param nc числовое условие
     * @return условие для одной записи
     */
    public static Condition offset(NumberCondition<Long> nc) {
        return record -> nc.check(record.offset());
    }

    /**
     * Создает {@link Condition} для проверки временной метки записи.
     *
     * @param tc условие для временной метки
     * @return условие для одной записи
     */
    public static Condition timestamp(TimestampCondition tc) {
        return record -> tc.check(Instant.ofEpochMilli(record.timestamp()));
    }

    // ------------------- Композиционные условия -------------------

    /**
     * Объединяет несколько условий логической операцией AND.
     *
     * @param conditions условия для объединения
     * @return условие, которое пройдет только если все условия будут истинны
     */
    public static Condition allOf(Condition... conditions) {
        return CompositeAssertions.and(conditions);
    }

    /**
     * Объединяет несколько условий логической операцией OR.
     *
     * @param conditions условия для объединения
     * @return условие, которое пройдет если хотя бы одно условие будет истинно
     */
    public static Condition anyOf(Condition... conditions) {
        return CompositeAssertions.or(conditions);
    }

    /**
     * Инвертирует результаты указанных условий.
     *
     * @param conditions условия для инвертирования
     * @return условие, которое пройдет только если все указанные условия будут ложны
     */
    public static Condition not(Condition... conditions) {
        return CompositeAssertions.not(conditions);
    }

    /**
     * Проверяет, что хотя бы N из указанных условий истинны.
     *
     * @param n          минимальное количество истинных условий
     * @param conditions условия для проверки
     * @return условие для одной записи
     */
    public static Condition nOf(int n, Condition... conditions) {
        return CompositeAssertions.nOf(n, conditions);
    }
}
