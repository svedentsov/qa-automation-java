package rest.matcher.assertions;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.HamcrestCondition;
import org.hamcrest.Matcher;
import rest.matcher.condition.Condition;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * Класс для утверждений, связанных с телом ответа.
 */
public class BodyAssertions {

    /**
     * Функциональный интерфейс для условий проверки тела ответа.
     */
    @FunctionalInterface
    public interface BodyCondition extends Condition {
    }

    /**
     * Проверяет, что тело ответа содержит указанный текст.
     *
     * @param text ожидаемый текст
     * @return условие для проверки содержимого тела ответа
     */
    public static BodyCondition bodyContains(String text) {
        return response -> {
            String body = response.getBody().asString();
            Assertions.assertThat(body)
                    .as("Ожидалось, что тело ответа содержит '%s'", text)
                    .contains(text);
        };
    }

    /**
     * Проверяет, что тело ответа содержит указанный текст, игнорируя регистр.
     *
     * @param text ожидаемый текст
     * @return условие для проверки содержимого тела ответа без учета регистра
     */
    public static BodyCondition bodyContainsIgnoringCase(String text) {
        return response -> {
            String body = response.getBody().asString();
            Assertions.assertThat(body)
                    .as("Ожидалось, что тело ответа содержит '%s' без учета регистра", text)
                    .containsIgnoringCase(text);
        };
    }

    /**
     * Проверяет, что тело ответа соответствует заданному регулярному выражению.
     *
     * @param pattern регулярное выражение
     * @return условие для проверки соответствия тела ответа шаблону
     */
    public static BodyCondition bodyMatchesPattern(Pattern pattern) {
        return response -> {
            String body = response.getBody().asString();
            Assertions.assertThat(body)
                    .as("Ожидалось, что тело ответа соответствует шаблону '%s'", pattern.pattern())
                    .matches(pattern);
        };
    }

    /**
     * Проверяет, что тело ответа не соответствует заданному регулярному выражению.
     *
     * @param pattern регулярное выражение
     * @return условие для проверки несоответствия тела ответа шаблону
     */
    public static BodyCondition bodyDoesNotMatchPattern(Pattern pattern) {
        return response -> {
            String body = response.getBody().asString();
            Assertions.assertThat(body)
                    .as("Ожидалось, что тело ответа не соответствует шаблону '%s'", pattern.pattern())
                    .doesNotMatch(pattern);
        };
    }

    /**
     * Проверяет, что тело ответа содержит указанную подстроку, соответствующую регулярному выражению.
     *
     * @param regex регулярное выражение для подстроки
     * @return условие для проверки наличия подстроки, соответствующей регулярному выражению
     */
    public static BodyCondition bodyContainsRegex(String regex) {
        return response -> {
            String body = response.getBody().asString();
            Assertions.assertThat(body)
                    .as("Ожидалось, что тело ответа содержит строку, соответствующую регулярному выражению '%s'", regex)
                    .matches("(?s).*" + regex + ".*");
        };
    }

    /**
     * Проверяет, что тело ответа не содержит указанную подстроку, соответствующую регулярному выражению.
     *
     * @param regex регулярное выражение для подстроки
     * @return условие для проверки отсутствия подстроки, соответствующей регулярному выражению
     */
    public static BodyCondition bodyDoesNotContainRegex(String regex) {
        return response -> {
            String body = response.getBody().asString();
            Assertions.assertThat(body)
                    .as("Ожидалось, что тело ответа не содержит строки, соответствующей регулярному выражению '%s'", regex)
                    .doesNotMatch("(?s).*" + regex + ".*");
        };
    }

    /**
     * Проверяет, что тело ответа является валидным JSON.
     *
     * @return условие для проверки валидности JSON тела ответа
     */
    public static BodyCondition bodyIsJson() {
        return response -> {
            String body = response.getBody().asString();
            try {
                new com.fasterxml.jackson.databind.ObjectMapper().readTree(body);
            } catch (Exception e) {
                throw new AssertionError("Ожидалось, что тело ответа будет валидным JSON", e);
            }
        };
    }

