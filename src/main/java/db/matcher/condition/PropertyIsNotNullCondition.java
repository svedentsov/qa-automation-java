package db.matcher.condition;

import db.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.commons.beanutils.PropertyUtils;
import org.assertj.core.api.Assertions;

/**
 * Проверка, что свойство не является null.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyIsNotNullCondition<T> implements Condition<T> {

    private final String propertyName;

    @Override
    public void check(T entity) throws Exception {
        Object actualValue = PropertyUtils.getProperty(entity, propertyName);
        Assertions.assertThat(actualValue)
                .as("Проверка, что свойство '%s' не является null", propertyName)
                .isNotNull();
    }

    @Override
    public String toString() {
        return String.format("Свойство '%s' не является null", propertyName);
    }
}
