package db.matcher.condition;

import db.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * Проверка, что числовое свойство находится в заданном диапазоне.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyBetweenCondition<T> implements Condition<T> {

    private final Function<T, Number> getter;
    private final BigDecimal start;
    private final BigDecimal end;

    @Override
    public void check(T entity) {
        Number actualValue = getter.apply(entity);
        Assertions.assertThat(actualValue)
                .as("Значение должно быть числом")
                .isInstanceOf(Number.class);
        BigDecimal actualNumber = new BigDecimal(actualValue.toString());
        Assertions.assertThat(actualNumber)
                .as("Проверка, что значение между '%s' и '%s'", start, end)
                .isBetween(start, end);
    }

    @Override
    public String toString() {
        return String.format("Значение между '%s' и '%s'", start, end);
    }
}