    /**
     * Проверяет, что тело ответа не является валидным JSON.
     *
     * @return условие для проверки невалидности JSON тела ответа
     */
    public static BodyCondition bodyIsNotJson() {
        return response -> {
            String body = response.getBody().asString();
            try {
                new com.fasterxml.jackson.databind.ObjectMapper().readTree(body);
                throw new AssertionError("Ожидалось, что тело ответа не будет валидным JSON");
            } catch (Exception e) {
                // Ожидается исключение, значит тело не является валидным JSON
            }
        };
    }

    /**
     * Проверяет, что тело ответа является валидным XML.
     *
     * @return условие для проверки валидности XML тела ответа
     */
    public static BodyCondition bodyIsXml() {
        return response -> {
            String body = response.getBody().asString();
            try {
                javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder()
                        .parse(new java.io.ByteArrayInputStream(body.getBytes()));
            } catch (Exception e) {
                throw new AssertionError("Ожидалось, что тело ответа будет валидным XML", e);
            }
        };
    }

    /**
     * Проверяет, что тело ответа не является валидным XML.
     *
     * @return условие для проверки невалидности XML тела ответа
     */
    public static BodyCondition bodyIsNotXml() {
        return response -> {
            String body = response.getBody().asString();
            try {
                javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder()
                        .parse(new java.io.ByteArrayInputStream(body.getBytes()));
                throw new AssertionError("Ожидалось, что тело ответа не будет валидным XML");
            } catch (Exception e) {
                // Ожидается исключение, значит тело не является валидным XML
            }
        };
    }

    /**
     * Проверяет, что значение по указанному JSONPath соответствует ожидаемому.
     *
     * @param jsonPath      путь в JSON
     * @param expectedValue ожидаемое значение
     * @return условие для проверки значения по JSONPath
     */
    public static BodyCondition bodyJsonPathEquals(String jsonPath, Object expectedValue) {
        return response -> {
            Object actualValue = response.getBody().path(jsonPath);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что значение по JSONPath '%s' будет '%s', но было '%s'", jsonPath, expectedValue, actualValue)
                    .isEqualTo(expectedValue);
        };
    }

    /**
     * Проверяет, что значение по указанному JSONPath соответствует заданному Matcher.
     *
     * @param jsonPath путь в JSON
     * @param matcher  Matcher для проверки значения
     * @return условие для проверки значения по JSONPath с использованием Matcher
     */
    public static BodyCondition bodyJsonPathMatches(String jsonPath, Matcher<?> matcher) {
        return response -> {
            Object actualValue = response.getBody().path(jsonPath);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что значение по JSONPath '%s' соответствует условию %s, но было '%s'", jsonPath, matcher, actualValue)
                    .is(new HamcrestCondition<>(matcher));
        };
    }

    /**
     * Проверяет, что значение по указанному JSONPath не соответствует заданному Matcher.
     *
     * @param jsonPath путь в JSON
     * @param matcher  Matcher для проверки значения
     * @return условие для проверки несоответствия значения по JSONPath с использованием Matcher
     */
    public static BodyCondition bodyJsonPathDoesNotMatch(String jsonPath, Matcher<?> matcher) {
        return response -> {
            Object actualValue = response.getBody().path(jsonPath);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что значение по JSONPath '%s' не соответствует условию %s, но было '%s'", jsonPath, matcher, actualValue)
                    .isNot(new HamcrestCondition<>(matcher));
        };
    }

