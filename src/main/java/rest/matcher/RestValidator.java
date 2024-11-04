package rest.matcher;

import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

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
     * Проверяет, что ответ соответствует одному условию.
     * Логирует информацию о проверяемом условии и выполняет проверку.
     *
     * @param condition Условие, которое необходимо проверить
     * @return Ссылка на текущий экземпляр {@link RestValidator}
     */
    public RestValidator shouldHave(Condition condition) {
        log.debug("Проверка условия: {}", condition.toString());
        condition.check(response);
        return this;
    }

    /**
     * Проверяет, что ответ соответствует нескольким условиям.
     * Логирует информацию о каждом проверяемом условии и выполняет проверки.
     *
     * @param conditions Массив условий, которые необходимо проверить
     * @return Ссылка на текущий экземпляр {@link RestValidator}
     */
    public RestValidator shouldHave(Condition... conditions) {
        List<Condition> conds = Arrays.asList(conditions);
        conds.forEach(condition -> {
            log.debug("Проверка условия: {}", condition.toString());
            condition.check(response);
        });
        return this;
    }
}
