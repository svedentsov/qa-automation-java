package kafka.matcher.assertions;

import kafka.matcher.condition.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.Arrays;

/**
 * Утилитный класс для создания составных (композитных) условий проверки.
 */
@UtilityClass
public class CompositeAssertions {

    /**
     * Проверяет, что все перечисленные условия выполнены (логическое И).
     *
     * @param conditions набор условий
     * @return составное условие, которое проходит только если все условия истинны
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
     * @return составное условие, которое проходит если хотя бы одно условие истинно
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
     * Инвертирует результаты указанных условий (логическое НЕ).
     *
     * @param conditions набор условий
     * @return условие, которое проходит только если все указанные условия не выполнены
     */
    public static Condition not(Condition... conditions) {
        return record -> {
            for (Condition condition : conditions) {
                try {
                    condition.check(record);
                    Assertions.fail("Условие должно быть не выполнено, но выполнено: " + condition);
                } catch (AssertionError e) {
                    // Ожидаемый результат
                }
            }
        };
    }

    /**
     * Проверяет, что хотя бы n из перечисленных условий истинны.
     *
     * @param n          минимальное число условий, которые должны выполниться
     * @param conditions набор условий
     * @return условие, которое проходит если хотя бы n условий истинны
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
                    .as("Ожидалось, что хотя бы %d условий выполнится, но выполнено %d", n, successCount)
                    .isGreaterThanOrEqualTo(n);
        };
    }
}
