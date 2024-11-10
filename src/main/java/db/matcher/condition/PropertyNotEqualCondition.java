package db.matcher.condition;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.function.Function;

/**
 * Проверка, что свойство сущности не равно указанному значению.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyNotEqualCondition<T> implements Condition<T> {

    private final Function<T, ?> getter;
    private final Object unexpectedValue;

    @Override
    public void check(T entity) {
        Object actualValue = getter.apply(entity);
        Assertions.assertThat(actualValue)
                .as("Проверка, что значение не равно '%s'", unexpectedValue)
                .isNotEqualTo(unexpectedValue);
    }

    @Override
    public String toString() {
        return String.format("Значение не равно '%s'", unexpectedValue);
    }
}
