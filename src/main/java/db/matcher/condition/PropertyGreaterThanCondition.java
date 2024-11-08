package db.matcher.condition;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * Проверка, что числовое свойство больше заданного значения.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyGreaterThanCondition<T> implements Condition<T> {

    private final Function<T, Number> getter;
    private final BigDecimal value;

    @Override
    public void check(T entity) {
        Number actualValue = getter.apply(entity);
        Assertions.assertThat(actualValue)
                .as("Значение должно быть числом")
                .isInstanceOf(Number.class);
        BigDecimal actualNumber = new BigDecimal(actualValue.toString());
        Assertions.assertThat(actualNumber)
                .as("Проверка, что значение больше '%s'", value)
                .isGreaterThan(value);
    }

    @Override
    public String toString() {
        return String.format("Значение больше '%s'", value);
    }
}