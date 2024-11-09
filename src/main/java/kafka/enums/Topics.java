package kafka.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Перечисление, представляющее доступные Kafka топики в приложении.
 */
@Getter
@AllArgsConstructor
public enum Topics {
    TOPIC_1("topic1"),
    TOPIC_2("topic2");

    private final String name;
}
