package com.svedentsov.utils;

import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static java.util.Objects.requireNonNull;

/**
 * Утилитарный класс для работы с кодированием и декодированием в формате Base64.
 */
@UtilityClass
public class Base64Util {

    /**
     * Кодирует строку в формате Base64.
     *
     * @param value строка для кодирования
     * @return закодированная строка в формате Base64
     * @throws NullPointerException если value == null
     */
    public static String encode(final String value) {
        requireNonNull(value, "value must not be null");
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Декодирует строку из формата Base64.
     *
     * @param value закодированная строка в формате Base64
     * @return декодированная строка
     * @throws NullPointerException     если value == null
     * @throws IllegalArgumentException если value невалидна как Base64
     */
    public static String decode(final String value) {
        requireNonNull(value, "value must not be null");
        byte[] decodedValue = Base64.getDecoder().decode(value);
        return new String(decodedValue, StandardCharsets.UTF_8);
    }

    /**
     * Кодирует авторизационный токен с использованием формата Base64.
     *
     * @param accountId идентификатор учетной записи
     * @param publicKey публичный ключ
     * @param signature сигнатура
     * @return закодированный авторизационный токен в формате Base64
     * @throws NullPointerException если любой из аргументов == null
     */
    public static String encodeAuthToken(final String accountId, final String publicKey, final String signature) {
        requireNonNull(accountId, "accountId must not be null");
        requireNonNull(publicKey, "publicKey must not be null");
        requireNonNull(signature, "signature must not be null");
        String input = String.format("%s|%s|%s", accountId, publicKey, signature);
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }
}
