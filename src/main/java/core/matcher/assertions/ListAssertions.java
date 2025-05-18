package core.matcher.assertions;

import core.matcher.Condition;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Утилитный класс для создания условий проверки коллекций сущностей.
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
     * Проверяет, что список пуст.
     *
     * @param <T> тип сущности
     * @return условие: список должен быть пустым
     */
    public static <T> ListCondition<T> isEmpty() {
        return list -> {
            requireList(list);
            Assertions.assertThat(list)
                    .as("Ожидалось, что список будет пуст")
                    .isEmpty();
        };
    }

    /**
     * Проверяет, что список не пуст.
     *
     * @param <T> тип сущности
     * @return условие: список не должен быть пустым
     */
    public static <T> ListCondition<T> isNotEmpty() {
        return list -> {
            requireList(list);
            Assertions.assertThat(list)
                    .as("Ожидалось, что список не пуст")
                    .isNotEmpty();
        };
    }

    /**
     * Проверяет, что размер списка равен exact.
     *
     * @param expected ожидаемый размер
     * @param <T>      тип сущности
     * @return условие: список.size() == expected
     * @throws IllegalArgumentException если expected < 0
     */
    public static <T> ListCondition<T> countEqual(int expected) {
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
     * Проверяет, что размер списка меньше upperExclusive.
     *
     * @param upperExclusive верхняя граница (исключая)
     * @param <T>            тип сущности
     * @return условие: список.size() < upperExclusive
     * @throws IllegalArgumentException если upperExclusive <= 0
     */
    public static <T> ListCondition<T> countLessThan(int upperExclusive) {
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
     * Проверяет, что размер списка больше lowerExclusive.
     *
     * @param lowerExclusive нижняя граница (исключая)
     * @param <T>            тип сущности
     * @return условие: список.size() > lowerExclusive
     * @throws IllegalArgumentException если lowerExclusive < 0
     */
    public static <T> ListCondition<T> countGreaterThan(int lowerExclusive) {
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
     * Проверяет, что размер списка находится между minInclusive и maxInclusive включительно.
     *
     * @param minInclusive минимальный размер
     * @param maxInclusive максимальный размер
     * @param <T>          тип сущности
     * @return условие: minInclusive <= список.size() <= maxInclusive
     * @throws IllegalArgumentException если minInclusive < 0 или maxInclusive < minInclusive
     */
    public static <T> ListCondition<T> hasSizeBetween(int minInclusive, int maxInclusive) {
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
     * Проверяет, что все элементы списка удовлетворяют условию cond.
     *
     * @param cond условие для одного элемента
     * @param <T>  тип сущности
     * @return условие: для каждого элемента cond.check(item)
     */
    public static <T> ListCondition<T> allMatch(@NonNull Condition<T> cond) {
        return list -> {
            requireList(list);
            list.forEach(cond::check);
        };
    }

    /**
     * Проверяет, что хотя бы один элемент списка удовлетворяет условию cond.
     *
     * @param cond условие для одного элемента
     * @param <T>  тип сущности
     * @return условие: существует элемент, для которого cond.check(item) не бросает AssertionError
     */
    public static <T> ListCondition<T> anyMatch(@NonNull Condition<T> cond) {
        return list -> {
            requireList(list);
            boolean found = list.stream().anyMatch(item -> {
                try {
                    cond.check(item);
                    return true;
                } catch (AssertionError e) {
                    return false;
                }
            });
            Assertions.assertThat(found)
                    .as("Ожидалось, что хотя бы одна сущность удовлетворяет условию")
                    .isTrue();
        };
    }

    /**
     * Проверяет, что ни один элемент списка не удовлетворяет условию cond.
     *
     * @param cond условие для одного элемента
     * @param <T>  тип сущности
     * @return условие: ни один элемент не проходит cond.check(item)
     */
    public static <T> ListCondition<T> noneMatch(@NonNull Condition<T> cond) {
        return list -> {
            requireList(list);
            boolean any = list.stream().anyMatch(item -> {
                try {
                    cond.check(item);
                    return true;
                } catch (AssertionError e) {
                    return false;
                }
            });
            Assertions.assertThat(any)
                    .as("Ожидалось, что ни одна сущность не удовлетворяет условию")
                    .isFalse();
        };
    }

    /**
     * Проверяет, что ровно times элементов списка удовлетворяют условию cond.
     *
     * @param cond  условие для одного элемента
     * @param times ожидаемое число совпадений
     * @param <T>   тип сущности
     * @return условие: ровно times совпадений
     * @throws IllegalArgumentException если times < 0
     */
    public static <T> ListCondition<T> exactlyMatches(@NonNull Condition<T> cond, int times) {
        if (times < 0) {
            throw new IllegalArgumentException("Количество не может быть отрицательным");
        }
        return list -> {
            requireList(list);
            long cnt = list.stream().filter(item -> {
                try {
                    cond.check(item);
                    return true;
                } catch (AssertionError e) {
                    return false;
                }
            }).count();
            Assertions.assertThat(cnt)
                    .as(fmt("Ожидалось ровно %d вхождений, но найдено %d", times, cnt))
                    .isEqualTo(times);
        };
    }

    /**
     * Проверяет, что не менее min элементов списка удовлетворяют условию cond.
     *
     * @param cond условие для одного элемента
     * @param min  минимальное число совпадений
     * @param <T>  тип сущности
     * @return условие: count >= min
     * @throws IllegalArgumentException если min < 0
     */
    public static <T> ListCondition<T> atLeastMatches(@NonNull Condition<T> cond, int min) {
        if (min < 0) {
            throw new IllegalArgumentException("Минимальное количество не может быть отрицательным");
        }
        return list -> {
            requireList(list);
            long cnt = list.stream().filter(item -> {
                try {
                    cond.check(item);
                    return true;
                } catch (AssertionError e) {
                    return false;
                }
            }).count();
            Assertions.assertThat(cnt)
                    .as(fmt("Ожидалось минимум %d вхождений, но найдено %d", min, cnt))
                    .isGreaterThanOrEqualTo(min);
        };
    }

    /**
     * Проверяет, что не более max элементов списка удовлетворяют условию cond.
     *
     * @param cond условие для одного элемента
     * @param max  максимальное число совпадений
     * @param <T>  тип сущности
     * @return условие: count <= max
     * @throws IllegalArgumentException если max < 0
     */
    public static <T> ListCondition<T> atMostMatches(@NonNull Condition<T> cond, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("Максимальное количество не может быть отрицательным");
        }
        return list -> {
            requireList(list);
            long cnt = list.stream().filter(item -> {
                try {
                    cond.check(item);
                    return true;
                } catch (AssertionError e) {
                    return false;
                }
            }).count();
            Assertions.assertThat(cnt)
                    .as(fmt("Ожидалось не более %d вхождений, но найдено %d", max, cnt))
                    .isLessThanOrEqualTo(max);
        };
    }

    /**
     * Проверяет, что список отсортирован по компаратору comp в порядке возрастания.
     *
     * @param comp компаратор для сравнения
     * @param <T>  тип сущности
     * @return условие: для каждого i: comp.compare(list[i], list[i+1]) <= 0
     */
    public static <T> ListCondition<T> isSorted(@NonNull Comparator<T> comp) {
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
     * Проверяет, что список отсортирован по компаратору comp в порядке убывания.
     *
     * @param comp компаратор для сравнения
     * @param <T>  тип сущности
     * @return условие: для каждого i: comp.compare(list[i], list[i+1]) >= 0
     */
    public static <T> ListCondition<T> isSortedDescending(@NonNull Comparator<T> comp) {
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
     * Проверяет, что список содержит элемент el.
     *
     * @param el  ожидаемый элемент
     * @param <T> тип сущности
     * @return условие: list.contains(el)
     */
    public static <T> ListCondition<T> containsElement(T el) {
        return list -> {
            requireList(list);
            Assertions.assertThat(list)
                    .as(fmt("Ожидалось, что список содержит элемент %s", el))
                    .contains(el);
        };
    }

    /**
     * Проверяет, что список содержит все переданные элементы.
     *
     * @param elements ожидаемые элементы
     * @param <T>      тип сущности
     * @return условие: list.contains(elements...)
     */
    @SafeVarargs
    public static <T> ListCondition<T> containsAllElements(@NonNull T... elements) {
        return list -> {
            requireList(list);
            Assertions.assertThat(list)
                    .as(fmt("Ожидалось, что список содержит все элементы %s", Arrays.toString(elements)))
                    .contains(elements);
        };
    }

    /**
     * Проверяет, что список содержит только указанные элементы (в любом порядке).
     *
     * @param elements ожидаемые элементы
     * @param <T>      тип сущности
     * @return условие: list.containsOnly(elements...)
     */
    @SafeVarargs
    public static <T> ListCondition<T> containsOnly(@NonNull T... elements) {
        return list -> {
            requireList(list);
            Assertions.assertThat(list)
                    .as(fmt("Ожидалось, что список содержит только элементы %s", Arrays.toString(elements)))
                    .containsOnly(elements);
        };
    }

    /**
     * Проверяет, что список содержит ровно указанные элементы в заданном порядке.
     *
     * @param expected список ожидаемых элементов в порядке
     * @param <T>      тип сущности
     * @return условие: containsExactlyElementsOf(expected)
     */
    public static <T> ListCondition<T> containsExactly(List<T> expected) {
        return list -> {
            requireList(list);
            Assertions.assertThat(list)
                    .as(fmt("Ожидалось, что список точно %s", expected))
                    .containsExactlyElementsOf(expected);
        };
    }

    /**
     * Проверяет, что список содержит ровно указанные элементы в любом порядке.
     *
     * @param expected список ожидаемых элементов
     * @param <T>      тип сущности
     * @return условие: containsExactlyInAnyOrderElementsOf(expected)
     */
    public static <T> ListCondition<T> containsExactlyInAnyOrder(List<T> expected) {
        return list -> {
            requireList(list);
            Assertions.assertThat(list)
                    .as(fmt("Ожидалось, что список содержит ровно (в любом порядке) %s", expected))
                    .containsExactlyInAnyOrderElementsOf(expected);
        };
    }

    /**
     * Проверяет, что все элементы списка уникальны.
     *
     * @param <T> тип сущности
     * @return условие: distinct count == list.size()
     */
    public static <T> ListCondition<T> entitiesAreUnique() {
        return list -> {
            requireList(list);
            long distinct = list.stream().distinct().count();
            Assertions.assertThat(distinct)
                    .as("Ожидалось, что все сущности будут уникальными")
                    .isEqualTo(list.size());
        };
    }

    /**
     * Проверяет, что в списке есть дубликаты.
     *
     * @param <T> тип сущности
     * @return условие: distinct count < list.size()
     */
    public static <T> ListCondition<T> hasDuplicates() {
        return list -> {
            requireList(list);
            long distinct = list.stream().distinct().count();
            Assertions.assertThat(distinct)
                    .as("Ожидалось, что в списке есть дубликаты")
                    .isLessThan(list.size());
        };
    }

    /**
     * Проверяет, что значения свойства getter уникальны среди всех элементов.
     *
     * @param getter функция получения ключа
     * @param <T>    тип сущности
     * @param <K>    тип ключа
     * @return условие: distinct(getter) == list.size()
     */
    public static <T, K> ListCondition<T> distinctBy(@NonNull Function<T, K> getter) {
        return list -> {
            requireList(list);
            long distinct = list.stream().map(getter).distinct().count();
            Assertions.assertThat(distinct)
                    .as("Ожидалось, что значения по ключу будут уникальными")
                    .isEqualTo(list.size());
        };
    }

    /**
     * Проверяет сумму значений getter по всем элементам.
     *
     * @param getter   функция получения числового свойства
     * @param expected ожидаемая сумма
     * @param <T>      тип сущности
     * @return условие: sum(getter) == expected
     */
    public static <T> ListCondition<T> sumEqual(
            @NonNull Function<T, ? extends Number> getter,
            double expected
    ) {
        return list -> {
            requireList(list);
            double sum = list.stream().mapToDouble(i -> getter.apply(i).doubleValue()).sum();
            Assertions.assertThat(sum)
                    .as(fmt("Ожидалось, что сумма равна %s, но была %s", expected, sum))
                    .isEqualTo(expected);
        };
    }

    /**
     * Проверяет среднее значение getter по всем элементам.
     *
     * @param getter   функция получения числового свойства
     * @param expected ожидаемое среднее
     * @param <T>      тип сущности
     * @return условие: average(getter) == expected
     */
    public static <T> ListCondition<T> averageEqual(
            @NonNull Function<T, ? extends Number> getter,
            double expected
    ) {
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
     * Проверяет, что значение свойства getter каждого элемента равно expected.
     *
     * @param getter   функция получения свойства
     * @param expected ожидаемое значение
     * @param <T>      тип сущности
     * @return условие: getter(item) == expected для каждого элемента
     */
    public static <T> ListCondition<T> valuesEqual(
            @NonNull Function<T, ?> getter,
            Object expected
    ) {
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
     * Проверяет, что свойства getter всех элементов уникальны.
     *
     * @param getter функция получения свойства
     * @param <T>    тип сущности
     * @return условие: distinct(getter) == list.size()
     */
    public static <T> ListCondition<T> entitiesPropertyAreDistinct(@NonNull Function<T, ?> getter) {
        return list -> {
            requireList(list);
            long distinct = list.stream().map(getter).distinct().count();
            Assertions.assertThat(distinct)
                    .as("Ожидалось, что значения свойства будут уникальными")
                    .isEqualTo(list.size());
        };
    }

    /**
     * Проверяет, что список не содержит null-значений.
     *
     * @param <T> тип сущности
     * @return условие: !list.contains(null)
     */
    public static <T> ListCondition<T> noNulls() {
        return list -> {
            requireList(list);
            Assertions.assertThat(list)
                    .as("Ожидалось, что список не содержит null")
                    .noneMatch(Objects::isNull);
        };
    }

    /**
     * Проверяет размеры групп, полученных по ключу getter.
     *
     * @param getter        функция получения ключа группы
     * @param expectedSizes карта ожиданий: ключ -> размер группы
     * @param <T>           тип сущности
     * @param <K>           тип ключа
     * @return условие: для каждого ключа actualCount == expectedSize
     */
    public static <T, K> ListCondition<T> groupedBySize(
            @NonNull Function<T, K> getter,
            @NonNull Map<K, Integer> expectedSizes
    ) {
        return list -> {
            requireList(list);
            Map<K, Long> actual = list.stream()
                    .collect(Collectors.groupingBy(getter, Collectors.counting()));
            expectedSizes.forEach((key, size) -> {
                long got = actual.getOrDefault(key, 0L);
                Assertions.assertThat(got)
                        .as(fmt("Ожидался размер группы '%s' = %d, но было %d", key, size, got))
                        .isEqualTo(size);
            });
        };
    }

    /**
     * Проверяет, что список не равен null.
     *
     * @param list список для проверки
     * @param <T>  тип сущности
     * @throws NullPointerException если список null
     */
    private static <T> void requireList(@NonNull List<T> list) {
        Objects.requireNonNull(list, "Список сущностей не должен быть null");
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
}
