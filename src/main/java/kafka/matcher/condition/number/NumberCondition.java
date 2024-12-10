package kafka.matcher.condition.number;

/**
 * Функциональный интерфейс для числовых условий.
 *
 * @param <T> тип числа (например, Integer, Long)
 */
@FunctionalInterface
public interface NumberCondition<T extends Number & Comparable<T>> {

    /**
     * Проверяет число на соответствие условию.
     *
     * @param actual фактическое значение числа
     */
    void check(T actual);
}
