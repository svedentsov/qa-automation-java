package db.matcher.condition;

import db.matcher.Conditions;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Проверка наличия хотя бы одной сущности в списке.
 */
public class EntityExistsCondition<T> implements Conditions<T> {

    @Override
    public void check(List<T> entities) {
        Assertions.assertThat(entities)
                .as("Проверка наличия хотя бы одной сущности")
                .isNotEmpty();
    }

    @Override
    public String toString() {
        return "Условие наличия хотя бы одной сущности";
    }
}
