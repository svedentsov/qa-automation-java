package db.matcher.assertions;

import db.matcher.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.function.Function;
import java.util.Comparator;

/**
 * Утилитный класс для проверки списков сущностей.
 */
@UtilityClass
public class EntityAssertions {

    /**
     * Проверяет, что список сущностей не пуст.
     *
     * @param <T> тип сущности
     * @return проверка наличия хотя бы одной сущности
     */
    public static <T> Condition<T> exists() {
        return entities -> {
            if (entities instanceof List) {
                @SuppressWarnings("unchecked")
                List<T> list = (List<T>) entities;
                Assertions.assertThat(list)
                        .as("Проверка наличия хотя бы одной сущности")
                        .isNotEmpty();
            } else {
                throw new IllegalArgumentException("Ожидался список сущностей");
            }
        };
    }

    /**
     * Проверяет, что количество сущностей равно заданному значению.
     *
     * @param count ожидаемое количество сущностей
     * @param <T>   тип сущности
     * @return проверка количества сущностей
     */
    public static <T> Condition<T> countEqual(int count) {
        return entities -> {
            if (entities instanceof List) {
                @SuppressWarnings("unchecked")
                List<T> list = (List<T>) entities;
                Assertions.assertThat(list)
                        .as("Количество сущностей должно быть равно %d", count)
                        .hasSize(count);
            } else {
                throw new IllegalArgumentException("Ожидался список сущностей");
            }
        };
    }

    /**
     * Проверяет, что количество сущностей больше заданного значения.
     *
     * @param count минимальное количество сущностей
     * @param <T>   тип сущности
     * @return проверка количества сущностей
     */
    public static <T> Condition<T> countGreater(int count) {
        return entities -> {
            if (entities instanceof List) {
                @SuppressWarnings("unchecked")
                List<T> list = (List<T>) entities;
                Assertions.assertThat(list)
                        .as("Количество сущностей должно быть больше %d", count)
                        .hasSizeGreaterThan(count);
            } else {
                throw new IllegalArgumentException("Ожидался список сущностей");
            }
        };
    }

    /**
     * Проверяет, что все сущности удовлетворяют указанной проверке.
     *
     * @param condition проверка для каждой сущности
     * @param <T>       тип сущности
     * @return проверка сущностей
     */
    public static <T> Condition<T> allMatch(Condition<T> condition) {
        return entity -> condition.check(entity);
    }

    /**
     * Проверяет, что хотя бы одна сущность удовлетворяет указанной проверке.
     *
     * @param condition проверка для сущности
     * @param <T>       тип сущности
     * @return проверка сущностей
     */
    public static <T> Condition<T> anyEntityMatches(Condition<T> condition) {
        return entities -> {
            if (entities instanceof List) {
                @SuppressWarnings("unchecked")
                List<T> list = (List<T>) entities;
                boolean matchFound = list.stream().anyMatch(entity -> {
                    try {
                        condition.check(entity);
                        return true;
                    } catch (AssertionError e) {
                        return false;
                    }
                });
                Assertions.assertThat(matchFound)
                        .as("Ожидалось, что хотя бы одна сущность удовлетворяет условию")
                        .isTrue();
            } else {
                throw new IllegalArgumentException("Ожидался список сущностей");
            }
        };
    }

    /**
     * Проверяет, что ни одна сущность не удовлетворяет указанной проверке.
     *
     * @param condition проверка для сущности
     * @param <T>       тип сущности
     * @return проверка сущностей
     */
    public static <T> Condition<T> noMatches(Condition<T> condition) {
        return entities -> {
            if (entities instanceof List) {
                @SuppressWarnings("unchecked")
                List<T> list = (List<T>) entities;
                boolean anyMatch = list.stream().anyMatch(entity -> {
                    try {
                        condition.check(entity);
                        return true;
                    } catch (AssertionError e) {
                        return false;
                    }
                });
                Assertions.assertThat(anyMatch)
                        .as("Найдена сущность, соответствующая условию, хотя ожидалось отсутствие совпадений")
                        .isFalse();
            } else {
                throw new IllegalArgumentException("Ожидался список сущностей");
            }
        };
    }

    /**
     * Проверяет, что значение свойства для каждой сущности равно ожидаемому.
     *
     * @param getter        функция для получения свойства
     * @param expectedValue ожидаемое значение свойства
     * @param <T>           тип сущности
     * @return проверка сущностей
     */
    public static <T> Condition<T> valuesEqual(Function<T, ?> getter, Object expectedValue) {
        return entity -> {
            Object actualValue = getter.apply(entity);
            Assertions.assertThat(actualValue)
                    .as("Значение должно быть равно %s", expectedValue)
                    .isEqualTo(expectedValue);
        };
    }

