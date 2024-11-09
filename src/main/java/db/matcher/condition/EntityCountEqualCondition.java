package db.matcher.condition;

import db.matcher.Conditions;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Проверка, что количество сущностей равно указанному значению.
 */
@RequiredArgsConstructor
public class EntityCountEqualCondition<T> implements Conditions<T> {

    private final int count;

    @Override
    public void check(List<T> entities) {
        Assertions.assertThat(entities)
                .as("Проверка, что количество сущностей равно " + count)
                .hasSize(count);
    }

    @Override
    public String toString() {
        return "Условие, что количество сущностей равно " + count;
    }
}
