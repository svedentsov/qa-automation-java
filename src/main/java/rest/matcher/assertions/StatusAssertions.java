package rest.matcher.assertions;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.HamcrestCondition;
import org.hamcrest.Matcher;
import rest.matcher.condition.Condition;

import java.util.Arrays;
import java.util.List;

/**
 * Класс для утверждений, связанных с кодом состояния ответа.
 */
public class StatusAssertions {

    /**
     * Функциональный интерфейс для условий проверки кода состояния ответа.
     */
    @FunctionalInterface
    public interface StatusCondition extends Condition {
    }

    /**
     * Проверяет, что код состояния соответствует ожидаемому значению.
     *
     * @param expectedCode ожидаемый код состояния
     * @return условие для проверки кода состояния
     */
    public static StatusCondition statusCode(int expectedCode) {
        return response -> {
            int actualCode = response.getStatusCode();
            Assertions.assertThat(actualCode)
                    .as("Ожидалось, что код состояния будет %d, но был %d", expectedCode, actualCode)
                    .isEqualTo(expectedCode);
        };
    }

    /**
     * Проверяет, что код состояния находится в заданном диапазоне.
     *
     * @param startInclusive начало диапазона (включительно)
     * @param endInclusive   конец диапазона (включительно)
     * @return условие для проверки диапазона кода состояния
     */
    public static StatusCondition statusCodeBetween(int startInclusive, int endInclusive) {
        return response -> {
            int code = response.getStatusCode();
            Assertions.assertThat(code)
                    .as("Ожидалось, что код состояния будет в диапазоне от %d до %d, но был %d", startInclusive, endInclusive, code)
                    .isBetween(startInclusive, endInclusive);
        };
    }

    /**
     * Проверяет, что код состояния является информационным (1xx).
     *
     * @return условие для проверки информационного кода состояния (1xx)
     */
    public static StatusCondition isInformational1xx() {
        return response -> {
            int code = response.getStatusCode();
            Assertions.assertThat(code)
                    .as("Ожидалось, что код состояния будет в диапазоне 100-199, но был %d", code)
                    .isBetween(100, 199);
        };
    }

    /**
     * Проверяет, что код состояния является успешным (2xx).
     *
     * @return условие для проверки успешного кода состояния
     */
    public static StatusCondition isSuccessful2xx() {
        return response -> {
            int code = response.getStatusCode();
            Assertions.assertThat(code)
                    .as("Ожидалось, что код состояния будет в диапазоне 200-299, но был %d", code)
                    .isBetween(200, 299);
        };
    }

    /**
     * Проверяет, что код состояния является перенаправлением (3xx).
     *
     * @return условие для проверки кода состояния перенаправления (3xx)
     */
    public static StatusCondition isRedirect3xx() {
        return response -> {
            int code = response.getStatusCode();
            Assertions.assertThat(code)
                    .as("Ожидалось, что код состояния будет в диапазоне 300-399, но был %d", code)
                    .isBetween(300, 399);
        };
    }

    /**
     * Проверяет, что код состояния является ошибкой клиента (4xx).
     *
     * @return условие для проверки кода состояния ошибки клиента
     */
    public static StatusCondition isClientError4xx() {
        return response -> {
            int code = response.getStatusCode();
            Assertions.assertThat(code)
                    .as("Ожидалось, что код состояния будет в диапазоне 400-499, но был %d", code)
                    .isBetween(400, 499);
        };
    }

    /**
     * Проверяет, что код состояния является ошибкой сервера (5xx).
     *
     * @return условие для проверки кода состояния ошибки сервера
     */
    public static StatusCondition isServerError5xx() {
        return response -> {
            int code = response.getStatusCode();
            Assertions.assertThat(code)
                    .as("Ожидалось, что код состояния будет в диапазоне 500-599, но был %d", code)
                    .isBetween(500, 599);
        };
    }

