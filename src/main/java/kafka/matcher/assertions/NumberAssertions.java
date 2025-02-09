package kafka.matcher.assertions;

import kafka.matcher.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

/**
 * Утилитный класс для создания числовых условий.
 */
@UtilityClass
public class NumberAssertions {

    /**
     * Функциональный интерфейс для числовых условий.
     *
     * @param <T> тип числа (например, Integer, Long, Float, Double, BigDecimal, BigInteger и т.д.)
     */
    @FunctionalInterface
    public interface NumberCondition<T extends Number & Comparable<T>> extends Condition<T> {
    }

    /**
     * Проверяет, что значение является числом.
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isNumber() {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть числом")
                .isInstanceOf(Number.class);
    }

    /**
     * Проверяет, что число равно ожидаемому значению.
     *
     * @param expected ожидаемое значение
     * @param <T>      тип числа
     * @return условие равенства
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> equalTo(T expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть равно %s", expected)
                .isEqualTo(expected);
    }

    /**
     * Проверяет, что число не равно ожидаемому значению.
     *
     * @param expected ожидаемое значение
     * @param <T>      тип числа
     * @return условие неравенства
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> notEqualTo(T expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение не должно быть равно %s", expected)
                .isNotEqualTo(expected);
    }

    /**
     * Проверяет, что число больше указанного значения.
     *
     * @param expected значение
     * @param <T>      тип числа
     * @return условие "больше"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> greaterThan(T expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть > %s", expected)
                .isGreaterThan(expected);
    }

    /**
     * Проверяет, что число меньше указанного значения.
     *
     * @param expected значение
     * @param <T>      тип числа
     * @return условие "меньше"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> lessThan(T expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть < %s", expected)
                .isLessThan(expected);
    }

    /**
     * Проверяет, что число больше или равно указанному значению.
     *
     * @param expected значение
     * @param <T>      тип числа
     * @return условие "больше или равно"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> greaterOrEqualTo(T expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть >= %s", expected)
                .isGreaterThanOrEqualTo(expected);
    }

    /**
     * Проверяет, что число меньше или равно указанному значению.
     *
     * @param expected значение
     * @param <T>      тип числа
     * @return условие "меньше или равно"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> lessOrEqualTo(T expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть <= %s", expected)
                .isLessThanOrEqualTo(expected);
    }

    /**
     * Проверяет, что число находится в диапазоне [start, end] (включительно).
     *
     * @param start начало диапазона
     * @param end   конец диапазона
     * @param <T>   тип числа
     * @return условие "в диапазоне"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> inRange(T start, T end) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть в диапазоне [%s, %s]", start, end)
                .isBetween(start, end);
    }

    /**
     * Проверяет, что число находится в диапазоне (start, end) (строго, без включения границ).
     *
     * @param start начало диапазона
     * @param end   конец диапазона
     * @param <T>   тип числа
     * @return условие "в диапазоне (start, end)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> betweenExclusive(T start, T end) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть в диапазоне (%s, %s)", start, end)
                .isGreaterThan(start)
                .isLessThan(end);
    }

    /**
     * Проверяет, что число равно нулю.
     *
     * @param <T> тип числа
     * @return условие "равно нулю"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isZero() {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть равно 0")
                .isEqualTo(0.0);
    }

    /**
     * Проверяет, что число не равно нулю.
     *
     * @param <T> тип числа
     * @return условие "не равно нулю"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isNotZero() {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение не должно быть равно 0")
                .isNotEqualTo(0.0);
    }

    /**
     * Проверяет, что число положительно.
     *
     * @param <T> тип числа
     * @return условие "положительное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isPositive() {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть положительным (больше 0)")
                .isGreaterThan(0.0);
    }

    /**
     * Проверяет, что число отрицательно.
     *
     * @param <T> тип числа
     * @return условие "отрицательное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isNegative() {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть отрицательным (меньше 0)")
                .isLessThan(0.0);
    }

    /**
     * Проверяет, что число неотрицательно (>= 0).
     *
     * @param <T> тип числа
     * @return условие "неотрицательное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isPositiveOrZero() {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть >= 0")
                .isGreaterThanOrEqualTo(0.0);
    }

    /**
     * Проверяет, что число неположительно (<= 0).
     *
     * @param <T> тип числа
     * @return условие "неположительное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isNegativeOrZero() {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть <= 0")
                .isLessThanOrEqualTo(0.0);
    }

    /**
     * Проверяет, что целочисленное число является чётным.
     *
     * @param <T> тип числа (предполагается, что это Integer, Long, Byte, Short и т.п.)
     * @return условие "чётное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isEven() {
        return actual -> {
            long value = actual.longValue();
            Assertions.assertThat(value % 2)
                    .as("Значение должно быть чётным")
                    .isEqualTo(0);
        };
    }

    /**
     * Проверяет, что целочисленное число является нечётным.
     *
     * @param <T> тип числа (предполагается, что это Integer, Long, Byte, Short и т.п.)
     * @return условие "нечётное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isOdd() {
        return actual -> {
            long value = actual.longValue();
            Assertions.assertThat(value % 2)
                    .as("Значение должно быть нечётным")
                    .isNotEqualTo(0);
        };
    }

    /**
     * Проверяет, что целочисленное число является кратным указанному делителю.
     *
     * @param divisor делитель
     * @param <T>     тип числа (предполагается, что это Integer, Long, Byte, Short и т.п.)
     * @return условие "кратно"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> multipleOf(long divisor) {
        return actual -> {
            long value = actual.longValue();
            Assertions.assertThat(value % divisor)
                    .as("Значение должно быть кратно %d", divisor)
                    .isEqualTo(0);
        };
    }
}
