package com.svedentsov.matcher.assertions;

import com.svedentsov.matcher.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

/**
 * Утилитный класс для проверки числовых свойств сущности.
 */
@UtilityClass
public class NumberAssertions {

    /**
     * Функциональный интерфейс для проверки числовых значений.
     *
     * @param <T> тип числа, реализующий Comparable
     */
    @FunctionalInterface
    public interface NumberCondition<T extends Number & Comparable<T>> extends Condition<T> {
    }

    /**
     * Число равно ожидаемому значению.
     *
     * @param expected ожидаемое значение
     * @param <T>      тип числа
     * @return условие равенства
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberEqualTo(T expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть равно %s", expected)
                .isEqualTo(expected);
    }

    /**
     * Число не равно ожидаемому значению.
     *
     * @param expected ожидаемое значение
     * @param <T>      тип числа
     * @return условие неравенства
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberNotEqualTo(T expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение не должно быть равно %s", expected)
                .isNotEqualTo(expected);
    }

    /**
     * Число находится в диапазоне (исключая границы).
     *
     * @param start начало диапазона (исключается)
     * @param end   конец диапазона (исключается)
     * @param <T>   тип числа
     * @return условие, что число находится строго между start и end
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberBetweenExclusive(T start, T end) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть в диапазоне (%s, %s)", start, end)
                .isGreaterThan(start)
                .isLessThan(end);
    }

    /**
     * Число находится в диапазоне [start, end] (границы включаются).
     *
     * @param start начало диапазона
     * @param end   конец диапазона
     * @param <T>   тип числа
     * @return условие, что число находится в диапазоне
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberInRange(T start, T end) {
        return numberBetween(toBigDecimal(start), toBigDecimal(end));
    }

    /**
     * Число больше указанного порогового значения.
     *
     * @param threshold пороговое значение в BigDecimal
     * @param <T>       тип числа
     * @return условие "больше"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberGreaterThan(BigDecimal threshold) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно быть больше %s, но было %s", threshold, actual)
                    .isGreaterThan(threshold);
        };
    }

    /**
     * Перегруженная версия numberGreaterThan, принимающая значение типа T.
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberGreaterThan(T expected) {
        return numberGreaterThan(toBigDecimal(expected));
    }

    /**
     * Число меньше указанного порогового значения.
     *
     * @param threshold пороговое значение в BigDecimal
     * @param <T>       тип числа
     * @return условие "меньше"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberLessThan(BigDecimal threshold) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно быть меньше %s, но было %s", threshold, actual)
                    .isLessThan(threshold);
        };
    }

    /**
     * Перегруженная версия numberLessThan, принимающая значение типа T.
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberLessThan(T expected) {
        return numberLessThan(toBigDecimal(expected));
    }

    /**
     * Число больше или равно указанному порогу.
     *
     * @param threshold пороговое значение в BigDecimal
     * @param <T>       тип числа
     * @return условие "больше или равно"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberGreaterThanOrEqualTo(BigDecimal threshold) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно быть больше или равно %s, но было %s", threshold, actual)
                    .isGreaterThanOrEqualTo(threshold);
        };
    }

    /**
     * Перегруженная версия numberGreaterThanOrEqualTo, принимающая значение типа T.
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberGreaterThanOrEqualTo(T expected) {
        return numberGreaterThanOrEqualTo(toBigDecimal(expected));
    }

    /**
     * Число меньше или равно указанному порогу.
     *
     * @param threshold пороговое значение в BigDecimal
     * @param <T>       тип числа
     * @return условие "меньше или равно"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberLessThanOrEqualTo(BigDecimal threshold) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно быть меньше или равно %s, но было %s", threshold, actual)
                    .isLessThanOrEqualTo(threshold);
        };
    }

    /**
     * Перегруженная версия numberLessThanOrEqualTo, принимающая значение типа T.
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberLessThanOrEqualTo(T expected) {
        return numberLessThanOrEqualTo(toBigDecimal(expected));
    }

    /**
     * Число находится в диапазоне [start, end] (границы включаются).
     *
     * @param start начало диапазона в BigDecimal
     * @param end   конец диапазона в BigDecimal
     * @param <T>   тип числа
     * @return условие "в диапазоне"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberBetween(BigDecimal start, BigDecimal end) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно находиться между %s и %s, но было %s", start, end, actual)
                    .isBetween(start, end);
        };
    }

    /**
     * Перегруженная версия numberBetween, принимающая значения типа T.
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberBetween(T start, T end) {
        return numberBetween(toBigDecimal(start), toBigDecimal(end));
    }

    /**
     * Число находится строго между start и end (границы не включаются).
     *
     * @param start начало диапазона в BigDecimal
     * @param end   конец диапазона в BigDecimal
     * @param <T>   тип числа
     * @return условие "строго между"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberStrictlyBetween(BigDecimal start, BigDecimal end) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно находиться строго между %s и %s, но было %s", start, end, actual)
                    .isStrictlyBetween(start, end);
        };
    }

    /**
     * Перегруженная версия numberStrictlyBetween, принимающая значения типа T.
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberStrictlyBetween(T start, T end) {
        return numberStrictlyBetween(toBigDecimal(start), toBigDecimal(end));
    }

    /**
     * Число равно нулю.
     *
     * @param <T> тип числа
     * @return условие "равно 0"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsZero() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual.compareTo(BigDecimal.ZERO) == 0)
                    .as("Значение должно быть равно 0, но было %s", actual)
                    .isTrue();
        };
    }

    /**
     * Число равно нулю, алиас для numberIsZero().
     *
     * @param <T> тип числа
     * @return условие "равно 0"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberZero() {
        return numberIsZero();
    }

    /**
     * Число не равно нулю.
     *
     * @param <T> тип числа
     * @return условие "не равно 0"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsNotZero() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual.compareTo(BigDecimal.ZERO) != 0)
                    .as("Значение не должно быть равно 0, но было %s", actual)
                    .isTrue();
        };
    }

    /**
     * Число положительное (строго больше 0).
     *
     * @param <T> тип числа
     * @return условие "положительное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsPositive() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно быть положительным, но было %s", actual)
                    .isGreaterThan(BigDecimal.ZERO);
        };
    }

    /**
     * Число отрицательное (строго меньше 0).
     *
     * @param <T> тип числа
     * @return условие "отрицательное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsNegative() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно быть отрицательным, но было %s", actual)
                    .isLessThan(BigDecimal.ZERO);
        };
    }

    /**
     * Число неотрицательное (больше или равно 0).
     *
     * @param <T> тип числа
     * @return условие "неотрицательное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsNonNegative() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение не должно быть отрицательным, но было %s", actual)
                    .isGreaterThanOrEqualTo(BigDecimal.ZERO);
        };
    }

    /**
     * Число неположительное (меньше или равно 0).
     *
     * @param <T> тип числа
     * @return условие "неположительное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsNonPositive() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение не должно быть положительным, но было %s", actual)
                    .isLessThanOrEqualTo(BigDecimal.ZERO);
        };
    }

    /**
     * Число приблизительно равно ожидаемому значению с заданной абсолютной погрешностью.
     *
     * @param expected  ожидаемое значение
     * @param tolerance допустимая абсолютная погрешность
     * @param <T>       тип числа
     * @return условие приблизительного равенства
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberApproximatelyEqualTo(BigDecimal expected, BigDecimal tolerance) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            BigDecimal diff = actual.subtract(expected).abs();
            Assertions.assertThat(diff)
                    .as("Значение %s должно быть примерно равно %s с допустимой погрешностью %s, разница %s",
                            actual, expected, tolerance, diff)
                    .isLessThanOrEqualTo(tolerance);
        };
    }

    /**
     * Перегруженная версия numberApproximatelyEqualTo, принимающая значение типа T.
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberApproximatelyEqualTo(T expected, BigDecimal tolerance) {
        return numberApproximatelyEqualTo(toBigDecimal(expected), tolerance);
    }

    /**
     * Число приблизительно равно нулю с заданной абсолютной погрешностью.
     *
     * @param tolerance допустимая погрешность
     * @param <T>       тип числа
     * @return условие, проверяющее, что число примерно равно 0
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberApproximatelyZero(BigDecimal tolerance) {
        return numberApproximatelyEqualTo(BigDecimal.ZERO, tolerance);
    }

    /**
     * Число является целым (не имеет дробной части).
     *
     * @param <T> тип числа
     * @return условие "целое число"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsInteger() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            int scale = actual.stripTrailingZeros().scale();
            Assertions.assertThat(scale)
                    .as("Число %s должно быть целым, но имеет дробную часть", actual)
                    .isLessThanOrEqualTo(0);
        };
    }

    /**
     * Число чётное (применимо только к целым числам).
     *
     * @param <T> тип числа
     * @return условие "чётное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsEven() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            int scale = actual.stripTrailingZeros().scale();
            Assertions.assertThat(scale)
                    .as("Число %s должно быть целым для проверки чётности", actual)
                    .isLessThanOrEqualTo(0);
            BigDecimal remainder = actual.remainder(BigDecimal.valueOf(2));
            Assertions.assertThat(remainder)
                    .as("Число %s должно быть чётным", actual)
                    .isEqualTo(BigDecimal.ZERO);
        };
    }

    /**
     * Число нечётное (применимо только к целым числам).
     *
     * @param <T> тип числа
     * @return условие "нечётное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsOdd() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            int scale = actual.stripTrailingZeros().scale();
            Assertions.assertThat(scale)
                    .as("Число %s должно быть целым для проверки нечётности", actual)
                    .isLessThanOrEqualTo(0);
            BigDecimal remainder = actual.remainder(BigDecimal.valueOf(2));
            Assertions.assertThat(remainder)
                    .as("Число %s должно быть нечётным", actual)
                    .isNotEqualTo(BigDecimal.ZERO);
        };
    }

    /**
     * Число делится на указанный делитель без остатка.
     *
     * @param divisor делитель в виде BigDecimal
     * @param <T>     тип числа
     * @return условие делимости
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsDivisibleBy(BigDecimal divisor) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            BigDecimal remainder = actual.remainder(divisor);
            Assertions.assertThat(remainder)
                    .as("Число %s должно делиться на %s без остатка", actual, divisor)
                    .isEqualTo(BigDecimal.ZERO);
        };
    }

    /**
     * Число отличается от ожидаемого не более чем на заданное процентное отклонение.
     * Если ожидаемое значение равно 0, то и фактическое должно быть равно 0.
     *
     * @param expected   ожидаемое значение
     * @param percentage допустимое процентное отклонение (например, 5 означает 5%)
     * @param <T>        тип числа
     * @return условие относительной близости значений
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberWithinPercentage(BigDecimal expected, BigDecimal percentage) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            if (expected.compareTo(BigDecimal.ZERO) == 0) {
                Assertions.assertThat(actual)
                        .as("Если ожидаемое значение равно 0, то и фактическое должно быть 0")
                        .isEqualTo(BigDecimal.ZERO);
            } else {
                BigDecimal allowed = expected.abs().multiply(percentage).divide(new BigDecimal("100"));
                BigDecimal diff = actual.subtract(expected).abs();
                Assertions.assertThat(diff)
                        .as("Разница %s должна быть не более чем %s%% (%s) от %s", diff, percentage, allowed, expected)
                        .isLessThanOrEqualTo(allowed);
            }
        };
    }

    /**
     * Число имеет тот же знак, что и указанное.
     *
     * @param other значение для сравнения знака
     * @param <T>   тип числа
     * @return условие совпадения знака
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberHasSameSignAs(BigDecimal other) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            boolean sameSign = actual.signum() == other.signum();
            Assertions.assertThat(sameSign)
                    .as("Число %s должно иметь тот же знак, что и %s", actual, other)
                    .isTrue();
        };
    }

    /**
     * Число является простым (применимо только к целым числам).
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsPrime() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            if (actual.stripTrailingZeros().scale() > 0) {
                throw new IllegalArgumentException(
                        String.format("Число %s должно быть целым для проверки на простоту", actual));
            }
            BigInteger intVal = actual.toBigIntegerExact();
            boolean isPrime = intVal.compareTo(BigInteger.TWO) >= 0 && intVal.isProbablePrime(10);
            Assertions.assertThat(isPrime)
                    .as("Число %s должно быть простым", actual)
                    .isTrue();
        };
    }

    /**
     * Число является совершенным квадратом (применимо только к целым числам).
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsPerfectSquare() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            if (actual.stripTrailingZeros().scale() > 0) {
                throw new IllegalArgumentException(
                        String.format("Число %s должно быть целым для проверки на совершенный квадрат", actual));
            }
            BigInteger intVal = actual.toBigIntegerExact();
            BigInteger sqrt = sqrt(intVal);
            boolean isPerfect = sqrt.multiply(sqrt).equals(intVal);
            Assertions.assertThat(isPerfect)
                    .as("Число %s должно быть совершенным квадратом (так как %s^2 != %s)", actual, sqrt, intVal)
                    .isTrue();
        };
    }

    /**
     * Число имеет дробную часть (то есть не является целым).
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberHasFractionalPart() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            boolean hasFraction = actual.stripTrailingZeros().scale() > 0;
            Assertions.assertThat(hasFraction)
                    .as("Число %s должно иметь дробную часть", actual)
                    .isTrue();
        };
    }

    /**
     * Масштаб (scale) числа равен ожидаемому значению.
     *
     * @param expectedScale ожидаемый масштаб
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberHasScale(int expectedScale) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            int scale = actual.scale();
            Assertions.assertThat(scale)
                    .as("Число %s должно иметь масштаб (scale) равный %d", actual, expectedScale)
                    .isEqualTo(expectedScale);
        };
    }

    /**
     * Число находится в диапазоне [start, end), то есть включает левую границу и исключает правую.
     *
     * @param start начало диапазона (включается)
     * @param end   конец диапазона (исключается)
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberLeftInclusiveRightExclusive(T start, T end) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            BigDecimal startVal = toBigDecimal(start);
            BigDecimal endVal = toBigDecimal(end);
            Assertions.assertThat(actual)
                    .as("Число %s должно быть в диапазоне [%s, %s)", actual, startVal, endVal)
                    .isGreaterThanOrEqualTo(startVal)
                    .isLessThan(endVal);
        };
    }

    /**
     * Число находится в диапазоне (start, end], то есть исключает левую границу и включает правую.
     *
     * @param start начало диапазона (исключается)
     * @param end   конец диапазона (включается)
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberLeftExclusiveRightInclusive(T start, T end) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            BigDecimal startVal = toBigDecimal(start);
            BigDecimal endVal = toBigDecimal(end);
            Assertions.assertThat(actual)
                    .as("Число %s должно быть в диапазоне (%s, %s]", actual, startVal, endVal)
                    .isGreaterThan(startVal)
                    .isLessThanOrEqualTo(endVal);
        };
    }

    /**
     * Число близко к ожидаемому значению с указанным абсолютным отклонением.
     *
     * @param expected ожидаемое значение
     * @param offset   допустимое отклонение
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsCloseTo(T expected, T offset) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            BigDecimal expectedVal = toBigDecimal(expected);
            BigDecimal offsetVal = toBigDecimal(offset);
            BigDecimal diff = actual.subtract(expectedVal).abs();
            Assertions.assertThat(diff)
                    .as("Разница между %s и %s должна быть не более %s, но равна %s",
                            actual, expectedVal, offsetVal, diff)
                    .isLessThanOrEqualTo(offsetVal);
        };
    }

    /**
     * Число является конечным (для Float и Double).
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsFinite() {
        return number -> {
            if (number instanceof Double) {
                double d = (Double) number;
                Assertions.assertThat(!Double.isNaN(d) && !Double.isInfinite(d))
                        .as("Число %s должно быть конечным", d)
                        .isTrue();
            } else if (number instanceof Float) {
                float f = (Float) number;
                Assertions.assertThat(!Float.isNaN(f) && !Float.isInfinite(f))
                        .as("Число %s должно быть конечным", f)
                        .isTrue();
            }
            // Для остальных типов чисел считаем значение конечным.
        };
    }

    /**
     * Число является NaN (Not-a-Number) для Float и Double.
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsNaN() {
        return number -> {
            if (number instanceof Double) {
                double d = (Double) number;
                Assertions.assertThat(Double.isNaN(d))
                        .as("Число %s должно быть NaN", d)
                        .isTrue();
            } else if (number instanceof Float) {
                float f = (Float) number;
                Assertions.assertThat(Float.isNaN(f))
                        .as("Число %s должно быть NaN", f)
                        .isTrue();
            } else {
                Assertions.fail("Проверка на NaN применима только к типам Float и Double, но был передан тип %s", number.getClass().getName());
            }
        };
    }

    /**
     * Число является бесконечным (для Float и Double).
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsInfinite() {
        return number -> {
            if (number instanceof Double) {
                double d = (Double) number;
                Assertions.assertThat(Double.isInfinite(d))
                        .as("Число %s должно быть бесконечным", d)
                        .isTrue();
            } else if (number instanceof Float) {
                float f = (Float) number;
                Assertions.assertThat(Float.isInfinite(f))
                        .as("Число %s должно быть бесконечным", f)
                        .isTrue();
            } else {
                Assertions.fail("Проверка на бесконечность применима только к типам Float и Double, но был передан тип %s", number.getClass().getName());
            }
        };
    }

    /**
     * Абсолютное значение числа больше указанного порога.
     *
     * @param threshold пороговое значение
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberHasAbsoluteValueGreaterThan(T threshold) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            BigDecimal thresholdVal = toBigDecimal(threshold);
            BigDecimal absActual = actual.abs();
            Assertions.assertThat(absActual)
                    .as("Абсолютное значение %s должно быть больше %s", absActual, thresholdVal)
                    .isGreaterThan(thresholdVal);
        };
    }

    /**
     * Абсолютное значение числа меньше указанного порога.
     *
     * @param threshold пороговое значение
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberHasAbsoluteValueLessThan(T threshold) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            BigDecimal thresholdVal = toBigDecimal(threshold);
            BigDecimal absActual = actual.abs();
            Assertions.assertThat(absActual)
                    .as("Абсолютное значение %s должно быть меньше %s", absActual, thresholdVal)
                    .isLessThan(thresholdVal);
        };
    }

    /**
     * Абсолютное значение числа находится между заданными значениями.
     *
     * @param lower нижняя граница
     * @param upper верхняя граница
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberHasAbsoluteValueBetween(T lower, T upper) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            BigDecimal lowerVal = toBigDecimal(lower);
            BigDecimal upperVal = toBigDecimal(upper);
            BigDecimal absActual = actual.abs();
            Assertions.assertThat(absActual)
                    .as("Абсолютное значение %s должно быть между %s и %s", absActual, lowerVal, upperVal)
                    .isBetween(lowerVal, upperVal);
        };
    }

    /**
     * Число приблизительно равно ожидаемому значению с относительной погрешностью.
     * Если ожидаемое значение равно 0, то и фактическое должно быть 0.
     *
     * @param expected          ожидаемое значение
     * @param relativeTolerance относительная погрешность (например, 0.05 означает 5% отклонения)
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberApproximatelyEqualRelative(T expected, BigDecimal relativeTolerance) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            BigDecimal expectedVal = toBigDecimal(expected);
            if (expectedVal.compareTo(BigDecimal.ZERO) == 0) {
                Assertions.assertThat(actual)
                        .as("Поскольку ожидаемое значение равно 0, фактическое значение %s также должно быть 0", actual)
                        .isEqualTo(BigDecimal.ZERO);
            } else {
                BigDecimal diff = actual.subtract(expectedVal).abs();
                BigDecimal relativeError = diff.divide(expectedVal.abs(), MathContext.DECIMAL128);
                Assertions.assertThat(relativeError)
                        .as("Относительная погрешность %s должна быть не более чем %s", relativeError, relativeTolerance)
                        .isLessThanOrEqualTo(relativeTolerance);
            }
        };
    }

    /**
     * Число находится в диапазоне от 0 до 1 (границы включаются).
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsBetweenZeroAndOne() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Число %s должно быть между 0 и 1 (включительно)", actual)
                    .isBetween(BigDecimal.ZERO, BigDecimal.ONE);
        };
    }

    /**
     * Число входит в арифметическую последовательность, заданную начальным значением, шагом и конечным значением.
     * То есть число должно удовлетворять: start ≤ number ≤ end и (number - start) должно делиться на step без остатка.
     *
     * @param start начальное значение последовательности
     * @param step  шаг последовательности
     * @param end   конечное значение последовательности
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberFitsArithmeticSequence(T start, T step, T end) {
        return number -> {
            BigDecimal actualVal = toBigDecimal(number);
            BigDecimal startVal = toBigDecimal(start);
            BigDecimal stepVal = toBigDecimal(step);
            BigDecimal endVal = toBigDecimal(end);
            Assertions.assertThat(actualVal)
                    .as("Число %s должно быть между %s и %s", actualVal, startVal, endVal)
                    .isBetween(startVal, endVal);
            BigDecimal diff = actualVal.subtract(startVal);
            BigDecimal remainder = diff.remainder(stepVal).abs();
            Assertions.assertThat(remainder)
                    .as("Разница между %s и начальным значением %s должна делиться на шаг %s без остатка, остаток %s",
                            actualVal, startVal, stepVal, remainder)
                    .isEqualTo(BigDecimal.ZERO);
        };
    }

    /**
     * Число имеет указанное количество знаков в целой части.
     *
     * @param expectedDigits ожидаемое количество знаков
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberHasNumberOfDigits(int expectedDigits) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            BigInteger integerPart = actual.toBigInteger();
            int numberOfDigits = integerPart.abs().toString().length();
            Assertions.assertThat(numberOfDigits)
                    .as("Число %s должно иметь %d знаков в целой части", actual, expectedDigits)
                    .isEqualTo(expectedDigits);
        };
    }

    /**
     * Значение является числом (экземпляром Number).
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsNumber() {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть числом (экземпляром Number)")
                .isInstanceOf(Number.class);
    }

    /**
     * Число больше или равно указанному значению (generic-версия).
     *
     * @param expected значение
     * @param <T>      тип числа
     * @return условие "больше или равно"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberGreaterOrEqualTo(T expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть >= %s", expected)
                .isGreaterThanOrEqualTo(expected);
    }

    /**
     * Число меньше или равно указанному значению (generic-версия).
     *
     * @param expected значение
     * @param <T>      тип числа
     * @return условие "меньше или равно"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberLessOrEqualTo(T expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть <= %s", expected)
                .isLessThanOrEqualTo(expected);
    }

    /**
     * Число неотрицательно (>= 0) (через doubleValue()).
     *
     * @param <T> тип числа
     * @return условие "неотрицательное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsPositiveOrZero() {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть >= 0")
                .isGreaterThanOrEqualTo(0.0);
    }

    /**
     * Число неположительно (<= 0) (через doubleValue()).
     *
     * @param <T> тип числа
     * @return условие "неположительное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsNegativeOrZero() {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть <= 0")
                .isLessThanOrEqualTo(0.0);
    }

    /**
     * Целочисленное число является кратным указанному делителю.
     *
     * @param divisor делитель
     * @param <T>     тип числа (Integer, Long, Byte, Short, BigInteger и т.п.)
     * @return условие "кратно"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberMultipleOf(long divisor) {
        return actual -> Assertions.assertThat(actual.longValue() % divisor)
                .as("Значение должно быть кратно %d", divisor)
                .isEqualTo(0L);
    }

    /**
     * Число является экземпляром Integer.
     *
     * @param <T> тип числа
     * @return условие проверки типа
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsIntegerType() {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть экземпляром Integer")
                .isInstanceOf(Integer.class);
    }

    /**
     * Число является экземпляром Long.
     *
     * @param <T> тип числа
     * @return условие проверки типа
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsLongType() {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть экземпляром Long")
                .isInstanceOf(Long.class);
    }

    /**
     * Число является экземпляром Float.
     *
     * @param <T> тип числа
     * @return условие проверки типа
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsFloatType() {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть экземпляром Float")
                .isInstanceOf(Float.class);
    }

    /**
     * Число является экземпляром Double.
     *
     * @param <T> тип числа
     * @return условие проверки типа
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsDoubleType() {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть экземпляром Double")
                .isInstanceOf(Double.class);
    }

    /**
     * Число является экземпляром BigDecimal.
     *
     * @param <T> тип числа
     * @return условие проверки типа
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsBigDecimalType() {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть экземпляром BigDecimal")
                .isInstanceOf(BigDecimal.class);
    }

    /**
     * Число является экземпляром BigInteger.
     *
     * @param <T> тип числа
     * @return условие проверки типа
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsBigIntegerType() {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть экземпляром BigInteger")
                .isInstanceOf(BigInteger.class);
    }

    /**
     * Число является экземпляром Short.
     *
     * @param <T> тип числа
     * @return условие проверки типа
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsShortType() {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть экземпляром Short")
                .isInstanceOf(Short.class);
    }

    /**
     * Число является экземпляром Byte.
     *
     * @param <T> тип числа
     * @return условие проверки типа
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsByteType() {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть экземпляром Byte")
                .isInstanceOf(Byte.class);
    }

    /**
     * Число равно единице (через intValue()).
     *
     * @param <T> тип числа
     * @return условие "равно 1"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsOne() {
        return actual -> Assertions.assertThat(actual.intValue())
                .as("Значение должно быть равно 1")
                .isEqualTo(1);
    }

    /**
     * Число равно минус единице (через intValue()).
     *
     * @param <T> тип числа
     * @return условие "равно -1"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsMinusOne() {
        return actual -> Assertions.assertThat(actual.intValue())
                .as("Значение должно быть равно -1")
                .isEqualTo(-1);
    }

    /**
     * Число является положительной бесконечностью (для Float и Double).
     *
     * @param <T> тип числа
     * @return условие "положительная бесконечность"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsPositiveInfinity() {
        return actual -> {
            if (actual instanceof Float) {
                Assertions.assertThat(((Float) actual).isInfinite() && ((Float) actual) > 0)
                        .as("Значение должно быть положительной бесконечностью")
                        .isTrue();
            } else if (actual instanceof Double) {
                Assertions.assertThat(((Double) actual).isInfinite() && ((Double) actual) > 0)
                        .as("Значение должно быть положительной бесконечностью")
                        .isTrue();
            } else {
                Assertions.fail("Проверка на бесконечность применима только к Float и Double");
            }
        };
    }

    /**
     * Число является отрицательной бесконечностью (для Float и Double).
     *
     * @param <T> тип числа
     * @return условие "отрицательная бесконечность"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsNegativeInfinity() {
        return actual -> {
            if (actual instanceof Float) {
                Assertions.assertThat(((Float) actual).isInfinite() && ((Float) actual) < 0)
                        .as("Значение должно быть отрицательной бесконечностью")
                        .isTrue();
            } else if (actual instanceof Double) {
                Assertions.assertThat(((Double) actual).isInfinite() && ((Double) actual) < 0)
                        .as("Значение должно быть отрицательной бесконечностью")
                        .isTrue();
            } else {
                Assertions.fail("Проверка на бесконечность применима только к Float и Double");
            }
        };
    }

    /**
     * Число является строго положительным (больше 0) (через doubleValue()).
     *
     * @param <T> тип числа
     * @return условие "строго положительное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsStrictlyPositive() {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть строго больше 0")
                .isGreaterThan(0.0);
    }

    /**
     * Число является строго отрицательным (меньше 0) (через doubleValue()).
     *
     * @param <T> тип числа
     * @return условие "строго отрицательное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsStrictlyNegative() {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть строго меньше 0")
                .isLessThan(0.0);
    }

    /**
     * Число является степенью двойки (только для целых чисел).
     *
     * @param <T> тип числа
     * @return условие "степень двойки"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsPowerOfTwo() {
        return actual -> {
            if (actual instanceof Integer
                    || actual instanceof Long
                    || actual instanceof Short
                    || actual instanceof Byte
                    || actual instanceof BigInteger) {
                long n = actual.longValue();
                Assertions.assertThat(n > 0 && (n & (n - 1)) == 0)
                        .as("Значение должно быть степенью двойки")
                        .isTrue();
            } else {
                Assertions.fail("Проверка на степень двойки применима только к целым числам");
            }
        };
    }

    /**
     * Число является чётным и положительным (через longValue() и doubleValue()).
     *
     * @param <T> тип числа
     * @return условие "чётное и положительное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsEvenAndPositive() {
        return actual -> {
            Assertions.assertThat(actual.longValue())
                    .as("Значение должно быть чётным")
                    .isEven();
            Assertions.assertThat(actual.doubleValue())
                    .as("Значение должно быть положительным (больше 0)")
                    .isGreaterThan(0.0);
        };
    }

    /**
     * Число является нечётным и положительным (через longValue() и doubleValue()).
     *
     * @param <T> тип числа
     * @return условие "нечётное и положительное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsOddAndPositive() {
        return actual -> {
            Assertions.assertThat(actual.longValue())
                    .as("Значение должно быть нечётным")
                    .isOdd();
            Assertions.assertThat(actual.doubleValue())
                    .as("Значение должно быть положительным (больше 0)")
                    .isGreaterThan(0.0);
        };
    }

    /**
     * Число является чётным и отрицательным (через longValue() и doubleValue()).
     *
     * @param <T> тип числа
     * @return условие "чётное и отрицательное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsEvenAndNegative() {
        return actual -> {
            Assertions.assertThat(actual.longValue())
                    .as("Значение должно быть чётным")
                    .isEven();
            Assertions.assertThat(actual.doubleValue())
                    .as("Значение должно быть отрицательным (меньше 0)")
                    .isLessThan(0.0);
        };
    }

    /**
     * Число является нечётным и отрицательным (через longValue() и doubleValue()).
     *
     * @param <T> тип числа
     * @return условие "нечётное и отрицательное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsOddAndNegative() {
        return actual -> {
            Assertions.assertThat(actual.longValue())
                    .as("Значение должно быть нечётным")
                    .isOdd();
            Assertions.assertThat(actual.doubleValue())
                    .as("Значение должно быть отрицательным (меньше 0)")
                    .isLessThan(0.0);
        };
    }

    /**
     * Число является положительным и кратным указанному делителю.
     *
     * @param divisor делитель
     * @param <T>     тип числа
     * @return условие "положительное и кратное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsPositiveAndMultipleOf(long divisor) {
        return actual -> {
            Assertions.assertThat(actual.doubleValue())
                    .as("Значение должно быть положительным (больше 0)")
                    .isGreaterThan(0.0);
            Assertions.assertThat(actual.longValue() % divisor)
                    .as("Значение должно быть кратно %d", divisor)
                    .isEqualTo(0L);
        };
    }

    /**
     * Число является отрицательным и кратным указанному делителю.
     *
     * @param divisor делитель
     * @param <T>     тип числа
     * @return условие "отрицательное и кратное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsNegativeAndMultipleOf(long divisor) {
        return actual -> {
            Assertions.assertThat(actual.doubleValue())
                    .as("Значение должно быть отрицательным (меньше 0)")
                    .isLessThan(0.0);
            Assertions.assertThat(actual.longValue() % divisor)
                    .as("Значение должно быть кратно %d", divisor)
                    .isEqualTo(0L);
        };
    }

    /**
     * Число находится в диапазоне (start, end] (строго больше start и меньше или равно end).
     *
     * @param start начало диапазона
     * @param end   конец диапазона
     * @param <T>   тип числа
     * @return условие "в диапазоне (start, end]"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsGreaterThanAndLessThanOrEqualTo(T start, T end) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть > %s и <= %s", start, end)
                .isGreaterThan(start)
                .isLessThanOrEqualTo(end);
    }

    /**
     * Число находится в диапазоне [start, end) (больше или равно start и строго меньше end).
     *
     * @param start начало диапазона
     * @param end   конец диапазона
     * @param <T>   тип числа
     * @return условие "в диапазоне [start, end)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsGreaterThanOrEqualToAndLessThan(T start, T end) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть >= %s и < %s", start, end)
                .isGreaterThanOrEqualTo(start)
                .isLessThan(end);
    }

    /**
     * Число является "близким к нулю", сравнивая абсолютное значение с некоторым небольшим порогом (Double.MIN_VALUE * 10).
     *
     * @param <T> тип числа
     * @return условие "близко к нулю"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsCloseToZero() {
        return actual -> Assertions.assertThat(Math.abs(actual.doubleValue()))
                .as("Значение должно быть близко к нулю")
                .isLessThan(Double.MIN_VALUE * 10);
    }

    /**
     * Число является делителем указанного значения (только для целых).
     *
     * @param number число, которое должно делиться
     * @param <T>    тип числа (предполагается целое число)
     * @return условие "является делителем"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsDivisorOf(long number) {
        return actual -> {
            if (actual.longValue() == 0) {
                Assertions.fail("Делитель не может быть нулём");
            }
            Assertions.assertThat(number % actual.longValue())
                    .as("Число %d должно делиться на %d", number, actual.longValue())
                    .isEqualTo(0);
        };
    }

    /**
     * Абсолютное значение числа равно указанному значению.
     *
     * @param expected абсолютное значение
     * @param <T>      тип числа
     * @return условие "абсолютное значение равно"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsEqualToAbsoluteValue(T expected) {
        return actual -> Assertions.assertThat(Math.abs(actual.doubleValue()))
                .as("Абсолютное значение должно быть равно %s", expected)
                .isEqualTo(expected.doubleValue());
    }

    /**
     * Абсолютное значение числа больше указанного значения.
     *
     * @param expected абсолютное значение
     * @param <T>      тип числа
     * @return условие "абсолютное значение больше"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsGreaterThanAbsoluteValue(T expected) {
        return actual -> Assertions.assertThat(Math.abs(actual.doubleValue()))
                .as("Абсолютное значение должно быть больше %s", expected)
                .isGreaterThan(expected.doubleValue());
    }

    /**
     * Абсолютное значение числа меньше указанного значения.
     *
     * @param expected абсолютное значение
     * @param <T>      тип числа
     * @return условие "абсолютное значение меньше"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsLessThanAbsoluteValue(T expected) {
        return actual -> Assertions.assertThat(Math.abs(actual.doubleValue()))
                .as("Абсолютное значение должно быть меньше %s", expected)
                .isLessThan(expected.doubleValue());
    }

    /**
     * Число находится в полуоткрытом диапазоне [min, max) для чисел с плавающей точкой (через doubleValue()).
     *
     * @param min минимальное значение (включительно)
     * @param max максимальное значение (исключительно)
     * @param <T> тип числа
     * @return условие "в диапазоне с плавающей точкой (start включительно, end исключительно)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsFloatRangeStartInclusiveEndExclusive(double min, double max) {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть >= %s и < %s", min, max)
                .isGreaterThanOrEqualTo(min)
                .isLessThan(max);
    }

    /**
     * Число находится в полуоткрытом диапазоне (start, end] для чисел с плавающей точкой (через doubleValue()).
     *
     * @param min минимальное значение (исключительно)
     * @param max максимальное значение (включительно)
     * @param <T> тип числа
     * @return условие "в диапазоне с плавающей точкой (start исключительно, end включительно)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsFloatRangeStartExclusiveEndInclusive(double min, double max) {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть > %s и <= %s", min, max)
                .isGreaterThan(min)
                .isLessThanOrEqualTo(max);
    }

    /**
     * Число является строго положительным целым (не включая ноль).
     *
     * @param <T> тип числа
     * @return условие "строго положительное целое число"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsStrictlyPositiveInteger() {
        return actual -> {
            if (actual instanceof Integer
                    || actual instanceof Long
                    || actual instanceof Short
                    || actual instanceof Byte
                    || actual instanceof BigInteger) {
                Assertions.assertThat(actual.longValue())
                        .as("Значение должно быть строго больше 0")
                        .isGreaterThan(0L);
            } else {
                Assertions.fail("Проверка применима только к целым числам");
            }
        };
    }

    /**
     * Число является строго отрицательным целым (не включая ноль).
     *
     * @param <T> тип числа
     * @return условие "строго отрицательное целое число"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsStrictlyNegativeInteger() {
        return actual -> {
            if (actual instanceof Integer
                    || actual instanceof Long
                    || actual instanceof Short
                    || actual instanceof Byte
                    || actual instanceof BigInteger) {
                Assertions.assertThat(actual.longValue())
                        .as("Значение должно быть строго меньше 0")
                        .isLessThan(0L);
            } else {
                Assertions.fail("Проверка применима только к целым числам");
            }
        };
    }

    /**
     * Число является нулём для целочисленных типов.
     *
     * @param <T> тип числа
     * @return условие "ноль для целого числа"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsZeroInteger() {
        return actual -> {
            if (actual instanceof Integer
                    || actual instanceof Long
                    || actual instanceof Short
                    || actual instanceof Byte
                    || actual instanceof BigInteger) {
                Assertions.assertThat(actual.longValue())
                        .as("Значение должно быть равно 0")
                        .isEqualTo(0L);
            } else {
                Assertions.fail("Проверка применима только к целым числам");
            }
        };
    }

    /**
     * Число является единицей для целочисленных типов.
     *
     * @param <T> тип числа
     * @return условие "единица для целого числа"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsOneInteger() {
        return actual -> {
            if (actual instanceof Integer
                    || actual instanceof Long
                    || actual instanceof Short
                    || actual instanceof Byte
                    || actual instanceof BigInteger) {
                Assertions.assertThat(actual.longValue())
                        .as("Значение должно быть равно 1")
                        .isEqualTo(1L);
            } else {
                Assertions.fail("Проверка применима только к целым числам");
            }
        };
    }

    /**
     * Число является нечётным и неотрицательным (>= 0).
     *
     * @param <T> тип числа
     * @return условие "неотрицательное и нечётное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsOddAndNonNegative() {
        return actual -> {
            Assertions.assertThat(actual.doubleValue())
                    .as("Значение должно быть >= 0")
                    .isGreaterThanOrEqualTo(0.0);
            Assertions.assertThat(actual.longValue())
                    .as("Значение должно быть нечётным")
                    .isOdd();
        };
    }

    /**
     * Число является чётным и неотрицательным (>= 0).
     *
     * @param <T> тип числа
     * @return условие "неотрицательное и чётное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsEvenAndNonNegative() {
        return actual -> {
            Assertions.assertThat(actual.doubleValue())
                    .as("Значение должно быть >= 0")
                    .isGreaterThanOrEqualTo(0.0);
            Assertions.assertThat(actual.longValue())
                    .as("Значение должно быть чётным")
                    .isEven();
        };
    }

    /**
     * Число является нечётным и неположительным (<= 0).
     *
     * @param <T> тип числа
     * @return условие "неположительное и нечётное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsOddAndNonPositive() {
        return actual -> {
            Assertions.assertThat(actual.doubleValue())
                    .as("Значение должно быть <= 0")
                    .isLessThanOrEqualTo(0.0);
            Assertions.assertThat(actual.longValue())
                    .as("Значение должно быть нечётным")
                    .isOdd();
        };
    }

    /**
     * Число является чётным и неположительным (<= 0).
     *
     * @param <T> тип числа
     * @return условие "неположительное и чётное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsEvenAndNonPositive() {
        return actual -> {
            Assertions.assertThat(actual.doubleValue())
                    .as("Значение должно быть <= 0")
                    .isLessThanOrEqualTo(0.0);
            Assertions.assertThat(actual.longValue())
                    .as("Значение должно быть чётным")
                    .isEven();
        };
    }

    /**
     * Число является экземпляром указанного класса.
     *
     * @param type ожидаемый класс числа
     * @param <T>  тип числа
     * @return условие проверки типа
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberIsInstanceOf(Class<?> type) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть экземпляром %s", type.getName())
                .isInstanceOf(type);
    }

    /**
     * Число имеет precision, равный указанному значению (для BigDecimal).
     *
     * @param expectedPrecision ожидаемая точность (precision)
     * @param <T>               тип числа
     * @return условие проверки precision
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberHasPrecision(int expectedPrecision) {
        return actual -> {
            if (actual instanceof BigDecimal) {
                Assertions.assertThat(((BigDecimal) actual).precision())
                        .as("Precision должна быть равна %d", expectedPrecision)
                        .isEqualTo(expectedPrecision);
            } else {
                Assertions.fail("Проверка precision применима только к BigDecimal");
            }
        };
    }

    /* ***********************************************************************
       Методы для сравнения с использованием double в качестве ожидаемого значения
       (возможно использование для любых T, но следует учитывать потери точности
       для BigDecimal, BigInteger и т.д.).
       *********************************************************************** */

