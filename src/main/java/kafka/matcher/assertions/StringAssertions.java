package kafka.matcher.assertions;

import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Утилитный класс для создания строковых условий.
 */
@UtilityClass
public final class StringAssertions {

    /**
     * Функциональный интерфейс для строковых условий.
     */
    @FunctionalInterface
    public interface StringCondition {
        /**
         * Проверяет строку на соответствие условию.
         *
         * @param actual строка для проверки
         */
        void check(String actual);
    }

    /**
     * Проверяет, что строка пустая.
     */
    public static StringCondition isEmpty() {
        return actual -> Assertions.assertThat(actual)
                .as("Строка должна быть пустой")
                .isEmpty();
    }

    /**
     * Проверяет, что строка не пустая.
     */
    public static StringCondition isNotEmpty() {
        return actual -> Assertions.assertThat(actual)
                .as("Строка не должна быть пустой")
                .isNotEmpty();
    }

    /**
     * Проверяет, что строка пустая или состоит только из пробелов.
     */
    public static StringCondition isBlank() {
        return actual -> Assertions.assertThat(actual)
                .as("Строка должна быть пустой или состоять только из пробелов")
                .isBlank();
    }

    /**
     * Проверяет, что строка не пустая и не состоит только из пробелов.
     */
    public static StringCondition isNotBlank() {
        return actual -> Assertions.assertThat(actual)
                .as("Строка не должна быть пустой или состоять только из пробелов")
                .isNotBlank();
    }

    /**
     * Проверяет, что строка равна ожидаемой.
     */
    public static StringCondition equalsTo(String expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Строка должна быть равна %s", expected)
                .isEqualTo(expected);
    }

    /**
     * Проверяет, что строка содержит указанный текст.
     */
    public static StringCondition contains(String text) {
        return actual -> Assertions.assertThat(actual)
                .as("Строка должна содержать %s", text)
                .contains(text);
    }

    /**
     * Проверяет, что строка начинается с указанного префикса.
     */
    public static StringCondition startsWith(String prefix) {
        return actual -> Assertions.assertThat(actual)
                .as("Строка должна начинаться с %s", prefix)
                .startsWith(prefix);
    }

    /**
     * Проверяет, что строка заканчивается указанным суффиксом.
     */
    public static StringCondition endsWith(String suffix) {
        return actual -> Assertions.assertThat(actual)
                .as("Строка должна заканчиваться %s", suffix)
                .endsWith(suffix);
    }

    /**
     * Проверяет, что длина строки равна указанному числу.
     */
    public static StringCondition lengthEqualTo(int length) {
        return actual -> Assertions.assertThat(actual)
                .as("Длина строки должна быть равна %d", length)
                .hasSize(length);
    }

    /**
     * Проверяет, что длина строки больше указанного значения.
     */
    public static StringCondition lengthGreaterThan(int length) {
        return actual -> Assertions.assertThat(actual.length())
                .as("Длина строки должна быть > %d", length)
                .isGreaterThan(length);
    }

    /**
     * Проверяет, что длина строки меньше указанного значения.
     */
    public static StringCondition lengthLessThan(int length) {
        return actual -> Assertions.assertThat(actual.length())
                .as("Длина строки должна быть < %d", length)
                .isLessThan(length);
    }

    /**
     * Проверяет, что строка содержит указанный текст без учета регистра.
     */
    public static StringCondition containsIgnoringCase(String text) {
        return actual -> Assertions.assertThat(actual.toLowerCase())
                .as("Строка должна содержать %s без учета регистра", text)
                .contains(text.toLowerCase());
    }

    /**
     * Проверяет, что строка соответствует указанному регулярному выражению.
     */
    public static StringCondition matchesRegex(String regex) {
        Pattern pattern = Pattern.compile(regex);
        return actual -> Assertions.assertThat(actual)
                .as("Строка должна соответствовать рег. выражению %s", regex)
                .matches(pattern);
    }

    /**
     * Проверяет, что строка соответствует указанному регулярному выражению без учета регистра.
     */
    public static StringCondition matchesCaseInsensitive(String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        return actual -> Assertions.assertThat(pattern.matcher(actual).matches())
                .as("Строка должна соответствовать рег. выражению %s без учета регистра", regex)
                .isTrue();
    }

    /**
     * Проверяет, что строка содержит все указанные подстроки.
     */
    public static StringCondition containsAll(String... texts) {
        return actual -> Arrays.stream(texts).forEach(text -> Assertions.assertThat(actual)
                .as("Строка должна содержать %s", text)
                .contains(text));
    }

    /**
     * Проверяет, что строка содержит заданные слова в указанном порядке.
     */
    public static StringCondition wordsOrder(String... words) {
        return actual -> {
            String patternString = String.join(".*?", words);
            Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
            Assertions.assertThat(pattern.matcher(actual).find())
                    .as("Строка должна содержать слова в порядке: %s", List.of(words))
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка содержит хотя бы одну из указанных подстрок.
     */
    public static StringCondition containsAny(String... texts) {
        return actual -> {
            boolean found = false;
            for (String text : texts) {
                if (actual.contains(text)) {
                    found = true;
                    break;
                }
            }
            Assertions.assertThat(found)
                    .as("Строка должна содержать хотя бы один из %s", List.of(texts))
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка не пуста и не является null, а также не содержит только пробелы.
     */
    public static StringCondition isNonBlank() {
        return actual -> Assertions.assertThat(actual)
                .as("Строка не должна быть пустой, null или состоять только из пробелов")
                .isNotBlank();
    }
}
