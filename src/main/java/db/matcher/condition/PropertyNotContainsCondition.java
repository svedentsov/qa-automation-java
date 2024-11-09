package db.matcher.condition;

import db.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.function.Function;

/**
 * Проверка, что строковое свойство не содержит указанный текст.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyNotContainsCondition<T> implements Condition<T> {

    private final Function<T, String> getter;
    private final String text;

    @Override
    public void check(T entity) {
        String actualValue = getter.apply(entity);
        Assertions.assertThat(actualValue)
                .as("Значение должно быть строкой")
                .isInstanceOf(String.class);
        Assertions.assertThat(actualValue)
                .as("Проверка, что значение не содержит '%s'", text)
                .doesNotContain(text);
    }

    @Override
    public String toString() {
        return String.format("Значение не содержит '%s'", text);
    }
}
