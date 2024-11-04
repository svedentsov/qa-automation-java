package db.matcher.condition;

import db.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.commons.beanutils.PropertyUtils;
import org.assertj.core.api.Assertions;

import java.math.BigDecimal;

/**
 * Проверка, что числовое свойство находится в заданном диапазоне.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyBetweenCondition<T> implements Condition<T> {

    private final String propertyName;
    private final BigDecimal start;
    private final BigDecimal end;

    @Override
    public void check(T entity) throws Exception {
        Object actualValue = PropertyUtils.getProperty(entity, propertyName);
        Assertions.assertThat(actualValue)
                .as("Свойство '%s' должно быть числом", propertyName)
                .isInstanceOf(Number.class);
        BigDecimal actualNumber = new BigDecimal(actualValue.toString());
        Assertions.assertThat(actualNumber)
                .as("Проверка, что свойство '%s' между '%s' и '%s'", propertyName, start, end)
                .isBetween(start, end);
    }

    @Override
    public String toString() {
        return String.format("Свойство '%s' между '%s' и '%s'", propertyName, start, end);
    }
}
