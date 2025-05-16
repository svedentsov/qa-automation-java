package core.matcher.assertions;

import core.matcher.Condition;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Утилитный класс для проверок коллекции сущностей.
 */
@UtilityClass
public class ListAssertions {

    /**
     * Маркерный функциональный интерфейс для проверок списка сущностей.
     */
    @FunctionalInterface
    public interface ListCondition<T> extends Condition<List<T>> {
    }

    /**
     * Проверяет, что список сущностей не пуст.
     *
     * @param <T> тип сущности
     * @return условие: список не должен быть пустым
     * @throws NullPointerException если список null
     */
    public static <T> ListCondition<T> exists() {
        return list -> {
            Objects.requireNonNull(list, "Список сущностей не должен быть null");
            Assertions.assertThat(list)
                    .as("Ожидалось, что список сущностей не пуст")
                    .isNotEmpty();
        };
    }

    /**
     * Проверяет, что количество сущностей равно заданному.
     *
     * @param expected ожидаемое количество
     * @param <T>      тип сущности
     * @return условие: размер списка == expected
     * @throws NullPointerException     если список null
     * @throws IllegalArgumentException если expected < 0
     */
    public static <T> ListCondition<T> countEqual(int expected) {
        if (expected < 0) {
            throw new IllegalArgumentException("Ожидаемое количество не может быть отрицательным");
        }
        return list -> {
            Objects.requireNonNull(list, "Список сущностей не должен быть null");
            Assertions.assertThat(list)
                    .as("Ожидалось, что количество сущностей равно %d, но было %d", expected, list.size())
                    .hasSize(expected);
        };
    }

    /**
     * Проверяет, что количество сущностей больше заданного.
     *
     * @param min минимальное количество +1
     * @param <T> тип сущности
     * @return условие: размер списка > min
     * @throws NullPointerException     если список null
     * @throws IllegalArgumentException если min < 0
     */
    public static <T> ListCondition<T> countGreater(int min) {
        if (min < 0) {
            throw new IllegalArgumentException("Минимальное количество не может быть отрицательным");
        }
        return list -> {
            Objects.requireNonNull(list, "Список сущностей не должен быть null");
            Assertions.assertThat(list.size())
                    .as("Ожидалось, что количество сущностей больше %d, но было %d", min, list.size())
                    .isGreaterThan(min);
        };
    }

    /**
     * Проверяет, что количество сущностей не меньше заданного.
     *
     * @param minCount минимальное количество
     * @param <T>      тип сущности
     * @return условие: размер списка >= minCount
     * @throws NullPointerException     если список null
     * @throws IllegalArgumentException если minCount < 0
     */
    public static <T> ListCondition<T> hasMinimumCount(int minCount) {
        if (minCount < 0) {
            throw new IllegalArgumentException("Минимальное количество не может быть отрицательным");
        }
        return list -> {
            Objects.requireNonNull(list, "Список сущностей не должен быть null");
            Assertions.assertThat(list.size())
                    .as("Ожидалось, что количество сущностей не меньше %d, но было %d", minCount, list.size())
                    .isGreaterThanOrEqualTo(minCount);
        };
    }

    /**
     * Проверяет, что количество сущностей не больше заданного.
     *
     * @param maxCount максимальное количество
     * @param <T>      тип сущности
     * @return условие: размер списка <= maxCount
     * @throws NullPointerException     если список null
     * @throws IllegalArgumentException если maxCount < 0
     */
    public static <T> ListCondition<T> hasMaximumCount(int maxCount) {
        if (maxCount < 0) {
            throw new IllegalArgumentException("Максимальное количество не может быть отрицательным");
        }
        return list -> {
            Objects.requireNonNull(list, "Список сущностей не должен быть null");
            Assertions.assertThat(list.size())
                    .as("Ожидалось, что количество сущностей не больше %d, но было %d", maxCount, list.size())
                    .isLessThanOrEqualTo(maxCount);
        };
    }

    /**
     * Проверяет, что размер списка находится между min и max (включительно).
     *
     * @param min минимум
     * @param max максимум
     * @param <T> тип сущности
     * @return условие: min <= размер списка <= max
     * @throws IllegalArgumentException если min > max или min<0
     */
    public static <T> ListCondition<T> hasSizeBetween(int min, int max) {
        if (min < 0 || max < min) {
            throw new IllegalArgumentException("Неверные границы: min=" + min + ", max=" + max);
        }
        return list -> {
            Objects.requireNonNull(list, "Список сущностей не должен быть null");
            Assertions.assertThat(list.size())
                    .as("Ожидалось, что размер списка между %d и %d, но было %d", min, max, list.size())
                    .isBetween(min, max);
        };
    }

    /**
     * Проверяет, что хотя бы одна сущность удовлетворяет условию.
     *
     * @param condition условие для одной сущности
     * @param <T>       тип сущности
     * @return условие для списка: anyMatch
     * @throws NullPointerException если список или condition null
     */
    public static <T> ListCondition<T> anyEntityMatches(@NonNull Condition<T> condition) {
        return list -> {
            Objects.requireNonNull(list, "Список сущностей не должен быть null");
            boolean found = list.stream().anyMatch(item -> {
                try {
                    condition.check(item);
                    return true;
                } catch (AssertionError ignored) {
                    return false;
                }
            });
            Assertions.assertThat(found)
                    .as("Ожидалось, что хотя бы одна сущность удовлетворяет условию")
                    .isTrue();
        };
    }

