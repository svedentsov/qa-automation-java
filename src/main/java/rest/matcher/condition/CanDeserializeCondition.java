package rest.matcher.condition;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import rest.matcher.Condition;

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
        T object = response.as(clazz);
        Assertions.assertThat(object)
                .isNotNull();
    }

    @Override
    public String toString() {
        return String.format("Тело ответа может быть десериализовано в класс %s", clazz.getName());
    }
}
