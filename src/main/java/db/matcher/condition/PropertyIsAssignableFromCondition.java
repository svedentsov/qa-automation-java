package db.matcher.condition;

import db.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.commons.beanutils.PropertyUtils;
import org.assertj.core.api.Assertions;

/**
 * Проверка, что свойство является подклассом указанного типа или реализует указанный интерфейс.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyIsAssignableFromCondition<T> implements Condition<T> {

    private final String propertyName;
    private final Class<?> expectedSuperType;

    @Override
    public void check(T entity) throws Exception {
        Object value = PropertyUtils.getProperty(entity, propertyName);
        Assertions.assertThat(value)
                .as("Свойство '%s' не должно быть null", propertyName)
                .isNotNull();
        Assertions.assertThat(expectedSuperType.isAssignableFrom(value.getClass()))
                .as("Свойство '%s' должно быть подклассом %s", propertyName, expectedSuperType.getName())
                .isTrue();
    }

    @Override
    public String toString() {
        return String.format("Свойство '%s' является подклассом %s", propertyName, expectedSuperType.getName());
    }
}
