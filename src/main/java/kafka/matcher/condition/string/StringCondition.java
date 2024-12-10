package kafka.matcher.condition.string;

/**
 * Функциональный интерфейс для строковых условий.
 */
@FunctionalInterface
public interface StringCondition {
    /**
     * Проверяет строку на соответствие условию.
     *
     * @param actual строка для проверки
     */
    void check(String actual);
}
