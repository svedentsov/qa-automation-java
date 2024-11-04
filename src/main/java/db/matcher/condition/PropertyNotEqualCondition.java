package db.matcher.condition;

import db.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.commons.beanutils.PropertyUtils;
import org.assertj.core.api.Assertions;

/**
 * Проверка, что свойство сущности не равно указанному значению.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyNotEqualCondition<T> implements Condition<T> {

    private final String propertyName;
    private final Object unexpectedValue;

    @Override
    public void check(T entity) throws Exception {
        Object actualValue = PropertyUtils.getProperty(entity, propertyName);
        Assertions.assertThat(actualValue)
                .as("Проверка, что свойство '%s' не равно '%s'", propertyName, unexpectedValue)
                .isNotEqualTo(unexpectedValue);
    }

    @Override
    public String toString() {
        return String.format("Свойство '%s' не равно '%s'", propertyName, unexpectedValue);
    }
}
