package db.matcher.assertions;

import db.matcher.condition.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * Утилитный класс для проверки числовых свойств в сущности.
 */
@UtilityClass
public class NumberAssertions {

    /**
     * Проверяет, что числовое свойство больше заданного значения.
     */
    public static <T> Condition<T> propertyGreaterThan(Function<T, Number> getter, BigDecimal value) {
        return entity -> {
            Number actualValue = getter.apply(entity);
            Assertions.assertThat(actualValue)
                    .as("Значение должно быть числом (не null)")
                    .isNotNull();
            BigDecimal actualDecimal = new BigDecimal(actualValue.toString());
            Assertions.assertThat(actualDecimal)
                    .as("Значение должно быть > %s", value)
                    .isGreaterThan(value);
        };
    }

    /**
     * Проверяет, что числовое свойство меньше заданного значения.
     */
    public static <T> Condition<T> propertyLessThan(Function<T, Number> getter, BigDecimal value) {
        return entity -> {
            Number actualValue = getter.apply(entity);
            Assertions.assertThat(actualValue)
                    .as("Значение должно быть числом (не null)")
                    .isNotNull();
            BigDecimal actualDecimal = new BigDecimal(actualValue.toString());
            Assertions.assertThat(actualDecimal)
                    .as("Значение должно быть < %s", value)
                    .isLessThan(value);
        };
    }

    /**
     * Проверяет, что числовое свойство находится в заданном диапазоне [start, end].
     */
    public static <T> Condition<T> propertyBetween(Function<T, Number> getter, BigDecimal start, BigDecimal end) {
        return entity -> {
            Number actualValue = getter.apply(entity);
            Assertions.assertThat(actualValue)
                    .as("Значение должно быть числом (не null)")
                    .isNotNull();
            BigDecimal actualDecimal = new BigDecimal(actualValue.toString());
            Assertions.assertThat(actualDecimal)
                    .as("Значение должно находиться между %s и %s", start, end)
                    .isBetween(start, end);
        };
    }

    /**
     * Проверяет, что числовое свойство равно нулю.
     */
    public static <T> Condition<T> propertyIsZero(Function<T, Number> getter) {
        return entity -> {
            Number actualValue = getter.apply(entity);
            Assertions.assertThat(actualValue)
                    .as("Значение должно быть числом (не null)")
                    .isNotNull();
            BigDecimal actualDecimal = new BigDecimal(actualValue.toString());
            Assertions.assertThat(actualDecimal.compareTo(BigDecimal.ZERO) == 0)
                    .as("Значение должно быть равно 0")
                    .isTrue();
        };
    }

    /**
     * Проверяет, что числовое свойство не равно нулю.
     */
    public static <T> Condition<T> propertyIsNotZero(Function<T, Number> getter) {
        return entity -> {
            Number actualValue = getter.apply(entity);
            Assertions.assertThat(actualValue)
                    .as("Значение должно быть числом (не null)")
                    .isNotNull();
            BigDecimal actualDecimal = new BigDecimal(actualValue.toString());
            Assertions.assertThat(actualDecimal.compareTo(BigDecimal.ZERO) != 0)
                    .as("Значение не должно быть равно 0")
                    .isTrue();
        };
    }
}
