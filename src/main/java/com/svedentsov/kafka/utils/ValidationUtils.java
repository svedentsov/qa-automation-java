package com.svedentsov.kafka.utils;

import lombok.experimental.UtilityClass;

/**
 * Утилитарные методы для валидации аргументов.
 */
@UtilityClass
public final class ValidationUtils {

    /**
     * Проверяет, что объект не null.
     *
     * @param obj     объект для проверки
     * @param message сообщение для исключения, если null
     * @param <T>     тип объекта
     * @return obj, если не null
     * @throws IllegalArgumentException если obj == null
     */
    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null) throw new IllegalArgumentException(message);
        return obj;
    }

    /**
     * Проверяет, что строка не null и не пустая после обрезки.
     *
     * @param str     строка для проверки
     * @param message сообщение для исключения, если невалидна
     * @return str, если валидна
     * @throws IllegalArgumentException если str == null или пустая
     */
    public static String requireNonBlank(String str, String message) {
        if (str == null || str.isBlank()) throw new IllegalArgumentException(message);
        return str;
    }
}
