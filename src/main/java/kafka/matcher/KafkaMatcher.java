package kafka.matcher;

import kafka.matcher.condition.*;
import kafka.matcher.conditions.*;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * Утилитный класс для создания условий проверки сообщений Kafka.
 * Предоставляет методы для создания различных условий проверки наличия записей, значений ключей, заголовков и содержимого.
 */
@UtilityClass
public class KafkaMatcher {

    /**
     * Проверка наличия хотя бы одной записи.
     *
     * @return условие {@link RecordExistConditions}
     */
    public static Conditions recordsExists() {
        return new RecordExistConditions();
    }

    /**
     * Проверка, что количество записей больше указанного.
     *
     * @param count минимальное количество записей
     * @return условие {@link RecordCountGreaterConditions}
     */
    public static Conditions recordsCountGreater(int count) {
        return new RecordCountGreaterConditions(count);
    }

    /**
     * Проверка, что количество записей равно указанному значению.
     *
     * @param count точное количество записей
     * @return условие {@link RecordCountEqualConditions}
     */
    public static Conditions recordsCountEqual(int count) {
        return new RecordCountEqualConditions(count);
    }

    /**
     * Проверка наличия записи с указанным ключом.
     *
     * @param key ключ для проверки
     * @return условие {@link KeyExistConditions}
     */
    public static Conditions keysExists(@NonNull String key) {
        return new KeyExistConditions(key);
    }

    /**
     * Проверка наличия записи с указанным заголовком и значением.
     *
     * @param headerKey   ключ заголовка
     * @param headerValue значение заголовка
     * @return условие {@link HeaderExistConditions}
     */
    public static Conditions headersExists(@NonNull String headerKey, @NonNull String headerValue) {
        return new HeaderExistConditions(headerKey, headerValue);
    }

    /**
     * Проверка, что ключ записи равен ожидаемому значению.
     *
     * @param expectedKey ожидаемый ключ
     * @return условие {@link KeyEqualCondition}
     */
    public static KeyEqualCondition keyEquals(@NonNull String expectedKey) {
        return new KeyEqualCondition(expectedKey);
    }

    /**
     * Проверка, что ключ записи содержит указанный текст.
     *
     * @param text текст для проверки
     * @return условие {@link KeyContainCondition}
     */
    public static Condition keyContains(@NonNull String text) {
        return new KeyContainCondition(text);
    }

    /**
     * Проверка, что ключ записи не содержит указанный текст.
     *
     * @param text текст для проверки
     * @return условие {@link KeyContainNotCondition}
     */
    public static Condition keyNotContains(@NonNull String text) {
        return new KeyContainNotCondition(text);
    }

    /**
     * Проверка, что заголовок записи равен ожидаемому значению.
     *
     * @param headerKey ключ заголовка
     * @param text      ожидаемое значение заголовка
     * @return условие {@link HeaderEqualCondition}
     */
    public static Condition headerEquals(@NonNull String headerKey, @NonNull String text) {
        return new HeaderEqualCondition(headerKey, text);
    }

    /**
     * Проверка, что заголовок записи содержит указанный текст.
     *
     * @param headerKey ключ заголовка
     * @param text      текст для проверки
     * @return условие {@link HeaderContainCondition}
     */
    public static Condition headerContains(@NonNull String headerKey, @NonNull String text) {
        return new HeaderContainCondition(headerKey, text);
    }

    /**
     * Проверка, что заголовок записи не содержит указанный текст.
     *
     * @param headerKey ключ заголовка
     * @param text      текст для проверки
     * @return условие {@link HeaderContainNotCondition}
     */
    public static Condition headerNotContains(@NonNull String headerKey, @NonNull String text) {
        return new HeaderContainNotCondition(headerKey, text);
    }

    /**
     * Проверка, что ключ заголовка записи существует.
     *
     * @param headerKey ключ заголовка
     * @return условие {@link HeaderKeyExistCondition}
     */
    public static Condition headerKeyExists(@NonNull String headerKey) {
        return new HeaderKeyExistCondition(headerKey);
    }

