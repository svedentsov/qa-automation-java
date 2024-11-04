package db.matcher.conditions;

import db.matcher.Condition;
import db.matcher.Conditions;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Проверка, что все сущности в списке соответствуют указанному условию.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class AllEntitiesMatchCondition<T> implements Conditions<T> {

    private final Condition<T> condition;

    @Override
    public void check(List<T> entities) throws Exception {
        for (T entity : entities) {
            condition.check(entity);
        }
    }

    @Override
    public String toString() {
        return String.format("Все сущности соответствуют условию '%s'", condition);
    }
}
