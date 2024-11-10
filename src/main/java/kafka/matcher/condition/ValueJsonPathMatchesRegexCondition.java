package kafka.matcher.condition;

import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;

import java.util.regex.Pattern;

/**
 * Проверка, что значение записи соответствует регулярному выражению по выражению JSONPath.
 */
@RequiredArgsConstructor
public class ValueJsonPathMatchesRegexCondition implements Condition {

    private final String jsonPath;
    private final String regex;

    @Override
    public void check(ConsumerRecord<String, String> record) {
        String value = record.value();
        Object json = JsonPath.parse(value).read(jsonPath);
        Pattern pattern = Pattern.compile(regex);
        Assertions.assertThat(pattern.matcher(json.toString()).matches())
                .as("Значение по JSONPath '%s' не соответствует регулярному выражению '%s'", jsonPath, regex)
                .isTrue();
    }

    @Override
    public String toString() {
        return String.format("Значение по JSONPath %s должно соответствовать регулярному выражению: %s", jsonPath, regex);
    }
}
