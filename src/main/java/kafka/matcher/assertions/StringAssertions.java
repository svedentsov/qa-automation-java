package kafka.matcher.assertions;

import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Утилитный класс для создания условий проверки строк.
 * Предоставляет множество методов для проверки длины, формата, соответствия шаблонам,
 * наличия подстрок, регистрозависимости/регистронезависимости и т.д.
 */
@UtilityClass
public class StringAssertions {

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
     *
     * @param expected ожидаемое значение строки
     */
    public static StringCondition equalsTo(String expected) {
        return actual -> Assertions.assertThat(actual)
                .as("Строка должна быть равна '%s'", expected)
                .isEqualTo(expected);
    }

    /**
     * Проверяет, что строка содержит указанный текст.
     *
     * @param text текст для поиска
     */
    public static StringCondition contains(String text) {
        return actual -> Assertions.assertThat(actual)
                .as("Строка должна содержать '%s'", text)
                .contains(text);
    }

    /**
     * Проверяет, что строка начинается с указанного префикса.
     *
     * @param prefix префикс для поиска
     */
    public static StringCondition startsWith(String prefix) {
        return actual -> Assertions.assertThat(actual)
                .as("Строка должна начинаться с '%s'", prefix)
                .startsWith(prefix);
    }

    /**
     * Проверяет, что строка заканчивается указанным суффиксом.
     *
     * @param suffix суффикс для поиска
     */
    public static StringCondition endsWith(String suffix) {
        return actual -> Assertions.assertThat(actual)
                .as("Строка должна заканчиваться '%s'", suffix)
                .endsWith(suffix);
    }

    /**
     * Проверяет, что длина строки равна указанному числу.
     *
     * @param length ожидаемая длина строки
     */
    public static StringCondition lengthEqualTo(int length) {
        return actual -> Assertions.assertThat(actual)
                .as("Длина строки должна быть равна %d", length)
                .hasSize(length);
    }

    /**
     * Проверяет, что длина строки больше указанного значения.
     *
     * @param length минимальная длина строки
     */
    public static StringCondition lengthGreaterThan(int length) {
        return actual -> Assertions.assertThat(actual.length())
                .as("Длина строки должна быть > %d", length)
                .isGreaterThan(length);
    }

    /**
     * Проверяет, что длина строки меньше указанного значения.
     *
     * @param length максимальная длина строки
     */
    public static StringCondition lengthLessThan(int length) {
        return actual -> Assertions.assertThat(actual.length())
                .as("Длина строки должна быть < %d", length)
                .isLessThan(length);
    }

    /**
     * Проверяет, что строка содержит указанный текст без учета регистра.
     *
     * @param text текст для поиска
     */
    public static StringCondition containsIgnoringCase(String text) {
        return actual -> Assertions.assertThat(actual.toLowerCase())
                .as("Строка должна содержать '%s' без учета регистра", text)
                .contains(text.toLowerCase());
    }

    /**
     * Проверяет, что строка соответствует указанному регулярному выражению.
     *
     * @param regex регулярное выражение
     */
    public static StringCondition matchesRegex(String regex) {
        Pattern pattern = Pattern.compile(regex);
        return actual -> Assertions.assertThat(actual)
                .as("Строка должна соответствовать рег. выражению '%s'", regex)
                .matches(pattern);
    }

    /**
     * Проверяет, что строка соответствует указанному регулярному выражению без учета регистра.
     *
     * @param regex регулярное выражение
     */
    public static StringCondition matchesCaseInsensitive(String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        return actual -> Assertions.assertThat(pattern.matcher(actual).matches())
                .as("Строка должна соответствовать рег. выражению '%s' без учета регистра", regex)
                .isTrue();
    }

    /**
     * Проверяет, что строка содержит все указанные подстроки.
     *
     * @param texts подстроки для поиска
     */
    public static StringCondition containsAll(String... texts) {
        return actual -> Arrays.stream(texts).forEach(text -> Assertions.assertThat(actual)
                .as("Строка должна содержать '%s'", text)
                .contains(text));
    }

