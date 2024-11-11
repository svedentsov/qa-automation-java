package core.config.converters;

import core.utils.StrUtil;
import core.utils.StreamUtils;
import lombok.AllArgsConstructor;
import org.aeonbits.owner.Converter;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Класс-конвертер для преобразования строкового представления в объект Duration.
 */
public class DurationConverter implements Converter<Duration> {

    /**
     * Преобразует строку в объект типа Duration.
     *
     * @param method метод, для которого выполняется преобразование
     * @param text   входная строка для преобразования
     * @return объект типа Duration, представляющий время, указанное во входной строке
     * @throws IllegalStateException если не удалось определить единицу времени для указанной строки
     */
    @Override
    public Duration convert(Method method, String text) {
        int numericPart = StrUtil.getNumberFromStr(text);

        return StreamUtils.getFirstInOptional(DurationTags.values(), tag -> text.endsWith(tag.strValue))
                .map(tag -> Duration.of(numericPart, tag.chronoUnitValue))
                .orElseThrow(() -> new IllegalStateException(String.format(
                        "Time unit not specified for '%s' property. Expected time unit values are: '%s'",
                        text, StreamUtils.mapEnumToList(DurationTags.class, tag -> tag.strValue))));
    }

    /**
     * Перечисление, определяющее теги длительности и их соответствующие хронологические единицы.
     */
    @AllArgsConstructor
    private enum DurationTags {
        MINUTES(".min", ChronoUnit.MINUTES),
        SECONDS(".sec", ChronoUnit.SECONDS),
        MILLIS(".milli", ChronoUnit.MILLIS),
        HOURS(".hour", ChronoUnit.HOURS);

        private final String strValue; // Строковое представление тега.
        private final ChronoUnit chronoUnitValue; // Хронологическая единица, соответствующая тегу.
    }
}
