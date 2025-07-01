package com.svedentsov.kafka.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

import static java.util.Objects.requireNonNull;

/**
 * Утилитарные методы для валидации аргументов.
 */
@Slf4j
@UtilityClass
public final class ValidationUtils {
    private static final Duration MIN_POLL_TIMEOUT = Duration.ofMillis(1);

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

    public static Duration validatePollTimeout(Duration pollTimeout) {
        requireNonNull(pollTimeout, "Poll timeout не может быть null.");
        if (pollTimeout.isNegative() || pollTimeout.isZero()) {
            log.warn("pollTimeout отрицательный или нулевой ({}). Установлен минимальный таймаут {} для корректной работы.", pollTimeout, MIN_POLL_TIMEOUT);
            return MIN_POLL_TIMEOUT;
        }
        return pollTimeout;
    }
}
