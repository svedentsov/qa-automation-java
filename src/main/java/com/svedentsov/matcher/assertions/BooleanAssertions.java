package com.svedentsov.matcher.assertions;

import com.svedentsov.matcher.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

/**
 * Утилитный класс для создания условий проверки булевых значений.
 */
@UtilityClass
public class BooleanAssertions {

    /**
     * Функциональный интерфейс для условий проверки булевого значения.
     */
    @FunctionalInterface
    public interface BooleanCondition extends Condition<Boolean> {
    }

    /**
     * Значение является булевым.
     */
    public static BooleanCondition isBoolean() {
        return value -> Assertions.assertThat(value)
                .as("Значение должно быть boolean")
                .isInstanceOf(Boolean.class);
    }

    /**
     * Булево значение равно true.
     *
     * @return условие, которое проходит если значение true
     */
    public static BooleanCondition isTrue() {
        return value -> Assertions.assertThat(value)
                .as("Значение должно быть true")
                .isTrue();
    }

    /**
     * Булево значение равно false.
     *
     * @return условие, которое проходит если значение false
     */
    public static BooleanCondition isFalse() {
        return value -> Assertions.assertThat(value)
                .as("Значение должно быть false")
                .isFalse();
    }

    /**
     * Булево значение является null.
     *
     * @return условие, которое проходит если значение null
     */
    public static BooleanCondition isNull() {
        return value -> Assertions.assertThat(value)
                .as("Значение должно быть null")
                .isNull();
    }

    /**
     * Булево значение не является null.
     *
     * @return условие, которое проходит если значение не null
     */
    public static BooleanCondition isNotNull() {
        return value -> Assertions.assertThat(value)
                .as("Значение не должно быть null")
                .isNotNull();
    }
}
