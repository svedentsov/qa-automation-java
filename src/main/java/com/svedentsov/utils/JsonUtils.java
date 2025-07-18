package com.svedentsov.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * Утилитарный класс для операций сериализации и десериализации JSON, основанный на Jackson.
 * Класс полностью потокобезопасен и сконфигурирован для решения большинства типовых задач.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonUtils {

    private static final ObjectMapper MAPPER = createDefaultMapper();
    private static final ObjectWriter WRITER = MAPPER.writer();
    private static final ObjectWriter PRETTY_WRITER = MAPPER.writerWithDefaultPrettyPrinter();

    /**
     * Создает и конфигурирует стандартный экземпляр ObjectMapper.
     *
     * @return настроенный {@link ObjectMapper}
     */
    private static ObjectMapper createDefaultMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Поддержка современных типов даты и времени (LocalDate, LocalDateTime и т.д.)
        mapper.registerModule(new JavaTimeModule());
        // Даты сериализуются в стандартном формате ISO-8601 (например, "2025-07-18T22:09:18.123Z")
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // При десериализации игнорируются неизвестные поля в JSON, что делает клиент более устойчивым к изменениям API
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }

    /**
     * Сериализует объект в JSON-строку.
     *
     * @param object объект для сериализации
     * @return JSON-представление объекта
     * @throws JsonOperationException в случае ошибки сериализации
     */
    public static String toJson(final Object object) {
        try {
            return WRITER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JsonOperationException("Ошибка сериализации объекта в JSON", e);
        }
    }

    /**
     * Сериализует объект в "красивую" JSON-строку с отступами.
     *
     * @param object объект для сериализации
     * @return отформатированное JSON-представление объекта
     * @throws JsonOperationException в случае ошибки сериализации
     */
    public static String toPrettyJson(final Object object) {
        try {
            return PRETTY_WRITER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JsonOperationException("Ошибка сериализации объекта в красивый JSON", e);
        }
    }

    /**
     * Десериализует JSON-строку в объект указанного класса.
     *
     * @param json  JSON-строка
     * @param clazz класс целевого объекта
     * @param <T>   тип целевого объекта
     * @return экземпляр объекта типа T
     * @throws JsonOperationException в случае ошибки десериализации
     */
    public static <T> T fromJson(final String json, final Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new JsonOperationException("Ошибка десериализации JSON в " + clazz.getSimpleName(), e);
        }
    }

    /**
     * Десериализует JSON-строку в объект сложного generic-типа (например, List<User>).
     *
     * @param json          JSON-строка
     * @param typeReference объект {@link TypeReference}, описывающий целевой тип
     * @param <T>           целевой тип
     * @return экземпляр объекта типа T
     * @throws JsonOperationException в случае ошибки десериализации
     */
    public static <T> T fromJson(final String json, final TypeReference<T> typeReference) {
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new JsonOperationException("Ошибка десериализации JSON в " + typeReference.getType(), e);
        }
    }

    /**
     * Десериализует JSON-строку в древовидную модель {@link JsonNode}.
     *
     * @param json JSON-строка
     * @return корневой узел {@link JsonNode}
     * @throws JsonOperationException в случае ошибки парсинга
     */
    public static JsonNode fromJsonToNode(final String json) {
        try {
            return MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            throw new JsonOperationException("Ошибка парсинга JSON в JsonNode", e);
        }
    }

    /**
     * Конвертирует один объект в другой совместимый тип (например, POJO в Map).
     *
     * @param fromValue объект-источник
     * @param toValueTypeReference тип результата
     * @param <T> тип результата
     * @return сконвертированный объект
     */
    public static <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeReference) {
        return MAPPER.convertValue(fromValue, toValueTypeReference);
    }

    /**
     * Обновляет существующий объект значениями из JSON-строки.
     *
     * @param objectToUpdate объект для обновления
     * @param json JSON-строка с новыми значениями
     * @param <T> тип объекта
     * @return обновленный объект
     * @throws JsonOperationException в случае ошибки
     */
    public static <T> T updateValue(T objectToUpdate, String json) {
        try {
            return MAPPER.readerForUpdating(objectToUpdate).readValue(json);
        } catch (IOException e) {
            throw new JsonOperationException("Ошибка обновления объекта из JSON", e);
        }
    }

    /**
     * Исключение для инкапсуляции ошибок при работе с JSON.
     */
    public static class JsonOperationException extends RuntimeException {
        public JsonOperationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
