package db.matcher.condition.property;

import db.matcher.condition.Condition;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.function.Function;

/**
 * Проверка, что свойство сущности равно ожидаемому значению.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyEqualCondition<T> implements Condition<T> {

    private final Function<T, ?> getter;
    private final Object expectedValue;

    @Override
    public void check(T entity) {
        Object actualValue = getter.apply(entity);
        Assertions.assertThat(actualValue)
                .as("Проверка, что значение равно '%s'", expectedValue)
                .isEqualTo(expectedValue);
    }

    @Override
    public String toString() {
        return String.format("Значение равно '%s'", expectedValue);
    }
}
