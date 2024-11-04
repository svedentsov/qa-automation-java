package db.matcher.condition;

import db.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.commons.beanutils.PropertyUtils;
import org.assertj.core.api.Assertions;

import java.util.Collection;

/**
 * Проверка, что длина свойства равна заданному значению.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyLengthEqualCondition<T> implements Condition<T> {

    private final String propertyName;
    private final int expectedLength;

    @Override
    public void check(T entity) throws Exception {
        Object value = PropertyUtils.getProperty(entity, propertyName);
        int actualLength = getLength(value, propertyName);
        Assertions.assertThat(actualLength)
                .as("Длина свойства '%s' должна быть равна %d", propertyName, expectedLength)
                .isEqualTo(expectedLength);
    }

    private int getLength(Object value, String propertyName) {
        Assertions.assertThat(value)
                .as("Свойство '%s' не должно быть null", propertyName)
                .isNotNull();

        if (value instanceof String) {
            return ((String) value).length();
        } else if (value instanceof Collection) {
            return ((Collection<?>) value).size();
        } else if (value.getClass().isArray()) {
            return ((Object[]) value).length;
        } else {
            throw new IllegalArgumentException(String.format("Свойство '%s' не является строкой, коллекцией или массивом", propertyName));
        }
    }

    @Override
    public String toString() {
        return String.format("Длина свойства '%s' равна %d", propertyName, expectedLength);
    }
}
