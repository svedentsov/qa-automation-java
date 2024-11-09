package db.matcher.condition;

import db.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.function.Function;

/**
 * Проверка, что свойство сущности равно ожидаемому значению с кастомным сообщением об ошибке.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyEqualWithMessageCondition<T> implements Condition<T> {

    private final Function<T, ?> getter;
    private final Object expectedValue;
    private final String errorMessage;

    @Override
    public void check(T entity) {
        Object actualValue = getter.apply(entity);
        if (errorMessage != null) {
            Assertions.assertThat(actualValue)
                    .withFailMessage(errorMessage)
                    .isEqualTo(expectedValue);
        } else {
            Assertions.assertThat(actualValue)
                    .as("Проверка, что значение равно '%s'", expectedValue)
                    .isEqualTo(expectedValue);
        }
    }

    @Override
    public String toString() {
        return String.format("Значение равно '%s'", expectedValue);
    }
}