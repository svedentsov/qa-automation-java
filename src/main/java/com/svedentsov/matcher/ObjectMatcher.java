package com.svedentsov.matcher;

import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.function.Function;

/**
 * Утилитный класс для «объектного» матчингa: позволяет взять любое
 * условие на тип R (Condition<R>) и «протянуть» его на вложённое поле T → R.
 */
@UtilityClass
public class ObjectMatcher {

    /**
     * Спускает проверку cond на свойство getter(obj).
     * Например:
     * ObjectMatcher.value(Pet::getName, equalTo("Rex"))
     * создаст Condition<Pet>, который достаёт сначала имя (String),
     * а затем проверяет его через equalTo("Rex").
     *
     * @param getter функция, которая по объекту T возвращает поле R
     * @param cond   любое условие для R (Condition<R>)
     * @param <T>    тип «верхнего» объекта
     * @param <R>    тип свойства, к которому применяется cond
     * @return Condition<T>, валидирующий поле R внутри T
     */
    public static <T, R> Condition<T> value(
            Function<? super T, ? extends R> getter,
            Condition<? super R> cond) {
        Objects.requireNonNull(getter, "getter не может быть null");
        Objects.requireNonNull(cond, "condition не может быть null");
        return obj -> cond.check(getter.apply(obj));
    }
}
