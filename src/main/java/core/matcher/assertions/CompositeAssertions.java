package core.matcher.assertions;

import core.matcher.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Утилитный класс для композиционных (логических) операций над проверками: AND, OR, NOT, nOf, exactlyNOf, atMostNOf, xor.
 */
@UtilityClass
public class CompositeAssertions {

    /**
     * Возвращает проверку, которая проходит, если **все** переданные условия выполняются.
     *
     * @param conditions набор условий; не может быть null или пустым
     * @param <T>        тип сущности
     * @return составное условие AND
     * @throws NullPointerException     если массив или любой из элементов равен null
     * @throws IllegalArgumentException если передан пустой массив условий
     */
    @SafeVarargs
    public static <T> Condition<T> and(Condition<T>... conditions) {
        validateConditions(conditions, "AND");
        return entity -> Stream.of(conditions).forEach(cond -> cond.check(entity));
    }

    /**
     * Возвращает проверку, которая проходит, если **хотя бы одно** из переданных условий выполняется.
     *
     * @param conditions набор условий; не может быть null или пустым
     * @param <T>        тип сущности
     * @return составное условие OR
     * @throws NullPointerException     если массив или любой из элементов равен null
     * @throws IllegalArgumentException если передан пустой массив условий
     */
    @SafeVarargs
    public static <T> Condition<T> or(Condition<T>... conditions) {
        validateConditions(conditions, "OR");
        return entity -> {
            long passed = countPassed(entity, conditions);
            Assertions.assertThat(passed)
                    .as("Ни одно из OR-условий не выполнено")
                    .isGreaterThan(0);
        };
    }

    /**
     * Возвращает проверку, которая проходит, если **ни одно** из переданных условий не выполняется.
     *
     * @param conditions набор условий; не может быть null или пустым
     * @param <T>        тип сущности
     * @return составное условие NOT (ни одно не выполняется)
     * @throws NullPointerException     если массив или любой из элементов равен null
     * @throws IllegalArgumentException если передан пустой массив условий
     */
    @SafeVarargs
    public static <T> Condition<T> not(Condition<T>... conditions) {
        validateConditions(conditions, "NOT");
        return entity -> {
            long passed = countPassed(entity, conditions);
            Assertions.assertThat(passed)
                    .as("Ожидалось, что ни одно условие не выполнится, но выполнено %d", passed)
                    .isEqualTo(0);
        };
    }

    /**
     * Возвращает проверку, которая проходит, если **единичное** условие не выполняется.
     *
     * @param condition условие; не может быть null
     * @param <T>       тип сущности
     * @return инвертированное условие
     * @throws NullPointerException если условие равно null
     */
    public static <T> Condition<T> not(Condition<T> condition) {
        Objects.requireNonNull(condition, "Условие не может быть null");
        return entity -> {
            boolean passed = passes(condition, entity);
            Assertions.assertThat(passed)
                    .as("Ожидалось, что условие не выполнится, но оно выполнилось")
                    .isFalse();
        };
    }

    /**
     * Возвращает проверку, которая проходит, если **хотя бы n** из переданных условий выполняются.
     *
     * @param n          минимальное число успешных условий (>0)
     * @param conditions набор условий; не может быть null или пустым
     * @param <T>        тип сущности
     * @return составное условие nOf
     * @throws NullPointerException     если массив или любой элемент равен null
     * @throws IllegalArgumentException если массив пуст или n < 1
     */
    @SafeVarargs
    public static <T> Condition<T> nOf(int n, Condition<T>... conditions) {
        validateCountArgs(n, conditions, "nOf", true);
        return entity -> {
            long passed = countPassed(entity, conditions);
            Assertions.assertThat(passed)
                    .as("Ожидалось, что минимум %d условий выполнится, но выполнено %d", n, passed)
                    .isGreaterThanOrEqualTo(n);
        };
    }

    /**
     * Возвращает проверку, которая проходит, если **ровно n** из переданных условий выполняются.
     *
     * @param n          точное число успешных условий (>=0)
     * @param conditions набор условий; не может быть null или пустым
     * @param <T>        тип сущности
     * @return составное условие exactlyNOf
     * @throws NullPointerException     если массив или любой элемент равен null
     * @throws IllegalArgumentException если массив пуст или n < 0
     */
    @SafeVarargs
    public static <T> Condition<T> exactlyNOf(int n, Condition<T>... conditions) {
        validateCountArgs(n, conditions, "exactlyNOf", false);
        return entity -> {
            long passed = countPassed(entity, conditions);
            Assertions.assertThat(passed)
                    .as("Ожидалось, что ровно %d условий выполнится, но выполнено %d", n, passed)
                    .isEqualTo(n);
        };
    }

