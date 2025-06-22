package com.svedentsov.kafka.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Перечисление, представляющее доступные Kafka топики в приложении.
 * Используется для централизованного управления именами топиков.
 */
@Getter
@AllArgsConstructor
public enum Topic {
    /**
     * Первый тестовый топик.
     */
    TOPIC_1("topic1"),
    /**
     * Второй тестовый топик.
     */
    TOPIC_2("topic2");
    /**
     * Фактическое строковое имя топика в Kafka.
     */
    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
