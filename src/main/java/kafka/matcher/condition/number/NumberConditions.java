package kafka.matcher.condition.number;

import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

/**
 * Утилитный класс для создания числовых условий {@link NumberCondition}.
 * Все методы возвращают лямбда-выражения типа {@link NumberCondition}, которые можно применять к числовым значениям.
 */
@UtilityClass
public final class NumberConditions {

    /**
     * Проверяет, что число равно ожидаемому целочисленному значению.
     */
    public static NumberCondition<Integer> equalTo(int expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть равно %d", expected)
                .isEqualTo(expected);
    }

    /**
     * Проверяет, что число больше указанного значения.
     */
    public static NumberCondition<Integer> greaterThan(int expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть > %d", expected)
                .isGreaterThan(expected);
    }

    /**
     * Проверяет, что число меньше указанного значения.
     */
    public static NumberCondition<Integer> lessThan(int expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть < %d", expected)
                .isLessThan(expected);
    }

    /**
     * Проверяет, что число больше или равно указанному значению.
     */
    public static NumberCondition<Integer> greaterOrEqualTo(int expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть >= %d", expected)
                .isGreaterThanOrEqualTo(expected);
    }

    /**
     * Проверяет, что число меньше или равно указанному значению.
     */
    public static NumberCondition<Integer> lessOrEqualTo(int expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть <= %d", expected)
                .isLessThanOrEqualTo(expected);
    }

    /**
     * Проверяет, что число (Long) равно ожидаемому значению.
     */
    public static NumberCondition<Long> equalToLong(long expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть равно %d", expected)
                .isEqualTo(expected);
    }

    /**
     * Проверяет, что число (Long) больше указанного значения.
     */
    public static NumberCondition<Long> greaterThanLong(long expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть > %d", expected)
                .isGreaterThan(expected);
    }

    /**
     * Проверяет, что число (Long) меньше указанного значения.
     */
    public static NumberCondition<Long> lessThanLong(long expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть < %d", expected)
                .isLessThan(expected);
    }

    /**
     * Проверяет, что число (Long) больше или равно указанному значению.
     */
    public static NumberCondition<Long> greaterOrEqualToLong(long expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть >= %d", expected)
                .isGreaterThanOrEqualTo(expected);
    }

    /**
     * Проверяет, что число (Long) меньше или равно указанному значению.
     */
    public static NumberCondition<Long> lessOrEqualToLong(long expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть <= %d", expected)
                .isLessThanOrEqualTo(expected);
    }

    /**
     * Проверяет, что число не равно указанному целочисленному значению.
     */
    public static NumberCondition<Integer> notEqualTo(int expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение не должно быть равно %d", expected)
                .isNotEqualTo(expected);
    }

    /**
     * Проверяет, что число находится в указанном целочисленном диапазоне включительно.
     */
    public static NumberCondition<Integer> inRange(int start, int end) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть в диапазоне [%d, %d]", start, end)
                .isBetween(start, end);
    }

    /**
     * Проверяет, что число (Long) не равно указанному значению.
     */
    public static NumberCondition<Long> notEqualTo(long expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение не должно быть равно %d", expected)
                .isNotEqualTo(expected);
    }

    /**
     * Проверяет, что число (Long) находится в указанном диапазоне включительно.
     */
    public static NumberCondition<Long> inRange(long start, long end) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть в диапазоне [%d, %d]", start, end)
                .isBetween(start, end);
    }

    /**
     * Проверяет, что число целочисленное и положительное.
     */
    public static NumberCondition<Integer> isPositive() {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть положительным")
                .isGreaterThan(0);
    }

    /**
     * Проверяет, что число целочисленное и отрицательное.
     */
    public static NumberCondition<Integer> isNegative() {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть отрицательным")
                .isLessThan(0);
    }
}
