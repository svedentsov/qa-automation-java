package kafka.matcher.assertions;

import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Утилитный класс для создания условий, применяемых к значениям JsonPath.
 */
@UtilityClass
public final class JsonPathConditions {

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

    /**
     * Проверяет, что значение по JsonPath является строкой.
     */
    public static JsonPathCondition isString() {
        return (val, jsonPath) -> {
            Assertions.assertThat(val)
                    .as("Значение по JsonPath %s должно быть строкой", jsonPath)
                    .isInstanceOf(String.class);
        };
    }

    /**
     * Проверяет, что значение по JsonPath является числом.
     */
    public static JsonPathCondition isNumber() {
        return (val, jsonPath) -> {
            Assertions.assertThat(val)
                    .as("Значение по JsonPath %s должно быть числом", jsonPath)
                    .isInstanceOf(Number.class);
        };
    }

    /**
     * Проверяет, что значение по JsonPath является булевым.
     */
    public static JsonPathCondition isBoolean() {
        return (val, jsonPath) -> {
            Assertions.assertThat(val)
                    .as("Значение по JsonPath %s должно быть boolean", jsonPath)
                    .isInstanceOf(Boolean.class);
        };
    }

    /**
     * Проверяет, что значение по JsonPath является массивом.
     */
    public static JsonPathCondition isArray() {
        return (val, jsonPath) -> {
            Assertions.assertThat(val)
                    .as("Значение по JsonPath %s должно быть массивом", jsonPath)
                    .isInstanceOf(List.class);
        };
    }

    /**
     * Проверяет, что значение по JsonPath является массивом заданного размера.
     */
    public static JsonPathCondition arraySize(int expectedSize) {
        return (val, jsonPath) -> {
            Assertions.assertThat(val)
                    .as("Значение по JsonPath %s должно быть массивом", jsonPath)
                    .isInstanceOf(List.class);
            List<?> arr = (List<?>) val;
            Assertions.assertThat(arr.size())
                    .as("Размер массива по JsonPath %s должен быть %d", jsonPath, expectedSize)
                    .isEqualTo(expectedSize);
        };
    }

    /**
     * Проверяет, что число по JsonPath больше заданного порога.
     */
    public static JsonPathCondition numberGreater(Number threshold) {
        return (val, jsonPath) -> {
            Assertions.assertThat(val)
                    .as("Значение по JsonPath %s должно быть числом", jsonPath)
                    .isInstanceOf(Number.class);
            Number number = (Number) val;
            Assertions.assertThat(number.doubleValue())
                    .as("Число по JsonPath %s должно быть > %s", jsonPath, threshold)
                    .isGreaterThan(threshold.doubleValue());
        };
    }

    /**
     * Проверяет, что число по JsonPath меньше заданного порога.
     */
    public static JsonPathCondition numberLess(Number threshold) {
        return (val, jsonPath) -> {
            Assertions.assertThat(val)
                    .as("Значение по JsonPath %s должно быть числом", jsonPath)
                    .isInstanceOf(Number.class);
            Number number = (Number) val;
            Assertions.assertThat(number.doubleValue())
                    .as("Число по JsonPath %s должно быть < %s", jsonPath, threshold)
                    .isLessThan(threshold.doubleValue());
        };
    }

    /**
     * Проверяет, что значение по JsonPath соответствует заданному регулярному выражению.
     */
    public static JsonPathCondition matchesRegexJson(String regex) {
        return (val, jsonPath) -> {
            Pattern p = Pattern.compile(regex);
            Assertions.assertThat(p.matcher(val.toString()).matches())
                    .as("Значение по JsonPath %s должно соответствовать %s", jsonPath, regex)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что значение по JsonPath содержит указанный текст.
     */
    public static JsonPathCondition containsJson(String text) {
        return (val, jsonPath) -> {
            Assertions.assertThat(val.toString())
                    .as("Значение по JsonPath %s должно содержать %s", jsonPath, text)
                    .contains(text);
        };
    }

    /**
     * Проверяет, что значение по JsonPath не равно null.
     */
    public static JsonPathCondition jsonPathExists() {
        return (val, jsonPath) -> {
            Assertions.assertThat(val)
                    .as("По JsonPath %s не должно быть null", jsonPath)
                    .isNotNull();
        };
    }

    /**
     * Проверяет, что значение по JsonPath не пусто.
     * Применимо к строкам и спискам.
     */
    public static JsonPathCondition jsonPathNotEmpty() {
        return (val, jsonPath) -> {
            Assertions.assertThat(val)
                    .as("Значение по JsonPath %s не должно быть пустым или null", jsonPath)
                    .isNotNull();
            if (val instanceof String) {
                Assertions.assertThat((String) val)
                        .as("Строка по JsonPath %s не должна быть пустой", jsonPath)
                        .isNotEmpty();
            } else if (val instanceof List) {
                Assertions.assertThat((List<?>) val)
                        .as("Список по JsonPath %s не должен быть пустым", jsonPath)
                        .isNotEmpty();
            }
        };
    }

    /**
     * Проверяет, что массив по JsonPath содержит уникальные элементы.
     */
    public static JsonPathCondition isJsonArrayWithUniqueElements() {
        return (val, jsonPath) -> {
            Assertions.assertThat(val)
                    .as("Значение по JsonPath %s должно быть массивом", jsonPath)
                    .isInstanceOf(List.class);

            List<?> arr = (List<?>) val;
            Assertions.assertThat(arr)
                    .as("Массив по JsonPath %s не должен быть пустым", jsonPath)
                    .isNotEmpty();

            HashSet<Object> uniqueSet = new HashSet<>(arr);
            Assertions.assertThat(uniqueSet.size())
                    .as("Все элементы массива по JsonPath %s должны быть уникальными", jsonPath)
                    .isEqualTo(arr.size());
        };
    }
}
