package db.matcher.condition.string;

import db.matcher.condition.Condition;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.function.Function;

/**
 * Проверка, что строковое свойство содержит указанный текст без учета регистра.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyContainsIgnoreCaseCondition<T> implements Condition<T> {

    private final Function<T, String> getter;
    private final String text;

    @Override
    public void check(T entity) {
        String actualValue = getter.apply(entity);
        Assertions.assertThat(actualValue)
                .as("Значение должно содержать '%s' без учета регистра", text)
                .isNotNull()
                .containsIgnoringCase(text);
    }

    @Override
    public String toString() {
        return String.format("Значение содержит '%s' без учета регистра", text);
    }
}
