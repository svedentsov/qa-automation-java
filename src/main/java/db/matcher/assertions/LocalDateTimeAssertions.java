package db.matcher.assertions;

import db.matcher.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;

/**
 * Утилитный класс для проверки временных свойств (LocalDateTime) сущности.
 */
@UtilityClass
public class LocalDateTimeAssertions {

    /**
     * Функциональный интерфейс для проверки значений типа LocalDateTime.
     */
    @FunctionalInterface
    public interface LocalDateTimeCondition extends Condition<LocalDateTime> {
    }

    /**
     * Проверяет, что значение LocalDateTime раньше (до) указанного момента.
     *
     * @param dateTime момент времени, до которого должно быть значение
     * @return условие проверки, что дата раньше указанной
     */
    public static LocalDateTimeCondition dateBefore(LocalDateTime dateTime) {
        return timestamp -> Assertions.assertThat(timestamp)
                .as("Дата должна быть до %s", dateTime)
                .isBefore(dateTime);
    }

    /**
     * Проверяет, что значение LocalDateTime строго позже указанного момента.
     *
     * @param dateTime момент времени, после которого должно быть значение
     * @return условие проверки, что дата позже указанной
     */
    public static LocalDateTimeCondition localDateTimeAfter(LocalDateTime dateTime) {
        return timestamp -> Assertions.assertThat(timestamp)
                .as("Дата и время должны быть после %s", dateTime)
                .isAfter(dateTime);
    }

    /**
     * Проверяет, что значение LocalDateTime находится в будущем (после настоящего момента).
     *
     * @return условие проверки, что дата находится в будущем
     */
    public static LocalDateTimeCondition isInFuture() {
        return timestamp -> Assertions.assertThat(timestamp)
                .as("Дата и время должны быть в будущем")
                .isAfter(LocalDateTime.now());
    }

    /**
     * Проверяет, что значение LocalDateTime находится в прошлом (до настоящего момента).
     *
     * @return условие проверки, что дата находится в прошлом
     */
    public static LocalDateTimeCondition isInPast() {
        return timestamp -> Assertions.assertThat(timestamp)
                .as("Дата и время должны быть в прошлом")
                .isBefore(LocalDateTime.now());
    }

    /**
     * Проверяет, что значение LocalDateTime точно равно указанному.
     *
     * @param dateTime ожидаемое значение даты и времени
     * @return условие проверки равенства
     */
    public static LocalDateTimeCondition dateEquals(LocalDateTime dateTime) {
        return timestamp -> Assertions.assertThat(timestamp)
                .as("Дата и время должны быть равны %s", dateTime)
                .isEqualTo(dateTime);
    }

    /**
     * Проверяет, что значение LocalDateTime после или равно указанному моменту.
     *
     * @param dateTime опорное значение даты и времени
     * @return условие проверки, что дата после или равна опорному значению
     */
    public static LocalDateTimeCondition dateAfterOrEqual(LocalDateTime dateTime) {
        return timestamp -> Assertions.assertThat(timestamp)
                .as("Дата и время должны быть после или равны %s", dateTime)
                .isAfterOrEqualTo(dateTime);
    }

    /**
     * Проверяет, что значение LocalDateTime до или равно указанному моменту.
     *
     * @param dateTime опорное значение даты и времени
     * @return условие проверки, что дата до или равна опорному значению
     */
    public static LocalDateTimeCondition dateBeforeOrEqual(LocalDateTime dateTime) {
        return timestamp -> Assertions.assertThat(timestamp)
                .as("Дата и время должны быть до или равны %s", dateTime)
                .isBeforeOrEqualTo(dateTime);
    }

    /**
     * Проверяет, что значение LocalDateTime находится в диапазоне между start и end (включительно).
     *
     * @param start начало диапазона
     * @param end   конец диапазона
     * @return условие проверки, что дата находится между start и end
     */
    public static LocalDateTimeCondition isBetween(LocalDateTime start, LocalDateTime end) {
        return timestamp -> Assertions.assertThat(timestamp)
                .as("Дата и время должны быть между %s и %s", start, end)
                .isBetween(start, end);
    }

