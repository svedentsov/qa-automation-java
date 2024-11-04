package kafka.matcher;

import kafka.matcher.condition.*;
import kafka.matcher.conditions.*;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * Утилитный класс для создания условий проверки сообщений Kafka.
 * Предоставляет методы для создания различных условий проверки записей, ключей, заголовков и значений.
 */
@UtilityClass
public class KafkaMatcher {

    /**
     * Проверяет наличие записи с указанным заголовком и значением.
     *
     * @param headerKey   ключ заголовка
     * @param headerValue значение заголовка
     * @return условие для проверки наличия заголовка с указанным значением
     */
    public static Conditions headersExists(@NonNull String headerKey, @NonNull String headerValue) {
        return new HeaderExistConditions(headerKey, headerValue);
    }

    /**
     * Проверяет наличие записи с указанным ключом.
     *
     * @param key ключ для проверки
     * @return условие для проверки наличия ключа
     */
    public static Conditions keysExists(@NonNull String key) {
        return new KeyExistConditions(key);
    }

    /**
     * Проверяет, что количество записей больше указанного.
     *
     * @param count минимальное количество записей
     * @return условие для проверки количества записей
     */
    public static Conditions recordsCountGreater(int count) {
        return new RecordCountGreaterConditions(count);
    }

    /**
     * Проверяет, что количество записей равно указанному значению.
     *
     * @param count точное количество записей
     * @return условие для проверки количества записей
     */
    public static Conditions recordsCountEqual(int count) {
        return new RecordCountEqualConditions(count);
    }

    /**
     * Проверяет наличие хотя бы одной записи.
     *
     * @return условие для проверки наличия записей
     */
    public static Conditions recordsExists() {
        return new RecordExistConditions();
    }

    /**
     * Проверяет, что заголовок записи содержит указанный текст.
     *
     * @param headerKey ключ заголовка
     * @param text      текст для проверки
     * @return условие для проверки содержимого заголовка
     */
    public static Condition headerContains(@NonNull String headerKey, @NonNull String text) {
        return new HeaderContainCondition(headerKey, text);
    }

    /**
     * Проверяет, что заголовок записи не содержит указанный текст.
     *
     * @param headerKey ключ заголовка
     * @param text      текст для проверки
     * @return условие для проверки отсутствия текста в заголовке
     */
    public static Condition headerNotContains(@NonNull String headerKey, @NonNull String text) {
        return new HeaderContainNotCondition(headerKey, text);
    }

    /**
     * Проверяет, что заголовок записи равен ожидаемому значению.
     *
     * @param headerKey ключ заголовка
     * @param text      ожидаемое значение заголовка
     * @return условие для проверки равенства значения заголовка
     */
    public static Condition headerEquals(@NonNull String headerKey, @NonNull String text) {
        return new HeaderEqualCondition(headerKey, text);
    }

    /**
     * Проверяет, что ключ заголовка записи существует.
     *
     * @param headerKey ключ заголовка
     * @return условие для проверки наличия ключа заголовка
     */
    public static Condition headerKeyExists(@NonNull String headerKey) {
        return new HeaderKeyExistCondition(headerKey);
    }

    /**
     * Проверяет, что ключ заголовка записи равен ожидаемому значению.
     *
     * @param expectedKey ожидаемый ключ заголовка
     * @return условие для проверки равенства ключа заголовка
     */
    public static Condition headerKeyEquals(@NonNull String expectedKey) {
        return new HeaderKeyEqualCondition(expectedKey);
    }

    /**
     * Проверяет, что ключ заголовка записи содержит указанный текст.
     *
     * @param text текст для проверки
     * @return условие для проверки содержимого ключа заголовка
     */
    public static Condition headerKeyContains(@NonNull String text) {
        return new HeaderKeyContainCondition(text);
    }

