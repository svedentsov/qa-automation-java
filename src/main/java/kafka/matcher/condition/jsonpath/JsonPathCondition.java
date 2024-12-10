package kafka.matcher.condition.jsonpath;

/**
 * Функциональный интерфейс для условий, применяемых к значению, извлеченному по JsonPath.
 */
@FunctionalInterface
public interface JsonPathCondition {
    /**
     * Проверяет значение, полученное по заданному JsonPath.
     *
     * @param value    значение, извлеченное из JSON по пути jsonPath
     * @param jsonPath путь JsonPath
     */
    void check(Object value, String jsonPath);
}