    /**
     * Проверяет, что все сущности в списке уникальны (без дублирующихся элементов).
     *
     * @param <T> тип сущности
     * @return условие проверки уникальности сущностей
     */
    public static <T> Condition<T> entitiesAreUnique() {
        return entities -> {
            if (entities instanceof List) {
                List<?> list = (List<?>) entities;
                long distinctCount = list.stream().distinct().count();
                Assertions.assertThat(distinctCount)
                        .as("Все сущности должны быть уникальными")
                        .isEqualTo(list.size());
            } else {
                throw new IllegalArgumentException("Ожидался список сущностей");
            }
        };
    }

    /**
     * Проверяет, что список отсортирован согласно заданному компаратору.
     *
     * @param comparator компаратор для сравнения сущностей
     * @param <T>        тип сущности
     * @return условие проверки сортировки списка
     */
    public static <T> Condition<T> isSorted(Comparator<T> comparator) {
        return entities -> {
            if (entities instanceof List) {
                @SuppressWarnings("unchecked")
                List<T> list = (List<T>) entities;
                for (int i = 0; i < list.size() - 1; i++) {
                    T current = list.get(i);
                    T next = list.get(i + 1);
                    Assertions.assertThat(comparator.compare(current, next))
                            .as("Список должен быть отсортирован согласно заданному компаратору: элементы %s и %s", current, next)
                            .isLessThanOrEqualTo(0);
                }
            } else {
                throw new IllegalArgumentException("Ожидался список сущностей");
            }
        };
    }

    /**
     * Проверяет, что список содержит не менее указанного количества сущностей.
     *
     * @param minCount минимальное ожидаемое количество сущностей
     * @param <T>      тип сущности
     * @return условие проверки минимального количества сущностей
     */
    public static <T> Condition<T> hasMinimumCount(int minCount) {
        return entities -> {
            if (entities instanceof List) {
                List<?> list = (List<?>) entities;
                Assertions.assertThat(list.size())
                        .as("Количество сущностей должно быть не меньше %d", minCount)
                        .isGreaterThanOrEqualTo(minCount);
            } else {
                throw new IllegalArgumentException("Ожидался список сущностей");
            }
        };
    }

    /**
     * Проверяет, что список содержит не более указанного количества сущностей.
     *
     * @param maxCount максимальное ожидаемое количество сущностей
     * @param <T>      тип сущности
     * @return условие проверки максимального количества сущностей
     */
    public static <T> Condition<T> hasMaximumCount(int maxCount) {
        return entities -> {
            if (entities instanceof List) {
                List<?> list = (List<?>) entities;
                Assertions.assertThat(list.size())
                        .as("Количество сущностей должно быть не больше %d", maxCount)
                        .isLessThanOrEqualTo(maxCount);
            } else {
                throw new IllegalArgumentException("Ожидался список сущностей");
            }
        };
    }

    /**
     * Проверяет, что для каждой сущности, значение, полученное с помощью getter, равно ожидаемому.
     *
     * @param getter        функция для получения свойства из сущности
     * @param expectedValue ожидаемое значение свойства
     * @param <T>           тип сущности
     * @return условие проверки свойства для каждой сущности
     */
    public static <T> Condition<T> allEntitiesSatisfy(Function<T, ?> getter, Object expectedValue) {
        return entities -> {
            if (entities instanceof List) {
                @SuppressWarnings("unchecked")
                List<T> list = (List<T>) entities;
                for (T entity : list) {
                    Object actual = getter.apply(entity);
                    Assertions.assertThat(actual)
                            .as("Значение, полученное с помощью getter, должно быть равно %s", expectedValue)
                            .isEqualTo(expectedValue);
                }
            } else {
                throw new IllegalArgumentException("Ожидался список сущностей");
            }
        };
    }

    /**
     * Проверяет, что значения свойства, извлечённые из каждой сущности с помощью getter, отсортированы в естественном порядке.
     *
     * @param getter функция для получения сравнимого свойства из сущности
     * @param <T>    тип сущности
     * @return условие проверки сортировки свойств сущностей
     */
    public static <T> Condition<T> entityPropertyIsSorted(Function<T, ? extends Comparable> getter) {
        return entities -> {
            if (entities instanceof List) {
                @SuppressWarnings("unchecked")
                List<T> list = (List<T>) entities;
                for (int i = 0; i < list.size() - 1; i++) {
                    Comparable current = getter.apply(list.get(i));
                    Comparable next = getter.apply(list.get(i + 1));
                    Assertions.assertThat(current.compareTo(next))
                            .as("Свойства сущностей должны быть отсортированы: %s и %s", current, next)
                            .isLessThanOrEqualTo(0);
                }
            } else {
                throw new IllegalArgumentException("Ожидался список сущностей");
            }
        };
    }

