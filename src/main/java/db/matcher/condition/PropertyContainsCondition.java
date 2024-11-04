package db.matcher.condition;

import db.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.commons.beanutils.PropertyUtils;
import org.assertj.core.api.Assertions;

/**
 * Проверка, что строковое свойство содержит указанный текст.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyContainsCondition<T> implements Condition<T> {

    private final String propertyName;
    private final String text;

    @Override
    public void check(T entity) throws Exception {
        Object actualValue = PropertyUtils.getProperty(entity, propertyName);
        Assertions.assertThat(actualValue)
                .as("Свойство '%s' должно быть строкой", propertyName)
                .isInstanceOf(String.class);
        Assertions.assertThat((String) actualValue)
                .as("Проверка, что свойство '%s' содержит '%s'", propertyName, text)
                .contains(text);
    }

    @Override
    public String toString() {
        return String.format("Свойство '%s' содержит '%s'", propertyName, text);
    }
}
