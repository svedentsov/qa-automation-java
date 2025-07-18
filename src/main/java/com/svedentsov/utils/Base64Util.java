package com.svedentsov.utils;

import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/**
 * Утилитарный класс для выполнения операций кодирования и декодирования в формате Base64.
 * <p>Предоставляет методы для стандартного и URL-safe кодирования строк и байтовых массивов.
 * Все строковые операции по умолчанию используют кодировку UTF-8.
 */
@UtilityClass
public class Base64Util {

    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Base64.Decoder DECODER = Base64.getDecoder();
    private static final Base64.Encoder URL_SAFE_ENCODER = Base64.getUrlEncoder();
    private static final Base64.Decoder URL_SAFE_DECODER = Base64.getUrlDecoder();

    /**
     * Кодирует строку в стандартный формат Base64, используя кодировку UTF-8.
     *
     * @param value Исходная строка для кодирования. Не может быть {@code null}.
     * @return Закодированная строка в формате Base64.
     * @throws NullPointerException если {@code value} равен {@code null}.
     */
    public static String encode(final String value) {
        Objects.requireNonNull(value, "Строка для кодирования не может быть null");
        return encodeBytes(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Кодирует массив байтов в стандартный формат Base64.
     *
     * @param bytes Массив байтов для кодирования. Не может быть {@code null}.
     * @return Закодированная строка в формате Base64.
     * @throws NullPointerException если {@code bytes} равен {@code null}.
     */
    public static String encodeBytes(final byte[] bytes) {
        Objects.requireNonNull(bytes, "Массив байтов для кодирования не может быть null");
        return ENCODER.encodeToString(bytes);
    }

    /**
     * Декодирует строку из стандартного формата Base64, используя кодировку UTF-8.
     *
     * @param value Закодированная строка в формате Base64. Не может быть {@code null}.
     * @return Декодированная исходная строка.
     * @throws NullPointerException     если {@code value} равен {@code null}.
     * @throws IllegalArgumentException если {@code value} не является валидной строкой Base64.
     */
    public static String decode(final String value) {
        Objects.requireNonNull(value, "Строка для декодирования не может быть null");
        byte[] decodedBytes = decodeToBytes(value);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    /**
     * Декодирует строку из стандартного формата Base64 в массив байтов.
     *
     * @param value Закодированная строка в формате Base64. Не может быть {@code null}.
     * @return Декодированный массив байтов.
     * @throws NullPointerException     если {@code value} равен {@code null}.
     * @throws IllegalArgumentException если {@code value} не является валидной строкой Base64.
     */
    public static byte[] decodeToBytes(final String value) {
        Objects.requireNonNull(value, "Строка для декодирования не может быть null");
        return DECODER.decode(value);
    }

    /**
     * Кодирует строку в URL-safe формат Base64, используя кодировку UTF-8.
     * Этот формат безопасен для использования в именах файлов и URL.
     *
     * @param value Исходная строка для кодирования. Не может быть {@code null}.
     * @return Закодированная строка в формате URL-safe Base64.
     * @throws NullPointerException если {@code value} равен {@code null}.
     */
    public static String encodeUrlSafe(final String value) {
        Objects.requireNonNull(value, "Строка для URL-safe кодирования не может быть null");
        return encodeBytesUrlSafe(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Кодирует массив байтов в URL-safe формат Base64.
     *
     * @param bytes Массив байтов для кодирования. Не может быть {@code null}.
     * @return Закодированная строка в формате URL-safe Base64.
     * @throws NullPointerException если {@code bytes} равен {@code null}.
     */
    public static String encodeBytesUrlSafe(final byte[] bytes) {
        Objects.requireNonNull(bytes, "Массив байтов для URL-safe кодирования не может быть null");
        return URL_SAFE_ENCODER.encodeToString(bytes);
    }

    /**
     * Декодирует строку из URL-safe формата Base64, используя кодировку UTF-8.
     *
     * @param value Закодированная URL-safe строка в формате Base64. Не может быть {@code null}.
     * @return Декодированная исходная строка.
     * @throws NullPointerException     если {@code value} равен {@code null}.
     * @throws IllegalArgumentException если {@code value} не является валидной строкой Base64.
     */
    public static String decodeUrlSafe(final String value) {
        Objects.requireNonNull(value, "Строка для URL-safe декодирования не может быть null");
        byte[] decodedBytes = decodeToBytesUrlSafe(value);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    /**
     * Декодирует строку из URL-safe формата Base64 в массив байтов.
     *
     * @param value Закодированная URL-safe строка в формате Base64. Не может быть {@code null}.
     * @return Декодированный массив байтов.
     * @throws NullPointerException     если {@code value} равен {@code null}.
     * @throws IllegalArgumentException если {@code value} не является валидной строкой Base64.
     */
    public static byte[] decodeToBytesUrlSafe(final String value) {
        Objects.requireNonNull(value, "Строка для URL-safe декодирования не может быть null");
        return URL_SAFE_DECODER.decode(value);
    }
}
