package com.svedentsov.kafka.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

import static java.util.Objects.requireNonNull;

/**
 * Утилитарный класс для общих методов валидации.
 */
@Slf4j
@UtilityClass
public final class ValidationUtils {
    private static final Duration MIN_POLL_TIMEOUT = Duration.ofMillis(1);

    /**
     * Проверяет, что строка не является null и не состоит из пробельных символов.
     *
     * @param str     строка для проверки.
     * @param message сообщение для исключения, если проверка не пройдена.
     * @return исходная непустая строка.
     * @throws IllegalArgumentException если str является {@code null} или пустой/состоит из пробелов.
     */
    public static String requireNonBlank(@NonNull String str, String message) {
        if (str.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return str;
    }

    /**
     * Валидирует и нормализует таймаут для операции poll.
     * Если таймаут null, отрицательный или нулевой, возвращает минимально допустимое значение.
     *
     * @param pollTimeout Таймаут для проверки.
     * @return Валидный таймаут.
     */
    public static Duration validatePollTimeout(Duration pollTimeout) {
        requireNonNull(pollTimeout, "Poll timeout не может быть null.");
        if (pollTimeout.isNegative() || pollTimeout.isZero()) {
            log.warn("pollTimeout отрицательный или нулевой ({}). Установлен минимальный таймаут {} для корректной работы.", pollTimeout, MIN_POLL_TIMEOUT);
            return MIN_POLL_TIMEOUT;
        }
        return pollTimeout;
    }
}