    /**
     * Проверяет, что строка состояния соответствует ожидаемой.
     *
     * @param expectedLine ожидаемая строка состояния
     * @return условие для проверки строки состояния
     */
    public static StatusCondition statusLineEquals(String expectedLine) {
        return response -> {
            String actualLine = response.getStatusLine();
            Assertions.assertThat(actualLine)
                    .as("Ожидалось, что строка состояния будет '%s', но была '%s'", expectedLine, actualLine)
                    .isEqualTo(expectedLine);
        };
    }

    /**
     * Проверяет, что строка состояния содержит указанную подстроку.
     *
     * @param substring ожидаемая подстрока в строке состояния
     * @return условие для проверки содержимого строки состояния
     */
    public static StatusCondition statusLineContains(String substring) {
        return response -> {
            String actualLine = response.getStatusLine();
            Assertions.assertThat(actualLine)
                    .as("Ожидалось, что строка состояния содержит '%s', но была '%s'", substring, actualLine)
                    .contains(substring);
        };
    }

    /**
     * Проверяет, что строка состояния не содержит указанную подстроку.
     *
     * @param substring подстрока, которую не должно содержать строка состояния
     * @return условие для проверки отсутствия подстроки в строке состояния
     */
    public static StatusCondition statusLineDoesNotContain(String substring) {
        return response -> {
            String actualLine = response.getStatusLine();
            Assertions.assertThat(actualLine)
                    .as("Ожидалось, что строка состояния не содержит '%s', но была '%s'", substring, actualLine)
                    .doesNotContain(substring);
        };
    }

    /**
     * Проверяет, что код состояния не равен указанному значению.
     *
     * @param notExpectedCode код состояния, который не должен быть
     * @return условие для проверки, что код состояния не равен указанному
     */
    public static StatusCondition statusCodeNot(int notExpectedCode) {
        return response -> {
            int code = response.getStatusCode();
            Assertions.assertThat(code)
                    .as("Ожидалось, что код состояния не будет %d, но он равен", notExpectedCode)
                    .isNotEqualTo(notExpectedCode);
        };
    }

    /**
     * Проверяет, что код состояния принадлежит указанному классу (например, 2 для 2xx).
     *
     * @param statusClass класс кода состояния (1 для 1xx, 2 для 2xx и т.д.)
     * @return условие для проверки принадлежности к классу кода состояния
     * @throws IllegalArgumentException если {@code statusClass} не в диапазоне 1-5
     */
    public static StatusCondition statusCodeClass(int statusClass) {
        if (statusClass < 1 || statusClass > 5) {
            throw new IllegalArgumentException("statusClass должен быть в диапазоне от 1 до 5");
        }
        return response -> {
            int statusCode = response.getStatusCode();
            int codeClass = statusCode / 100;
            Assertions.assertThat(codeClass)
                    .as("Код состояния %d не принадлежит классу '%dxx'", statusCode, statusClass)
                    .isEqualTo(statusClass);
        };
    }

    /**
     * Проверяет, что код состояния соответствует заданному Matcher.
     *
     * @param matcher Matcher для проверки кода состояния
     * @return условие для проверки соответствия кода состояния Matcher
     */
    public static StatusCondition statusCodeMatches(Matcher<Integer> matcher) {
        return response -> {
            int actualCode = response.getStatusCode();
            Assertions.assertThat(actualCode)
                    .as("Ожидалось, что код состояния соответствует %s, но был %d", matcher, actualCode)
                    .is(new HamcrestCondition<>(matcher));
        };
    }

    /**
     * Проверяет, что строка состояния соответствует заданному Matcher.
     *
     * @param matcher Matcher для проверки строки состояния
     * @return условие для проверки соответствия строки состояния Matcher
     */
    public static StatusCondition statusLineMatches(Matcher<String> matcher) {
        return response -> {
            String actualLine = response.getStatusLine();
            Assertions.assertThat(actualLine)
                    .as("Ожидалось, что строка состояния соответствует %s, но была '%s'", matcher, actualLine)
                    .is(new HamcrestCondition<>(matcher));
        };
    }