    /**
     * Проверка, что ключ заголовка записи равен ожидаемому значению.
     *
     * @param expectedKey ожидаемый ключ заголовка
     * @return условие {@link HeaderKeyEqualCondition}
     */
    public static Condition headerKeyEquals(@NonNull String expectedKey) {
        return new HeaderKeyEqualCondition(expectedKey);
    }

    /**
     * Проверка, что ключ заголовка записи содержит указанный текст.
     *
     * @param text текст для проверки
     * @return условие {@link HeaderKeyContainCondition}
     */
    public static Condition headerKeyContains(@NonNull String text) {
        return new HeaderKeyContainCondition(text);
    }

    /**
     * Проверка, что ключ заголовка записи не содержит указанный текст.
     *
     * @param text текст для проверки
     * @return условие {@link HeaderKeyContainNotCondition}
     */
    public static Condition headerKeyNotContains(@NonNull String text) {
        return new HeaderKeyContainNotCondition(text);
    }

    /**
     * Проверка, что значение заголовка записи равно ожидаемому значению.
     *
     * @param headerKey     ключ заголовка
     * @param expectedValue ожидаемое значение заголовка
     * @return условие {@link HeaderValueEqualCondition}
     */
    public static Condition headerValueEquals(@NonNull String headerKey, @NonNull String expectedValue) {
        return new HeaderValueEqualCondition(headerKey, expectedValue);
    }

    /**
     * Проверка, что значение заголовка записи содержит указанный текст.
     *
     * @param headerKey ключ заголовка
     * @param text      текст для проверки
     * @return условие {@link HeaderValueContainCondition}
     */
    public static Condition headerValueContains(@NonNull String headerKey, @NonNull String text) {
        return new HeaderValueContainCondition(headerKey, text);
    }

    /**
     * Проверка, что значение заголовка записи не содержит указанный текст.
     *
     * @param headerKey ключ заголовка
     * @param text      текст для проверки
     * @return условие {@link HeaderValueContainNotCondition}
     */
    public static Condition headerValueNotContains(@NonNull String headerKey, @NonNull String text) {
        return new HeaderValueContainNotCondition(headerKey, text);
    }

    /**
     * Проверка, что значение записи равно ожидаемому значению.
     *
     * @param expectedValue ожидаемое значение
     * @return условие {@link ValueEqualCondition}
     */
    public static Condition valueEquals(@NonNull String expectedValue) {
        return new ValueEqualCondition(expectedValue);
    }

    /**
     * Проверка, что значение записи содержит указанный текст.
     *
     * @param text текст для проверки
     * @return условие {@link ValueContainCondition}
     */
    public static Condition valueContains(@NonNull String text) {
        return new ValueContainCondition(text);
    }

    /**
     * Проверка, что значение записи содержит все указанные тексты.
     *
     * @param texts список текстов для проверки
     * @return условие {@link ValueContainsCondition}
     */
    public static Condition valueContains(@NonNull List<String> texts) {
        return new ValueContainsCondition(texts);
    }

    /**
     * Проверка, что значение записи содержит хотя бы один из указанных текстов.
     *
     * @param texts список текстов для проверки
     * @return условие {@link ValueContainsAnyCondition}
     */
    public static Condition valueContainsAny(@NonNull List<String> texts) {
        return new ValueContainsAnyCondition(texts);
    }

    /**
     * Проверка, что значение записи не содержит указанный текст.
     *
     * @param text текст для проверки
     * @return условие {@link ValueContainNotCondition}
     */
    public static Condition valueNotContains(@NonNull String text) {
        return new ValueContainNotCondition(text);
    }

    /**
     * Проверка, что значение записи не содержит все указанные тексты.
     *
     * @param texts список текстов для проверки
     * @return условие {@link ValueContainsNotCondition}
     */
    public static Condition valueNotContains(@NonNull List<String> texts) {
        return new ValueContainsNotCondition(texts);
    }

