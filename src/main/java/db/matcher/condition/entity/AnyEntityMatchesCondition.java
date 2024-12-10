package db.matcher.condition.entity;

import db.matcher.condition.Condition;
import db.matcher.condition.Conditions;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Проверка, что хотя бы одна сущность в списке соответствует указанному условию.
 *
 * @param <T> тип сущности
 */
@RequiredArgsConstructor
public class AnyEntityMatchesCondition<T> implements Conditions<T> {

    private final Condition<T> condition;

    @Override
    public void check(List<T> entities) {
        boolean matchFound = false;
        for (T entity : entities) {
            try {
                condition.check(entity);
                matchFound = true;
                break;
            } catch (Exception e) {
                throw e; // Ожидаемое поведение, если сущность не соответствует условию
            }
        }
        Assertions.assertThat(matchFound)
                .as("Проверка, что хотя бы одна сущность соответствует условию %s", condition)
                .isTrue();
    }

    @Override
    public String toString() {
        return String.format("Хотя бы одна сущность соответствует условию %s", condition);
    }
}
