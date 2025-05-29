package rest.matcher.assertions;

import core.matcher.Condition;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.HamcrestCondition;
import org.hamcrest.Matcher;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Класс для утверждений, связанных с кодом состояния ответа.
 */
@UtilityClass
public class StatusAssertions {

    /**
     * Функциональный интерфейс для условий проверки кода состояния ответа.
     */
    @FunctionalInterface
    public interface StatusCondition extends Condition<Response> {
    }

    /**
     * Проверяет, что код состояния соответствует ожидаемому значению.
     *
     * @param expectedCode ожидаемый код состояния
     * @return условие для проверки кода состояния
     * @throws IllegalArgumentException если expectedCode не является валидным HTTP кодом
     */
    public static StatusCondition statusCode(int expectedCode) {
        if (expectedCode < 100 || expectedCode > 599) {
            throw new IllegalArgumentException("expectedCode должен быть валидным HTTP кодом (100-599)");
        }
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
     * @throws IllegalArgumentException если startInclusive > endInclusive или диапазон не валиден
     */
    public static StatusCondition statusCodeBetween(int startInclusive, int endInclusive) {
        if (startInclusive > endInclusive) {
            throw new IllegalArgumentException("startInclusive не может быть больше endInclusive");
        }
        if (startInclusive < 100 || endInclusive > 599) {
            throw new IllegalArgumentException("Диапазон должен быть в пределах 100-599");
        }
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
    public static StatusCondition statusIsSuccessful2xx() {
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
    public static StatusCondition statusIsRedirect3xx() {
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
    public static StatusCondition statusIsClientError4xx() {
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
    public static StatusCondition statusIsServerError5xx() {
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
     * @throws IllegalArgumentException если expectedLine равно null
     */
    public static StatusCondition statusLineEquals(String expectedLine) {
        Objects.requireNonNull(expectedLine, "expectedLine не может быть null");
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
     * @throws IllegalArgumentException если substring равно null
     */
    public static StatusCondition statusLineContains(String substring) {
        Objects.requireNonNull(substring, "substring не может быть null");
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
     * @throws IllegalArgumentException если substring равно null
     */
    public static StatusCondition statusLineDoesNotContain(String substring) {
        Objects.requireNonNull(substring, "substring не может быть null");
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
     * @throws IllegalArgumentException если notExpectedCode не является валидным HTTP кодом
     */
    public static StatusCondition statusCodeNot(int notExpectedCode) {
        if (notExpectedCode < 100 || notExpectedCode > 599) {
            throw new IllegalArgumentException("notExpectedCode должен быть валидным HTTP кодом (100-599)");
        }
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
     * @throws IllegalArgumentException если matcher равно null
     */
    public static StatusCondition statusCodeMatches(Matcher<Integer> matcher) {
        Objects.requireNonNull(matcher, "matcher не может быть null");
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
     * @throws IllegalArgumentException если matcher равно null
     */
    public static StatusCondition statusLineMatches(Matcher<String> matcher) {
        Objects.requireNonNull(matcher, "matcher не может быть null");
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
     * @throws IllegalArgumentException если {@code expectedCodes} пуст или содержит недопустимые значения
     */
    public static StatusCondition statusCodeIn(List<Integer> expectedCodes) {
        if (expectedCodes == null || expectedCodes.isEmpty()) {
            throw new IllegalArgumentException("expectedCodes не может быть null или пустым");
        }
        for (Integer code : expectedCodes) {
            if (code == null || code < 100 || code > 599) {
                throw new IllegalArgumentException("Каждый expectedCode должен быть валидным HTTP кодом (100-599)");
            }
        }
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
     * @throws IllegalArgumentException если {@code expectedCodes} пуст или содержит недопустимые значения
     */
    public static StatusCondition statusCodeIn(int... expectedCodes) {
        if (expectedCodes == null || expectedCodes.length == 0) {
            throw new IllegalArgumentException("expectedCodes не может быть null или пустым");
        }
        for (int code : expectedCodes) {
            if (code < 100 || code > 599) {
                throw new IllegalArgumentException("Каждый expectedCode должен быть валидным HTTP кодом (100-599)");
            }
        }
        Integer[] expected = Arrays.stream(expectedCodes).boxed().toArray(Integer[]::new);
        return response -> {
            int actualCode = response.getStatusCode();
            Assertions.assertThat(actualCode)
                    .as("Ожидалось, что код состояния будет одним из %s, но был %d", Arrays.toString(expectedCodes), actualCode)
                    .isIn((Object[]) expected);
        };
    }

    /**
     * Проверяет, что код состояния не соответствует одному из заданных значений.
     *
     * @param notExpectedCodes список кодов состояния, которые не должны быть
     * @return условие для проверки отсутствия соответствия кода состояния одному из заданных значений
     * @throws IllegalArgumentException если {@code notExpectedCodes} пуст или содержит недопустимые значения
     */
    public static StatusCondition statusCodeNotIn(List<Integer> notExpectedCodes) {
        if (notExpectedCodes == null || notExpectedCodes.isEmpty()) {
            throw new IllegalArgumentException("notExpectedCodes не может быть null или пустым");
        }
        for (Integer code : notExpectedCodes) {
            if (code == null || code < 100 || code > 599) {
                throw new IllegalArgumentException("Каждый notExpectedCode должен быть валидным HTTP кодом (100-599)");
            }
        }
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
     * @throws IllegalArgumentException если {@code notExpectedCodes} пуст или содержит недопустимые значения
     */
    public static StatusCondition statusCodeNotIn(int... notExpectedCodes) {
        if (notExpectedCodes == null || notExpectedCodes.length == 0) {
            throw new IllegalArgumentException("notExpectedCodes не может быть null или пустым");
        }
        for (int code : notExpectedCodes) {
            if (code < 100 || code > 599) {
                throw new IllegalArgumentException("Каждый notExpectedCode должен быть валидным HTTP кодом (100-599)");
            }
        }
        Integer[] notExpected = Arrays.stream(notExpectedCodes).boxed().toArray(Integer[]::new);
        return response -> {
            int actualCode = response.getStatusCode();
            Assertions.assertThat(actualCode)
                    .as("Ожидалось, что код состояния не будет одним из %s, но был %d", Arrays.toString(notExpectedCodes), actualCode)
                    .isNotIn((Object[]) notExpected);
        };
    }
}
