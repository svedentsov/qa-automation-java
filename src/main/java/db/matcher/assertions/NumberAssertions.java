package db.matcher.assertions;

import db.matcher.Condition;
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
     * Проверяет, что число находится в диапазоне (исключая границы).
     *
     * @param start начало диапазона (исключается)
     * @param end   конец диапазона (исключается)
     * @param <T>   тип числа
     * @return условие, что число находится строго между start и end
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> betweenExclusive(T start, T end) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть в диапазоне (%s, %s)", start, end)
                .isGreaterThan(start)
                .isLessThan(end);
    }

    /**
     * Проверяет, что число находится в диапазоне [start, end] (границы включаются).
     *
     * @param start начало диапазона
     * @param end   конец диапазона
     * @param <T>   тип числа
     * @return условие, что число находится в диапазоне
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> inRange(T start, T end) {
        return between(toBigDecimal(start), toBigDecimal(end));
    }

    /**
     * Проверяет, что число больше указанного порогового значения.
     *
     * @param threshold пороговое значение в BigDecimal
     * @param <T>       тип числа
     * @return условие "больше"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> greaterThan(BigDecimal threshold) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно быть больше %s, но было %s", threshold, actual)
                    .isGreaterThan(threshold);
        };
    }

    /**
     * Перегруженная версия greaterThan, принимающая значение типа T.
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> greaterThan(T expected) {
        return greaterThan(toBigDecimal(expected));
    }

    /**
     * Проверяет, что число меньше указанного порогового значения.
     *
     * @param threshold пороговое значение в BigDecimal
     * @param <T>       тип числа
     * @return условие "меньше"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> lessThan(BigDecimal threshold) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно быть меньше %s, но было %s", threshold, actual)
                    .isLessThan(threshold);
        };
    }

    /**
     * Перегруженная версия lessThan, принимающая значение типа T.
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> lessThan(T expected) {
        return lessThan(toBigDecimal(expected));
    }

    /**
     * Проверяет, что число больше или равно указанному порогу.
     *
     * @param threshold пороговое значение в BigDecimal
     * @param <T>       тип числа
     * @return условие "больше или равно"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> greaterThanOrEqualTo(BigDecimal threshold) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно быть больше или равно %s, но было %s", threshold, actual)
                    .isGreaterThanOrEqualTo(threshold);
        };
    }

    /**
     * Перегруженная версия greaterThanOrEqualTo, принимающая значение типа T.
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> greaterThanOrEqualTo(T expected) {
        return greaterThanOrEqualTo(toBigDecimal(expected));
    }

    /**
     * Проверяет, что число меньше или равно указанному порогу.
     *
     * @param threshold пороговое значение в BigDecimal
     * @param <T>       тип числа
     * @return условие "меньше или равно"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> lessThanOrEqualTo(BigDecimal threshold) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно быть меньше или равно %s, но было %s", threshold, actual)
                    .isLessThanOrEqualTo(threshold);
        };
    }

    /**
     * Перегруженная версия lessThanOrEqualTo, принимающая значение типа T.
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> lessThanOrEqualTo(T expected) {
        return lessThanOrEqualTo(toBigDecimal(expected));
    }

    /**
     * Проверяет, что число находится в диапазоне [start, end] (границы включаются).
     *
     * @param start начало диапазона в BigDecimal
     * @param end   конец диапазона в BigDecimal
     * @param <T>   тип числа
     * @return условие "в диапазоне"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> between(BigDecimal start, BigDecimal end) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно находиться между %s и %s, но было %s", start, end, actual)
                    .isBetween(start, end);
        };
    }

    /**
     * Перегруженная версия between, принимающая значения типа T.
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> between(T start, T end) {
        return between(toBigDecimal(start), toBigDecimal(end));
    }

    /**
     * Проверяет, что число находится строго между start и end (границы не включаются).
     *
     * @param start начало диапазона в BigDecimal
     * @param end   конец диапазона в BigDecimal
     * @param <T>   тип числа
     * @return условие "строго между"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> strictlyBetween(BigDecimal start, BigDecimal end) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно находиться строго между %s и %s, но было %s", start, end, actual)
                    .isStrictlyBetween(start, end);
        };
    }

    /**
     * Перегруженная версия strictlyBetween, принимающая значения типа T.
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> strictlyBetween(T start, T end) {
        return strictlyBetween(toBigDecimal(start), toBigDecimal(end));
    }

    /**
     * Проверяет, что число равно нулю.
     *
     * @param <T> тип числа
     * @return условие "равно 0"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> propertyIsZero() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual.compareTo(BigDecimal.ZERO) == 0)
                    .as("Значение должно быть равно 0, но было %s", actual)
                    .isTrue();
        };
    }

    /**
     * Алиас для propertyIsZero().
     *
     * @param <T> тип числа
     * @return условие "равно 0"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isZero() {
        return propertyIsZero();
    }

    /**
     * Проверяет, что число не равно нулю.
     *
     * @param <T> тип числа
     * @return условие "не равно 0"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isNotZero() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual.compareTo(BigDecimal.ZERO) != 0)
                    .as("Значение не должно быть равно 0, но было %s", actual)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что число положительное (строго больше 0).
     *
     * @param <T> тип числа
     * @return условие "положительное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isPositive() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно быть положительным, но было %s", actual)
                    .isGreaterThan(BigDecimal.ZERO);
        };
    }

    /**
     * Проверяет, что число отрицательное (строго меньше 0).
     *
     * @param <T> тип числа
     * @return условие "отрицательное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isNegative() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение должно быть отрицательным, но было %s", actual)
                    .isLessThan(BigDecimal.ZERO);
        };
    }

    /**
     * Проверяет, что число неотрицательное (больше или равно 0).
     *
     * @param <T> тип числа
     * @return условие "неотрицательное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isNonNegative() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение не должно быть отрицательным, но было %s", actual)
                    .isGreaterThanOrEqualTo(BigDecimal.ZERO);
        };
    }

    /**
     * Проверяет, что число неположительное (меньше или равно 0).
     *
     * @param <T> тип числа
     * @return условие "неположительное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isNonPositive() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Значение не должно быть положительным, но было %s", actual)
                    .isLessThanOrEqualTo(BigDecimal.ZERO);
        };
    }

    /**
     * Проверяет, что число приблизительно равно ожидаемому значению с заданной абсолютной погрешностью.
     *
     * @param expected  ожидаемое значение
     * @param tolerance допустимая абсолютная погрешность
     * @param <T>       тип числа
     * @return условие приблизительного равенства
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> approximatelyEqualTo(BigDecimal expected, BigDecimal tolerance) {
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
     * Перегруженная версия approximatelyEqualTo, принимающая значение типа T.
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> approximatelyEqualTo(T expected, BigDecimal tolerance) {
        return approximatelyEqualTo(toBigDecimal(expected), tolerance);
    }

    /**
     * Проверяет, что число приблизительно равно нулю с заданной абсолютной погрешностью.
     *
     * @param tolerance допустимая погрешность
     * @param <T>       тип числа
     * @return условие, проверяющее, что число примерно равно 0
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> approximatelyZero(BigDecimal tolerance) {
        return approximatelyEqualTo(BigDecimal.ZERO, tolerance);
    }

    /**
     * Проверяет, что число является целым (не имеет дробной части).
     *
     * @param <T> тип числа
     * @return условие "целое число"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isInteger() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            int scale = actual.stripTrailingZeros().scale();
            Assertions.assertThat(scale)
                    .as("Число %s должно быть целым, но имеет дробную часть", actual)
                    .isLessThanOrEqualTo(0);
        };
    }

    /**
     * Проверяет, является ли число чётным (применимо только к целым числам).
     *
     * @param <T> тип числа
     * @return условие "чётное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isEven() {
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
     * Проверяет, является ли число нечётным (применимо только к целым числам).
     *
     * @param <T> тип числа
     * @return условие "нечётное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isOdd() {
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
     * Проверяет, что число делится на указанный делитель без остатка.
     *
     * @param divisor делитель в виде BigDecimal
     * @param <T>     тип числа
     * @return условие делимости
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isDivisibleBy(BigDecimal divisor) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            BigDecimal remainder = actual.remainder(divisor);
            Assertions.assertThat(remainder)
                    .as("Число %s должно делиться на %s без остатка", actual, divisor)
                    .isEqualTo(BigDecimal.ZERO);
        };
    }

    /**
     * Проверяет, что число отличается от ожидаемого не более чем на заданное процентное отклонение.
     * Если ожидаемое значение равно 0, то и фактическое должно быть равно 0.
     *
     * @param expected   ожидаемое значение
     * @param percentage допустимое процентное отклонение (например, 5 означает 5%)
     * @param <T>        тип числа
     * @return условие относительной близости значений
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> withinPercentage(BigDecimal expected, BigDecimal percentage) {
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
     * Проверяет, что число имеет тот же знак, что и указанное.
     *
     * @param other значение для сравнения знака
     * @param <T>   тип числа
     * @return условие совпадения знака
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> hasSameSignAs(BigDecimal other) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            boolean sameSign = actual.signum() == other.signum();
            Assertions.assertThat(sameSign)
                    .as("Число %s должно иметь тот же знак, что и %s", actual, other)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что число является простым (применимо только к целым числам).
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isPrime() {
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
     * Проверяет, что число является совершенным квадратом (применимо только к целым числам).
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isPerfectSquare() {
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
     * Проверяет, что число имеет дробную часть (то есть не является целым).
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> hasFractionalPart() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            boolean hasFraction = actual.stripTrailingZeros().scale() > 0;
            Assertions.assertThat(hasFraction)
                    .as("Число %s должно иметь дробную часть", actual)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что масштаб (scale) числа равен ожидаемому значению.
     *
     * @param expectedScale ожидаемый масштаб
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> hasScale(int expectedScale) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            int scale = actual.scale();
            Assertions.assertThat(scale)
                    .as("Число %s должно иметь масштаб (scale) равный %d", actual, expectedScale)
                    .isEqualTo(expectedScale);
        };
    }

    /**
     * Проверяет, что число находится в диапазоне [start, end), то есть включает левую границу и исключает правую.
     *
     * @param start начало диапазона (включается)
     * @param end   конец диапазона (исключается)
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> leftInclusiveRightExclusive(T start, T end) {
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
     * Проверяет, что число находится в диапазоне (start, end], то есть исключает левую границу и включает правую.
     *
     * @param start начало диапазона (исключается)
     * @param end   конец диапазона (включается)
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> leftExclusiveRightInclusive(T start, T end) {
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
     * Проверяет, что число близко к ожидаемому значению с указанным абсолютным отклонением.
     *
     * @param expected ожидаемое значение
     * @param offset   допустимое отклонение
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isCloseTo(T expected, T offset) {
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
     * Проверяет, что число является конечным (для Float и Double).
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isFinite() {
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
     * Проверяет, что число является NaN (Not-a-Number) для Float и Double.
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isNaN() {
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
     * Проверяет, что число является бесконечным (для Float и Double).
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isInfinite() {
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
     * Проверяет, что абсолютное значение числа больше указанного порога.
     *
     * @param threshold пороговое значение
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> hasAbsoluteValueGreaterThan(T threshold) {
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
     * Проверяет, что абсолютное значение числа меньше указанного порога.
     *
     * @param threshold пороговое значение
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> hasAbsoluteValueLessThan(T threshold) {
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
     * Проверяет, что абсолютное значение числа находится между заданными значениями.
     *
     * @param lower нижняя граница
     * @param upper верхняя граница
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> hasAbsoluteValueBetween(T lower, T upper) {
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
     * Проверяет, что число приблизительно равно ожидаемому значению с относительной погрешностью.
     * Если ожидаемое значение равно 0, то и фактическое должно быть 0.
     *
     * @param expected          ожидаемое значение
     * @param relativeTolerance относительная погрешность (например, 0.05 означает 5% отклонения)
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> approximatelyEqualRelative(T expected, BigDecimal relativeTolerance) {
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
     * Проверяет, что число находится в диапазоне от 0 до 1 (границы включаются).
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isBetweenZeroAndOne() {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            Assertions.assertThat(actual)
                    .as("Число %s должно быть между 0 и 1 (включительно)", actual)
                    .isBetween(BigDecimal.ZERO, BigDecimal.ONE);
        };
    }

    /**
     * Проверяет, что число входит в арифметическую последовательность, заданную начальным значением, шагом и конечным значением.
     * То есть число должно удовлетворять: start ≤ number ≤ end и (number - start) должно делиться на step без остатка.
     *
     * @param start начальное значение последовательности
     * @param step  шаг последовательности
     * @param end   конечное значение последовательности
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> fitsArithmeticSequence(T start, T step, T end) {
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
     * Проверяет, что число имеет точность (precision) равную ожидаемому значению.
     *
     * @param expectedPrecision ожидаемая точность
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> hasPrecision(int expectedPrecision) {
        return number -> {
            BigDecimal actual = toBigDecimal(number);
            int precision = actual.precision();
            Assertions.assertThat(precision)
                    .as("Число %s должно иметь точность (precision) %d", actual, expectedPrecision)
                    .isEqualTo(expectedPrecision);
        };
    }

    /**
     * Проверяет, что число имеет указанное количество знаков в целой части.
     *
     * @param expectedDigits ожидаемое количество знаков
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> hasNumberOfDigits(int expectedDigits) {
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
     * Проверяет, что число является экземпляром указанного типа Number.
     *
     * @param expectedType ожидаемый тип Number (например, Integer.class, Double.class)
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isInstanceOf(Class<? extends Number> expectedType) {
        return number -> Assertions.assertThat(number)
                .as("Число должно быть экземпляром типа %s", expectedType.getName())
                .isInstanceOf(expectedType);
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
