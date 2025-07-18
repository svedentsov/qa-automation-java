package com.svedentsov.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Утилиты для работы с Base64")
class Base64UtilTest {

    private final String asciiOriginal = "hello world";
    private final String asciiEncoded = "aGVsbG8gd29ybGQ=";

    private final String cyrillicOriginal = "Привет, мир!";
    private final String cyrillicEncoded = "0J/RgNC40LLQtdGCLCDQvNC40YAh";
    private final byte[] cyrillicBytes = cyrillicOriginal.getBytes(java.nio.charset.StandardCharsets.UTF_8);

    @Nested
    @DisplayName("Стандартное кодирование (encode)")
    class EncodeTests {
        @Test
        @DisplayName("Корректно кодирует строку ASCII")
        void shouldEncodeSimpleAsciiString() {
            assertEquals(asciiEncoded, Base64Util.encode(asciiOriginal));
        }

        @Test
        @DisplayName("Корректно кодирует строку с кириллицей (UTF-8)")
        void shouldEncodeCyrillicString() {
            assertEquals(cyrillicEncoded, Base64Util.encode(cyrillicOriginal));
        }

        @Test
        @DisplayName("Корректно кодирует массив байтов")
        void shouldEncodeBytes() {
            assertEquals(cyrillicEncoded, Base64Util.encodeBytes(cyrillicBytes));
        }

        @Test
        @DisplayName("Пустая строка кодируется в пустую строку")
        void shouldEncodeEmptyString() {
            assertEquals("", Base64Util.encode(""));
        }

        @Test
        @DisplayName("Выбрасывает NullPointerException при кодировании null строки")
        void shouldThrowExceptionForNullString() {
            assertThrows(NullPointerException.class, () -> Base64Util.encode(null));
        }

        @Test
        @DisplayName("Выбрасывает NullPointerException при кодировании null массива байтов")
        void shouldThrowExceptionForNullBytes() {
            assertThrows(NullPointerException.class, () -> Base64Util.encodeBytes(null));
        }
    }

    @Nested
    @DisplayName("Стандартное декодирование (decode)")
    class DecodeTests {
        @Test
        @DisplayName("Корректно декодирует строку ASCII")
        void shouldDecodeSimpleAsciiString() {
            assertEquals(asciiOriginal, Base64Util.decode(asciiEncoded));
        }

        @Test
        @DisplayName("Корректно декодирует строку с кириллицей")
        void shouldDecodeCyrillicString() {
            assertEquals(cyrillicOriginal, Base64Util.decode(cyrillicEncoded));
        }

        @Test
        @DisplayName("Корректно декодирует в массив байтов")
        void shouldDecodeToBytes() {
            assertArrayEquals(cyrillicBytes, Base64Util.decodeToBytes(cyrillicEncoded));
        }

        @Test
        @DisplayName("Пустая строка Base64 декодируется в пустую строку")
        void shouldDecodeEmptyString() {
            assertEquals("", Base64Util.decode(""));
        }

        @Test
        @DisplayName("Выбрасывает IllegalArgumentException для невалидной Base64 строки")
        void shouldThrowExceptionForInvalidBase64() {
            String invalidBase64 = "это не base64!";
            assertThrows(IllegalArgumentException.class, () -> Base64Util.decode(invalidBase64));
        }
    }

    @Nested
    @DisplayName("URL-safe кодирование и декодирование")
    class UrlSafeTests {
        private final String originalUrlUnsafe = "a?b&c=d/e+f";
        private final String encodedUrlSafe = "YT9iJmM9ZC9lK2Y=";

        @Test
        @DisplayName("Корректно кодирует строку в URL-safe формат")
        void shouldEncodeUrlSafeString() {
            assertEquals(encodedUrlSafe, Base64Util.encodeUrlSafe(originalUrlUnsafe));
        }

        @Test
        @DisplayName("Корректно декодирует строку из URL-safe формата")
        void shouldDecodeUrlSafeString() {
            assertEquals(originalUrlUnsafe, Base64Util.decodeUrlSafe(encodedUrlSafe));
        }

        @Test
        @DisplayName("Декодированная строка идентична исходной")
        void shouldBeReversible() {
            String original = "сложная/строка?с+символами#123";
            String encoded = Base64Util.encodeUrlSafe(original);
            String decoded = Base64Util.decodeUrlSafe(encoded);
            assertEquals(original, decoded);
        }
    }
}
