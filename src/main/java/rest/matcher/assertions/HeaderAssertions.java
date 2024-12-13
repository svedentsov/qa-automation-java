package rest.matcher.assertions;

import io.restassured.http.ContentType;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.HamcrestCondition;
import org.hamcrest.Matcher;
import rest.matcher.condition.Condition;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Класс для утверждений, связанных с заголовками ответа.
 */
public class HeaderAssertions {

    /**
     * Функциональный интерфейс для условий проверки заголовков ответа.
     */
    @FunctionalInterface
    public interface HeaderCondition extends Condition {
    }

    /**
     * Проверяет, что заголовок имеет указанное значение.
     *
     * @param headerName    имя заголовка
     * @param expectedValue ожидаемое значение заголовка
     * @return условие для проверки значения заголовка
     */
    public static HeaderCondition headerEquals(String headerName, String expectedValue) {
        return response -> {
            String actualValue = response.getHeader(headerName);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что заголовок '%s' будет '%s', но был '%s'", headerName, expectedValue, actualValue)
                    .isEqualTo(expectedValue);
        };
    }

    /**
     * Проверяет, что заголовок содержит указанный подстроку.
     *
     * @param headerName имя заголовка
     * @param substring  подстрока, которую должен содержать заголовок
     * @return условие для проверки содержимого заголовка
     */
    public static HeaderCondition headerContains(String headerName, String substring) {
        return response -> {
            String actualValue = response.getHeader(headerName);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что заголовок '%s' будет содержать '%s', но был '%s'", headerName, substring, actualValue)
                    .contains(substring);
        };
    }

