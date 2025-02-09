package rest.matcher.assertions;

import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.HamcrestCondition;
import org.hamcrest.Matcher;
import rest.matcher.Condition;

import java.time.Duration;
import java.util.Objects;

/**
 * Класс для утверждений, связанных с временем ответа.
 */
@UtilityClass
public class TimeAssertions {

    /**
     * Функциональный интерфейс для условий проверки времени ответа.
     */
    @FunctionalInterface
    public interface TimeCondition extends Condition {
    }

    /**
     * Проверяет, что время ответа меньше или равно заданному значению.
     *
     * @param maxDuration максимальное время ответа
     * @return условие для проверки времени ответа
     * @throws IllegalArgumentException если {@code maxDuration} равно {@code null}
     */
    public static TimeCondition responseTimeLessThan(Duration maxDuration) {
        Objects.requireNonNull(maxDuration, "maxDuration не может быть null");
        return response -> {
            long time = response.getTime();
            Assertions.assertThat(time)
                    .as("Ожидалось, что время ответа будет меньше или равно %d мс, но было %d мс", maxDuration.toMillis(), time)
                    .isLessThanOrEqualTo(maxDuration.toMillis());
        };
    }

    /**
     * Проверяет, что время ответа соответствует заданному Matcher.
     *
     * @param matcher Matcher для проверки времени ответа (в миллисекундах)
     * @return условие для проверки времени ответа
     * @throws IllegalArgumentException если {@code matcher} равно {@code null}
     */
    public static TimeCondition responseTimeMatches(Matcher<Long> matcher) {
        Objects.requireNonNull(matcher, "matcher не может быть null");
        return response -> {
            long time = response.getTime();
            Assertions.assertThat(time)
                    .as("Ожидалось, что время ответа будет соответствовать %s, но было %d мс", matcher, time)
                    .is(new HamcrestCondition<>(matcher));
        };
    }

    /**
     * Проверяет, что время ответа больше или равно заданному значению.
     *
     * @param minDuration минимальное время ответа
     * @return условие для проверки времени ответа
     * @throws IllegalArgumentException если {@code minDuration} равно {@code null}
     */
    public static TimeCondition responseTimeGreaterThan(Duration minDuration) {
        Objects.requireNonNull(minDuration, "minDuration не может быть null");
        return response -> {
            long time = response.getTime();
            Assertions.assertThat(time)
                    .as("Ожидалось, что время ответа будет больше или равно %d мс, но было %d мс", minDuration.toMillis(), time)
                    .isGreaterThanOrEqualTo(minDuration.toMillis());
        };
    }

    /**
     * Проверяет, что время ответа находится в заданном диапазоне.
     *
     * @param minDuration минимальное время ответа
     * @param maxDuration максимальное время ответа
     * @return условие для проверки диапазона времени ответа
     * @throws IllegalArgumentException если {@code minDuration} или {@code maxDuration} равны {@code null},
     *                                  или {@code minDuration} превышает {@code maxDuration}
     */
    public static TimeCondition responseTimeBetween(Duration minDuration, Duration maxDuration) {
        Objects.requireNonNull(minDuration, "minDuration не может быть null");
        Objects.requireNonNull(maxDuration, "maxDuration не может быть null");
        if (minDuration.toMillis() > maxDuration.toMillis()) {
            throw new IllegalArgumentException("minDuration не может быть больше maxDuration");
        }
        return response -> {
            long time = response.getTime();
            Assertions.assertThat(time)
                    .as("Ожидалось, что время ответа будет в диапазоне от %d мс до %d мс, но было %d мс",
                            minDuration.toMillis(), maxDuration.toMillis(), time)
                    .isBetween(minDuration.toMillis(), maxDuration.toMillis());
        };
    }

    /**
     * Проверяет, что время ответа больше указанного значения.
     *
     * @param minDuration минимальное время ответа
     * @return условие для проверки времени ответа
     * @throws IllegalArgumentException если {@code minDuration} равно {@code null}
     */
    public static TimeCondition responseTimeGreaterThanExclusive(Duration minDuration) {
        Objects.requireNonNull(minDuration, "minDuration не может быть null");
        return response -> {
            long time = response.getTime();
            Assertions.assertThat(time)
                    .as("Ожидалось, что время ответа будет больше %d мс, но было %d мс", minDuration.toMillis(), time)
                    .isGreaterThan(minDuration.toMillis());
        };
    }

    /**
     * Проверяет, что время ответа меньше указанного значения.
     *
     * @param maxDuration максимальное время ответа
     * @return условие для проверки времени ответа
     * @throws IllegalArgumentException если {@code maxDuration} равно {@code null}
     */
    public static TimeCondition responseTimeLessThanExclusive(Duration maxDuration) {
        Objects.requireNonNull(maxDuration, "maxDuration не может быть null");
        return response -> {
            long time = response.getTime();
            Assertions.assertThat(time)
                    .as("Ожидалось, что время ответа будет меньше %d мс, но было %d мс", maxDuration.toMillis(), time)
                    .isLessThan(maxDuration.toMillis());
        };
    }

