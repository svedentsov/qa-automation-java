package kafka.matcher.condition;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

/**
 * Проверка, что запись Kafka принадлежит указанному топику.
 */
@RequiredArgsConstructor
public class TopicEqualCondition implements Condition {

    private final String expectedTopic;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        Assertions.assertThat(record.topic())
                .as("Проверка, что запись принадлежит топику '%s'", expectedTopic)
                .isEqualTo(expectedTopic);
    }

    @Override
    public String toString() {
        return String.format("Условие: запись должна принадлежать топику '%s'", expectedTopic);
    }
}
