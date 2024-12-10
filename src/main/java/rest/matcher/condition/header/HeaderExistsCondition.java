package rest.matcher.condition.header;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки наличия заголовка в ответе.
 */
@AllArgsConstructor
public class HeaderExistsCondition implements Condition {

    private final String headerName;

    @Override
    public void check(Response response) {
        boolean hasHeader = response.headers().hasHeaderWithName(headerName);
        Assertions.assertThat(hasHeader)
                .as("Заголовок %s не найден в ответе.", headerName)
                .isTrue();
    }

    @Override
    public String toString() {
        return String.format("Заголовок %s присутствует", headerName);
    }
}
