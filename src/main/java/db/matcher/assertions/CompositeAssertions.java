package db.matcher.assertions;

import db.matcher.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.Arrays;

/**
 * Утилитный класс для композиционных (логических) операций над проверками: AND, OR, NOT, nOf.
 */
@UtilityClass
public class CompositeAssertions {

    /**
     * Возвращает составную проверку, которая проходит, если пройдены все переданные проверки.
     *
     * @param conditions набор проверок
     * @param <T>        тип сущности
     * @return составная проверка
     */
    @SafeVarargs
    public static <T> Condition<T> and(Condition<T>... conditions) {
        return entity -> Arrays.stream(conditions)
                .forEach(checker -> checker.check(entity));
    }

    /**
     * Возвращает составную проверку, которая проходит, если хотя бы одна из переданных проверок пройдена.
     *
     * @param conditions набор проверок
     * @param <T>        тип сущности
     * @return составная проверка
     */
    @SafeVarargs
    public static <T> Condition<T> or(Condition<T>... conditions) {
        return entity -> {
            boolean atLeastOnePassed = Arrays.stream(conditions)
                    .anyMatch(checker -> passes(checker, entity));
            Assertions.assertThat(atLeastOnePassed)
                    .as("Ни одно из OR-условий не выполнено")
                    .isTrue();
        };
    }

    /**
     * Возвращает составную проверку, которая проходит, если ни одна из переданных проверок не пройдена.
     *
     * @param conditions набор проверок
     * @param <T>        тип сущности
     * @return составная проверка
     */
    @SafeVarargs
    public static <T> Condition<T> not(Condition<T>... conditions) {
        return entity -> {
            boolean nonePassed = Arrays.stream(conditions)
                    .noneMatch(checker -> passes(checker, entity));
            Assertions.assertThat(nonePassed)
                    .as("Ожидалось, что ни одна проверка не пройдет, но хотя бы одна выполнилась")
                    .isTrue();
        };
    }

    /**
     * Возвращает составную проверку, которая проходит, если выполнено хотя бы n из переданных проверок.
     *
     * @param n          минимальное число проверок, которые должны пройти
     * @param conditions набор проверок
     * @param <T>        тип сущности
     * @return составная проверка
     */
    @SafeVarargs
    public static <T> Condition<T> nOf(int n, Condition<T>... conditions) {
        return entity -> {
            long successCount = Arrays.stream(conditions)
                    .filter(checker -> passes(checker, entity))
                    .count();
            Assertions.assertThat(successCount)
                    .as("Ожидалось, что хотя бы %d условий будут выполнены, но выполнено %d", n, successCount)
                    .isGreaterThanOrEqualTo(n);
        };
    }

    /**
     * Вспомогательный метод для проверки, проходит ли переданная проверка для заданной сущности.
     *
     * @param condition проверка
     * @param entity    сущность
     * @param <T>       тип сущности
     * @return {@code true}, если проверка прошла, иначе {@code false}
     */
    private static <T> boolean passes(Condition<T> condition, T entity) {
        try {
            condition.check(entity);
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }
}
