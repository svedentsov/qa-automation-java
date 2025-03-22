package kafka.matcher.assertions;

import kafka.matcher.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Set;

/**
 * Утилитный класс для создания условий проверки временных меток (Instant).
 */
@UtilityClass
public class TimeAssertions {

    /**
     * Функциональный интерфейс для условий проверки временной метки.
     */
    @FunctionalInterface
    public interface TimeCondition extends Condition<Instant> {
    }

    /**
     * Проверяет, что временная метка раньше указанного времени.
     *
     * @param time время для сравнения
     * @return условие "до указанного времени"
     */
    public static TimeCondition before(Instant time) {
        return actual -> Assertions.assertThat(actual)
                .as("Временная метка должна быть до %s", time)
                .isBefore(time);
    }

    /**
     * Проверяет, что временная метка позже указанного времени.
     *
     * @param time время для сравнения
     * @return условие "после указанного времени"
     */
    public static TimeCondition after(Instant time) {
        return actual -> Assertions.assertThat(actual)
                .as("Временная метка должна быть после %s", time)
                .isAfter(time);
    }

    /**
     * Проверяет, что временная метка находится в заданном диапазоне [start, end].
     *
     * @param start начало диапазона (включительно)
     * @param end   конец диапазона (включительно)
     * @return условие "в диапазоне"
     */
    public static TimeCondition inRange(Instant start, Instant end) {
        return actual -> Assertions.assertThat(actual)
                .as("Временная метка должна быть в диапазоне %s - %s", start, end)
                .isBetween(start, end);
    }

    /**
     * Проверяет, что временная метка равна указанной.
     *
     * @param time ожидаемая временная метка
     * @return условие равенства временной метки
     */
    public static TimeCondition equalsTo(Instant time) {
        return actual -> Assertions.assertThat(actual)
                .as("Временная метка должна быть равна %s", time)
                .isEqualTo(time);
    }

    /**
     * Проверяет, что временная метка не равна указанной.
     *
     * @param time временная метка для проверки неравенства
     * @return условие неравенства временной метки
     */
    public static TimeCondition notEqualsTo(Instant time) {
        return actual -> Assertions.assertThat(actual)
                .as("Временная метка не должна быть равна %s", time)
                .isNotEqualTo(time);
    }

    /**
     * Проверяет, что временная метка после или равна указанному времени.
     *
     * @param time время для сравнения
     */
    public static TimeCondition afterOrEqualTo(Instant time) {
        return actual -> Assertions.assertThat(actual)
                .as("Временная метка должна быть после или равна %s", time)
                .isAfterOrEqualTo(time);
    }

    /**
     * Проверяет, что временная метка до или равна указанному времени.
     *
     * @param time время для сравнения
     */
    public static TimeCondition beforeOrEqualTo(Instant time) {
        return actual -> Assertions.assertThat(actual)
                .as("Временная метка должна быть до или равна %s", time)
                .isBeforeOrEqualTo(time);
    }

    /**
     * Проверяет, что временная метка находится в прошлом.
     *
     * @return условие, что временная метка в прошлом
     */
    public static TimeCondition isInPast() {
        return actual -> Assertions.assertThat(actual)
                .as("Временная метка должна быть в прошлом")
                .isBefore(Instant.now());
    }

    /**
     * Проверяет, что временная метка находится в будущем.
     *
     * @return условие, что временная метка в будущем
     */
    public static TimeCondition isInFuture() {
        return actual -> Assertions.assertThat(actual)
                .as("Временная метка должна быть в будущем")
                .isAfter(Instant.now());
    }

    /**
     * Проверяет, что временная метка находится в пределах заданной временной дельты
     * относительно указанной точки во времени. Например, пригодно для проверки "около" (near) определенного момента.
     *
     * @param reference опорная временная метка
     * @param tolerance максимально допустимое отклонение
     */
    public static TimeCondition within(Instant reference, Duration tolerance) {
        return actual -> {
            Instant lowerBound = reference.minus(tolerance);
            Instant upperBound = reference.plus(tolerance);
            Assertions.assertThat(actual)
                    .as("Временная метка должна находиться в интервале [%s - %s] относительно опорной точки %s",
                            lowerBound, upperBound, reference)
                    .isBetween(lowerBound, upperBound);
        };
    }

    /**
     * Проверяет, что временная метка раньше текущего момента не более, чем на заданную длительность.
     *
     * @param maxAgo максимальная длительность "назад" от текущего момента
     */
    public static TimeCondition notOlderThan(Duration maxAgo) {
        return actual -> {
            Instant threshold = Instant.now().minus(maxAgo);
            Assertions.assertThat(actual)
                    .as("Временная метка должна быть не старше, чем %s назад от текущего момента", maxAgo)
                    .isAfterOrEqualTo(threshold);
        };
    }

