package rest.matcher.condition.body;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки, что тело ответа может быть десериализовано в указанный класс.
 *
 * @param <T> тип класса для десериализации
 */
@AllArgsConstructor
public class CanDeserializeCondition<T> implements Condition {

    private final Class<T> clazz;

    @Override
    public void check(Response response) {
        String body = response.getBody().asString();
        try {
            T object = new ObjectMapper().readValue(body, clazz);
            Assertions.assertThat(object)
                    .as("Тело ответа не может быть десериализовано в класс %s", clazz.getName())
                    .isNotNull();
        } catch (Exception e) {
            throw new AssertionError("Не удалось десериализовать тело ответа в класс " + clazz.getName(), e);
        }
    }

    @Override
    public String toString() {
        return String.format("Тело ответа может быть десериализовано в класс %s", clazz.getName());
    }
}
