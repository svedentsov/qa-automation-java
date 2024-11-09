package db.matcher.condition;

import db.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.Collection;
import java.util.function.Function;

/**
 * Проверяет, что все элементы коллекции внутри сущности соответствуют условию.
 *
 * @param <T> тип сущности
 * @param <E> тип элементов коллекции
 */
@RequiredArgsConstructor
public class AllCollectionElementsMatchCondition<T, E> implements Condition<T> {

    private final Function<T, Collection<E>> getter;
    private final Condition<E> elementCondition;

    @Override
    public void check(T entity) {
        Collection<E> collection = getter.apply(entity);
        Assertions.assertThat(collection)
                .as("Коллекция не должна быть пустой")
                .isNotEmpty();
        for (E element : collection) {
            elementCondition.check(element);
        }
    }

    @Override
    public String toString() {
        return String.format("Все элементы коллекции соответствуют условию '%s'", elementCondition);
    }
}
