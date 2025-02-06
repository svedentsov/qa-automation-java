package db.matcher.assertions;

import db.matcher.Checker;
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
    public interface StringCondition extends Checker<String> {
    }

    /**
     * Возвращает условие, проверяющее, что строка содержит заданный текст.
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
     * Возвращает условие, проверяющее, что строка содержит заданный текст без учёта регистра.
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
     * Возвращает условие, проверяющее, что строка начинается с указанного префикса.
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
     * Возвращает условие, проверяющее, что строка заканчивается указанным суффиксом.
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
     * Возвращает условие, проверяющее, что строка соответствует заданному регулярному выражению.
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
     * Возвращает условие, проверяющее, что строка пуста.
     *
     * @return условие проверки пустоты строки
     */
    public static StringCondition isEmpty() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна быть пустой")
                .isEmpty();
    }

    /**
     * Возвращает условие, проверяющее, что строка не пуста (не null и не "").
     *
     * @return условие проверки, что строка не пуста
     */
    public static StringCondition isNotEmpty() {
        return value -> Assertions.assertThat(value)
                .as("Строка не должна быть пустой")
                .isNotEmpty();
    }

    /**
     * Возвращает условие, проверяющее, что строка состоит только из цифр.
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
}