    /**
     * Проверяет, что хотя бы одна сущность имеет значение свойства (полученного с помощью getter),
     * равное ожидаемому.
     *
     * @param getter        функция для получения свойства из сущности
     * @param expectedValue ожидаемое значение свойства
     * @param <T>           тип сущности
     * @return условие проверки наличия хотя бы одной сущности с нужным значением свойства
     */
    public static <T> Condition<T> anyEntityHasProperty(Function<T, ?> getter, Object expectedValue) {
        return entities -> {
            if (entities instanceof List) {
                @SuppressWarnings("unchecked")
                List<T> list = (List<T>) entities;
                boolean found = list.stream().anyMatch(entity -> expectedValue.equals(getter.apply(entity)));
                Assertions.assertThat(found)
                        .as("Ожидалось, что хотя бы одна сущность имеет свойство, равное %s", expectedValue)
                        .isTrue();
            } else {
                throw new IllegalArgumentException("Ожидался список сущностей");
            }
        };
    }

    /**
     * Проверяет, что для каждой сущности значение, полученное с помощью getter, не равно null.
     *
     * @param getter функция для получения свойства из сущности
     * @param <T>    тип сущности
     * @return условие проверки отсутствия null в свойствах сущностей
     */
    public static <T> Condition<T> allEntitiesHaveNonNullProperties(Function<T, ?> getter) {
        return entities -> {
            if (entities instanceof List) {
                @SuppressWarnings("unchecked")
                List<T> list = (List<T>) entities;
                for (T entity : list) {
                    Object val = getter.apply(entity);
                    Assertions.assertThat(val)
                            .as("Свойство сущности не должно быть null")
                            .isNotNull();
                }
            } else {
                throw new IllegalArgumentException("Ожидался список сущностей");
            }
        };
    }

    /**
     * Проверяет, что список сущностей не содержит null-значений.
     *
     * @param <T> тип сущности
     * @return условие проверки отсутствия null в списке
     */
    public static <T> Condition<T> entitiesContainNoNulls() {
        return entities -> {
            if (entities instanceof List) {
                List<?> list = (List<?>) entities;
                // Используем позитивную проверку: каждый элемент списка не равен null.
                Assertions.assertThat(list)
                        .as("Список сущностей не должен содержать null")
                        .allSatisfy(entity -> Assertions.assertThat(entity)
                                .isNotNull());
            } else {
                throw new IllegalArgumentException("Ожидался список сущностей");
            }
        };
    }

    /**
     * Проверяет, что значения свойства, извлечённые из каждой сущности с помощью getter, являются уникальными.
     *
     * @param getter функция для получения свойства из сущности
     * @param <T>    тип сущности
     * @return условие проверки уникальности значений свойства
     */
    public static <T> Condition<T> entitiesPropertyAreDistinct(Function<T, ?> getter) {
        return entities -> {
            if (entities instanceof List) {
                @SuppressWarnings("unchecked")
                List<T> list = (List<T>) entities;
                long distinctCount = list.stream()
                        .map(getter)
                        .filter(v -> v != null)
                        .distinct()
                        .count();
                Assertions.assertThat(distinctCount)
                        .as("Свойства, извлечённые с помощью getter, должны быть уникальными")
                        .isEqualTo(list.size());
            } else {
                throw new IllegalArgumentException("Ожидался список сущностей");
            }
        };
    }

    /**
     * Проверяет, что порядок значений свойства, извлечённых из сущностей с помощью getter,
     * точно соответствует ожидаемому.
     *
     * @param getter        функция для получения свойства из сущности
     * @param expectedOrder список ожидаемого порядка значений
     * @param <T>           тип сущности
     * @return условие проверки порядка значений свойства
     */
    public static <T> Condition<T> entitiesMatchOrder(Function<T, ? extends Comparable> getter, List<?> expectedOrder) {
        return entities -> {
            if (entities instanceof List) {
                @SuppressWarnings("unchecked")
                List<T> list = (List<T>) entities;
                List<?> actualOrder = list.stream().map(getter).toList();
                Assertions.assertThat(actualOrder)
                        .as("Порядок значений свойства должен соответствовать ожидаемому: %s", expectedOrder)
                        .isEqualTo(expectedOrder);
            } else {
                throw new IllegalArgumentException("Ожидался список сущностей");
            }
        };
    }

    /**
     * Проверяет, что размер списка сущностей находится между заданными границами (включительно).
     *
     * @param min минимальное количество сущностей
     * @param max максимальное количество сущностей
     * @param <T> тип сущности
     * @return условие проверки размера списка
     */
    public static <T> Condition<T> hasSizeBetween(int min, int max) {
        return entities -> {
            if (entities instanceof List) {
                List<?> list = (List<?>) entities;
                int size = list.size();
                Assertions.assertThat(size)
                        .as("Размер списка должен быть между %d и %d", min, max)
                        .isBetween(min, max);
            } else {
                throw new IllegalArgumentException("Ожидался список сущностей");
            }
        };
    }
}
