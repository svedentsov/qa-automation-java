package com.svedentsov.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Утилиты для работы со строками")
class StrUtilTest {

    @Nested
    @DisplayName("Базовые проверки и утилиты")
    class BasicUtilsTests {
        @ParameterizedTest
        @ValueSource(strings = {" ", "  ", "\n", "\t"})
        @DisplayName("Должен считать пробельные строки бланковыми")
        void shouldBeBlankForWhitespace(String input) {
            assertTrue(StrUtil.isBlank(input));
        }

        @Test
        @DisplayName("Должен считать null и пустую строку бланковыми")
        void shouldBeBlankForNullAndEmpty() {
            assertTrue(StrUtil.isBlank(null));
            assertTrue(StrUtil.isBlank(""));
        }

        @Test
        @DisplayName("isEmpty: null и \"\" - пустые; пробелы - не пустые")
        void shouldDetectEmpty() {
            assertTrue(StrUtil.isEmpty(null));
            assertTrue(StrUtil.isEmpty(""));
            assertFalse(StrUtil.isEmpty(" "));
            assertFalse(StrUtil.isEmpty("abc"));
        }

        @Test
        @DisplayName("defaultIfNull: возвращает дефолт для null, иначе исходную")
        void shouldDefaultIfNull() {
            assertEquals("def", StrUtil.defaultIfNull(null, "def"));
            assertEquals("abc", StrUtil.defaultIfNull("abc", "def"));
        }

        @Test
        @DisplayName("Должен корректно укорачивать строку")
        void shouldAbbreviateString() {
            assertEquals("abc...", StrUtil.abbreviate("abcdefg", 6));
            assertEquals("abcdefg", StrUtil.abbreviate("abcdefg", 7));
            assertEquals("abcdefg", StrUtil.abbreviate("abcdefg", 8));
        }

        @Test
        @DisplayName("Должен корректно дополнять строку слева")
        void shouldLeftPadString() {
            assertEquals("00123", StrUtil.leftPad("123", 5, '0'));
            assertEquals("  abc", StrUtil.leftPad("abc", 5, ' '));
            assertEquals("abc", StrUtil.leftPad("abc", 3, '0'));
        }

        @Test
        @DisplayName("Должен делать первую букву заглавной")
        void shouldCapitalizeString() {
            assertEquals("Hello", StrUtil.capitalize("hello"));
            assertEquals("World", StrUtil.capitalize("World"));
            assertEquals(" Test", StrUtil.capitalize(" test"));
        }
    }

    @Nested
    @DisplayName("Очистка и замена")
    class CleaningAndReplacingTests {
        @ParameterizedTest
        @CsvSource({"N/A, ''", "n/a, ''", "some text, some text", "null, ''"})
        @DisplayName("Должен заменять 'N/A' и null на пустую строку")
        void shouldReplaceNaWithEmpty(String input, String expected) {
            if ("null".equals(input)) input = null;
            assertEquals(expected, StrUtil.replaceNaWithEmpty(input));
        }

        @Test
        @DisplayName("removeWhitespaceChars: удаляет все пробельные символы")
        void shouldRemoveWhitespaceChars() {
            String s = " a \tb\nc  ";
            assertEquals("abc", StrUtil.removeWhitespace(s));

            // blank input
            assertEquals("", StrUtil.removeWhitespace("   "));
            assertEquals("", StrUtil.removeWhitespace(null));
        }

        @Test
        @DisplayName("removeForbiddenXmlChars: удаляет запрещённые XML‑символы")
        void shouldRemoveForbiddenXmlChars() {
            // \u0000 и \u001F — запрещённые в XML 1.0
            String s = "a\u0000b\u001Fc";
            assertEquals("abc", StrUtil.removeForbiddenXmlChars(s));

            assertEquals("", StrUtil.removeForbiddenXmlChars(null));
        }
    }

    @Nested
    @DisplayName("Извлечение чисел")
    class NumericExtractionTests {
        @ParameterizedTest
        @CsvSource({
                "'Price is 42 items', 42",
                "'Temp: -10 C', -10",
                "'Value: 123.45', 123", // Дробная часть отсекается
                "'No numbers here', 0",
                "'' , 0",
                "null, 0"
        })
        @DisplayName("Должен получать первое int из строки или 0")
        void shouldGetNumberFromStr(String input, int expected) {
            if ("null".equals(input)) input = null;
            assertEquals(expected, StrUtil.getNumber(input));
        }

        @ParameterizedTest
        @CsvSource({
                "'Price: 199.99', 199.99",
                "'Temp: -10.5 C', -10.5",
                "'Value: 100', 100.0",
                "'No numbers', 0.0"
        })
        @DisplayName("Должен получать первое double из строки или 0.0")
        void shouldGetDoubleFromStr(String input, double expected) {
            assertEquals(expected, StrUtil.getDoubleFromStr(input), 0.001);
        }

        @Test
        @DisplayName("findFirstNumberAsString: находит строковое представление числа")
        void shouldFindFirstNumberAsString() {
            assertEquals(Optional.of("123.45"), StrUtil.findFirstNumber("x=123.45;"));
            assertEquals(Optional.of("-10"), StrUtil.findFirstNumber("val=-10;"));
            assertTrue(StrUtil.findFirstNumber("nothing").isEmpty());
            assertTrue(StrUtil.findFirstNumber("").isEmpty());
            assertTrue(StrUtil.findFirstNumber(null).isEmpty());
        }
    }

    @Nested
    @DisplayName("Операции с регулярными выражениями")
    class RegexTests {
        @Test
        @DisplayName("Должен находить первое совпадение по regex")
        void shouldGetFirstMatch() {
            String text = "Event: 2024-06-10, Time: 10:30";
            String regex = "\\d{2}:\\d{2}";
            assertEquals(Optional.of("10:30"), StrUtil.getFirstMatch(regex, text));
        }

        @Test
        @DisplayName("Должен возвращать пустой Optional, если совпадений по regex нет")
        void shouldReturnEmptyOptionalForNoMatch() {
            assertTrue(StrUtil.getFirstMatch("\\d+", "abc").isEmpty());
        }

        @Test
        @DisplayName("Должен находить все совпадения по regex")
        void shouldGetAllMatches() {
            String text = "Items: [item1], [item2], [item3]";
            String regex = "\\[(.*?)]";
            assertEquals(
                    List.of("[item1]", "[item2]", "[item3]"),
                    StrUtil.getAllMatches(regex, text)
            );
        }
    }

    @Nested
    @DisplayName("Дополнительные утилиты")
    class ExtraUtilsTests {
        @Test
        @DisplayName("trimTrailingZeros: убирает нулевую дробную часть")
        void shouldTrimTrailingZeros() {
            assertEquals("123", StrUtil.trimTrailingZeros("123.000"));
            assertEquals("123.1", StrUtil.trimTrailingZeros("123.1000"));
            assertEquals("123.45", StrUtil.trimTrailingZeros("123.4500")); // дробная часть не из одних нулей
            assertEquals("abc", StrUtil.trimTrailingZeros("abc"));
            assertEquals("", StrUtil.trimTrailingZeros(""));
            assertNull(StrUtil.trimTrailingZeros(null));
        }
    }
}
