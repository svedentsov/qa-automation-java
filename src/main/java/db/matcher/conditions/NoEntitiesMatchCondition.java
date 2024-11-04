package db.matcher.conditions;

import db.matcher.Condition;
import db.matcher.Conditions;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Проверка, что ни одна сущность в списке не соответствует указанному условию.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class NoEntitiesMatchCondition<T> implements Conditions<T> {

    private final Condition<T> condition;

    @Override
    public void check(List<T> entities) throws Exception {
        for (T entity : entities) {
            try {
                condition.check(entity);
                Assertions.fail("Найдена сущность, соответствующая условию '%s'", condition);
            } catch (AssertionError | Exception e) {
                // Ожидаемое поведение, сущность не должна соответствовать условию
            }
        }
    }

    @Override
    public String toString() {
        return String.format("Ни одна сущность не соответствует условию '%s'", condition);
    }
}