    /**
     * Проверяет, что временная метка не позднее (не дальше в будущем), чем заданная длительность от текущего момента.
     *
     * @param maxAhead максимальная длительность "вперёд" от текущего момента
     */
    public static TimeCondition notFurtherThan(Duration maxAhead) {
        return actual -> {
            Instant threshold = Instant.now().plus(maxAhead);
            Assertions.assertThat(actual)
                    .as("Временная метка не должна быть дальше, чем %s вперёд от текущего момента", maxAhead)
                    .isBeforeOrEqualTo(threshold);
        };
    }

    /**
     * Проверяет, что временная метка приходится на ту же дату (год, месяц, день) что и указанная временная точка.
     *
     * @param instant ожидаемая дата (год, месяц, день)
     */
    public static TimeCondition sameDateAs(Instant instant) {
        return actual -> {
            LocalDate expectedDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate actualDate = actual.atZone(ZoneId.systemDefault()).toLocalDate();
            Assertions.assertThat(actualDate)
                    .as("Ожидалось, что дата %s совпадает с %s", actualDate, expectedDate)
                    .isEqualTo(expectedDate);
        };
    }

    /**
     * Проверяет, что временная метка относится к заданному году.
     *
     * @param year год, который ожидается (например, 2024)
     */
    public static TimeCondition inYear(int year) {
        return actual -> {
            int actualYear = actual.atZone(ZoneId.systemDefault()).getYear();
            Assertions.assertThat(actualYear)
                    .as("Ожидалось, что год временной метки %d равен %d", actualYear, year)
                    .isEqualTo(year);
        };
    }

    /**
     * Проверяет, что временная метка относится к указанному месяцу (1-12).
     *
     * @param month месяц (1 - январь, 12 - декабрь)
     */
    public static TimeCondition inMonth(int month) {
        return actual -> {
            int actualMonth = actual.atZone(ZoneId.systemDefault()).getMonthValue();
            Assertions.assertThat(actualMonth)
                    .as("Ожидалось, что месяц %d равен %d", actualMonth, month)
                    .isEqualTo(month);
        };
    }

    /**
     * Проверяет, что временная метка приходится на указанный день недели (например, Monday).
     *
     * @param dayOfWeek ожидаемый день недели
     */
    public static TimeCondition dayOfWeek(DayOfWeek dayOfWeek) {
        return actual -> {
            DayOfWeek actualDOW = actual.atZone(ZoneId.systemDefault()).getDayOfWeek();
            Assertions.assertThat(actualDOW)
                    .as("Ожидалось, что день недели %s равен %s", actualDOW, dayOfWeek)
                    .isEqualTo(dayOfWeek);
        };
    }

    /**
     * Проверяет, что временная метка приходится на один из заданных дней недели.
     *
     * @param daysOfWeek набор допустимых дней недели
     */
    public static TimeCondition inAnyOfDaysOfWeek(Set<DayOfWeek> daysOfWeek) {
        return actual -> {
            DayOfWeek actualDOW = actual.atZone(ZoneId.systemDefault()).getDayOfWeek();
            Assertions.assertThat(daysOfWeek)
                    .as("Ожидалось, что день недели %s входит в %s", actualDOW, daysOfWeek)
                    .contains(actualDOW);
        };
    }

