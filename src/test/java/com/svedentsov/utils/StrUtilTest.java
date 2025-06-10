package com.svedentsov.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Утилиты для работы со строками (StrUtil)")
class StrUtilTest {

    @Nested
    @DisplayName("Очистка и замена")
    class CleaningAndReplacingTests {

        @ParameterizedTest(name = "[{index}] Вход: \"{0}\", Ожидание: \"{1}\"")
        @CsvSource({
                "N/A, ''",
                "n/a, ''",
                "NA, NA",
                "Some Text, Some Text"
        })
        @DisplayName("Должен заменять 'N/A' (без учета регистра) на пустую строку")
        void shouldReplaceNaWithEmpty(String input, String expected) {
            assertEquals(expected, StrUtil.replaceNaWithEmpty(input));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Должен корректно обрабатывать null и пустые строки при замене 'N/A'")
        void shouldHandleNullAndEmptyOnReplaceNa(String input) {
            assertEquals(input, StrUtil.replaceNaWithEmpty(input));
        }

        @Test
        @DisplayName("Должен удалять все пробельные символы (пробелы, табы, переносы)")
        void shouldRemoveWhitespaceChars() {
            String input = "  Hello\tWorld\n  ";
            assertEquals("HelloWorld", StrUtil.removeWhitespaceChars(input));
        }

        @Test
        @DisplayName("Должен заменять символ переноса строки (LF) на пробел")
        void shouldReplaceLFtoSpace() {
            String input = "Line1\nLine2\r\nLine3";
            assertEquals("Line1 Line2\r Line3", StrUtil.replaceLFtoSpace(input));
        }

        @Test
        @DisplayName("Должен удалять запрещенные символы XML 1.0")
        void shouldRemoveForbiddenXmlChars() {
            String input = "ValidText\u0009\r\n" + "Invalid\u0000\u000B\uFFFFText";
            String expected = "ValidText\u0009\r\n" + "InvalidText";
            assertEquals(expected, StrUtil.removeForbiddenXmlChars(input));
        }
    }

    @Nested
    @DisplayName("Извлечение данных")
    class ExtractionTests {

        @ParameterizedTest(name = "[{index}] Вход: \"{0}\", Ожидание: \"{1}\"")
        @CsvSource({
                "'Test 123 String 45', '12345'",
                "'NoDigits', ''",
                "'789', '789'"
        })
        @DisplayName("Должен извлекать только цифры")
        void shouldGetDigits(String input, String expected) {
            assertEquals(expected, StrUtil.getDigits(input));
        }

        @ParameterizedTest
        @CsvSource({
                "'Price: 199.99 USD', '199.99'",
                "'Version 2.0', '2.0'",
                "'No numbers', ''"
        })
        @DisplayName("Должен извлекать только цифры и точки")
        void shouldGetDigitsAndDots(String input, String expected) {
            assertEquals(expected, StrUtil.getDigitsAndDots(input));
        }

        @ParameterizedTest
        @CsvSource({
                "'Hello123World!', 'HelloWorld'",
                "'123-456', ''",
                "'JustLetters', 'JustLetters'"
        })
        @DisplayName("Должен извлекать только буквы (a-zA-Z)")
        void shouldGetChars(String input, String expected) {
            assertEquals(expected, StrUtil.getChars(input));
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
            assertEquals("10:30", StrUtil.getFirstMatch(regex, text));
        }

        @Test
        @DisplayName("Должен возвращать null, если совпадений по regex нет")
        void shouldReturnNullForNoMatch() {
            assertNull(StrUtil.getFirstMatch("\\d+", "abc"));
        }

        @Test
        @DisplayName("Должен находить все совпадения по regex")
        void shouldGetAllMatches() {
            String text = "Items: [item1], [item2], [item3]";
            String regex = "\\[(.*?)\\]";
            List<String> expected = List.of("[item1]", "[item2]", "[item3]");
            assertEquals(expected, StrUtil.getAllMatches(regex, text));
        }

        @Test
        @DisplayName("Должен возвращать пустой список, если совпадений по regex нет")
        void shouldReturnEmptyListForNoMatches() {
            assertTrue(StrUtil.getAllMatches("\\d+", "abc").isEmpty());
        }
    }

    @Nested
    @DisplayName("Конвертация в числа")
    class NumericConversionTests {

        @ParameterizedTest
        @CsvSource({
                "'Value is 42', 42",
                "'No numbers here', 0",
                "'' , 0"
        })
        @DisplayName("Должен получать int из строки или 0")
        void shouldGetNumberFromStr(String input, int expected) {
            assertEquals(expected, StrUtil.getNumberFromStr(input));
        }

        @ParameterizedTest
        @CsvSource({
                "'Price: 199.99', 199.99",
                "'Temp: -10.5 C', 10.5", // getDigitsAndDots удалит минус
                "'Value: 100', 100.0",
                "'No numbers', 0.0"
        })
        @DisplayName("Должен получать double из строки или 0.0")
        void shouldGetDoubleFromStr(String input, double expected) {
            assertEquals(expected, StrUtil.getDoubleFromStr(input), 0.001);
        }

        @ParameterizedTest
        @CsvSource({
                "123.45, 123.45",
                "789, 789.0",
                "not a number, 0.0"
        })
        @DisplayName("Должен конвертировать строку в Double или возвращать 0.0")
        void shouldConvertToDouble(String input, double expected) {
            assertEquals(expected, StrUtil.convertToDouble(input), 0.001);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Должен возвращать 0.0 при конвертации null или пустой строки в Double")
        void shouldReturnZeroForNullOrEmptyOnConvertToDouble(String input) {
            assertEquals(0.0, StrUtil.convertToDouble(input));
        }
    }

    @Nested
    @DisplayName("Манипуляции со строками")
    class StringManipulationTests {
        @ParameterizedTest
        @ValueSource(strings = {"125.00", "Text 99.00"})
        @DisplayName("Должен удалять '.00' в конце строки")
        void shouldTrimZeros(String input) {
            String expected = input.replace(".00", "");
            assertEquals(expected, StrUtil.trimZeros(input));
        }

        @Test
        @DisplayName("Не должен изменять строку, если она не заканчивается на '.00'")
        void shouldNotTrimZerosIfNotPresent() {
            String input = "125.50";
            assertEquals(input, StrUtil.trimZeros(input));
        }

        @Test
        @DisplayName("Должен удалять последний символ, если он совпадает с заданным")
        void shouldDeleteLastSymbolIfPresent() {
            assertEquals("test", StrUtil.deleteLastSymbolIfPresent("test;", ";"));
        }

        @Test
        @DisplayName("Не должен изменять строку, если последний символ не совпадает")
        void shouldNotDeleteLastSymbolIfNotPresent() {
            String input = "test;";
            assertEquals(input, StrUtil.deleteLastSymbolIfPresent(input, ","));
        }
    }
}
