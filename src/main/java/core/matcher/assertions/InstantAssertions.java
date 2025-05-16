package core.matcher.assertions;

import core.matcher.Condition;
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
public class InstantAssertions {

    /**
     * Функциональный интерфейс для условий проверки временной метки.
     */
    @FunctionalInterface
    public interface InstantCondition extends Condition<Instant> {
    }

    /**
     * Проверяет, что временная метка раньше указанного времени.
     *
     * @param time время для сравнения
     * @return условие "до указанного времени"
     */
    public static InstantCondition before(Instant time) {
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
    public static InstantCondition after(Instant time) {
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
    public static InstantCondition inRange(Instant start, Instant end) {
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
    public static InstantCondition equalsTo(Instant time) {
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
    public static InstantCondition notEqualsTo(Instant time) {
        return actual -> Assertions.assertThat(actual)
                .as("Временная метка не должна быть равна %s", time)
                .isNotEqualTo(time);
    }

    /**
     * Проверяет, что временная метка после или равна указанному времени.
     *
     * @param time время для сравнения
     */
    public static InstantCondition afterOrEqualTo(Instant time) {
        return actual -> Assertions.assertThat(actual)
                .as("Временная метка должна быть после или равна %s", time)
                .isAfterOrEqualTo(time);
    }

    /**
     * Проверяет, что временная метка до или равна указанному времени.
     *
     * @param time время для сравнения
     */
    public static InstantCondition beforeOrEqualTo(Instant time) {
        return actual -> Assertions.assertThat(actual)
                .as("Временная метка должна быть до или равна %s", time)
                .isBeforeOrEqualTo(time);
    }

    /**
     * Проверяет, что временная метка находится в прошлом.
     *
     * @return условие, что временная метка в прошлом
     */
    public static InstantCondition isInPast() {
        return actual -> Assertions.assertThat(actual)
                .as("Временная метка должна быть в прошлом")
                .isBefore(Instant.now());
    }

    /**
     * Проверяет, что временная метка находится в будущем.
     *
     * @return условие, что временная метка в будущем
     */
    public static InstantCondition isInFuture() {
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
    public static InstantCondition within(Instant reference, Duration tolerance) {
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
     * Проверяет, что временная метка находится в пределах заданной временной дельты
     * относительно указанной точки во времени. Например, пригодно для проверки "около" (near) определенного момента.
     *
     * @param reference опорная временная метка
     * @param value     максимально допустимое отклонение
     * @param unit      единица измерения отклонения
     */
    public static InstantCondition within(Instant reference, long value, ChronoUnit unit) {
        return actual -> {
            Instant lowerBound = reference.minus(value, unit);
            Instant upperBound = reference.plus(value, unit);
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
    public static InstantCondition notOlderThan(Duration maxAgo) {
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
    public static InstantCondition notFurtherThan(Duration maxAhead) {
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
    public static InstantCondition sameDateAs(Instant instant) {
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
    public static InstantCondition inYear(int year) {
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
    public static InstantCondition inMonth(int month) {
        return actual -> {
            int actualMonth = actual.atZone(ZoneId.systemDefault()).getMonthValue();
            Assertions.assertThat(actualMonth)
                    .as("Ожидалось, что месяц %d равен %d", actualMonth, month)
                    .isEqualTo(month);
        };
    }

    /**
     * Проверяет, что временная метка относится к указанному месяцу.
     *
     * @param month ожидаемый месяц
     */
    public static InstantCondition inMonth(Month month) {
        return actual -> {
            Month actualMonth = actual.atZone(ZoneId.systemDefault()).getMonth();
            Assertions.assertThat(actualMonth)
                    .as("Ожидалось, что месяц %s равен %s", actualMonth, month)
                    .isEqualTo(month);
        };
    }

    /**
     * Проверяет, что временная метка приходится на указанный день недели (например, Monday).
     *
     * @param dayOfWeek ожидаемый день недели
     */
    public static InstantCondition dayOfWeek(DayOfWeek dayOfWeek) {
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
    public static InstantCondition inAnyOfDaysOfWeek(Set<DayOfWeek> daysOfWeek) {
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
    public static InstantCondition isWeekend() {
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
    public static InstantCondition isWeekday() {
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
    public static InstantCondition isStartOfDay() {
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
    public static InstantCondition isEndOfDay() {
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
     * Проверяет, что временная метка приходится на начало часа (минуты, секунды и наносекунды равны нулю).
     */
    public static InstantCondition isStartOfHour() {
        return actual -> {
            LocalDateTime ldt = LocalDateTime.ofInstant(actual, ZoneId.systemDefault());
            boolean isStart = ldt.getMinute() == 0 && ldt.getSecond() == 0 && ldt.getNano() == 0;
            Assertions.assertThat(isStart)
                    .as("Ожидалось, что временная метка %s — начало часа (минуты, секунды и наносекунды равны нулю)", ldt)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что временная метка приходится на конец часа (минуты и секунды равны 59, наносекунды 999999999).
     */
    public static InstantCondition isEndOfHour() {
        return actual -> {
            LocalDateTime ldt = LocalDateTime.ofInstant(actual, ZoneId.systemDefault());
            boolean isEnd = ldt.getMinute() == 59 && ldt.getSecond() == 59 && ldt.getNano() == 999999999;
            Assertions.assertThat(isEnd)
                    .as("Ожидалось, что временная метка %s — конец часа (минуты и секунды равны 59, наносекунды 999999999)", ldt)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что временная метка приходится на начало минуты (секунды и наносекунды равны нулю).
     */
    public static InstantCondition isStartOfMinute() {
        return actual -> {
            LocalDateTime ldt = LocalDateTime.ofInstant(actual, ZoneId.systemDefault());
            boolean isStart = ldt.getSecond() == 0 && ldt.getNano() == 0;
            Assertions.assertThat(isStart)
                    .as("Ожидалось, что временная метка %s — начало минуты (секунды и наносекунды равны нулю)", ldt)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что временная метка приходится на конец минуты (секунда равна 59, наносекунды 999999999).
     */
    public static InstantCondition isEndOfMinute() {
        return actual -> {
            LocalDateTime ldt = LocalDateTime.ofInstant(actual, ZoneId.systemDefault());
            boolean isEnd = ldt.getSecond() == 59 && ldt.getNano() == 999999999;
            Assertions.assertThat(isEnd)
                    .as("Ожидалось, что временная метка %s — конец минуты (секунда равна 59, наносекунды 999999999)", ldt)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что временная метка приходится на начало секунды (наносекунды равны нулю).
     */
    public static InstantCondition isStartOfSecond() {
        return actual -> {
            LocalDateTime ldt = LocalDateTime.ofInstant(actual, ZoneId.systemDefault());
            boolean isStart = ldt.getNano() == 0;
            Assertions.assertThat(isStart)
                    .as("Ожидалось, что временная метка %s — начало секунды (наносекунды равны нулю)", ldt)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что временная метка приходится на конец секунды (наносекунды равны 999999999).
     */
    public static InstantCondition isEndOfSecond() {
        return actual -> {
            LocalDateTime ldt = LocalDateTime.ofInstant(actual, ZoneId.systemDefault());
            boolean isEnd = ldt.getNano() == 999999999;
            Assertions.assertThat(isEnd)
                    .as("Ожидалось, что временная метка %s — конец секунды (наносекунды равны 999999999)", ldt)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что между двумя временными метками прошло не более заданного количества секунд.
     *
     * @param reference      опорная временная метка
     * @param maxSecondsDiff максимально допустимая разница в секундах
     */
    public static InstantCondition withinSecondsOf(Instant reference, long maxSecondsDiff) {
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
    public static InstantCondition withinMinutesOf(Instant reference, long maxMinutesDiff) {
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
    public static InstantCondition withinHoursOf(Instant reference, long maxHoursDiff) {
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
    public static InstantCondition withinDaysOf(Instant reference, long maxDaysDiff) {
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
    public static InstantCondition checkField(ChronoField field, int value) {
        return actual -> {
            ZonedDateTime zdt = actual.atZone(ZoneId.systemDefault());
            int actualValue = zdt.get(field);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что поле %s временной метки будет %d, но было %d", field, value, actualValue)
                    .isEqualTo(value);
        };
    }

    /**
     * Проверяет, что временная метка имеет поле (час, минута, секунда и т.д.) больше заданного значения.
     * Например, позволяет проверить, что час больше определенного значения: isFieldGreaterThan(ChronoField.HOUR_OF_DAY, 10).
     *
     * @param field поле времени (например, {@code ChronoField.HOUR_OF_DAY})
     * @param value значение, с которым сравнивается поле
     */
    public static InstantCondition isFieldGreaterThan(ChronoField field, int value) {
        return actual -> {
            ZonedDateTime zdt = actual.atZone(ZoneId.systemDefault());
            int actualValue = zdt.get(field);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что поле %s временной метки будет больше %d, но было %d", field, value, actualValue)
                    .isGreaterThan(value);
        };
    }

    /**
     * Проверяет, что временная метка имеет поле (час, минута, секунда и т.д.) меньше заданного значения.
     * Например, позволяет проверить, что минута меньше определенного значения: isFieldLessThan(ChronoField.MINUTE_OF_HOUR, 30).
     *
     * @param field поле времени (например, {@code ChronoField.MINUTE_OF_HOUR})
     * @param value значение, с которым сравнивается поле
     */
    public static InstantCondition isFieldLessThan(ChronoField field, int value) {
        return actual -> {
            ZonedDateTime zdt = actual.atZone(ZoneId.systemDefault());
            int actualValue = zdt.get(field);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что поле %s временной метки будет меньше %d, но было %d", field, value, actualValue)
                    .isLessThan(value);
        };
    }

    /**
     * Проверяет, что временная метка имеет поле (час, минута, секунда и т.д.) больше или равно заданному значению.
     * Например, позволяет проверить, что секунда больше или равна нулю: isFieldGreaterOrEqual(ChronoField.SECOND_OF_MINUTE, 0).
     *
     * @param field поле времени (например, {@code ChronoField.SECOND_OF_MINUTE})
     * @param value значение, с которым сравнивается поле
     */
    public static InstantCondition isFieldGreaterOrEqual(ChronoField field, int value) {
        return actual -> {
            ZonedDateTime zdt = actual.atZone(ZoneId.systemDefault());
            int actualValue = zdt.get(field);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что поле %s временной метки будет больше или равно %d, но было %d", field, value, actualValue)
                    .isGreaterThanOrEqualTo(value);
        };
    }

    /**
     * Проверяет, что временная метка имеет поле (час, минута, секунда и т.д.) меньше или равно заданному значению.
     * Например, позволяет проверить, что час меньше или равен 23: isFieldLessOrEqual(ChronoField.HOUR_OF_DAY, 23).
     *
     * @param field поле времени (например, {@code ChronoField.HOUR_OF_DAY})
     * @param value значение, с которым сравнивается поле
     */
    public static InstantCondition isFieldLessOrEqual(ChronoField field, int value) {
        return actual -> {
            ZonedDateTime zdt = actual.atZone(ZoneId.systemDefault());
            int actualValue = zdt.get(field);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что поле %s временной метки будет меньше или равно %d, но было %d", field, value, actualValue)
                    .isLessThanOrEqualTo(value);
        };
    }

    /**
     * Проверяет, что временная метка приходится на определенный час суток (0-23).
     *
     * @param hour ожидаемый час (0-23)
     */
    public static InstantCondition inHour(int hour) {
        return checkField(ChronoField.HOUR_OF_DAY, hour);
    }

    /**
     * Проверяет, что временная метка приходится на определенную минуту часа (0-59).
     *
     * @param minute ожидаемая минута (0-59)
     */
    public static InstantCondition inMinute(int minute) {
        return checkField(ChronoField.MINUTE_OF_HOUR, minute);
    }

    /**
     * Проверяет, что временная метка приходится на определенную секунду минуты (0-59).
     *
     * @param second ожидаемая секунда (0-59)
     */
    public static InstantCondition inSecond(int second) {
        return checkField(ChronoField.SECOND_OF_MINUTE, second);
    }

    /**
     * Проверяет, что временная метка приходится на определенную миллисекунду секунды (0-999).
     *
     * @param milli ожидаемая миллисекунда (0-999)
     */
    public static InstantCondition inMillisecond(int milli) {
        return actual -> {
            ZonedDateTime zdt = actual.atZone(ZoneId.systemDefault());
            int actualMilli = zdt.get(ChronoField.MILLI_OF_SECOND);
            Assertions.assertThat(actualMilli)
                    .as("Ожидалось, что миллисекунда будет %d, но была %d", milli, actualMilli)
                    .isEqualTo(milli);
        };
    }

    /**
     * Проверяет, что временная метка приходится на определенный день месяца (1-31).
     *
     * @param dayOfMonth ожидаемый день месяца (1-31)
     */
    public static InstantCondition dayOfMonth(int dayOfMonth) {
        return checkField(ChronoField.DAY_OF_MONTH, dayOfMonth);
    }

    /**
     * Проверяет, что временная метка приходится на первый день месяца.
     */
    public static InstantCondition isFirstDayOfMonth() {
        return dayOfMonth(1);
    }

    /**
     * Проверяет, что временная метка приходится на последний день месяца.
     */
    public static InstantCondition isLastDayOfMonth() {
        return actual -> {
            LocalDate localDate = actual.atZone(ZoneId.systemDefault()).toLocalDate();
            int lastDay = localDate.lengthOfMonth();
            Assertions.assertThat(localDate.getDayOfMonth())
                    .as("Ожидалось, что день месяца будет последним (%d), но был %d", lastDay, localDate.getDayOfMonth())
                    .isEqualTo(lastDay);
        };
    }

    /**
     * Проверяет, что временная метка приходится на определенный день года (1-366).
     *
     * @param dayOfYear ожидаемый день года (1-366)
     */
    public static InstantCondition dayOfYear(int dayOfYear) {
        return checkField(ChronoField.DAY_OF_YEAR, dayOfYear);
    }

    /**
     * Проверяет, что временная метка приходится на определенный квартал года (1-4).
     *
     * @param quarter ожидаемый квартал (1-4)
     */
    public static InstantCondition inQuarter(int quarter) {
        return actual -> {
            Month month = actual.atZone(ZoneId.systemDefault()).getMonth();
            int actualQuarter = (month.getValue() - 1) / 3 + 1;
            Assertions.assertThat(actualQuarter)
                    .as("Ожидалось, что квартал будет %d, но был %d", quarter, actualQuarter)
                    .isEqualTo(quarter);
        };
    }

    /**
     * Проверяет, что временная метка приходится на утро (например, с 6:00 до 12:00).
     */
    public static InstantCondition isMorning() {
        return actual -> {
            int hour = actual.atZone(ZoneId.systemDefault()).getHour();
            Assertions.assertThat(hour)
                    .as("Ожидалось, что время приходится на утро (6-11), но был час %d", hour)
                    .isBetween(6, 11);
        };
    }

    /**
     * Проверяет, что временная метка приходится на день (например, с 12:00 до 18:00).
     */
    public static InstantCondition isAfternoon() {
        return actual -> {
            int hour = actual.atZone(ZoneId.systemDefault()).getHour();
            Assertions.assertThat(hour)
                    .as("Ожидалось, что время приходится на день (12-17), но был час %d", hour)
                    .isBetween(12, 17);
        };
    }

    /**
     * Проверяет, что временная метка приходится на вечер (например, с 18:00 до 23:00).
     */
    public static InstantCondition isEvening() {
        return actual -> {
            int hour = actual.atZone(ZoneId.systemDefault()).getHour();
            Assertions.assertThat(hour)
                    .as("Ожидалось, что время приходится на вечер (18-22), но был час %d", hour)
                    .isBetween(18, 22);
        };
    }

    /**
     * Проверяет, что временная метка находится в заданной временной зоне.
     *
     * @param zoneId ожидаемая временная зона
     */
    public static InstantCondition isInZone(ZoneId zoneId) {
        return actual -> {
            ZoneId actualZoneId = actual.atZone(zoneId).getZone();
            Assertions.assertThat(actualZoneId)
                    .as("Ожидалось, что временная зона будет %s, но была %s", zoneId, actualZoneId)
                    .isEqualTo(zoneId);
        };
    }

    /**
     * Проверяет, что временная метка приходится на определенный день месяца в указанном месяце.
     *
     * @param month      ожидаемый месяц (1-12)
     * @param dayOfMonth ожидаемый день месяца (1-31)
     */
    public static InstantCondition isDate(int month, int dayOfMonth) {
        return actual -> {
            LocalDate localDate = actual.atZone(ZoneId.systemDefault()).toLocalDate();
            Assertions.assertThat(localDate)
                    .as("Ожидалось, что дата будет %02d-%02d, но была %s", month, dayOfMonth, localDate)
                    .isEqualTo(LocalDate.of(localDate.getYear(), month, dayOfMonth));
        };
    }

    /**
     * Проверяет, что временная метка приходится на определенную дату.
     *
     * @param year       ожидаемый год
     * @param month      ожидаемый месяц (1-12)
     * @param dayOfMonth ожидаемый день месяца (1-31)
     */
    public static InstantCondition isDate(int year, int month, int dayOfMonth) {
        return actual -> {
            LocalDate localDate = actual.atZone(ZoneId.systemDefault()).toLocalDate();
            Assertions.assertThat(localDate)
                    .as("Ожидалось, что дата будет %d-%02d-%02d, но была %s", year, month, dayOfMonth, localDate)
                    .isEqualTo(LocalDate.of(year, month, dayOfMonth));
        };
    }

    /**
     * Проверяет, что временная метка приходится на определенное время суток.
     *
     * @param hour   ожидаемый час (0-23)
     * @param minute ожидаемая минута (0-59)
     */
    public static InstantCondition isTime(int hour, int minute) {
        return actual -> {
            LocalTime localTime = actual.atZone(ZoneId.systemDefault()).toLocalTime().truncatedTo(ChronoUnit.MINUTES);
            Assertions.assertThat(localTime)
                    .as("Ожидалось, что время будет %02d:%02d, но было %s", hour, minute, localTime)
                    .isEqualTo(LocalTime.of(hour, minute));
        };
    }

    /**
     * Проверяет, что временная метка приходится на определенное время суток.
     *
     * @param hour   ожидаемый час (0-23)
     * @param minute ожидаемая минута (0-59)
     * @param second ожидаемая секунда (0-59)
     */
    public static InstantCondition isTime(int hour, int minute, int second) {
        return actual -> {
            LocalTime localTime = actual.atZone(ZoneId.systemDefault()).toLocalTime().truncatedTo(ChronoUnit.SECONDS);
            Assertions.assertThat(localTime)
                    .as("Ожидалось, что время будет %02d:%02d:%02d, но было %s", hour, minute, second, localTime)
                    .isEqualTo(LocalTime.of(hour, minute, second));
        };
    }

    /**
     * Проверяет, что временная метка приходится на определенное время суток с точностью до миллисекунд.
     *
     * @param hour   ожидаемый час (0-23)
     * @param minute ожидаемая минута (0-59)
     * @param second ожидаемая секунда (0-59)
     * @param milli  ожидаемая миллисекунда (0-999)
     */
    public static InstantCondition isTime(int hour, int minute, int second, int milli) {
        return actual -> {
            LocalTime localTime = actual.atZone(ZoneId.systemDefault()).toLocalTime().withNano(milli * 1_000_000);
            Assertions.assertThat(localTime)
                    .as("Ожидалось, что время будет %02d:%02d:%02d.%03d, но было %s", hour, minute, second, milli, localTime)
                    .isEqualTo(LocalTime.of(hour, minute, second, milli * 1_000_000));
        };
    }

    /**
     * Проверяет, что временная метка приходится на определенное LocalDateTime.
     *
     * @param expectedLocalDateTime ожидаемое LocalDateTime
     */
    public static InstantCondition isLocalDateTime(LocalDateTime expectedLocalDateTime) {
        return actual -> {
            LocalDateTime actualLocalDateTime = LocalDateTime.ofInstant(actual, ZoneId.systemDefault());
            Assertions.assertThat(actualLocalDateTime)
                    .as("Ожидалось, что LocalDateTime будет %s, но было %s", expectedLocalDateTime, actualLocalDateTime)
                    .isEqualTo(expectedLocalDateTime);
        };
    }

    /**
     * Проверяет, что временная метка приходится на определенное ZonedDateTime.
     *
     * @param expectedZonedDateTime ожидаемое ZonedDateTime
     */
    public static InstantCondition isZonedDateTime(ZonedDateTime expectedZonedDateTime) {
        return actual -> {
            ZonedDateTime actualZonedDateTime = ZonedDateTime.ofInstant(actual, expectedZonedDateTime.getZone());
            Assertions.assertThat(actualZonedDateTime)
                    .as("Ожидалось, что ZonedDateTime будет %s, но было %s", expectedZonedDateTime, actualZonedDateTime)
                    .isEqualTo(expectedZonedDateTime);
        };
    }

    /**
     * Проверяет, что временная метка приходится на определенное OffsetDateTime.
     *
     * @param expectedOffsetDateTime ожидаемое OffsetDateTime
     */
    public static InstantCondition isOffsetDateTime(OffsetDateTime expectedOffsetDateTime) {
        return actual -> {
            OffsetDateTime actualOffsetDateTime = OffsetDateTime.ofInstant(actual, expectedOffsetDateTime.getOffset());
            Assertions.assertThat(actualOffsetDateTime)
                    .as("Ожидалось, что OffsetDateTime будет %s, но было %s", expectedOffsetDateTime, actualOffsetDateTime)
                    .isEqualTo(expectedOffsetDateTime);
        };
    }

    /**
     * Проверяет, что временная метка находится в том же самом часе, что и указанная временная метка.
     *
     * @param reference опорная временная метка
     * @return условие, что временная метка в том же часе
     */
    public static InstantCondition sameHourAs(Instant reference) {
        return actual -> {
            int actualHour = actual.atZone(ZoneId.systemDefault()).getHour();
            int referenceHour = reference.atZone(ZoneId.systemDefault()).getHour();
            Assertions.assertThat(actualHour)
                    .as("Ожидалось, что час (%d) совпадает с часом (%d) опорной временной метки", actualHour, referenceHour)
                    .isEqualTo(referenceHour);
        };
    }

    /**
     * Проверяет, что временная метка находится в той же самой минуте, что и указанная временная метка.
     *
     * @param reference опорная временная метка
     * @return условие, что временная метка в той же минуте
     */
    public static InstantCondition sameMinuteAs(Instant reference) {
        return actual -> {
            int actualMinute = actual.atZone(ZoneId.systemDefault()).getMinute();
            int referenceMinute = reference.atZone(ZoneId.systemDefault()).getMinute();
            Assertions.assertThat(actualMinute)
                    .as("Ожидалось, что минута (%d) совпадает с минутой (%d) опорной временной метки", actualMinute, referenceMinute)
                    .isEqualTo(referenceMinute);
        };
    }

    /**
     * Проверяет, что временная метка находится в той же самой секунде, что и указанная временная метка.
     *
     * @param reference опорная временная метка
     * @return условие, что временная метка в той же секунде
     */
    public static InstantCondition sameSecondAs(Instant reference) {
        return actual -> {
            int actualSecond = actual.atZone(ZoneId.systemDefault()).getSecond();
            int referenceSecond = reference.atZone(ZoneId.systemDefault()).getSecond();
            Assertions.assertThat(actualSecond)
                    .as("Ожидалось, что секунда (%d) совпадает с секундой (%d) опорной временной метки", actualSecond, referenceSecond)
                    .isEqualTo(referenceSecond);
        };
    }

    /**
     * Проверяет, что временная метка находится в той же самой миллисекунде, что и указанная временная метка.
     *
     * @param reference опорная временная метка
     * @return условие, что временная метка в той же миллисекунде
     */
    public static InstantCondition sameMillisecondAs(Instant reference) {
        return actual -> {
            int actualMillis = actual.atZone(ZoneId.systemDefault()).get(ChronoField.MILLI_OF_SECOND);
            int referenceMillis = reference.atZone(ZoneId.systemDefault()).get(ChronoField.MILLI_OF_SECOND);
            Assertions.assertThat(actualMillis)
                    .as("Ожидалось, что миллисекунда (%d) совпадает с миллисекундой (%d) опорной временной метки", actualMillis, referenceMillis)
                    .isEqualTo(referenceMillis);
        };
    }

    /**
     * Проверяет, что временная метка приходится на начало недели (первый день недели).
     * Учитывается системная локаль для определения первого дня недели.
     */
    public static InstantCondition isStartOfWeek() {
        return actual -> {
            ZonedDateTime zdt = actual.atZone(ZoneId.systemDefault());
            DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY; // Default to Monday, consider using Locale for more flexibility
            boolean isStart = zdt.getDayOfWeek() == firstDayOfWeek && zdt.toLocalTime().equals(LocalTime.MIN);
            Assertions.assertThat(isStart)
                    .as("Ожидалось, что временная метка %s — начало недели (%s 00:00)", zdt.toLocalDate(), firstDayOfWeek)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что временная метка приходится на конец недели (последний день недели).
     * Учитывается системная локаль для определения последнего дня недели.
     */
    public static InstantCondition isEndOfWeek() {
        return actual -> {
            ZonedDateTime zdt = actual.atZone(ZoneId.systemDefault());
            DayOfWeek lastDayOfWeek = DayOfWeek.SUNDAY; // Default to Sunday, consider using Locale for more flexibility
            boolean isEnd = zdt.getDayOfWeek() == lastDayOfWeek && zdt.toLocalTime().equals(LocalTime.MAX.truncatedTo(ChronoUnit.NANOS));
            Assertions.assertThat(isEnd)
                    .as("Ожидалось, что временная метка %s — конец недели (%s 23:59:59.999999999)", zdt.toLocalDate(), lastDayOfWeek)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что временная метка приходится на начало месяца (первый день месяца, 00:00:00).
     */
    public static InstantCondition isStartOfMonth() {
        return actual -> {
            LocalDateTime ldt = LocalDateTime.ofInstant(actual, ZoneId.systemDefault());
            boolean isStart = ldt.getDayOfMonth() == 1 && ldt.toLocalTime().equals(LocalTime.MIN);
            Assertions.assertThat(isStart)
                    .as("Ожидалось, что временная метка %s — начало месяца (первый день, 00:00)", ldt.toLocalDate())
                    .isTrue();
        };
    }

    /**
     * Проверяет, что временная метка приходится на конец месяца (последний день месяца, 23:59:59.999999999).
     */
    public static InstantCondition isEndOfMonth() {
        return actual -> {
            LocalDateTime ldt = LocalDateTime.ofInstant(actual, ZoneId.systemDefault());
            LocalDate lastDayOfMonth = ldt.toLocalDate().with(java.time.temporal.TemporalAdjusters.lastDayOfMonth());
            boolean isEnd = ldt.toLocalDate().equals(lastDayOfMonth) && ldt.toLocalTime().equals(LocalTime.MAX.truncatedTo(ChronoUnit.NANOS));
            Assertions.assertThat(isEnd)
                    .as("Ожидалось, что временная метка %s — конец месяца (последний день, 23:59:59.999999999)", lastDayOfMonth)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что временная метка приходится на начало года (1 января, 00:00:00).
     */
    public static InstantCondition isStartOfYear() {
        return actual -> {
            LocalDateTime ldt = LocalDateTime.ofInstant(actual, ZoneId.systemDefault());
            boolean isStart = ldt.getDayOfYear() == 1 && ldt.toLocalTime().equals(LocalTime.MIN);
            Assertions.assertThat(isStart)
                    .as("Ожидалось, что временная метка %s — начало года (1 января, 00:00)", ldt.toLocalDate())
                    .isTrue();
        };
    }

    /**
     * Проверяет, что временная метка приходится на конец года (31 декабря, 23:59:59.999999999).
     */
    public static InstantCondition isEndOfYear() {
        return actual -> {
            LocalDateTime ldt = LocalDateTime.ofInstant(actual, ZoneId.systemDefault());
            LocalDate lastDayOfYear = ldt.toLocalDate().with(java.time.temporal.TemporalAdjusters.lastDayOfYear());
            boolean isEnd = ldt.toLocalDate().equals(lastDayOfYear) && ldt.toLocalTime().equals(LocalTime.MAX.truncatedTo(ChronoUnit.NANOS));
            Assertions.assertThat(isEnd)
                    .as("Ожидалось, что временная метка %s — конец года (31 декабря, 23:59:59.999999999)", lastDayOfYear)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что временная метка приходится на полдень (12:00:00).
     */
    public static InstantCondition isNoon() {
        return actual -> {
            LocalTime localTime = actual.atZone(ZoneId.systemDefault()).toLocalTime().truncatedTo(ChronoUnit.SECONDS);
            Assertions.assertThat(localTime)
                    .as("Ожидалось, что время будет полдень (12:00:00), но было %s", localTime)
                    .isEqualTo(LocalTime.NOON.truncatedTo(ChronoUnit.SECONDS));
        };
    }

    /**
     * Проверяет, что временная метка приходится на полночь (00:00:00).
     */
    public static InstantCondition isMidnight() {
        return actual -> {
            LocalTime localTime = actual.atZone(ZoneId.systemDefault()).toLocalTime().truncatedTo(ChronoUnit.SECONDS);
            Assertions.assertThat(localTime)
                    .as("Ожидалось, что время будет полночь (00:00:00), но было %s", localTime)
                    .isEqualTo(LocalTime.MIDNIGHT.truncatedTo(ChronoUnit.SECONDS));
        };
    }

    /**
     * Проверяет, что временная метка находится в пределах последних заданных секунд от текущего момента.
     *
     * @param seconds количество секунд назад
     */
    public static InstantCondition isWithinLastSeconds(long seconds) {
        return notOlderThan(Duration.ofSeconds(seconds));
    }

    /**
     * Проверяет, что временная метка находится в пределах следующих заданных секунд от текущего момента.
     *
     * @param seconds количество секунд вперед
     */
    public static InstantCondition isWithinNextSeconds(long seconds) {
        return notFurtherThan(Duration.ofSeconds(seconds));
    }

    /**
     * Проверяет, что временная метка находится в пределах последних заданных минут от текущего момента.
     *
     * @param minutes количество минут назад
     */
    public static InstantCondition isWithinLastMinutes(long minutes) {
        return notOlderThan(Duration.ofMinutes(minutes));
    }

    /**
     * Проверяет, что временная метка находится в пределах следующих заданных минут от текущего момента.
     *
     * @param minutes количество минут вперед
     */
    public static InstantCondition isWithinNextMinutes(long minutes) {
        return notFurtherThan(Duration.ofMinutes(minutes));
    }

    /**
     * Проверяет, что временная метка находится в пределах последних заданных часов от текущего момента.
     *
     * @param hours количество часов назад
     */
    public static InstantCondition isWithinLastHours(long hours) {
        return notOlderThan(Duration.ofHours(hours));
    }

    /**
     * Проверяет, что временная метка находится в пределах следующих заданных часов от текущего момента.
     *
     * @param hours количество часов вперед
     */
    public static InstantCondition isWithinNextHours(long hours) {
        return notFurtherThan(Duration.ofHours(hours));
    }

    /**
     * Проверяет, что временная метка находится в пределах последних заданных дней от текущего момента.
     *
     * @param days количество дней назад
     */
    public static InstantCondition isWithinLastDays(long days) {
        return notOlderThan(Duration.ofDays(days));
    }

    /**
     * Проверяет, что временная метка находится в пределах следующих заданных дней от текущего момента.
     *
     * @param days количество дней вперед
     */
    public static InstantCondition isWithinNextDays(long days) {
        return notFurtherThan(Duration.ofDays(days));
    }
}
