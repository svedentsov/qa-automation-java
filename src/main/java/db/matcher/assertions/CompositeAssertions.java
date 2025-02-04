package db.matcher.assertions;

import db.matcher.condition.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.Arrays;

/**
 * Утилитный класс для композиционных (логических) операций над условиями: AND, OR, NOT, а также "хотя бы N из M".
 */
@UtilityClass
public class CompositeAssertions {

    /**
     * Возвращает условие, представляющее логическую операцию И (AND) для нескольких условий.
     *
     * @param conditions набор условий
     * @param <T>        тип проверяемой сущности
     * @return условие, которое считается выполненным, если выполнены все переданные условия
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
     * Возвращает условие, представляющее логическую операцию ИЛИ (OR) для нескольких условий.
     *
     * @param conditions набор условий
     * @param <T>        тип проверяемой сущности
     * @return условие, которое считается выполненным, если выполнено хотя бы одно из переданных условий
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
     * Возвращает условие, представляющее логическую операцию НЕ (NOT) для одного или нескольких условий.
     *
     * @param conditions набор условий
     * @param <T>        тип проверяемой сущности
     * @return условие, которое считается выполненным, если ни одно из переданных условий не выполнено
     */
    @SafeVarargs
    public static <T> Condition<T> not(Condition<T>... conditions) {
        return entity -> {
            for (Condition<T> condition : conditions) {
                try {
                    condition.check(entity);
                    Assertions.fail("Условие должно было не выполняться, но выполнилось: " + condition);
                } catch (AssertionError expected) {
                    // Ожидаемое поведение: условие не выполнено
                }
            }
        };
    }

    /**
     * Возвращает условие, которое проверяет, что хотя бы n из переданных условий выполнены.
     *
     * @param n          минимальное число условий, которые должны выполниться
     * @param conditions набор условий
     * @param <T>        тип проверяемой сущности
     * @return условие, которое считается выполненным, если выполнено хотя бы n условий
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
