package db.matcher.assertions;

import db.matcher.condition.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.time.LocalDateTime;
import java.util.function.Function;

/**
 * Утилитный класс для проверки временных (LocalDateTime) свойств в сущности.
 */
@UtilityClass
public class TimestampAssertions {

    /**
     * Проверяет, что LocalDateTime-свойство раньше (до) указанного момента.
     */
    public static <T> Condition<T> dateBefore(Function<T, LocalDateTime> getter, LocalDateTime dateTime) {
        return entity -> {
            LocalDateTime actual = getter.apply(entity);
            Assertions.assertThat(actual)
                    .as("Дата должна быть до %s", dateTime)
                    .isNotNull()
                    .isBefore(dateTime);
        };
    }

    /**
     * Проверяет, что LocalDateTime-свойство после (строго позже) указанного момента.
     */
    public static <T> Condition<T> localDateTimeAfter(Function<T, LocalDateTime> getter, LocalDateTime dateTime) {
        return entity -> {
            LocalDateTime actual = getter.apply(entity);
            Assertions.assertThat(actual)
                    .as("Дата и время должны быть после %s", dateTime)
                    .isNotNull()
                    .isAfter(dateTime);
        };
    }

    /**
     * Проверяет, что LocalDateTime-свойство в будущем (после настоящего момента).
     */
    public static <T> Condition<T> isInFuture(Function<T, LocalDateTime> getter) {
        return entity -> {
            LocalDateTime actual = getter.apply(entity);
            Assertions.assertThat(actual)
                    .as("Дата и время должны быть в будущем")
                    .isNotNull()
                    .isAfter(LocalDateTime.now());
        };
    }

    /**
     * Проверяет, что LocalDateTime-свойство в прошлом (до настоящего момента).
     */
    public static <T> Condition<T> isInPast(Function<T, LocalDateTime> getter) {
        return entity -> {
            LocalDateTime actual = getter.apply(entity);
            Assertions.assertThat(actual)
                    .as("Дата и время должны быть в прошлом")
                    .isNotNull()
                    .isBefore(LocalDateTime.now());
        };
    }
}
