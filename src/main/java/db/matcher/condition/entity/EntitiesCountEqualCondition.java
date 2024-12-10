package db.matcher.condition.entity;

import db.matcher.condition.Conditions;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Проверка, что количество сущностей равно указанному значению.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class EntitiesCountEqualCondition<T> implements Conditions<T> {

    private final int count;

    @Override
    public void check(List<T> entities) {
        Assertions.assertThat(entities)
                .as("Проверка, что количество сущностей равно %d", count)
                .hasSize(count);
    }

    @Override
    public String toString() {
        return String.format("Количество сущностей равно %d", count);
    }
}
