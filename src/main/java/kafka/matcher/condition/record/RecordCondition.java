package kafka.matcher.condition.record;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

/**
 * Функциональный интерфейс для условий, проверяющих список записей целиком.
 */
@FunctionalInterface
public interface RecordCondition {
    /**
     * Проверяет условие для списка записей.
     *
     * @param records список записей для проверки
     */
    void check(List<ConsumerRecord<String, String>> records);
}
