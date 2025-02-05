package db.matcher.assertions;

import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.time.LocalDateTime;

/**
 * Утилитный класс для проверки временных свойств (LocalDateTime) сущности.
 */
@UtilityClass
public class TimeAssertions {

    /**
     * Функциональный интерфейс для проверки LocalDateTime значений.
     */
    @FunctionalInterface
    public interface TimestampCondition {
        /**
         * Проверяет значение LocalDateTime.
         *
         * @param value значение даты и времени для проверки
         */
        void check(LocalDateTime value);
    }

    /**
     * Возвращает условие, проверяющее, что значение LocalDateTime раньше (до) указанного момента.
     *
     * @param dateTime момент времени, до которого должно быть значение
     * @return условие проверки, что дата раньше указанной
     */
    public static TimestampCondition dateBefore(LocalDateTime dateTime) {
        return timestamp -> Assertions.assertThat(timestamp)
                .as("Дата должна быть до %s", dateTime)
                .isBefore(dateTime);
    }

    /**
     * Возвращает условие, проверяющее, что значение LocalDateTime строго позже указанного момента.
     *
     * @param dateTime момент времени, после которого должно быть значение
     * @return условие проверки, что дата позже указанной
     */
    public static TimestampCondition localDateTimeAfter(LocalDateTime dateTime) {
        return timestamp -> Assertions.assertThat(timestamp)
                .as("Дата и время должны быть после %s", dateTime)
                .isAfter(dateTime);
    }

    /**
     * Возвращает условие, проверяющее, что значение LocalDateTime находится в будущем (после настоящего момента).
     *
     * @return условие проверки, что дата находится в будущем
     */
    public static TimestampCondition isInFuture() {
        return timestamp -> Assertions.assertThat(timestamp)
                .as("Дата и время должны быть в будущем")
                .isAfter(LocalDateTime.now());
    }

    /**
     * Возвращает условие, проверяющее, что значение LocalDateTime находится в прошлом (до настоящего момента).
     *
     * @return условие проверки, что дата находится в прошлом
     */
    public static TimestampCondition isInPast() {
        return timestamp -> Assertions.assertThat(timestamp)
                .as("Дата и время должны быть в прошлом")
                .isBefore(LocalDateTime.now());
    }
}
