package db.matcher.condition;

import db.matcher.Condition;
import org.assertj.core.api.Assertions;

/**
 * Условие, объединяющее несколько условий с помощью логической операции OR.
 *
 * @param <T> тип сущности
 */
public class OrCondition<T> extends CompositeCondition<T> {

    @SafeVarargs
    public OrCondition(Condition<T>... conditions) {
        super(conditions);
    }

    @Override
    public void check(T entity) {
        boolean anyMatch = false;
        for (Condition<T> condition : conditions) {
            try {
                condition.check(entity);
                anyMatch = true;
                break;
            } catch (AssertionError ignored) {
                // Пропускаем, если условие не выполнено
            }
        }
        Assertions.assertThat(anyMatch)
                .as("Ни одно из условий не выполнено: %s", conditions)
                .isTrue();
    }

    @Override
    public String toString() {
        return "OR условие с условиями " + conditions;
    }
}
