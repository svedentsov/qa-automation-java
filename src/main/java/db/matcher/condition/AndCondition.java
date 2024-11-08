package db.matcher.condition;

/**
 * Условие, объединяющее несколько условий с помощью логической операции AND.
 *
 * @param <T> тип сущности
 */
public class AndCondition<T> extends CompositeCondition<T> {

    @SafeVarargs
    public AndCondition(Condition<T>... conditions) {
        super(conditions);
    }

    @Override
    public void check(T entity) {
        for (Condition<T> condition : conditions) {
            condition.check(entity);
        }
    }

    @Override
    public String toString() {
        return "AND условие с условиями " + conditions;
    }
}
