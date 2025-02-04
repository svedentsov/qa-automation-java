package db.matcher.assertions;

import db.matcher.condition.Condition;
import db.matcher.condition.Conditions;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.function.Function;

/**
 * Утилитный класс для проверки списков сущностей.
 * Позволяет проверять наличие, количество, а также соответствие условий для всех или некоторых сущностей.
 */
@UtilityClass
public class EntityAssertions {

    /**
     * Проверяет, что в списке сущностей присутствует хотя бы одна сущность.
     *
     * @param <T> тип сущности
     * @return условие проверки существования хотя бы одной сущности
     */
    public static <T> Conditions<T> exists() {
        return entities -> Assertions.assertThat(entities)
                .as("Проверка наличия хотя бы одной сущности")
                .isNotEmpty();
    }

    /**
     * Проверяет, что количество сущностей в списке равно указанному значению.
     *
     * @param count ожидаемое количество сущностей
     * @param <T>   тип сущности
     * @return условие проверки количества сущностей
     */
    public static <T> Conditions<T> countEqual(int count) {
        return entities -> Assertions.assertThat(entities)
                .as("Количество сущностей должно быть равно %d", count)
                .hasSize(count);
    }

    /**
     * Проверяет, что количество сущностей в списке больше указанного значения.
     *
     * @param count минимальное допустимое количество сущностей
     * @param <T>   тип сущности
     * @return условие проверки, что количество сущностей больше заданного
     */
    public static <T> Conditions<T> countGreater(int count) {
        return entities -> Assertions.assertThat(entities)
                .as("Количество сущностей должно быть больше %d", count)
                .hasSizeGreaterThan(count);
    }

    /**
     * Проверяет, что все сущности в списке удовлетворяют заданному условию.
     *
     * @param condition условие для проверки каждой сущности
     * @param <T>       тип сущности
     * @return условие проверки, что все сущности удовлетворяют условию
     */
    public static <T> Conditions<T> allMatch(Condition<T> condition) {
        return entities -> entities.forEach(condition::check);
    }

    /**
     * Проверяет, что хотя бы одна сущность в списке удовлетворяет заданному условию.
     *
     * @param condition условие для проверки
     * @param <T>       тип сущности
     * @return условие проверки, что хотя бы одна сущность удовлетворяет условию
     */
    public static <T> Conditions<T> anyEntityMatches(Condition<T> condition) {
        return entities -> {
            boolean matchFound = entities.stream().anyMatch(entity -> {
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
        };
    }

    /**
     * Проверяет, что ни одна сущность в списке не удовлетворяет заданному условию.
     *
     * @param condition условие для проверки
     * @param <T>       тип сущности
     * @return условие проверки, что ни одна сущность не удовлетворяет условию
     */
    public static <T> Conditions<T> noMatches(Condition<T> condition) {
        return entities -> {
            boolean anyMatch = entities.stream().anyMatch(entity -> {
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
        };
    }

    /**
     * Проверяет, что значение свойства для всех сущностей равно ожидаемому.
     *
     * @param getter        функция-геттер для извлечения свойства из сущности
     * @param expectedValue ожидаемое значение свойства
     * @param <T>           тип сущности
     * @return условие проверки, что все сущности имеют указанное значение свойства
     */
    public static <T> Conditions<T> valuesEqual(Function<T, ?> getter, Object expectedValue) {
        return entities -> entities.forEach(entity -> {
            Object actualValue = getter.apply(entity);
            Assertions.assertThat(actualValue)
                    .as("Значение должно быть равно %s", expectedValue)
                    .isEqualTo(expectedValue);
        });
    }
}
