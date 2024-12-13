package kafka.matcher.assertions;

import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.time.Instant;

/**
 * Утилитный класс для создания условий для временных меток.
 */
@UtilityClass
public final class TimestampAssertions {

    /**
     * Функциональный интерфейс для условий, проверяющих временную метку.
     */
    @FunctionalInterface
    public interface TimestampCondition {
        /**
         * Проверяет временную метку.
         *
         * @param actual временная метка записи
         */
        void check(Instant actual);
    }

    /**
     * Проверяет, что временная метка раньше указанного времени.
     */
    public static TimestampCondition before(Instant time) {
        return actual -> Assertions.assertThat(actual)
                .as("Временная метка должна быть до %s", time)
                .isBefore(time);
    }

    /**
     * Проверяет, что временная метка позже указанного времени.
     */
    public static TimestampCondition after(Instant time) {
        return actual -> Assertions.assertThat(actual)
                .as("Временная метка должна быть после %s", time)
                .isAfter(time);
    }

    /**
     * Проверяет, что временная метка находится в заданном диапазоне.
     */
    public static TimestampCondition inRange(Instant start, Instant end) {
        return actual -> Assertions.assertThat(actual)
                .as("Временная метка должна быть в диапазоне %s - %s", start, end)
                .isBetween(start, end);
    }

    /**
     * Проверяет, что временная метка равна указанному времени.
     */
    public static TimestampCondition equalsTo(Instant time) {
        return actual -> Assertions.assertThat(actual)
                .as("Временная метка должна быть равна %s", time)
                .isEqualTo(time);
    }

    /**
     * Проверяет, что временная метка не равна указанному времени.
     */
    public static TimestampCondition notEqualsTo(Instant time) {
        return actual -> Assertions.assertThat(actual)
                .as("Временная метка не должна быть равна %s", time)
                .isNotEqualTo(time);
    }

    /**
     * Проверяет, что временная метка после или равна указанному времени.
     */
    public static TimestampCondition afterOrEqualTo(Instant time) {
        return actual -> Assertions.assertThat(actual)
                .as("Временная метка должна быть после или равна %s", time)
                .isAfterOrEqualTo(time);
    }

    /**
     * Проверяет, что временная метка до или равна указанному времени.
     */
    public static TimestampCondition beforeOrEqualTo(Instant time) {
        return actual -> Assertions.assertThat(actual)
                .as("Временная метка должна быть раньше или равна %s", time)
                .isBeforeOrEqualTo(time);
    }

    /**
     * Проверяет, что временная метка является прошлым временем (раньше текущего момента).
     */
    public static TimestampCondition isInPast() {
        return actual -> Assertions.assertThat(actual)
                .as("Временная метка должна быть в прошлом")
                .isBefore(Instant.now());
    }

    /**
     * Проверяет, что временная метка является будущим временем (позже текущего момента).
     */
    public static TimestampCondition isInFuture() {
        return actual -> Assertions.assertThat(actual)
                .as("Временная метка должна быть в будущем")
                .isAfter(Instant.now());
    }
}
