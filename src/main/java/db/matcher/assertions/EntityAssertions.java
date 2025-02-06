package db.matcher.assertions;

import db.matcher.Checker;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.function.Function;

/**
 * Утилитный класс для проверки списков сущностей.
 */
@UtilityClass
public class EntityAssertions {

    /**
     * Возвращает проверку, что список сущностей не пуст.
     *
     * @param <T> тип сущности
     * @return проверка сущностей
     */
    public static <T> Checker<T> exists() {
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
     * Возвращает проверку, что количество сущностей равно заданному значению.
     *
     * @param count ожидаемое количество сущностей
     * @param <T>   тип сущности
     * @return проверка сущностей
     */
    public static <T> Checker<T> countEqual(int count) {
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
     * Возвращает проверку, что количество сущностей больше заданного значения.
     *
     * @param count минимальное количество сущностей
     * @param <T>   тип сущности
     * @return проверка сущностей
     */
    public static <T> Checker<T> countGreater(int count) {
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
     * Возвращает проверку, что все сущности удовлетворяют указанной проверке.
     *
     * @param checker проверка для каждой сущности
     * @param <T>     тип сущности
     * @return проверка сущностей
     */
    public static <T> Checker<T> allMatch(Checker<T> checker) {
        return entity -> checker.check(entity);
    }

    /**
     * Возвращает проверку, что хотя бы одна сущность удовлетворяет указанной проверке.
     *
     * @param checker проверка для сущности
     * @param <T>     тип сущности
     * @return проверка сущностей
     */
    public static <T> Checker<T> anyEntityMatches(Checker<T> checker) {
        return entities -> {
            if (entities instanceof List) {
                @SuppressWarnings("unchecked")
                List<T> list = (List<T>) entities;
                boolean matchFound = list.stream().anyMatch(entity -> {
                    try {
                        checker.check(entity);
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
     * Возвращает проверку, что ни одна сущность не удовлетворяет указанной проверке.
     *
     * @param checker проверка для сущности
     * @param <T>     тип сущности
     * @return проверка сущностей
     */
    public static <T> Checker<T> noMatches(Checker<T> checker) {
        return entities -> {
            if (entities instanceof List) {
                @SuppressWarnings("unchecked")
                List<T> list = (List<T>) entities;
                boolean anyMatch = list.stream().anyMatch(entity -> {
                    try {
                        checker.check(entity);
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
     * Возвращает проверку, что значение свойства для каждой сущности равно ожидаемому.
     *
     * @param getter        функция для получения свойства
     * @param expectedValue ожидаемое значение свойства
     * @param <T>           тип сущности
     * @return проверка сущностей
     */
    public static <T> Checker<T> valuesEqual(Function<T, ?> getter, Object expectedValue) {
        return entity -> {
            Object actualValue = getter.apply(entity);
            Assertions.assertThat(actualValue)
                    .as("Значение должно быть равно %s", expectedValue)
                    .isEqualTo(expectedValue);
        };
    }
}
