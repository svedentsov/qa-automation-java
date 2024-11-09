package db.matcher.condition;

import db.matcher.Conditions;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Проверка наличия хотя бы одной сущности в списке.
 *
 * @param <T> тип сущности
 */
public class EntitiesExistCondition<T> implements Conditions<T> {

    @Override
    public void check(List<T> entities) {
        Assertions.assertThat(entities)
                .as("Проверка наличия хотя бы одной сущности")
                .isNotEmpty();
    }

    @Override
    public String toString() {
        return "Наличие хотя бы одной сущности";
    }
}
