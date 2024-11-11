package db.matcher.condition.entity;

import db.matcher.condition.Conditions;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.function.Function;

/**
 * Проверка, что все сущности имеют свойство с указанным значением.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class EntitiesPropertyValuesEqualCondition<T> implements Conditions<T> {

    private final Function<T, ?> getter;
    private final Object expectedValue;

    @Override
    public void check(List<T> entities) {
        for (T entity : entities) {
            Object actualValue = getter.apply(entity);
            Assertions.assertThat(actualValue)
                    .as("Значение должно быть равно '%s'", expectedValue)
                    .isEqualTo(expectedValue);
        }
    }

    @Override
    public String toString() {
        return String.format("Все сущности имеют значение, равное '%s'", expectedValue);
    }
}