    /**
     * Проверяет, что разница между значением LocalDateTime и опорной датой не превышает заданный интервал.
     *
     * @param reference опорное значение даты и времени
     * @param tolerance максимально допустимая разница
     * @return условие проверки, что разница во времени не превышает tolerance
     */
    public static LocalDateTimeCondition within(LocalDateTime reference, Duration tolerance) {
        return timestamp -> {
            Duration diff = Duration.between(timestamp, reference).abs();
            Assertions.assertThat(diff)
                    .as("Разница между %s и %s должна быть не больше %s", timestamp, reference, tolerance)
                    .isLessThanOrEqualTo(tolerance);
        };
    }

    /**
     * Проверяет, что год значения LocalDateTime равен указанному.
     *
     * @param year ожидаемый год
     * @return условие проверки года
     */
    public static LocalDateTimeCondition hasYear(int year) {
        return timestamp -> Assertions.assertThat(timestamp.getYear())
                .as("Год должен быть %d", year)
                .isEqualTo(year);
    }

    /**
     * Проверяет, что месяц значения LocalDateTime равен указанному.
     *
     * @param month ожидаемый месяц (1-12)
     * @return условие проверки месяца
     */
    public static LocalDateTimeCondition hasMonth(int month) {
        return timestamp -> Assertions.assertThat(timestamp.getMonthValue())
                .as("Месяц должен быть %d", month)
                .isEqualTo(month);
    }

    /**
     * Проверяет, что месяц значения LocalDateTime равен указанному.
     *
     * @param month ожидаемый месяц
     * @return условие проверки месяца
     */
    public static LocalDateTimeCondition hasMonth(Month month) {
        return timestamp -> Assertions.assertThat(timestamp.getMonth())
                .as("Месяц должен быть %s", month)
                .isEqualTo(month);
    }

    /**
     * Проверяет, что день месяца значения LocalDateTime равен указанному.
     *
     * @param day ожидаемый день месяца
     * @return условие проверки дня месяца
     */
    public static LocalDateTimeCondition hasDayOfMonth(int day) {
        return timestamp -> Assertions.assertThat(timestamp.getDayOfMonth())
                .as("День месяца должен быть %d", day)
                .isEqualTo(day);
    }

    /**
     * Проверяет, что день года значения LocalDateTime равен указанному.
     *
     * @param dayOfYear ожидаемый день года
     * @return условие проверки дня года
     */
    public static LocalDateTimeCondition hasDayOfYear(int dayOfYear) {
        return timestamp -> Assertions.assertThat(timestamp.getDayOfYear())
                .as("День года должен быть %d", dayOfYear)
                .isEqualTo(dayOfYear);
    }

    /**
     * Проверяет, что час значения LocalDateTime равен указанному.
     *
     * @param hour ожидаемое значение часа (0-23)
     * @return условие проверки часа
     */
    public static LocalDateTimeCondition hasHour(int hour) {
        return timestamp -> Assertions.assertThat(timestamp.getHour())
                .as("Час должен быть %d", hour)
                .isEqualTo(hour);
    }

    /**
     * Проверяет, что минута значения LocalDateTime равна указанной.
     *
     * @param minute ожидаемое значение минуты (0-59)
     * @return условие проверки минуты
     */
    public static LocalDateTimeCondition hasMinute(int minute) {
        return timestamp -> Assertions.assertThat(timestamp.getMinute())
                .as("Минута должна быть %d", minute)
                .isEqualTo(minute);
    }

    /**
     * Проверяет, что секунда значения LocalDateTime равна указанной.
     *
     * @param second ожидаемое значение секунды (0-59)
     * @return условие проверки секунды
     */
    public static LocalDateTimeCondition hasSecond(int second) {
        return timestamp -> Assertions.assertThat(timestamp.getSecond())
                .as("Секунда должна быть %d", second)
                .isEqualTo(second);
    }

    /**
     * Проверяет, что значение LocalDateTime приходится на тот же календарный день, что и указанное.
     *
     * @param other другая дата для сравнения
     * @return условие проверки совпадения календарной даты
     */
    public static LocalDateTimeCondition isOnSameDayAs(LocalDateTime other) {
        return timestamp -> Assertions.assertThat(timestamp.toLocalDate())
                .as("Дата %s должна совпадать с %s", timestamp.toLocalDate(), other.toLocalDate())
                .isEqualTo(other.toLocalDate());
    }

