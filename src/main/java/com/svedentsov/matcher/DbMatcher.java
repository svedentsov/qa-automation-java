package com.svedentsov.matcher;

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
}
