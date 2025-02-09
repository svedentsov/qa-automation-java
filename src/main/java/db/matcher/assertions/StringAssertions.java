package db.matcher.assertions;

import db.matcher.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

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
        return value -> {
            Pattern pattern = Pattern.compile(regex);
            Assertions.assertThat(value)
                    .as("Строка должна соответствовать рег. выражению '%s'", regex)
                    .matches(pattern);
        };
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
     * Проверяет, что строка состоит только из цифр.
     *
     * @return условие проверки, что строка содержит только цифры
     */
    public static StringCondition isDigitsOnly() {
        return value -> {
            boolean onlyDigits = value.chars().allMatch(Character::isDigit);
            Assertions.assertThat(onlyDigits)
                    .as("Строка '%s' должна содержать только цифры", value)
                    .isTrue();
        };
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
        return value -> Assertions.assertThat(value.length())
                .as("Длина строки должна быть равна %d", expectedLength)
                .isEqualTo(expectedLength);
    }

    /**
     * Проверяет, что длина строки не меньше заданного значения.
     *
     * @param minLength минимальная длина строки
     * @return условие проверки минимальной длины строки
     */
    public static StringCondition hasMinLength(int minLength) {
        return value -> Assertions.assertThat(value.length())
                .as("Длина строки должна быть не меньше %d", minLength)
                .isGreaterThanOrEqualTo(minLength);
    }

    /**
     * Проверяет, что длина строки не превышает заданное значение.
     *
     * @param maxLength максимальная длина строки
     * @return условие проверки максимальной длины строки
     */
    public static StringCondition hasMaxLength(int maxLength) {
        return value -> Assertions.assertThat(value.length())
                .as("Длина строки должна быть не больше %d", maxLength)
                .isLessThanOrEqualTo(maxLength);
    }

    /**
     * Проверяет, что строка состоит только из пробельных символов.
     *
     * @return условие проверки, что строка состоит только из пробелов
     */
    public static StringCondition isBlank() {
        return value -> {
            String trimmedString = value.trim();
            Assertions.assertThat(trimmedString)
                    .as("Строка должна состоять только из пробельных символов")
                    .isEmpty();
        };
    }

    /**
     * Проверяет, что строка содержит хотя бы один непробельный символ.
     *
     * @return условие проверки, что строка содержит текст
     */
    public static StringCondition hasNonBlankContent() {
        return value -> {
            int trimmedLength = value.trim().length();
            Assertions.assertThat(trimmedLength)
                    .as("Строка должна содержать непустой текст")
                    .isGreaterThan(0);
        };
    }

    /**
     * Проверяет, что строка состоит только из букв.
     *
     * @return условие проверки, что строка содержит только буквы
     */
    public static StringCondition isAlphabetic() {
        return value -> {
            boolean isAlphabetic = value.chars().allMatch(Character::isLetter);
            Assertions.assertThat(isAlphabetic)
                    .as("Строка '%s' должна содержать только буквы", value)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка состоит только из букв и цифр.
     *
     * @return условие проверки, что строка содержит только буквы и цифры
     */
    public static StringCondition isAlphanumeric() {
        return value -> {
            boolean isAlphanumeric = value.chars().allMatch(ch -> Character.isLetter(ch) || Character.isDigit(ch));
            Assertions.assertThat(isAlphanumeric)
                    .as("Строка '%s' должна содержать только буквы и цифры", value)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка является корректным адресом электронной почты.
     *
     * @return условие проверки адреса электронной почты
     */
    public static StringCondition isValidEmail() {
        return value -> {
            String emailPattern = "^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$";
            Pattern pattern = Pattern.compile(emailPattern);
            Assertions.assertThat(value)
                    .as("Строка '%s' должна быть корректным адресом электронной почты", value)
                    .matches(pattern);
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
        return value -> {
            Assertions.assertThat(value)
                    .as("Строка должна начинаться с '%s'", wrapper)
                    .startsWith(wrapper);
            Assertions.assertThat(value)
                    .as("Строка должна заканчиваться на '%s'", wrapper)
                    .endsWith(wrapper);
        };
    }

    /**
     * Проверяет, что строка содержит ожидаемое количество слов.
     *
     * @param expectedCount ожидаемое количество слов
     * @return условие проверки количества слов в строке
     */
    public static StringCondition hasWordCount(int expectedCount) {
        return value -> {
            // Разбиваем строку по пробельным символам
            String[] words = value.trim().split("\\s+");
            Assertions.assertThat(words.length)
                    .as("Строка должна содержать %d слов", expectedCount)
                    .isEqualTo(expectedCount);
        };
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
}
