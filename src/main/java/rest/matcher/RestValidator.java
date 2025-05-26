package rest.matcher;

import io.restassured.response.Response;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Валидатор HTTP-ответа REST-запроса.
 * Предоставляет удобный fluent-интерфейс для последовательной валидации ответа
 * по набору пользовательских условий {@link Condition}.
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RestValidator {

    /**
     * HTTP-ответ, который будет проверяться условиями.
     */
    private final Response response;

    /**
     * Фабричный метод для создания валидатора по заданному ответу.
     *
     * @param response HTTP-ответ, не должен быть null
     * @return новый экземпляр {@link RestValidator}
     * @throws NullPointerException если {@code response} равен null
     */
    public static RestValidator forResponse(@Nonnull Response response) {
        Objects.requireNonNull(response, "HTTP-ответ (response) не может быть null");
        return new RestValidator(response);
    }

    /**
     * Проверяет, что HTTP-ответ удовлетворяет всем переданным условиям.
     * При ошибке валидации конкретное условие само выбросит исключение, описывающее, какое именно условие не выполнено.
     *
     * @param conditions массив условий, каждое из которых не должно быть null
     * @return текущий экземпляр {@link RestValidator} для цепочного вызова
     * @throws IllegalArgumentException если массив условий null, пуст или содержит null-элементы
     */
    public RestValidator shouldHave(@Nonnull Condition... conditions) {
        log.debug("Начало валидации ответа по {} условиям: {}", conditions.length, Arrays.toString(conditions));
        validateConditions(conditions);
        Arrays.stream(conditions).forEach(condition -> {
            log.debug("Применяем условие: {}", condition);
            condition.check(response);
            log.debug("Условие {} выполнено успешно", condition);
        });
        log.debug("Валидация ответа по всем условиям завершена успешно");
        return this;
    }

    /**
     * Проверяет, что массив условий корректен (не null, не пуст и не содержит null-элементов).
     *
     * @param conditions массив условий
     * @throws IllegalArgumentException если массив условий некорректен
     */
    private void validateConditions(Condition... conditions) {
        if (conditions == null) {
            throw new IllegalArgumentException("Массив условий не может быть null.");
        }
        if (conditions.length == 0) {
            throw new IllegalArgumentException("Необходимо передать хотя бы одно условие.");
        }
        IntStream.range(0, conditions.length).filter(i -> conditions[i] == null).forEach(i -> {
            throw new IllegalArgumentException(String.format("Условие в позиции %d не может быть null.", i));
        });
    }
}
