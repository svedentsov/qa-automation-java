package com.svedentsov.matcher;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import io.restassured.response.Response;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class JsonReader {

    /**
     * Предварительно настроенная конфигурация JsonPath для переиспользования.
     * Option.DEFAULT_PATH_LEAF_TO_NULL гарантирует, что для несуществующих путей
     * будет возвращаться null вместо выбрасывания исключения.
     */
    private static final Configuration JSON_PATH_CONFIG = Configuration.defaultConfiguration()
            .addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

    /**
     * Извлекает значение из HTTP-ответа по JSONPath.
     * Является удобной обёрткой для {@link #extractValue(String, String, Class)}.
     *
     * @param response     HTTP-ответ
     * @param jsonPath     путь JSONPath
     * @param expectedType ожидаемый класс значения
     * @param <T>          тип значения
     * @return извлечённое значение или null
     * @throws IllegalArgumentException если какой-либо из аргументов равен null (покрывается Lombok @NonNull)
     * @throws AssertionError           если фактический тип значения не соответствует ожидаемому
     */
    public static <T> T extractValue(@NonNull Response response, @NonNull String jsonPath, @NonNull Class<T> expectedType) {
        log.debug("Извлечение значения из ответа по пути '{}' с ожидаемым типом '{}'", jsonPath, expectedType.getSimpleName());
        return extractValue(response.asString(), jsonPath, expectedType);
    }

    /**
     * Извлекает значение из JSON-строки по JSONPath и проверяет его тип.
     *
     * @param json         исходная JSON-строка
     * @param jsonPath     путь JSONPath
     * @param expectedType ожидаемый класс значения
     * @param <T>          тип значения
     * @return извлечённое и приведённое к {@code expectedType} значение, или null, если значение отсутствует
     * @throws IllegalArgumentException если какой-либо из аргументов равен null (покрывается Lombok @NonNull)
     * @throws AssertionError           если фактический тип значения не соответствует ожидаемому
     */
    public static <T> T extractValue(@NonNull String json, @NonNull String jsonPath, @NonNull Class<T> expectedType) {
        log.debug("Чтение JSON-значения по пути '{}' с ожидаемым типом '{}'", jsonPath, expectedType.getSimpleName());
        Object value = JsonPath.using(JSON_PATH_CONFIG).parse(json).read(jsonPath);

        if (value == null) {
            log.debug("Значение по пути '{}' не найдено или равно null", jsonPath);
            return null;
        }

        if (!expectedType.isInstance(value)) {
            String errorMessage = String.format(
                    "Тип значения по пути '%s' не соответствует ожидаемому. Ожидали: %s, получили: %s (%s)",
                    jsonPath,
                    expectedType.getSimpleName(),
                    value,
                    value.getClass().getSimpleName());
            log.error(errorMessage);
            throw new AssertionError(errorMessage);
        }

        log.debug("Успешно извлечено значение '{}' типа '{}' по пути '{}'", value, expectedType.getSimpleName(), jsonPath);
        return expectedType.cast(value);
    }
}