    /**
     * Проверка значения записи с помощью JSONPath.
     *
     * @param jsonPath      JSONPath выражение
     * @param expectedValue ожидаемое значение
     * @return условие {@link ValueJsonPathEqualCondition}
     */
    public static Condition valueJsonPathEquals(@NonNull String jsonPath, @NonNull String expectedValue) {
        return new ValueJsonPathEqualCondition(jsonPath, expectedValue);
    }

    /**
     * Проверка, что значение записи содержит указанный текст по JSONPath.
     *
     * @param jsonPath      JSONPath выражение
     * @param expectedValue ожидаемый текст
     * @return условие {@link ValueJsonPathContainCondition}
     */
    public static Condition valueJsonPathContains(@NonNull String jsonPath, @NonNull String expectedValue) {
        return new ValueJsonPathContainCondition(jsonPath, expectedValue);
    }

    /**
     * Проверка, что значение записи не содержит указанный текст по JSONPath.
     *
     * @param jsonPath JSONPath выражение
     * @param text     текст для проверки
     * @return условие {@link ValueJsonPathContainNotCondition}
     */
    public static Condition valueJsonPathNotContains(@NonNull String jsonPath, @NonNull String text) {
        return new ValueJsonPathContainNotCondition(jsonPath, text);
    }

    /**
     * Проверка, что значение записи соответствует регулярному выражению по JSONPath.
     *
     * @param jsonPath JSONPath выражение
     * @param regex    регулярное выражение
     * @return условие {@link ValueJsonPathMatchesRegexCondition}
     */
    public static Condition valueJsonPathMatchesRegex(@NonNull String jsonPath, @NonNull String regex) {
        return new ValueJsonPathMatchesRegexCondition(jsonPath, regex);
    }

    /**
     * Проверка, что значение по JSONPath является строкой.
     *
     * @param jsonPath JSONPath выражение
     * @return условие {@link ValueJsonPathIsStringCondition}
     */
    public static Condition valueJsonPathIsString(@NonNull String jsonPath) {
        return new ValueJsonPathIsStringCondition(jsonPath);
    }

    /**
     * Проверка, что значение по JSONPath является числом.
     *
     * @param jsonPath JSONPath выражение
     * @return условие {@link ValueJsonPathIsNumberCondition}
     */
    public static Condition valueJsonPathIsNumber(@NonNull String jsonPath) {
        return new ValueJsonPathIsNumberCondition(jsonPath);
    }

    /**
     * Проверка, что значение по JSONPath является булевым значением.
     *
     * @param jsonPath JSONPath выражение
     * @return условие {@link ValueJsonPathIsBooleanCondition}
     */
    public static Condition valueJsonPathIsBoolean(@NonNull String jsonPath) {
        return new ValueJsonPathIsBooleanCondition(jsonPath);
    }

    /**
     * Проверка, что значение по JSONPath является массивом.
     *
     * @param jsonPath JSONPath выражение
     * @return условие {@link ValueJsonPathIsArrayCondition}
     */
    public static Condition valueJsonPathIsArray(@NonNull String jsonPath) {
        return new ValueJsonPathIsArrayCondition(jsonPath);
    }

    /**
     * Проверка размера массива по JSONPath.
     *
     * @param jsonPath     JSONPath выражение
     * @param expectedSize ожидаемый размер массива
     * @return условие {@link ValueJsonPathArraySizeCondition}
     */
    public static Condition valueJsonPathArraySize(@NonNull String jsonPath, int expectedSize) {
        return new ValueJsonPathArraySizeCondition(jsonPath, expectedSize);
    }

    /**
     * Проверка порядка слов в значении записи.
     *
     * @param words список слов для проверки порядка
     * @return условие {@link ValueWordsOrderCondition}
     */
    public static Condition valueWordsOrder(@NonNull List<String> words) {
        return new ValueWordsOrderCondition(words);
    }
}
