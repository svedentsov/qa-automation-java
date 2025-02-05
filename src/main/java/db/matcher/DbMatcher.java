package db.matcher;

import db.matcher.assertions.CollectionAssertions.CollectionCondition;
import db.matcher.assertions.CompositeAssertions;
import db.matcher.assertions.NumberAssertions.NumberCondition;
import db.matcher.assertions.PropertyAssertions.PropertyCondition;
import db.matcher.assertions.StringAssertions.StringCondition;
import db.matcher.assertions.TimeAssertions.TimestampCondition;
import db.matcher.condition.Condition;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.function.Consumer;
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
    public static <T, R> Condition<T> value(Function<T, R> getter, Condition<R> cond) {
        return valueInternal(getter, cond::check);
    }

    /**
     * Создаёт условие для проверки коллекционного свойства сущности.
     *
     * @param getter функция для получения коллекции из сущности
     * @param cc     условие для проверки коллекции
     * @param <T>    тип сущности
     * @param <E>    тип элементов коллекции
     * @return условие для проверки сущности
     */
    public static <T, E> Condition<T> value(Function<T, Collection<E>> getter, CollectionCondition<E> cc) {
        return valueInternal(getter, cc::check);
    }

    /**
     * Создаёт условие для проверки строкового свойства сущности.
     *
     * @param getter функция для получения строки из сущности
     * @param sc     условие для проверки строки
     * @param <T>    тип сущности
     * @return условие для проверки сущности
     */
    public static <T> Condition<T> value(Function<T, String> getter, StringCondition sc) {
        return valueInternal(getter, sc::check);
    }

    /**
     * Создаёт условие для проверки числового свойства сущности.
     *
     * @param getter функция для получения числа из сущности
     * @param nc     условие для проверки числа
     * @param <T>    тип сущности
     * @param <N>    тип числа, который наследуется от Number и реализует Comparable
     * @return условие для проверки сущности
     */
    public static <T, N extends Number & Comparable<N>> Condition<T> value(Function<T, N> getter, NumberCondition<N> nc) {
        return valueInternal(getter, nc::check);
    }

    /**
     * Создаёт условие для проверки свойства типа LocalDateTime сущности.
     *
     * @param getter функция для получения LocalDateTime из сущности
     * @param tc     условие для проверки даты и времени
     * @param <T>    тип сущности
     * @return условие для проверки сущности
     */
    public static <T> Condition<T> value(Function<T, LocalDateTime> getter, TimestampCondition tc) {
        return valueInternal(getter, tc::check);
    }

    /**
     * Создаёт условие для проверки свойства сущности с помощью пользовательского условия.
     *
     * @param getter функция для получения свойства из сущности
     * @param pc     пользовательское условие для проверки свойства
     * @param <T>    тип сущности
     * @param <V>    тип свойства
     * @return условие для проверки сущности
     */
    public static <T, V> Condition<T> value(Function<T, V> getter, PropertyCondition<V> pc) {
        return valueInternal(getter, pc::check);
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

    /**
     * Внутренний универсальный метод для создания условия на основе функции получения свойства и потребителя,
     * выполняющего проверку.
     *
     * @param getter  функция для получения свойства из сущности
     * @param checker потребитель, выполняющий проверку свойства
     * @param <T>     тип сущности
     * @param <V>     тип свойства
     * @return условие для проверки сущности
     */
    private static <T, V> Condition<T> valueInternal(Function<T, V> getter, Consumer<V> checker) {
        return entity -> checker.accept(getter.apply(entity));
    }
}
