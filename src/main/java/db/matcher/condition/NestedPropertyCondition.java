package db.matcher.condition;

import db.matcher.Condition;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

/**
 * Условие для проверки вложенного свойства.
 *
 * @param <T> тип сущности
 * @param <R> тип вложенной сущности
 */
@RequiredArgsConstructor
public class NestedPropertyCondition<T, R> implements Condition<T> {

    private final Function<T, R> getter;
    private final Condition<R> nestedCondition;

    @Override
    public void check(T entity) {
        R nestedEntity = getter.apply(entity);
        nestedCondition.check(nestedEntity);
    }

    @Override
    public String toString() {
        return String.format("Проверка вложенного свойства с условием '%s'", nestedCondition);
    }
}
