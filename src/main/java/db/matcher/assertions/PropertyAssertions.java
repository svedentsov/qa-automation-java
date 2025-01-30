package db.matcher.assertions;

import db.matcher.condition.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Утилитный класс для различных проверок свойств сущности: на null, in(...) проверка, на тип, Optional, несколько свойств сразу и т.д.
 */
@UtilityClass
public class PropertyAssertions {

    /**
     * Проверяет, что свойство сущности равно ожидаемому значению.
     */
    public static <T> Condition<T> propertyEquals(Function<T, ?> getter, Object expectedValue) {
        return entity -> {
            Object actualValue = getter.apply(entity);
            Assertions.assertThat(actualValue)
                    .as("Значение должно быть равно %s", expectedValue)
                    .isEqualTo(expectedValue);
        };
    }

    /**
     * Проверяет, что свойство null.
     */
    public static <T> Condition<T> propertyIsNull(Function<T, ?> getter) {
        return entity -> {
            Object actualValue = getter.apply(entity);
            Assertions.assertThat(actualValue)
                    .as("Значение должно быть null")
                    .isNull();
        };
    }

    /**
     * Проверяет, что свойство пустое (для строки или коллекции).
     */
    public static <T> Condition<T> propertyIsEmpty(Function<T, ?> getter) {
        return entity -> {
            Object value = getter.apply(entity);
            Assertions.assertThat(value)
                    .as("Значение не должно быть null")
                    .isNotNull();

            if (value instanceof String) {
                Assertions.assertThat((String) value)
                        .as("Строка должна быть пустой")
                        .isEmpty();
            } else if (value instanceof java.util.Collection) {
                Assertions.assertThat((java.util.Collection<?>) value)
                        .as("Коллекция должна быть пустой")
                        .isEmpty();
            } else {
                throw new IllegalArgumentException("Значение не является строкой или коллекцией");
            }
        };
    }

    /**
     * Проверяет, что свойство имеет ожидаемый тип (instanceOf).
     */
    public static <T> Condition<T> propertyIsOfType(Function<T, ?> getter, Class<?> expectedType) {
        return entity -> {
            Object value = getter.apply(entity);
            Assertions.assertThat(value)
                    .as("Значение не должно быть null")
                    .isNotNull();
            Assertions.assertThat(value.getClass())
                    .as("Значение должно быть типа %s", expectedType.getName())
                    .isEqualTo(expectedType);
        };
    }

    /**
     * Проверяет, что свойство является подклассом (или реализует интерфейс) заданного типа.
     */
    public static <T> Condition<T> propertyIsAssignableFrom(Function<T, ?> getter, Class<?> expectedSuperType) {
        return entity -> {
            Object value = getter.apply(entity);
            Assertions.assertThat(value)
                    .as("Значение не должно быть null")
                    .isNotNull();
            Assertions.assertThat(expectedSuperType.isAssignableFrom(value.getClass()))
                    .as("Значение должно быть подклассом/реализовывать %s", expectedSuperType.getName())
                    .isTrue();
        };
    }

    /**
     * Проверяет, что Optional свойство присутствует (isPresent).
     */
    public static <T, R> Condition<T> optionalPropertyIsPresent(Function<T, Optional<R>> getter) {
        return entity -> {
            Optional<R> optionalValue = getter.apply(entity);
            Assertions.assertThat(optionalValue)
                    .as("Optional значение должно быть present")
                    .isPresent();
        };
    }

    /**
     * Проверяет, что все перечисленные свойства сущности (getter -> expectedValue) равны ожидаемым значениям.
     */
    public static <T> Condition<T> allPropertiesEqual(Map<Function<T, ?>, Object> expectedProperties) {
        return entity -> {
            for (Map.Entry<Function<T, ?>, Object> entry : expectedProperties.entrySet()) {
                Function<T, ?> getter = entry.getKey();
                Object expectedValue = entry.getValue();
                Object actualValue = getter.apply(entity);
                Assertions.assertThat(actualValue)
                        .as("Проверка, что значение равно %s", expectedValue)
                        .isEqualTo(expectedValue);
            }
        };
    }

    /**
     * Проверяет, что свойство входит в заданный список значений.
     */
    public static <T> Condition<T> propertyIn(Function<T, ?> getter, List<?> values) {
        return entity -> {
            Object actualValue = getter.apply(entity);
            Assertions.assertThat(values)
                    .as("Список значений не должен быть пустым")
                    .isNotEmpty();
            Assertions.assertThat(actualValue)
                    .as("Проверка, что значение входит в список %s", values)
                    .isIn(values);
        };
    }
}
