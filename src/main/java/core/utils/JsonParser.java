package core.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Утилитарный класс для работы с JSON-парсингом и сериализацией.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonParser {

    private static final String EQUALS_UTF_CODE = "\\u003d";
    private static Gson gsonWithNulls = new GsonBuilder().serializeNulls().create();
    private static Gson gson = new GsonBuilder().create();
    private static Gson prettyPrintingGson = new GsonBuilder().setPrettyPrinting().create();
    private static Gson printingFullGson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    private static com.google.gson.JsonParser jsonParser = new com.google.gson.JsonParser();
    private static ObjectMapper OM = new ObjectMapper();

    /**
     * Преобразует объект в JSON-строку.
     *
     * @param obj Объект для сериализации в JSON.
     * @return JSON-строка.
     */
    public static String toJson(Object obj) {
        return toJson(obj, true);
    }

    private static String toJson(Object obj, boolean serializeNulls) {
        String json;
        if (serializeNulls) {
            json = gsonWithNulls.toJson(obj);
        } else {
            json = gson.toJson(obj);
        }
        /*
         * Escaping equals sign is not necessary
         */
        return json.replace(EQUALS_UTF_CODE, StringUtil.EQUAL_SIGN);
    }

    /**
     * Преобразует объект в красиво отформатированную JSON-строку.
     *
     * @param obj Объект для сериализации в JSON.
     * @return Красиво отформатированная JSON-строка.
     */
    public static String toPrintingFullJson(Object obj) {
        return printingFullGson.toJson(obj);
    }

    /**
     * Преобразует объект в красиво отформатированную JSON-строку.
     *
     * @param obj Объект для сериализации в JSON.
     * @return Красиво отформатированная JSON-строка.
     */
    public static String toPrettyPrintingJson(Object obj) {
        return prettyPrintingGson.toJson(obj);
    }

    /**
     * Парсит JSON-строку в объект указанного типа.
     *
     * @param json JSON-строка для десериализации.
     * @param type Тип объекта для десериализации.
     * @param <T>  Тип объекта.
     * @return Десериализованный объект.
     * @deprecated Используйте метод {@link #fromJsonViaJackson(String, Class)} вместо этого.
     */
    @Deprecated
    public static <T> T fromJson(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    /**
     * Преобразует объект в POJO (Plain Old Java Object) указанного типа.
     *
     * @param object Исходный объект.
     * @param clazz  Тип объекта для десериализации.
     * @param <T>    Тип объекта.
     * @return Десериализованный объект.
     * @deprecated Используйте метод {@link #fromJsonViaJackson(String, Class)} вместо этого.
     */
    @Deprecated
    public static <T> T toPojo(Object object, Class<T> clazz) {
        JsonElement jsonElement = gson.toJsonTree(object);
        return gson.fromJson(jsonElement, clazz);
    }

    /**
     * Преобразует объект в Map.
     *
     * @param object Объект для сериализации в JSON и десериализации в Map.
     * @return Map, представляющая объект в виде ключ-значение.
     */
    public static Map toMap(Object object) {
        String json = toJson(object);
        return gson.fromJson(json, Map.class);
    }

    /**
     * Парсит JSON-строку в объект указанного типа.
     *
     * @param json JSON-строка для десериализации.
     * @param type Тип объекта для десериализации.
     * @param <T>  Тип объекта.
     * @return Десериализованный объект.
     * @deprecated Используйте метод {@link #fromJsonViaJackson(String, Class)} вместо этого.
     */
    @Deprecated
    public static <T> T fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }

    /**
     * Проверяет, является ли переданная строка валидной JSON-строкой.
     *
     * @param json Строка для проверки.
     * @return {@code true}, если строка является валидной JSON, в противном случае {@code false}.
     */
    public static boolean isJson(String json) {
        try {
            gson.fromJson(json, Object.class);
            return true;
        } catch (com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }

    /**
     * Получает объект типа {@link JsonObject} из объекта.
     *
     * @param src Исходный объект.
     * @return Объект типа {@link JsonObject}.
     */
    public static JsonObject getJsonObject(Object src) {
        return (JsonObject) getJsonElement(gson.toJson(src));
    }

    /**
     * Получает объект типа {@link JsonElement} из JSON-строки.
     *
     * @param json JSON-строка.
     * @return Объект типа {@link JsonElement}.
     */
    public static JsonElement getJsonElement(String json) {
        return jsonParser.parse(json);
    }

    /**
     * Преобразует объект в JSON-строку с использованием ObjectMapper из библиотеки Jackson.
     *
     * @param object Объект для сериализации в JSON.
     * @return JSON-строка.
     * @throws RuntimeException если произошла ошибка во время сериализации.
     */
    @SneakyThrows
    public static String toJsonViaJackson(Object object) {
        return toJsonViaJackson(object, false);
    }

    /**
     * Преобразует объект в JSON-строку с использованием ObjectMapper из библиотеки Jackson.
     *
     * @param object     Объект для сериализации в JSON.
     * @param ignoreNull Флаг для игнорирования null-значений.
     * @return JSON-строка.
     * @throws RuntimeException если произошла ошибка во время сериализации.
     */
    @SneakyThrows
    public static String toJsonViaJackson(Object object, boolean ignoreNull) {
        if (ignoreNull) {
            OM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        return OM.writeValueAsString(object);
    }

    /**
     * Парсит JSON-строку в объект указанного типа с использованием ObjectMapper из библиотеки Jackson.
     *
     * @param json JSON-строка для десериализации.
     * @param type Тип объекта для десериализации.
     * @param <T>  Тип объекта.
     * @return Десериализованный объект.
     * @throws RuntimeException если произошла ошибка во время десериализации.
     */
    @SneakyThrows
    public static <T> T fromJsonViaJackson(String json, Class<T> type) {
        return OM.readValue(json, type);
    }
}
