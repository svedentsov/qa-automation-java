package rest.matcher;

import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Валидатор для проверки соответствия HTTP-ответа заданным условиям.
 * Условия предоставляются в виде объектов, реализующих интерфейс {@link Condition}.
 */
@Slf4j
@RequiredArgsConstructor
public class RestValidator {

    /**
     * HTTP-ответ для проверки.
     */
    private final Response response;

    /**
     * Проверяет, что HTTP-ответ удовлетворяет переданным условиям.
     *
     * @param conditions условия для проверки
     * @return текущий экземпляр {@link RestValidator}
     * @throws IllegalArgumentException если массив условий некорректен
     */
    public RestValidator shouldHave(Condition... conditions) {
        validateConditions(conditions);
        for (Condition condition : conditions) {
            condition.check(response);
        }
        return this;
    }

    /**
     * Проверяет, что массив условий корректен (не null, не пуст и не содержит null-элементов).
     *
     * @param conditions массив условий
     * @throws IllegalArgumentException если массив условий некорректен
     */
    private void validateConditions(Condition... conditions) {
        Objects.requireNonNull(conditions, "Условия проверки не могут быть null.");
        if (conditions.length == 0) {
            throw new IllegalArgumentException("Условия проверки не могут быть пустыми.");
        }
        for (Condition condition : conditions) {
            Objects.requireNonNull(condition, "Условие проверки не может быть null.");
        }
    }
}
