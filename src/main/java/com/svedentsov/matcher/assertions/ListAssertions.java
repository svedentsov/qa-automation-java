package com.svedentsov.matcher.assertions;

import com.svedentsov.matcher.Condition;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Утилитный класс для создания и комбинирования условий проверки коллекций сущностей.
 */
@UtilityClass
public class ListAssertions {

    /**
     * Маркерный функциональный интерфейс для условий проверки списка сущностей.
     *
     * @param <T> тип сущности
     */
    @FunctionalInterface
    public interface ListCondition<T> extends Condition<List<T>> {
    }

    /**
     * Список пуст.
     *
     * @param <T> тип сущности
     * @return условие проверки пустоты списка
     */
    public static <T> ListCondition<T> listIsEmpty() {
        return list -> {
            requireList(list);
            Assertions.assertThat(list)
                    .as("Ожидалось, что список будет пуст")
                    .isEmpty();
        };
    }

    /**
     * Список не пуст.
     *
     * @param <T> тип сущности
     * @return условие проверки непустоты списка
     */
    public static <T> ListCondition<T> listIsNotEmpty() {
        return list -> {
            requireList(list);
            Assertions.assertThat(list)
                    .as("Ожидалось, что список не пуст")
                    .isNotEmpty();
        };
    }

    /**
     * Размер списка равен exact.
     *
     * @param expected ожидаемый размер (>= 0)
     * @param <T>      тип сущности
     * @return условие проверки точного размера
     * @throws IllegalArgumentException если expected < 0
     */
    public static <T> ListCondition<T> listCountEqual(int expected) {
        if (expected < 0) {
            throw new IllegalArgumentException("Ожидаемое количество не может быть отрицательным");
        }
        return list -> {
            requireList(list);
            Assertions.assertThat(list)
                    .as(fmt("Ожидалось, что количество сущностей равно %d, но было %d", expected, list.size()))
                    .hasSize(expected);
        };
    }

    /**
     * Размер списка меньше upperExclusive.
     *
     * @param upperExclusive верхняя граница (исключая, > 0)
     * @param <T>            тип сущности
     * @return условие проверки верхней границы размера
     * @throws IllegalArgumentException если upperExclusive <= 0
     */
    public static <T> ListCondition<T> listCountLessThan(int upperExclusive) {
        if (upperExclusive <= 0) {
            throw new IllegalArgumentException("Верхняя граница должна быть > 0");
        }
        return list -> {
            requireList(list);
            Assertions.assertThat(list.size())
                    .as(fmt("Ожидалось, что количество сущностей меньше %d, но было %d", upperExclusive, list.size()))
                    .isLessThan(upperExclusive);
        };
    }

    /**
     * Размер списка больше lowerExclusive.
     *
     * @param lowerExclusive нижняя граница (исключая, >= 0)
     * @param <T>            тип сущности
     * @return условие проверки нижней границы размера
     * @throws IllegalArgumentException если lowerExclusive < 0
     */
    public static <T> ListCondition<T> listCountGreaterThan(int lowerExclusive) {
        if (lowerExclusive < 0) {
            throw new IllegalArgumentException("Нижняя граница не может быть отрицательной");
        }
        return list -> {
            requireList(list);
            Assertions.assertThat(list.size())
                    .as(fmt("Ожидалось, что количество сущностей больше %d, но было %d", lowerExclusive, list.size()))
                    .isGreaterThan(lowerExclusive);
        };
    }

    /**
     * Размер списка находится между minInclusive и maxInclusive включительно.
     *
     * @param minInclusive минимальный размер (>= 0)
     * @param maxInclusive максимальный размер (>= minInclusive)
     * @param <T>          тип сущности
     * @return условие проверки диапазона размера
     * @throws IllegalArgumentException если minInclusive < 0 или maxInclusive < minInclusive
     */
    public static <T> ListCondition<T> listHasSizeBetween(int minInclusive, int maxInclusive) {
        if (minInclusive < 0 || maxInclusive < minInclusive) {
            throw new IllegalArgumentException(
                    fmt("Неверные границы: min=%d, max=%d", minInclusive, maxInclusive));
        }
        return list -> {
            requireList(list);
            Assertions.assertThat(list.size())
                    .as(fmt("Ожидалось, что размер списка между %d и %d, но было %d",
                            minInclusive, maxInclusive, list.size()))
                    .isBetween(minInclusive, maxInclusive);
        };
    }

