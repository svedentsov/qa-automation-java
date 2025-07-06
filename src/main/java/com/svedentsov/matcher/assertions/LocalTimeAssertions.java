package com.svedentsov.matcher.assertions;

import com.svedentsov.matcher.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

/**
 * Утилитный класс, предоставляющий набор условий (матчеров) для проверки полей с типом {@link LocalTime}.
 * <p>Каждый метод возвращает функциональный интерфейс {@link LocalTimeCondition},
 * который можно использовать для валидации времени в составе более сложных проверок объектов.
 */
@UtilityClass
public class LocalTimeAssertions {

    /**
     * Функциональный интерфейс для условий проверки {@link LocalTime}.
     */
    @FunctionalInterface
    public interface LocalTimeCondition extends Condition<LocalTime> {
    }

    /**
     * Проверяет, что время находится до указанного значения.
     *
     * @param expected максимально допустимое время (не включая).
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeBefore(LocalTime expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Время %s должно быть до %s", actual, expected)
                .isBefore(expected);
    }

    /**
     * Проверяет, что время находится после указанного значения.
     *
     * @param expected минимально допустимое время (не включая).
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeAfter(LocalTime expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Время %s должно быть после %s", actual, expected)
                .isAfter(expected);
    }

    /**
     * Проверяет, что время равно указанному значению.
     *
     * @param expected ожидаемое время.
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeEquals(LocalTime expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Время %s должно быть равно %s", actual, expected)
                .isEqualTo(expected);
    }

    /**
     * Проверяет, что время равно или находится после указанного значения.
     *
     * @param expected минимально допустимое время (включая).
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeAfterOrEqual(LocalTime expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Время %s должно быть после или равно %s", actual, expected)
                .isAfterOrEqualTo(expected);
    }

    /**
     * Проверяет, что время равно или находится до указанного значения.
     *
     * @param expected максимально допустимое время (включая).
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeBeforeOrEqual(LocalTime expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Время %s должно быть до или равно %s", actual, expected)
                .isBeforeOrEqualTo(expected);
    }

    /**
     * Проверяет, что время находится между двумя значениями (включительно).
     *
     * @param start начальное время диапазона.
     * @param end   конечное время диапазона.
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeIsBetween(LocalTime start, LocalTime end) {
        return actual -> Assertions.assertThat(actual)
                .as("Время %s должно быть между %s и %s", actual, start, end)
                .isBetween(start, end);
    }

    /**
     * Проверяет, что время находится строго между двумя значениями (не включая границы).
     *
     * @param start начальное время диапазона.
     * @param end   конечное время диапазона.
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeIsStrictlyBetween(LocalTime start, LocalTime end) {
        return actual -> Assertions.assertThat(actual)
                .as("Время %s должно быть строго между %s и %s", actual, start, end)
                .isStrictlyBetween(start, end);
    }

    /**
     * Проверяет, что компонент часа во времени равен указанному значению.
     *
     * @param hour ожидаемый час (0-23).
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeHasHour(int hour) {
        return actual -> Assertions.assertThat(actual.getHour())
                .as("Час во времени %s должен быть %d", actual, hour)
                .isEqualTo(hour);
    }

    /**
     * Проверяет, что компонент минуты во времени равен указанному значению.
     *
     * @param minute ожидаемая минута (0-59).
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeHasMinute(int minute) {
        return actual -> Assertions.assertThat(actual.getMinute())
                .as("Минута во времени %s должна быть %d", actual, minute)
                .isEqualTo(minute);
    }

    /**
     * Проверяет, что компонент секунды во времени равен указанному значению.
     *
     * @param second ожидаемая секунда (0-59).
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeHasSecond(int second) {
        return actual -> Assertions.assertThat(actual.getSecond())
                .as("Секунда во времени %s должна быть %d", actual, second)
                .isEqualTo(second);
    }

    /**
     * Проверяет, что компонент наносекунды во времени равен указанному значению.
     *
     * @param nano ожидаемая наносекунда (0-999,999,999).
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeHasNano(int nano) {
        return actual -> Assertions.assertThat(actual.getNano())
                .as("Наносекунда во времени %s должна быть %d", actual, nano)
                .isEqualTo(nano);
    }

    /**
     * Проверяет, что время равно указанному, игнорируя наносекунды.
     *
     * @param expected ожидаемое время.
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeIsEqualToIgnoringNanos(LocalTime expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Время %s должно быть равно %s (игнорируя наносекунды)", actual, expected)
                .isEqualToIgnoringNanos(expected);
    }

    /**
     * Проверяет, что время равно указанному, игнорируя секунды и наносекунды.
     *
     * @param expected ожидаемое время.
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeIsEqualToIgnoringSeconds(LocalTime expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Время %s должно быть равно %s (игнорируя секунды)", actual, expected)
                .isEqualToIgnoringSeconds(expected);
    }

    /**
     * Проверяет, что время находится в утренние часы (с 06:00 до 12:00, не включая).
     *
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeIsMorning() {
        LocalTime start = LocalTime.of(6, 0);
        LocalTime end = LocalTime.of(12, 0);
        return actual -> Assertions.assertThat(actual)
                .as("Время %s должно быть утром (между %s и %s)", actual, start, end)
                .isAfterOrEqualTo(start)
                .isBefore(end);
    }

    /**
     * Проверяет, что время находится в дневные часы (с 12:00 до 18:00, не включая).
     *
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeIsAfternoon() {
        LocalTime start = LocalTime.of(12, 0);
        LocalTime end = LocalTime.of(18, 0);
        return actual -> Assertions.assertThat(actual)
                .as("Время %s должно быть днем (между %s и %s)", actual, start, end)
                .isAfterOrEqualTo(start)
                .isBefore(end);
    }

    /**
     * Проверяет, что время находится в вечерние часы (с 18:00 до 22:00, не включая).
     *
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeIsEvening() {
        LocalTime start = LocalTime.of(18, 0);
        LocalTime end = LocalTime.of(22, 0);
        return actual -> Assertions.assertThat(actual)
                .as("Время %s должно быть вечером (между %s и %s)", actual, start, end)
                .isAfterOrEqualTo(start)
                .isBefore(end);
    }

    /**
     * Проверяет, что время находится в ночные часы (с 22:00 до 06:00, не включая).
     *
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeIsNight() {
        LocalTime startEvening = LocalTime.of(22, 0);
        LocalTime endMorning = LocalTime.of(6, 0);
        return actual -> {
            boolean isNight = !actual.isBefore(startEvening) || actual.isBefore(endMorning);
            Assertions.assertThat(isNight)
                    .as("Время %s должно быть ночью (с %s до %s)", actual, startEvening, endMorning)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что время соответствует рабочим часам (например, с 9:00 до 18:00).
     *
     * @param startOfWork начало рабочего дня.
     * @param endOfWork   конец рабочего дня.
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeIsOfficeHours(LocalTime startOfWork, LocalTime endOfWork) {
        return actual -> Assertions.assertThat(actual)
                .as("Время %s должно быть в пределах рабочих часов (с %s по %s)", actual, startOfWork, endOfWork)
                .isBetween(startOfWork, endOfWork);
    }

    /**
     * Проверяет, что время является {@link LocalTime#MIDNIGHT} (00:00).
     *
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeIsMidnight() {
        return actual -> Assertions.assertThat(actual)
                .as("Время %s должно быть полночью (00:00:00)", actual)
                .isEqualTo(LocalTime.MIDNIGHT);
    }

    /**
     * Проверяет, что время является {@link LocalTime#NOON} (12:00).
     *
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeIsNoon() {
        return actual -> Assertions.assertThat(actual)
                .as("Время %s должно быть полднем (12:00:00)", actual)
                .isEqualTo(LocalTime.NOON);
    }

    /**
     * Проверяет, что значение поля времени (`ChronoField`) равно ожидаемому.
     *
     * @param field поле времени (например, {@link ChronoField#HOUR_OF_AMPM}).
     * @param value ожидаемое значение поля.
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeHasFieldValue(ChronoField field, long value) {
        return actual -> Assertions.assertThat(actual.get(field))
                .as("Значение поля %s во времени %s должно быть %d", field, actual, value)
                .isEqualTo(value);
    }

    /**
     * Проверяет, что время до полудня (AM).
     *
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeIsAm() {
        return actual -> Assertions.assertThat(actual.get(ChronoField.AMPM_OF_DAY))
                .as("Время %s должно быть до полудня (AM)", actual)
                .isEqualTo(0);
    }

    /**
     * Проверяет, что время после полудня (PM).
     *
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeIsPm() {
        return actual -> Assertions.assertThat(actual.get(ChronoField.AMPM_OF_DAY))
                .as("Время %s должно быть после полудня (PM)", actual)
                .isEqualTo(1);
    }

    /**
     * Проверяет, что время находится внутри или на границах "часа" (например, между 14:00:00 и 14:59:59.999...).
     *
     * @param hour час для проверки (0-23).
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeIsWithinHour(int hour) {
        return actual -> Assertions.assertThat(actual.getHour())
                .as("Время %s должно быть внутри %d-го часа", actual, hour)
                .isEqualTo(hour);
    }

    /**
     * Проверяет, что время находится внутри или на границах "минуты" (например, между 14:30:00 и 14:30:59.999...).
     *
     * @param hour   час (0-23).
     * @param minute минута (0-59).
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeIsWithinMinute(int hour, int minute) {
        return actual -> {
            Assertions.assertThat(actual.getHour()).as("Час должен быть %d", hour).isEqualTo(hour);
            Assertions.assertThat(actual.getMinute()).as("Минута должна быть %d", minute).isEqualTo(minute);
        };
    }

    /**
     * Проверяет, что время усечено до указанной единицы (например, до минут, т.е. секунды и наносекунды равны 0).
     *
     * @param unit единица времени, до которой должно быть усечено время (например, {@link ChronoUnit#MINUTES}).
     * @return {@link LocalTimeCondition} для проверки.
     */
    public static LocalTimeCondition timeIsTruncatedTo(ChronoUnit unit) {
        return actual -> Assertions.assertThat(actual)
                .as("Время %s должно быть усечено до %s", actual, unit)
                .isEqualTo(actual.truncatedTo(unit));
    }
}