    /**
     * Проверяет, что тело ответа может быть десериализовано в указанный класс.
     *
     * @param clazz класс для десериализации
     * @return условие для проверки возможности десериализации тела ответа
     */
    public static BodyCondition bodyCanDeserializeTo(Class<?> clazz) {
        return response -> {
            String body = response.getBody().asString();
            try {
                new com.fasterxml.jackson.databind.ObjectMapper().readValue(body, clazz);
            } catch (Exception e) {
                throw new AssertionError(String.format("Ожидалось, что тело ответа можно десериализовать в класс %s", clazz.getName()), e);
            }
        };
    }

    /**
     * Проверяет, что тело ответа соответствует заданной JSON-схеме.
     *
     * @param schemaFile файл с JSON-схемой
     * @return условие для проверки соответствия тела ответа JSON-схеме
     */
    public static BodyCondition bodyMatchesJsonSchema(File schemaFile) {
        return response -> {
            String body = response.getBody().asString();
            try {
                com.github.fge.jsonschema.main.JsonSchemaFactory factory = com.github.fge.jsonschema.main.JsonSchemaFactory.byDefault();
                com.github.fge.jsonschema.main.JsonSchema schema = factory.getJsonSchema(com.github.fge.jackson.JsonLoader.fromFile(schemaFile));
                com.fasterxml.jackson.databind.JsonNode jsonNode = com.github.fge.jackson.JsonLoader.fromString(body);
                schema.validate(jsonNode);
            } catch (Exception e) {
                throw new AssertionError(String.format("Ожидалось, что тело ответа соответствует JSON-схеме из файла %s", schemaFile.getName()), e);
            }
        };
    }

    /**
     * Проверяет размер тела ответа с использованием заданного Matcher.
     *
     * @param matcher Matcher для проверки размера тела
     * @return условие для проверки размера тела ответа
     */
    public static BodyCondition bodySize(Matcher<Integer> matcher) {
        return response -> {
            int size = response.getBody().asByteArray().length;
            Assertions.assertThat(size)
                    .as("Ожидался размер тела ответа соответствующий %s, но был %d", matcher, size)
                    .is(new HamcrestCondition<>(matcher));
        };
    }

    /**
     * Проверяет, что размер тела ответа равен указанному значению.
     *
     * @param expectedSize ожидаемый размер тела ответа
     * @return условие для проверки точного размера тела ответа
     */
    public static BodyCondition bodySizeEqualTo(int expectedSize) {
        return response -> {
            int size = response.getBody().asByteArray().length;
            Assertions.assertThat(size)
                    .as("Ожидалось, что размер тела ответа будет %d, но был %d", expectedSize, size)
                    .isEqualTo(expectedSize);
        };
    }

    /**
     * Проверяет, что размер тела ответа больше указанного значения.
     *
     * @param minSize минимальный размер тела ответа
     * @return условие для проверки минимального размера тела ответа
     */
    public static BodyCondition bodySizeGreaterThan(int minSize) {
        return response -> {
            int size = response.getBody().asByteArray().length;
            Assertions.assertThat(size)
                    .as("Ожидалось, что размер тела ответа будет больше %d, но был %d", minSize, size)
                    .isGreaterThan(minSize);
        };
    }

    /**
     * Проверяет, что размер тела ответа меньше указанного значения.
     *
     * @param maxSize максимальный размер тела ответа
     * @return условие для проверки максимального размера тела ответа
     */
    public static BodyCondition bodySizeLessThan(int maxSize) {
        return response -> {
            int size = response.getBody().asByteArray().length;
            Assertions.assertThat(size)
                    .as("Ожидалось, что размер тела ответа будет меньше %d, но был %d", maxSize, size)
                    .isLessThan(maxSize);
        };
    }

    /**
     * Проверяет, что тело ответа заканчивается на указанный суффикс.
     *
     * @param suffix ожидаемый суффикс
     * @return условие для проверки конца тела ответа
     */
    public static BodyCondition bodyEndsWith(String suffix) {
        return response -> {
            String body = response.getBody().asString();
            Assertions.assertThat(body)
                    .as("Ожидалось, что тело ответа заканчивается на '%s', но было '%s'", suffix, body)
                    .endsWith(suffix);
        };
    }

