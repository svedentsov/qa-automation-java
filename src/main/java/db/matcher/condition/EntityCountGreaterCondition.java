package db.matcher.condition;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Проверка, что количество сущностей больше указанного.
 */
@RequiredArgsConstructor
public class EntityCountGreaterCondition<T> implements Conditions<T> {

    private final int count;

    @Override
    public void check(List<T> entities) {
        Assertions.assertThat(entities)
                .as("Проверка, что количество сущностей больше " + count)
                .hasSizeGreaterThan(count);
    }

    @Override
    public String toString() {
        return "Условие, что количество сущностей больше " + count;
    }
}
