package kafka.matcher.assertions;

import kafka.matcher.condition.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.Arrays;

/**
 * Утилитный класс для создания композитных условий (логические операции).
 */
@UtilityClass
public class CompositeAssertions {

    /**
     * Проверяет, что все перечисленные условия выполнены (логическое И).
     *
     * @param conditions набор условий
     * @return условие, которое проходит только если все условия истинны
     */
    public static Condition and(Condition... conditions) {
        return record -> {
            for (Condition condition : conditions) {
                condition.check(record);
            }
        };
    }

    /**
     * Проверяет, что хотя бы одно из перечисленных условий выполнено (логическое ИЛИ).
     *
     * @param conditions набор условий
     * @return условие, которое проходит, если хотя бы одно условие истинно
     */
    public static Condition or(Condition... conditions) {
        return record -> {
            boolean atLeastOnePassed = Arrays.stream(conditions).anyMatch(condition -> {
                try {
                    condition.check(record);
                    return true;
                } catch (AssertionError e) {
                    return false;
                }
            });
            Assertions.assertThat(atLeastOnePassed)
                    .as("Ни одно из условий OR не выполнено")
                    .isTrue();
        };
    }

    /**
     * Проверяет, что хотя бы {@code n} из заданных условий истинны.
     *
     * @param n          минимальное число условий, которые должны быть выполнены
     * @param conditions набор условий
     * @return условие, которое проходит, если хотя бы {@code n} условий истинны
     */
    public static Condition nOf(int n, Condition... conditions) {
        return record -> {
            long successCount = Arrays.stream(conditions)
                    .filter(condition -> {
                        try {
                            condition.check(record);
                            return true;
                        } catch (AssertionError e) {
                            return false;
                        }
                    })
                    .count();

            Assertions.assertThat(successCount)
                    .as("Ожидалось, что хотя бы %d условий выполнятся, но выполнено %d", n, successCount)
                    .isGreaterThanOrEqualTo(n);
        };
    }

    /**
     * Проверяет, что ни одно из указанных условий не выполнено (логическое НЕ).
     *
     * @param conditions набор условий
     * @return условие, которое проходит только если все условия ложны
     */
    public static Condition not(Condition... conditions) {
        return record -> {
            for (Condition condition : conditions) {
                try {
                    condition.check(record);
                    Assertions.fail("Условие должно быть не выполнено, но оно выполнено: " + condition);
                } catch (AssertionError e) {
                    // Ожидаемый результат: условие не выполнено
                }
            }
        };
    }
}
