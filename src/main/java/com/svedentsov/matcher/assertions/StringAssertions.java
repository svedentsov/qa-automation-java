package com.svedentsov.matcher.assertions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svedentsov.matcher.Condition;
import org.assertj.core.api.Assertions;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

public class StringAssertions {

    /**
     * Функциональный интерфейс для проверки строк.
     */
    @FunctionalInterface
    public interface StringCondition extends Condition<String> {
    }

    /**
     * Строка содержит заданный текст.
     *
     * @param text текст, который должна содержать строка
     * @return условие проверки на содержание подстроки
     */
    public static StringCondition contains(String text) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна содержать %s", text)
                .contains(text);
    }

    /**
     * Строка содержит заданный текст без учёта регистра.
     *
     * @param text текст для проверки
     * @return условие проверки на содержание подстроки без учёта регистра
     */
    public static StringCondition containsIgnoreCase(String text) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна содержать %s (без учёта регистра)", text)
                .containsIgnoringCase(text);
    }

    /**
     * Строка начинается с указанного префикса.
     *
     * @param prefix префикс, с которого должна начинаться строка
     * @return условие проверки начала строки
     */
    public static StringCondition startsWith(String prefix) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна начинаться с %s", prefix)
                .startsWith(prefix);
    }

    /**
     * Строка начинается с указанного префикса без учёта регистра.
     *
     * @param prefix префикс, с которого должна начинаться строка
     * @return условие проверки начала строки без учёта регистра
     */
    public static StringCondition startsWithIgnoreCase(String prefix) {
        return value -> Assertions.assertThat(value.toLowerCase(Locale.ROOT))
                .as("Строка должна начинаться с %s (без учёта регистра)", prefix)
                .startsWith(prefix.toLowerCase(Locale.ROOT));
    }

    /**
     * Строка заканчивается указанным суффиксом.
     *
     * @param suffix суффикс, которым должна заканчиваться строка
     * @return условие проверки конца строки
     */
    public static StringCondition endsWith(String suffix) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна заканчиваться на %s", suffix)
                .endsWith(suffix);
    }

    /**
     * Строка заканчивается указанным суффиксом без учёта регистра.
     *
     * @param suffix суффикс, которым должна заканчиваться строка
     * @return условие проверки конца строки без учёта регистра
     */
    public static StringCondition endsWithIgnoreCase(String suffix) {
        return value -> Assertions.assertThat(value.toLowerCase(Locale.ROOT))
                .as("Строка должна заканчиваться на %s (без учёта регистра)", suffix)
                .endsWith(suffix.toLowerCase(Locale.ROOT));
    }

    /**
     * Строка соответствует заданному регулярному выражению.
     *
     * @param regex регулярное выражение
     * @return условие проверки соответствия строки регулярному выражению
     */
    public static StringCondition matchesRegex(String regex) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна соответствовать рег. выражению %s", regex)
                .matches(Pattern.compile(regex));
    }

    /**
     * Строка пуста.
     *
     * @return условие проверки пустоты строки
     */
    public static StringCondition isEmpty() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна быть пустой")
                .isEmpty();
    }

    /**
     * Строка не пуста.
     *
     * @return условие проверки, что строка не пустая
     */
    public static StringCondition isNotEmpty() {
        return value -> Assertions.assertThat(value)
                .as("Строка не должна быть пустой")
                .isNotEmpty();
    }

    /**
     * Строка состоит только из цифр.
     *
     * @return условие проверки, что строка содержит только цифры
     */
    public static StringCondition isDigitsOnly() {
        return value -> Assertions.assertThat(value)
                .as("Строка %s должна содержать только цифры", value)
                .matches("\\d+");
    }

    /**
     * Строка равна ожидаемой.
     *
     * @param expected ожидаемое значение строки
     */
    public static StringCondition equalTo(String expected) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна быть равна %s", expected)
                .isEqualTo(expected);
    }


    /**
     * Строка равна ожидаемой без учёта регистра.
     *
     * @param expected ожидаемое значение строки
     * @return условие проверки равенства строк без учёта регистра
     */
    public static StringCondition equalsIgnoreCase(String expected) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна быть равна %s (без учёта регистра)", expected)
                .isEqualToIgnoringCase(expected);
    }

    /**
     * Длина строки равна ожидаемой.
     *
     * @param expectedLength ожидаемая длина строки
     * @return условие проверки длины строки
     */
    public static StringCondition hasLength(int expectedLength) {
        return value -> Assertions.assertThat(value)
                .as("Длина строки должна быть равна %d", expectedLength)
                .hasSize(expectedLength);
    }

    /**
     * Длина строки не меньше заданного значения.
     *
     * @param minLength минимальная длина строки
     * @return условие проверки минимальной длины строки
     */
    public static StringCondition hasMinLength(int minLength) {
        return value -> Assertions.assertThat(value)
                .as("Длина строки должна быть не меньше %d", minLength)
                .hasSizeGreaterThanOrEqualTo(minLength);
    }

    /**
     * Длина строки не превышает заданное значение.
     *
     * @param maxLength максимальная длина строки
     * @return условие проверки максимальной длины строки
     */
    public static StringCondition hasMaxLength(int maxLength) {
        return value -> Assertions.assertThat(value)
                .as("Длина строки должна быть не больше %d", maxLength)
                .hasSizeLessThanOrEqualTo(maxLength);
    }

    /**
     * Длина строки больше заданного значения.
     *
     * @param length значение, которое длина строки должна превышать
     * @return условие проверки, что длина строки больше заданного значения
     */
    public static StringCondition hasLengthGreaterThan(int length) {
        return value -> Assertions.assertThat(value)
                .as("Длина строки должна быть больше %d", length)
                .hasSizeGreaterThan(length);
    }

    /**
     * Длина строки меньше заданного значения.
     *
     * @param length значение, которое длина строки должна быть меньше
     * @return условие проверки, что длина строки меньше заданного значения
     */
    public static StringCondition hasLengthLessThan(int length) {
        return value -> Assertions.assertThat(value)
                .as("Длина строки должна быть меньше %d", length)
                .hasSizeLessThan(length);
    }

    /**
     * Длина строки находится в заданном диапазоне (включительно).
     *
     * @param minLength минимальная длина строки
     * @param maxLength максимальная длина строки
     * @return условие проверки диапазона длины строки
     */
    public static StringCondition hasLengthBetween(int minLength, int maxLength) {
        return value -> Assertions.assertThat(value)
                .as("Длина строки должна быть между %d и %d (включительно)", minLength, maxLength)
                .hasSizeBetween(minLength, maxLength);
    }

    /**
     * Длина строки находится в заданном диапазоне (исключительно).
     *
     * @param minLength минимальная длина строки (не включительно)
     * @param maxLength максимальная длина строки (не включительно)
     * @return условие проверки диапазона длины строки (исключительно)
     */
    public static StringCondition hasLengthBetweenExclusive(int minLength, int maxLength) {
        return value -> Assertions.assertThat(value)
                .as("Длина строки должна быть больше %d и меньше %d", minLength, maxLength)
                .hasSizeGreaterThan(minLength)
                .hasSizeLessThan(maxLength);
    }

    /**
     * Строка пустая или состоит только из пробелов.
     */
    public static StringCondition isBlank() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна быть пустой или состоять только из пробелов")
                .isBlank();
    }

    /**
     * Строка не пустая и не состоит только из пробелов.
     */
    public static StringCondition isNotBlank() {
        return value -> Assertions.assertThat(value)
                .as("Строка не должна быть пустой или состоять только из пробелов")
                .isNotBlank();
    }

    /**
     * Строка содержит хотя бы один непробельный символ.
     *
     * @return условие проверки, что строка содержит текст
     */
    public static StringCondition hasNonBlankContent() {
        return value -> Assertions.assertThat(value.trim())
                .as("Строка должна содержать непустой текст")
                .isNotEmpty();
    }

    /**
     * Строка состоит только из букв.
     *
     * @return условие проверки, что строка содержит только буквы
     */
    public static StringCondition isAlphabetic() {
        return value -> Assertions.assertThat(value)
                .as("Строка %s должна содержать только буквы (a-z, A-Z)", value)
                .matches("[a-zA-Z]+");
    }

    /**
     * Строка состоит только из букв и цифр.
     *
     * @return условие проверки, что строка содержит только буквы и цифры
     */
    public static StringCondition isAlphanumeric() {
        return value -> Assertions.assertThat(value)
                .as("Строка %s должна содержать только буквы и цифры", value)
                .matches("[a-zA-Z0-9]+");
    }

    /**
     * Строка является корректным адресом электронной почты.
     *
     * @return условие проверки адреса электронной почты
     */
    public static StringCondition isValidEmail() {
        return value -> Assertions.assertThat(value)
                .as("Строка %s должна быть корректным адресом электронной почты", value)
                .matches("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Строка является корректным URL.
     *
     * @return условие проверки URL
     */
    public static StringCondition isValidUrl() {
        return value -> {
            try {
                new URL(value);
                Assertions.assertThat(true)
                        .as("Строка %s должна быть корректным URL", value)
                        .isTrue();
            } catch (MalformedURLException e) {
                Assertions.fail("Строка %s не является корректным URL: %s", value, e.getMessage());
            }
        };
    }

    /**
     * Строка полностью записана заглавными буквами.
     *
     * @return условие проверки, что строка в верхнем регистре
     */
    public static StringCondition isUpperCase() {
        return value -> Assertions.assertThat(value)
                .as("Строка %s должна быть записана заглавными буквами", value)
                .isEqualTo(value.toUpperCase(Locale.ROOT));
    }

    /**
     * Строка полностью записана строчными буквами.
     *
     * @return условие проверки, что строка в нижнем регистре
     */
    public static StringCondition isLowerCase() {
        return value -> Assertions.assertThat(value)
                .as("Строка %s должна быть записана строчными буквами", value)
                .isEqualTo(value.toLowerCase(Locale.ROOT));
    }

    /**
     * Строка начинается и заканчивается заданной подстрокой.
     *
     * @param wrapper подстрока, которой должна начинаться и заканчиваться строка
     * @return условие проверки обрамления строки
     */
    public static StringCondition startsAndEndsWith(String wrapper) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна начинаться и заканчиваться на %s", wrapper)
                .startsWith(wrapper)
                .endsWith(wrapper);
    }

    /**
     * Строка содержит ожидаемое количество слов.
     *
     * @param expectedCount ожидаемое количество слов
     * @return условие проверки количества слов в строке
     */
    public static StringCondition hasWordCount(int expectedCount) {
        return value -> Assertions.assertThat(value.trim().split("\\s+"))
                .as("Строка должна содержать %d слов", expectedCount)
                .hasSize(expectedCount);
    }

    /**
     * Строка содержит заданное слово.
     *
     * @param word слово, которое должна содержать строка
     * @return условие проверки на содержание слова
     */
    public static StringCondition hasWord(String word) {
        return value -> Assertions.assertThat(value.trim().split("\\s+"))
                .as("Строка должна содержать слово %s", word)
                .contains(word);
    }

    /**
     * Строка содержит заданное слово без учёта регистра.
     *
     * @param word слово для проверки
     * @return условие проверки на содержание слова без учёта регистра
     */
    public static StringCondition hasWordIgnoreCase(String word) {
        return value -> Assertions.assertThat(Arrays.asList(value.trim().split("\\s+")))
                .as("Строка должна содержать слово %s (без учёта регистра)", word)
                .anyMatch(actualWord -> actualWord.equalsIgnoreCase(word));
    }

    /**
     * Строка соответствует заданному шаблону (Pattern).
     *
     * @param pattern шаблон для проверки
     * @return условие проверки соответствия строки заданному шаблону
     */
    public static StringCondition matchesPattern(Pattern pattern) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна соответствовать шаблону %s", pattern)
                .matches(pattern);
    }

    /**
     * Строка является палиндромом.
     */
    public static StringCondition isPalindrome() {
        return value -> {
            String cleaned = value.replaceAll("\\s+", "").toLowerCase();
            String reversed = new StringBuilder(cleaned).reverse().toString();
            Assertions.assertThat(cleaned)
                    .as("Строка %s должна быть палиндромом", value)
                    .isEqualTo(reversed);
        };
    }

    /**
     * Строка является корректным XML.
     *
     * @return условие проверки на XML
     */
    public static StringCondition isXml() {
        return value -> {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
                InputSource is = new InputSource(new StringReader(value));
                Document doc = db.parse(is);
                Assertions.assertThat(true)
                        .as("Строка %s должна быть корректным XML", value)
                        .isTrue();
            } catch (Exception e) {
                Assertions.fail("Строка %s не является корректным XML: %s", value, e.getMessage());
            }
        };
    }

    /**
     * Строка является десятичным числом.
     *
     * @return условие проверки на десятичное число
     */
    public static StringCondition isDecimal() {
        return value -> {
            try {
                Double.parseDouble(value);
                Assertions.assertThat(true)
                        .as("Строка %s должна быть десятичным числом", value)
                        .isTrue();
            } catch (NumberFormatException e) {
                Assertions.fail("Строка %s не является десятичным числом: %s", value, e.getMessage());
            }
        };
    }

    /**
     * Строка содержит только указанные символы.
     *
     * @param allowedCharacters строка с разрешенными символами
     * @return условие проверки на содержание только разрешенных символов
     */
    public static StringCondition containsOnlyCharacters(String allowedCharacters) {
        return value -> {
            for (char c : value.toCharArray()) {
                if (allowedCharacters.indexOf(c) == -1) {
                    Assertions.fail("Строка %s содержит недопустимый символ '%c'. Разрешены только %s", value, c, allowedCharacters);
                    return;
                }
            }
            Assertions.assertThat(true)
                    .as("Строка %s содержит только разрешенные символы %s", value, allowedCharacters)
                    .isTrue();
        };
    }

    /**
     * Строка не содержит указанные символы.
     *
     * @param disallowedCharacters строка с запрещенными символами
     * @return условие проверки на отсутствие запрещенных символов
     */
    public static StringCondition doesNotContainCharacters(String disallowedCharacters) {
        return value -> {
            for (char c : value.toCharArray()) {
                if (disallowedCharacters.indexOf(c) != -1) {
                    Assertions.fail("Строка %s содержит запрещенный символ '%c'. Запрещены %s", value, c, disallowedCharacters);
                    return;
                }
            }
            Assertions.assertThat(true)
                    .as("Строка %s не содержит запрещенные символы %s", value, disallowedCharacters)
                    .isTrue();
        };
    }

    /**
     * Строка является шестнадцатеричным числом.
     *
     * @return условие проверки на шестнадцатеричное число
     */
    public static StringCondition isHexadecimal() {
        return value -> Assertions.assertThat(value)
                .as("Строка %s должна быть шестнадцатеричным числом", value)
                .matches("^[0-9a-fA-F]+$");
    }

    /**
     * Строка является двоичным числом.
     *
     * @return условие проверки на двоичное число
     */
    public static StringCondition isBinary() {
        return value -> Assertions.assertThat(value)
                .as("Строка %s должна быть двоичным числом", value)
                .matches("^[01]+$");
    }

    /**
     * Строка является валидным UUID.
     */
    public static StringCondition isValidUUID() {
        Pattern UUID_REGEX = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[4][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$");
        return value -> Assertions.assertThat(UUID_REGEX.matcher(value).matches())
                .as("Строка %s должна быть валидным UUID", value)
                .isTrue();
    }

    /**
     * Строка является корректным UUID.
     *
     * @return условие проверки на UUID
     */
    public static StringCondition isValidUuid() {
        return value -> {
            try {
                UUID.fromString(value);
                Assertions.assertThat(true)
                        .as("Строка %s должна быть корректным UUID", value)
                        .isTrue();
            } catch (IllegalArgumentException e) {
                Assertions.fail("Строка %s не является корректным UUID: %s", value, e.getMessage());
            }
        };
    }

    /**
     * Строка является корректным UUID без дефисов.
     *
     * @return условие проверки на UUID без дефисов
     */
    public static StringCondition isValidUuidWithoutHyphens() {
        return value -> {
            try {
                UUID.fromString(value.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{12})", "$1-$2-$3-$4-$5"));
                Assertions.assertThat(true)
                        .as("Строка %s должна быть корректным UUID без дефисов", value)
                        .isTrue();
            } catch (IllegalArgumentException e) {
                Assertions.fail("Строка %s не является корректным UUID без дефисов: %s", value, e.getMessage());
            }
        };
    }

    /**
     * Строка содержит указанное количество строк (разделитель - символ новой строки '\n').
     *
     * @param expectedCount ожидаемое количество строк
     * @return условие проверки количества строк
     */
    public static StringCondition hasLineCount(int expectedCount) {
        return value -> Assertions.assertThat(value.split("\n"))
                .as("Строка должна содержать %d строк", expectedCount)
                .hasSize(expectedCount);
    }

    /**
     * Строка содержит заданное количество вхождений подстроки.
     *
     * @param text  подстрока для поиска
     * @param count ожидаемое количество вхождений
     * @return условие проверки количества вхождений подстроки
     */
    public static StringCondition hasOccurrences(String text, int count) {
        return value -> {
            int occurrences = 0;
            int index = 0;
            while ((index = value.indexOf(text, index)) != -1) {
                occurrences++;
                index += text.length();
            }
            Assertions.assertThat(occurrences)
                    .as("Строка должна содержать %s %d раз(а)", text, count)
                    .isEqualTo(count);
        };
    }

    /**
     * Строка содержит хотя бы одну цифру.
     *
     * @return условие проверки наличия цифры
     */
    public static StringCondition containsDigit() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна содержать хотя бы одну цифру")
                .matches(".*\\d.*");
    }

    /**
     * Строка содержит хотя бы одну букву.
     *
     * @return условие проверки наличия буквы
     */
    public static StringCondition containsLetter() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна содержать хотя бы одну букву")
                .matches(".*[a-zA-Z].*");
    }

    /**
     * Строка содержит хотя бы один символ в верхнем регистре.
     *
     * @return условие проверки наличия символа в верхнем регистре
     */
    public static StringCondition containsUpperCase() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна содержать хотя бы один символ в верхнем регистре")
                .matches(".*[A-Z].*");
    }

    /**
     * Строка содержит хотя бы один символ в нижнем регистре.
     *
     * @return условие проверки наличия символа в нижнем регистре
     */
    public static StringCondition containsLowerCase() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна содержать хотя бы один символ в нижнем регистре")
                .matches(".*[a-z].*");
    }

    /**
     * Строка содержит хотя бы один пробельный символ.
     *
     * @return условие проверки наличия пробельного символа
     */
    public static StringCondition containsWhitespace() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна содержать хотя бы один пробельный символ")
                .matches(".*\\s.*");
    }

    /**
     * Строка не содержит пробельных символов.
     *
     * @return условие проверки отсутствия пробельных символов
     */
    public static StringCondition containsNoWhitespace() {
        return value -> Assertions.assertThat(value)
                .as("Строка не должна содержать пробельных символов")
                .doesNotContainPattern("\\s");
    }

    /**
     * Строка является корректным номером телефона (простая проверка формата).
     *
     * @return условие проверки номера телефона
     */
    public static StringCondition isValidPhoneNumber() {
        return value -> Assertions.assertThat(value)
                .as("Строка %s должна быть корректным номером телефона", value)
                .matches("^\\+?\\d{1,}([- ]?\\d{1,})*$");
    }

    /**
     * Строка является корректным IP-адресом (версии 4).
     *
     * @return условие проверки IP-адреса
     */
    public static StringCondition isValidIpAddress() {
        return value -> Assertions.assertThat(value)
                .as("Строка %s должна быть корректным IP-адресом", value)
                .matches("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
    }

    /**
     * Строка является корректным MAC-адресом.
     *
     * @return условие проверки MAC-адреса
     */
    public static StringCondition isValidMacAddress() {
        return value -> Assertions.assertThat(value)
                .as("Строка %s должна быть корректным MAC-адресом", value)
                .matches("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
    }

    /**
     * Строка представляет собой дату в формате ISO 8601 (YYYY-MM-DD).
     *
     * @return условие проверки формата даты ISO 8601
     */
    public static StringCondition isIso8601Date() {
        return value -> Assertions.assertThat(value)
                .as("Строка %s должна быть датой в формате ISO 8601 (YYYY-MM-DD)", value)
                .matches("^\\d{4}-\\d{2}-\\d{2}$");
    }

    /**
     * Строка представляет собой дату и время в формате ISO 8601 (YYYY-MM-DDTHH:mm:ssZ).
     *
     * @return условие проверки формата даты и времени ISO 8601
     */
    public static StringCondition isIso8601DateTime() {
        return value -> Assertions.assertThat(value)
                .as("Строка %s должна быть датой и временем в формате ISO 8601 (YYYY-MM-DDTHH:mm:ssZ)", value)
                .matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(Z|[+-]\\d{2}:\\d{2})$");
    }

    /**
     * Строка является корректным временем в формате HH:mm:ss.
     *
     * @return условие проверки формата времени HH:mm:ss
     */
    public static StringCondition isValidTime() {
        return value -> {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalTime.parse(value, formatter);
                Assertions.assertThat(true)
                        .as("Строка %s должна быть корректным временем в формате HH:mm:ss", value)
                        .isTrue();
            } catch (DateTimeParseException e) {
                Assertions.fail("Строка %s не является корректным временем в формате HH:mm:ss: %s", value, e.getMessage());
            }
        };
    }

    /**
     * Строка содержит только заданные символы (альтернативный вариант).
     *
     * @param allowedCharacters строка с разрешенными символами
     * @return условие проверки на содержание только разрешенных символов
     */
    public static StringCondition containsOnly(String allowedCharacters) {
        return value -> Assertions.assertThat(value)
                .as("Строка %s должна содержать только символы из %s", value, allowedCharacters)
                .matches("[" + Pattern.quote(allowedCharacters) + "]+");
    }

    /**
     * Строка является корректным цветом в HEX-формате (например, #RRGGBB).
     *
     * @return условие проверки HEX-цвета
     */
    public static StringCondition isValidHexColor() {
        return value -> Assertions.assertThat(value)
                .as("Строка %s должна быть корректным цветом в HEX-формате (#RRGGBB)", value)
                .matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
    }

    /**
     * Строка содержит заданное количество символов.
     *
     * @param character символ для поиска
     * @param count     ожидаемое количество вхождений символа
     * @return условие проверки количества вхождений символа
     */
    public static StringCondition hasCharacterCount(char character, int count) {
        return value -> {
            long actualCount = value.chars().filter(ch -> ch == character).count();
            Assertions.assertThat(actualCount)
                    .as("Строка должна содержать символ '%c' %d раз(а)", character, count)
                    .isEqualTo(count);
        };
    }

    /**
     * Строка начинается с цифры.
     *
     * @return условие проверки начала с цифры
     */
    public static StringCondition startsWithDigit() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна начинаться с цифры")
                .matches("^\\d.*");
    }

    /**
     * Строка заканчивается цифрой.
     *
     * @return условие проверки окончания на цифру
     */
    public static StringCondition endsWithDigit() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна заканчиваться цифрой")
                .matches(".*\\d$");
    }

    /**
     * Строка начинается с буквы.
     *
     * @return условие проверки начала с буквы
     */
    public static StringCondition startsWithLetter() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна начинаться с буквы")
                .matches("^[a-zA-Z].*");
    }

    /**
     * Строка заканчивается буквой.
     *
     * @return условие проверки окончания на букву
     */
    public static StringCondition endsWithLetter() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна заканчиваться буквой")
                .matches(".*[a-zA-Z]$");
    }

    /**
     * Строка содержит последовательность цифр.
     *
     * @return условие проверки наличия последовательности цифр
     */
    public static StringCondition containsDigits() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна содержать последовательность цифр")
                .matches(".*\\d+.*");
    }

    /**
     * Строка содержит последовательность букв.
     *
     * @return условие проверки наличия последовательности букв
     */
    public static StringCondition containsLetters() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна содержать последовательность букв")
                .matches(".*[a-zA-Z]+.*");
    }

    /**
     * Строка представляет собой логическое значение (true или false, без учёта регистра).
     *
     * @return условие проверки на логическое значение
     */
    public static StringCondition isBoolean() {
        return value -> Assertions.assertThat(value.toLowerCase(Locale.ROOT))
                .as("Строка %s должна представлять собой логическое значение (true или false)", value)
                .isIn("true", "false");
    }

    /**
     * Строка содержит только уникальные символы.
     *
     * @return условие проверки на уникальность символов
     */
    public static StringCondition containsOnlyUniqueCharacters() {
        return value -> Assertions.assertThat(value.chars().distinct().count())
                .as("Строка %s должна содержать только уникальные символы", value)
                .isEqualTo(value.length());
    }

    /**
     * Значение является строкой.
     */
    public static StringCondition isString() {
        return value -> {
            Assertions.assertThat(value)
                    .as("Значение должно быть строкой")
                    .isInstanceOf(String.class);
        };
    }

    /**
     * Значение не равно null.
     */
    public static StringCondition isNotNull() {
        return value -> Assertions.assertThat(value)
                .as("Не должно быть null")
                .isNotNull();
    }

    /**
     * Строка не пуста и не состоит только из пробелов.
     */
    public static StringCondition isNonBlank() {
        return value -> Assertions.assertThat(value)
                .as("Строка не должна быть пустой или состоять только из пробелов")
                .isNotBlank();
    }

    /**
     * Длина строки равна указанному числу.
     *
     * @param length ожидаемая длина строки
     */
    public static StringCondition lengthEqualTo(int length) {
        return value -> Assertions.assertThat(value)
                .as("Длина строки должна быть равна %d", length)
                .hasSize(length);
    }

    /**
     * Длина строки больше указанного значения.
     *
     * @param length минимальная длина строки
     */
    public static StringCondition lengthGreaterThan(int length) {
        return value -> Assertions.assertThat(value.length())
                .as("Длина строки должна быть > %d", length)
                .isGreaterThan(length);
    }

    /**
     * Длина строки меньше указанного значения.
     *
     * @param length максимальная длина строки
     */
    public static StringCondition lengthLessThan(int length) {
        return value -> Assertions.assertThat(value.length())
                .as("Длина строки должна быть < %d", length)
                .isLessThan(length);
    }

    /**
     * Строка имеет длину в заданном диапазоне [min, max].
     *
     * @param min минимальная длина
     * @param max максимальная длина
     */
    public static StringCondition lengthBetweenStr(int min, int max) {
        return value -> Assertions.assertThat(value.length())
                .as("Длина строки должна быть в диапазоне [%d, %d]", min, max)
                .isBetween(min, max);
    }

    /**
     * Строка не превышает заданного размера (количество символов).
     *
     * @param maxSize максимальный размер (количество символов)
     */
    public static StringCondition maxSize(int maxSize) {
        return value -> Assertions.assertThat(value.length())
                .as("Ожидалось, что длина строки не превышает %d, но фактически %d", maxSize, value.length())
                .isLessThanOrEqualTo(maxSize);
    }

    /**
     * Строка не короче указанного размера.
     *
     * @param minSize минимальный размер (количество символов)
     */
    public static StringCondition minSize(int minSize) {
        return value -> Assertions.assertThat(value.length())
                .as("Ожидалось, что длина строки не меньше %d, но фактически %d", minSize, value.length())
                .isGreaterThanOrEqualTo(minSize);
    }

    /**
     * Строка не превышает указанное количество строк (разделитель - перевод строки).
     *
     * @param maxLines максимально допустимое количество строк
     */
    public static StringCondition maxLines(int maxLines) {
        return value -> {
            long count = value.lines().count();
            Assertions.assertThat(count)
                    .as("Ожидалось, что строка содержит не более %d строк, но фактически %d", maxLines, count)
                    .isLessThanOrEqualTo(maxLines);
        };
    }

    /**
     * Строка не содержит указанный текст.
     *
     * @param text подстрока, которой не должно быть в строке
     */
    public static StringCondition doesNotContain(String text) {
        return value -> Assertions.assertThat(value)
                .as("Строка не должна содержать %s", text)
                .doesNotContain(text);
    }

    /**
     * Строка содержит указанный текст без учета регистра.
     *
     * @param text текст для поиска
     */
    public static StringCondition containsIgnoringCase(String text) {
        return value -> Assertions.assertThat(value.toLowerCase())
                .as("Строка должна содержать %s без учета регистра", text)
                .contains(text.toLowerCase());
    }

    /**
     * Строка содержит хотя бы одну из указанных подстрок.
     *
     * @param texts подстроки для поиска
     */
    public static StringCondition containsAny(String... texts) {
        return value -> {
            boolean found = Arrays.stream(texts).anyMatch(value::contains);
            Assertions.assertThat(found)
                    .as("Строка должна содержать хотя бы один из %s", List.of(texts))
                    .isTrue();
        };
    }

    /**
     * Строка содержит все указанные подстроки.
     *
     * @param texts подстроки для поиска
     */
    public static StringCondition containsAll(String... texts) {
        return value -> Arrays.stream(texts).forEach(
                text -> Assertions.assertThat(value)
                        .as("Строка должна содержать %s", text)
                        .contains(text)
        );
    }

    /**
     * Строка не содержит ни одной из указанных подстрок.
     *
     * @param texts подстроки, которых не должно быть
     */
    public static StringCondition containsNone(String... texts) {
        return value -> {
            boolean foundAny = Arrays.stream(texts).anyMatch(value::contains);
            Assertions.assertThat(foundAny)
                    .as("Строка не должна содержать ни одну из подстрок %s", List.of(texts))
                    .isFalse();
        };
    }

    /**
     * Строка содержит все гласные буквы.
     */
    public static StringCondition containsAllVowels() {
        return value -> {
            List<Character> vowels = Arrays.asList('a', 'e', 'i', 'o', 'u', 'а', 'е', 'и', 'о', 'у', 'э', 'ю', 'я');
            boolean containsAll = vowels.stream().allMatch(v -> value.toLowerCase().indexOf(v) >= 0);
            Assertions.assertThat(containsAll)
                    .as("Строка %s должна содержать все гласные буквы", value)
                    .isTrue();
        };
    }

    /**
     * Строка содержит заданные слова в указанном порядке.
     * Между словами может быть любой текст (многострочный тоже).
     *
     * @param words слова для поиска в порядке следования
     */
    public static StringCondition wordsOrder(String... words) {
        return value -> {
            String patternString = String.join(".*?", words);
            Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
            Assertions.assertThat(pattern.matcher(value).find())
                    .as("Строка должна содержать слова в порядке: %s", List.of(words))
                    .isTrue();
        };
    }

    /**
     * Строка содержит слова в обратном порядке относительно заданного списка.
     *
     * @param words слова для поиска в обратном порядке
     */
    public static StringCondition wordsReverseOrder(String... words) {
        return value -> {
            String[] reversedWords = Arrays.copyOf(words, words.length);
            Collections.reverse(Arrays.asList(reversedWords));
            String patternString = String.join(".*?", reversedWords);
            Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
            Assertions.assertThat(pattern.matcher(value).find())
                    .as("Строка должна содержать слова в обратном порядке: %s", List.of(reversedWords))
                    .isTrue();
        };
    }

    /**
     * Строка начинается и заканчивается с заданных префикса и суффикса.
     *
     * @param prefix префикс
     * @param suffix суффикс
     */
    public static StringCondition startsAndEndsWith(String prefix, String suffix) {
        return value -> {
            Assertions.assertThat(value)
                    .as("Строка должна начинаться с %s", prefix)
                    .startsWith(prefix);
            Assertions.assertThat(value)
                    .as("Строка должна заканчиваться на %s", suffix)
                    .endsWith(suffix);
        };
    }

    /**
     * Строка начинается и заканчивается заданным префиксом/суффиксом, игнорируя регистр.
     *
     * @param prefix префикс
     * @param suffix суффикс
     */
    public static StringCondition startsWithAndEndsWithIgnoreCase(String prefix, String suffix) {
        return value -> {
            String lowerValue = value.toLowerCase();
            Assertions.assertThat(lowerValue)
                    .as("Строка должна начинаться с %s (без учета регистра)", prefix)
                    .startsWith(prefix.toLowerCase());
            Assertions.assertThat(lowerValue)
                    .as("Строка должна заканчиваться на %s (без учета регистра)", suffix)
                    .endsWith(suffix.toLowerCase());
        };
    }

    /**
     * Строка начинается с буквы и заканчивается цифрой.
     */
    public static StringCondition startsWithLetterEndsWithDigit() {
        return value -> {
            boolean startsWithLetter = !value.isEmpty() && Character.isLetter(value.charAt(0));
            boolean endsWithDigit = !value.isEmpty() && Character.isDigit(value.charAt(value.length() - 1));
            Assertions.assertThat(startsWithLetter && endsWithDigit)
                    .as("Строка %s должна начинаться с буквы и заканчиваться цифрой", value)
                    .isTrue();
        };
    }

    /**
     * Строка начинается и заканчивается с цифры.
     */
    public static StringCondition startsAndEndsWithDigit() {
        return value -> {
            boolean startsWithDigit = !value.isEmpty() && Character.isDigit(value.charAt(0));
            boolean endsWithDigit = !value.isEmpty() && Character.isDigit(value.charAt(value.length() - 1));
            Assertions.assertThat(startsWithDigit && endsWithDigit)
                    .as("Строка %s должна начинаться и заканчиваться цифрой", value)
                    .isTrue();
        };
    }

    /**
     * Строка соответствует указанному регулярному выражению без учета регистра.
     *
     * @param regex регулярное выражение
     */
    public static StringCondition matchesCaseInsensitive(String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        return value -> Assertions.assertThat(pattern.matcher(value).matches())
                .as("Строка должна соответствовать рег. выражению %s без учета регистра", regex)
                .isTrue();
    }

    /**
     * Строка соответствует шаблону с использованием шаблона Wildcard (* и ?).
     *
     * @param wildcardPattern шаблон с подстановочными символами
     */
    public static StringCondition matchesWildcardPattern(String wildcardPattern) {
        String regex = "^" + Pattern.quote(wildcardPattern)
                .replace("\\*", ".*")
                .replace("\\?", ".") + "$";
        Pattern pattern = Pattern.compile(regex);
        return value -> Assertions.assertThat(pattern.matcher(value).matches())
                .as("Строка %s должна соответствовать шаблону %s", value, wildcardPattern)
                .isTrue();
    }

    /**
     * Строка содержит заданное количество вхождений подстроки.
     *
     * @param substring   искомая подстрока
     * @param occurrences ожидаемое количество вхождений
     */
    public static StringCondition containsOccurrences(String substring, int occurrences) {
        return value -> {
            int count = 0;
            int idx = 0;
            while ((idx = value.indexOf(substring, idx)) != -1) {
                count++;
                idx += substring.length();
            }
            Assertions.assertThat(count)
                    .as("Ожидалось, что строка содержит %s %d раз, но было %d", substring, occurrences, count)
                    .isEqualTo(occurrences);
        };
    }

    /**
     * Строка содержит подстроку, повторенную заданное количество раз подряд.
     *
     * @param substring   искомая подстрока
     * @param repetitions ожидаемое количество повторений
     */
    public static StringCondition containsRepeatedSubstring(String substring, int repetitions) {
        return value -> {
            String repeated = substring.repeat(repetitions);
            Assertions.assertThat(value)
                    .as("Строка %s должна содержать подстроку %s повторенную %d раз подряд", value, substring, repetitions)
                    .contains(repeated);
        };
    }

    /**
     * Строка содержит заданные подстроки в произвольном порядке и без перекрытий.
     *
     * @param texts подстроки для поиска
     */
    public static StringCondition containsAllSubstringsUnorderedNoOverlap(String... texts) {
        return value -> {
            int currentIndex = 0;
            for (String text : texts) {
                int index = value.indexOf(text, currentIndex);
                Assertions.assertThat(index)
                        .as("Строка %s должна содержать подстроку %s после позиции %d", value, text, currentIndex)
                        .isGreaterThanOrEqualTo(0);
                currentIndex = index + text.length();
            }
        };
    }

    /**
     * Строка содержит заданные подстроки в определенном количестве раз.
     *
     * @param substring искомая подстрока
     * @param count     ожидаемое количество вхождений
     */
    public static StringCondition containsSubstringExactCount(String substring, int count) {
        return value -> {
            long actualCount = value.chars()
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString()
                    .split(Pattern.quote(substring), -1).length - 1;
            Assertions.assertThat(actualCount)
                    .as("Строка %s должна содержать подстроку %s ровно %d раз(а), но содержит %d раз(а)",
                            value, substring, count, actualCount)
                    .isEqualTo(count);
        };
    }

    /**
     * Строка представляет собой валидное число (Integer).
     */
    public static StringCondition isInteger() {
        return value -> {
            try {
                Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new AssertionError(String.format("Ожидалось, что строка %s будет валидным целым числом", value), e);
            }
        };
    }

    /**
     * Строка представляет собой валидное число (Double).
     */
    public static StringCondition isDouble() {
        return value -> {
            try {
                Double.parseDouble(value);
            } catch (NumberFormatException e) {
                throw new AssertionError(String.format("Ожидалось, что строка %s будет валидным числом с плавающей точкой", value), e);
            }
        };
    }

    /**
     * Строка является закодированной Base64.
     */
    public static StringCondition isBase64Encoded() {
        return value -> {
            try {
                Base64.getDecoder().decode(value);
                Assertions.assertThat(true)
                        .as("Строка %s должна быть закодированной Base64", value)
                        .isTrue();
            } catch (IllegalArgumentException e) {
                throw new AssertionError(String.format("Строка %s не является закодированной Base64", value), e);
            }
        };
    }

    /**
     * Строка содержит хотя бы одну цифру и одну букву.
     */
    public static StringCondition containsLetterAndDigit() {
        return value -> {
            boolean hasLetter = value.chars().anyMatch(Character::isLetter);
            boolean hasDigit = value.chars().anyMatch(Character::isDigit);
            Assertions.assertThat(hasLetter && hasDigit)
                    .as("Строка %s должна содержать как минимум одну букву и одну цифру", value)
                    .isTrue();
        };
    }

    /**
     * Строка не содержит цифр.
     */
    public static StringCondition containsNoDigits() {
        return value -> {
            boolean hasDigits = value.chars().anyMatch(Character::isDigit);
            Assertions.assertThat(hasDigits)
                    .as("Строка %s не должна содержать цифр", value)
                    .isFalse();
        };
    }

    /**
     * Строка содержит только буквы из латинского алфавита.
     */
    public static StringCondition isLatinLettersOnly() {
        return value -> {
            boolean isLatin = value.chars().allMatch(ch ->
                    (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')
            );
            Assertions.assertThat(isLatin)
                    .as("Строка %s должна содержать только латинские буквы", value)
                    .isTrue();
        };
    }

    /**
     * Строка содержит хотя бы один специальный символ.
     */
    public static StringCondition containsSpecialCharacter() {
        String SPECIAL_CHARS = "!@#$%^&*()-_=+[]{}|;:'\",.<>/?`~";
        return value -> {
            boolean hasSpecial = value.chars().anyMatch(ch -> SPECIAL_CHARS.indexOf(ch) >= 0);
            Assertions.assertThat(hasSpecial)
                    .as("Строка %s должна содержать хотя бы один специальный символ", value)
                    .isTrue();
        };
    }

    /**
     * Строка содержит заданное количество цифр.
     *
     * @param count ожидаемое количество цифр
     */
    public static StringCondition hasDigits(int count) {
        return value -> {
            long digitCount = value.chars().filter(Character::isDigit).count();
            Assertions.assertThat(digitCount)
                    .as("Строка %s должна содержать %d цифр, но содержит %d", value, count, digitCount)
                    .isEqualTo(count);
        };
    }

    /**
     * Строка содержит заданное количество заглавных букв.
     *
     * @param count ожидаемое количество заглавных букв
     */
    public static StringCondition hasUpperCaseLetters(int count) {
        return value -> {
            long upperCaseCount = value.chars().filter(Character::isUpperCase).count();
            Assertions.assertThat(upperCaseCount)
                    .as("Строка %s должна содержать %d заглавных букв, но содержит %d", value, count, upperCaseCount)
                    .isEqualTo(count);
        };
    }

    /**
     * Строка содержит заданное количество строчных букв.
     *
     * @param count ожидаемое количество строчных букв
     */
    public static StringCondition hasLowerCaseLetters(int count) {
        return value -> {
            long lowerCaseCount = value.chars().filter(Character::isLowerCase).count();
            Assertions.assertThat(lowerCaseCount)
                    .as("Строка %s должна содержать %d строчных букв, но содержит %d", value, count, lowerCaseCount)
                    .isEqualTo(count);
        };
    }

    /**
     * Строка содержит заданное количество специальных символов.
     *
     * @param count ожидаемое количество специальных символов
     */
    public static StringCondition hasSpecialCharacters(int count) {
        return value -> {
            String SPECIAL_CHARS = "!@#$%^&*()-_=+[]{}|;:'\",.<>/?`~";
            long specialCount = value.chars().filter(ch -> SPECIAL_CHARS.indexOf(ch) >= 0).count();
            Assertions.assertThat(specialCount)
                    .as("Строка %s должна содержать %d специальных символов, но содержит %d", value, count, specialCount)
                    .isEqualTo(count);
        };
    }

    /**
     * Строка содержит только уникальные символы.
     */
    public static StringCondition hasAllUniqueCharacters() {
        return value -> {
            long distinctCount = value.chars().distinct().count();
            Assertions.assertThat(distinctCount)
                    .as("Строка %s должна содержать только уникальные символы", value)
                    .isEqualTo(value.length());
        };
    }

    /**
     * Строка содержит только уникальные биграммы (последовательности из двух символов).
     */
    public static StringCondition hasUniqueBigrams() {
        return value -> {
            List<String> bigrams = new java.util.ArrayList<>();
            for (int i = 0; i < value.length() - 1; i++) {
                bigrams.add(value.substring(i, i + 2));
            }
            long distinctCount = bigrams.stream().distinct().count();
            Assertions.assertThat(distinctCount)
                    .as("Строка %s должна содержать только уникальные биграммы", value)
                    .isEqualTo(bigrams.size());
        };
    }

    /**
     * Строка содержит только уникальные триграммы (последовательности из трех символов).
     */
    public static StringCondition hasUniqueTrigrams() {
        return value -> {
            List<String> trigrams = new java.util.ArrayList<>();
            for (int i = 0; i < value.length() - 2; i++) {
                trigrams.add(value.substring(i, i + 3));
            }
            long distinctCount = trigrams.stream().distinct().count();
            Assertions.assertThat(distinctCount)
                    .as("Строка %s должна содержать только уникальные триграммы", value)
                    .isEqualTo(trigrams.size());
        };
    }

    /**
     * В строке все слова (разделяемые пробелами) уникальны.
     */
    public static StringCondition allWordsUnique() {
        return value -> {
            String[] words = value.split("\\s+");
            long distinctCount = Arrays.stream(words).distinct().count();
            Assertions.assertThat(distinctCount)
                    .as("Ожидалось, что все слова строки уникальны, но некоторые повторяются: %s", value)
                    .isEqualTo(words.length);
        };
    }

    /**
     * Строка содержит только уникальные слова независимо от регистра.
     */
    public static StringCondition hasUniqueWordsIgnoreCase() {
        return value -> {
            String[] words = value.toLowerCase().split("\\s+");
            long distinctCount = Arrays.stream(words).distinct().count();
            Assertions.assertThat(distinctCount)
                    .as("Строка %s должна содержать только уникальные слова независимо от регистра", value)
                    .isEqualTo(words.length);
        };
    }

    /**
     * Строка содержит все буквы алфавита хотя бы один раз (панграмма).
     */
    public static StringCondition isPangram() {
        return value -> {
            String lower = value.toLowerCase();
            boolean isPangram = "abcdefghijklmnopqrstuvwxyz".chars().allMatch(ch -> lower.indexOf(ch) >= 0);
            Assertions.assertThat(isPangram)
                    .as("Строка %s должна быть панграммой (содержать все буквы алфавита)", value)
                    .isTrue();
        };
    }

    /**
     * Строка соответствует формату даты по заданному шаблону (DateTimeFormatter).
     *
     * @param pattern шаблон даты, например "yyyy-MM-dd"
     */
    public static StringCondition matchesDateFormat(String pattern) {
        return value -> {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                LocalDate.parse(value, formatter);
                Assertions.assertThat(true)
                        .as("Строка должна соответствовать формату даты %s", pattern)
                        .isTrue();
            } catch (Exception e) {
                throw new AssertionError(String.format("Строка %s не соответствует формату даты %s", value, pattern), e);
            }
        };
    }

    /**
     * Строка соответствует формату времени по заданному шаблону (DateTimeFormatter).
     *
     * @param pattern шаблон времени, например "HH:mm:ss"
     */
    public static StringCondition matchesTimeFormat(String pattern) {
        return value -> {
            try {
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern(pattern);
                java.time.LocalTime.parse(value, formatter);
                Assertions.assertThat(true)
                        .as("Строка должна соответствовать формату времени %s", pattern)
                        .isTrue();
            } catch (Exception e) {
                throw new AssertionError(String.format("Строка %s не соответствует формату времени %s", value, pattern), e);
            }
        };
    }

    /**
     * Строка закодирована в UTF-8.
     */
    public static StringCondition isUtf8Encoded() {
        return value -> {
            byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
            String decoded = new String(bytes, StandardCharsets.UTF_8);
            Assertions.assertThat(decoded)
                    .as("Строка %s должна быть корректно закодирована в UTF-8", value)
                    .isEqualTo(value);
        };
    }

    /**
     * Строка имеет кодировку ASCII (все символы < 128).
     */
    public static StringCondition isAscii() {
        return value -> {
            boolean isAscii = value.chars().allMatch(ch -> ch < 128);
            Assertions.assertThat(isAscii)
                    .as("Ожидалось, что строка %s состоит только из ASCII символов", value)
                    .isTrue();
        };
    }

    /**
     * Строка содержит только символы, представимые в заданной кодировке.
     *
     * @param charsetName название кодировки, например "UTF-8", "ISO-8859-1"
     */
    public static StringCondition isEncodableIn(String charsetName) {
        return value -> {
            Charset charset;
            try {
                charset = Charset.forName(charsetName);
            } catch (Exception e) {
                throw new IllegalArgumentException("Неверное название кодировки: " + charsetName, e);
            }
            boolean isEncodable = value.chars().allMatch(ch -> {
                byte[] bytes = new String(new int[]{ch}, 0, 1).getBytes(charset);
                return bytes.length > 0;
            });
            Assertions.assertThat(isEncodable)
                    .as("Строка %s должна быть кодируема в %s", value, charsetName)
                    .isTrue();
        };
    }

    /**
     * Строка является валидным URL.
     */
    public static StringCondition isValidURL() {
        Pattern URL_REGEX = Pattern.compile("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", Pattern.CASE_INSENSITIVE);
        return value -> Assertions.assertThat(URL_REGEX.matcher(value).matches())
                .as("Строка %s должна быть валидным URL", value)
                .isTrue();
    }

    /**
     * Строка является валидным JSON.
     */
    public static StringCondition isValidJson() {
        return value -> {
            try {
                new ObjectMapper().readTree(value);
                Assertions.assertThat(true)
                        .as("Строка %s должна быть валидным JSON", value)
                        .isTrue();
            } catch (Exception e) {
                throw new AssertionError(String.format("Строка %s не является валидным JSON", value), e);
            }
        };
    }

    /**
     * Строка является валидным XML.
     */
    public static StringCondition isValidXml() {
        return value -> {
            try {
                DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder()
                        .parse(new InputSource(new StringReader(value)));
                Assertions.assertThat(true)
                        .as("Строка %s должна быть валидным XML", value)
                        .isTrue();
            } catch (Exception e) {
                throw new AssertionError(String.format("Строка %s не является валидным XML", value), e);
            }
        };
    }

    /**
     * Строка является валидным идентификатором переменной в Java.
     */
    public static StringCondition isValidJavaIdentifier() {
        return value -> {
            boolean isValid = !value.isEmpty() && Character.isJavaIdentifierStart(value.charAt(0));
            for (int i = 1; i < value.length() && isValid; i++) {
                if (!Character.isJavaIdentifierPart(value.charAt(i))) {
                    isValid = false;
                }
            }
            Assertions.assertThat(isValid)
                    .as("Строка %s должна быть валидным идентификатором Java", value)
                    .isTrue();
        };
    }

    /**
     * Строка не содержит символов табуляции.
     */
    public static StringCondition hasNoTabs() {
        return value -> {
            boolean hasTabs = value.contains("\t");
            Assertions.assertThat(hasTabs)
                    .as("Строка %s не должна содержать символов табуляции", value)
                    .isFalse();
        };
    }

    /**
     * Строка не содержит символов управления (не считая пробелов).
     */
    public static StringCondition hasNoControlCharacters() {
        return value -> {
            boolean hasControl = value.chars().anyMatch(ch -> (ch < 32 && ch != ' ') || ch == 127);
            Assertions.assertThat(hasControl)
                    .as("Строка %s не должна содержать символов управления", value)
                    .isFalse();
        };
    }

    /**
     * Строка не содержит повторяющихся последовательностей символов длины n.
     *
     * @param sequenceLength длина последовательности символов
     */
    public static StringCondition hasNoRepeatedSequences(int sequenceLength) {
        return value -> {
            for (int i = 0; i <= value.length() - sequenceLength * 2; i++) {
                String sequence = value.substring(i, i + sequenceLength);
                String nextSequence = value.substring(i + sequenceLength, i + sequenceLength * 2);
                if (sequence.equals(nextSequence)) {
                    Assertions.fail(String.format("Строка %s содержит повторяющуюся последовательность %s", value, sequence));
                }
            }
        };
    }

    /**
     * Строка не содержит подряд идущих пробелов.
     */
    public static StringCondition hasNoConsecutiveSpaces() {
        return value -> {
            boolean hasConsecutive = value.contains("  ");
            Assertions.assertThat(hasConsecutive)
                    .as("Строка %s не должна содержать подряд идущих пробелов", value)
                    .isFalse();
        };
    }

    /**
     * Строка не содержит подряд идущих одинаковых символов.
     */
    public static StringCondition hasNoConsecutiveDuplicateCharacters() {
        return value -> {
            for (int i = 1; i < value.length(); i++) {
                if (value.charAt(i) == value.charAt(i - 1)) {
                    Assertions.fail(String.format("Строка %s содержит подряд идущие одинаковые символы: '%c'",
                            value, value.charAt(i))
                    );
                }
            }
        };
    }

    /**
     * Строка не содержит подряд идущих букв и цифр.
     */
    public static StringCondition hasNoConsecutiveLettersAndDigits() {
        return value -> {
            for (int i = 1; i < value.length(); i++) {
                char current = value.charAt(i);
                char previous = value.charAt(i - 1);
                if (Character.isLetter(current) && Character.isDigit(previous) ||
                        Character.isDigit(current) && Character.isLetter(previous)) {
                    Assertions.fail(String.format("Строка %s содержит подряд идущие буквы и цифры: '%c%c'",
                            value, previous, current)
                    );
                }
            }
        };
    }

    /**
     * Строка не содержит символов из заданного набора.
     *
     * @param forbiddenChars строка с запрещенными символами
     */
    public static StringCondition containsNoForbiddenChars(String forbiddenChars) {
        return value -> {
            boolean hasForbidden = value.chars().anyMatch(ch -> forbiddenChars.indexOf(ch) >= 0);
            Assertions.assertThat(hasForbidden)
                    .as("Строка %s не должна содержать символы из набора %s", value, forbiddenChars)
                    .isFalse();
        };
    }

    /**
     * Строка не содержит подстрок, начинающихся и заканчивающихся определенными символами.
     *
     * @param startSymbol начальный символ подстроки
     * @param endSymbol   конечный символ подстроки
     */
    public static StringCondition doesNotContainSubstringsStartingAndEndingWith(char startSymbol, char endSymbol) {
        return value -> {
            Pattern pattern = Pattern.compile(
                    Pattern.quote(String.valueOf(startSymbol)) + ".*?" + Pattern.quote(String.valueOf(endSymbol)));
            boolean found = pattern.matcher(value).find();
            Assertions.assertThat(found)
                    .as("Строка %s не должна содержать подстрок, начинающихся с '%c' и заканчивающихся на '%c'",
                            value, startSymbol, endSymbol)
                    .isFalse();
        };
    }

    /**
     * Строка содержит только символы из заданного набора (шаблона).
     *
     * @param allowedChars строка с допустимыми символами
     */
    public static StringCondition containsOnlyAllowedChars(String allowedChars) {
        return value -> {
            boolean allAllowed = value.chars().allMatch(ch -> allowedChars.indexOf(ch) >= 0);
            Assertions.assertThat(allAllowed)
                    .as("Ожидалось, что строка %s содержит только символы из набора %s", value, allowedChars)
                    .isTrue();
        };
    }

    /**
     * Строка не содержит специальных символов, кроме разрешенных.
     *
     * @param allowedSpecialChars строка с разрешенными специальными символами
     */
    public static StringCondition containsOnlyAllowedSpecialChars(String allowedSpecialChars) {
        return value -> {
            String allowed = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789" + allowedSpecialChars;
            boolean allAllowed = value.chars().allMatch(ch -> allowed.indexOf(ch) >= 0);
            Assertions.assertThat(allAllowed)
                    .as("Строка %s должна содержать только разрешенные специальные символы %s",
                            value, allowedSpecialChars)
                    .isTrue();
        };
    }

    /**
     * Строка не содержит символов с кодом выше заданного значения.
     *
     * @param maxCode максимальный допустимый код символа
     */
    public static StringCondition hasMaxCharCode(int maxCode) {
        return value -> {
            boolean allBelow = value.chars().allMatch(ch -> ch <= maxCode);
            Assertions.assertThat(allBelow)
                    .as("Все символы строки %s должны иметь код не выше %d", value, maxCode)
                    .isTrue();
        };
    }

    /**
     * Строка содержит только символы из заданного Unicode блока.
     *
     * @param blockName название Unicode блока, например "GREEK", "HIRAGANA"
     */
    public static StringCondition containsOnlyUnicodeBlock(String blockName) {
        return value -> {
            Character.UnicodeBlock block = Character.UnicodeBlock.forName(blockName.toUpperCase());
            boolean allInBlock = value.chars().allMatch(ch -> Character.UnicodeBlock.of(ch) == block);
            Assertions.assertThat(allInBlock)
                    .as("Строка %s должна содержать только символы из Unicode блока %s", value, blockName)
                    .isTrue();
        };
    }

    /**
     * Строка содержит слово (подстроку) с учетом границ слова.
     *
     * @param word искомое слово
     */
    public static StringCondition containsWholeWord(String word) {
        String regex = "\\b" + Pattern.quote(word) + "\\b";
        Pattern pattern = Pattern.compile(regex);
        return value -> Assertions.assertThat(pattern.matcher(value).find())
                .as("Строка %s должна содержать слово %s", value, word)
                .isTrue();
    }

    /**
     * Строка не содержит слово (подстроку) с учетом границ слова.
     *
     * @param word запрещенное слово
     */
    public static StringCondition doesNotContainWholeWord(String word) {
        String regex = "\\b" + Pattern.quote(word) + "\\b";
        Pattern pattern = Pattern.compile(regex);
        return value -> Assertions.assertThat(pattern.matcher(value).find())
                .as("Строка %s не должна содержать слово %s", value, word)
                .isFalse();
    }

    /**
     * Строка содержит заданное количество символов Unicode из определенного диапазона.
     *
     * @param unicodeStart начальный код символа (включительно)
     * @param unicodeEnd   конечный код символа (включительно)
     * @param count        ожидаемое количество символов в диапазоне
     */
    public static StringCondition hasUnicodeCharactersInRange(int unicodeStart, int unicodeEnd, int count) {
        return value -> {
            long actualCount = value.chars().filter(ch -> ch >= unicodeStart && ch <= unicodeEnd).count();
            Assertions.assertThat(actualCount)
                    .as("Строка %s должна содержать %d символов в диапазоне [%d, %d], но содержит %d",
                            value, count, unicodeStart, unicodeEnd, actualCount)
                    .isEqualTo(count);
        };
    }

    /**
     * Строка содержит только символы из заданного набора Unicode блоков.
     *
     * @param blocks массив имен блоков Unicode, например "LATIN", "CYRILLIC"
     */
    public static StringCondition containsOnlyUnicodeBlocks(String... blocks) {
        return value -> {
            boolean allMatch = value.chars().allMatch(ch -> {
                Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
                return Arrays.stream(blocks).anyMatch(b -> block.toString().contains(b.toUpperCase()));
            });
            Assertions.assertThat(allMatch)
                    .as("Строка %s должна содержать только символы из блоков %s", value, List.of(blocks))
                    .isTrue();
        };
    }

    /**
     * Строка не содержит последовательностей символов длины более заданной.
     *
     * @param maxSequenceLength максимальная допустимая длина последовательности одинаковых символов
     */
    public static StringCondition hasNoLongSequencesOfSameCharacter(int maxSequenceLength) {
        return value -> {
            int count = 1;
            for (int i = 1; i < value.length(); i++) {
                if (value.charAt(i) == value.charAt(i - 1)) {
                    count++;
                    if (count > maxSequenceLength) {
                        Assertions.fail(
                                String.format("Строка %s содержит последовательность символов '%c' длиной %d, что превышает %d",
                                        value, value.charAt(i), count, maxSequenceLength)
                        );
                    }
                } else {
                    count = 1;
                }
            }
        };
    }

    /**
     * Строка содержит только символы из заданного регулярного выражения.
     *
     * @param regex регулярное выражение, описывающее допустимые символы
     */
    public static StringCondition containsOnlyRegex(String regex) {
        Pattern pattern = Pattern.compile("^" + regex + "+$");
        return value -> Assertions.assertThat(pattern.matcher(value).matches())
                .as("Строка %s должна содержать только символы, соответствующие регулярному выражению %s", value, regex)
                .isTrue();
    }

    /**
     * Строка содержит определенное количество символов из заданного набора.
     *
     * @param chars строка с символами для подсчета
     * @param count ожидаемое количество символов
     */
    public static StringCondition hasSpecificCharCount(String chars, int count) {
        return value -> {
            long actualCount = value.chars().filter(ch -> chars.indexOf(ch) >= 0).count();
            Assertions.assertThat(actualCount)
                    .as("Строка %s должна содержать %d символов из набора %s, но содержит %d",
                            value, count, chars, actualCount)
                    .isEqualTo(count);
        };
    }

    /**
     * Строка содержит заданное количество пробелов.
     *
     * @param count ожидаемое количество пробелов
     */
    public static StringCondition hasSpaceCount(int count) {
        return value -> {
            long spaceCount = value.chars().filter(ch -> ch == ' ').count();
            Assertions.assertThat(spaceCount)
                    .as("Строка %s должна содержать %d пробелов, но содержит %d", value, count, spaceCount)
                    .isEqualTo(count);
        };
    }

    /**
     * Строка не содержит последовательностей пробелов более заданного количества.
     *
     * @param maxConsecutive максимальное допустимое количество подряд идущих пробелов
     */
    public static StringCondition hasMaxConsecutiveSpaces(int maxConsecutive) {
        return value -> {
            Pattern pattern = Pattern.compile(" {" + (maxConsecutive + 1) + "}");
            boolean hasTooMany = pattern.matcher(value).find();
            Assertions.assertThat(hasTooMany)
                    .as("Строка %s не должна содержать более %d подряд идущих пробелов", value, maxConsecutive)
                    .isFalse();
        };
    }

    /**
     * Строка содержит определенное количество символов из каждой категории:
     * буквы, цифры, пробелы, специальные символы.
     *
     * @param lettersCount количество букв
     * @param digitsCount  количество цифр
     * @param spacesCount  количество пробелов
     * @param specialCount количество специальных символов
     */
    public static StringCondition hasCategorizedCharCounts(int lettersCount, int digitsCount, int spacesCount, int specialCount) {
        return value -> {
            long letters = value.chars().filter(Character::isLetter).count();
            long digits = value.chars().filter(Character::isDigit).count();
            long spaces = value.chars().filter(ch -> ch == ' ').count();
            long specials = value.length() - letters - digits - spaces;
            Assertions.assertThat(letters)
                    .as("Строка %s должна содержать %d букв, но содержит %d", value, lettersCount, letters)
                    .isEqualTo(lettersCount);
            Assertions.assertThat(digits)
                    .as("Строка %s должна содержать %d цифр, но содержит %d", value, digitsCount, digits)
                    .isEqualTo(digitsCount);
            Assertions.assertThat(spaces)
                    .as("Строка %s должна содержать %d пробелов, но содержит %d", value, spacesCount, spaces)
                    .isEqualTo(spacesCount);
            Assertions.assertThat(specials)
                    .as("Строка %s должна содержать %d специальных символов, но содержит %d", value, specialCount, specials)
                    .isEqualTo(specialCount);
        };
    }

    /**
     * Строка содержит заданное количество различных типов символов:
     * буквы, цифры, пробелы, специальные символы (не меньше заданных).
     *
     * @param minLetters минимальное количество букв
     * @param minDigits  минимальное количество цифр
     * @param minSpaces  минимальное количество пробелов
     * @param minSpecial минимальное количество спецсимволов
     */
    public static StringCondition containsMinimumCharacterTypes(int minLetters, int minDigits, int minSpaces, int minSpecial) {
        return value -> {
            long letters = value.chars().filter(Character::isLetter).count();
            long digits = value.chars().filter(Character::isDigit).count();
            long spaces = value.chars().filter(ch -> ch == ' ').count();
            long special = value.length() - letters - digits - spaces;

            Assertions.assertThat(letters)
                    .as("Строка %s должна содержать минимум %d букв, но содержит %d", value, minLetters, letters)
                    .isGreaterThanOrEqualTo(minLetters);
            Assertions.assertThat(digits)
                    .as("Строка %s должна содержать минимум %d цифр, но содержит %d", value, minDigits, digits)
                    .isGreaterThanOrEqualTo(minDigits);
            Assertions.assertThat(spaces)
                    .as("Строка %s должна содержать минимум %d пробелов, но содержит %d", value, minSpaces, spaces)
                    .isGreaterThanOrEqualTo(minSpaces);
            Assertions.assertThat(special)
                    .as("Строка %s должна содержать минимум %d специальных символов, но содержит %d", value, minSpecial, special)
                    .isGreaterThanOrEqualTo(minSpecial);
        };
    }

    /**
     * Строка начинается с заглавной буквы.
     */
    public static StringCondition startsWithCapitalLetter() {
        return value -> {
            boolean startsWithCapital = !value.isEmpty() && Character.isUpperCase(value.charAt(0));
            Assertions.assertThat(startsWithCapital)
                    .as("Строка %s должна начинаться с заглавной буквы", value)
                    .isTrue();
        };
    }

    /**
     * Строка содержит одинаковое количество заглавных и строчных букв.
     */
    public static StringCondition hasEqualUpperAndLowerCase() {
        return value -> {
            long upper = value.chars().filter(Character::isUpperCase).count();
            long lower = value.chars().filter(Character::isLowerCase).count();
            Assertions.assertThat(upper)
                    .as("Строка %s должна содержать равное количество заглавных и строчных букв", value)
                    .isEqualTo(lower);
        };
    }

    /**
     * Строка содержит определенное количество заглавных и строчных букв.
     *
     * @param upperCase ожидаемое количество заглавных букв
     * @param lowerCase ожидаемое количество строчных букв
     */
    public static StringCondition hasExactCaseCounts(int upperCase, int lowerCase) {
        return value -> {
            long actualUpper = value.chars().filter(Character::isUpperCase).count();
            long actualLower = value.chars().filter(Character::isLowerCase).count();
            Assertions.assertThat(actualUpper)
                    .as("Строка %s должна содержать %d заглавных букв, но содержит %d", value, upperCase, actualUpper)
                    .isEqualTo(upperCase);
            Assertions.assertThat(actualLower)
                    .as("Строка %s должна содержать %d строчных букв, но содержит %d", value, lowerCase, actualLower)
                    .isEqualTo(lowerCase);
        };
    }

    /**
     * Строка не содержит подряд идущих букв одного регистра.
     *
     * @param maxConsecutive максимальное допустимое количество подряд идущих букв одного регистра
     */
    public static StringCondition hasMaxConsecutiveSameCaseLetters(int maxConsecutive) {
        return value -> {
            int count = 1;
            boolean isUpper = !value.isEmpty() && Character.isUpperCase(value.charAt(0));
            for (int i = 1; i < value.length(); i++) {
                char current = value.charAt(i);
                if (Character.isLetter(current)) {
                    boolean currentIsUpper = Character.isUpperCase(current);
                    if (currentIsUpper == isUpper) {
                        count++;
                        if (count > maxConsecutive) {
                            Assertions.fail(String.format("Строка %s содержит более %d подряд идущих букв одного регистра",
                                    value, maxConsecutive));
                        }
                    } else {
                        isUpper = currentIsUpper;
                        count = 1;
                    }
                } else {
                    count = 1;
                }
            }
        };
    }

    /**
     * Строка заканчивается точкой, восклицательным или вопросительным знаком.
     */
    public static StringCondition endsWithPunctuation() {
        return value -> {
            if (value.isEmpty()) {
                Assertions.fail("Строка не должна быть пустой");
            }
            char last = value.charAt(value.length() - 1);
            boolean isPunct = last == '.' || last == '!' || last == '?';
            Assertions.assertThat(isPunct)
                    .as("Строка %s должна заканчиваться точкой, восклицательным или вопросительным знаком", value)
                    .isTrue();
        };
    }

    /**
     * Строка содержит заданное количество разных символов.
     *
     * @param distinctCount ожидаемое количество различных символов
     */
    public static StringCondition hasDistinctCharacterCount(int distinctCount) {
        return value -> {
            long actualDistinct = value.chars().distinct().count();
            Assertions.assertThat(actualDistinct)
                    .as("Строка %s должна содержать %d различных символов, но содержит %d",
                            value, distinctCount, actualDistinct)
                    .isEqualTo(distinctCount);
        };
    }

    /**
     * Строка содержит заданное количество символов из каждого заданного набора.
     *
     * @param charSets массив строк, каждая из которых содержит набор символов
     * @param counts   массив целых чисел, соответствующих количеству символов из каждого набора
     */
    public static StringCondition hasMultipleCharacterSetsCounts(String[] charSets, int[] counts) {
        if (charSets.length != counts.length) {
            throw new IllegalArgumentException("Количество наборов символов и количеств должно совпадать");
        }
        return value -> {
            for (int i = 0; i < charSets.length; i++) {
                String set = charSets[i];
                int expected = counts[i];
                long actualCount = value.chars().filter(ch -> set.indexOf(ch) >= 0).count();
                Assertions.assertThat(actualCount)
                        .as("Строка %s должна содержать %d символов из набора %s, но содержит %d",
                                value, expected, set, actualCount)
                        .isEqualTo(expected);
            }
        };
    }

    /**
     * Строка имеет фиксированную длину и соответствует заданному шаблону.
     *
     * @param length длина строки
     * @param regex  регулярное выражение
     */
    public static StringCondition hasFixedLengthAndMatchesPattern(int length, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return value -> {
            Assertions.assertThat(value.length())
                    .as("Длина строки %s должна быть %d", value, length)
                    .isEqualTo(length);
            Assertions.assertThat(pattern.matcher(value).matches())
                    .as("Строка %s должна соответствовать шаблону %s", value, regex)
                    .isTrue();
        };
    }
}
