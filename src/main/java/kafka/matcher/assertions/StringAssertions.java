package kafka.matcher.assertions;

import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
         * @param value строка для проверки
         */
        void check(String value);
    }

    // Константы для часто используемых шаблонов
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern URL_REGEX = Pattern.compile("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern UUID_REGEX = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[4][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$");
    private static final String SPECIAL_CHARS = "!@#$%^&*()-_=+[]{}|;:'\",.<>/?`~";

    /**
     * Проверяет, что строка пустая.
     */
    public static StringCondition isEmpty() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна быть пустой")
                .isEmpty();
    }

    /**
     * Проверяет, что строка не пустая.
     */
    public static StringCondition isNotEmpty() {
        return value -> Assertions.assertThat(value)
                .as("Строка не должна быть пустой")
                .isNotEmpty();
    }

    /**
     * Проверяет, что строка пустая или состоит только из пробелов.
     */
    public static StringCondition isBlank() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна быть пустой или состоять только из пробелов")
                .isBlank();
    }

    /**
     * Проверяет, что строка не пустая и не состоит только из пробелов.
     */
    public static StringCondition isNotBlank() {
        return value -> Assertions.assertThat(value)
                .as("Строка не должна быть пустой или состоять только из пробелов")
                .isNotBlank();
    }

    /**
     * Проверяет, что строка не пуста и не состоит только из пробелов.
     */
    public static StringCondition isNonBlank() {
        return value -> Assertions.assertThat(value)
                .as("Строка не должна быть пустой или состоять только из пробелов")
                .isNotBlank();
    }

    /**
     * Проверяет, что длина строки равна указанному числу.
     *
     * @param length ожидаемая длина строки
     */
    public static StringCondition lengthEqualTo(int length) {
        return value -> Assertions.assertThat(value)
                .as("Длина строки должна быть равна %d", length)
                .hasSize(length);
    }

    /**
     * Проверяет, что длина строки больше указанного значения.
     *
     * @param length минимальная длина строки
     */
    public static StringCondition lengthGreaterThan(int length) {
        return value -> Assertions.assertThat(value.length())
                .as("Длина строки должна быть > %d", length)
                .isGreaterThan(length);
    }

    /**
     * Проверяет, что длина строки меньше указанного значения.
     *
     * @param length максимальная длина строки
     */
    public static StringCondition lengthLessThan(int length) {
        return value -> Assertions.assertThat(value.length())
                .as("Длина строки должна быть < %d", length)
                .isLessThan(length);
    }

    /**
     * Проверяет, что строка имеет длину в заданном диапазоне [min, max].
     *
     * @param min минимальная длина
     * @param max максимальная длина
     */
    public static StringCondition lengthBetween(int min, int max) {
        return value -> {
            int actualLength = value.length();
            Assertions.assertThat(actualLength)
                    .as("Длина строки должна быть в диапазоне [%d, %d]", min, max)
                    .isBetween(min, max);
        };
    }

    /**
     * Проверяет, что строка не превышает заданного размера (количество символов).
     *
     * @param maxSize максимальный размер (количество символов)
     */
    public static StringCondition maxSize(int maxSize) {
        return value -> Assertions.assertThat(value.length())
                .as("Ожидалось, что длина строки не превышает %d, но фактически %d", maxSize, value.length())
                .isLessThanOrEqualTo(maxSize);
    }

    /**
     * Проверяет, что строка не короче указанного размера.
     *
     * @param minSize минимальный размер (количество символов)
     */
    public static StringCondition minSize(int minSize) {
        return value -> Assertions.assertThat(value.length())
                .as("Ожидалось, что длина строки не меньше %d, но фактически %d", minSize, value.length())
                .isGreaterThanOrEqualTo(minSize);
    }

    /**
     * Проверяет, что строка не превышает указанное количество строк (разделитель - перевод строки).
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
     * Проверяет, что строка равна ожидаемой.
     *
     * @param expected ожидаемое значение строки
     */
    public static StringCondition equalsTo(String expected) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна быть равна '%s'", expected)
                .isEqualTo(expected);
    }

    /**
     * Проверяет, что строка содержит указанный текст.
     *
     * @param text текст для поиска
     */
    public static StringCondition contains(String text) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна содержать '%s'", text)
                .contains(text);
    }

    /**
     * Проверяет, что строка не содержит указанный текст.
     *
     * @param text подстрока, которой не должно быть в строке
     */
    public static StringCondition doesNotContain(String text) {
        return value -> Assertions.assertThat(value)
                .as("Строка не должна содержать '%s'", text)
                .doesNotContain(text);
    }

    /**
     * Проверяет, что строка содержит указанный текст без учета регистра.
     *
     * @param text текст для поиска
     */
    public static StringCondition containsIgnoringCase(String text) {
        return value -> Assertions.assertThat(value.toLowerCase())
                .as("Строка должна содержать '%s' без учета регистра", text)
                .contains(text.toLowerCase());
    }

    /**
     * Проверяет, что строка содержит хотя бы одну из указанных подстрок.
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
     * Проверяет, что строка содержит все указанные подстроки.
     *
     * @param texts подстроки для поиска
     */
    public static StringCondition containsAll(String... texts) {
        return value -> Arrays.stream(texts).forEach(
                text -> Assertions.assertThat(value)
                        .as("Строка должна содержать '%s'", text)
                        .contains(text)
        );
    }

    /**
     * Проверяет, что строка не содержит ни одной из указанных подстрок.
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
     * Проверяет, что строка содержит все гласные буквы.
     */
    public static StringCondition containsAllVowels() {
        return value -> {
            List<Character> vowels = Arrays.asList('a', 'e', 'i', 'o', 'u', 'а', 'е', 'и', 'о', 'у', 'э', 'ю', 'я');
            boolean containsAll = vowels.stream().allMatch(v -> value.toLowerCase().indexOf(v) >= 0);
            Assertions.assertThat(containsAll)
                    .as("Строка '%s' должна содержать все гласные буквы", value)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка содержит заданные слова в указанном порядке.
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
     * Проверяет, что строка содержит слова в обратном порядке относительно заданного списка.
     *
     * @param words слова для поиска в обратном порядке
     */
    public static StringCondition wordsReverseOrder(String... words) {
        return value -> {
            String[] reversedWords = Arrays.copyOf(words, words.length);
            java.util.Collections.reverse(Arrays.asList(reversedWords));
            String patternString = String.join(".*?", reversedWords);
            Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
            Assertions.assertThat(pattern.matcher(value).find())
                    .as("Строка должна содержать слова в обратном порядке: %s", List.of(reversedWords))
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка начинается с указанного префикса.
     *
     * @param prefix префикс для поиска
     */
    public static StringCondition startsWith(String prefix) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна начинаться с '%s'", prefix)
                .startsWith(prefix);
    }

    /**
     * Проверяет, что строка заканчивается указанным суффиксом.
     *
     * @param suffix суффикс для поиска
     */
    public static StringCondition endsWith(String suffix) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна заканчиваться '%s'", suffix)
                .endsWith(suffix);
    }

    /**
     * Проверяет, что строка начинается и заканчивается с заданных префикса и суффикса.
     *
     * @param prefix префикс
     * @param suffix суффикс
     */
    public static StringCondition startsAndEndsWith(String prefix, String suffix) {
        return value -> {
            Assertions.assertThat(value)
                    .as("Строка должна начинаться с '%s'", prefix)
                    .startsWith(prefix);
            Assertions.assertThat(value)
                    .as("Строка должна заканчиваться на '%s'", suffix)
                    .endsWith(suffix);
        };
    }

    /**
     * Проверяет, что строка начинается и заканчивается заданным префиксом/суффиксом, игнорируя регистр.
     *
     * @param prefix префикс
     * @param suffix суффикс
     */
    public static StringCondition startsWithAndEndsWithIgnoreCase(String prefix, String suffix) {
        return value -> {
            String lowerValue = value.toLowerCase();
            Assertions.assertThat(lowerValue)
                    .as("Строка должна начинаться с '%s' (без учета регистра)", prefix)
                    .startsWith(prefix.toLowerCase());
            Assertions.assertThat(lowerValue)
                    .as("Строка должна заканчиваться на '%s' (без учета регистра)", suffix)
                    .endsWith(suffix.toLowerCase());
        };
    }

    /**
     * Проверяет, что строка начинается с буквы и заканчивается цифрой.
     */
    public static StringCondition startsWithLetterEndsWithDigit() {
        return value -> {
            boolean startsWithLetter = !value.isEmpty() && Character.isLetter(value.charAt(0));
            boolean endsWithDigit = !value.isEmpty() && Character.isDigit(value.charAt(value.length() - 1));
            Assertions.assertThat(startsWithLetter && endsWithDigit)
                    .as("Строка '%s' должна начинаться с буквы и заканчиваться цифрой", value)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка начинается и заканчивается с цифры.
     */
    public static StringCondition startsAndEndsWithDigit() {
        return value -> {
            boolean startsWithDigit = !value.isEmpty() && Character.isDigit(value.charAt(0));
            boolean endsWithDigit = !value.isEmpty() && Character.isDigit(value.charAt(value.length() - 1));
            Assertions.assertThat(startsWithDigit && endsWithDigit)
                    .as("Строка '%s' должна начинаться и заканчиваться цифрой", value)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка соответствует указанному регулярному выражению.
     *
     * @param regex регулярное выражение
     */
    public static StringCondition matchesRegex(String regex) {
        Pattern pattern = Pattern.compile(regex);
        return value -> Assertions.assertThat(value)
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
        return value -> Assertions.assertThat(pattern.matcher(value).matches())
                .as("Строка должна соответствовать рег. выражению '%s' без учета регистра", regex)
                .isTrue();
    }

    /**
     * Проверяет, что строка соответствует шаблону с использованием шаблона Wildcard (* и ?).
     *
     * @param wildcardPattern шаблон с подстановочными символами
     */
    public static StringCondition matchesWildcardPattern(String wildcardPattern) {
        String regex = "^" + Pattern.quote(wildcardPattern)
                .replace("\\*", ".*")
                .replace("\\?", ".") + "$";
        Pattern pattern = Pattern.compile(regex);
        return value -> Assertions.assertThat(pattern.matcher(value).matches())
                .as("Строка '%s' должна соответствовать шаблону '%s'", value, wildcardPattern)
                .isTrue();
    }

    /**
     * Проверяет, что строка содержит заданное количество вхождений подстроки.
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
                    .as("Ожидалось, что строка содержит '%s' %d раз, но было %d", substring, occurrences, count)
                    .isEqualTo(occurrences);
        };
    }

    /**
     * Проверяет, что строка содержит подстроку, повторенную заданное количество раз подряд.
     *
     * @param substring   искомая подстрока
     * @param repetitions ожидаемое количество повторений
     */
    public static StringCondition containsRepeatedSubstring(String substring, int repetitions) {
        return value -> {
            String repeated = substring.repeat(repetitions);
            Assertions.assertThat(value)
                    .as("Строка '%s' должна содержать подстроку '%s' повторенную %d раз подряд", value, substring, repetitions)
                    .contains(repeated);
        };
    }

    /**
     * Проверяет, что строка содержит заданные подстроки в произвольном порядке и без перекрытий.
     *
     * @param texts подстроки для поиска
     */
    public static StringCondition containsAllSubstringsUnorderedNoOverlap(String... texts) {
        return value -> {
            int currentIndex = 0;
            for (String text : texts) {
                int index = value.indexOf(text, currentIndex);
                Assertions.assertThat(index)
                        .as("Строка '%s' должна содержать подстроку '%s' после позиции %d", value, text, currentIndex)
                        .isGreaterThanOrEqualTo(0);
                currentIndex = index + text.length();
            }
        };
    }

    /**
     * Проверяет, что строка содержит заданные подстроки в определенном количестве раз.
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
                    .as("Строка '%s' должна содержать подстроку '%s' ровно %d раз(а), но содержит %d раз(а)",
                            value, substring, count, actualCount)
                    .isEqualTo(count);
        };
    }

    /**
     * Проверяет, что строка представляет собой валидное число (Integer).
     */
    public static StringCondition isInteger() {
        return value -> {
            try {
                Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new AssertionError(String.format("Ожидалось, что строка '%s' будет валидным целым числом", value), e);
            }
        };
    }

    /**
     * Проверяет, что строка представляет собой валидное число (Double).
     */
    public static StringCondition isDouble() {
        return value -> {
            try {
                Double.parseDouble(value);
            } catch (NumberFormatException e) {
                throw new AssertionError(String.format("Ожидалось, что строка '%s' будет валидным числом с плавающей точкой", value), e);
            }
        };
    }

    /**
     * Проверяет, что строка является закодированной Base64.
     */
    public static StringCondition isBase64Encoded() {
        return value -> {
            try {
                java.util.Base64.getDecoder().decode(value);
                Assertions.assertThat(true)
                        .as("Строка '%s' должна быть закодированной Base64", value)
                        .isTrue();
            } catch (IllegalArgumentException e) {
                throw new AssertionError(String.format("Строка '%s' не является закодированной Base64", value), e);
            }
        };
    }

    /**
     * Проверяет, что строка состоит только из цифр.
     */
    public static StringCondition isDigitsOnly() {
        return value -> {
            boolean onlyDigits = value.chars().allMatch(Character::isDigit);
            Assertions.assertThat(onlyDigits)
                    .as("Ожидалось, что строка '%s' содержит только цифры", value)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка содержит хотя бы одну цифру и одну букву.
     */
    public static StringCondition containsLetterAndDigit() {
        return value -> {
            boolean hasLetter = value.chars().anyMatch(Character::isLetter);
            boolean hasDigit = value.chars().anyMatch(Character::isDigit);
            Assertions.assertThat(hasLetter && hasDigit)
                    .as("Строка '%s' должна содержать как минимум одну букву и одну цифру", value)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка не содержит цифр.
     */
    public static StringCondition containsNoDigits() {
        return value -> {
            boolean hasDigits = value.chars().anyMatch(Character::isDigit);
            Assertions.assertThat(hasDigits)
                    .as("Строка '%s' не должна содержать цифр", value)
                    .isFalse();
        };
    }

    /**
     * Проверяет, что строка состоит только из букв (a-z, A-Z).
     */
    public static StringCondition isAlphabetic() {
        return value -> {
            boolean allLetters = value.chars().allMatch(Character::isLetter);
            Assertions.assertThat(allLetters)
                    .as("Ожидалось, что строка '%s' содержит только буквы (a-z, A-Z)", value)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка содержит только буквы из латинского алфавита.
     */
    public static StringCondition isLatinLettersOnly() {
        return value -> {
            boolean isLatin = value.chars().allMatch(ch ->
                    (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')
            );
            Assertions.assertThat(isLatin)
                    .as("Строка '%s' должна содержать только латинские буквы", value)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка содержит только буквы и цифры.
     */
    public static StringCondition isAlphanumeric() {
        return value -> {
            boolean isAlnum = value.chars().allMatch(ch -> Character.isLetterOrDigit(ch));
            Assertions.assertThat(isAlnum)
                    .as("Строка '%s' должна содержать только буквы и цифры", value)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка содержит хотя бы один специальный символ.
     */
    public static StringCondition containsSpecialCharacter() {
        return value -> {
            boolean hasSpecial = value.chars().anyMatch(ch -> SPECIAL_CHARS.indexOf(ch) >= 0);
            Assertions.assertThat(hasSpecial)
                    .as("Строка '%s' должна содержать хотя бы один специальный символ", value)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка содержит заданное количество цифр.
     *
     * @param count ожидаемое количество цифр
     */
    public static StringCondition hasDigits(int count) {
        return value -> {
            long digitCount = value.chars().filter(Character::isDigit).count();
            Assertions.assertThat(digitCount)
                    .as("Строка '%s' должна содержать %d цифр, но содержит %d", value, count, digitCount)
                    .isEqualTo(count);
        };
    }

    /**
     * Проверяет, что строка содержит заданное количество заглавных букв.
     *
     * @param count ожидаемое количество заглавных букв
     */
    public static StringCondition hasUpperCaseLetters(int count) {
        return value -> {
            long upperCaseCount = value.chars().filter(Character::isUpperCase).count();
            Assertions.assertThat(upperCaseCount)
                    .as("Строка '%s' должна содержать %d заглавных букв, но содержит %d", value, count, upperCaseCount)
                    .isEqualTo(count);
        };
    }

    /**
     * Проверяет, что строка содержит заданное количество строчных букв.
     *
     * @param count ожидаемое количество строчных букв
     */
    public static StringCondition hasLowerCaseLetters(int count) {
        return value -> {
            long lowerCaseCount = value.chars().filter(Character::isLowerCase).count();
            Assertions.assertThat(lowerCaseCount)
                    .as("Строка '%s' должна содержать %d строчных букв, но содержит %d", value, count, lowerCaseCount)
                    .isEqualTo(count);
        };
    }

    /**
     * Проверяет, что строка содержит заданное количество специальных символов.
     *
     * @param count ожидаемое количество специальных символов
     */
    public static StringCondition hasSpecialCharacters(int count) {
        return value -> {
            long specialCount = value.chars().filter(ch -> SPECIAL_CHARS.indexOf(ch) >= 0).count();
            Assertions.assertThat(specialCount)
                    .as("Строка '%s' должна содержать %d специальных символов, но содержит %d", value, count, specialCount)
                    .isEqualTo(count);
        };
    }

    /**
     * Проверяет, что строка содержит только уникальные символы.
     */
    public static StringCondition hasAllUniqueCharacters() {
        return value -> {
            long distinctCount = value.chars().distinct().count();
            Assertions.assertThat(distinctCount)
                    .as("Строка '%s' должна содержать только уникальные символы", value)
                    .isEqualTo(value.length());
        };
    }

    /**
     * Проверяет, что строка содержит только уникальные биграммы (последовательности из двух символов).
     */
    public static StringCondition hasUniqueBigrams() {
        return value -> {
            List<String> bigrams = new java.util.ArrayList<>();
            for (int i = 0; i < value.length() - 1; i++) {
                bigrams.add(value.substring(i, i + 2));
            }
            long distinctCount = bigrams.stream().distinct().count();
            Assertions.assertThat(distinctCount)
                    .as("Строка '%s' должна содержать только уникальные биграммы", value)
                    .isEqualTo(bigrams.size());
        };
    }

    /**
     * Проверяет, что строка содержит только уникальные триграммы (последовательности из трех символов).
     */
    public static StringCondition hasUniqueTrigrams() {
        return value -> {
            List<String> trigrams = new java.util.ArrayList<>();
            for (int i = 0; i < value.length() - 2; i++) {
                trigrams.add(value.substring(i, i + 3));
            }
            long distinctCount = trigrams.stream().distinct().count();
            Assertions.assertThat(distinctCount)
                    .as("Строка '%s' должна содержать только уникальные триграммы", value)
                    .isEqualTo(trigrams.size());
        };
    }

    /**
     * Проверяет, что в строке все слова (разделяемые пробелами) уникальны.
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
     * Проверяет, что строка содержит только уникальные слова независимо от регистра.
     */
    public static StringCondition hasUniqueWordsIgnoreCase() {
        return value -> {
            String[] words = value.toLowerCase().split("\\s+");
            long distinctCount = Arrays.stream(words).distinct().count();
            Assertions.assertThat(distinctCount)
                    .as("Строка '%s' должна содержать только уникальные слова независимо от регистра", value)
                    .isEqualTo(words.length);
        };
    }

    /**
     * Проверяет, что строка является палиндромом.
     */
    public static StringCondition isPalindrome() {
        return value -> {
            String cleaned = value.replaceAll("\\s+", "").toLowerCase();
            String reversed = new StringBuilder(cleaned).reverse().toString();
            Assertions.assertThat(cleaned)
                    .as("Строка '%s' должна быть палиндромом", value)
                    .isEqualTo(reversed);
        };
    }

    /**
     * Проверяет, что строка содержит все буквы алфавита хотя бы один раз (панграмма).
     */
    public static StringCondition isPangram() {
        return value -> {
            String lower = value.toLowerCase();
            boolean isPangram = "abcdefghijklmnopqrstuvwxyz".chars().allMatch(ch -> lower.indexOf(ch) >= 0);
            Assertions.assertThat(isPangram)
                    .as("Строка '%s' должна быть панграммой (содержать все буквы алфавита)", value)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка соответствует формату даты по заданному шаблону (DateTimeFormatter).
     *
     * @param pattern шаблон даты, например "yyyy-MM-dd"
     */
    public static StringCondition matchesDateFormat(String pattern) {
        return value -> {
            try {
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern(pattern);
                java.time.LocalDate.parse(value, formatter);
                Assertions.assertThat(true)
                        .as("Строка должна соответствовать формату даты '%s'", pattern)
                        .isTrue();
            } catch (Exception e) {
                throw new AssertionError(String.format("Строка '%s' не соответствует формату даты '%s'", value, pattern), e);
            }
        };
    }

    /**
     * Проверяет, что строка соответствует формату времени по заданному шаблону (DateTimeFormatter).
     *
     * @param pattern шаблон времени, например "HH:mm:ss"
     */
    public static StringCondition matchesTimeFormat(String pattern) {
        return value -> {
            try {
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern(pattern);
                java.time.LocalTime.parse(value, formatter);
                Assertions.assertThat(true)
                        .as("Строка должна соответствовать формату времени '%s'", pattern)
                        .isTrue();
            } catch (Exception e) {
                throw new AssertionError(String.format("Строка '%s' не соответствует формату времени '%s'", value, pattern), e);
            }
        };
    }

    /**
     * Проверяет, что строка закодирована в UTF-8.
     */
    public static StringCondition isUtf8Encoded() {
        return value -> {
            byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
            String decoded = new String(bytes, StandardCharsets.UTF_8);
            Assertions.assertThat(decoded)
                    .as("Строка '%s' должна быть корректно закодирована в UTF-8", value)
                    .isEqualTo(value);
        };
    }

    /**
     * Проверяет, что строка имеет кодировку ASCII (все символы < 128).
     */
    public static StringCondition isAscii() {
        return value -> {
            boolean isAscii = value.chars().allMatch(ch -> ch < 128);
            Assertions.assertThat(isAscii)
                    .as("Ожидалось, что строка '%s' состоит только из ASCII символов", value)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка содержит только символы, представимые в заданной кодировке.
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
                    .as("Строка '%s' должна быть кодируема в '%s'", value, charsetName)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка является валидным email адресом.
     */
    public static StringCondition isValidEmail() {
        return value -> Assertions.assertThat(EMAIL_REGEX.matcher(value).matches())
                .as("Строка '%s' должна быть валидным email адресом", value)
                .isTrue();
    }

    /**
     * Проверяет, что строка является валидным URL.
     */
    public static StringCondition isValidURL() {
        return value -> Assertions.assertThat(URL_REGEX.matcher(value).matches())
                .as("Строка '%s' должна быть валидным URL", value)
                .isTrue();
    }

    /**
     * Проверяет, что строка является валидным UUID.
     */
    public static StringCondition isValidUUID() {
        return value -> Assertions.assertThat(UUID_REGEX.matcher(value).matches())
                .as("Строка '%s' должна быть валидным UUID", value)
                .isTrue();
    }

    /**
     * Проверяет, что строка является валидным JSON.
     */
    public static StringCondition isValidJson() {
        return value -> {
            try {
                new com.fasterxml.jackson.databind.ObjectMapper().readTree(value);
                Assertions.assertThat(true)
                        .as("Строка '%s' должна быть валидным JSON", value)
                        .isTrue();
            } catch (Exception e) {
                throw new AssertionError(String.format("Строка '%s' не является валидным JSON", value), e);
            }
        };
    }

    /**
     * Проверяет, что строка является валидным XML.
     */
    public static StringCondition isValidXml() {
        return value -> {
            try {
                javax.xml.parsers.DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder()
                        .parse(new org.xml.sax.InputSource(new java.io.StringReader(value)));
                Assertions.assertThat(true)
                        .as("Строка '%s' должна быть валидным XML", value)
                        .isTrue();
            } catch (Exception e) {
                throw new AssertionError(String.format("Строка '%s' не является валидным XML", value), e);
            }
        };
    }

    /**
     * Проверяет, что строка соответствует формату телефонного номера
     * (+1-800-555-0199, (800) 555-0199, 8005550199 и т.д.).
     */
    public static StringCondition isValidPhoneNumber() {
        String phoneRegex = "^\\+?[0-9\\-\\(\\)\\s]{7,20}$";
        Pattern pattern = Pattern.compile(phoneRegex);
        return value -> Assertions.assertThat(pattern.matcher(value).matches())
                .as("Строка '%s' должна быть валидным телефонным номером", value)
                .isTrue();
    }

    /**
     * Проверяет, что строка является валидным идентификатором переменной в Java.
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
                    .as("Строка '%s' должна быть валидным идентификатором Java", value)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка не содержит символов табуляции.
     */
    public static StringCondition hasNoTabs() {
        return value -> {
            boolean hasTabs = value.contains("\t");
            Assertions.assertThat(hasTabs)
                    .as("Строка '%s' не должна содержать символов табуляции", value)
                    .isFalse();
        };
    }

    /**
     * Проверяет, что строка не содержит символов управления (не считая пробелов).
     */
    public static StringCondition hasNoControlCharacters() {
        return value -> {
            boolean hasControl = value.chars().anyMatch(ch -> (ch < 32 && ch != ' ') || ch == 127);
            Assertions.assertThat(hasControl)
                    .as("Строка '%s' не должна содержать символов управления", value)
                    .isFalse();
        };
    }

    /**
     * Проверяет, что строка не содержит повторяющихся последовательностей символов длины n.
     *
     * @param sequenceLength длина последовательности символов
     */
    public static StringCondition hasNoRepeatedSequences(int sequenceLength) {
        return value -> {
            for (int i = 0; i <= value.length() - sequenceLength * 2; i++) {
                String sequence = value.substring(i, i + sequenceLength);
                String nextSequence = value.substring(i + sequenceLength, i + sequenceLength * 2);
                if (sequence.equals(nextSequence)) {
                    Assertions.fail(String.format("Строка '%s' содержит повторяющуюся последовательность '%s'", value, sequence));
                }
            }
        };
    }

    /**
     * Проверяет, что строка не содержит подряд идущих пробелов.
     */
    public static StringCondition hasNoConsecutiveSpaces() {
        return value -> {
            boolean hasConsecutive = value.contains("  ");
            Assertions.assertThat(hasConsecutive)
                    .as("Строка '%s' не должна содержать подряд идущих пробелов", value)
                    .isFalse();
        };
    }

    /**
     * Проверяет, что строка не содержит подряд идущих одинаковых символов.
     */
    public static StringCondition hasNoConsecutiveDuplicateCharacters() {
        return value -> {
            for (int i = 1; i < value.length(); i++) {
                if (value.charAt(i) == value.charAt(i - 1)) {
                    Assertions.fail(
                            String.format("Строка '%s' содержит подряд идущие одинаковые символы: '%c'",
                                    value, value.charAt(i))
                    );
                }
            }
        };
    }

    /**
     * Проверяет, что строка не содержит подряд идущих букв и цифр.
     */
    public static StringCondition hasNoConsecutiveLettersAndDigits() {
        return value -> {
            for (int i = 1; i < value.length(); i++) {
                char current = value.charAt(i);
                char previous = value.charAt(i - 1);
                if (Character.isLetter(current) && Character.isDigit(previous) ||
                        Character.isDigit(current) && Character.isLetter(previous)) {
                    Assertions.fail(
                            String.format("Строка '%s' содержит подряд идущие буквы и цифры: '%c%c'",
                                    value, previous, current)
                    );
                }
            }
        };
    }

    /**
     * Проверяет, что строка не содержит символов из заданного набора.
     *
     * @param forbiddenChars строка с запрещенными символами
     */
    public static StringCondition containsNoForbiddenChars(String forbiddenChars) {
        return value -> {
            boolean hasForbidden = value.chars().anyMatch(ch -> forbiddenChars.indexOf(ch) >= 0);
            Assertions.assertThat(hasForbidden)
                    .as("Строка '%s' не должна содержать символы из набора '%s'", value, forbiddenChars)
                    .isFalse();
        };
    }

    /**
     * Проверяет, что строка не содержит подстрок, начинающихся и заканчивающихся определенными символами.
     *
     * @param startSymbol начальный символ подстроки
     * @param endSymbol   конечный символ подстроки
     */
    public static StringCondition doesNotContainSubstringsStartingAndEndingWith(char startSymbol, char endSymbol) {
        return value -> {
            Pattern pattern = Pattern.compile(
                    Pattern.quote(String.valueOf(startSymbol)) + ".*?" + Pattern.quote(String.valueOf(endSymbol))
            );
            boolean found = pattern.matcher(value).find();
            Assertions.assertThat(found)
                    .as("Строка '%s' не должна содержать подстрок, начинающихся с '%c' и заканчивающихся на '%c'",
                            value, startSymbol, endSymbol)
                    .isFalse();
        };
    }

    /**
     * Проверяет, что строка содержит только символы из заданного набора (шаблона).
     *
     * @param allowedChars строка с допустимыми символами
     */
    public static StringCondition containsOnlyAllowedChars(String allowedChars) {
        return value -> {
            boolean allAllowed = value.chars().allMatch(ch -> allowedChars.indexOf(ch) >= 0);
            Assertions.assertThat(allAllowed)
                    .as("Ожидалось, что строка '%s' содержит только символы из набора '%s'", value, allowedChars)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка не содержит специальных символов, кроме разрешенных.
     *
     * @param allowedSpecialChars строка с разрешенными специальными символами
     */
    public static StringCondition containsOnlyAllowedSpecialChars(String allowedSpecialChars) {
        return value -> {
            String allowed = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789" + allowedSpecialChars;
            boolean allAllowed = value.chars().allMatch(ch -> allowed.indexOf(ch) >= 0);
            Assertions.assertThat(allAllowed)
                    .as("Строка '%s' должна содержать только разрешенные специальные символы '%s'",
                            value, allowedSpecialChars)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка не содержит символов с кодом выше заданного значения.
     *
     * @param maxCode максимальный допустимый код символа
     */
    public static StringCondition hasMaxCharCode(int maxCode) {
        return value -> {
            boolean allBelow = value.chars().allMatch(ch -> ch <= maxCode);
            Assertions.assertThat(allBelow)
                    .as("Все символы строки '%s' должны иметь код не выше %d", value, maxCode)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка содержит только символы из заданного Unicode блока.
     *
     * @param blockName название Unicode блока, например "GREEK", "HIRAGANA"
     */
    public static StringCondition containsOnlyUnicodeBlock(String blockName) {
        return value -> {
            Character.UnicodeBlock block = Character.UnicodeBlock.forName(blockName.toUpperCase());
            boolean allInBlock = value.chars().allMatch(ch -> Character.UnicodeBlock.of(ch) == block);
            Assertions.assertThat(allInBlock)
                    .as("Строка '%s' должна содержать только символы из Unicode блока '%s'", value, blockName)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка содержит заданное количество слов.
     * Словом считается последовательность символов, разделенная пробелами.
     *
     * @param wordCount ожидаемое количество слов
     */
    public static StringCondition hasWordCount(int wordCount) {
        return value -> {
            long count = Arrays.stream(value.trim().split("\\s+"))
                    .filter(word -> !word.isEmpty())
                    .count();
            Assertions.assertThat(count)
                    .as("Строка '%s' должна содержать %d слов, но содержит %d", value, wordCount, count)
                    .isEqualTo(wordCount);
        };
    }

    /**
     * Проверяет, что строка содержит слово (подстроку) с учетом границ слова.
     *
     * @param word искомое слово
     */
    public static StringCondition containsWholeWord(String word) {
        String regex = "\\b" + Pattern.quote(word) + "\\b";
        Pattern pattern = Pattern.compile(regex);
        return value -> Assertions.assertThat(pattern.matcher(value).find())
                .as("Строка '%s' должна содержать слово '%s'", value, word)
                .isTrue();
    }

    /**
     * Проверяет, что строка не содержит слово (подстроку) с учетом границ слова.
     *
     * @param word запрещенное слово
     */
    public static StringCondition doesNotContainWholeWord(String word) {
        String regex = "\\b" + Pattern.quote(word) + "\\b";
        Pattern pattern = Pattern.compile(regex);
        return value -> Assertions.assertThat(pattern.matcher(value).find())
                .as("Строка '%s' не должна содержать слово '%s'", value, word)
                .isFalse();
    }

    /**
     * Проверяет, что строка содержит заданное количество символов Unicode из определенного диапазона.
     *
     * @param unicodeStart начальный код символа (включительно)
     * @param unicodeEnd   конечный код символа (включительно)
     * @param count        ожидаемое количество символов в диапазоне
     */
    public static StringCondition hasUnicodeCharactersInRange(int unicodeStart, int unicodeEnd, int count) {
        return value -> {
            long actualCount = value.chars().filter(ch -> ch >= unicodeStart && ch <= unicodeEnd).count();
            Assertions.assertThat(actualCount)
                    .as("Строка '%s' должна содержать %d символов в диапазоне [%d, %d], но содержит %d",
                            value, count, unicodeStart, unicodeEnd, actualCount)
                    .isEqualTo(count);
        };
    }

    /**
     * Проверяет, что строка содержит только символы из заданного набора Unicode блоков.
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
                    .as("Строка '%s' должна содержать только символы из блоков %s", value, List.of(blocks))
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка не содержит последовательностей символов длины более заданной.
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
                                String.format("Строка '%s' содержит последовательность символов '%c' длиной %d, что превышает %d",
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
     * Проверяет, что строка содержит только символы из заданного регулярного выражения.
     *
     * @param regex регулярное выражение, описывающее допустимые символы
     */
    public static StringCondition containsOnlyRegex(String regex) {
        Pattern pattern = Pattern.compile("^" + regex + "+$");
        return value -> Assertions.assertThat(pattern.matcher(value).matches())
                .as("Строка '%s' должна содержать только символы, соответствующие регулярному выражению '%s'", value, regex)
                .isTrue();
    }

    /**
     * Проверяет, что строка содержит определенное количество символов из заданного набора.
     *
     * @param chars строка с символами для подсчета
     * @param count ожидаемое количество символов
     */
    public static StringCondition hasSpecificCharCount(String chars, int count) {
        return value -> {
            long actualCount = value.chars().filter(ch -> chars.indexOf(ch) >= 0).count();
            Assertions.assertThat(actualCount)
                    .as("Строка '%s' должна содержать %d символов из набора '%s', но содержит %d",
                            value, count, chars, actualCount)
                    .isEqualTo(count);
        };
    }

    /**
     * Проверяет, что строка содержит заданное количество пробелов.
     *
     * @param count ожидаемое количество пробелов
     */
    public static StringCondition hasSpaceCount(int count) {
        return value -> {
            long spaceCount = value.chars().filter(ch -> ch == ' ').count();
            Assertions.assertThat(spaceCount)
                    .as("Строка '%s' должна содержать %d пробелов, но содержит %d", value, count, spaceCount)
                    .isEqualTo(count);
        };
    }

    /**
     * Проверяет, что строка не содержит последовательностей пробелов более заданного количества.
     *
     * @param maxConsecutive максимальное допустимое количество подряд идущих пробелов
     */
    public static StringCondition hasMaxConsecutiveSpaces(int maxConsecutive) {
        return value -> {
            Pattern pattern = Pattern.compile(" {" + (maxConsecutive + 1) + "}");
            boolean hasTooMany = pattern.matcher(value).find();
            Assertions.assertThat(hasTooMany)
                    .as("Строка '%s' не должна содержать более %d подряд идущих пробелов", value, maxConsecutive)
                    .isFalse();
        };
    }

    /**
     * Проверяет, что строка содержит определенное количество символов из каждой категории:
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
                    .as("Строка '%s' должна содержать %d букв, но содержит %d", value, lettersCount, letters)
                    .isEqualTo(lettersCount);
            Assertions.assertThat(digits)
                    .as("Строка '%s' должна содержать %d цифр, но содержит %d", value, digitsCount, digits)
                    .isEqualTo(digitsCount);
            Assertions.assertThat(spaces)
                    .as("Строка '%s' должна содержать %d пробелов, но содержит %d", value, spacesCount, spaces)
                    .isEqualTo(spacesCount);
            Assertions.assertThat(specials)
                    .as("Строка '%s' должна содержать %d специальных символов, но содержит %d", value, specialCount, specials)
                    .isEqualTo(specialCount);
        };
    }

    /**
     * Проверяет, что строка содержит заданное количество различных типов символов:
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
                    .as("Строка '%s' должна содержать минимум %d букв, но содержит %d", value, minLetters, letters)
                    .isGreaterThanOrEqualTo(minLetters);
            Assertions.assertThat(digits)
                    .as("Строка '%s' должна содержать минимум %d цифр, но содержит %d", value, minDigits, digits)
                    .isGreaterThanOrEqualTo(minDigits);
            Assertions.assertThat(spaces)
                    .as("Строка '%s' должна содержать минимум %d пробелов, но содержит %d", value, minSpaces, spaces)
                    .isGreaterThanOrEqualTo(minSpaces);
            Assertions.assertThat(special)
                    .as("Строка '%s' должна содержать минимум %d специальных символов, но содержит %d", value, minSpecial, special)
                    .isGreaterThanOrEqualTo(minSpecial);
        };
    }

    /**
     * Проверяет, что строка содержит только строчные буквы.
     */
    public static StringCondition isLowerCase() {
        return value -> {
            boolean isLower = value.chars().allMatch(ch -> !Character.isLetter(ch) || Character.isLowerCase(ch));
            Assertions.assertThat(isLower)
                    .as("Строка '%s' должна содержать только строчные буквы", value)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка содержит только заглавные буквы.
     */
    public static StringCondition isUpperCase() {
        return value -> {
            boolean isUpper = value.chars().allMatch(ch -> !Character.isLetter(ch) || Character.isUpperCase(ch));
            Assertions.assertThat(isUpper)
                    .as("Строка '%s' должна содержать только заглавные буквы", value)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка начинается с заглавной буквы.
     */
    public static StringCondition startsWithCapitalLetter() {
        return value -> {
            boolean startsWithCapital = !value.isEmpty() && Character.isUpperCase(value.charAt(0));
            Assertions.assertThat(startsWithCapital)
                    .as("Строка '%s' должна начинаться с заглавной буквы", value)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка содержит одинаковое количество заглавных и строчных букв.
     */
    public static StringCondition hasEqualUpperAndLowerCase() {
        return value -> {
            long upper = value.chars().filter(Character::isUpperCase).count();
            long lower = value.chars().filter(Character::isLowerCase).count();
            Assertions.assertThat(upper)
                    .as("Строка '%s' должна содержать равное количество заглавных и строчных букв", value)
                    .isEqualTo(lower);
        };
    }

    /**
     * Проверяет, что строка содержит определенное количество заглавных и строчных букв.
     *
     * @param upperCase ожидаемое количество заглавных букв
     * @param lowerCase ожидаемое количество строчных букв
     */
    public static StringCondition hasExactCaseCounts(int upperCase, int lowerCase) {
        return value -> {
            long actualUpper = value.chars().filter(Character::isUpperCase).count();
            long actualLower = value.chars().filter(Character::isLowerCase).count();
            Assertions.assertThat(actualUpper)
                    .as("Строка '%s' должна содержать %d заглавных букв, но содержит %d", value, upperCase, actualUpper)
                    .isEqualTo(upperCase);
            Assertions.assertThat(actualLower)
                    .as("Строка '%s' должна содержать %d строчных букв, но содержит %d", value, lowerCase, actualLower)
                    .isEqualTo(lowerCase);
        };
    }

    /**
     * Проверяет, что строка не содержит подряд идущих букв одного регистра.
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
                            Assertions.fail(String.format("Строка '%s' содержит более %d подряд идущих букв одного регистра",
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
     * Проверяет, что строка заканчивается точкой, восклицательным или вопросительным знаком.
     */
    public static StringCondition endsWithPunctuation() {
        return value -> {
            if (value.isEmpty()) {
                Assertions.fail("Строка не должна быть пустой");
            }
            char last = value.charAt(value.length() - 1);
            boolean isPunct = last == '.' || last == '!' || last == '?';
            Assertions.assertThat(isPunct)
                    .as("Строка '%s' должна заканчиваться точкой, восклицательным или вопросительным знаком", value)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что строка содержит заданное количество разных символов.
     *
     * @param distinctCount ожидаемое количество различных символов
     */
    public static StringCondition hasDistinctCharacterCount(int distinctCount) {
        return value -> {
            long actualDistinct = value.chars().distinct().count();
            Assertions.assertThat(actualDistinct)
                    .as("Строка '%s' должна содержать %d различных символов, но содержит %d",
                            value, distinctCount, actualDistinct)
                    .isEqualTo(distinctCount);
        };
    }

    /**
     * Проверяет, что строка содержит заданное количество символов из каждого заданного набора.
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
                        .as("Строка '%s' должна содержать %d символов из набора '%s', но содержит %d",
                                value, expected, set, actualCount)
                        .isEqualTo(expected);
            }
        };
    }

    /**
     * Проверяет, что строка имеет фиксированную длину и соответствует заданному шаблону.
     *
     * @param length длина строки
     * @param regex  регулярное выражение
     */
    public static StringCondition hasFixedLengthAndMatchesPattern(int length, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return value -> {
            Assertions.assertThat(value.length())
                    .as("Длина строки '%s' должна быть %d", value, length)
                    .isEqualTo(length);
            Assertions.assertThat(pattern.matcher(value).matches())
                    .as("Строка '%s' должна соответствовать шаблону '%s'", value, regex)
                    .isTrue();
        };
    }
}
