package core.matcher.assertions;

import core.matcher.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.Collection;
import java.util.List;

/**
 * Утилитный класс для проверок свойств, представляющих коллекции, строки или массивы.
 * Здесь можно проверить длину, содержимое, наличие элементов и другие характеристики.
 */
@UtilityClass
public class CollectionAssertions {

    @FunctionalInterface
    public interface CollectionCondition<T> extends Condition<T> {
    }

    /**
     * Проверяет, что длина значения (строка, коллекция или массив) равна заданному значению.
     *
     * @param expectedLength ожидаемая длина
     * @param <T>            тип проверяемого значения (String, Collection или массив)
     * @return условие, проверяющее равенство длины
     */
    public static <T> CollectionCondition<T> lengthEquals(int expectedLength) {
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
    public static <T> CollectionCondition<T> lengthGreaterThan(int minLength) {
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
    public static <T> CollectionCondition<T> lengthLessThan(int maxLength) {
        return value -> {
            int actualLength = getLength(value);
            Assertions.assertThat(actualLength)
                    .as("Длина значения должна быть меньше %d", maxLength)
                    .isLessThan(maxLength);
        };
    }

    /**
     * Проверяет, что коллекция или массив пусты.
     *
     * @param <T> тип проверяемого значения (Collection или массив)
     * @return условие, проверяющее, что коллекция или массив пусты
     */
    public static <T> CollectionCondition<T> empty() {
        return value -> {
            boolean isEmpty = getLength(value) == 0;
            Assertions.assertThat(isEmpty)
                    .as("Коллекция или массив должны быть пустыми")
                    .isTrue();
        };
    }

    /**
     * Проверяет, что коллекция или строка содержат указанный элемент.
     *
     * @param element элемент для проверки наличия
     * @param <T>     тип проверяемого значения (String, Collection или массив)
     * @return условие, проверяющее, что коллекция или строка содержат элемент
     */
    public static <T> CollectionCondition<T> contains(Object element) {
        return value -> {
            boolean contains = getValueAsCollection(value).contains(element);
            Assertions.assertThat(contains)
                    .as("Коллекция или строка должны содержать элемент: %s", element)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что коллекция содержит все указанные элементы.
     *
     * @param elements элементы для проверки
     * @param <T>      тип проверяемого значения (Collection или массив)
     * @return условие, проверяющее, что коллекция содержит все элементы
     */
    @SafeVarargs
    public static <T> CollectionCondition<T> containsAll(Object... elements) {
        return value -> {
            Collection<?> collection = getValueAsCollection(value);
            boolean containsAll = collection.containsAll(List.of(elements));
            Assertions.assertThat(containsAll)
                    .as("Коллекция должна содержать все элементы: %s", elements)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что коллекция не содержит дублирующихся элементов.
     *
     * @param <T> тип проверяемого значения (Collection или массив)
     * @return условие, проверяющее отсутствие дублирующихся элементов
     */
    public static <T> CollectionCondition<T> noDuplicates() {
        return value -> {
            Collection<?> collection = getValueAsCollection(value);
            long uniqueCount = collection.stream().distinct().count();
            long totalCount = collection.size();
            Assertions.assertThat(uniqueCount)
                    .as("Коллекция не должна содержать дублирующихся элементов")
                    .isEqualTo(totalCount);
        };
    }

    /**
     * Проверяет, что длина значения находится между min и max (включительно).
     *
     * @param min минимальная граница длины
     * @param max максимальная граница длины
     * @param <T> тип проверяемого значения (String, Collection или массив)
     * @return условие, проверяющее, что длина находится в заданном диапазоне
     */
    public static <T> CollectionCondition<T> lengthBetween(int min, int max) {
        return value -> {
            int actualLength = getLength(value);
            Assertions.assertThat(actualLength)
                    .as("Длина значения должна быть между %d и %d", min, max)
                    .isBetween(min, max);
        };
    }

    /**
     * Проверяет, что значение начинается с указанного префикса или первого элемента.
     * Для строк используется метод startsWith, для коллекций – сравнение первого элемента.
     *
     * @param prefix префикс или первый элемент
     * @param <T>    тип проверяемого значения (String, Collection или массив)
     * @return условие, проверяющее начало значения
     */
    public static <T> CollectionCondition<T> startsWith(Object prefix) {
        return value -> {
            if (value instanceof String) {
                Assertions.assertThat((String) value)
                        .as("Строка должна начинаться с: %s", prefix)
                        .startsWith(prefix.toString());
            } else {
                Collection<?> collection = getValueAsCollection(value);
                Assertions.assertThat(collection)
                        .as("Коллекция или массив должны начинаться с: %s", prefix)
                        .first().isEqualTo(prefix);
            }
        };
    }

    /**
     * Проверяет, что значение заканчивается указанным суффиксом или последним элементом.
     * Для строк используется метод endsWith, для коллекций – сравнение последнего элемента.
     *
     * @param suffix суффикс или последний элемент
     * @param <T>    тип проверяемого значения (String, Collection или массив)
     * @return условие, проверяющее окончание значения
     */
    public static <T> CollectionCondition<T> endsWith(Object suffix) {
        return value -> {
            if (value instanceof String) {
                Assertions.assertThat((String) value)
                        .as("Строка должна заканчиваться на: %s", suffix)
                        .endsWith(suffix.toString());
            } else {
                Collection<?> collection = getValueAsCollection(value);
                Assertions.assertThat(collection)
                        .as("Коллекция или массив должны заканчиваться на: %s", suffix)
                        .last().isEqualTo(suffix);
            }
        };
    }

    /**
     * Проверяет, что все элементы коллекции или массива являются экземплярами указанного класса.
     *
     * @param clazz класс, которому должны соответствовать все элементы
     * @param <T>   тип проверяемого значения (Collection или массив)
     * @return условие, проверяющее тип всех элементов
     */
    public static <T> CollectionCondition<T> allElementsInstanceOf(Class<?> clazz) {
        return value -> {
            Collection<?> collection = getValueAsCollection(value);
            Assertions.assertThat(collection)
                    .as("Все элементы коллекции или массива должны быть экземплярами класса %s", clazz.getName())
                    .allSatisfy(element -> Assertions.assertThat(element)
                            .isInstanceOf(clazz));
        };
    }

    /**
     * Проверяет, что длина значения больше или равна заданному значению.
     *
     * @param minSize минимальное допустимое значение длины
     * @param <T>     тип проверяемого значения (String, Collection или массив)
     * @return условие, проверяющее, что длина больше или равна minSize
     */
    public static <T> CollectionCondition<T> lengthGreaterThanOrEqual(int minSize) {
        return value -> {
            int actualLength = getLength(value);
            Assertions.assertThat(actualLength)
                    .as("Длина значения должна быть больше или равна %d", minSize)
                    .isGreaterThanOrEqualTo(minSize);
        };
    }

    /**
     * Проверяет, что длина значения меньше или равна заданному значению.
     *
     * @param maxLength максимальное допустимое значение длины
     * @param <T>       тип проверяемого значения (String, Collection или массив)
     * @return условие, проверяющее, что длина меньше или равна maxLength
     */
    public static <T> CollectionCondition<T> lengthLessThanOrEqual(int maxLength) {
        return value -> {
            int actualLength = getLength(value);
            Assertions.assertThat(actualLength)
                    .as("Длина значения должна быть меньше или равна %d", maxLength)
                    .isLessThanOrEqualTo(maxLength);
        };
    }

    /**
     * Проверяет, что длина данного значения совпадает с длиной другого значения.
     *
     * @param other другое значение (String, Collection или массив) для сравнения длины
     * @param <T>   тип проверяемого значения (String, Collection или массив)
     * @return условие, проверяющее равенство длин двух значений
     */
    public static <T> CollectionCondition<T> hasSameSizeAs(Object other) {
        return value -> {
            int thisLength = getLength(value);
            int otherLength = getLength(other);
            Assertions.assertThat(thisLength)
                    .as("Длина значения должна быть равна длине другого значения")
                    .isEqualTo(otherLength);
        };
    }

    /**
     * Проверяет, что элемент встречается в коллекции или массиве как минимум count раз.
     *
     * @param count   минимальное количество вхождений элемента
     * @param element элемент для проверки
     * @param <T>     тип проверяемого значения (Collection или массив)
     * @return условие, проверяющее, что элемент встречается не менее count раз
     */
    public static <T> CollectionCondition<T> containsAtLeast(int count, Object element) {
        return value -> {
            Collection<?> collection = getValueAsCollection(value);
            long occurrence = collection.stream().filter(e -> e.equals(element)).count();
            Assertions.assertThat(occurrence)
                    .as("Элемент %s должен встречаться хотя бы %d раз, найдено: %d", element, count, occurrence)
                    .isGreaterThanOrEqualTo((long) count);
        };
    }

    /**
     * Проверяет, что элемент встречается в коллекции или массиве ровно expectedCount раз.
     *
     * @param element       элемент для проверки
     * @param expectedCount ожидаемое количество вхождений элемента
     * @param <T>           тип проверяемого значения (Collection или массив)
     * @return условие, проверяющее, что элемент встречается ровно expectedCount раз
     */
    public static <T> CollectionCondition<T> occurrenceCountEquals(Object element, int expectedCount) {
        return value -> {
            Collection<?> collection = getValueAsCollection(value);
            long occurrence = collection.stream().filter(e -> e.equals(element)).count();
            Assertions.assertThat(occurrence)
                    .as("Элемент %s должен встречаться ровно %d раз, найдено: %d", element, expectedCount, occurrence)
                    .isEqualTo(expectedCount);
        };
    }

    /**
     * Возвращает длину значения в зависимости от его типа (строка, коллекция, массив).
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

    /**
     * Преобразует значение в коллекцию для универсальной работы с коллекциями и массивами.
     *
     * @param value значение для преобразования в коллекцию
     * @return коллекция, содержащая элементы
     * @throws IllegalArgumentException если значение не является строкой, коллекцией или массивом
     */
    private static Collection<?> getValueAsCollection(Object value) {
        if (value instanceof String) {
            return List.of(((String) value).split(""));
        } else if (value instanceof Collection) {
            return (Collection<?>) value;
        } else if (value.getClass().isArray()) {
            return List.of((Object[]) value);
        }
        throw new IllegalArgumentException("Значение не является строкой, коллекцией или массивом");
    }
}
