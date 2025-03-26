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
                .isEqualTo(value.toUpperCase());
    }

    /**
     * Проверяет, что строка полностью записана строчными буквами.
     *
     * @return условие проверки, что строка в нижнем регистре
     */
    public static StringCondition isLowerCase() {
        return value -> Assertions.assertThat(value)
                .as("Строка '%s' должна быть записана строчными буквами", value)
                .isEqualTo(value.toLowerCase());
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
}
