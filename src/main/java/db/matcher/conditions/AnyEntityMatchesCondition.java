package db.matcher.conditions;

import db.matcher.Condition;
import db.matcher.Conditions;
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
    public void check(List<T> entities) throws Exception {
        boolean matchFound = false;
        for (T entity : entities) {
            try {
                condition.check(entity);
                matchFound = true;
                break;
            } catch (Exception e) {
                throw e;
            }
        }
        Assertions.assertThat(matchFound)
                .as("Проверка, что хотя бы одна сущность соответствует условию '%s'", condition)
                .isTrue();
    }

    @Override
    public String toString() {
        return String.format("Хотя бы одна сущность соответствует условию '%s'", condition);
    }
}
