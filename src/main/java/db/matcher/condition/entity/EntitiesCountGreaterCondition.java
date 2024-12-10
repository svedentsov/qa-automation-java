package db.matcher.condition.entity;

import db.matcher.condition.Conditions;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Проверка, что количество сущностей больше указанного.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class EntitiesCountGreaterCondition<T> implements Conditions<T> {

    private final int count;

    @Override
    public void check(List<T> entities) {
        Assertions.assertThat(entities)
                .as("Проверка, что количество сущностей больше %d", count)
                .hasSizeGreaterThan(count);
    }

    @Override
    public String toString() {
        return String.format("Количество сущностей больше %d", count);
    }
}
