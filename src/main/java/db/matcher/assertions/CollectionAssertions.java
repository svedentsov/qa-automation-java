package db.matcher.assertions;

import db.matcher.condition.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.Collection;
import java.util.function.Function;

/**
 * Утилитный класс с условиями для коллекций (поля типа List/Set/массив внутри сущности),
 * в том числе проверки длины, проверки каждого элемента и т.д.
 */
@UtilityClass
public class CollectionAssertions {

    /**
     * Проверяет, что все элементы коллекции внутри сущности соответствуют заданному условию (Condition<E>).
     *
     * @param getter           функция для получения коллекции из сущности
     * @param elementCondition условие для каждого элемента
     * @param <T>              тип сущности
     * @param <E>              тип элементов коллекции
     */
    public static <T, E> Condition<T> allCollectionElementsMatch(Function<T, Collection<E>> getter, Condition<E> elementCondition) {
        return entity -> {
            Collection<E> collection = getter.apply(entity);
            Assertions.assertThat(collection)
                    .as("Коллекция не должна быть null или пустой")
                    .isNotNull()
                    .isNotEmpty();
            for (E element : collection) {
                elementCondition.check(element);
            }
        };
    }

    /**
     * Проверяет, что длина свойства (строка, коллекция или массив) равна заданному значению.
     *
     * @param getter         функция, возвращающая строку / коллекцию / массив
     * @param expectedLength ожидаемая длина
     * @param <T>            тип сущности
     */
    public static <T> Condition<T> propertyLengthEquals(Function<T, ?> getter, int expectedLength) {
        return entity -> {
            Object value = getter.apply(entity);
            int actualLength = getLength(value);
            Assertions.assertThat(actualLength)
                    .as("Длина значения должна быть равна %d", expectedLength)
                    .isEqualTo(expectedLength);
        };
    }

    /**
     * Проверяет, что длина свойства больше заданного значения.
     *
     * @param getter    функция, возвращающая строку / коллекцию / массив
     * @param minLength минимально допустимая длина
     * @param <T>       тип сущности
     */
    public static <T> Condition<T> propertyLengthGreaterThan(Function<T, ?> getter, int minLength) {
        return entity -> {
            Object value = getter.apply(entity);
            int actualLength = getLength(value);
            Assertions.assertThat(actualLength)
                    .as("Длина значения должна быть больше %d", minLength)
                    .isGreaterThan(minLength);
        };
    }

    /**
     * Проверяет, что длина свойства меньше заданного значения.
     *
     * @param getter    функция, возвращающая строку / коллекцию / массив
     * @param maxLength максимально допустимая длина
     * @param <T>       тип сущности
     */
    public static <T> Condition<T> propertyLengthLessThan(Function<T, ?> getter, int maxLength) {
        return entity -> {
            Object value = getter.apply(entity);
            int actualLength = getLength(value);
            Assertions.assertThat(actualLength)
                    .as("Длина значения должна быть меньше %d", maxLength)
                    .isLessThan(maxLength);
        };
    }

    private static int getLength(Object value) {
        Assertions.assertThat(value)
                .as("Значение не должно быть null")
                .isNotNull();

        if (value instanceof String) {
            return ((String) value).length();
        } else if (value instanceof Collection) {
            return ((Collection<?>) value).size();
        } else if (value.getClass().isArray()) {
            return ((Object[]) value).length;
        }
        throw new IllegalArgumentException("Значение не является строкой, коллекцией или массивом");
    }
}
