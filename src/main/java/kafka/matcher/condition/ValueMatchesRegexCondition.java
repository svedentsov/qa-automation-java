package kafka.matcher.condition;

import kafka.matcher.Condition;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.regex.Pattern;

/**
 * Проверка, что значение записи Kafka соответствует заданному регулярному выражению.
 */
@RequiredArgsConstructor
public class ValueMatchesRegexCondition implements Condition {

    private final String regex;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        Pattern pattern = Pattern.compile(regex);
        Assertions.assertThat(record.value())
                .as("Проверка, что значение записи соответствует регулярному выражению: '%s'", regex)
                .matches(pattern);
    }

    @Override
    public String toString() {
        return String.format("Условие: значение записи должно соответствовать регулярному выражению '%s'", regex);
    }
}
