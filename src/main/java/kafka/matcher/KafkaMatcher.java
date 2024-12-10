package kafka.matcher;

import com.jayway.jsonpath.JsonPath;
import kafka.matcher.condition.Condition;
import kafka.matcher.condition.Conditions;
import kafka.matcher.condition.composite.CompositeConditions;
import kafka.matcher.condition.jsonpath.JsonPathCondition;
import kafka.matcher.condition.number.NumberCondition;
import kafka.matcher.condition.record.RecordCondition;
import kafka.matcher.condition.string.StringCondition;
import kafka.matcher.condition.timestamp.TimestampCondition;
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
     * @param rc условие для списка записей
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
            String actual = record.value();
            Object val = JsonPath.parse(actual).read(jsonPath);
            jc.check(val, jsonPath);
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
        return CompositeConditions.and(conditions);
    }

    /**
     * Объединяет несколько условий логической операцией OR.
     *
     * @param conditions условия для объединения
     * @return условие, которое пройдет если хотя бы одно условие будет истинно
     */
    public static Condition anyOf(Condition... conditions) {
        return CompositeConditions.or(conditions);
    }

    /**
     * Инвертирует результаты указанных условий.
     *
     * @param conditions условия для инвертирования
     * @return условие, которое пройдет только если все указанные условия будут ложны
     */
    public static Condition not(Condition... conditions) {
        return CompositeConditions.not(conditions);
    }

    /**
     * Проверяет, что хотя бы N из указанных условий истинны.
     *
     * @param n          минимальное количество истинных условий
     * @param conditions условия для проверки
     * @return условие для одной записи
     */
    public static Condition nOf(int n, Condition... conditions) {
        return CompositeConditions.nOf(n, conditions);
    }
}
