package rest.matcher.assertions;

import lombok.experimental.UtilityClass;
import rest.matcher.Condition;

import java.util.Objects;

/**
 * Класс для композитных условий (логические операции).
 */
@UtilityClass
public class CompositeAssertions {

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
     * @throws IllegalArgumentException если conditions содержит null или пусто
     */
    public static CompositeCondition allOf(Condition... conditions) {
        Objects.requireNonNull(conditions, "conditions не могут быть null");
        if (conditions.length == 0) {
            throw new IllegalArgumentException("conditions не могут быть пустыми");
        }
        for (Condition condition : conditions) {
            Objects.requireNonNull(condition, "условие не может быть null");
        }
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
     * @throws IllegalArgumentException если conditions содержит null или пусто
     */
    public static CompositeCondition anyOf(Condition... conditions) {
        Objects.requireNonNull(conditions, "conditions не могут быть null");
        if (conditions.length == 0) {
            throw new IllegalArgumentException("conditions не могут быть пустыми");
        }
        for (Condition condition : conditions) {
            Objects.requireNonNull(condition, "условие не может быть null");
        }
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
                StringBuilder errorMsg = new StringBuilder("Ожидалось, что выполнится хотя бы одно условие, но ни одно не выполнено.");
                if (lastError != null) {
                    errorMsg.append(" Последняя ошибка: ").append(lastError.getMessage());
                }
                throw new AssertionError(errorMsg.toString(), lastError);
            }
        };
    }

    /**
     * Создает условие, которое проходит только если ни одно из указанных условий не выполняется.
     *
     * @param conditions условия для проверки
     * @return композитное условие, требующее невыполнения всех условий
     * @throws IllegalArgumentException если conditions содержит null или пусто
     */
    public static CompositeCondition not(Condition... conditions) {
        Objects.requireNonNull(conditions, "conditions не могут быть null");
        if (conditions.length == 0) {
            throw new IllegalArgumentException("conditions не могут быть пустыми");
        }
        for (Condition condition : conditions) {
            Objects.requireNonNull(condition, "условие не может быть null");
        }
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
     * @throws IllegalArgumentException если n <=0, или conditions содержит null, или n > conditions.length
     */
    public static CompositeCondition nOf(int n, Condition... conditions) {
        Objects.requireNonNull(conditions, "conditions не могут быть null");
        if (n <= 0) {
            throw new IllegalArgumentException("n должно быть больше 0");
        }
        if (conditions.length < n) {
            throw new IllegalArgumentException("Количество условий не может быть меньше n");
        }
        for (Condition condition : conditions) {
            Objects.requireNonNull(condition, "условие не может быть null");
        }
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