    /**
     * Каждый элемент списка удовлетворяет всем переданным условиям.
     *
     * @param conds набор условий (не null, length > 0)
     * @param <T>   тип сущности
     * @return условие проверки каждого элемента
     * @throws IllegalArgumentException если conds.length == 0
     * @throws NullPointerException     если conds или любой элемент conds равен null
     */
    @SafeVarargs
    public static <T> ListCondition<T> listAllMatch(@NonNull Condition<T>... conds) {
        if (conds.length == 0) {
            throw new IllegalArgumentException("Не передано ни одного условия для allMatch");
        }
        Condition<T> composite = CompositeAssertions.and(conds);
        return list -> {
            requireList(list);
            list.forEach(composite::check);
        };
    }

    /**
     * Существует хотя бы один элемент списка, удовлетворяющий всем переданным условиям одновременно.
     *
     * @param conds набор условий (не null, length > 0)
     * @param <T>   тип сущности
     * @return условие проверки наличия совпадения
     * @throws IllegalArgumentException если conds.length == 0
     * @throws NullPointerException     если conds или любой элемент conds равен null
     */
    @SafeVarargs
    public static <T> ListCondition<T> listAnyMatch(@NonNull Condition<T>... conds) {
        if (conds.length == 0) {
            throw new IllegalArgumentException("Не передано ни одного условия для anyMatch");
        }
        Condition<T> composite = CompositeAssertions.and(conds);
        return list -> {
            requireList(list);
            boolean found = list.stream().anyMatch(item -> {
                try {
                    composite.check(item);
                    return true;
                } catch (AssertionError ignored) {
                    return false;
                }
            });
            Assertions.assertThat(found)
                    .as("Ожидалось, что хотя бы один элемент удовлетворяет всем условиям")
                    .isTrue();
        };
    }

    /**
     * Ни один элемент списка не удовлетворяет всем переданным условиям одновременно.
     *
     * @param conds набор условий (не null, length > 0)
     * @param <T>   тип сущности
     * @return условие проверки отсутствия совпадений
     * @throws IllegalArgumentException если conds.length == 0
     * @throws NullPointerException     если conds или любой элемент conds равен null
     */
    @SafeVarargs
    public static <T> ListCondition<T> listNoneMatch(@NonNull Condition<T>... conds) {
        if (conds.length == 0) {
            throw new IllegalArgumentException("Не передано ни одного условия для noneMatch");
        }
        Condition<T> composite = CompositeAssertions.and(conds);
        return list -> {
            requireList(list);
            boolean any = list.stream().anyMatch(item -> {
                try {
                    composite.check(item);
                    return true;
                } catch (AssertionError ignored) {
                    return false;
                }
            });
            Assertions.assertThat(any)
                    .as("Ожидалось, что ни один элемент не удовлетворяет всем условиям")
                    .isFalse();
        };
    }

    /**
     * Ровно times элементов списка удовлетворяют условию cond.
     *
     * @param cond  условие для одного элемента (не null)
     * @param times ожидаемое число совпадений (>= 0)
     * @param <T>   тип сущности
     * @return условие проверки точного количества совпадений
     * @throws IllegalArgumentException если times < 0
     */
    public static <T> ListCondition<T> listExactlyMatches(@NonNull Condition<T> cond, int times) {
        if (times < 0) {
            throw new IllegalArgumentException("Количество не может быть отрицательным");
        }
        return list -> {
            requireList(list);
            long count = countMatches(list, cond);
            Assertions.assertThat(count)
                    .as(fmt("Ожидалось ровно %d вхождений, но найдено %d", times, count))
                    .isEqualTo(times);
        };
    }

