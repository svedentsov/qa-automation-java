package db.matcher;

import db.matcher.assertions.CompositeAssertions;
import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.function.Function;

/**
 * Утилитный класс для создания условий (Condition) для проверки свойств сущности.
 * Предоставляет перегруженные методы для различных типов свойств (коллекции, строки, числа, даты и др.).
 */
@UtilityClass
public class DbMatcher {

    /**
     * Создаёт условие для проверки свойства сущности с помощью другого условия.
     *
     * @param getter функция для получения свойства из сущности
     * @param cond   условие для проверки свойства
     * @param <T>    тип сущности
     * @param <R>    тип свойства
     * @return условие для проверки сущности
     */
    public static <T, R> Condition<T> value(Function<? super T, ? extends R> getter, Condition<? super R> cond) {
        Objects.requireNonNull(getter, "getter не может быть null");
        Objects.requireNonNull(cond, "condition не может быть null");
        return entity -> cond.check(getter.apply(entity));
    }

    /**
     * Логическая операция И для набора условий.
     *
     * @param conditions набор условий
     * @param <T>        тип сущности
     * @return составное условие, которое считается выполненным, если выполнены все условия
     */
    @SafeVarargs
    public static <T> Condition<T> and(Condition<T>... conditions) {
        return CompositeAssertions.and(conditions);
    }

    /**
     * Логическая операция ИЛИ для набора условий.
     *
     * @param conditions набор условий
     * @param <T>        тип сущности
     * @return составное условие, которое считается выполненным, если выполнено хотя бы одно условие
     */
    @SafeVarargs
    public static <T> Condition<T> or(Condition<T>... conditions) {
        return CompositeAssertions.or(conditions);
    }

    /**
     * Логическая операция НЕ для набора условий.
     *
     * @param conditions набор условий
     * @param <T>        тип сущности
     * @return составное условие, которое считается выполненным, если ни одно из условий не выполнено
     */
    @SafeVarargs
    public static <T> Condition<T> not(Condition<T>... conditions) {
        return CompositeAssertions.not(conditions);
    }

    /**
     * Возвращает составное условие, которое считается выполненным, если выполнено хотя бы n из переданных условий.
     *
     * @param n          минимальное число условий, которые должны выполниться
     * @param conditions набор условий
     * @param <T>        тип сущности
     * @return составное условие для проверки хотя бы n условий
     */
    @SafeVarargs
    public static <T> Condition<T> nOf(int n, Condition<T>... conditions) {
        return CompositeAssertions.nOf(n, conditions);
    }
}
