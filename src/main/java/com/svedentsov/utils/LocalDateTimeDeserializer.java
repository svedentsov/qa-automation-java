package com.svedentsov.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

/**
 * Десериализатор для преобразования строкового представления даты и времени в объект {@link LocalDateTime}.
 * Этот класс предназначен для обработки строковых представлений дат и времени, которые включают смещение часового пояса.
 * Он поддерживает формат даты и времени, включающий миллисекунды и смещение в формате {@code +HHMM} (например, {@code +0000}).
 * Пример строки даты и времени: {@code 2024-07-30T14:54:20.891+0000}.
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    // Форматтер для парсинга даты и времени с учетом смещения
    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .optionalStart()
            .appendPattern(".SSS")
            .optionalStart()
            .appendOffset("+HHMM", "+0000")
            .toFormatter();

    /**
     * Десериализует строковое представление даты и времени в объект {@link LocalDateTime}.
     * Метод парсит строку, представляющую дату и время с учетом смещения часового пояса
     * и возвращает объект {@link LocalDateTime}.
     *
     * @param p    {@link JsonParser} для получения строкового представления даты и времени из JSON
     * @param ctxt {@link DeserializationContext} контекст десериализации
     * @return объект {@link LocalDateTime}, соответствующий строковому представлению
     * @throws IOException если не удалось разобрать строку или произошла ошибка ввода-вывода
     */
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateString = p.getValueAsString();
        try {
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateString, formatter);
            return offsetDateTime.toLocalDateTime();
        } catch (Exception e) {
            throw new IOException("Не удалось разобрать дату: " + dateString, e);
        }
    }
}
