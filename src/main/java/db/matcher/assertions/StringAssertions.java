package db.matcher.assertions;

import db.matcher.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Утилитный класс для проверки строковых свойств сущности.
 */
@UtilityClass
public class StringAssertions {

    /**
     * Функциональный интерфейс для проверки строк.
     */
    @FunctionalInterface
    public interface StringCondition extends Condition<String> {
    }

    /**
     * Проверяет, что строка содержит заданный текст.
     *
     * @param text текст, который должна содержать строка
     * @return условие проверки на содержание подстроки
     */
    public static StringCondition contains(String text) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна содержать '%s'", text)
                .contains(text);
    }

    /**
     * Проверяет, что строка содержит заданный текст без учёта регистра.
     *
     * @param text текст для проверки
     * @return условие проверки на содержание подстроки без учёта регистра
     */
    public static StringCondition containsIgnoreCase(String text) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна содержать '%s' (без учёта регистра)", text)
                .containsIgnoringCase(text);
    }

    /**
     * Проверяет, что строка начинается с указанного префикса.
     *
     * @param prefix префикс, с которого должна начинаться строка
     * @return условие проверки начала строки
     */
    public static StringCondition startsWith(String prefix) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна начинаться с '%s'", prefix)
                .startsWith(prefix);
    }

    /**
     * Проверяет, что строка начинается с указанного префикса без учёта регистра.
     *
     * @param prefix префикс, с которого должна начинаться строка
     * @return условие проверки начала строки без учёта регистра
     */
    public static StringCondition startsWithIgnoreCase(String prefix) {
        return value -> Assertions.assertThat(value.toLowerCase(Locale.ROOT))
                .as("Строка должна начинаться с '%s' (без учёта регистра)", prefix)
                .startsWith(prefix.toLowerCase(Locale.ROOT));
    }

    /**
     * Проверяет, что строка заканчивается указанным суффиксом.
     *
     * @param suffix суффикс, которым должна заканчиваться строка
     * @return условие проверки конца строки
     */
    public static StringCondition endsWith(String suffix) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна заканчиваться на '%s'", suffix)
                .endsWith(suffix);
    }

    /**
     * Проверяет, что строка заканчивается указанным суффиксом без учёта регистра.
     *
     * @param suffix суффикс, которым должна заканчиваться строка
     * @return условие проверки конца строки без учёта регистра
     */
    public static StringCondition endsWithIgnoreCase(String suffix) {
        return value -> Assertions.assertThat(value.toLowerCase(Locale.ROOT))
                .as("Строка должна заканчиваться на '%s' (без учёта регистра)", suffix)
                .endsWith(suffix.toLowerCase(Locale.ROOT));
    }

    /**
     * Проверяет, что строка соответствует заданному регулярному выражению.
     *
     * @param regex регулярное выражение
     * @return условие проверки соответствия строки регулярному выражению
     */
    public static StringCondition matchesRegex(String regex) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна соответствовать рег. выражению '%s'", regex)
                .matches(Pattern.compile(regex));
    }

    /**
     * Проверяет, что строка пуста.
     *
     * @return условие проверки пустоты строки
     */
    public static StringCondition isEmpty() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна быть пустой")
                .isEmpty();
    }

    /**
     * Проверяет, что строка не пуста.
     *
     * @return условие проверки, что строка не пустая
     */
    public static StringCondition isNotEmpty() {
        return value -> Assertions.assertThat(value)
                .as("Строка не должна быть пустой")
                .isNotEmpty();
    }

    /**
     * Проверяет, что строка состоит только из цифр.
     *
     * @return условие проверки, что строка содержит только цифры
     */
    public static StringCondition isDigitsOnly() {
        return value -> Assertions.assertThat(value)
                .as("Строка '%s' должна содержать только цифры", value)
                .matches("\\d+");
    }

    /**
     * Проверяет, что строка равна ожидаемой.
     *
     * @param expected ожидаемое значение строки
     * @return условие проверки равенства строк
     */
    public static StringCondition equalsTo(String expected) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна быть равна '%s'", expected)
                .isEqualTo(expected);
    }

    /**
     * Проверяет, что строка равна ожидаемой без учёта регистра.
     *
     * @param expected ожидаемое значение строки
     * @return условие проверки равенства строк без учёта регистра
     */
    public static StringCondition equalsIgnoreCase(String expected) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна быть равна '%s' (без учёта регистра)", expected)
                .isEqualToIgnoringCase(expected);
    }

    /**
     * Проверяет, что длина строки равна ожидаемой.
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
     * Проверяет, что длина строки не меньше заданного значения.
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
     * Проверяет, что длина строки не превышает заданное значение.
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
     * Проверяет, что длина строки больше заданного значения.
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
     * Проверяет, что длина строки меньше заданного значения.
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
     * Проверяет, что длина строки находится в заданном диапазоне (включительно).
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
     * Проверяет, что длина строки находится в заданном диапазоне (исключительно).
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
     * Проверяет, что строка состоит только из пробельных символов.
     *
     * @return условие проверки, что строка состоит только из пробелов
     */
    public static StringCondition isBlank() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна состоять только из пробельных символов")
                .isBlank();
    }

    /**
     * Проверяет, что строка не является пустой или состоящей только из пробельных символов.
     *
     * @return условие проверки, что строка не пустая и не состоит только из пробелов
     */
    public static StringCondition isNotBlank() {
        return value -> Assertions.assertThat(value)
                .as("Строка не должна быть пустой или состоять только из пробельных символов")
                .isNotBlank();
    }

    /**
     * Проверяет, что строка содержит хотя бы один непробельный символ.
     *
     * @return условие проверки, что строка содержит текст
     */
    public static StringCondition hasNonBlankContent() {
        return value -> Assertions.assertThat(value.trim())
                .as("Строка должна содержать непустой текст")
                .isNotEmpty();
    }

    /**
     * Проверяет, что строка состоит только из букв.
     *
     * @return условие проверки, что строка содержит только буквы
     */
    public static StringCondition isAlphabetic() {
        return value -> Assertions.assertThat(value)
                .as("Строка '%s' должна содержать только буквы", value)
                .matches("[a-zA-Z]+");
    }

    /**
     * Проверяет, что строка состоит только из букв и цифр.
     *
     * @return условие проверки, что строка содержит только буквы и цифры
     */
    public static StringCondition isAlphanumeric() {
        return value -> Assertions.assertThat(value)
                .as("Строка '%s' должна содержать только буквы и цифры", value)
                .matches("[a-zA-Z0-9]+");
    }

    /**
     * Проверяет, что строка является корректным адресом электронной почты.
     *
     * @return условие проверки адреса электронной почты
     */
    public static StringCondition isValidEmail() {
        return value -> Assertions.assertThat(value)
                .as("Строка '%s' должна быть корректным адресом электронной почты", value)
                .matches("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Проверяет, что строка является корректным URL.
     *
     * @return условие проверки URL
     */
    public static StringCondition isValidUrl() {
        return value -> {
            try {
                new URL(value);
                Assertions.assertThat(true)
                        .as("Строка '%s' должна быть корректным URL", value)
                        .isTrue();
            } catch (MalformedURLException e) {
                Assertions.fail("Строка '%s' не является корректным URL: %s", value, e.getMessage());
            }
        };
    }

    /**
     * Проверяет, что строка полностью записана заглавными буквами.
     *
     * @return условие проверки, что строка в верхнем регистре
     */
    public static StringCondition isUpperCase() {
        return value -> Assertions.assertThat(value)
                .as("Строка '%s' должна быть записана заглавными буквами", value)
                .isEqualTo(value.toUpperCase(Locale.ROOT));
    }

    /**
     * Проверяет, что строка полностью записана строчными буквами.
     *
     * @return условие проверки, что строка в нижнем регистре
     */
    public static StringCondition isLowerCase() {
        return value -> Assertions.assertThat(value)
                .as("Строка '%s' должна быть записана строчными буквами", value)
                .isEqualTo(value.toLowerCase(Locale.ROOT));
    }

    /**
     * Проверяет, что строка начинается и заканчивается заданной подстрокой.
     *
     * @param wrapper подстрока, которой должна начинаться и заканчиваться строка
     * @return условие проверки обрамления строки
     */
    public static StringCondition startsAndEndsWith(String wrapper) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна начинаться и заканчиваться на '%s'", wrapper)
                .startsWith(wrapper)
                .endsWith(wrapper);
    }

    /**
     * Проверяет, что строка содержит ожидаемое количество слов.
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
     * Проверяет, что строка содержит заданное слово.
     *
     * @param word слово, которое должна содержать строка
     * @return условие проверки на содержание слова
     */
    public static StringCondition hasWord(String word) {
        return value -> Assertions.assertThat(value.trim().split("\\s+"))
                .as("Строка должна содержать слово '%s'", word)
                .contains(word);
    }

    /**
     * Проверяет, что строка содержит заданное слово без учёта регистра.
     *
     * @param word слово для проверки
     * @return условие проверки на содержание слова без учёта регистра
     */
    public static StringCondition hasWordIgnoreCase(String word) {
        return value -> Assertions.assertThat(Arrays.asList(value.trim().split("\\s+")))
                .as("Строка должна содержать слово '%s' (без учёта регистра)", word)
                .anyMatch(actualWord -> actualWord.equalsIgnoreCase(word));
    }

    /**
     * Проверяет, что строка соответствует заданному шаблону (Pattern).
     *
     * @param pattern шаблон для проверки
     * @return условие проверки соответствия строки заданному шаблону
     */
    public static StringCondition matchesPattern(Pattern pattern) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна соответствовать шаблону '%s'", pattern)
                .matches(pattern);
    }

    /**
     * Проверяет, является ли строка палиндромом (читается одинаково в обоих направлениях).
     *
     * @return условие проверки на палиндром
     */
    public static StringCondition isPalindrome() {
        return value -> {
            String reversed = new StringBuilder(value).reverse().toString();
            Assertions.assertThat(value)
                    .as("Строка '%s' должна быть палиндромом", value)
                    .isEqualTo(reversed);
        };
    }

    /**
     * Проверяет, является ли строка корректным XML.
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
                        .as("Строка '%s' должна быть корректным XML", value)
                        .isTrue();
            } catch (Exception e) {
                Assertions.fail("Строка '%s' не является корректным XML: %s", value, e.getMessage());
            }
        };
    }

    /**
     * Проверяет, является ли строка десятичным числом.
     *
     * @return условие проверки на десятичное число
     */
    public static StringCondition isDecimal() {
        return value -> {
            try {
                Double.parseDouble(value);
                Assertions.assertThat(true)
                        .as("Строка '%s' должна быть десятичным числом", value)
                        .isTrue();
            } catch (NumberFormatException e) {
                Assertions.fail("Строка '%s' не является десятичным числом: %s", value, e.getMessage());
            }
        };
    }

    /**
     * Проверяет, содержит ли строка только указанные символы.
     *
     * @param allowedCharacters строка с разрешенными символами
     * @return условие проверки на содержание только разрешенных символов
     */
    public static StringCondition containsOnlyCharacters(String allowedCharacters) {
        return value -> {
            for (char c : value.toCharArray()) {
                if (allowedCharacters.indexOf(c) == -1) {
                    Assertions.fail("Строка '%s' содержит недопустимый символ '%c'. Разрешены только '%s'", value, c, allowedCharacters);
                    return;
                }
            }
            Assertions.assertThat(true)
                    .as("Строка '%s' содержит только разрешенные символы '%s'", value, allowedCharacters)
                    .isTrue();
        };
    }

    /**
     * Проверяет, не содержит ли строка указанные символы.
     *
     * @param disallowedCharacters строка с запрещенными символами
     * @return условие проверки на отсутствие запрещенных символов
     */
    public static StringCondition doesNotContainCharacters(String disallowedCharacters) {
        return value -> {
            for (char c : value.toCharArray()) {
                if (disallowedCharacters.indexOf(c) != -1) {
                    Assertions.fail("Строка '%s' содержит запрещенный символ '%c'. Запрещены '%s'", value, c, disallowedCharacters);
                    return;
                }
            }
            Assertions.assertThat(true)
                    .as("Строка '%s' не содержит запрещенные символы '%s'", value, disallowedCharacters)
                    .isTrue();
        };
    }

    /**
     * Проверяет, является ли строка шестнадцатеричным числом.
     *
     * @return условие проверки на шестнадцатеричное число
     */
    public static StringCondition isHexadecimal() {
        return value -> Assertions.assertThat(value)
                .as("Строка '%s' должна быть шестнадцатеричным числом", value)
                .matches("^[0-9a-fA-F]+$");
    }

    /**
     * Проверяет, является ли строка двоичным числом.
     *
     * @return условие проверки на двоичное число
     */
    public static StringCondition isBinary() {
        return value -> Assertions.assertThat(value)
                .as("Строка '%s' должна быть двоичным числом", value)
                .matches("^[01]+$");
    }

    /**
     * Проверяет, является ли строка корректным UUID.
     *
     * @return условие проверки на UUID
     */
    public static StringCondition isValidUuid() {
        return value -> {
            try {
                UUID.fromString(value);
                Assertions.assertThat(true)
                        .as("Строка '%s' должна быть корректным UUID", value)
                        .isTrue();
            } catch (IllegalArgumentException e) {
                Assertions.fail("Строка '%s' не является корректным UUID: %s", value, e.getMessage());
            }
        };
    }

    /**
     * Проверяет, содержит ли строка указанное количество строк (разделитель - символ новой строки '\n').
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
     * Проверяет, содержит ли строка заданное количество вхождений подстроки.
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
                    .as("Строка должна содержать '%s' %d раз(а)", text, count)
                    .isEqualTo(count);
        };
    }

    /**
     * Проверяет, что строка содержит хотя бы одну цифру.
     *
     * @return условие проверки наличия цифры
     */
    public static StringCondition containsDigit() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна содержать хотя бы одну цифру")
                .matches(".*\\d.*");
    }

    /**
     * Проверяет, что строка содержит хотя бы одну букву.
     *
     * @return условие проверки наличия буквы
     */
    public static StringCondition containsLetter() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна содержать хотя бы одну букву")
                .matches(".*[a-zA-Z].*");
    }

    /**
     * Проверяет, что строка содержит хотя бы один символ в верхнем регистре.
     *
     * @return условие проверки наличия символа в верхнем регистре
     */
    public static StringCondition containsUpperCase() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна содержать хотя бы один символ в верхнем регистре")
                .matches(".*[A-Z].*");
    }

    /**
     * Проверяет, что строка содержит хотя бы один символ в нижнем регистре.
     *
     * @return условие проверки наличия символа в нижнем регистре
     */
    public static StringCondition containsLowerCase() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна содержать хотя бы один символ в нижнем регистре")
                .matches(".*[a-z].*");
    }

    /**
     * Проверяет, что строка содержит хотя бы один пробельный символ.
     *
     * @return условие проверки наличия пробельного символа
     */
    public static StringCondition containsWhitespace() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна содержать хотя бы один пробельный символ")
                .matches(".*\\s.*");
    }

    /**
     * Проверяет, что строка не содержит пробельных символов.
     *
     * @return условие проверки отсутствия пробельных символов
     */
    public static StringCondition containsNoWhitespace() {
        return value -> Assertions.assertThat(value)
                .as("Строка не должна содержать пробельных символов")
                .doesNotContainPattern("\\s");
    }

    /**
     * Проверяет, что строка является корректным номером телефона (простая проверка формата).
     *
     * @return условие проверки номера телефона
     */
    public static StringCondition isValidPhoneNumber() {
        return value -> Assertions.assertThat(value)
                .as("Строка '%s' должна быть корректным номером телефона", value)
                .matches("^\\+?\\d{1,}([- ]?\\d{1,})*$");
    }

    /**
     * Проверяет, что строка является корректным IP-адресом (версии 4).
     *
     * @return условие проверки IP-адреса
     */
    public static StringCondition isValidIpAddress() {
        return value -> Assertions.assertThat(value)
                .as("Строка '%s' должна быть корректным IP-адресом", value)
                .matches("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
    }

    /**
     * Проверяет, что строка является корректным MAC-адресом.
     *
     * @return условие проверки MAC-адреса
     */
    public static StringCondition isValidMacAddress() {
        return value -> Assertions.assertThat(value)
                .as("Строка '%s' должна быть корректным MAC-адресом", value)
                .matches("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
    }

    /**
     * Проверяет, что строка представляет собой дату в формате ISO 8601 (YYYY-MM-DD).
     *
     * @return условие проверки формата даты ISO 8601
     */
    public static StringCondition isIso8601Date() {
        return value -> Assertions.assertThat(value)
                .as("Строка '%s' должна быть датой в формате ISO 8601 (YYYY-MM-DD)", value)
                .matches("^\\d{4}-\\d{2}-\\d{2}$");
    }

    /**
     * Проверяет, что строка представляет собой дату и время в формате ISO 8601 (YYYY-MM-DDTHH:mm:ssZ).
     *
     * @return условие проверки формата даты и времени ISO 8601
     */
    public static StringCondition isIso8601DateTime() {
        return value -> Assertions.assertThat(value)
                .as("Строка '%s' должна быть датой и временем в формате ISO 8601 (YYYY-MM-DDTHH:mm:ssZ)", value)
                .matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(Z|[+-]\\d{2}:\\d{2})$");
    }

    /**
     * Проверяет, что строка является корректным временем в формате HH:mm:ss.
     *
     * @return условие проверки формата времени HH:mm:ss
     */
    public static StringCondition isValidTime() {
        return value -> {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalTime.parse(value, formatter);
                Assertions.assertThat(true)
                        .as("Строка '%s' должна быть корректным временем в формате HH:mm:ss", value)
                        .isTrue();
            } catch (DateTimeParseException e) {
                Assertions.fail("Строка '%s' не является корректным временем в формате HH:mm:ss: %s", value, e.getMessage());
            }
        };
    }

    /**
     * Проверяет, что строка содержит только заданные символы (альтернативный вариант).
     *
     * @param allowedCharacters строка с разрешенными символами
     * @return условие проверки на содержание только разрешенных символов
     */
    public static StringCondition containsOnly(String allowedCharacters) {
        return value -> Assertions.assertThat(value)
                .as("Строка '%s' должна содержать только символы из '%s'", value, allowedCharacters)
                .matches("[" + Pattern.quote(allowedCharacters) + "]+");
    }

    /**
     * Проверяет, что строка является корректным цветом в HEX-формате (например, #RRGGBB).
     *
     * @return условие проверки HEX-цвета
     */
    public static StringCondition isValidHexColor() {
        return value -> Assertions.assertThat(value)
                .as("Строка '%s' должна быть корректным цветом в HEX-формате (#RRGGBB)", value)
                .matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
    }

    /**
     * Проверяет, что строка содержит заданное количество символов.
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
     * Проверяет, что строка начинается с цифры.
     *
     * @return условие проверки начала с цифры
     */
    public static StringCondition startsWithDigit() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна начинаться с цифры")
                .matches("^\\d.*");
    }

    /**
     * Проверяет, что строка заканчивается цифрой.
     *
     * @return условие проверки окончания на цифру
     */
    public static StringCondition endsWithDigit() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна заканчиваться цифрой")
                .matches(".*\\d$");
    }

    /**
     * Проверяет, что строка начинается с буквы.
     *
     * @return условие проверки начала с буквы
     */
    public static StringCondition startsWithLetter() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна начинаться с буквы")
                .matches("^[a-zA-Z].*");
    }

    /**
     * Проверяет, что строка заканчивается буквой.
     *
     * @return условие проверки окончания на букву
     */
    public static StringCondition endsWithLetter() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна заканчиваться буквой")
                .matches(".*[a-zA-Z]$");
    }

    /**
     * Проверяет, что строка содержит последовательность цифр.
     *
     * @return условие проверки наличия последовательности цифр
     */
    public static StringCondition containsDigits() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна содержать последовательность цифр")
                .matches(".*\\d+.*");
    }

    /**
     * Проверяет, что строка содержит последовательность букв.
     *
     * @return условие проверки наличия последовательности букв
     */
    public static StringCondition containsLetters() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна содержать последовательность букв")
                .matches(".*[a-zA-Z]+.*");
    }

    /**
     * Проверяет, что строка представляет собой логическое значение (true или false, без учёта регистра).
     *
     * @return условие проверки на логическое значение
     */
    public static StringCondition isBoolean() {
        return value -> Assertions.assertThat(value.toLowerCase(Locale.ROOT))
                .as("Строка '%s' должна представлять собой логическое значение (true или false)", value)
                .isIn("true", "false");
    }

    /**
     * Проверяет, что строка является корректным UUID без дефисов.
     *
     * @return условие проверки на UUID без дефисов
     */
    public static StringCondition isValidUuidWithoutHyphens() {
        return value -> {
            try {
                UUID.fromString(value.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{12})", "$1-$2-$3-$4-$5"));
                Assertions.assertThat(true)
                        .as("Строка '%s' должна быть корректным UUID без дефисов", value)
                        .isTrue();
            } catch (IllegalArgumentException e) {
                Assertions.fail("Строка '%s' не является корректным UUID без дефисов: %s", value, e.getMessage());
            }
        };
    }

    /**
     * Проверяет, что строка содержит только уникальные символы.
     *
     * @return условие проверки на уникальность символов
     */
    public static StringCondition containsOnlyUniqueCharacters() {
        return value -> Assertions.assertThat(value.chars().distinct().count())
                .as("Строка '%s' должна содержать только уникальные символы", value)
                .isEqualTo(value.length());
    }
}
