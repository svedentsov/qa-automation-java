package kafka.matcher.condition.timestamp;

import java.time.Instant;

/**
 * Функциональный интерфейс для условий, проверяющих временную метку.
 */
@FunctionalInterface
public interface TimestampCondition {
    /**
     * Проверяет временную метку.
     *
     * @param actual временная метка записи
     */
    void check(Instant actual);
}