    /**
     * Возвращает проверку, которая проходит, если **не более n** из переданных условий выполняются.
     *
     * @param n          максимальное число успешных условий (>=0)
     * @param conditions набор условий; не может быть null или пустым
     * @param <T>        тип сущности
     * @return составное условие atMostNOf
     * @throws NullPointerException     если массив или любой элемент равен null
     * @throws IllegalArgumentException если массив пуст или n < 0
     */
    @SafeVarargs
    public static <T> Condition<T> atMostNOf(int n, Condition<T>... conditions) {
        validateCountArgs(n, conditions, "atMostNOf", false);
        return entity -> {
            long passed = countPassed(entity, conditions);
            Assertions.assertThat(passed)
                    .as("Ожидалось, что не более %d условий выполнится, но выполнено %d", n, passed)
                    .isLessThanOrEqualTo(n);
        };
    }

    /**
     * Возвращает проверку, которая проходит, если **ровно одно** из переданных условий выполняется.
     *
     * @param conditions набор условий; не может быть null или пустым
     * @param <T>        тип сущности
     * @return составное условие XOR
     * @throws NullPointerException     если массив или любой элемент равен null
     * @throws IllegalArgumentException если массив пуст
     */
    @SafeVarargs
    public static <T> Condition<T> xor(Condition<T>... conditions) {
        validateConditions(conditions, "XOR");
        return entity -> {
            long passed = countPassed(entity, conditions);
            Assertions.assertThat(passed)
                    .as("Ожидалось, что ровно одно условие выполнится, но выполнено %d", passed)
                    .isEqualTo(1);
        };
    }

    /**
     * Проверяет массив условий на null, пустоту и наличие null-элементов.
     *
     * @param conditions набор условий
     * @param name       имя операции (для сообщений об ошибке)
     * @param <T>        тип сущности
     * @throws NullPointerException     если массив или любой элемент равен null
     * @throws IllegalArgumentException если массив пуст
     */
    private static <T> void validateConditions(Condition<T>[] conditions, String name) {
        Objects.requireNonNull(conditions, name + ": массив условий не может быть null");
        if (conditions.length == 0) {
            throw new IllegalArgumentException(name + " требует хотя бы одного условия");
        }
        Stream.of(conditions).forEach(cond ->
                Objects.requireNonNull(cond, name + ": элемент массива условий не может быть null")
        );
    }

    /**
     * Проверяет корректность аргументов для методов с подсчётом n.
     *
     * @param n             число успешных условий
     * @param conditions    массив условий
     * @param name          имя операции
     * @param requireNAtOne если true, то n >= 1, иначе n >= 0
     * @param <T>           тип сущности
     */
    private static <T> void validateCountArgs(int n, Condition<T>[] conditions, String name, boolean requireNAtOne) {
        validateConditions(conditions, name);
        if (requireNAtOne && n < 1) {
            throw new IllegalArgumentException(name + ": n должно быть >= 1");
        }
        if (!requireNAtOne && n < 0) {
            throw new IllegalArgumentException(name + ": n должно быть >= 0");
        }
    }

    /**
     * Подсчитывает, сколько условий прошло успешно.
     *
     * @param entity     тестируемая сущность
     * @param conditions массив условий
     * @param <T>        тип сущности
     * @return количество успешных проверок
     */
    private static <T> long countPassed(T entity, Condition<T>[] conditions) {
        return Stream.of(conditions).filter(cond -> passes(cond, entity)).count();
    }

    /**
     * Проверяет одно условие, возвращает true, если оно не бросило AssertionError.
     *
     * @param condition условие; не может быть null
     * @param entity    тестируемая сущность
     * @param <T>       тип сущности
     * @return true, если проверка успешна, иначе false
     */
    private static <T> boolean passes(Condition<T> condition, T entity) {
        Objects.requireNonNull(condition, "Условие не может быть null");
        try {
            condition.check(entity);
            return true;
        } catch (AssertionError ignored) {
            return false;
        }
    }
}
