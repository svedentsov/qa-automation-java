package db.matcher.condition;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.function.Function;

/**
 * Проверка, что свойство перечисление имеет определенное значение.
 *
 * @param <T> тип сущности
 * @param <E> тип перечисления
 */
@RequiredArgsConstructor
public class PropertyEnumValueCondition<T, E extends Enum<E>> implements Condition<T> {

    private final Function<T, E> getter;
    private final E expectedValue;

    @Override
    public void check(T entity) {
        E actualValue = getter.apply(entity);
        Assertions.assertThat(actualValue)
                .as("Проверка, что перечисление имеет значение '%s'", expectedValue)
                .isEqualTo(expectedValue);
    }

    @Override
    public String toString() {
        return String.format("Перечисление имеет значение '%s'", expectedValue);
    }
}
