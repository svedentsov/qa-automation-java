package db.matcher.assertions;

import db.matcher.condition.Condition;
import db.matcher.condition.Conditions;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.function.Function;

/**
 * Утилитный класс с наборами условий, применяющихся к спискам сущностей:
 * количество, присутствие, отсутствие, все соответствуют условию и т.д.
 */
@UtilityClass
public class EntityAssertions {

    /**
     * Проверка наличия хотя бы одной сущности в списке.
     *
     * @param <T> тип сущности
     */
    public static <T> Conditions<T> entitiesExist() {
        return entities -> Assertions.assertThat(entities)
                .as("Проверка наличия хотя бы одной сущности")
                .isNotEmpty();
    }

    /**
     * Проверка, что количество сущностей равно указанному значению.
     *
     * @param count ожидаемое количество
     * @param <T>   тип сущности
     */
    public static <T> Conditions<T> entitiesCountEqual(int count) {
        return entities -> Assertions.assertThat(entities)
                .as("Количество сущностей должно быть равно %d", count)
                .hasSize(count);
    }

    /**
     * Проверка, что количество сущностей больше указанного.
     *
     * @param count минимальное количество
     * @param <T>   тип сущности
     */
    public static <T> Conditions<T> entitiesCountGreater(int count) {
        return entities -> Assertions.assertThat(entities)
                .as("Количество сущностей должно быть больше %d", count)
                .hasSizeGreaterThan(count);
    }

    /**
     * Проверка, что все сущности в списке соответствуют заданному условию.
     *
     * @param condition условие для каждой сущности
     * @param <T>       тип сущности
     */
    public static <T> Conditions<T> allEntitiesMatch(Condition<T> condition) {
        return entities -> {
            for (T entity : entities) {
                condition.check(entity);
            }
        };
    }

    /**
     * Проверка, что хотя бы одна сущность в списке соответствует условию.
     *
     * @param condition условие для проверки
     * @param <T>       тип сущности
     */
    public static <T> Conditions<T> anyEntityMatches(Condition<T> condition) {
        return entities -> {
            boolean matchFound = false;
            for (T entity : entities) {
                try {
                    condition.check(entity);
                    matchFound = true;
                    break;
                } catch (AssertionError ignored) {
                    // игнорируем — значит данная сущность не подошла
                }
            }
            Assertions.assertThat(matchFound)
                    .as("Ожидалось, что хотя бы одна сущность удовлетворяет условию")
                    .isTrue();
        };
    }

    /**
     * Проверка, что ни одна сущность в списке не соответствует условию.
     *
     * @param condition условие для проверки
     * @param <T>       тип сущности
     */
    public static <T> Conditions<T> noEntitiesMatch(Condition<T> condition) {
        return entities -> {
            for (T entity : entities) {
                try {
                    condition.check(entity);
                    Assertions.fail("Найдена сущность, соответствующая условию, хотя ожидалось отсутствие совпадений");
                } catch (AssertionError ignored) {
                    // ожидаемое поведение: условие не выполнено
                }
            }
        };
    }

    /**
     * Проверка, что все сущности имеют значение свойства, равное ожидаемому.
     *
     * @param getter        функция-геттер для свойства
     * @param expectedValue ожидаемое значение
     * @param <T>           тип сущности
     */
    public static <T> Conditions<T> entitiesPropertyValuesEqual(Function<T, ?> getter, Object expectedValue) {
        return entities -> {
            for (T entity : entities) {
                Object actualValue = getter.apply(entity);
                Assertions.assertThat(actualValue)
                        .as("Значение должно быть равно %s", expectedValue)
                        .isEqualTo(expectedValue);
            }
        };
    }
}
