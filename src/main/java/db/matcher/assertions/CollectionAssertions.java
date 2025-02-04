package db.matcher.assertions;

import db.matcher.condition.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.Collection;

/**
 * Утилитный класс для проверок свойств, представляющих коллекции, строки или массивы.
 * Здесь можно проверить длину (количество элементов, длину строки и т.д.).
 */
@UtilityClass
public class CollectionAssertions {

    /**
     * Функциональный интерфейс для проверки коллекций.
     *
     * @param <E> тип элементов коллекции
     */
    @FunctionalInterface
    public interface CollectionCondition<E> {
        /**
         * Выполняет проверку коллекции.
         *
         * @param collection проверяемая коллекция
         */
        void check(Collection<E> collection);
    }

    /**
     * Проверяет, что длина значения (строка, коллекция или массив) равна заданному значению.
     *
     * @param expectedLength ожидаемая длина
     * @param <T>            тип проверяемого значения (String, Collection или массив)
     * @return условие, проверяющее равенство длины
     */
    public static <T> Condition<T> lengthEquals(int expectedLength) {
        return value -> {
            int actualLength = getLength(value);
            Assertions.assertThat(actualLength)
                    .as("Длина значения должна быть равна %d", expectedLength)
                    .isEqualTo(expectedLength);
        };
    }

    /**
     * Проверяет, что длина значения (строка, коллекция или массив) больше заданного значения.
     *
     * @param minLength минимально допустимая длина (строго больше)
     * @param <T>       тип проверяемого значения (String, Collection или массив)
     * @return условие, проверяющее, что длина больше указанного минимума
     */
    public static <T> Condition<T> lengthGreaterThan(int minLength) {
        return value -> {
            int actualLength = getLength(value);
            Assertions.assertThat(actualLength)
                    .as("Длина значения должна быть больше %d", minLength)
                    .isGreaterThan(minLength);
        };
    }

    /**
     * Проверяет, что длина значения (строка, коллекция или массив) меньше заданного значения.
     *
     * @param maxLength максимально допустимая длина (строго меньше)
     * @param <T>       тип проверяемого значения (String, Collection или массив)
     * @return условие, проверяющее, что длина меньше указанного максимума
     */
    public static <T> Condition<T> lengthLessThan(int maxLength) {
        return value -> {
            int actualLength = getLength(value);
            Assertions.assertThat(actualLength)
                    .as("Длина значения должна быть меньше %d", maxLength)
                    .isLessThan(maxLength);
        };
    }

    /**
     * Возвращает длину значения в зависимости от его типа.
     *
     * @param value значение для получения длины (String, Collection или массив)
     * @return длина значения
     * @throws IllegalArgumentException если значение не является строкой, коллекцией или массивом
     */
    private static int getLength(Object value) {
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