    /**
     * Проверяет, что ключ заголовка записи не содержит указанный текст.
     *
     * @param text текст для проверки
     * @return условие для проверки отсутствия текста в ключе заголовка
     */
    public static Condition headerKeyNotContains(@NonNull String text) {
        return new HeaderKeyContainNotCondition(text);
    }

    /**
     * Проверяет, что значение заголовка записи содержит указанный текст.
     *
     * @param headerKey ключ заголовка
     * @param text      текст для проверки
     * @return условие для проверки содержимого значения заголовка
     */
    public static Condition headerValueContains(@NonNull String headerKey, @NonNull String text) {
        return new HeaderValueContainCondition(headerKey, text);
    }

    /**
     * Проверяет, что значение заголовка записи не содержит указанный текст.
     *
     * @param headerKey ключ заголовка
     * @param text      текст для проверки
     * @return условие для проверки отсутствия текста в значении заголовка
     */
    public static Condition headerValueNotContains(@NonNull String headerKey, @NonNull String text) {
        return new HeaderValueContainNotCondition(headerKey, text);
    }

    /**
     * Проверяет, что значение заголовка записи равно ожидаемому значению.
     *
     * @param headerKey     ключ заголовка
     * @param expectedValue ожидаемое значение заголовка
     * @return условие для проверки равенства значения заголовка
     */
    public static Condition headerValueEquals(@NonNull String headerKey, @NonNull String expectedValue) {
        return new HeaderValueEqualCondition(headerKey, expectedValue);
    }

    /**
     * Проверяет, что ключ записи равен ожидаемому значению.
     *
     * @param expectedKey ожидаемый ключ
     * @return условие для проверки равенства ключа
     */
    public static Condition keyEquals(@NonNull String expectedKey) {
        return new KeyEqualCondition(expectedKey);
    }

    /**
     * Проверяет, что ключ записи содержит указанный текст.
     *
     * @param text текст для проверки
     * @return условие для проверки содержимого ключа
     */
    public static Condition keyContains(@NonNull String text) {
        return new KeyContainCondition(text);
    }

    /**
     * Проверяет, что ключ записи не содержит указанный текст.
     *
     * @param text текст для проверки
     * @return условие для проверки отсутствия текста в ключе
     */
    public static Condition keyNotContains(@NonNull String text) {
        return new KeyContainNotCondition(text);
    }

    /**
     * Проверяет значение записи с помощью JSONPath.
     *
     * @param jsonPath      JSONPath выражение
     * @param expectedValue ожидаемое значение
     * @return условие для проверки значения по JSONPath
     */
    public static Condition valueJsonPathEquals(@NonNull String jsonPath, @NonNull String expectedValue) {
        return new ValueJsonPathEqualCondition(jsonPath, expectedValue);
    }

    /**
     * Проверяет, что значение записи содержит указанный текст по JSONPath.
     *
     * @param jsonPath      JSONPath выражение
     * @param expectedValue ожидаемый текст
     * @return условие для проверки содержания по JSONPath
     */
    public static Condition valueJsonPathContains(@NonNull String jsonPath, @NonNull String expectedValue) {
        return new ValueJsonPathContainCondition(jsonPath, expectedValue);
    }

    /**
     * Проверяет, что значение записи не содержит указанный текст по JSONPath.
     *
     * @param jsonPath JSONPath выражение
     * @param text     текст для проверки
     * @return условие для проверки отсутствия текста по JSONPath
     */
    public static Condition valueJsonPathNotContains(@NonNull String jsonPath, @NonNull String text) {
        return new ValueJsonPathContainNotCondition(jsonPath, text);
    }

    /**
     * Проверяет, что значение записи соответствует регулярному выражению по JSONPath.
     *
     * @param jsonPath JSONPath выражение
     * @param regex    регулярное выражение
     * @return условие для проверки значения по регулярному выражению
     */
    public static Condition valueJsonPathMatchesRegex(@NonNull String jsonPath, @NonNull String regex) {
        return new ValueJsonPathMatchesRegexCondition(jsonPath, regex);
    }

