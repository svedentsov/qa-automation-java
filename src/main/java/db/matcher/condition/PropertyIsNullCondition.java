package db.matcher.condition;

import db.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.function.Function;

/**
 * Проверка, что свойство является null.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyIsNullCondition<T> implements Condition<T> {

    private final Function<T, ?> getter;

    @Override
    public void check(T entity) {
        Object actualValue = getter.apply(entity);
        Assertions.assertThat(actualValue)
                .as("Проверка, что значение является null")
                .isNull();
    }

    @Override
    public String toString() {
        return "Значение является null";
    }
}
