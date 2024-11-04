package db.matcher.condition;

import db.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.commons.beanutils.PropertyUtils;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Проверка, что свойство не входит в заданный список значений.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyNotInCondition<T> implements Condition<T> {

    private final String propertyName;
    private final List<?> values;

    @Override
    public void check(T entity) throws Exception {
        Object actualValue = PropertyUtils.getProperty(entity, propertyName);
        Assertions.assertThat(values)
                .as("Список значений для сравнения не должен быть пустым")
                .isNotEmpty();
        Assertions.assertThat(actualValue)
                .as("Проверка, что свойство '%s' не входит в список значений", propertyName)
                .isNotIn(values);
    }

    @Override
    public String toString() {
        return String.format("Свойство '%s' не входит в список значений '%s'", propertyName, values);
    }
}
