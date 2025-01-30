package db.matcher.assertions;

import db.matcher.condition.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.Arrays;

/**
 * Утилитный класс для композиционных (логических) операций над условиями: AND, OR, NOT, а также
 * примера "хотя бы N из M" (nOf).
 */
@UtilityClass
public class CompositeAssertions {

    /**
     * Логическое И (AND) для нескольких условий.
     *
     * @param conditions условия
     * @param <T>        тип сущности
     * @return новое условие, которое пройдёт только если все условия верны
     */
    @SafeVarargs
    public static <T> Condition<T> and(Condition<T>... conditions) {
        return entity -> {
            for (Condition<T> condition : conditions) {
                condition.check(entity);
            }
        };
    }

    /**
     * Логическое ИЛИ (OR) для нескольких условий.
     *
     * @param conditions условия
     * @param <T>        тип сущности
     * @return новое условие, которое пройдёт, если хотя бы одно из условий верно
     */
    @SafeVarargs
    public static <T> Condition<T> or(Condition<T>... conditions) {
        return entity -> {
            boolean atLeastOnePassed = Arrays.stream(conditions).anyMatch(cond -> {
                try {
                    cond.check(entity);
                    return true;
                } catch (AssertionError ignore) {
                    return false;
                }
            });
            Assertions.assertThat(atLeastOnePassed)
                    .as("Ни одно из OR-условий не выполнено")
                    .isTrue();
        };
    }

    /**
     * Логическое НЕ (NOT) для одного или нескольких условий.
     *
     * @param conditions условия
     * @param <T>        тип сущности
     * @return новое условие, которое пройдёт, только если все указанные условия НЕ выполнены
     */
    @SafeVarargs
    public static <T> Condition<T> not(Condition<T>... conditions) {
        return entity -> {
            for (Condition<T> condition : conditions) {
                try {
                    condition.check(entity);
                    Assertions.fail("Условие должно было не выполняться, но выполнилось: " + condition);
                } catch (AssertionError expected) {
                    // ожидаемое поведение: условие не выполнено
                }
            }
        };
    }

    /**
     * Проверяет, что хотя бы N из заданных условий истинны.
     *
     * @param n          минимальное число условий, которые должны быть выполнены
     * @param conditions набор условий
     * @param <T>        тип сущности
     * @return новое условие, которое пройдёт, если хотя бы N условий истинны
     */
    @SafeVarargs
    public static <T> Condition<T> nOf(int n, Condition<T>... conditions) {
        return entity -> {
            long successCount = Arrays.stream(conditions)
                    .filter(cond -> {
                        try {
                            cond.check(entity);
                            return true;
                        } catch (AssertionError e) {
                            return false;
                        }
                    })
                    .count();
            Assertions.assertThat(successCount)
                    .as("Ожидалось, что хотя бы %d условий будут выполнены, но выполнено %d", n, successCount)
                    .isGreaterThanOrEqualTo(n);
        };
    }
}
