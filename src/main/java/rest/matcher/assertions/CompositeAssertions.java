package rest.matcher.assertions;

import lombok.experimental.UtilityClass;
import rest.matcher.condition.Condition;

/**
 * Класс для композитных условий (логические операции).
 */
@UtilityClass
public final class CompositeAssertions {

    /**
     * Функциональный интерфейс для композитных условий.
     */
    @FunctionalInterface
    public interface CompositeCondition extends Condition {
    }

    /**
     * Создает условие, которое проходит только если выполняются все указанные условия.
     *
     * @param conditions условия для проверки
     * @return композитное условие, требующее выполнения всех условий
     */
    public static CompositeCondition allOf(Condition... conditions) {
        return response -> {
            for (Condition condition : conditions) {
                condition.check(response);
            }
        };
    }

    /**
     * Создает условие, которое проходит если выполняется хотя бы одно из указанных условий.
     *
     * @param conditions условия для проверки
     * @return композитное условие, требующее выполнения хотя бы одного из условий
     */
    public static CompositeCondition anyOf(Condition... conditions) {
        return response -> {
            boolean atLeastOne = false;
            AssertionError lastError = null;
            for (Condition condition : conditions) {
                try {
                    condition.check(response);
                    atLeastOne = true;
                    break;
                } catch (AssertionError e) {
                    lastError = e;
                }
            }
            if (!atLeastOne) {
                throw new AssertionError("Ожидалось, что выполнится хотя бы одно условие, но ни одно не выполнено.", lastError);
            }
        };
    }

    /**
     * Создает условие, которое проходит только если ни одно из указанных условий не выполняется.
     *
     * @param conditions условия для проверки
     * @return композитное условие, требующее невыполнения всех условий
     */
    public static CompositeCondition not(Condition... conditions) {
        return response -> {
            for (Condition condition : conditions) {
                try {
                    condition.check(response);
                    throw new AssertionError("Ожидалось, что условие не выполнится, но оно выполнено: " + condition);
                } catch (AssertionError e) {
                    // Ожидается, что условие не выполнится, продолжаем
                }
            }
        };
    }

    /**
     * Создает условие, которое проходит если выполнено не менее N из указанных условий.
     *
     * @param n          минимальное количество условий, которые должны быть выполнены
     * @param conditions условия для проверки
     * @return композитное условие, требующее выполнения не менее N условий
     */
    public static CompositeCondition nOf(int n, Condition... conditions) {
        return response -> {
            long count = 0;
            for (Condition condition : conditions) {
                try {
                    condition.check(response);
                    count++;
                    if (count >= n) break;
                } catch (AssertionError e) {
                    // Условие не выполнено, продолжаем
                }
            }
            if (count < n) {
                throw new AssertionError(String.format("Ожидалось, что выполнится не менее %d условий, но выполнено только %d.", n, count));
            }
        };
    }
}
