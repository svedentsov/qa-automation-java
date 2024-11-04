package db.matcher.condition;

import db.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.commons.beanutils.PropertyUtils;
import org.assertj.core.api.Assertions;

/**
 * Проверка, что свойство сущности равно ожидаемому значению.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyEqualCondition<T> implements Condition<T> {

    private final String propertyName;
    private final Object expectedValue;

    @Override
    public void check(T entity) throws Exception {
        Object actualValue = PropertyUtils.getProperty(entity, propertyName);
        Assertions.assertThat(actualValue)
                .as("Проверка, что свойство '%s' равно '%s'", propertyName, expectedValue)
                .isEqualTo(expectedValue);
    }

    @Override
    public String toString() {
        return String.format("Свойство '%s' равно '%s'", propertyName, expectedValue);
    }
}
