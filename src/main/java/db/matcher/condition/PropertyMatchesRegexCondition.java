package db.matcher.condition;

import db.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.commons.beanutils.PropertyUtils;
import org.assertj.core.api.Assertions;

import java.util.regex.Pattern;

/**
 * Проверка, что строковое свойство соответствует регулярному выражению.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyMatchesRegexCondition<T> implements Condition<T> {

    private final String propertyName;
    private final String regex;

    @Override
    public void check(T entity) throws Exception {
        Object actualValue = PropertyUtils.getProperty(entity, propertyName);
        Pattern pattern = Pattern.compile(regex);
        Assertions.assertThat(actualValue)
                .as("Свойство '%s' должно быть строкой", propertyName)
                .isInstanceOf(String.class);
        Assertions.assertThat((String) actualValue)
                .as("Проверка, что свойство '%s' соответствует регулярному выражению '%s'", propertyName, regex)
                .matches(pattern);
    }

    @Override
    public String toString() {
        return String.format("Свойство '%s' соответствует регулярному выражению '%s'", propertyName, regex);
    }
}
