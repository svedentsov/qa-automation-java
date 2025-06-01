package com.svedentsov.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svedentsov.kafka.exception.JsonDeserializationException;
import com.svedentsov.kafka.exception.JsonSerializationException;
import lombok.extern.slf4j.Slf4j;

/**
 * Утилитарный класс JsonUtils предоставляет методы для сериализации объектов в JSON и десериализации из JSON.
 */
@Slf4j
public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Преобразует объект в JSON строку.
     *
     * @param obj объект для сериализации в JSON
     * @return JSON строка, представляющая указанный объект
     * @throws JsonSerializationException если возникает ошибка в процессе сериализации
     */
    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации объекта {}: {}", obj, e.getMessage());
            throw new JsonSerializationException("Ошибка при сериализации объекта", e);
        }
    }

    /**
     * Десериализует JSON строку в объект заданного класса.
     *
     * @param json  JSON строка для десериализации
     * @param clazz класс, в который необходимо преобразовать JSON
     * @param <T>   тип объекта
     * @return объект заданного класса, созданный из JSON строки
     * @throws JsonDeserializationException если возникает ошибка в процессе десериализации
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Ошибка десериализации JSON строки {}: {}", json, e.getMessage());
            throw new JsonDeserializationException("Ошибка при десериализации JSON строки", e);
        }
    }

    /**
     * Десериализует JSON строку в объект заданного типа с использованием TypeReference.
     *
     * @param json    JSON строка для десериализации
     * @param typeRef TypeReference, определяющий целевой тип
     * @param <T>     тип объекта
     * @return объект заданного типа, созданный из JSON строки
     * @throws JsonDeserializationException если возникает ошибка в процессе десериализации
     */
    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            log.error("Ошибка десериализации JSON строки с TypeReference {}: {}", json, e.getMessage());
            throw new JsonDeserializationException("Ошибка при десериализации JSON строки с TypeReference", e);
        }
    }
}
