package db.matcher.condition;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.function.Function;

/**
 * Проверка, что свойство не входит в заданный список значений.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyNotInCondition<T> implements Condition<T> {

    private final Function<T, ?> getter;
    private final List<?> values;

    @Override
    public void check(T entity) {
        Object actualValue = getter.apply(entity);
        Assertions.assertThat(values)
                .as("Список значений для сравнения не должен быть пустым")
                .isNotEmpty();
        Assertions.assertThat(actualValue)
                .as("Проверка, что значение не входит в список значений")
                .isNotIn(values);
    }

    @Override
    public String toString() {
        return String.format("Значение не входит в список значений '%s'", values);
    }
}
