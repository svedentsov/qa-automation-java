package db.matcher.condition.string;

import db.matcher.condition.Condition;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.function.Function;

/**
 * Проверка, что строковое свойство содержит указанный текст.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyContainsCondition<T> implements Condition<T> {

    private final Function<T, String> getter;
    private final String text;

    @Override
    public void check(T entity) {
        String actualValue = getter.apply(entity);
        Assertions.assertThat(actualValue)
                .as("Значение должно быть строкой")
                .isInstanceOf(String.class);
        Assertions.assertThat(actualValue)
                .as("Проверка, что значение содержит %s", text)
                .contains(text);
    }

    @Override
    public String toString() {
        return String.format("Значение содержит %s", text);
    }
}
