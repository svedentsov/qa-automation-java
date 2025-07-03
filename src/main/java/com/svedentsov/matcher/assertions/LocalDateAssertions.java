package com.svedentsov.matcher.assertions;

import com.svedentsov.matcher.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * Утилитный класс для проверки свойств даты (LocalDate) сущности.
 * Создан по аналогии с {@link LocalDateTimeAssertions}, но значительно расширен
 * специфичными для {@link LocalDate} проверками.
 */
@UtilityClass
public class LocalDateAssertions {

    /**
     * Функциональный интерфейс для проверки значений типа LocalDate.
     */
    @FunctionalInterface
    public interface LocalDateCondition extends Condition<LocalDate> {
    }

    // --- Базовые сравнения ---

    /**
     * Значение LocalDate раньше (до) указанной даты.
     *
     * @param date дата, до которой должно быть значение
     * @return условие проверки, что дата раньше указанной
     */
    public static LocalDateCondition dateBefore(LocalDate date) {
        return value -> Assertions.assertThat(value)
                .as("Дата %s должна быть до %s", value, date)
                .isBefore(date);
    }

    /**
     * Значение LocalDate строго позже указанной даты.
     *
     * @param date дата, после которой должно быть значение
     * @return условие проверки, что дата позже указанной
     */
    public static LocalDateCondition dateAfter(LocalDate date) {
        return value -> Assertions.assertThat(value)
                .as("Дата %s должна быть после %s", value, date)
                .isAfter(date);
    }

    /**
     * Значение LocalDate находится в будущем (после сегодняшней даты).
     *
     * @return условие проверки, что дата находится в будущем
     */
    public static LocalDateCondition dateIsInFuture() {
        return value -> Assertions.assertThat(value)
                .as("Дата %s должна быть в будущем", value)
                .isAfter(LocalDate.now());
    }

    /**
     * Значение LocalDate находится в прошлом (до сегодняшней даты).
     *
     * @return условие проверки, что дата находится в прошлом
     */
    public static LocalDateCondition dateIsInPast() {
        return value -> Assertions.assertThat(value)
                .as("Дата %s должна быть в прошлом", value)
                .isBefore(LocalDate.now());
    }

    /**
     * Значение LocalDate точно равно указанному.
     *
     * @param date ожидаемое значение даты
     * @return условие проверки равенства
     */
    public static LocalDateCondition dateEquals(LocalDate date) {
        return value -> Assertions.assertThat(value)
                .as("Дата %s должна быть равна %s", value, date)
                .isEqualTo(date);
    }

    /**
     * Значение LocalDate является сегодняшней датой.
     *
     * @return условие проверки, что дата является сегодняшней
     */
    public static LocalDateCondition dateIsToday() {
        LocalDate today = LocalDate.now();
        return value -> Assertions.assertThat(value)
                .as("Дата %s должна быть сегодняшней (%s)", value, today)
                .isEqualTo(today);
    }

