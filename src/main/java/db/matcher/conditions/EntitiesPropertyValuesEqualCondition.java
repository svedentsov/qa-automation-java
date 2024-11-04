package db.matcher.conditions;

import db.matcher.Conditions;
import lombok.RequiredArgsConstructor;
import org.apache.commons.beanutils.PropertyUtils;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Проверка, что все сущности имеют свойство с указанным значением.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class EntitiesPropertyValuesEqualCondition<T> implements Conditions<T> {

    private final String propertyName;
    private final Object expectedValue;

    @Override
    public void check(List<T> entities) throws Exception {
        for (T entity : entities) {
            Object actualValue = PropertyUtils.getProperty(entity, propertyName);
            Assertions.assertThat(actualValue)
                    .as("Свойство '%s' должно быть равно '%s'", propertyName, expectedValue)
                    .isEqualTo(expectedValue);
        }
    }

    @Override
    public String toString() {
        return String.format("Все сущности имеют свойство '%s', равное '%s'", propertyName, expectedValue);
    }
}
