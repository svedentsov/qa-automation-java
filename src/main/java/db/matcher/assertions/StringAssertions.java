package db.matcher.assertions;

import db.matcher.condition.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Утилитный класс для проверки строковых свойств в сущности (property + проверка).
 */
@UtilityClass
public class StringAssertions {

    /**
     * Проверяет, что строковое свойство содержит указанный текст.
     */
    public static <T> Condition<T> propertyContains(Function<T, String> getter, String text) {
        return entity -> {
            String actualValue = getter.apply(entity);
            Assertions.assertThat(actualValue)
                    .as("Строка должна содержать '%s'", text)
                    .isNotNull()
                    .contains(text);
        };
    }

    /**
     * Проверяет, что строковое свойство содержит указанный текст (без учёта регистра).
     */
    public static <T> Condition<T> propertyContainsIgnoreCase(Function<T, String> getter, String text) {
        return entity -> {
            String actualValue = getter.apply(entity);
            Assertions.assertThat(actualValue)
                    .as("Строка должна содержать '%s' (без учёта регистра)", text)
                    .isNotNull()
                    .containsIgnoringCase(text);
        };
    }

    /**
     * Проверяет, что строковое свойство начинается с указанного префикса.
     */
    public static <T> Condition<T> propertyStartsWith(Function<T, String> getter, String prefix) {
        return entity -> {
            String actualValue = getter.apply(entity);
            Assertions.assertThat(actualValue)
                    .as("Строка должна начинаться с '%s'", prefix)
                    .isNotNull()
                    .startsWith(prefix);
        };
    }

    /**
     * Проверяет, что строковое свойство заканчивается указанным суффиксом.
     */
    public static <T> Condition<T> propertyEndsWith(Function<T, String> getter, String suffix) {
        return entity -> {
            String actualValue = getter.apply(entity);
            Assertions.assertThat(actualValue)
                    .as("Строка должна заканчиваться на '%s'", suffix)
                    .isNotNull()
                    .endsWith(suffix);
        };
    }

    /**
     * Проверяет, что строковое свойство соответствует регулярному выражению.
     */
    public static <T> Condition<T> propertyMatchesRegex(Function<T, String> getter, String regex) {
        return entity -> {
            String actualValue = getter.apply(entity);
            Pattern pattern = Pattern.compile(regex);
            Assertions.assertThat(actualValue)
                    .as("Строка должна соответствовать рег. выражению '%s'", regex)
                    .isNotNull()
                    .matches(pattern);
        };
    }

    /**
     * Проверяет, что строковое свойство пустое.
     */
    public static <T> Condition<T> propertyIsEmpty(Function<T, String> getter) {
        return entity -> {
            String actualValue = getter.apply(entity);
            Assertions.assertThat(actualValue)
                    .as("Строка должна быть пустой")
                    .isNotNull()
                    .isEmpty();
        };
    }

    /**
     * Проверяет, что строковое свойство не пустое (не null и не "").
     */
    public static <T> Condition<T> propertyIsNotEmpty(Function<T, String> getter) {
        return entity -> {
            String actualValue = getter.apply(entity);
            Assertions.assertThat(actualValue)
                    .as("Строка не должна быть пустой")
                    .isNotNull()
                    .isNotEmpty();
        };
    }

    /**
     * Проверяет, что строковое свойство состоит только из цифр.
     */
    public static <T> Condition<T> propertyIsDigitsOnly(Function<T, String> getter) {
        return entity -> {
            String value = getter.apply(entity);
            Assertions.assertThat(value)
                    .as("Строка не должна быть null")
                    .isNotNull();
            boolean onlyDigits = value.chars().allMatch(Character::isDigit);
            Assertions.assertThat(onlyDigits)
                    .as("Строка '%s' должна содержать только цифры", value)
                    .isTrue();
        };
    }
}
