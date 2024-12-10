package db.matcher.condition.property;

import db.matcher.condition.Condition;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.Map;
import java.util.function.Function;

/**
 * Проверка, что все указанные свойства имеют ожидаемые значения.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class AllPropertiesEqualCondition<T> implements Condition<T> {

    private final Map<Function<T, ?>, Object> expectedProperties;

    @Override
    public void check(T entity) {
        for (Map.Entry<Function<T, ?>, Object> entry : expectedProperties.entrySet()) {
            Function<T, ?> getter = entry.getKey();
            Object expectedValue = entry.getValue();
            Object actualValue = getter.apply(entity);
            Assertions.assertThat(actualValue)
                    .as("Проверка, что значение равно %s", expectedValue)
                    .isEqualTo(expectedValue);
        }
    }

    @Override
    public String toString() {
        return String.format("Свойства равны ожидаемым значениям %s", expectedProperties);
    }
}
