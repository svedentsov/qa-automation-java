package db.matcher.condition.logical;

import db.matcher.condition.Condition;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

/**
 * Условие, которое инвертирует результат другого условия.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class NotCondition<T> implements Condition<T> {

    private final Condition<T> condition;

    @Override
    public void check(T entity) {
        try {
            condition.check(entity);
            Assertions.fail("Условие должно было не выполняться, но выполнилось: '%s'", condition);
        } catch (AssertionError ignored) {
            // Ожидаемое поведение
        }
    }

    @Override
    public String toString() {
        return "NOT условие для " + condition;
    }
}
