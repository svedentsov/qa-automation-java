package db.matcher.condition;

import db.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.function.Function;

/**
 * Проверка, что свойство является подклассом указанного типа или реализует указанный интерфейс.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyIsAssignableFromCondition<T> implements Condition<T> {

    private final Function<T, ?> getter;
    private final Class<?> expectedSuperType;

    @Override
    public void check(T entity) {
        Object value = getter.apply(entity);
        Assertions.assertThat(value)
                .as("Значение не должно быть null")
                .isNotNull();
        Assertions.assertThat(expectedSuperType.isAssignableFrom(value.getClass()))
                .as("Значение должно быть подклассом %s", expectedSuperType.getName())
                .isTrue();
    }

    @Override
    public String toString() {
        return String.format("Значение является подклассом %s", expectedSuperType.getName());
    }
}
