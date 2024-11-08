package db.matcher.condition;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.time.LocalDateTime;
import java.util.function.Function;

/**
 * Проверка, что дата до заданной даты.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyDateBeforeCondition<T> implements Condition<T> {

    private final Function<T, LocalDateTime> getter;
    private final LocalDateTime dateTime;

    @Override
    public void check(T entity) {
        LocalDateTime actualDate = getter.apply(entity);
        Assertions.assertThat(actualDate)
                .as("Дата должна быть до %s", dateTime)
                .isBefore(dateTime);
    }

    @Override
    public String toString() {
        return String.format("Дата до %s", dateTime);
    }
}
