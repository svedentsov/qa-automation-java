package com.svedentsov.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Утилиты для работы с Base64 (Base64Util)")
class Base64UtilTest {

    @Nested
    @DisplayName("Кодирование (encode)")
    class EncodeTests {

        @Test
        @DisplayName("Простая строка на латинице корректно кодируется")
        void shouldEncodeSimpleAsciiString() {
            String original = "hello world";
            String expected = "aGVsbG8gd29ybGQ=";
            assertEquals(expected, Base64Util.encode(original));
        }

        @Test
        @DisplayName("Строка с кириллическими символами корректно кодируется в UTF-8")
        void shouldEncodeCyrillicString() {
            String original = "Привет, мир!";
            // Корректное Base64-представление "Привет, мир!" в UTF-8:
            String expected = "0J/RgNC40LLQtdGCLCDQvNC40YAh";
            assertEquals(expected, Base64Util.encode(original));
        }

        @Test
        @DisplayName("Пустая строка кодируется в пустую строку Base64")
        void shouldEncodeEmptyString() {
            assertEquals("", Base64Util.encode(""));
        }

        @Test
        @DisplayName("Метод выбрасывает NullPointerException при попытке закодировать null")
        void shouldThrowExceptionForNullInput() {
            assertThrows(NullPointerException.class, () -> Base64Util.encode(null));
        }
    }

    @Nested
    @DisplayName("Декодирование (decode)")
    class DecodeTests {

        @Test
        @DisplayName("Ззакодированная строка на латинице корректно декодируется")
        void shouldDecodeSimpleAsciiString() {
            String encoded = "aGVsbG8gd29ybGQ=";
            String expected = "hello world";
            assertEquals(expected, Base64Util.decode(encoded));
        }

        @Test
        @DisplayName("Закодированная строка с кириллическими символами корректно декодируется")
        void shouldDecodeCyrillicString() {
            // Используем корректный Base64 для "Привет, мир!"
            String encoded = "0J/RgNC40LLQtdGCLCDQvNC40YAh";
            String expected = "Привет, мир!";
            assertEquals(expected, Base64Util.decode(encoded));
        }

        @Test
        @DisplayName("Пустая строка Base64 декодируется в пустую строку")
        void shouldDecodeEmptyString() {
            assertEquals("", Base64Util.decode(""));
        }

        @Test
        @DisplayName("Метод выбрасывает IllegalArgumentException для некорректной Base64 строки")
        void shouldThrowExceptionForInvalidBase64() {
            String invalidBase64 = "это не base64!";
            assertThrows(IllegalArgumentException.class, () -> Base64Util.decode(invalidBase64));
        }
    }

    @Nested
    @DisplayName("Кодирование токена авторизации (encodeAuthToken)")
    class EncodeAuthTokenTests {

        @ParameterizedTest(name = "[{index}] Кодирование токена для accountId=`{0}`")
        @CsvSource({
                "user1, pub_key_123, sign_abc",
                "test_account, , ''", // Проверка с пустыми значениями
                "another_user, some_very_long_public_key_that_needs_to_be_encoded, signature"
        })
        @DisplayName("Кодирование токена с использованием параметризованных тестов")
        void shouldEncodeAuthToken(String accountId, String publicKey, String signature) {
            // Чтобы избежать null в CsvSource, используем пустую строку для симуляции
            publicKey = (publicKey == null) ? "" : publicKey;
            signature = (signature == null) ? "" : signature;

            String expectedInput = String.format("%s|%s|%s", accountId, publicKey, signature);
            String expectedEncoded = Base64.getEncoder().encodeToString(expectedInput.getBytes(StandardCharsets.UTF_8));

            String actualEncoded = Base64Util.encodeAuthToken(accountId, publicKey, signature);

            assertEquals(expectedEncoded, actualEncoded);
        }

        @Test
        @DisplayName("Закодированный токен может быть успешно декодирован обратно в исходную структуру")
        void shouldCreateReversibleToken() {
            String accountId = "my_account";
            String publicKey = "my_public_key";
            String signature = "my_signature";

            String token = Base64Util.encodeAuthToken(accountId, publicKey, signature);

            byte[] decodedBytes = Base64.getDecoder().decode(token);
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

            String[] parts = decodedString.split("\\|");

            assertEquals(3, parts.length);
            assertEquals(accountId, parts[0]);
            assertEquals(publicKey, parts[1]);
            assertEquals(signature, parts[2]);
        }

        @Test
        @DisplayName("Метод выбрасывает NullPointerException, если один из аргументов равен null")
        void shouldThrowExceptionForNullArguments() {
            String value = "some_value";
            assertThrows(NullPointerException.class, () -> Base64Util.encodeAuthToken(null, value, value));
            assertThrows(NullPointerException.class, () -> Base64Util.encodeAuthToken(value, null, value));
            assertThrows(NullPointerException.class, () -> Base64Util.encodeAuthToken(value, value, null));
        }
    }
}
