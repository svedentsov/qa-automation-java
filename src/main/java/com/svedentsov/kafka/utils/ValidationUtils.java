package com.svedentsov.kafka.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Утилитарные методы для валидации аргументов.
 */
@UtilityClass
public final class ValidationUtils {

    /**
     * Проверяет, что строка не является null и не состоит из пробельных символов.
     *
     * @param str     строка для проверки (не может быть {@code null})
     * @param message сообщение для исключения, если проверка не пройдена
     * @return исходная строка {@code str}, если она валидна
     * @throws IllegalArgumentException если str является {@code null} или пустой/состоит из пробелов
     */
    public static String requireNonBlank(@NonNull String str, String message) {
        if (str.isBlank()) throw new IllegalArgumentException(message);
        return str;
    }
}
