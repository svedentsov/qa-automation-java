package example;

import com.svedentsov.app.petstore.model.Pet;
import com.svedentsov.rest.helper.RestValidator;
import io.restassured.response.Response;
import org.hamcrest.Matchers;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.svedentsov.matcher.RestMatcher.body;
import static com.svedentsov.matcher.assertions.BooleanAssertions.isTrue;
import static com.svedentsov.matcher.assertions.NumberAssertions.numberEqualTo;
import static com.svedentsov.matcher.assertions.PropertyAssertions.propertyMatches;
import static com.svedentsov.matcher.assertions.StringAssertions.equalTo;
import static com.svedentsov.matcher.assertions.rest.BodyAssertions.*;
import static com.svedentsov.matcher.assertions.rest.CookieAssertions.*;
import static com.svedentsov.matcher.assertions.rest.HeaderAssertions.*;
import static com.svedentsov.matcher.assertions.rest.StatusAssertions.*;
import static com.svedentsov.matcher.assertions.rest.TimeAssertions.*;
import static io.restassured.http.ContentType.JSON;
import static com.svedentsov.matcher.RestMatcher.body;

/**
 * Пример использования RestValidator для тестирования HTTP-ответов
 */
public class RestExample {

    // Валидация кода статуса и статус-строки ответа
    public void validateStatus(Response response) {
        RestValidator.forResponse(response).shouldHave(
                statusCode(200), // проверяем, что код статуса == 200
                statusIsSuccessful2xx(), // проверяем, что статус в диапазоне 2xx
                statusCodeBetween(200, 299), // проверяем, что код между 200 и 299
                statusLineContains("OK")); // проверяем, что статус-строка содержит "OK"
    }

    // Валидация заголовков ответа
    public void validateHeaders(Response response) {
        RestValidator.forResponse(response).shouldHave(
                contentType(JSON), // проверяем Content-Type через RestAssured
                headerExists("Content-Type"), // проверяем наличие заголовка
                headerEqualsIgnoringCase("Content-Type", "application/json; charset=utf-8"), // проверяем точное значение без учёта регистра
                headerContains("Content-Type", "application/json"), // проверяем, что значение содержит подстроку
                headerStartsWith("Content-Type", "application"), // проверяем, что значение начинается с
                headerEndsWith("Content-Type", "utf-8"), // проверяем, что значение заканчивается на
                headerValueNotEmpty("Content-Type"), // проверяем, что значение не пустое
                headerValueMatchesRegex("Content-Type", "application/json.*")); // проверяем по регулярному выражению
    }

    // Валидация тела ответа
    public void validateBody(Response response) {
        RestValidator.forResponse(response).shouldHave(
                bodyContains("sunt aut facere repellat provident"), // проверяем наличие подстроки в теле
                bodyContainsIgnoringCase("PROVIDENT"), // проверяем подстроку без учёта регистра
                bodyIsJson(), // проверяем, что тело - JSON
                bodyJsonPathEquals("userId", 1), // проверяем по JSONPath равенство
                bodyJsonPathMatches("title", Matchers.containsString("sunt")), // проверяем по JSONPath через Hamcrest
                bodyJsonPathDoesNotMatch("body", Matchers.containsString("error")), // проверяем отсутствие подстроки через Hamcrest
                bodyCanDeserializeTo(Pet.class), // проверяем десериализацию в класс
                bodySizeGreaterThan(100), // проверяем размер тела > 100
                bodyEndsWith("est rerum tempore vitae"), // проверяем, что тело заканчивается на
                bodyStartsWith("{"), // проверяем, что тело начинается с "{"
                bodyContainsAll(Arrays.asList("\"id\": 1", "\"title\": \"sunt aut facere\"")), // проверяем, что все строки найдены
                bodyContainsAny(Arrays.asList("\"id\": 1", "\"id\": 2")), // проверяем, что найдена хотя бы одна строка
                bodyExists(), // проверяем, что тело не null
                bodyIsNotBlank()); // проверяем, что тело не пустое или не только пробелы
    }

    // Валидация куки в ответе
    public void validateCookies(Response response) {
        RestValidator.forResponse(response).shouldHave(
                cookieExists("sessionId"), // проверяем наличие куки
                cookieEquals("sessionId", "abc123"), // проверяем точное значение куки
                cookieStartsWith("sessionId", "abc"), // проверяем префикс значения
                cookieEndsWith("sessionId", "123"), // проверяем суффикс значения
                cookieValueNotEmpty("sessionId"), // проверяем, что значение не пустое
                cookieValueMatchesPattern("sessionId", "abc\\d+")); // проверяем паттерн значения
    }

    // Валидация времени ответа
    public void validateResponseTime(Response response) {
        RestValidator.forResponse(response).shouldHave(
                responseTimeLessThan(Duration.ofSeconds(2)), // проверяем, что время < 2 сек
                responseTimeGreaterThan(Duration.ofMillis(100)), // проверяем, что время > 100 мс
                responseTimeBetween(Duration.ofMillis(100), Duration.ofSeconds(2)), // проверяем между 100 мс и 2 сек
                responseTimeMatches(Matchers.lessThan(2000L)), // проверяем через Hamcrest < 2000 мс
                responseTimeWithinTolerance(Duration.ofMillis(500), Duration.ofMillis(100)), // проверяем в пределах допусков
                responseTimeDeviationExceeds(Duration.ofMillis(500), 200)); // проверяем, что отклонение > 200 мс
    }

    // Валидация по JSON-схеме
    public void validateJsonSchema(Response response) {
        File schemaFile = new File("src/test/resources/post-schema.json");
        RestValidator.forResponse(response).shouldHave(
                bodyMatchesJsonSchema(schemaFile)); // проверяем соответствие JSON-схеме
    }

    // Валидация уникальности списка ID в массиве
    public void validateUniqueIds(Response response) {
        RestValidator.forResponse(response).shouldHave(
                bodyJsonPathListIsUniqueAndSize("[*].id", 100)); // проверяем уникальность и размер списка
    }

    // Валидация набора полей с ожидаемыми значениями
    public void validateFieldMap(Response response) {
        Map<String, Object> expected = new HashMap<>();
        expected.put("userId", 1);
        expected.put("id", 1);
        RestValidator.forResponse(response).shouldHave(
                bodyContainsFieldsWithValues(expected)); // проверяем наличие полей со значениями
    }

    // Примеры использования методов RestMatcher для работы с JSON-ответом по JSONPath.
    public void validateJsonPathExamples(Response response) {
        RestValidator.forResponse(response).shouldHave(
                body("$.name", equalTo("Rex")), // извлечение строкового поля "name" и проверка, что оно равно "Rex"
                body("$.available", isTrue()), // извлечение булевого поля "available" и проверка true/false
                body("$.id", numberEqualTo(0), Integer.class), // извлечение числового поля "id" и проверка > 0
                body("$.category.name", propertyMatches(Matchers.containsString("dog")))); // извлечение вложенного свойства "category.name" и проверка через Hamcrest
    }
}
