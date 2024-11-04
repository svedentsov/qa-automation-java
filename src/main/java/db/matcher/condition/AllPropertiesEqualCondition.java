package db.matcher.condition;

import db.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.commons.beanutils.PropertyUtils;
import org.assertj.core.api.Assertions;

import java.util.Map;

/**
 * Проверка, что все указанные свойства имеют ожидаемые значения.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class AllPropertiesEqualCondition<T> implements Condition<T> {

    private final Map<String, Object> expectedProperties;

    @Override
    public void check(T entity) throws Exception {
        for (Map.Entry<String, Object> entry : expectedProperties.entrySet()) {
            String propertyName = entry.getKey();
            Object expectedValue = entry.getValue();
            Object actualValue = PropertyUtils.getProperty(entity, propertyName);
            Assertions.assertThat(actualValue)
                    .as("Проверка, что свойство '%s' равно '%s'", propertyName, expectedValue)
                    .isEqualTo(expectedValue);
        }
    }

    @Override
    public String toString() {
        return String.format("Свойства равны ожидаемым значениям '%s'", expectedProperties);
    }
}
