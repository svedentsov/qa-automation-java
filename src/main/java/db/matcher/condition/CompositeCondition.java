package db.matcher.condition;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * Базовый класс для составных условий.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public abstract class CompositeCondition<T> implements Condition<T> {

    protected final List<Condition<T>> conditions;

    @SafeVarargs
    public CompositeCondition(Condition<T>... conditions) {
        this.conditions = Arrays.asList(conditions);
    }
}
