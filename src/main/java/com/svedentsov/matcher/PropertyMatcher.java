package com.svedentsov.matcher;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.function.Function;

/**
 * Утилитный класс для «объектного» матчингa: позволяет взять любое
 * условие на тип R (Condition&lt;R&gt;) и «протянуть» его на вложенное поле T → R.
 * Это обеспечивает возможность проверки свойств объектов с помощью уже существующих условий.
 */
@UtilityClass
public class PropertyMatcher {

    /**
     * Создает {@code Condition<T>}, который применяет заданное условие {@code condition}
     * к значению, извлеченному из объекта типа {@code T} с помощью функции {@code getter}.
     * Например:
     * {@code PropertyMatcher.value(Pet::getName, equalTo("Rex"))}
     * создаст {@code Condition<Pet>}, который сначала извлекает имя питомца (строку),
     * а затем проверяет его с помощью условия {@code equalTo("Rex")}.
     *
     * @param getter    функция, которая по объекту {@code T} возвращает поле {@code R}.
     * @param condition любое условие для типа {@code R} ({@code Condition<R>}).
     * @param <T>       тип "верхнего" объекта, свойство которого проверяется.
     * @param <R>       тип свойства, к которому применяется условие {@code condition}.
     * @return {@code Condition<T>}, который валидирует поле {@code R} внутри {@code T}.
     * @throws NullPointerException если любой из входных аргументов равен null.
     */
    public static <T, R> Condition<T> value(
            @NonNull Function<? super T, ? extends R> getter,
            @NonNull Condition<? super R> condition) {
        return entity -> condition.check(getter.apply(entity));
    }
}
