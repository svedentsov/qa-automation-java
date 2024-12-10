package common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Утилитный класс {@code DateUtil} предоставляет методы для работы с датами и временем.
 * Этот класс предоставляет функциональность для парсинга дат, изменения форматов,
 * получения диапазонов дат, а также конвертации объектов {@link java.time.Duration}
 * в объекты {@link org.awaitility.Duration}.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateUtil {

    public static final String DATE_TIME_FORMAT_RANGE = "dd/MM/yyyy";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_RANGE);

    /**
     * Получает диапазон дат из строки в формате "dd/MM/yyyy - dd/MM/yyyy".
     *
     * @param date строка с диапазоном дат
     * @return список из двух дат
     * @throws IllegalArgumentException если строка пустая или содержит некорректное количество дат
     */
    public static List<LocalDate> getDateRange(String date) {
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException("Дата не может быть пустой или null");
        }

        List<String> dates = Arrays.stream(date.split(" - "))
                .map(String::trim)
                .toList();

        if (dates.size() != 2) {
            throw new IllegalArgumentException("Диапазон должен содержать две даты, разделённые ' - '");
        }

        return dates.stream()
                .map(d -> LocalDate.parse(d, DATE_FORMATTER))
                .collect(Collectors.toList());
    }

    /**
     * Парсит строку с датой в формат {@link LocalDateTime}.
     *
     * @param date    строка с датой
     * @param pattern шаблон для парсинга даты
     * @return объект {@link LocalDateTime}
     * @throws IllegalArgumentException если парсинг не удался
     */
    public static LocalDateTime parseDate(String date, String pattern) {
        try {
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(pattern));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    String.format("Ошибка при парсинге даты %s с шаблоном %s", date, pattern), e);
        }
    }

    /**
     * Изменяет формат отображения даты.
     *
     * @param ldt    объект {@link LocalDateTime}
     * @param format новый формат
     * @return дата в новом формате
     * @throws IllegalArgumentException если дата или формат являются null или пустыми
     */
    public static String changeFormat(LocalDateTime ldt, String format) {
        if (ldt == null || format == null || format.trim().isEmpty()) {
            throw new IllegalArgumentException("Дата и формат не могут быть null или пустыми");
        }
        return ldt.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * Конвертирует объект {@link java.time.Duration} в {@link org.awaitility.Duration}.
     *
     * @param duration объект {@link java.time.Duration}
     * @return объект {@link org.awaitility.Duration}
     */
    public static org.awaitility.Duration convert(java.time.Duration duration) {
        return new org.awaitility.Duration(duration.getSeconds(), TimeUnit.SECONDS);
    }

    /**
     * Преобразует объект {@link LocalDate} в строковое представление.
     *
     * @param date объект {@link LocalDate}, который требуется преобразовать
     * @return строковое представление даты в формате "dd MMM yy"
     */
    public static String getDateAsString(@Nonnull LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yy");
        return date.format(formatter);
    }

    /**
     * Преобразует объект {@link LocalDate} в строковое представление с использованием указанного формата.
     *
     * @param date         объект {@link LocalDate}, который требуется преобразовать
     * @param stringFormat Формат строки даты (например, "dd/MM/yyyy")
     * @return строковое представление даты в указанном формате
     */
    public static String getDateAsString(@Nonnull LocalDate date, @Nonnull String stringFormat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(stringFormat);
        return date.format(formatter);
    }

    /**
     * Преобразует строку в объект {@link LocalDate}.
     *
     * @param dateAsString строковое представление даты
     * @return объект {@link LocalDate}, соответствующий переданной строке
     * @throws IllegalArgumentException если произошла ошибка при парсинге строки
     */
    public static LocalDate fromString(@Nonnull String dateAsString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yy");
        try {
            return LocalDate.parse(dateAsString, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ошибка при парсинге строки даты", e);
        }
    }

    /**
     * Добавляет указанное количество дней к указанной дате.
     *
     * @param date исходная дата
     * @param days количество дней, которое следует добавить
     * @return новый объект {@link LocalDate}, представляющий дату после добавления указанного количества дней
     */
    public static LocalDate addDaysToDate(@Nonnull LocalDate date, int days) {
        return date.plusDays(days);
    }

    /**
     * Добавляет указанное количество единиц времени к указанной дате.
     *
     * @param date        исходная дата
     * @param amountToAdd количество единиц времени, которое следует добавить
     * @param unit        поле календаря, к которому добавляются дни ({@link ChronoUnit#DAYS}, {@link ChronoUnit#MONTHS}, {@link ChronoUnit#YEARS} и т. д.)
     * @return новый объект {@link LocalDate}, представляющий дату после добавления указанного количества дней.
     */
    public static LocalDate addTimeToDate(@Nonnull LocalDate date, long amountToAdd, @Nonnull ChronoUnit unit) {
        return date.plus(amountToAdd, unit);
    }
}
