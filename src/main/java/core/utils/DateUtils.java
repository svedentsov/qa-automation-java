package core.utils;

import javax.annotation.Nonnull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Утилитарный класс для работы с датами.
 */
public class DateUtils {

    /**
     * Преобразует объект {@link Date} в строковое представление.
     *
     * @param date Объект {@link Date}, который требуется преобразовать.
     * @return Строковое представление даты в формате "dd MMM yy".
     */
    @Nonnull
    public static String getDateAsString(@Nonnull Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy");
        return sdf.format(date);
    }

    /**
     * Преобразует объект {@link Date} в строковое представление с использованием указанного формата.
     *
     * @param date         Объект {@link Date}, который требуется преобразовать.
     * @param stringFormat Формат строки даты (например, "dd/MM/yyyy").
     * @return Строковое представление даты в указанном формате.
     */
    @Nonnull
    public static String getDateAsString(@Nonnull Date date, @Nonnull String stringFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(stringFormat);
        return sdf.format(date);
    }

    /**
     * Преобразует строку в объект {@link Date}.
     *
     * @param dateAsString Строковое представление даты.
     * @return Объект {@link Date}, соответствующий переданной строке.
     * @throws RuntimeException Если произошла ошибка при парсинге строки.
     */
    @Nonnull
    public static Date fromString(@Nonnull String dateAsString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy");
        try {
            return sdf.parse(dateAsString);
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing date string", e);
        }
    }

    /**
     * Добавляет указанное количество дней к указанной дате.
     *
     * @param date     Исходная дата.
     * @param selector Поле календаря, к которому добавляются дни ({@link Calendar#DAY_OF_MONTH}, {@link Calendar#MONTH}, {@link Calendar#YEAR} и т. д.).
     * @param days     Количество дней, которое следует добавить.
     * @return Новый объект {@link Date}, представляющий дату после добавления указанного количества дней.
     */
    @Nonnull
    public static Date addDaysToDate(@Nonnull Date date, int selector, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(selector, days);
        return cal.getTime();
    }
}
