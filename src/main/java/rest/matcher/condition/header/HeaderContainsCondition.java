package rest.matcher.condition.header;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import rest.matcher.condition.Condition;

/**
 * Класс, представляющий условие проверки наличия заголовка в ответе.
 */
@AllArgsConstructor
public class HeaderContainsCondition implements Condition {

    private final String expectedHeaderName;

    @Override
    public void check(Response response) {
        boolean hasHeader = response.headers().hasHeaderWithName(expectedHeaderName);
        Assertions.assertThat(hasHeader)
                .as("Заголовок с именем %s не найден.", expectedHeaderName)
                .isTrue();
    }

    @Override
    public String toString() {
        return String.format("Заголовок %s должен присутствовать.", expectedHeaderName);
    }
}
