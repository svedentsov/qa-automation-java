package db.matcher.condition.numeric;

import db.matcher.condition.Condition;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * Проверка, что числовое свойство меньше заданного значения.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyLessThanCondition<T> implements Condition<T> {

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
                .as("Проверка, что значение меньше %s", value)
                .isLessThan(value);
    }

    @Override
    public String toString() {
        return String.format("Значение меньше %s", value);
    }
}