    /**
     * Проверяет, что временная метка приходится на выходной день (суббота или воскресенье).
     */
    public static TimeCondition isWeekend() {
        return actual -> {
            DayOfWeek actualDOW = actual.atZone(ZoneId.systemDefault()).getDayOfWeek();
            boolean weekend = (actualDOW == DayOfWeek.SATURDAY || actualDOW == DayOfWeek.SUNDAY);
            Assertions.assertThat(weekend)
                    .as("Ожидалось, что день недели %s — выходной (суббота или воскресенье)", actualDOW)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что временная метка приходится на будний день (не выходной).
     */
    public static TimeCondition isWeekday() {
        return actual -> {
            DayOfWeek actualDOW = actual.atZone(ZoneId.systemDefault()).getDayOfWeek();
            boolean weekday = (actualDOW != DayOfWeek.SATURDAY && actualDOW != DayOfWeek.SUNDAY);
            Assertions.assertThat(weekday)
                    .as("Ожидалось, что день недели %s — рабочий (не суббота или воскресенье)", actualDOW)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что временная метка приходится на начало суток (00:00:00).
     * Учитывается системная временная зона.
     */
    public static TimeCondition isStartOfDay() {
        return actual -> {
            LocalDateTime ldt = LocalDateTime.ofInstant(actual, ZoneId.systemDefault());
            boolean isStart = ldt.getHour() == 0 && ldt.getMinute() == 0 && ldt.getSecond() == 0 && ldt.getNano() == 0;
            Assertions.assertThat(isStart)
                    .as("Ожидалось, что временная метка %s — начало суток (00:00:00)", ldt)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что временная метка приходится на конец суток (23:59:59.999999999).
     * Учитывается системная временная зона.
     * <p>
     * Обратите внимание, что в реальных системах конец суток может не всегда быть ровно 23:59:59.999999999
     * из-за возможных переходов на летнее/зимнее время, но в большинстве случаев достаточно этой проверки.
     */
    public static TimeCondition isEndOfDay() {
        return actual -> {
            LocalDateTime ldt = LocalDateTime.ofInstant(actual, ZoneId.systemDefault());
            boolean isEnd = (ldt.getHour() == 23
                    && ldt.getMinute() == 59
                    && ldt.getSecond() == 59
                    && ldt.getNano() == 999999999);
            Assertions.assertThat(isEnd)
                    .as("Ожидалось, что временная метка %s — конец суток (23:59:59.999999999)", ldt)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что между двумя временными метками прошло не более заданного количества секунд.
     *
     * @param reference      опорная временная метка
     * @param maxSecondsDiff максимально допустимая разница в секундах
     */
    public static TimeCondition withinSecondsOf(Instant reference, long maxSecondsDiff) {
        return actual -> {
            long diff = ChronoUnit.SECONDS.between(reference, actual);
            Assertions.assertThat(Math.abs(diff))
                    .as("Разница во времени должна быть не более %d секунд, но была %d", maxSecondsDiff, diff)
                    .isLessThanOrEqualTo(maxSecondsDiff);
        };
    }

    /**
     * Проверяет, что между двумя временными метками прошло не более заданного количества минут.
     *
     * @param reference      опорная временная метка
     * @param maxMinutesDiff максимально допустимая разница в минутах
     */
    public static TimeCondition withinMinutesOf(Instant reference, long maxMinutesDiff) {
        return actual -> {
            long diff = ChronoUnit.MINUTES.between(reference, actual);
            Assertions.assertThat(Math.abs(diff))
                    .as("Разница во времени должна быть не более %d минут, но была %d", maxMinutesDiff, diff)
                    .isLessThanOrEqualTo(maxMinutesDiff);
        };
    }

    /**
     * Проверяет, что между двумя временными метками прошло не более заданного количества часов.
     *
     * @param reference    опорная временная метка
     * @param maxHoursDiff максимально допустимая разница в часах
     */
    public static TimeCondition withinHoursOf(Instant reference, long maxHoursDiff) {
        return actual -> {
            long diff = ChronoUnit.HOURS.between(reference, actual);
            Assertions.assertThat(Math.abs(diff))
                    .as("Разница во времени должна быть не более %d часов, но была %d", maxHoursDiff, diff)
                    .isLessThanOrEqualTo(maxHoursDiff);
        };
    }

    /**
     * Проверяет, что между двумя временными метками прошло не более заданного количества дней.
     *
     * @param reference   опорная временная метка
     * @param maxDaysDiff максимально допустимая разница в днях
     */
    public static TimeCondition withinDaysOf(Instant reference, long maxDaysDiff) {
        return actual -> {
            long diff = ChronoUnit.DAYS.between(reference, actual);
            Assertions.assertThat(Math.abs(diff))
                    .as("Разница во времени должна быть не более %d дней, но была %d", maxDaysDiff, diff)
                    .isLessThanOrEqualTo(maxDaysDiff);
        };
    }

    /**
     * Проверяет, что временная метка имеет поле (час, минута, секунда и т.д.) равное заданному значению.
     * Например, позволяет проверить конкретный час суток: checkField(ChronoField.HOUR_OF_DAY, 14).
     *
     * @param field поле времени (например, {@code ChronoField.HOUR_OF_DAY})
     * @param value ожидаемое значение поля
     */
    public static TimeCondition checkField(ChronoField field, int value) {
        return actual -> {
            ZonedDateTime zdt = actual.atZone(ZoneId.systemDefault());
            int actualValue = zdt.get(field);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что поле %s временной метки будет %d, но было %d", field, value, actualValue)
                    .isEqualTo(value);
        };
    }
}