    /**
     * Проверяет, что ни одна сущность не удовлетворяет условию.
     *
     * @param condition условие для одной сущности
     * @param <T>       тип сущности
     * @return условие для списка: noneMatch
     * @throws NullPointerException если список или condition null
     */
    public static <T> ListCondition<T> noMatches(@NonNull Condition<T> condition) {
        return list -> {
            Objects.requireNonNull(list, "Список сущностей не должен быть null");
            boolean any = list.stream().anyMatch(item -> {
                try {
                    condition.check(item);
                    return true;
                } catch (AssertionError ignored) {
                    return false;
                }
            });
            Assertions.assertThat(any)
                    .as("Ожидалось, что ни одна сущность не удовлетворяет условию")
                    .isFalse();
        };
    }

    /**
     * Проверяет, что все сущности уникальны (без повторов).
     *
     * @param <T> тип сущности
     * @return условие: distinct count == size
     * @throws NullPointerException если список null
     */
    public static <T> ListCondition<T> entitiesAreUnique() {
        return list -> {
            Objects.requireNonNull(list, "Список сущностей не должен быть null");
            long distinct = list.stream().distinct().count();
            Assertions.assertThat(distinct)
                    .as("Ожидалось, что все сущности будут уникальными")
                    .isEqualTo(list.size());
        };
    }

    /**
     * Проверяет, что список отсортирован согласно компаратору.
     *
     * @param comparator компаратор для сравнения соседних элементов
     * @param <T>        тип сущности
     * @return условие: list[i] <= list[i+1] для всех i
     * @throws NullPointerException если список или comparator null
     */
    public static <T> ListCondition<T> isSorted(@NonNull Comparator<T> comparator) {
        return list -> {
            Objects.requireNonNull(list, "Список сущностей не должен быть null");
            for (int i = 0; i + 1 < list.size(); i++) {
                T curr = list.get(i);
                T next = list.get(i + 1);
                Assertions.assertThat(comparator.compare(curr, next))
                        .as("Ожидалось, что элемент %s не больше %s", curr, next)
                        .isLessThanOrEqualTo(0);
            }
        };
    }

    /**
     * Проверяет, что значения свойства, полученные через getter, равны expected для каждой сущности.
     *
     * @param getter        функция получения свойства
     * @param expectedValue ожидаемое значение
     * @param <T>           тип сущности
     * @return условие для списка: все элементы имеют property == expectedValue
     * @throws NullPointerException если список или getter null
     */
    public static <T> ListCondition<T> valuesEqual(@NonNull Function<T, ?> getter, Object expectedValue) {
        return list -> {
            Objects.requireNonNull(list, "Список сущностей не должен быть null");
            list.forEach(item -> {
                Object actual = getter.apply(item);
                Assertions.assertThat(actual)
                        .as("Ожидалось, что значение свойства равно %s, но было %s", expectedValue, actual)
                        .isEqualTo(expectedValue);
            });
        };
    }

    /**
     * Проверяет, что свойства, полученные через getter, уникальны среди всех сущностей.
     *
     * @param getter функция получения свойства
     * @param <T>    тип сущности
     * @return условие: distinct(property) == size
     * @throws NullPointerException если список или getter null
     */
    public static <T> ListCondition<T> entitiesPropertyAreDistinct(@NonNull Function<T, ?> getter) {
        return list -> {
            Objects.requireNonNull(list, "Список сущностей не должен быть null");
            long distinct = list.stream()
                    .map(getter)
                    .distinct()
                    .count();
            Assertions.assertThat(distinct)
                    .as("Ожидалось, что значения свойства будут уникальными")
                    .isEqualTo(list.size());
        };
    }

    /**
     * Проверяет, что список не содержит null-значений.
     *
     * @param <T> тип сущности
     * @return условие: все элементы != null
     * @throws NullPointerException если список null
     */
    public static <T> ListCondition<T> entitiesContainNoNulls() {
        return list -> {
            Objects.requireNonNull(list, "Список сущностей не должен быть null");
            Assertions.assertThat(list)
                    .as("Ожидалось, что список не содержит null")
                    .noneMatch(Objects::isNull);
        };
    }

    /**
     * Проверяет, что порядок свойств, полученных через getter, точно соответствует expectedOrder.
     *
     * @param getter        функция получения Comparable-свойства
     * @param expectedOrder ожидаемый порядок значений
     * @param <T>           тип сущности
     * @return условие: actualOrder == expectedOrder
     * @throws NullPointerException если список или getter или expectedOrder null
     */
    public static <T> ListCondition<T> entitiesMatchOrder(
            @NonNull Function<T, ? extends Comparable<?>> getter,
            @NonNull List<?> expectedOrder) {
        return list -> {
            Objects.requireNonNull(list, "Список сущностей не должен быть null");
            List<?> actual = list.stream().map(getter).toList();
            Assertions.assertThat(actual)
                    .as("Ожидалось, что порядок значений будет %s, но был %s", expectedOrder, actual)
                    .isEqualTo(expectedOrder);
        };
    }
}
