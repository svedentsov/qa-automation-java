package db.matcher.assertions;

import db.matcher.Checker;
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
     * @param checkers набор проверок
     * @param <T>      тип сущности
     * @return составная проверка
     */
    @SafeVarargs
    public static <T> Checker<T> and(Checker<T>... checkers) {
        return entity -> Arrays.stream(checkers)
                .forEach(checker -> checker.check(entity));
    }

    /**
     * Возвращает составную проверку, которая проходит, если хотя бы одна из переданных проверок пройдена.
     *
     * @param checkers набор проверок
     * @param <T>      тип сущности
     * @return составная проверка
     */
    @SafeVarargs
    public static <T> Checker<T> or(Checker<T>... checkers) {
        return entity -> {
            boolean atLeastOnePassed = Arrays.stream(checkers)
                    .anyMatch(checker -> passes(checker, entity));
            Assertions.assertThat(atLeastOnePassed)
                    .as("Ни одно из OR-условий не выполнено")
                    .isTrue();
        };
    }

    /**
     * Возвращает составную проверку, которая проходит, если ни одна из переданных проверок не пройдена.
     *
     * @param checkers набор проверок
     * @param <T>      тип сущности
     * @return составная проверка
     */
    @SafeVarargs
    public static <T> Checker<T> not(Checker<T>... checkers) {
        return entity -> {
            boolean nonePassed = Arrays.stream(checkers)
                    .noneMatch(checker -> passes(checker, entity));
            Assertions.assertThat(nonePassed)
                    .as("Ожидалось, что ни одна проверка не пройдет, но хотя бы одна выполнилась")
                    .isTrue();
        };
    }

    /**
     * Возвращает составную проверку, которая проходит, если выполнено хотя бы n из переданных проверок.
     *
     * @param n        минимальное число проверок, которые должны пройти
     * @param checkers набор проверок
     * @param <T>      тип сущности
     * @return составная проверка
     */
    @SafeVarargs
    public static <T> Checker<T> nOf(int n, Checker<T>... checkers) {
        return entity -> {
            long successCount = Arrays.stream(checkers)
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
     * @param checker проверка
     * @param entity  сущность
     * @param <T>     тип сущности
     * @return {@code true}, если проверка прошла, иначе {@code false}
     */
    private static <T> boolean passes(Checker<T> checker, T entity) {
        try {
            checker.check(entity);
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }
}
