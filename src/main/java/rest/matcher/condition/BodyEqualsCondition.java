package rest.matcher.condition;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import rest.matcher.Condition;

/**
 * Условие для проверки, что тело ответа равно ожидаемому объекту.
 *
 * @param <T> тип ожидаемого объекта
 */
@AllArgsConstructor
public class BodyEqualsCondition<T> implements Condition {

    private final T expectedObject;

    @Override
    public void check(Response response) {
        T actualObject = response.as((Class<T>) expectedObject.getClass());
        Assertions.assertThat(actualObject)
                .usingRecursiveComparison()
                .isEqualTo(expectedObject);
    }

    @Override
    public String toString() {
        return String.format("Тело ответа равно ожидаемому объекту '%s'", expectedObject);
    }
}