    /**
     * Проверяет, что строка содержит заданные слова в указанном порядке.
     * Между словами может быть любой текст (многострочный тоже).
     *
     * @param words слова для поиска в порядке следования
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
     *
     * @param texts подстроки для поиска
     */
    public static StringCondition containsAny(String... texts) {
        return actual -> {
            boolean found = Arrays.stream(texts).anyMatch(actual::contains);
            Assertions.assertThat(found)
                    .as("Строка должна содержать хотя бы один из %s", List.of(texts))
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка не пуста и не состоит только из пробелов.
     */
    public static StringCondition isNonBlank() {
        return actual -> Assertions.assertThat(actual)
                .as("Строка не должна быть пустой или состоять только из пробелов")
                .isNotBlank();
    }

    /**
     * Проверяет, что строка имеет длину в заданном диапазоне [min, max].
     *
     * @param min минимальная длина
     * @param max максимальная длина
     */
    public static StringCondition lengthBetween(int min, int max) {
        return actual -> {
            int length = actual.length();
            Assertions.assertThat(length)
                    .as("Длина строки должна быть в диапазоне [%d, %d]", min, max)
                    .isBetween(min, max);
        };
    }

    /**
     * Проверяет, что строка не содержит указанный текст.
     *
     * @param text подстрока, которой не должно быть в строке
     */
    public static StringCondition doesNotContain(String text) {
        return actual -> Assertions.assertThat(actual)
                .as("Строка не должна содержать '%s'", text)
                .doesNotContain(text);
    }

    /**
     * Проверяет, что строка представляет собой валидное число (Integer).
     */
    public static StringCondition isInteger() {
        return actual -> {
            try {
                Integer.parseInt(actual);
            } catch (NumberFormatException e) {
                throw new AssertionError(String.format("Ожидалось, что строка '%s' будет валидным целым числом", actual), e);
            }
        };
    }

    /**
     * Проверяет, что строка представляет собой валидное число (Double).
     */
    public static StringCondition isDouble() {
        return actual -> {
            try {
                Double.parseDouble(actual);
            } catch (NumberFormatException e) {
                throw new AssertionError(String.format("Ожидалось, что строка '%s' будет валидным числом с плавающей точкой", actual), e);
            }
        };
    }

    /**
     * Проверяет, что строка состоит только из цифр.
     */
    public static StringCondition isDigitsOnly() {
        return actual -> {
            boolean onlyDigits = actual.chars().allMatch(Character::isDigit);
            Assertions.assertThat(onlyDigits)
                    .as("Ожидалось, что строка '%s' содержит только цифры", actual)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка не превышает указанное количество строк (разделитель - перевод строки).
     *
     * @param maxLines максимально допустимое количество строк
     */
    public static StringCondition maxLines(int maxLines) {
        return actual -> {
            long count = actual.lines().count();
            Assertions.assertThat(count)
                    .as("Ожидалось, что строка содержит не более %d строк, но фактически %d", maxLines, count)
                    .isLessThanOrEqualTo(maxLines);
        };
    }

    /**
     * Проверяет, что строка содержит заданное количество вхождений подстроки.
     *
     * @param substring   искомая подстрока
     * @param occurrences ожидаемое количество вхождений
     */
    public static StringCondition containsOccurrences(String substring, int occurrences) {
        return actual -> {
            int count = 0;
            int idx = 0;
            while ((idx = actual.indexOf(substring, idx)) != -1) {
                count++;
                idx += substring.length();
            }
            Assertions.assertThat(count)
                    .as("Ожидалось, что строка содержит '%s' %d раз, но было %d", substring, occurrences, count)
                    .isEqualTo(occurrences);
        };
    }

    /**
     * Проверяет, что в строке все слова (разделяемые пробелами) уникальны.
     */
    public static StringCondition allWordsUnique() {
        return actual -> {
            String[] words = actual.split("\\s+");
            long distinctCount = Arrays.stream(words).distinct().count();
            Assertions.assertThat(distinctCount)
                    .as("Ожидалось, что все слова строки уникальны, но некоторые повторяются: %s", actual)
                    .isEqualTo(words.length);
        };
    }

    /**
     * Проверяет, что строка содержит только буквы (a-z, A-Z).
     */
    public static StringCondition isAlphabetic() {
        return actual -> {
            boolean allLetters = actual.chars().allMatch(Character::isLetter);
            Assertions.assertThat(allLetters)
                    .as("Ожидалось, что строка '%s' содержит только буквы (a-z, A-Z)", actual)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка не превышает заданного размера (количество символов).
     *
     * @param maxSize максимальный размер (количество символов)
     */
    public static StringCondition maxSize(int maxSize) {
        return actual -> Assertions.assertThat(actual.length())
                .as("Ожидалось, что длина строки не превышает %d, но фактически %d", maxSize, actual.length())
                .isLessThanOrEqualTo(maxSize);
    }

    /**
     * Проверяет, что строка не короче указанного размера.
     *
     * @param minSize минимальный размер (количество символов)
     */
    public static StringCondition minSize(int minSize) {
        return actual -> Assertions.assertThat(actual.length())
                .as("Ожидалось, что длина строки не меньше %d, но фактически %d", minSize, actual.length())
                .isGreaterThanOrEqualTo(minSize);
    }

    /**
     * Проверяет, что строка содержит подстроки в любой последовательности (не обязательно подряд и упорядоченно).
     *
     * @param texts подстроки, которые должны содержаться в строке
     */
    public static StringCondition containsAllUnordered(String... texts) {
        return actual -> {
            for (String text : texts) {
                Assertions.assertThat(actual)
                        .as("Строка должна содержать '%s'", text)
                        .contains(text);
            }
        };
    }

    /**
     * Проверяет, что строка не содержит ни одной из указанных подстрок.
     *
     * @param texts подстроки, которых не должно быть
     */
    public static StringCondition containsNone(String... texts) {
        return actual -> {
            boolean foundAny = Arrays.stream(texts).anyMatch(actual::contains);
            Assertions.assertThat(foundAny)
                    .as("Строка не должна содержать ни одну из подстрок %s", List.of(texts))
                    .isFalse();
        };
    }

    /**
     * Проверяет, что строка содержит только символы из заданного набора (шаблона).
     *
     * @param allowedChars строка с допустимыми символами
     */
    public static StringCondition containsOnlyAllowedChars(String allowedChars) {
        return actual -> {
            boolean allAllowed = actual.chars().allMatch(ch -> allowedChars.indexOf(ch) >= 0);
            Assertions.assertThat(allAllowed)
                    .as("Ожидалось, что строка '%s' содержит только символы из набора '%s'", actual, allowedChars)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка имеет кодировку ASCII (то есть все символы < 128).
     */
    public static StringCondition isAscii() {
        return actual -> {
            boolean isAscii = actual.chars().allMatch(ch -> ch < 128);
            Assertions.assertThat(isAscii)
                    .as("Ожидалось, что строка '%s' состоит только из ASCII символов", actual)
                    .isTrue();
        };
    }
}
