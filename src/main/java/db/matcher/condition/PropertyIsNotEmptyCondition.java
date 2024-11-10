package db.matcher.condition;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.Collection;
import java.util.function.Function;

/**
 * Проверка, что свойство не пустое (не пустая строка, не пустая коллекция).
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyIsNotEmptyCondition<T> implements Condition<T> {

    private final Function<T, ?> getter;

    @Override
    public void check(T entity) {
        Object value = getter.apply(entity);
        if (value instanceof String) {
            Assertions.assertThat((String) value)
                    .as("Строка не должна быть пустой")
                    .isNotEmpty();
        } else if (value instanceof Collection) {
            Assertions.assertThat((Collection<?>) value)
                    .as("Коллекция не должна быть пустой")
                    .isNotEmpty();
        } else {
            throw new IllegalArgumentException("Значение не является строкой или коллекцией");
        }
    }

    @Override
    public String toString() {
        return "Значение не пустое";
    }
}
