package db.matcher.condition.datetime;

import db.matcher.condition.Condition;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.time.LocalDateTime;
import java.util.function.Function;

/**
 * Проверка, что LocalDateTime после заданной даты и времени.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class PropertyLocalDateTimeAfterCondition<T> implements Condition<T> {

    private final Function<T, LocalDateTime> getter;
    private final LocalDateTime dateTime;

    @Override
    public void check(T entity) {
        LocalDateTime actualDateTime = getter.apply(entity);
        Assertions.assertThat(actualDateTime)
                .as("Дата и время должны быть после %s", dateTime)
                .isAfter(dateTime);
    }

    @Override
    public String toString() {
        return String.format("Дата и время после %s", dateTime);
    }
}
