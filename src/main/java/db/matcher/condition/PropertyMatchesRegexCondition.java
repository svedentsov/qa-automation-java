package db.matcher.condition;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Проверка, что строковое свойство соответствует регулярному выражению.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyMatchesRegexCondition<T> implements Condition<T> {

    private final Function<T, String> getter;
    private final String regex;

    @Override
    public void check(T entity) {
        String actualValue = getter.apply(entity);
        Pattern pattern = Pattern.compile(regex);
        Assertions.assertThat(actualValue)
                .as("Значение должно быть строкой")
                .isInstanceOf(String.class);
        Assertions.assertThat(actualValue)
                .as("Проверка, что значение соответствует регулярному выражению '%s'", regex)
                .matches(pattern);
    }

    @Override
    public String toString() {
        return String.format("Значение соответствует регулярному выражению '%s'", regex);
    }
}
