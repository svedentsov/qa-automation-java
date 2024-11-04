package db.matcher.condition;

import db.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.commons.beanutils.PropertyUtils;
import org.assertj.core.api.Assertions;

import java.math.BigDecimal;

/**
 * Проверка, что числовое свойство больше заданного значения.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyGreaterThanCondition<T> implements Condition<T> {

    private final String propertyName;
    private final BigDecimal value;

    @Override
    public void check(T entity) throws Exception {
        Object actualValue = PropertyUtils.getProperty(entity, propertyName);
        Assertions.assertThat(actualValue)
                .as("Свойство '%s' должно быть числом", propertyName)
                .isInstanceOf(Number.class);
        BigDecimal actualNumber = new BigDecimal(actualValue.toString());
        Assertions.assertThat(actualNumber)
                .as("Проверка, что свойство '%s' больше '%s'", propertyName, value)
                .isGreaterThan(value);
    }

    @Override
    public String toString() {
        return String.format("Свойство '%s' больше '%s'", propertyName, value);
    }
}
