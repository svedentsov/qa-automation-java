package kafka.matcher.assertions;

import kafka.matcher.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.Arrays;

/**
 * Утилитный класс для создания составных проверок (Checker) для произвольного типа T.
 * Предоставляет методы для объединения проверок через логические операции AND, OR, NOT и nOf.
 */
@UtilityClass
public class CompositeAssertions {

    /**
     * Возвращает составную проверку, которая проходит только если все переданные проверки выполнены.
     *
     * @param conditions массив проверок для объединения
     * @param <T>      тип проверяемой сущности
     * @return составная проверка, реализующая логическую операцию AND
     */
    @SafeVarargs
    public static <T> Condition<T> and(Condition<T>... conditions) {
        return entity -> Arrays.stream(conditions)
                .forEach(checker -> checker.check(entity));
    }

    /**
     * Возвращает составную проверку, которая проходит, если хотя бы одна из переданных проверок выполнена.
     *
     * @param conditions массив проверок для объединения
     * @param <T>      тип проверяемой сущности
     * @return составная проверка, реализующая логическую операцию OR
     */
    @SafeVarargs
    public static <T> Condition<T> or(Condition<T>... conditions) {
        return t -> {
            boolean atLeastOnePassed = Arrays.stream(conditions)
                    .anyMatch(checker -> {
                        try {
                            checker.check(t);
                            return true;
                        } catch (AssertionError e) {
                            return false;
                        }
                    });
            Assertions.assertThat(atLeastOnePassed)
                    .as("Ни одно из условий OR не выполнено")
                    .isTrue();
        };
    }

    /**
     * Возвращает составную проверку, которая инвертирует результаты переданных проверок.
     * То есть проверка проходит, если ни одна из переданных проверок не выполнена.
     *
     * @param conditions массив проверок для инвертирования
     * @param <T>      тип проверяемой сущности
     * @return составная проверка, реализующая логическую операцию NOT
     */
    @SafeVarargs
    public static <T> Condition<T> not(Condition<T>... conditions) {
        return t -> {
            for (Condition<T> condition : conditions) {
                try {
                    condition.check(t);
                    Assertions.fail("Условие должно было НЕ выполняться, но выполнилось: " + condition);
                } catch (AssertionError e) {
                    // Ожидаемое поведение: проверка не прошла
                }
            }
        };
    }

    /**
     * Возвращает составную проверку, которая проходит, если выполнено хотя бы n из переданных проверок.
     *
     * @param n        минимальное количество проверок, которые должны выполниться
     * @param conditions массив проверок для объединения
     * @param <T>      тип проверяемой сущности
     * @return составная проверка, которая проходит, если выполнено хотя бы n проверок
     */
    @SafeVarargs
    public static <T> Condition<T> nOf(int n, Condition<T>... conditions) {
        return t -> {
            long successCount = Arrays.stream(conditions)
                    .filter(checker -> {
                        try {
                            checker.check(t);
                            return true;
                        } catch (AssertionError e) {
                            return false;
                        }
                    })
                    .count();
            Assertions.assertThat(successCount)
                    .as("Ожидалось, что хотя бы %d проверок будут выполнены, но выполнено %d", n, successCount)
                    .isGreaterThanOrEqualTo(n);
        };
    }
}
