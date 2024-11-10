package db.matcher.condition;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.Collection;
import java.util.function.Function;

/**
 * Проверка, что свойство пустое (пустая строка, пустая коллекция).
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyIsEmptyCondition<T> implements Condition<T> {

    private final Function<T, ?> getter;

    @Override
    public void check(T entity) {
        Object value = getter.apply(entity);
        if (value instanceof String) {
            Assertions.assertThat((String) value)
                    .as("Строка должна быть пустой")
                    .isEmpty();
        } else if (value instanceof Collection) {
            Assertions.assertThat((Collection<?>) value)
                    .as("Коллекция должна быть пустой")
                    .isEmpty();
        } else {
            throw new IllegalArgumentException("Значение не является строкой или коллекцией");
        }
    }

    @Override
    public String toString() {
        return "Значение пустое";
    }
}