    /**
     * Проверяет, что код состояния не принадлежит указанному классу (например, не 2 для 2xx).
     *
     * @param statusClass класс кода состояния (1 для 1xx, 2 для 2xx и т.д.)
     * @return условие для проверки отсутствия принадлежности к классу кода состояния
     * @throws IllegalArgumentException если {@code statusClass} не в диапазоне 1-5
     */
    public static StatusCondition statusCodeNotClass(int statusClass) {
        if (statusClass < 1 || statusClass > 5) {
            throw new IllegalArgumentException("statusClass должен быть в диапазоне от 1 до 5");
        }
        return response -> {
            int statusCode = response.getStatusCode();
            int codeClass = statusCode / 100;
            Assertions.assertThat(codeClass)
                    .as("Код состояния %d не должен принадлежать классу '%dxx'", statusCode, statusClass)
                    .isNotEqualTo(statusClass);
        };
    }

    /**
     * Проверяет, что код состояния соответствует одному из заданных значений.
     *
     * @param expectedCodes список ожидаемых кодов состояния
     * @return условие для проверки соответствия кода состояния одному из заданных значений
     * @throws IllegalArgumentException если {@code expectedCodes} пуст или содержит {@code null}
     */
    public static StatusCondition statusCodeIn(List<Integer> expectedCodes) {
        return response -> {
            int actualCode = response.getStatusCode();
            Assertions.assertThat(expectedCodes)
                    .as("Ожидалось, что код состояния будет одним из %s, но был %d", expectedCodes, actualCode)
                    .contains(actualCode);
        };
    }

    /**
     * Проверяет, что код состояния соответствует одному из заданных значений.
     *
     * @param expectedCodes массив ожидаемых кодов состояния
     * @return условие для проверки соответствия кода состояния одному из заданных значений
     * @throws IllegalArgumentException если {@code expectedCodes} пуст или содержит {@code null}
     */
    public static StatusCondition statusCodeIn(int... expectedCodes) {
        return response -> {
            int actualCode = response.getStatusCode();
            Assertions.assertThat(actualCode)
                    .as("Ожидалось, что код состояния будет одним из %s, но был %d", List.of(expectedCodes), actualCode)
                    .isIn((Object) Arrays.stream(expectedCodes).boxed().toArray(Integer[]::new));
        };
    }

    /**
     * Проверяет, что код состояния не соответствует одному из заданных значений.
     *
     * @param notExpectedCodes список кодов состояния, которые не должны быть
     * @return условие для проверки отсутствия соответствия кода состояния одному из заданных значений
     * @throws IllegalArgumentException если {@code notExpectedCodes} пуст или содержит {@code null}
     */
    public static StatusCondition statusCodeNotIn(List<Integer> notExpectedCodes) {
        return response -> {
            int actualCode = response.getStatusCode();
            Assertions.assertThat(notExpectedCodes)
                    .as("Ожидалось, что код состояния не будет одним из %s, но был %d", notExpectedCodes, actualCode)
                    .doesNotContain(actualCode);
        };
    }

    /**
     * Проверяет, что код состояния не соответствует одному из заданных значений.
     *
     * @param notExpectedCodes массив кодов состояния, которые не должны быть
     * @return условие для проверки отсутствия соответствия кода состояния одному из заданных значений
     * @throws IllegalArgumentException если {@code notExpectedCodes} пуст или содержит {@code null}
     */
    public static StatusCondition statusCodeNotIn(int... notExpectedCodes) {
        return response -> {
            int actualCode = response.getStatusCode();
            Assertions.assertThat(actualCode)
                    .as("Ожидалось, что код состояния не будет одним из %s, но был %d", List.of(notExpectedCodes), actualCode)
                    .isNotIn((Object) Arrays.stream(notExpectedCodes).boxed().toArray(Integer[]::new));
        };
    }
}