    /**
     * Проверяет, что значение записи содержит все указанные тексты.
     *
     * @param texts список текстов для проверки
     * @return условие для проверки содержимого значения
     */
    public static Condition valueContains(@NonNull List<String> texts) {
        return new ValueContainsCondition(texts);
    }

    /**
     * Проверяет, что значение записи содержит хотя бы один из указанных текстов.
     *
     * @param texts список текстов для проверки
     * @return условие для проверки наличия любого текста в значении
     */
    public static Condition valueContainsAny(@NonNull List<String> texts) {
        return new ValueContainsAnyCondition(texts);
    }

    /**
     * Проверяет, что значение записи содержит указанный текст.
     *
     * @param text текст для проверки
     * @return условие для проверки содержимого значения
     */
    public static Condition valueContains(@NonNull String text) {
        return new ValueContainCondition(text);
    }

    /**
     * Проверяет, что значение записи не содержит указанный текст.
     *
     * @param text текст для проверки
     * @return условие для проверки отсутствия текста в значении
     */
    public static Condition valueNotContains(@NonNull String text) {
        return new ValueContainNotCondition(text);
    }

    /**
     * Проверяет, что значение записи не содержит все указанные тексты.
     *
     * @param texts список текстов для проверки
     * @return условие для проверки отсутствия всех текстов в значении
     */
    public static Condition valueNotContains(@NonNull List<String> texts) {
        return new ValueContainsNotCondition(texts);
    }

    /**
     * Проверяет, что значение записи равно ожидаемому значению.
     *
     * @param expectedValue ожидаемое значение
     * @return условие для проверки равенства значения
     */
    public static Condition valueEquals(@NonNull String expectedValue) {
        return new ValueEqualCondition(expectedValue);
    }

    /**
     * Проверяет, что значение по JSONPath является булевым значением.
     *
     * @param jsonPath JSONPath выражение
     * @return условие для проверки типа булевого значения по JSONPath
     */
    public static Condition valueJsonPathIsBoolean(@NonNull String jsonPath) {
        return new ValueJsonPathIsBooleanCondition(jsonPath);
    }

    /**
     * Проверяет, что значение по JSONPath является числом.
     *
     * @param jsonPath JSONPath выражение
     * @return условие для проверки типа числа по JSONPath
     */
    public static Condition valueJsonPathIsNumber(@NonNull String jsonPath) {
        return new ValueJsonPathIsNumberCondition(jsonPath);
    }

    /**
     * Проверяет, что значение по JSONPath является строкой.
     *
     * @param jsonPath JSONPath выражение
     * @return условие для проверки типа строки по JSONPath
     */
    public static Condition valueJsonPathIsString(@NonNull String jsonPath) {
        return new ValueJsonPathIsStringCondition(jsonPath);
    }

    /**
     * Проверяет, что значение по JSONPath является массивом.
     *
     * @param jsonPath JSONPath выражение
     * @return условие для проверки типа массива по JSONPath
     */
    public static Condition valueJsonPathIsArray(@NonNull String jsonPath) {
        return new ValueJsonPathIsArrayCondition(jsonPath);
    }

    /**
     * Проверяет размер массива по JSONPath.
     *
     * @param jsonPath     JSONPath выражение
     * @param expectedSize ожидаемый размер массива
     * @return условие для проверки размера массива
     */
    public static Condition valueJsonPathArraySize(@NonNull String jsonPath, int expectedSize) {
        return new ValueJsonPathArraySizeCondition(jsonPath, expectedSize);
    }

    /**
     * Проверяет порядок слов в значении записи.
     *
     * @param words список слов для проверки порядка
     * @return условие для проверки порядка слов в значении
     */
    public static Condition valueWordsOrder(@NonNull List<String> words) {
        return new ValueWordsOrderCondition(words);
    }
}
