package db.matcher.assertions;

import db.matcher.condition.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Утилитный класс для проверки различных свойств сущности: равенство, null, принадлежность к списку, тип и т.д.
 */
@UtilityClass
public class PropertyAssertions {

    /**
     * Функциональный интерфейс для проверки отдельного свойства.
     *
     * @param <V> тип проверяемого свойства
     */
    @FunctionalInterface
    public interface PropertyCondition<V> {
        /**
         * Проверяет значение свойства.
         *
         * @param value значение свойства для проверки
         */
        void check(V value);
    }

    /**
     * Возвращает условие, проверяющее, что значение свойства равно ожидаемому.
     *
     * @param expectedValue ожидаемое значение свойства
     * @param <T>           тип проверяемого свойства
     * @return условие проверки равенства
     */
    public static <T> Condition<T> equalsTo(Object expectedValue) {
        return value -> Assertions.assertThat(value)
                .as("Значение должно быть равно %s", expectedValue)
                .isEqualTo(expectedValue);
    }

    /**
     * Возвращает условие, проверяющее, что значение свойства равно null.
     *
     * @param <T> тип проверяемого свойства
     * @return условие проверки, что значение равно null
     */
    public static <T> Condition<T> isNull() {
        return value -> Assertions.assertThat(value)
                .as("Значение должно быть null")
                .isNull();
    }

    /**
     * Возвращает условие, проверяющее, что значение свойства (строка или коллекция) пустое.
     *
     * @param <T> тип проверяемого свойства
     * @return условие проверки пустоты значения
     * @throws IllegalArgumentException если значение не является строкой или коллекцией
     */
    public static <T> Condition<T> propertyIsEmpty() {
        return value -> {
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
     * Возвращает условие, проверяющее, что значение имеет точный тип {@code expectedType}.
     *
     * @param expectedType ожидаемый тип значения
     * @param <T>          тип проверяемого свойства
     * @return условие проверки типа
     */
    public static <T> Condition<T> isOfType(Class<?> expectedType) {
        return value -> Assertions.assertThat(value.getClass())
                .as("Значение должно быть типа %s", expectedType.getName())
                .isEqualTo(expectedType);
    }

    /**
     * Возвращает условие, проверяющее, что значение является подклассом или реализует указанный тип.
     *
     * @param expectedSuperType ожидаемый суперкласс или интерфейс
     * @param <T>               тип проверяемого свойства
     * @return условие проверки принадлежности к типу
     */
    public static <T> Condition<T> isAssignableFrom(Class<?> expectedSuperType) {
        return value -> Assertions.assertThat(expectedSuperType.isAssignableFrom(value.getClass()))
                .as("Значение должно быть подклассом/реализовывать %s", expectedSuperType.getName())
                .isTrue();
    }

    /**
     * Возвращает условие, проверяющее, что все указанные свойства сущности равны ожидаемым значениям.
     *
     * @param expectedProperties карта, где ключ – функция-геттер свойства, а значение – ожидаемое значение
     * @param <T>                тип сущности
     * @return условие проверки нескольких свойств
     */
    public static <T> Condition<T> allPropertiesEqual(Map<Function<T, ?>, Object> expectedProperties) {
        return value -> {
            for (Map.Entry<Function<T, ?>, Object> entry : expectedProperties.entrySet()) {
                Function<T, ?> getter = entry.getKey();
                Object expectedValue = entry.getValue();
                Object actualValue = getter.apply(value);
                Assertions.assertThat(actualValue)
                        .as("Проверка, что значение равно %s", expectedValue)
                        .isEqualTo(expectedValue);
            }
        };
    }

    /**
     * Возвращает условие, проверяющее, что значение входит в заданный список.
     *
     * @param values список допустимых значений
     * @param <T>    тип проверяемого свойства
     * @return условие проверки принадлежности значения списку
     */
    public static <T> Condition<T> in(List<?> values) {
        return value -> Assertions.assertThat(value)
                .as("Проверка, что значение входит в список %s", values)
                .isIn(values);
    }
}
