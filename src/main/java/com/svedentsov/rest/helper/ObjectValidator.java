package com.svedentsov.rest.helper;

import com.svedentsov.matcher.Condition;

import java.util.Arrays;
import java.util.Objects;

/**
 * Аналог DbValidator, но для обычных Java-объектов (POJO).
 * Теперь вся логика «forObject(...).shouldHave(...)» хранится в одном классе.
 */
public class ObjectValidator<T> {

    private final T target;

    private ObjectValidator(T target) {
        this.target = target;
    }

    /**
     * Точка входа: создаёт новый {@code ObjectValidator} для конкретного объекта.
     *
     * @param obj объект, который хотим валидировать
     * @param <T> тип этого объекта
     * @return новый экземпляр {@code ObjectValidator<T>}
     */
    public static <T> ObjectValidator<T> forObject(T obj) {
        Objects.requireNonNull(obj, "Объект для валидации не может быть null");
        return new ObjectValidator<>(obj);
    }

    /**
     * Запускает все переданные условия {@code Condition<? super T>} на поле {@code target}.
     * Если хотя бы одно условие выбросит AssertionError, валидация упадёт.
     * <p>
     * Пример использования:
     * <pre>
     *     ObjectValidator.forObject(pet).shouldHave(
     *         ObjectMatcher.value(Pet::getName, hasNonBlankContent()),
     *         ObjectMatcher.value(Pet::getAge, numberEqualTo(5))
     *     );
     * </pre>
     * * @param conditions varargs любых Condition<? super T>
     */
    @SafeVarargs
    public final void shouldHave(Condition<? super T>... conditions) {
        Arrays.stream(conditions).forEach(cond -> cond.check(target));
    }
}
