package db.matcher.assertions;

import db.matcher.condition.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.math.BigDecimal;

/**
 * Утилитный класс для проверки числовых свойств сущности.
 * Предоставляет условия для сравнения числовых значений.
 */
@UtilityClass
public class NumberAssertions {

    /**
     * Функциональный интерфейс для проверки числовых значений.
     *
     * @param <T> тип числа (например, Integer, Long, Float, Double, BigDecimal и т.д.), реализующий Comparable
     */
    @FunctionalInterface
    public interface NumberCondition<T extends Number & Comparable<T>> {
        /**
         * Проверяет числовое значение.
         *
         * @param value число для проверки
         */
        void check(T value);
    }

    /**
     * Возвращает условие, что число равно ожидаемому значению.
     *
     * @param expected ожидаемое значение
     * @param <T>      тип числа
     * @return условие проверки равенства
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> equalTo(T expected) {
        return value -> Assertions.assertThat(value)
                .as("Значение должно быть равно %s", expected)
                .isEqualTo(expected);
    }

    /**
     * Проверяет, что числовое значение больше заданного порогового значения.
     * Значение преобразуется в BigDecimal для сравнения.
     *
     * @param threshold пороговое значение
     * @param <T>       тип числа
     * @return условие, проверяющее, что число больше указанного порога
     */
    public static <T extends Number> Condition<T> greaterThan(BigDecimal threshold) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно быть больше %s, но было %s", threshold, actual)
                    .isGreaterThan(threshold);
        };
    }

    /**
     * Проверяет, что числовое значение меньше заданного порогового значения.
     * Значение преобразуется в BigDecimal для сравнения.
     *
     * @param threshold пороговое значение
     * @param <T>       тип числа
     * @return условие, проверяющее, что число меньше указанного порога
     */
    public static <T extends Number> Condition<T> lessThan(BigDecimal threshold) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно быть меньше %s, но было %s", threshold, actual)
                    .isLessThan(threshold);
        };
    }

    /**
     * Проверяет, что числовое значение больше или равно заданному пороговому значению.
     *
     * @param threshold пороговое значение
     * @param <T>       тип числа
     * @return условие, проверяющее, что число больше или равно указанному порогу
     */
    public static <T extends Number> Condition<T> greaterThanOrEqualTo(BigDecimal threshold) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно быть больше или равно %s, но было %s", threshold, actual)
                    .isGreaterThanOrEqualTo(threshold);
        };
    }

    /**
     * Проверяет, что числовое значение меньше или равно заданному пороговому значению.
     *
     * @param threshold пороговое значение
     * @param <T>       тип числа
     * @return условие, проверяющее, что число меньше или равно указанному порогу
     */
    public static <T extends Number> Condition<T> lessThanOrEqualTo(BigDecimal threshold) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно быть меньше или равно %s, но было %s", threshold, actual)
                    .isLessThanOrEqualTo(threshold);
        };
    }

    /**
     * Проверяет, что числовое значение находится в диапазоне [start, end] (границы включаются).
     *
     * @param start начало диапазона (включительно)
     * @param end   конец диапазона (включительно)
     * @param <T>   тип числа
     * @return условие, проверяющее, что число находится в заданном диапазоне
     */
    public static <T extends Number> Condition<T> between(BigDecimal start, BigDecimal end) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно находиться между %s и %s, но было %s", start, end, actual)
                    .isBetween(start, end);
        };
    }

    /**
     * Проверяет, что числовое значение находится строго между start и end (границы не включаются).
     *
     * @param start начало диапазона (исключается)
     * @param end   конец диапазона (исключается)
     * @param <T>   тип числа
     * @return условие, проверяющее, что число строго между start и end
     */
    public static <T extends Number> Condition<T> strictlyBetween(BigDecimal start, BigDecimal end) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно находиться строго между %s и %s, но было %s", start, end, actual)
                    .isStrictlyBetween(start, end);
        };
    }

    /**
     * Проверяет, что числовое значение находится вне диапазона [start, end] (границы включаются).
     *
     * @param start начало диапазона
     * @param end   конец диапазона
     * @param <T>   тип числа
     * @return условие, проверяющее, что число не находится в диапазоне [start, end]
     */
    public static <T extends Number> Condition<T> notBetween(BigDecimal start, BigDecimal end) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            boolean conditionMet = actual.compareTo(start) < 0 || actual.compareTo(end) > 0;
            Assertions.assertThat(conditionMet)
                    .as("Значение должно быть вне диапазона [%s, %s], но было %s", start, end, actual)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что числовое значение равно нулю.
     *
     * @param <T> тип числа
     * @return условие, проверяющее, что число равно 0
     */
    public static <T extends Number> Condition<T> propertyIsZero() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual.compareTo(BigDecimal.ZERO) == 0)
                    .as("Значение должно быть равно 0, но было %s", actual)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что числовое значение не равно нулю.
     *
     * @param <T> тип числа
     * @return условие, проверяющее, что число не равно 0
     */
    public static <T extends Number> Condition<T> propertyIsNotZero() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual.compareTo(BigDecimal.ZERO) != 0)
                    .as("Значение не должно быть равно 0")
                    .isTrue();
        };
    }

    /**
     * Проверяет, что числовое значение положительное (строго больше 0).
     *
     * @param <T> тип числа
     * @return условие, проверяющее, что число положительное
     */
    public static <T extends Number> Condition<T> isPositive() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно быть положительным, но было %s", actual)
                    .isGreaterThan(BigDecimal.ZERO);
        };
    }

    /**
     * Проверяет, что числовое значение отрицательное (строго меньше 0).
     *
     * @param <T> тип числа
     * @return условие, проверяющее, что число отрицательное
     */
    public static <T extends Number> Condition<T> isNegative() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно быть отрицательным, но было %s", actual)
                    .isLessThan(BigDecimal.ZERO);
        };
    }

    /**
     * Проверяет, что числовое значение неотрицательное (больше или равно 0).
     *
     * @param <T> тип числа
     * @return условие, проверяющее, что число неотрицательное
     */
    public static <T extends Number> Condition<T> isNonNegative() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение не должно быть отрицательным, но было %s", actual)
                    .isGreaterThanOrEqualTo(BigDecimal.ZERO);
        };
    }

    /**
     * Проверяет, что числовое значение неположительное (меньше или равно 0).
     *
     * @param <T> тип числа
     * @return условие, проверяющее, что число неположительное
     */
    public static <T extends Number> Condition<T> isNonPositive() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение не должно быть положительным, но было %s", actual)
                    .isLessThanOrEqualTo(BigDecimal.ZERO);
        };
    }

    /**
     * Проверяет, что числовое значение приблизительно равно ожидаемому значению с заданной допустимой погрешностью.
     *
     * @param expected  ожидаемое значение
     * @param tolerance допустимая погрешность
     * @param <T>       тип числа
     * @return условие, проверяющее, что разница между фактическим и ожидаемым значениями не превышает tolerance
     */
    public static <T extends Number> Condition<T> approximatelyEqualTo(BigDecimal expected, BigDecimal tolerance) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            BigDecimal diff = actual.subtract(expected).abs();
            Assertions.assertThat(diff)
                    .as("Значение %s должно быть примерно равно %s с допустимой погрешностью %s, разница %s", actual, expected, tolerance, diff)
                    .isLessThanOrEqualTo(tolerance);
        };
    }

    /**
     * Проверяет, что числовое значение приблизительно равно нулю с заданной допустимой погрешностью.
     *
     * @param tolerance допустимая погрешность
     * @param <T>       тип числа
     * @return условие, проверяющее, что число примерно равно 0
     */
    public static <T extends Number> Condition<T> approximatelyZero(BigDecimal tolerance) {
        return approximatelyEqualTo(BigDecimal.ZERO, tolerance);
    }

    /**
     * Проверяет, что числовое значение не равно ожидаемому значению.
     *
     * @param unexpected значение, которое число не должно принимать
     * @param <T>        тип числа
     * @return условие, проверяющее, что число не равно unexpected
     */
    public static <T extends Number & Comparable<T>> Condition<T> notEqualTo(T unexpected) {
        return number -> Assertions.assertThat(number)
                .as("Значение не должно быть равно %s", unexpected)
                .isNotEqualTo(unexpected);
    }

    /**
     * Преобразует число в BigDecimal для сравнения.
     *
     * @param number число для преобразования
     * @param <T>    тип числа
     * @return BigDecimal представление числа
     */
    private static <T extends Number> BigDecimal toBigDecimal(T number) {
        // Используем строковое представление, чтобы избежать проблем с точностью при преобразовании.
        return new BigDecimal(number.toString());
    }
}