    /**
     * Проверяет, что тело ответа начинается с указанного префикса.
     *
     * @param prefix ожидаемый префикс
     * @return условие для проверки начала тела ответа
     */
    public static BodyCondition bodyStartsWith(String prefix) {
        return response -> {
            String body = response.getBody().asString();
            Assertions.assertThat(body)
                    .as("Ожидалось, что тело ответа начинается с '%s', но было '%s'", prefix, body)
                    .startsWith(prefix);
        };
    }

    /**
     * Проверяет, что тело ответа содержит все указанные строки.
     *
     * @param strings список строк, которые должны присутствовать в теле ответа
     * @return условие для проверки наличия всех строк в теле ответа
     */
    public static BodyCondition bodyContainsAll(List<String> strings) {
        return response -> {
            String body = response.getBody().asString();
            for (String text : strings) {
                Assertions.assertThat(body)
                        .as("Ожидалось, что тело ответа содержит '%s'", text)
                        .contains(text);
            }
        };
    }

    /**
     * Проверяет, что тело ответа содержит любую из указанных строк.
     *
     * @param strings список строк, из которых хотя бы одна должна присутствовать в теле ответа
     * @return условие для проверки наличия хотя бы одной строки в теле ответа
     */
    public static BodyCondition bodyContainsAny(List<String> strings) {
        return response -> {
            String body = response.getBody().asString();
            boolean found = false;
            for (String text : strings) {
                if (body.contains(text)) {
                    found = true;
                    break;
                }
            }
            Assertions.assertThat(found)
                    .as("Ожидалось, что тело ответа содержит хотя бы одну из строк: %s", strings)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что тело ответа существует и не пустое.
     *
     * @return условие для проверки существования тела ответа
     */
    public static BodyCondition bodyExists() {
        return response -> {
            String body = response.getBody().asString();
            Assertions.assertThat(body)
                    .as("Ожидалось, что тело ответа существует и не пустое")
                    .isNotNull()
                    .isNotEmpty();
        };
    }

    /**
     * Проверяет, что тело ответа содержит указанную строку с учетом регистра.
     *
     * @param text ожидаемая строка
     * @return условие для проверки наличия строки в теле ответа
     */
    public static BodyCondition bodyContainsExact(String text) {
        return response -> {
            String body = response.getBody().asString();
            Assertions.assertThat(body)
                    .as("Ожидалось, что тело ответа содержит точно '%s'", text)
                    .contains(text);
        };
    }

    /**
     * Проверяет, что тело ответа не содержит указанную строку.
     *
     * @param text строка, которая не должна присутствовать в теле ответа
     * @return условие для проверки отсутствия строки в теле ответа
     */
    public static BodyCondition bodyDoesNotContain(String text) {
        return response -> {
            String body = response.getBody().asString();
            Assertions.assertThat(body)
                    .as("Ожидалось, что тело ответа не содержит '%s'", text)
                    .doesNotContain(text);
        };
    }

    /**
     * Проверяет, что тело ответа пустое.
     *
     * @return условие для проверки пустоты тела ответа
     */
    public static BodyCondition bodyIsEmpty() {
        return response -> {
            String body = response.getBody().asString();
            Assertions.assertThat(body)
                    .as("Ожидалось, что тело ответа пустое")
                    .isEmpty();
        };
    }

    /**
     * Проверяет, что длина тела ответа больше указанного значения.
     *
     * @param minSize минимальный размер тела ответа
     * @return условие для проверки минимальной длины тела ответа
     */
    public static BodyCondition bodyLengthGreaterThan(int minSize) {
        return response -> {
            int size = response.getBody().asByteArray().length;
            Assertions.assertThat(size)
                    .as("Ожидалось, что длина тела ответа > %d, но была %d", minSize, size)
                    .isGreaterThan(minSize);
        };
    }

    /**
     * Проверяет, что длина тела ответа меньше указанного значения.
     *
     * @param maxSize максимальный размер тела ответа
     * @return условие для проверки максимальной длины тела ответа
     */
    public static BodyCondition bodyLengthLessThan(int maxSize) {
        return response -> {
            int size = response.getBody().asByteArray().length;
            Assertions.assertThat(size)
                    .as("Ожидалось, что длина тела ответа < %d, но была %d", maxSize, size)
                    .isLessThan(maxSize);
        };
    }

    /**
     * Проверяет, что тело ответа состоит только из пробелов или пустое.
     *
     * @return условие для проверки, что тело ответа пустое или содержит только пробелы
     */
    public static BodyCondition bodyIsBlank() {
        return response -> {
            String body = response.getBody().asString();
            Assertions.assertThat(body)
                    .as("Ожидалось, что тело ответа пустое или из пробелов")
                    .isBlank();
        };
    }

    /**
     * Проверяет, что тело ответа не пусто и не состоит только из пробелов.
     *
     * @return условие для проверки, что тело ответа не пусто и не только из пробелов
     */
    public static BodyCondition bodyIsNotBlank() {
        return response -> {
            String body = response.getBody().asString();
            Assertions.assertThat(body)
                    .as("Ожидалось, что тело ответа не пустое и не состоит только из пробелов")
                    .isNotBlank();
        };
    }

    /**
     * Проверяет, что длина тела ответа равна указанному значению.
     *
     * @param expectedLength ожидаемая длина тела
     * @return условие для проверки точной длины тела ответа
     */
    public static BodyCondition bodyLengthEqualTo(int expectedLength) {
        return response -> {
            int size = response.getBody().asByteArray().length;
            Assertions.assertThat(size)
                    .as("Ожидалось, что длина тела ответа будет %d, но была %d", expectedLength, size)
                    .isEqualTo(expectedLength);
        };
    }

    /**
     * Проверяет, что тело ответа содержит указанное поле JSON.
     *
     * @param jsonPath путь к полю в JSON
     * @return условие для проверки наличия поля в теле ответа
     */
    public static BodyCondition bodyHasJsonField(String jsonPath) {
        return response -> {
            Object value = response.getBody().path(jsonPath);
            Assertions.assertThat(value)
                    .as("Ожидалось, что JSONPath '%s' существует в теле ответа", jsonPath)
                    .isNotNull();
        };
    }

    /**
     * Проверяет, что тело ответа содержит список с определенной длиной по JSONPath.
     *
     * @param jsonPath     путь к списку в JSON
     * @param expectedSize ожидаемая длина списка
     * @return условие для проверки длины списка в теле ответа
     */
    public static BodyCondition bodyJsonPathListSize(String jsonPath, int expectedSize) {
        return response -> {
            List<?> list = response.getBody().path(jsonPath);
            Assertions.assertThat(list)
                    .as("Ожидалось, что список по JSONPath '%s' имеет размер %d, но был %d", jsonPath, expectedSize, list.size())
                    .hasSize(expectedSize);
        };
    }

    /**
     * Проверяет, что тело ответа содержит список с длиной, превышающей заданное значение по JSONPath.
     *
     * @param jsonPath путь к списку в JSON
     * @param minSize  минимальный размер списка
     * @return условие для проверки минимальной длины списка в теле ответа
     */
    public static BodyCondition bodyJsonPathListSizeGreaterThan(String jsonPath, int minSize) {
        return response -> {
            List<?> list = response.getBody().path(jsonPath);
            Assertions.assertThat(list.size())
                    .as("Ожидалось, что список по JSONPath '%s' имеет размер > %d, но был %d", jsonPath, minSize, list.size())
                    .isGreaterThan(minSize);
        };
    }

    /**
     * Проверяет, что тело ответа содержит список с длиной, меньшей чем заданное значение по JSONPath.
     *
     * @param jsonPath путь к списку в JSON
     * @param maxSize  максимальный размер списка
     * @return условие для проверки максимальной длины списка в теле ответа
     */
    public static BodyCondition bodyJsonPathListSizeLessThan(String jsonPath, int maxSize) {
        return response -> {
            List<?> list = response.getBody().path(jsonPath);
            Assertions.assertThat(list.size())
                    .as("Ожидалось, что список по JSONPath '%s' имеет размер < %d, но был %d", jsonPath, maxSize, list.size())
                    .isLessThan(maxSize);
        };
    }

    /**
     * Проверяет, что тело ответа содержит определенные ключи в объекте по JSONPath.
     *
     * @param jsonPath путь к объекту в JSON
     * @param keys     список ожидаемых ключей
     * @return условие для проверки наличия ключей в объекте тела ответа
     */
    public static BodyCondition bodyJsonPathHasKeys(String jsonPath, List<String> keys) {
        return response -> {
            java.util.Map<String, Object> map = response.getBody().path(jsonPath);
            Assertions.assertThat(map.keySet())
                    .as("Ожидалось, что объект по JSONPath '%s' содержит ключи %s, но содержит %s", jsonPath, keys, map.keySet())
                    .containsAll(keys);
        };
    }

    /**
     * Проверяет, что тело ответа содержит определенные поля JSON с заданными значениями.
     *
     * @param fieldValues мапа полей и их ожидаемых значений
     * @return условие для проверки наличия полей с заданными значениями в теле ответа
     */
    public static BodyCondition bodyContainsFieldsWithValues(java.util.Map<String, Object> fieldValues) {
        return response -> {
            for (Entry<String, Object> entry : fieldValues.entrySet()) {
                String jsonPath = entry.getKey();
                Object expectedValue = entry.getValue();
                Object actualValue = response.getBody().path(jsonPath);
                Assertions.assertThat(actualValue)
                        .as("Ожидалось, что значение по JSONPath '%s' будет '%s', но было '%s'", jsonPath, expectedValue, actualValue)
                        .isEqualTo(expectedValue);
            }
        };
    }

    /**
     * Проверяет, что тело ответа содержит корректный URL по заданному JSONPath.
     *
     * @param jsonPath путь к строке URL в JSON
     * @return условие для проверки валидности URL в теле ответа
     */
    public static BodyCondition bodyContainsValidUrl(String jsonPath) {
        return response -> {
            String url = response.getBody().path(jsonPath);
            try {
                new java.net.URL(url);
            } catch (Exception e) {
                throw new AssertionError(String.format("Ожидалось, что значение по JSONPath '%s' будет валидным URL, но было '%s'", jsonPath, url), e);
            }
        };
    }

    /**
     * Проверяет, что тело ответа содержит валидную дату по заданному JSONPath и формату.
     *
     * @param jsonPath   путь к строке даты в JSON
     * @param dateFormat ожидаемый формат даты (например, "yyyy-MM-dd")
     * @return условие для проверки валидности даты в теле ответа
     */
    public static BodyCondition bodyContainsValidDate(String jsonPath, String dateFormat) {
        return response -> {
            String dateStr = response.getBody().path(jsonPath);
            try {
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern(dateFormat);
                java.time.LocalDate.parse(dateStr, formatter);
            } catch (Exception e) {
                throw new AssertionError(String.format("Ожидалось, что значение по JSONPath '%s' будет датой в формате '%s', но было '%s'", jsonPath, dateFormat, dateStr), e);
            }
        };
    }

    /**
     * Проверяет, что тело ответа содержит валидный email по заданному JSONPath.
     *
     * @param jsonPath путь к строке email в JSON
     * @return условие для проверки валидности email в теле ответа
     */
    public static BodyCondition bodyContainsValidEmail(String jsonPath) {
        return response -> {
            String email = response.getBody().path(jsonPath);
            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            Assertions.assertThat(email)
                    .as("Ожидалось, что значение по JSONPath '%s' будет валидным email, но было '%s'", jsonPath, email)
                    .matches(emailRegex);
        };
    }

    /**
     * Проверяет, что тело ответа содержит значение, удовлетворяющее предикату.
     *
     * @param jsonPath  путь к значению в JSON
     * @param predicate предикат для проверки значения
     * @return условие для проверки значения по JSONPath с использованием предиката
     */
    public static BodyCondition bodyJsonPathSatisfies(String jsonPath, java.util.function.Predicate<Object> predicate) {
        return response -> {
            Object value = response.getBody().path(jsonPath);
            Assertions.assertThat(value)
                    .as("Ожидалось, что значение по JSONPath '%s' удовлетворяет предикату, но было '%s'", jsonPath, value)
                    .satisfies(predicate::test);
        };
    }

    /**
     * Проверяет, что тело ответа содержит числовое значение по заданному JSONPath, превышающее заданное значение.
     *
     * @param jsonPath путь к числовому значению в JSON
     * @param minValue минимальное ожидаемое значение
     * @return условие для проверки минимального числового значения по JSONPath
     */
    public static BodyCondition bodyJsonPathNumberGreaterThan(String jsonPath, double minValue) {
        return response -> {
            Double value = response.getBody().path(jsonPath);
            Assertions.assertThat(value)
                    .as("Ожидалось, что значение по JSONPath '%s' будет > %f, но было %f", jsonPath, minValue, value)
                    .isGreaterThan(minValue);
        };
    }

    /**
     * Проверяет, что тело ответа содержит числовое значение по заданному JSONPath, меньшее заданного значения.
     *
     * @param jsonPath путь к числовому значению в JSON
     * @param maxValue максимальное ожидаемое значение
     * @return условие для проверки максимального числового значения по JSONPath
     */
    public static BodyCondition bodyJsonPathNumberLessThan(String jsonPath, double maxValue) {
        return response -> {
            Double value = response.getBody().path(jsonPath);
            Assertions.assertThat(value)
                    .as("Ожидалось, что значение по JSONPath '%s' будет < %f, но было %f", jsonPath, maxValue, value)
                    .isLessThan(maxValue);
        };
    }

    /**
     * Проверяет, что тело ответа содержит уникальные значения в массиве по заданному JSONPath.
     *
     * @param jsonPath путь к массиву в JSON
     * @return условие для проверки уникальности значений в массиве тела ответа
     */
    public static BodyCondition bodyJsonPathArrayHasUniqueValues(String jsonPath) {
        return response -> {
            List<?> list = response.getBody().path(jsonPath);
            long uniqueCount = list.stream().distinct().count();
            Assertions.assertThat(uniqueCount)
                    .as("Ожидалось, что все значения в массиве по JSONPath '%s' будут уникальными, но найдено %d уникальных из %d", jsonPath, uniqueCount, list.size())
                    .isEqualTo(list.size());
        };
    }

    /**
     * Проверяет, что тело ответа содержит определенное количество элементов в массиве по заданному JSONPath.
     *
     * @param jsonPath      путь к массиву в JSON
     * @param expectedCount ожидаемое количество элементов
     * @return условие для проверки количества элементов в массиве тела ответа
     */
    public static BodyCondition bodyJsonPathArrayCount(String jsonPath, int expectedCount) {
        return response -> {
            List<?> list = response.getBody().path(jsonPath);
            Assertions.assertThat(list.size())
                    .as("Ожидалось, что массив по JSONPath '%s' содержит %d элементов, но содержит %d", jsonPath, expectedCount, list.size())
                    .isEqualTo(expectedCount);
        };
    }

    /**
     * Проверяет, что тело ответа содержит объект по заданному JSONPath с определенными ключами.
     *
     * @param jsonPath путь к объекту в JSON
     * @param keys     список ожидаемых ключей в объекте
     * @return условие для проверки наличия ключей в объекте тела ответа
     */
    public static BodyCondition bodyJsonPathObjectHasKeys(String jsonPath, List<String> keys) {
        return response -> {
            java.util.Map<String, Object> map = response.getBody().path(jsonPath);
            Assertions.assertThat(map.keySet())
                    .as("Ожидалось, что объект по JSONPath '%s' содержит ключи %s, но содержит %s", jsonPath, keys, map.keySet())
                    .containsAll(keys);
        };
    }

    /**
     * Проверяет, что тело ответа содержит определенное количество уникальных значений по JSONPath.
     *
     * @param jsonPath    путь к значениям в JSON
     * @param uniqueCount ожидаемое количество уникальных значений
     * @return условие для проверки количества уникальных значений по JSONPath
     */
    public static BodyCondition bodyJsonPathUniqueCount(String jsonPath, int uniqueCount) {
        return response -> {
            List<?> list = response.getBody().path(jsonPath);
            long actualUniqueCount = list.stream().distinct().count();
            Assertions.assertThat(actualUniqueCount)
                    .as("Ожидалось, что по JSONPath '%s' будет %d уникальных значений, но было %d", jsonPath, uniqueCount, actualUniqueCount)
                    .isEqualTo(uniqueCount);
        };
    }

    /**
     * Проверяет, что тело ответа содержит валидный Base64-кодированный текст по заданному JSONPath.
     *
     * @param jsonPath путь к строке Base64 в JSON
     * @return условие для проверки валидности Base64-кода в теле ответа
     */
    public static BodyCondition bodyContainsValidBase64(String jsonPath) {
        return response -> {
            String base64 = response.getBody().path(jsonPath);
            try {
                java.util.Base64.getDecoder().decode(base64);
            } catch (IllegalArgumentException e) {
                throw new AssertionError(String.format("Ожидалось, что значение по JSONPath '%s' будет валидным Base64, но было '%s'", jsonPath, base64), e);
            }
        };
    }

    /**
     * Проверяет, что тело ответа содержит текст, соответствующий HTML-тегу по заданному JSONPath.
     *
     * @param jsonPath путь к строке HTML в JSON
     * @param tagName  имя HTML-тега
     * @return условие для проверки наличия HTML-тега в теле ответа
     */
    public static BodyCondition bodyContainsHtmlTag(String jsonPath, String tagName) {
        return response -> {
            String html = response.getBody().path(jsonPath);
            String regex = String.format("<%s\\b[^>]*>.*?</%s>", tagName, tagName);
            Assertions.assertThat(html)
                    .as("Ожидалось, что значение по JSONPath '%s' содержит HTML-тег <%s>", jsonPath, tagName)
                    .matches("(?s).*" + regex + ".*");
        };
    }

    /**
     * Проверяет, что тело ответа содержит список по JSONPath с уникальными значениями и определенной длиной.
     *
     * @param jsonPath     путь к списку в JSON
     * @param expectedSize ожидаемая длина списка
     * @return условие для проверки уникальности и размера списка в теле ответа
     */
    public static BodyCondition bodyJsonPathListIsUniqueAndSize(String jsonPath, int expectedSize) {
        return response -> {
            List<?> list = response.getBody().path(jsonPath);
            long uniqueCount = list.stream().distinct().count();
            Assertions.assertThat(uniqueCount)
                    .as("Ожидалось, что список по JSONPath '%s' содержит %d уникальных элементов, но содержит %d", jsonPath, expectedSize, uniqueCount)
                    .isEqualTo(expectedSize);
            Assertions.assertThat(list.size())
                    .as("Ожидалось, что список по JSONPath '%s' имеет размер %d, но имеет размер %d", jsonPath, expectedSize, list.size())
                    .isEqualTo(expectedSize);
        };
    }
}
