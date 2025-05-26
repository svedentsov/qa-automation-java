package core.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Сериализатор для преобразования объекта {@link LocalDateTime} в строковое представление даты и времени.
 * Этот класс предназначен для преобразования объектов {@link LocalDateTime} в строку в формате ISO {@code yyyy-MM-dd'T'HH:mm:ss}.
 */
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Сериализует объект {@link LocalDateTime} в строковое представление даты и времени.
     * Метод преобразует объект {@link LocalDateTime} в строку в формате ISO {@code yyyy-MM-dd'T'HH:mm:ss}
     * и записывает эту строку в {@link JsonGenerator}.
     *
     * @param value       объект {@link LocalDateTime}, который нужно сериализовать
     * @param gen         {@link JsonGenerator} для записи строки в JSON
     * @param serializers {@link SerializerProvider} для предоставления дополнительных данных
     * @throws IOException если произошла ошибка ввода-вывода
     */
    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.format(formatter));
    }
}
