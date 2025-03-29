package kafka.matcher.assertions;

import kafka.matcher.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.math.BigDecimal;
import java.math.BigInteger;

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
     * Проверяет, что значение является числом (экземпляром Number).
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isNumber() {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть числом (экземпляром Number)")
                .isInstanceOf(Number.class);
    }

    /**
     * Проверяет, что число равно ожидаемому значению (generic-версия).
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
     * Проверяет, что число не равно ожидаемому значению (generic-версия).
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
     * Проверяет, что число больше указанного значения (generic-версия).
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
     * Проверяет, что число меньше указанного значения (generic-версия).
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
     * Проверяет, что число больше или равно указанному значению (generic-версия).
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
     * Проверяет, что число меньше или равно указанному значению (generic-версия).
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
     * Проверяет, что число находится в диапазоне [start, end] (включительно) (generic-версия).
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
     * Проверяет, что число находится в диапазоне (start, end) (строго, без включения границ) (generic-версия).
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
     * Проверяет, что число равно нулю (через doubleValue()).
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
     * Проверяет, что число не равно нулю (через doubleValue()).
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
     * Проверяет, что число положительно (через doubleValue()).
     *
     * @param <T> тип числа
     * @return условие "положительное (больше 0)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isPositive() {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть положительным (больше 0)")
                .isGreaterThan(0.0);
    }

    /**
     * Проверяет, что число отрицательно (через doubleValue()).
     *
     * @param <T> тип числа
     * @return условие "отрицательное (меньше 0)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isNegative() {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть отрицательным (меньше 0)")
                .isLessThan(0.0);
    }

    /**
     * Проверяет, что число неотрицательно (>= 0) (через doubleValue()).
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
     * Проверяет, что число неположительно (<= 0) (через doubleValue()).
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
     * @param <T> тип числа (Integer, Long, Byte, Short, BigInteger и т.п.)
     * @return условие "чётное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isEven() {
        return actual -> Assertions.assertThat(actual.longValue())
                .as("Значение должно быть чётным")
                .isEven();
    }

    /**
     * Проверяет, что целочисленное число является нечётным.
     *
     * @param <T> тип числа (Integer, Long, Byte, Short, BigInteger и т.п.)
     * @return условие "нечётное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isOdd() {
        return actual -> Assertions.assertThat(actual.longValue())
                .as("Значение должно быть нечётным")
                .isOdd();
    }

    /**
     * Проверяет, что целочисленное число является кратным указанному делителю.
     *
     * @param divisor делитель
     * @param <T>     тип числа (Integer, Long, Byte, Short, BigInteger и т.п.)
     * @return условие "кратно"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> multipleOf(long divisor) {
        return actual -> Assertions.assertThat(actual.longValue() % divisor)
                .as("Значение должно быть кратно %d", divisor)
                .isEqualTo(0L);
    }

    /**
     * Проверяет, что число является экземпляром Integer.
     *
     * @param <T> тип числа
     * @return условие проверки типа
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isIntegerType() {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть экземпляром Integer")
                .isInstanceOf(Integer.class);
    }

    /**
     * Проверяет, что число является экземпляром Long.
     *
     * @param <T> тип числа
     * @return условие проверки типа
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isLongType() {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть экземпляром Long")
                .isInstanceOf(Long.class);
    }

    /**
     * Проверяет, что число является экземпляром Float.
     *
     * @param <T> тип числа
     * @return условие проверки типа
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isFloatType() {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть экземпляром Float")
                .isInstanceOf(Float.class);
    }

    /**
     * Проверяет, что число является экземпляром Double.
     *
     * @param <T> тип числа
     * @return условие проверки типа
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isDoubleType() {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть экземпляром Double")
                .isInstanceOf(Double.class);
    }

    /**
     * Проверяет, что число является экземпляром BigDecimal.
     *
     * @param <T> тип числа
     * @return условие проверки типа
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isBigDecimalType() {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть экземпляром BigDecimal")
                .isInstanceOf(BigDecimal.class);
    }

    /**
     * Проверяет, что число является экземпляром BigInteger.
     *
     * @param <T> тип числа
     * @return условие проверки типа
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isBigIntegerType() {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть экземпляром BigInteger")
                .isInstanceOf(BigInteger.class);
    }

    /**
     * Проверяет, что число является экземпляром Short.
     *
     * @param <T> тип числа
     * @return условие проверки типа
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isShortType() {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть экземпляром Short")
                .isInstanceOf(Short.class);
    }

    /**
     * Проверяет, что число является экземпляром Byte.
     *
     * @param <T> тип числа
     * @return условие проверки типа
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isByteType() {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть экземпляром Byte")
                .isInstanceOf(Byte.class);
    }

    /**
     * Проверяет, что число равно единице (через intValue()).
     *
     * @param <T> тип числа
     * @return условие "равно 1"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isOne() {
        return actual -> Assertions.assertThat(actual.intValue())
                .as("Значение должно быть равно 1")
                .isEqualTo(1);
    }

    /**
     * Проверяет, что число равно минус единице (через intValue()).
     *
     * @param <T> тип числа
     * @return условие "равно -1"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isMinusOne() {
        return actual -> Assertions.assertThat(actual.intValue())
                .as("Значение должно быть равно -1")
                .isEqualTo(-1);
    }

    /**
     * Проверяет, что число является положительной бесконечностью (для Float и Double).
     *
     * @param <T> тип числа
     * @return условие "положительная бесконечность"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isPositiveInfinity() {
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
     * Проверяет, что число является отрицательной бесконечностью (для Float и Double).
     *
     * @param <T> тип числа
     * @return условие "отрицательная бесконечность"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isNegativeInfinity() {
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
     * Проверяет, что число является строго положительным (больше 0) (через doubleValue()).
     *
     * @param <T> тип числа
     * @return условие "строго положительное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isStrictlyPositive() {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть строго больше 0")
                .isGreaterThan(0.0);
    }

    /**
     * Проверяет, что число является строго отрицательным (меньше 0) (через doubleValue()).
     *
     * @param <T> тип числа
     * @return условие "строго отрицательное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isStrictlyNegative() {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть строго меньше 0")
                .isLessThan(0.0);
    }

    /**
     * Проверяет, что число является степенью двойки (только для целых чисел).
     *
     * @param <T> тип числа
     * @return условие "степень двойки"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isPowerOfTwo() {
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
     * Проверяет, что число является простым (только для целых чисел больше 1).
     *
     * @param <T> тип числа
     * @return условие "простое число"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isPrime() {
        return actual -> {
            if (actual instanceof Integer
                    || actual instanceof Long
                    || actual instanceof Short
                    || actual instanceof Byte
                    || actual instanceof BigInteger) {
                long n = actual.longValue();
                if (n <= 1) {
                    Assertions.fail("Значение должно быть простым (больше 1)");
                    return;
                }
                for (long i = 2; i * i <= n; i++) {
                    if (n % i == 0) {
                        Assertions.fail("Значение должно быть простым");
                        return;
                    }
                }
                // Успешно прошли проверку
                Assertions.assertThat(true).as("Значение должно быть простым").isTrue();
            } else {
                Assertions.fail("Проверка на простое число применима только к целым числам");
            }
        };
    }

    /**
     * Проверяет, что число является чётным и положительным (через longValue() и doubleValue()).
     *
     * @param <T> тип числа
     * @return условие "чётное и положительное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isEvenAndPositive() {
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
     * Проверяет, что число является нечётным и положительным (через longValue() и doubleValue()).
     *
     * @param <T> тип числа
     * @return условие "нечётное и положительное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isOddAndPositive() {
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
     * Проверяет, что число является чётным и отрицательным (через longValue() и doubleValue()).
     *
     * @param <T> тип числа
     * @return условие "чётное и отрицательное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isEvenAndNegative() {
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
     * Проверяет, что число является нечётным и отрицательным (через longValue() и doubleValue()).
     *
     * @param <T> тип числа
     * @return условие "нечётное и отрицательное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isOddAndNegative() {
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
     * Проверяет, что число является положительным и кратным указанному делителю.
     *
     * @param divisor делитель
     * @param <T>     тип числа
     * @return условие "положительное и кратное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isPositiveAndMultipleOf(long divisor) {
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
     * Проверяет, что число является отрицательным и кратным указанному делителю.
     *
     * @param divisor делитель
     * @param <T>     тип числа
     * @return условие "отрицательное и кратное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isNegativeAndMultipleOf(long divisor) {
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
     * Проверяет, что число находится в диапазоне (start, end] (строго больше start и меньше или равно end).
     *
     * @param start начало диапазона
     * @param end   конец диапазона
     * @param <T>   тип числа
     * @return условие "в диапазоне (start, end]"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isGreaterThanAndLessThanOrEqualTo(T start, T end) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть > %s и <= %s", start, end)
                .isGreaterThan(start)
                .isLessThanOrEqualTo(end);
    }

    /**
     * Проверяет, что число находится в диапазоне [start, end) (больше или равно start и строго меньше end).
     *
     * @param start начало диапазона
     * @param end   конец диапазона
     * @param <T>   тип числа
     * @return условие "в диапазоне [start, end)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isGreaterThanOrEqualToAndLessThan(T start, T end) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть >= %s и < %s", start, end)
                .isGreaterThanOrEqualTo(start)
                .isLessThan(end);
    }

    /**
     * Проверяет, что число является "близким к нулю", сравнивая абсолютное значение с некоторым небольшим порогом (Double.MIN_VALUE * 10).
     *
     * @param <T> тип числа
     * @return условие "близко к нулю"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isCloseToZero() {
        return actual -> Assertions.assertThat(Math.abs(actual.doubleValue()))
                .as("Значение должно быть близко к нулю")
                .isLessThan(Double.MIN_VALUE * 10);
    }

    /**
     * Проверяет, что число является делителем указанного значения (только для целых).
     *
     * @param number число, которое должно делиться
     * @param <T>    тип числа (предполагается целое число)
     * @return условие "является делителем"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isDivisorOf(long number) {
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
     * Проверяет, что абсолютное значение числа равно указанному значению.
     *
     * @param expected абсолютное значение
     * @param <T>      тип числа
     * @return условие "абсолютное значение равно"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isEqualToAbsoluteValue(T expected) {
        return actual -> Assertions.assertThat(Math.abs(actual.doubleValue()))
                .as("Абсолютное значение должно быть равно %s", expected)
                .isEqualTo(expected.doubleValue());
    }

    /**
     * Проверяет, что абсолютное значение числа больше указанного значения.
     *
     * @param expected абсолютное значение
     * @param <T>      тип числа
     * @return условие "абсолютное значение больше"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isGreaterThanAbsoluteValue(T expected) {
        return actual -> Assertions.assertThat(Math.abs(actual.doubleValue()))
                .as("Абсолютное значение должно быть больше %s", expected)
                .isGreaterThan(expected.doubleValue());
    }

    /**
     * Проверяет, что абсолютное значение числа меньше указанного значения.
     *
     * @param expected абсолютное значение
     * @param <T>      тип числа
     * @return условие "абсолютное значение меньше"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isLessThanAbsoluteValue(T expected) {
        return actual -> Assertions.assertThat(Math.abs(actual.doubleValue()))
                .as("Абсолютное значение должно быть меньше %s", expected)
                .isLessThan(expected.doubleValue());
    }

    /**
     * Проверяет, что число находится в полуоткрытом диапазоне [start, end) для чисел с плавающей точкой (через doubleValue()).
     *
     * @param min минимальное значение (включительно)
     * @param max максимальное значение (исключительно)
     * @param <T> тип числа
     * @return условие "в диапазоне с плавающей точкой (start включительно, end исключительно)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isFloatRangeStartInclusiveEndExclusive(double min, double max) {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть >= %s и < %s", min, max)
                .isGreaterThanOrEqualTo(min)
                .isLessThan(max);
    }

    /**
     * Проверяет, что число находится в полуоткрытом диапазоне (start, end] для чисел с плавающей точкой (через doubleValue()).
     *
     * @param min минимальное значение (исключительно)
     * @param max максимальное значение (включительно)
     * @param <T> тип числа
     * @return условие "в диапазоне с плавающей точкой (start исключительно, end включительно)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isFloatRangeStartExclusiveEndInclusive(double min, double max) {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть > %s и <= %s", min, max)
                .isGreaterThan(min)
                .isLessThanOrEqualTo(max);
    }

    /**
     * Проверяет, что число является строго положительным целым (не включая ноль).
     *
     * @param <T> тип числа
     * @return условие "строго положительное целое число"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isStrictlyPositiveInteger() {
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
     * Проверяет, что число является строго отрицательным целым (не включая ноль).
     *
     * @param <T> тип числа
     * @return условие "строго отрицательное целое число"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isStrictlyNegativeInteger() {
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
     * Проверяет, что число является нулём для целочисленных типов.
     *
     * @param <T> тип числа
     * @return условие "ноль для целого числа"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isZeroInteger() {
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
     * Проверяет, что число является единицей для целочисленных типов.
     *
     * @param <T> тип числа
     * @return условие "единица для целого числа"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isOneInteger() {
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
     * Проверяет, что число является нечётным и неотрицательным (>= 0).
     *
     * @param <T> тип числа
     * @return условие "неотрицательное и нечётное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isOddAndNonNegative() {
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
     * Проверяет, что число является чётным и неотрицательным (>= 0).
     *
     * @param <T> тип числа
     * @return условие "неотрицательное и чётное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isEvenAndNonNegative() {
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
     * Проверяет, что число является нечётным и неположительным (<= 0).
     *
     * @param <T> тип числа
     * @return условие "неположительное и нечётное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isOddAndNonPositive() {
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
     * Проверяет, что число является чётным и неположительным (<= 0).
     *
     * @param <T> тип числа
     * @return условие "неположительное и чётное"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isEvenAndNonPositive() {
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
     * Проверяет, что число является экземпляром указанного класса.
     *
     * @param type ожидаемый класс числа
     * @param <T>  тип числа
     * @return условие проверки типа
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> isInstanceOf(Class<?> type) {
        return actual -> Assertions.assertThat(actual)
                .as("Значение должно быть экземпляром %s", type.getName())
                .isInstanceOf(type);
    }

    /**
     * Проверяет, что число имеет scale, равный указанному значению (для BigDecimal).
     *
     * @param expectedScale ожидаемый scale
     * @param <T>           тип числа
     * @return условие проверки scale
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> hasScale(int expectedScale) {
        return actual -> {
            if (actual instanceof BigDecimal) {
                Assertions.assertThat(((BigDecimal) actual).scale())
                        .as("Scale должна быть равна %d", expectedScale)
                        .isEqualTo(expectedScale);
            } else {
                Assertions.fail("Проверка scale применима только к BigDecimal");
            }
        };
    }

    /**
     * Проверяет, что число имеет precision, равный указанному значению (для BigDecimal).
     *
     * @param expectedPrecision ожидаемая точность (precision)
     * @param <T>               тип числа
     * @return условие проверки precision
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> hasPrecision(int expectedPrecision) {
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
     * Проверяет, что число больше или равно указанному значению с плавающей точкой.
     *
     * @param expected значение
     * @param <T>      тип числа
     * @return условие "больше или равно (double)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> greaterOrEqualTo(double expected) {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть >= %s", expected)
                .isGreaterThanOrEqualTo(expected);
    }

    /**
     * Проверяет, что число меньше или равно указанному значению с плавающей точкой.
     *
     * @param expected значение
     * @param <T>      тип числа
     * @return условие "меньше или равно (double)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> lessOrEqualTo(double expected) {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть <= %s", expected)
                .isLessThanOrEqualTo(expected);
    }

    /**
     * Проверяет, что число больше указанного значения с плавающей точкой.
     *
     * @param expected значение
     * @param <T>      тип числа
     * @return условие "больше (double)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> greaterThan(double expected) {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть > %s", expected)
                .isGreaterThan(expected);
    }

    /**
     * Проверяет, что число меньше указанного значения с плавающей точкой.
     *
     * @param expected значение
     * @param <T>      тип числа
     * @return условие "меньше (double)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> lessThan(double expected) {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть < %s", expected)
                .isLessThan(expected);
    }

    /**
     * Проверяет, что число равно указанному значению с плавающей точкой.
     *
     * @param expected значение
     * @param <T>      тип числа
     * @return условие "равно (double)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> equalTo(double expected) {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть равно %s", expected)
                .isEqualTo(expected);
    }

    /**
     * Проверяет, что число не равно указанному значению с плавающей точкой.
     *
     * @param expected значение
     * @param <T>      тип числа
     * @return условие "не равно (double)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> notEqualTo(double expected) {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение не должно быть равно %s", expected)
                .isNotEqualTo(expected);
    }

    /**
     * Проверяет, что число находится в диапазоне [min, max] с плавающей точкой.
     *
     * @param min начало диапазона
     * @param max конец диапазона
     * @param <T> тип числа
     * @return условие "в диапазоне [min, max] (double)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> inRange(double min, double max) {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть в диапазоне [%s, %s]", min, max)
                .isBetween(min, max);
    }

    /**
     * Проверяет, что число находится в диапазоне (min, max) с плавающей точкой.
     *
     * @param min начало диапазона
     * @param max конец диапазона
     * @param <T> тип числа
     * @return условие "в диапазоне (min, max) (double)"
     */
    public static <T extends Number & Comparable<T>> NumberCondition<T> betweenExclusive(double min, double max) {
        return actual -> Assertions.assertThat(actual.doubleValue())
                .as("Значение должно быть в диапазоне (%s, %s)", min, max)
                .isGreaterThan(min)
                .isLessThan(max);
    }
}
