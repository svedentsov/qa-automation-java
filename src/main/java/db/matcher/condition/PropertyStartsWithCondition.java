package db.matcher.condition;

import db.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.commons.beanutils.PropertyUtils;
import org.assertj.core.api.Assertions;

/**
 * Проверка, что строковое свойство начинается с указанного префикса.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyStartsWithCondition<T> implements Condition<T> {

    private final String propertyName;
    private final String prefix;

    @Override
    public void check(T entity) throws Exception {
        Object value = PropertyUtils.getProperty(entity, propertyName);
        Assertions.assertThat(value)
                .as("Свойство '%s' должно быть строкой", propertyName)
                .isInstanceOf(String.class);
        Assertions.assertThat((String) value)
                .as("Проверка, что свойство '%s' начинается с '%s'", propertyName, prefix)
                .startsWith(prefix);
    }

    @Override
    public String toString() {
        return String.format("Свойство '%s' начинается с '%s'", propertyName, prefix);
    }
}
