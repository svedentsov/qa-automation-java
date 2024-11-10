package db.matcher.condition;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.Optional;
import java.util.function.Function;

/**
 * Проверка, что Optional свойство присутствует (не пустое).
 *
 * @param <T> тип сущности
 * @param <R> тип значения в Optional
 */
@RequiredArgsConstructor
public class OptionalPropertyPresentCondition<T, R> implements Condition<T> {

    private final Function<T, Optional<R>> getter;

    @Override
    public void check(T entity) {
        Optional<R> optionalValue = getter.apply(entity);
        Assertions.assertThat(optionalValue)
                .as("Optional значение должно быть присутствующим")
                .isPresent();
    }

    @Override
    public String toString() {
        return "Optional значение присутствует";
    }
}