    /**
     * Значение LocalDate является вчерашней датой.
     *
     * @return условие проверки, что дата является вчерашней
     */
    public static LocalDateCondition dateIsYesterday() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return value -> Assertions.assertThat(value)
                .as("Дата %s должна быть вчерашней (%s)", value, yesterday)
                .isEqualTo(yesterday);
    }

    /**
     * Значение LocalDate является завтрашней датой.
     *
     * @return условие проверки, что дата является завтрашней
     */
    public static LocalDateCondition dateIsTomorrow() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return value -> Assertions.assertThat(value)
                .as("Дата %s должна быть завтрашней (%s)", value, tomorrow)
                .isEqualTo(tomorrow);
    }

    /**
     * Значение LocalDate после или равно указанной дате.
     *
     * @param date опорное значение даты
     * @return условие проверки, что дата после или равна опорному значению
     */
    public static LocalDateCondition dateAfterOrEqual(LocalDate date) {
        return value -> Assertions.assertThat(value)
                .as("Дата %s должна быть после или равна %s", value, date)
                .isAfterOrEqualTo(date);
    }

    /**
     * Значение LocalDate до или равно указанной дате.
     *
     * @param date опорное значение даты
     * @return условие проверки, что дата до или равна опорному значению
     */
    public static LocalDateCondition dateBeforeOrEqual(LocalDate date) {
        return value -> Assertions.assertThat(value)
                .as("Дата %s должна быть до или равна %s", value, date)
                .isBeforeOrEqualTo(date);
    }

    /**
     * Значение LocalDate находится в диапазоне между start и end (включительно).
     *
     * @param start начало диапазона
     * @param end   конец диапазона
     * @return условие проверки, что дата находится между start и end
     */
    public static LocalDateCondition dateIsBetween(LocalDate start, LocalDate end) {
        return value -> Assertions.assertThat(value)
                .as("Дата %s должна быть между %s и %s", value, start, end)
                .isBetween(start, end);
    }

    // --- Проверки по частям даты ---

    /**
     * Год значения LocalDate равен указанному.
     *
     * @param year ожидаемый год
     * @return условие проверки года
     */
    public static LocalDateCondition dateHasYear(int year) {
        return value -> Assertions.assertThat(value.getYear())
                .as("Год даты %s должен быть %d", value, year)
                .isEqualTo(year);
    }

    /**
     * Месяц значения LocalDate равен указанному.
     *
     * @param month ожидаемый месяц (1-12)
     * @return условие проверки месяца
     */
    public static LocalDateCondition dateHasMonth(int month) {
        return value -> Assertions.assertThat(value.getMonthValue())
                .as("Месяц даты %s должен быть %d", value, month)
                .isEqualTo(month);
    }

    /**
     * Месяц значения LocalDate равен указанному.
     *
     * @param month ожидаемый месяц
     * @return условие проверки месяца
     */
    public static LocalDateCondition dateHasMonth(Month month) {
        return value -> Assertions.assertThat(value.getMonth())
                .as("Месяц даты %s должен быть %s", value, month)
                .isEqualTo(month);
    }

    /**
     * День месяца значения LocalDate равен указанному.
     *
     * @param day ожидаемый день месяца
     * @return условие проверки дня месяца
     */
    public static LocalDateCondition dateHasDayOfMonth(int day) {
        return value -> Assertions.assertThat(value.getDayOfMonth())
                .as("День месяца даты %s должен быть %d", value, day)
                .isEqualTo(day);
    }

    /**
     * День года значения LocalDate равен указанному.
     *
     * @param dayOfYear ожидаемый день года
     * @return условие проверки дня года
     */
    public static LocalDateCondition dateHasDayOfYear(int dayOfYear) {
        return value -> Assertions.assertThat(value.getDayOfYear())
                .as("День года даты %s должен быть %d", value, dayOfYear)
                .isEqualTo(dayOfYear);
    }

    /**
     * День недели значения равен ожидаемому.
     *
     * @param expectedDay ожидаемый день недели
     * @return условие проверки дня недели
     */
    public static LocalDateCondition dateHasDayOfWeek(DayOfWeek expectedDay) {
        return value -> Assertions.assertThat(value.getDayOfWeek())
                .as("День недели даты %s должен быть %s", value, expectedDay)
                .isEqualTo(expectedDay);
    }

    // --- Календарные свойства ---

    /**
     * Год является високосным.
     *
     * @return условие проверки на високосный год
     */
    public static LocalDateCondition dateIsLeapYear() {
        return value -> Assertions.assertThat(value.isLeapYear())
                .as("Год %d даты %s должен быть високосным", value.getYear(), value)
                .isTrue();
    }

    /**
     * Год не является високосным.
     *
     * @return условие проверки на невисокосный год
     */
    public static LocalDateCondition dateIsNotLeapYear() {
        return value -> Assertions.assertThat(value.isLeapYear())
                .as("Год %d даты %s не должен быть високосным", value.getYear(), value)
                .isFalse();
    }

    /**
     * Значение LocalDate приходится на выходной день (суббота или воскресенье).
     *
     * @return условие проверки, что дата является выходным днем
     */
    public static LocalDateCondition dateIsWeekend() {
        return value -> {
            DayOfWeek day = value.getDayOfWeek();
            Assertions.assertThat(day)
                    .as("День недели %s для даты %s должен быть выходным (Сб или Вс)", day, value)
                    .isIn(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
        };
    }

    /**
     * Значение LocalDate приходится на рабочий день (с понедельника по пятницу).
     *
     * @return условие проверки, что дата является рабочим днем
     */
    public static LocalDateCondition dateIsWeekday() {
        return value -> {
            DayOfWeek day = value.getDayOfWeek();
            Assertions.assertThat(day)
                    .as("День недели %s для даты %s должен быть рабочим (Пн-Пт)", day, value)
                    .isNotIn(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
        };
    }

    // --- Сравнения по периодам и частям ---

    /**
     * Разница между значением LocalDate и опорной датой не превышает заданный период.
     *
     * @param reference опорное значение даты
     * @param tolerance максимально допустимая разница в виде {@link Period}
     * @return условие проверки, что разница в днях не превышает tolerance
     */
    public static LocalDateCondition dateWithin(LocalDate reference, Period tolerance) {
        return value -> {
            // Period не имеет метода abs(), поэтому считаем разницу в днях
            long daysBetween = Math.abs(ChronoUnit.DAYS.between(value, reference));
            long toleranceInDays = Math.abs(tolerance.toTotalMonths() * 30 + tolerance.getDays()); // Приблизительно
            Assertions.assertThat(daysBetween)
                    .as("Разница между %s и %s должна быть не больше %s (примерно %d дней)", value, reference, tolerance, toleranceInDays)
                    .isLessThanOrEqualTo(toleranceInDays);
        };
    }

    /**
     * Разница между значением и опорной датой составляет не менее заданного периода.
     *
     * @param reference         опорная дата
     * @param minimumDifference минимальное требуемое различие в виде {@link Period}
     * @return условие проверки минимальной разницы во времени
     */
    public static LocalDateCondition dateDiffersByAtLeast(LocalDate reference, Period minimumDifference) {
        return value -> {
            long daysBetween = Math.abs(ChronoUnit.DAYS.between(value, reference));
            long minDiffInDays = Math.abs(minimumDifference.toTotalMonths() * 30 + minimumDifference.getDays()); // Приблизительно
            Assertions.assertThat(daysBetween)
                    .as("Разница между %s и %s должна быть не меньше %s (примерно %d дней)", value, reference, minimumDifference, minDiffInDays)
                    .isGreaterThanOrEqualTo(minDiffInDays);
        };
    }

    /**
     * Значение находится в том же месяце (и году), что и указанное.
     *
     * @param other опорное значение даты
     * @return условие проверки совпадения месяца и года
     */
    public static LocalDateCondition dateIsInSameMonthAs(LocalDate other) {
        return value -> Assertions.assertThat(value)
                .as("Дата %s должна быть в том же месяце, что и %s", value, other)
                .hasYear(other.getYear())
                .hasMonth(other.getMonth());
    }

    /**
     * Значение находится в том же году, что и указанное.
     *
     * @param other опорное значение даты
     * @return условие проверки совпадения года
     */
    public static LocalDateCondition dateIsInSameYearAs(LocalDate other) {
        return value -> Assertions.assertThat(value.getYear())
                .as("Год даты %s должен совпадать с годом даты %s (%d)", value, other, other.getYear())
                .isEqualTo(other.getYear());
    }

    /**
     * Значение находится в той же неделе, что и указанное (с использованием ISO-недели).
     *
     * @param otherDate опорное значение даты
     * @return условие проверки совпадения недели
     */
    public static LocalDateCondition dateIsInSameWeekAs(LocalDate otherDate) {
        return dateIsInSameWeekAs(otherDate, Locale.getDefault());
    }

    /**
     * Значение находится в той же неделе, что и указанное, с учетом локали.
     *
     * @param otherDate опорное значение даты
     * @param locale    локаль для определения правил недели
     * @return условие проверки совпадения недели
     */
    public static LocalDateCondition dateIsInSameWeekAs(LocalDate otherDate, Locale locale) {
        return value -> {
            WeekFields weekFields = WeekFields.of(locale);
            int weekYearThis = value.get(weekFields.weekBasedYear());
            int weekOfYearThis = value.get(weekFields.weekOfWeekBasedYear());
            int weekYearOther = otherDate.get(weekFields.weekBasedYear());
            int weekOfYearOther = otherDate.get(weekFields.weekOfWeekBasedYear());

            Assertions.assertThat(weekYearThis)
                    .as("Год недели для даты %s должен совпадать с %d", value, weekYearOther)
                    .isEqualTo(weekYearOther);
            Assertions.assertThat(weekOfYearThis)
                    .as("Номер недели для даты %s должен быть равен %d", value, weekOfYearOther)
                    .isEqualTo(weekOfYearOther);
        };
    }

    // --- Проверки по границам периодов ---

    /**
     * Значение является первым днем месяца.
     *
     * @return условие проверки на первый день месяца
     */
    public static LocalDateCondition dateIsFirstDayOfMonth() {
        return value -> Assertions.assertThat(value.getDayOfMonth())
                .as("Дата %s должна быть первым днем месяца", value)
                .isEqualTo(1);
    }

    /**
     * Значение является последним днем месяца.
     *
     * @return условие проверки на последний день месяца
     */
    public static LocalDateCondition dateIsLastDayOfMonth() {
        return value -> {
            int dayOfMonth = value.getDayOfMonth();
            int lengthOfMonth = value.lengthOfMonth();
            Assertions.assertThat(dayOfMonth)
                    .as("Дата %s должна быть последним днем месяца (ожидалось %d)", value, lengthOfMonth)
                    .isEqualTo(lengthOfMonth);
        };
    }

    /**
     * Значение является первым днем года.
     *
     * @return условие проверки на первый день года
     */
    public static LocalDateCondition dateIsFirstDayOfYear() {
        return value -> Assertions.assertThat(value.getDayOfYear())
                .as("Дата %s должна быть первым днем года", value)
                .isEqualTo(1);
    }

    /**
     * Значение является последним днем года.
     *
     * @return условие проверки на последний день года
     */
    public static LocalDateCondition dateIsLastDayOfYear() {
        return value -> {
            int dayOfYear = value.getDayOfYear();
            int lengthOfYear = value.lengthOfYear();
            Assertions.assertThat(dayOfYear)
                    .as("Дата %s должна быть последним днем года (ожидалось %d)", value, lengthOfYear)
                    .isEqualTo(lengthOfYear);
        };
    }

    /**
     * Значение является первой датой указанного дня недели в месяце (напр., первый вторник).
     *
     * @param dayOfWeek искомый день недели
     * @return условие проверки
     */
    public static LocalDateCondition dateIsFirstInMonth(DayOfWeek dayOfWeek) {
        return value -> {
            LocalDate firstInMonth = value.with(TemporalAdjusters.firstInMonth(dayOfWeek));
            Assertions.assertThat(value)
                    .as("Дата %s должна быть первым %s в месяце (ожидалось %s)", value, dayOfWeek, firstInMonth)
                    .isEqualTo(firstInMonth);
        };
    }

    /**
     * Значение является последней датой указанного дня недели в месяце (напр., последний понедельник).
     *
     * @param dayOfWeek искомый день недели
     * @return условие проверки
     */
    public static LocalDateCondition dateIsLastInMonth(DayOfWeek dayOfWeek) {
        return value -> {
            LocalDate lastInMonth = value.with(TemporalAdjusters.lastInMonth(dayOfWeek));
            Assertions.assertThat(value)
                    .as("Дата %s должна быть последним %s в месяце (ожидалось %s)", value, dayOfWeek, lastInMonth)
                    .isEqualTo(lastInMonth);
        };
    }

    // --- Кварталы и половины месяца ---

    /**
     * Значение находится в указанном квартале года (1–4).
     *
     * @param quarter ожидаемый квартал (от 1 до 4)
     * @return условие проверки квартала
     */
    public static LocalDateCondition dateIsInQuarter(int quarter) {
        return value -> {
            if (quarter < 1 || quarter > 4) {
                throw new IllegalArgumentException("Квартал должен быть от 1 до 4");
            }
            int month = value.getMonthValue();
            int actualQuarter = (month + 2) / 3;
            Assertions.assertThat(actualQuarter)
                    .as("Дата %s (месяц %d) должна быть в квартале %d, а по факту в %d", value, month, quarter, actualQuarter)
                    .isEqualTo(quarter);
        };
    }

    /**
     * Значение находится в первой половине месяца (1-15 числа).
     *
     * @return условие проверки на первую половину месяца
     */
    public static LocalDateCondition dateIsInFirstHalfOfMonth() {
        return value -> Assertions.assertThat(value.getDayOfMonth())
                .as("Дата %s должна быть в первой половине месяца (1-15)", value)
                .isBetween(1, 15);
    }

    /**
     * Значение находится во второй половине месяца (с 16 числа до конца месяца).
     *
     * @return условие проверки на вторую половину месяца
     */
    public static LocalDateCondition dateIsInSecondHalfOfMonth() {
        return value -> Assertions.assertThat(value.getDayOfMonth())
                .as("Дата %s должна быть во второй половине месяца (с 16 до конца)", value)
                .isGreaterThanOrEqualTo(16);
    }

    // --- Дополнительные продвинутые проверки ---

    /**
     * Месяц имеет указанное количество дней. Полезно для проверки високосных годов.
     *
     * @param days ожидаемое количество дней в месяце
     * @return условие проверки количества дней в месяце
     */
    public static LocalDateCondition dateHasDaysInMonth(int days) {
        return value -> Assertions.assertThat(value.lengthOfMonth())
                .as("Месяц в дате %s должен иметь %d дней", value, days)
                .isEqualTo(days);
    }

    /**
     * Проверяет, что дата соответствует строковому представлению в заданном формате.
     *
     * @param pattern  шаблон формата (например, "dd.MM.yyyy")
     * @param expected ожидаемая строка
     * @return условие проверки
     */
    public static LocalDateCondition dateMatchesPattern(String pattern, String expected) {
        return value -> {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern(pattern);
            Assertions.assertThat(value)
                    .as("Дата %s, отформатированная как '%s', должна быть равна '%s'", value, pattern, expected)
                    .matches(d -> d.format(formatter).equals(expected));
        };
    }
}
