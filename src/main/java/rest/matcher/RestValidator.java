package rest.matcher;

import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rest.matcher.condition.Condition;

import java.util.Objects;

/**
 * Этот класс предоставляет методы для проверки, что ответ соответствует определённым условиям.
 * Условия могут быть заданы в виде одного или нескольких объектов, реализующих интерфейс {@link Condition}.
 */
@Slf4j
@RequiredArgsConstructor
public class RestValidator {

    /**
     * Ответ HTTP, который будет проверяться.
     */
    private final Response response;

    /**
     * Проверяет, что ответ соответствует одному или нескольким условиям.
     * Логирует информацию о проверяемом условии и выполняет проверку.
     *
     * @param conditions Условия, которые необходимо проверить
     * @return Ссылка на текущий экземпляр {@link RestValidator}
     * @throws IllegalArgumentException если conditions равны null или пусты
     */
    public RestValidator shouldHave(Condition... conditions) {
        Objects.requireNonNull(conditions, "Условия проверки не могут быть null.");
        if (conditions.length == 0) {
            throw new IllegalArgumentException("Условия проверки не могут быть пустыми.");
        }
        for (Condition condition : conditions) {
            Objects.requireNonNull(condition, "Условие проверки не может быть null.");
            log.debug("Проверка условия: {}", condition);
            condition.check(response);
        }
        return this;
    }
}
