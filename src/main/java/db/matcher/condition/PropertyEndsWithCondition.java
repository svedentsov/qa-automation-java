package db.matcher.condition;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.function.Function;

/**
 * Проверка, что строковое свойство заканчивается указанным суффиксом.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyEndsWithCondition<T> implements Condition<T> {

    private final Function<T, String> getter;
    private final String suffix;

    @Override
    public void check(T entity) {
        String actualValue = getter.apply(entity);
        Assertions.assertThat(actualValue)
                .as("Значение должно быть строкой")
                .isInstanceOf(String.class);
        Assertions.assertThat(actualValue)
                .as("Проверка, что значение заканчивается на '%s'", suffix)
                .endsWith(suffix);
    }

    @Override
    public String toString() {
        return String.format("Значение заканчивается на '%s'", suffix);
    }
}