    /**
     * Проверяет, что значение LocalDateTime приходится на выходной день (суббота или воскресенье).
     *
     * @return условие проверки, что дата является выходным днем
     */
    public static LocalDateTimeCondition isWeekend() {
        return timestamp -> {
            DayOfWeek day = timestamp.getDayOfWeek();
            Assertions.assertThat(day)
                    .as("День недели должен быть выходным (суббота или воскресенье), но был %s", day)
                    .isIn(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
        };
    }

    /**
     * Проверяет, что значение LocalDateTime приходится на рабочий день (с понедельника по пятницу).
     *
     * @return условие проверки, что дата является рабочим днем
     */
    public static LocalDateTimeCondition isWeekday() {
        return timestamp -> {
            DayOfWeek day = timestamp.getDayOfWeek();
            Assertions.assertThat(day)
                    .as("День недели должен быть рабочим (не суббота и не воскресенье), но был %s", day)
                    .isNotIn(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
        };
    }

    /**
     * Проверяет, что значение LocalDateTime равно ожидаемому с точностью до игнорирования наносекунд.
     *
     * @param expected ожидаемое значение даты и времени
     * @return условие проверки равенства с игнорированием наносекунд
     */
    public static LocalDateTimeCondition isEqualToIgnoringNanos(LocalDateTime expected) {
        return timestamp -> Assertions.assertThat(timestamp)
                .as("Дата и время должны быть равны %s с точностью до игнорирования наносекунд", expected)
                .isEqualToIgnoringNanos(expected);
    }

    /**
     * Проверяет, что день недели значения равен ожидаемому.
     *
     * @param expectedDay ожидаемый день недели
     * @return условие проверки дня недели
     */
    public static LocalDateTimeCondition hasDayOfWeek(DayOfWeek expectedDay) {
        return timestamp -> Assertions.assertThat(timestamp.getDayOfWeek())
                .as("День недели должен быть %s", expectedDay)
                .isEqualTo(expectedDay);
    }

    /**
     * Проверяет, что значение находится в том же месяце (и году), что и указанное.
     *
     * @param other опорное значение даты и времени
     * @return условие проверки совпадения месяца и года
     */
    public static LocalDateTimeCondition isInSameMonthAs(LocalDateTime other) {
        return timestamp -> {
            Assertions.assertThat(timestamp.getYear())
                    .as("Год должен совпадать с %d", other.getYear())
                    .isEqualTo(other.getYear());
            Assertions.assertThat(timestamp.getMonthValue())
                    .as("Месяц должен совпадать с %d", other.getMonthValue())
                    .isEqualTo(other.getMonthValue());
        };
    }

    /**
     * Проверяет, что значение находится в том же году, что и указанное.
     *
     * @param other опорное значение даты и времени
     * @return условие проверки совпадения года
     */
    public static LocalDateTimeCondition isInSameYearAs(LocalDateTime other) {
        return timestamp -> Assertions.assertThat(timestamp.getYear())
                .as("Год должен быть равен %d", other.getYear())
                .isEqualTo(other.getYear());
    }

    /**
     * Проверяет, что значение находится в той же неделе, что и указанное (с использованием ISO-недели).
     *
     * @param otherDateTime опорное значение даты и времени
     * @return условие проверки совпадения недели
     */
    public static LocalDateTimeCondition isInSameWeekAs(LocalDateTime otherDateTime) {
        return timestamp -> {
            WeekFields weekFields = WeekFields.ISO;
            int weekYearThis = timestamp.get(weekFields.weekBasedYear());
            int weekOfYearThis = timestamp.get(weekFields.weekOfWeekBasedYear());
            int weekYearOther = otherDateTime.get(weekFields.weekBasedYear());
            int weekOfYearOther = otherDateTime.get(weekFields.weekOfWeekBasedYear());
            Assertions.assertThat(weekYearThis)
                    .as("Год недели должен совпадать с %d", weekYearOther)
                    .isEqualTo(weekYearOther);
            Assertions.assertThat(weekOfYearThis)
                    .as("Номер недели должен быть равен %d", weekOfYearOther)
                    .isEqualTo(weekOfYearOther);
        };
    }

    /**
     * Проверяет, что разница между значением и опорной датой составляет не менее заданного интервала.
     *
     * @param reference         опорная дата и время
     * @param minimumDifference минимальное требуемое различие
     * @return условие проверки минимальной разницы во времени
     */
    public static LocalDateTimeCondition differsByAtLeast(LocalDateTime reference, Duration minimumDifference) {
        return timestamp -> {
            Duration diff = Duration.between(timestamp, reference).abs();
            Assertions.assertThat(diff)
                    .as("Разница между %s и %s должна быть не меньше %s", timestamp, reference, minimumDifference)
                    .isGreaterThanOrEqualTo(minimumDifference);
        };
    }

    /**
     * Проверяет, что значение находится как минимум на заданное время в будущем относительно настоящего момента.
     *
     * @param duration требуемый интервал в будущем
     * @return условие проверки, что значение находится в будущем с указанным отступом
     */
    public static LocalDateTimeCondition isFutureBy(Duration duration) {
        return timestamp -> {
            LocalDateTime threshold = LocalDateTime.now().plus(duration);
            Assertions.assertThat(timestamp)
                    .as("Дата и время должны быть после %s (с отступом %s)", threshold, duration)
                    .isAfter(threshold);
        };
    }

    /**
     * Проверяет, что значение находится как минимум на заданное время в прошлом относительно настоящего момента.
     *
     * @param duration требуемый интервал в прошлом
     * @return условие проверки, что значение находится в прошлом с указанным отступом
     */
    public static LocalDateTimeCondition isPastBy(Duration duration) {
        return timestamp -> {
            LocalDateTime threshold = LocalDateTime.now().minus(duration);
            Assertions.assertThat(timestamp)
                    .as("Дата и время должны быть до %s (с отступом %s)", threshold, duration)
                    .isBefore(threshold);
        };
    }

    /**
     * Проверяет, что значение находится в начале дня (время равно 00:00).
     *
     * @return условие проверки начала дня
     */
    public static LocalDateTimeCondition isAtStartOfDay() {
        return timestamp -> Assertions.assertThat(timestamp.toLocalTime())
                .as("Время должно быть %s (начало дня)", LocalTime.MIDNIGHT)
                .isEqualTo(LocalTime.MIDNIGHT);
    }

    /**
     * Проверяет, что значение находится в конце дня (время равно 23:59:59.999999999).
     *
     * @return условие проверки конца дня
     */
    public static LocalDateTimeCondition isAtEndOfDay() {
        return timestamp -> Assertions.assertThat(timestamp.toLocalTime())
                .as("Время должно быть %s (конец дня)", LocalTime.MAX)
                .isEqualTo(LocalTime.MAX);
    }

    /**
     * Проверяет, что значение находится в указанном квартале года (1–4).
     *
     * @param quarter ожидаемый квартал (от 1 до 4)
     * @return условие проверки квартала
     */
    public static LocalDateTimeCondition isInQuarter(int quarter) {
        return timestamp -> {
            if (quarter < 1 || quarter > 4) {
                throw new IllegalArgumentException("Квартал должен быть от 1 до 4");
            }
            int month = timestamp.getMonthValue();
            int actualQuarter = ((month - 1) / 3) + 1;
            Assertions.assertThat(actualQuarter)
                    .as("Ожидался квартал %d, а был %d", quarter, actualQuarter)
                    .isEqualTo(quarter);
        };
    }

    /**
     * Проверяет, что значение имеет те же часы и минуты, что и указанное.
     *
     * @param other опорное значение даты и времени
     * @return условие проверки совпадения часов и минут
     */
    public static LocalDateTimeCondition hasSameHourAndMinute(LocalDateTime other) {
        return timestamp -> {
            Assertions.assertThat(timestamp.getHour())
                    .as("Час должен быть равен %d", other.getHour())
                    .isEqualTo(other.getHour());
            Assertions.assertThat(timestamp.getMinute())
                    .as("Минута должна быть равна %d", other.getMinute())
                    .isEqualTo(other.getMinute());
        };
    }

    /**
     * Проверяет, что время (LocalTime, извлечённое из LocalDateTime) находится между заданными значениями.
     *
     * @param start начальное время
     * @param end   конечное время
     * @return условие проверки, что время находится в заданном диапазоне
     */
    public static LocalDateTimeCondition isBetweenTimeOfDay(LocalTime start, LocalTime end) {
        return timestamp -> {
            LocalTime time = timestamp.toLocalTime();
            Assertions.assertThat(time)
                    .as("Время должно быть не раньше %s", start)
                    .isAfterOrEqualTo(start);
            Assertions.assertThat(time)
                    .as("Время должно быть не позже %s", end)
                    .isBeforeOrEqualTo(end);
        };
    }

    /**
     * Проверяет, что наносекундная часть значения равна ожидаемому значению.
     *
     * @param nano ожидаемое значение наносекунд
     * @return условие проверки наносекунд
     */
    public static LocalDateTimeCondition hasNanoOfSecond(int nano) {
        return timestamp -> Assertions.assertThat(timestamp.getNano())
                .as("Наносекунды должны быть равны %d", nano)
                .isEqualTo(nano);
    }

    /**
     * Проверяет, что значение (с обрезкой секунд и ниже) находится позже, чем указанное значение.
     *
     * @param otherDateTime опорное значение даты и времени
     * @return условие проверки, что значение (без учета секунд) позже опорного
     */
    public static LocalDateTimeCondition isAfterIgnoringSeconds(LocalDateTime otherDateTime) {
        return timestamp -> {
            LocalDateTime truncatedTimestamp = timestamp.truncatedTo(ChronoUnit.MINUTES);
            LocalDateTime truncatedOther = otherDateTime.truncatedTo(ChronoUnit.MINUTES);
            Assertions.assertThat(truncatedTimestamp)
                    .as("Время (без секунд) должно быть после %s", truncatedOther)
                    .isAfter(truncatedOther);
        };
    }

    /**
     * Проверяет, что значение (с обрезкой секунд и ниже) находится раньше, чем указанное значение.
     *
     * @param otherDateTime опорное значение даты и времени
     * @return условие проверки, что значение (без учета секунд) раньше опорного
     */
    public static LocalDateTimeCondition isBeforeIgnoringSeconds(LocalDateTime otherDateTime) {
        return timestamp -> {
            LocalDateTime truncatedTimestamp = timestamp.truncatedTo(ChronoUnit.MINUTES);
            LocalDateTime truncatedOther = otherDateTime.truncatedTo(ChronoUnit.MINUTES);
            Assertions.assertThat(truncatedTimestamp)
                    .as("Время (без секунд) должно быть до %s", truncatedOther)
                    .isBefore(truncatedOther);
        };
    }

    /**
     * Проверяет, является ли год високосным.
     *
     * @return условие проверки на високосный год
     */
    public static LocalDateTimeCondition isLeapYear() {
        return timestamp -> Assertions.assertThat(timestamp.toLocalDate().isLeapYear())
                .as("Год должен быть високосным")
                .isTrue();
    }

    /**
     * Проверяет, что значение является первым днем месяца.
     *
     * @return условие проверки на первый день месяца
     */
    public static LocalDateTimeCondition isFirstDayOfMonth() {
        return timestamp -> Assertions.assertThat(timestamp.getDayOfMonth())
                .as("Дата должна быть первым днем месяца")
                .isEqualTo(1);
    }

    /**
     * Проверяет, что значение является последним днем месяца.
     *
     * @return условие проверки на последний день месяца
     */
    public static LocalDateTimeCondition isLastDayOfMonth() {
        return timestamp -> {
            int dayOfMonth = timestamp.getDayOfMonth();
            int lengthOfMonth = timestamp.toLocalDate().lengthOfMonth();
            Assertions.assertThat(dayOfMonth)
                    .as("Дата должна быть последним днем месяца (ожидалось %d)", lengthOfMonth)
                    .isEqualTo(lengthOfMonth);
        };
    }

    /**
     * Проверяет, что значение находится в первой половине месяца (1-15 числа).
     *
     * @return условие проверки на первую половину месяца
     */
    public static LocalDateTimeCondition isInFirstHalfOfMonth() {
        return timestamp -> Assertions.assertThat(timestamp.getDayOfMonth())
                .as("Дата должна быть в первой половине месяца (1-15)")
                .isBetween(1, 15);
    }

    /**
     * Проверяет, что значение находится во второй половине месяца (с 16 числа до конца месяца).
     *
     * @return условие проверки на вторую половину месяца
     */
    public static LocalDateTimeCondition isInSecondHalfOfMonth() {
        return timestamp -> Assertions.assertThat(timestamp.getDayOfMonth())
                .as("Дата должна быть во второй половине месяца (с 16 до конца)")
                .isGreaterThanOrEqualTo(16);
    }

    /**
     * Проверяет, что время находится утром (с 06:00 до 12:00, исключая 12:00).
     *
     * @return условие проверки на утро
     */
    public static LocalDateTimeCondition isMorning() {
        return timestamp -> {
            LocalTime time = timestamp.toLocalTime();
            Assertions.assertThat(time)
                    .as("Время должно быть утром (между 06:00 и 12:00)")
                    .isAfterOrEqualTo(LocalTime.of(6, 0))
                    .isBefore(LocalTime.of(12, 0));
        };
    }

    /**
     * Проверяет, что время находится днем (с 12:00 до 18:00, исключая 18:00).
     *
     * @return условие проверки на день
     */
    public static LocalDateTimeCondition isAfternoon() {
        return timestamp -> {
            LocalTime time = timestamp.toLocalTime();
            Assertions.assertThat(time)
                    .as("Время должно быть днем (между 12:00 и 18:00)")
                    .isAfterOrEqualTo(LocalTime.of(12, 0))
                    .isBefore(LocalTime.of(18, 0));
        };
    }

    /**
     * Проверяет, что время находится вечером (с 18:00 до 22:00, исключая 22:00).
     *
     * @return условие проверки на вечер
     */
    public static LocalDateTimeCondition isEvening() {
        return timestamp -> {
            LocalTime time = timestamp.toLocalTime();
            Assertions.assertThat(time)
                    .as("Время должно быть вечером (между 18:00 и 22:00)")
                    .isAfterOrEqualTo(LocalTime.of(18, 0))
                    .isBefore(LocalTime.of(22, 0));
        };
    }

    /**
     * Проверяет, что время находится ночью (с 22:00 до 06:00, исключая 06:00).
     *
     * @return условие проверки на ночь
     */
    public static LocalDateTimeCondition isNight() {
        return timestamp -> {
            LocalTime time = timestamp.toLocalTime();
            Assertions.assertThat(time)
                    .as("Время должно быть ночью (между 22:00 и 06:00)")
                    .isAfterOrEqualTo(LocalTime.of(22, 0))
                    .isBefore(LocalTime.of(6, 0));
        };
    }

    /**
     * Проверяет, что месяц имеет указанное количество дней.
     *
     * @param days ожидаемое количество дней в месяце
     * @return условие проверки количества дней в месяце
     */
    public static LocalDateTimeCondition hasDaysInMonth(int days) {
        return timestamp -> Assertions.assertThat(timestamp.toLocalDate().lengthOfMonth())
                .as("Месяц должен иметь %d дней", days)
                .isEqualTo(days);
    }

    /**
     * Проверяет, что значение находится в пределах указанного количества минут от другой даты.
     *
     * @param other   другая дата для сравнения
     * @param minutes количество минут
     * @return условие проверки нахождения в пределах минут
     */
    public static LocalDateTimeCondition isWithinMinutesOf(LocalDateTime other, long minutes) {
        return timestamp -> {
            Duration diff = Duration.between(timestamp, other).abs();
            Assertions.assertThat(diff)
                    .as("Разница между %s и %s должна быть не больше %d минут", timestamp, other, minutes)
                    .isLessThanOrEqualTo(Duration.ofMinutes(minutes));
        };
    }

    /**
     * Проверяет, что значение находится в пределах указанного количества часов от другой даты.
     *
     * @param other другая дата для сравнения
     * @param hours количество часов
     * @return условие проверки нахождения в пределах часов
     */
    public static LocalDateTimeCondition isWithinHoursOf(LocalDateTime other, long hours) {
        return timestamp -> {
            Duration diff = Duration.between(timestamp, other).abs();
            Assertions.assertThat(diff)
                    .as("Разница между %s и %s должна быть не больше %d часов", timestamp, other, hours)
                    .isLessThanOrEqualTo(Duration.ofHours(hours));
        };
    }

    /**
     * Проверяет, что значение находится в пределах указанного количества секунд от другой даты.
     *
     * @param other   другая дата для сравнения
     * @param seconds количество секунд
     * @return условие проверки нахождения в пределах секунд
     */
    public static LocalDateTimeCondition isWithinSecondsOf(LocalDateTime other, long seconds) {
        return timestamp -> {
            Duration diff = Duration.between(timestamp, other).abs();
            Assertions.assertThat(diff)
                    .as("Разница между %s и %s должна быть не больше %d секунд", timestamp, other, seconds)
                    .isLessThanOrEqualTo(Duration.ofSeconds(seconds));
        };
    }
}
