package db.matcher.condition.string;

import db.matcher.condition.Condition;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.function.Function;

/**
 * Проверка, что строковое свойство начинается с указанного префикса.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyStartsWithCondition<T> implements Condition<T> {

    private final Function<T, String> getter;
    private final String prefix;

    @Override
    public void check(T entity) {
        String value = getter.apply(entity);
        Assertions.assertThat(value)
                .as("Значение должно быть строкой")
                .isInstanceOf(String.class);
        Assertions.assertThat(value)
                .as("Проверка, что значение начинается с '%s'", prefix)
                .startsWith(prefix);
    }

    @Override
    public String toString() {
        return String.format("Значение начинается с '%s'", prefix);
    }
}
