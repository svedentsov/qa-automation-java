package core.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Утилитарный класс для работы с кодированием и декодированием в формате Base64.
 */
@UtilityClass
public class Base64Util {

    /**
     * Кодирует строку в формате Base64.
     *
     * @param value Строка для кодирования.
     * @return Закодированная строка в формате Base64.
     * @throws UnsupportedEncodingException Если кодировка не поддерживается.
     */
    @SneakyThrows(UnsupportedEncodingException.class)
    public static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8.toString()));
    }

    /**
     * Декодирует строку из формата Base64.
     *
     * @param value Закодированная строка в формате Base64.
     * @return Декодированная строка.
     * @throws UnsupportedEncodingException Если кодировка не поддерживается.
     */
    @SneakyThrows(UnsupportedEncodingException.class)
    public static String decode(String value) {
        byte[] decodedValue = Base64.getDecoder().decode(value);
        return new String(decodedValue, StandardCharsets.UTF_8.toString());
    }

    /**
     * Кодирует авторизационный токен с использованием формата Base64.
     *
     * @param accountId Идентификатор учетной записи.
     * @param publicKey Публичный ключ.
     * @param signature Сигнатура.
     * @return Закодированный авторизационный токен в формате Base64.
     * @throws UnsupportedEncodingException Если кодировка не поддерживается.
     */
    @SneakyThrows
    public static String encodeAuthToken(String accountId, String publicKey, String signature) {
        String input = String.format("%s|%s|%s", accountId, publicKey, signature);
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8.toString()));
    }
}