    /**
     * Число больше или равно указанному значению с плавающей точкой.
     *
     * @param expected значение
     * @param <T>      тип числа
     * @return условие "больше или равно (double)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberGreaterOrEqualTo(double expected) {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть >= %s", expected)
                .isGreaterThanOrEqualTo(expected);
    }

    /**
     * Число меньше или равно указанному значению с плавающей точкой.
     *
     * @param expected значение
     * @param <T>      тип числа
     * @return условие "меньше или равно (double)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberLessOrEqualTo(double expected) {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть <= %s", expected)
                .isLessThanOrEqualTo(expected);
    }

    /**
     * Число больше указанного значения с плавающей точкой.
     *
     * @param expected значение
     * @param <T>      тип числа
     * @return условие "больше (double)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberGreaterThan(double expected) {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть > %s", expected)
                .isGreaterThan(expected);
    }

    /**
     * Число меньше указанного значения с плавающей точкой.
     *
     * @param expected значение
     * @param <T>      тип числа
     * @return условие "меньше (double)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberLessThan(double expected) {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть < %s", expected)
                .isLessThan(expected);
    }

    /**
     * Число равно указанному значению с плавающей точкой.
     *
     * @param expected значение
     * @param <T>      тип числа
     * @return условие "равно (double)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberEqualTo(double expected) {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть равно %s", expected)
                .isEqualTo(expected);
    }

    /**
     * Число не равно указанному значению с плавающей точкой.
     *
     * @param expected значение
     * @param <T>      тип числа
     * @return условие "не равно (double)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberNotEqualTo(double expected) {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение не должно быть равно %s", expected)
                .isNotEqualTo(expected);
    }

    /**
     * Число находится в диапазоне [min, max] с плавающей точкой.
     *
     * @param min начало диапазона
     * @param max конец диапазона
     * @param <T> тип числа
     * @return условие "в диапазоне [min, max] (double)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberInRange(double min, double max) {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть в диапазоне [%s, %s]", min, max)
                .isBetween(min, max);
    }

    /**
     * Число находится в диапазоне (min, max) с плавающей точкой.
     *
     * @param min начало диапазона
     * @param max конец диапазона
     * @param <T> тип числа
     * @return условие "в диапазоне (min, max) (double)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> numberBetweenExclusive(double min, double max) {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть в диапазоне (%s, %s)", min, max)
                .isGreaterThan(min)
                .isLessThan(max);
    }

    /**
     * Преобразует число в BigDecimal.
     *
     * @param number число для преобразования
     * @param <T>    тип числа
     * @return число в виде BigDecimal
     */
    private static <T extends Number> BigDecimal toBigDecimal(T number) {
        return new BigDecimal(number.toString());
    }

    /**
     * Вычисляет целочисленный квадратный корень из BigInteger с использованием алгоритма Ньютона.
     *
     * @param value число, из которого вычисляется квадратный корень
     * @return целая часть квадратного корня
     */
    private static BigInteger sqrt(BigInteger value) {
        if (value.compareTo(BigInteger.ZERO) < 0) {
            throw new ArithmeticException("Квадратный корень из отрицательного числа");
        }
        if (value.equals(BigInteger.ZERO) || value.equals(BigInteger.ONE)) {
            return value;
        }
        BigInteger two = BigInteger.valueOf(2);
        BigInteger guess = value.divide(two);
        while (true) {
            BigInteger nextGuess = (guess.add(value.divide(guess))).divide(two);
            if (nextGuess.equals(guess) || nextGuess.equals(guess.subtract(BigInteger.ONE))) {
                return nextGuess;
            }
            guess = nextGuess;
        }
    }
}
