package kafka.matcher.condition;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Проверка порядка слов в значении записи Kafka.
 * Условие проверяет, что слова в значении записи следуют в указанном порядке.
 */
@RequiredArgsConstructor
public class ValueWordsOrderCondition implements Condition {

    private final List<String> words;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        String value = record.value();
        String patternString = String.join(".*?", words);
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(value);

        Assertions.assertThat(matcher.find())
                .as("Значение записи не содержит слова в указанном порядке: %s", words)
                .isTrue();
    }

    @Override
    public String toString() {
        return String.format("Значение записи должно содержать слова в порядке: %s", String.join(", ", words));
    }
}
