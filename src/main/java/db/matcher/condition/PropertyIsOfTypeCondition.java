package db.matcher.condition;

import db.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.commons.beanutils.PropertyUtils;
import org.assertj.core.api.Assertions;

/**
 * Проверка, что свойство является экземпляром указанного типа.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyIsOfTypeCondition<T> implements Condition<T> {

    private final String propertyName;
    private final Class<?> expectedType;

    @Override
    public void check(T entity) throws Exception {
        Object value = PropertyUtils.getProperty(entity, propertyName);
        Assertions.assertThat(value)
                .as("Свойство '%s' не должно быть null", propertyName)
                .isNotNull();
        Assertions.assertThat(value.getClass())
                .as("Свойство '%s' должно быть типа %s", propertyName, expectedType.getName())
                .isEqualTo(expectedType);
    }

    @Override
    public String toString() {
        return String.format("Свойство '%s' является экземпляром %s", propertyName, expectedType.getName());
    }
}
