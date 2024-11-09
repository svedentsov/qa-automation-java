package db.matcher.condition;

import db.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.Collection;
import java.util.function.Function;

/**
 * Проверка, что длина свойства больше заданного значения.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyLengthGreaterThanCondition<T> implements Condition<T> {

    private final Function<T, ?> getter;
    private final int minLength;

    @Override
    public void check(T entity) {
        Object value = getter.apply(entity);
        int actualLength = getLength(value);
        Assertions.assertThat(actualLength)
                .as("Длина значения должна быть больше %d", minLength)
                .isGreaterThan(minLength);
    }

    private int getLength(Object value) {
        Assertions.assertThat(value)
                .as("Значение не должно быть null")
                .isNotNull();

        if (value instanceof String) {
            return ((String) value).length();
        } else if (value instanceof Collection) {
            return ((Collection<?>) value).size();
        } else if (value.getClass().isArray()) {
            return ((Object[]) value).length;
        } else {
            throw new IllegalArgumentException("Значение не является строкой, коллекцией или массивом");
        }
    }

    @Override
    public String toString() {
        return String.format("Длина значения больше %d", minLength);
    }
}