    /**
     * Не менее min элементов списка удовлетворяют условию cond.
     *
     * @param cond условие для одного элемента (не null)
     * @param min  минимальное число совпадений (>= 0)
     * @param <T>  тип сущности
     * @return условие проверки минимального количества совпадений
     * @throws IllegalArgumentException если min < 0
     */
    public static <T> ListCondition<T> listAtLeastMatches(@NonNull Condition<T> cond, int min) {
        if (min < 0) {
            throw new IllegalArgumentException("Минимальное количество не может быть отрицательным");
        }
        return list -> {
            requireList(list);
            long count = countMatches(list, cond);
            Assertions.assertThat(count)
                    .as(fmt("Ожидалось минимум %d вхождений, но найдено %d", min, count))
                    .isGreaterThanOrEqualTo(min);
        };
    }

    /**
     * Не более max элементов списка удовлетворяют условию cond.
     *
     * @param cond условие для одного элемента (не null)
     * @param max  максимальное число совпадений (>= 0)
     * @param <T>  тип сущности
     * @return условие проверки максимального количества совпадений
     * @throws IllegalArgumentException если max < 0
     */
    public static <T> ListCondition<T> listAtMostMatches(@NonNull Condition<T> cond, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("Максимальное количество не может быть отрицательным");
        }
        return list -> {
            requireList(list);
            long count = countMatches(list, cond);
            Assertions.assertThat(count)
                    .as(fmt("Ожидалось не более %d вхождений, но найдено %d", max, count))
                    .isLessThanOrEqualTo(max);
        };
    }

    /**
     * Список отсортирован по компаратору comp в порядке возрастания.
     *
     * @param comp компаратор (не null)
     * @param <T>  тип сущности
     * @return условие проверки порядка возрастания
     */
    public static <T> ListCondition<T> listIsSorted(@NonNull Comparator<T> comp) {
        return list -> {
            requireList(list);
            for (int i = 0; i + 1 < list.size(); i++) {
                T a = list.get(i), b = list.get(i + 1);
                Assertions.assertThat(comp.compare(a, b))
                        .as(fmt("Ожидалось, что %s <= %s", a, b))
                        .isLessThanOrEqualTo(0);
            }
        };
    }

    /**
     * Список отсортирован по компаратору comp в порядке убывания.
     *
     * @param comp компаратор (не null)
     * @param <T>  тип сущности
     * @return условие проверки порядка убывания
     */
    public static <T> ListCondition<T> listIsSortedDescending(@NonNull Comparator<T> comp) {
        return list -> {
            requireList(list);
            for (int i = 0; i + 1 < list.size(); i++) {
                T a = list.get(i), b = list.get(i + 1);
                Assertions.assertThat(comp.compare(a, b))
                        .as(fmt("Ожидалось, что %s >= %s", a, b))
                        .isGreaterThanOrEqualTo(0);
            }
        };
    }

    /**
     * Список содержит элемент el.
     *
     * @param el  ожидаемый элемент
     * @param <T> тип сущности
     * @return условие проверки наличия элемента
     */
    public static <T> ListCondition<T> listContainsElement(T el) {
        return list -> {
            requireList(list);
            Assertions.assertThat(list)
                    .as(fmt("Ожидалось, что список содержит элемент %s", el))
                    .contains(el);
        };
    }

    /**
     * Список содержит все переданные элементы.
     *
     * @param elements ожидаемые элементы (не null, length > 0)
     * @param <T>      тип сущности
     * @return условие проверки наличия всех элементов
     * @throws IllegalArgumentException если elements.length == 0
     */
    @SafeVarargs
    public static <T> ListCondition<T> listContainsAllElements(@NonNull T... elements) {
        if (elements.length == 0) {
            throw new IllegalArgumentException("Не передано ни одного элемента для containsAllElements");
        }
        return list -> {
            requireList(list);
            Assertions.assertThat(list)
                    .as(fmt("Ожидалось, что список содержит все элементы %s", Arrays.toString(elements)))
                    .contains(elements);
        };
    }

    /**
     * Список содержит только указанные элементы (в любом порядке).
     *
     * @param elements ожидаемые элементы (не null, length > 0)
     * @param <T>      тип сущности
     * @return условие проверки точного набора элементов
     * @throws IllegalArgumentException если elements.length == 0
     */
    @SafeVarargs
    public static <T> ListCondition<T> listContainsOnly(@NonNull T... elements) {
        if (elements.length == 0) {
            throw new IllegalArgumentException("Не передано ни одного элемента для containsOnly");
        }
        return list -> {
            requireList(list);
            Assertions.assertThat(list)
                    .as(fmt("Ожидалось, что список содержит только элементы %s", Arrays.toString(elements)))
                    .containsOnly(elements);
        };
    }

    /**
     * Список содержит ровно указанные элементы в заданном порядке.
     *
     * @param expected ожидаемый список (не null)
     * @param <T>      тип сущности
     * @return условие проверки точного совпадения в порядке
     */
    public static <T> ListCondition<T> listMatchesExactly(@NonNull List<T> expected) {
        return list -> {
            requireList(list);
            Assertions.assertThat(list)
                    .as(fmt("Ожидалось, что список точно %s", expected))
                    .containsExactlyElementsOf(expected);
        };
    }

    /**
     * Список содержит ровно указанные элементы в любом порядке.
     *
     * @param expected ожидаемый список (не null)
     * @param <T>      тип сущности
     * @return условие проверки точного набора без учета порядка
     */
    public static <T> ListCondition<T> listMatchesExactlyUnordered(@NonNull List<T> expected) {
        return list -> {
            requireList(list);
            Assertions.assertThat(list)
                    .as(fmt("Ожидалось, что список содержит ровно (в любом порядке) %s", expected))
                    .containsExactlyInAnyOrderElementsOf(expected);
        };
    }

    /**
     * Все элементы списка уникальны.
     *
     * @param <T> тип сущности
     * @return условие проверки уникальности
     */
    public static <T> ListCondition<T> listEntitiesAreUnique() {
        return list -> {
            requireList(list);
            long distinct = list.stream().distinct().count();
            Assertions.assertThat(distinct)
                    .as("Ожидалось, что все сущности будут уникальными")
                    .isEqualTo(list.size());
        };
    }

    /**
     * В списке есть дубликаты.
     *
     * @param <T> тип сущности
     * @return условие проверки наличия дубликатов
     */
    public static <T> ListCondition<T> listHasDuplicates() {
        return list -> {
            requireList(list);
            long distinct = list.stream().distinct().count();
            Assertions.assertThat(distinct)
                    .as("Ожидалось, что в списке есть дубликаты")
                    .isLessThan(list.size());
        };
    }

    /**
     * Значения свойства getter уникальны среди всех элементов.
     *
     * @param getter функция получения ключа (не null)
     * @param <T>    тип сущности
     * @param <K>    тип ключа
     * @return условие проверки уникальности по свойству
     */
    public static <T, K> ListCondition<T> listDistinctBy(@NonNull Function<T, K> getter) {
        return list -> {
            requireList(list);
            long distinct = list.stream().map(getter).distinct().count();
            Assertions.assertThat(distinct)
                    .as("Ожидалось, что значения по ключу будут уникальными")
                    .isEqualTo(list.size());
        };
    }

    /**
     * Сумма значений getter по всем элементам.
     *
     * @param getter   функция получения числового свойства (не null)
     * @param expected ожидаемая сумма
     * @param <T>      тип сущности
     * @return условие проверки суммы
     */
    public static <T> ListCondition<T> listSumEqual(
            @NonNull Function<T, ? extends Number> getter,
            double expected) {
        return list -> {
            requireList(list);
            double sum = list.stream()
                    .mapToDouble(i -> getter.apply(i).doubleValue())
                    .sum();
            Assertions.assertThat(sum)
                    .as(fmt("Ожидалось, что сумма равна %s, но была %s", expected, sum))
                    .isEqualTo(expected);
        };
    }

    /**
     * Среднее значение getter по всем элементам.
     *
     * @param getter   функция получения числового свойства (не null)
     * @param expected ожидаемое среднее
     * @param <T>      тип сущности
     * @return условие проверки среднего
     * @throws AssertionError если список пустой
     */
    public static <T> ListCondition<T> listAverageEqual(
            @NonNull Function<T, ? extends Number> getter,
            double expected) {
        return list -> {
            requireList(list);
            double avg = list.stream()
                    .mapToDouble(i -> getter.apply(i).doubleValue())
                    .average()
                    .orElseThrow(() -> new AssertionError("Невозможно вычислить среднее по пустому списку"));
            Assertions.assertThat(avg)
                    .as(fmt("Ожидалось, что среднее равно %s, но было %s", expected, avg))
                    .isEqualTo(expected);
        };
    }

    /**
     * Значение свойства getter каждого элемента равно expected.
     *
     * @param getter   функция получения свойства (не null)
     * @param expected ожидаемое значение
     * @param <T>      тип сущности
     * @return условие проверки равенства значений
     */
    public static <T> ListCondition<T> listValuesEqual(
            @NonNull Function<T, ?> getter,
            Object expected) {
        return list -> {
            requireList(list);
            list.forEach(item -> {
                Object actual = getter.apply(item);
                Assertions.assertThat(actual)
                        .as(fmt("Ожидалось, что значение свойства равно %s, но было %s", expected, actual))
                        .isEqualTo(expected);
            });
        };
    }

    /**
     * Свойства getter всех элементов уникальны.
     *
     * @param getter функция получения свойства (не null)
     * @param <T>    тип сущности
     * @return условие проверки уникальности свойств
     */
    public static <T> ListCondition<T> listEntitiesPropertyAreDistinct(@NonNull Function<T, ?> getter) {
        return list -> {
            requireList(list);
            long distinct = list.stream().map(getter).distinct().count();
            Assertions.assertThat(distinct)
                    .as("Ожидалось, что значения свойства будут уникальными")
                    .isEqualTo(list.size());
        };
    }

    /**
     * Список не содержит null-значений.
     *
     * @param <T> тип сущности
     * @return условие проверки отсутствия null
     */
    public static <T> ListCondition<T> listNoNulls() {
        return list -> {
            requireList(list);
            Assertions.assertThat(list)
                    .as("Ожидалось, что список не содержит null")
                    .noneMatch(Objects::isNull);
        };
    }

    /**
     * Размеры групп, полученных по ключу getter.
     *
     * @param getter        функция получения ключа группы (не null)
     * @param expectedSizes ожидания по размеру каждой группы (не null)
     * @param <T>           тип сущности
     * @param <K>           тип ключа
     * @return условие проверки размеров групп
     */
    public static <T, K> ListCondition<T> listGroupedBySize(
            @NonNull Function<T, K> getter,
            @NonNull Map<K, Integer> expectedSizes) {
        return list -> {
            requireList(list);
            Map<K, Long> actual = list.stream()
                    .collect(Collectors.groupingBy(getter, Collectors.counting()));
            expectedSizes.forEach((key, size) -> {
                long got = actual.getOrDefault(key, 0L);
                Assertions.assertThat(got)
                        .as(fmt("Ожидалось размер группы %s = %d, но было %d", key, size, got))
                        .isEqualTo(size);
            });
        };
    }

    /**
     * Список не равен null.
     *
     * @param list проверяемый список
     * @throws NullPointerException если список равен null
     */
    private static <T> void requireList(List<T> list) {
        requireNonNull(list, "Список сущностей не должен быть null");
    }

    /**
     * Форматирует строку с аргументами.
     *
     * @param template шаблон
     * @param args     аргументы
     * @return отформатированная строка
     */
    private static String fmt(String template, Object... args) {
        return String.format(template, args);
    }

    /**
     * Подсчитывает количество элементов, удовлетворяющих условию.
     *
     * @param list список
     * @param cond условие для одного элемента
     * @param <T>  тип элемента
     * @return число совпадений
     */
    private static <T> long countMatches(List<T> list, Condition<T> cond) {
        return list.stream()
                .filter(item -> {
                    try {
                        cond.check(item);
                        return true;
                    } catch (AssertionError ignored) {
                        return false;
                    }
                })
                .count();
    }
}