    /**
     * Проверяет, что время ответа равно указанному значению в миллисекундах.
     *
     * @param expectedTime ожидаемое время ответа в миллисекундах
     * @return условие для проверки точного времени ответа
     */
    public static TimeCondition responseTimeEquals(long expectedTime) {
        return response -> {
            long time = response.getTime();
            Assertions.assertThat(time)
                    .as("Ожидалось, что время ответа будет %d мс, но было %d мс", expectedTime, time)
                    .isEqualTo(expectedTime);
        };
    }

    /**
     * Проверяет, что время ответа больше средней величины, вычисленной на основе списка ответов.
     *
     * @param averageTime среднее время ответа
     * @return условие для проверки времени ответа
     * @throws IllegalArgumentException если {@code averageTime} отрицательно
     */
    public static TimeCondition responseTimeGreaterThanAverage(long averageTime) {
        if (averageTime < 0) {
            throw new IllegalArgumentException("averageTime не может быть отрицательным");
        }
        return response -> {
            long time = response.getTime();
            Assertions.assertThat(time)
                    .as("Ожидалось, что время ответа будет больше средней величины %d мс, но было %d мс", averageTime, time)
                    .isGreaterThan(averageTime);
        };
    }

    /**
     * Проверяет, что время ответа меньше средней величины, вычисленной на основе списка ответов.
     *
     * @param averageTime среднее время ответа
     * @return условие для проверки времени ответа
     * @throws IllegalArgumentException если {@code averageTime} отрицательно
     */
    public static TimeCondition responseTimeLessThanAverage(long averageTime) {
        if (averageTime < 0) {
            throw new IllegalArgumentException("averageTime не может быть отрицательным");
        }
        return response -> {
            long time = response.getTime();
            Assertions.assertThat(time)
                    .as("Ожидалось, что время ответа будет меньше средней величины %d мс, но было %d мс", averageTime, time)
                    .isLessThan(averageTime);
        };
    }

    /**
     * Проверяет, что время ответа не превышает указанную процентильную величину.
     *
     * @param percentilePercent процентиль (например, 95 для 95-й процентиль)
     * @param thresholdMillis   пороговое значение в миллисекундах
     * @return условие для проверки процентильного времени ответа
     * @throws IllegalArgumentException если {@code percentilePercent} не находится в диапазоне 0-100
     */
    public static TimeCondition responseTimeWithinPercentile(int percentilePercent, long thresholdMillis) {
        if (percentilePercent < 0 || percentilePercent > 100) {
            throw new IllegalArgumentException("percentilePercent должен быть в диапазоне 0-100");
        }
        if (thresholdMillis < 0) {
            throw new IllegalArgumentException("thresholdMillis не может быть отрицательным");
        }
        return response -> {
            long time = response.getTime();
            Assertions.assertThat(time)
                    .as("Ожидалось, что время ответа будет в пределах %d-й процентиль %d мс, но было %d мс",
                            percentilePercent, thresholdMillis, time)
                    .isLessThanOrEqualTo(thresholdMillis);
        };
    }

    /**
     * Проверяет, что время ответа находится в пределах ожидаемого диапазона с учетом допуска.
     *
     * @param expectedDuration ожидаемое время ответа
     * @param tolerance        допуск на время ответа
     * @return условие для проверки времени ответа с учетом допуска
     * @throws IllegalArgumentException если {@code expectedDuration} или {@code tolerance} равны {@code null},
     *                                  или {@code tolerance} отрицательна
     */
    public static TimeCondition responseTimeWithinTolerance(Duration expectedDuration, Duration tolerance) {
        Objects.requireNonNull(expectedDuration, "expectedDuration не может быть null");
        Objects.requireNonNull(tolerance, "tolerance не может быть null");
        if (tolerance.isNegative()) {
            throw new IllegalArgumentException("tolerance не может быть отрицательным");
        }
        long minTime = expectedDuration.minus(tolerance).toMillis();
        long maxTime = expectedDuration.plus(tolerance).toMillis();
        return responseTimeBetween(Duration.ofMillis(minTime), Duration.ofMillis(maxTime));
    }

    /**
     * Проверяет, что время ответа значительно отличается от ожидаемого значения.
     * Это может быть полезно для обнаружения аномальных задержек.
     *
     * @param expectedDuration ожидаемое время ответа
     * @param deviation        допустимое отклонение в миллисекундах
     * @return условие для проверки аномального времени ответа
     * @throws IllegalArgumentException если {@code expectedDuration} равно {@code null},
     *                                  или {@code deviation} отрицательно
     */
    public static TimeCondition responseTimeDeviationExceeds(Duration expectedDuration, long deviation) {
        Objects.requireNonNull(expectedDuration, "expectedDuration не может быть null");
        if (deviation < 0) {
            throw new IllegalArgumentException("deviation не может быть отрицательным");
        }
        return response -> {
            long time = response.getTime();
            long expectedMillis = expectedDuration.toMillis();
            if (time > expectedMillis + deviation || time < expectedMillis - deviation) {
                throw new AssertionError(String.format(
                        "Ожидалось, что время ответа будет в пределах %d мс ± %d мс, но было %d мс",
                        expectedMillis, deviation, time));
            }
        };
    }
}
