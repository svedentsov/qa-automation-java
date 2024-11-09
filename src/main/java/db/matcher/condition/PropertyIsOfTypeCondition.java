package db.matcher.condition;

import db.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.function.Function;

/**
 * Проверка, что свойство является экземпляром указанного типа.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyIsOfTypeCondition<T> implements Condition<T> {

    private final Function<T, ?> getter;
    private final Class<?> expectedType;

    @Override
    public void check(T entity) {
        Object value = getter.apply(entity);
        Assertions.assertThat(value)
                .as("Значение не должно быть null")
                .isNotNull();
        Assertions.assertThat(value.getClass())
                .as("Значение должно быть типа %s", expectedType.getName())
                .isEqualTo(expectedType);
    }

    @Override
    public String toString() {
        return String.format("Значение является экземпляром %s", expectedType.getName());
    }
}