    /**
     * Проверяет, что заголовок существует в ответе.
     *
     * @param headerName имя заголовка
     * @return условие для проверки существования заголовка
     */
    public static HeaderCondition headerExists(String headerName) {
        return response -> {
            boolean exists = response.getHeaders().hasHeaderWithName(headerName);
            Assertions.assertThat(exists)
                    .as("Ожидалось, что заголовок '%s' существует", headerName)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что заголовок отсутствует в ответе.
     *
     * @param headerName имя заголовка
     * @return условие для проверки отсутствия заголовка
     */
    public static HeaderCondition headerAbsent(String headerName) {
        return response -> {
            boolean exists = response.getHeaders().hasHeaderWithName(headerName);
            Assertions.assertThat(exists)
                    .as("Ожидалось, что заголовок '%s' отсутствует", headerName)
                    .isFalse();
        };
    }

    /**
     * Проверяет, что заголовок соответствует заданному Matcher.
     *
     * @param headerName имя заголовка
     * @param matcher    Matcher для проверки значения заголовка
     * @return условие для проверки соответствия заголовка
     */
    public static HeaderCondition headerMatches(String headerName, Pattern matcher) {
        return response -> {
            String actualValue = response.getHeader(headerName);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что заголовок '%s' соответствует условию %s, но был '%s'", headerName, matcher, actualValue)
                    .matches(matcher);
        };
    }

    /**
     * Проверяет, что Content-Type соответствует ожидаемому значению.
     *
     * @param contentType ожидаемый тип содержимого
     * @return условие для проверки типа содержимого
     */
    public static HeaderCondition contentType(ContentType contentType) {
        return response -> {
            String actualContentType = response.getContentType();
            Assertions.assertThat(actualContentType)
                    .as("Ожидалось, что Content-Type будет '%s', но был '%s'", contentType, actualContentType)
                    .isEqualToIgnoringCase(contentType.toString());
        };
    }

    /**
     * Проверяет, что Content-Encoding соответствует ожидаемому значению.
     *
     * @param expectedEncoding ожидаемое значение кодировки содержимого
     * @return условие для проверки кодировки содержимого
     */
    public static HeaderCondition contentEncodingEquals(String expectedEncoding) {
        return response -> {
            String actualEncoding = response.getHeader("Content-Encoding");
            Assertions.assertThat(actualEncoding)
                    .as("Ожидалось, что Content-Encoding будет '%s', но был '%s'", expectedEncoding, actualEncoding)
                    .isEqualToIgnoringCase(expectedEncoding);
        };
    }

    /**
     * Проверяет, что заголовок Content-Type соответствует типу JSON.
     *
     * @return условие для проверки типа содержимого JSON
     */
    public static HeaderCondition isJsonContentType() {
        return contentType(ContentType.JSON);
    }

    /**
     * Проверяет, что заголовок Content-Type соответствует типу XML.
     *
     * @return условие для проверки типа содержимого XML
     */
    public static HeaderCondition isXmlContentType() {
        return contentType(ContentType.XML);
    }

    /**
     * Проверяет, что заголовок Content-Type соответствует типу текст.
     *
     * @return условие для проверки типа содержимого текст
     */
    public static HeaderCondition isTextContentType() {
        return contentType(ContentType.TEXT);
    }

    /**
     * Проверяет, что заголовок начинается с указанного префикса.
     *
     * @param headerName имя заголовка
     * @param prefix     ожидаемый префикс
     * @return условие для проверки начала заголовка
     */
    public static HeaderCondition headerStartsWith(String headerName, String prefix) {
        return response -> {
            String actualValue = response.getHeader(headerName);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что заголовок '%s' начинается с '%s', но был '%s'", headerName, prefix, actualValue)
                    .startsWith(prefix);
        };
    }

    /**
     * Проверяет, что заголовок заканчивается на указанный суффикс.
     *
     * @param headerName имя заголовка
     * @param suffix     ожидаемый суффикс
     * @return условие для проверки конца заголовка
     */
    public static HeaderCondition headerEndsWith(String headerName, String suffix) {
        return response -> {
            String actualValue = response.getHeader(headerName);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что заголовок '%s' заканчивается на '%s', но был '%s'", headerName, suffix, actualValue)
                    .endsWith(suffix);
        };
    }

    /**
     * Проверяет, что значение заголовка не пусто.
     *
     * @param headerName имя заголовка
     * @return условие для проверки непустого значения заголовка
     */
    public static HeaderCondition headerValueNotEmpty(String headerName) {
        return response -> {
            String actualValue = response.getHeader(headerName);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что заголовок '%s' не пуст", headerName)
                    .isNotEmpty();
        };
    }

    /**
     * Проверяет, что значение заголовка соответствует указанному регулярному выражению.
     *
     * @param headerName имя заголовка
     * @param regex      регулярное выражение
     * @return условие для проверки соответствия значения заголовка шаблону
     */
    public static HeaderCondition headerValueMatchesRegex(String headerName, String regex) {
        return response -> {
            String actualValue = response.getHeader(headerName);
            Pattern pattern = Pattern.compile(regex);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что заголовок '%s' соответствует шаблону '%s', но был '%s'", headerName, regex, actualValue)
                    .matches(pattern);
        };
    }

    /**
     * Проверяет, что значение заголовка равно ожидаемому значению без учета регистра.
     *
     * @param headerName    имя заголовка
     * @param expectedValue ожидаемое значение (без учета регистра)
     * @return условие для проверки равенства значения заголовка без учета регистра
     */
    public static HeaderCondition headerEqualsIgnoringCase(String headerName, String expectedValue) {
        return response -> {
            String actualValue = response.getHeader(headerName);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что заголовок '%s' равен '%s' (без учета регистра), но был '%s'", headerName, expectedValue, actualValue)
                    .isEqualToIgnoringCase(expectedValue);
        };
    }

    /**
     * Проверяет длину значения заголовка с использованием Matcher.
     *
     * @param headerName имя заголовка
     * @param matcher    Matcher для проверки длины строки
     * @return условие для проверки длины значения заголовка
     */
    public static HeaderCondition headerValueLengthMatches(String headerName, Matcher<Integer> matcher) {
        return response -> {
            String actualValue = response.getHeader(headerName);
            int length = actualValue != null ? actualValue.length() : 0;
            Assertions.assertThat(length)
                    .as("Ожидалось, что длина значения заголовка '%s' соответствует %s, но была %d", headerName, matcher, length)
                    .is(new HamcrestCondition<>(matcher));
        };
    }

    /**
     * Проверяет, что заголовок содержит все указанные подстроки.
     *
     * @param headerName имя заголовка
     * @param substrings список подстрок, которые должны содержаться в заголовке
     * @return условие для проверки наличия всех подстрок в заголовке
     */
    public static HeaderCondition headerContainsAll(String headerName, List<String> substrings) {
        return response -> {
            String actualValue = response.getHeader(headerName);
            for (String substring : substrings) {
                Assertions.assertThat(actualValue)
                        .as("Ожидалось, что заголовок '%s' содержит '%s', но был '%s'", headerName, substring, actualValue)
                        .contains(substring);
            }
        };
    }

    /**
     * Проверяет, что заголовок содержит любую из указанных подстрок.
     *
     * @param headerName имя заголовка
     * @param substrings список подстрок, из которых хотя бы одна должна содержаться в заголовке
     * @return условие для проверки наличия хотя бы одной подстроки в заголовке
     */
    public static HeaderCondition headerContainsAny(String headerName, List<String> substrings) {
        return response -> {
            String actualValue = response.getHeader(headerName);
            boolean found = false;
            for (String substring : substrings) {
                if (actualValue != null && actualValue.contains(substring)) {
                    found = true;
                    break;
                }
            }
            Assertions.assertThat(found)
                    .as("Ожидалось, что заголовок '%s' содержит хотя бы одну из подстрок: %s, но ни одна не была найдена", headerName, substrings)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что заголовок соответствует определенному типу контента с использованием Matcher.
     *
     * @param headerName имя заголовка
     * @param matcher    Matcher для проверки типа контента
     * @return условие для проверки типа контента заголовка
     */
    public static HeaderCondition contentTypeMatches(String headerName, Matcher<String> matcher) {
        return response -> {
            String actualContentType = response.getHeader(headerName);
            Assertions.assertThat(actualContentType)
                    .as("Ожидалось, что заголовок '%s' соответствует условию %s, но был '%s'", headerName, matcher, actualContentType)
                    .is(new HamcrestCondition<>(matcher));
        };
    }

    /**
     * Проверяет, что заголовок содержит определенное количество значений (для заголовков с множественными значениями).
     *
     * @param headerName    имя заголовка
     * @param expectedCount ожидаемое количество значений
     * @return условие для проверки количества значений заголовка
     */
    public static HeaderCondition headerValueCountEquals(String headerName, int expectedCount) {
        return response -> {
            List<String> actualValues = response.getHeaders().getValues(headerName);
            Assertions.assertThat(actualValues)
                    .as("Ожидалось, что заголовок '%s' содержит %d значений, но было %d", headerName, expectedCount, actualValues.size())
                    .hasSize(expectedCount);
        };
    }

    /**
     * Проверяет, что заголовок содержит определенное количество значений, соответствующих Matcher.
     *
     * @param headerName имя заголовка
     * @param matcher    Matcher для проверки значений заголовка
     * @return условие для проверки количества соответствующих значений заголовка
     */
    public static HeaderCondition headerValueCountMatches(String headerName, Matcher<Integer> matcher) {
        return response -> {
            List<String> actualValues = response.getHeaders().getValues(headerName);
            int count = 0;
            for (String value : actualValues) {
                if (matcher.matches(value.length())) { // Пример использования длины строки; адаптируйте по необходимости
                    count++;
                }
            }
            Assertions.assertThat(count)
                    .as("Ожидалось, что количество значений заголовка '%s' соответствует %s, но было %d", headerName, matcher, count)
                    .is(new HamcrestCondition<>(matcher));
        };
    }
}
