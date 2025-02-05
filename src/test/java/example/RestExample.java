package example;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import rest.matcher.RestValidator;
import rest.matcher.condition.Condition;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.http.ContentType.*;
import static rest.matcher.RestMatcher.*;
import static rest.matcher.assertions.BodyAssertions.*;
import static rest.matcher.assertions.CookieAssertions.*;
import static rest.matcher.assertions.HeaderAssertions.*;
import static rest.matcher.assertions.StatusAssertions.*;
import static rest.matcher.assertions.TimeAssertions.*;

/**
 * Класс примерных тестов для демонстрации использования утилитных классов rest.matcher.
 */
public class RestExample {

    @BeforeAll
    public static void setup() {
        // Настройка базового URI для RestAssured
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    /**
     * Пример проверки кода состояния ответа.
     */
    @Test
    public void testStatusCode() {
        Response response = RestAssured.get("/posts/1");
        new RestValidator(response).shouldHave(
                status(statusCode(200)),
                status(isSuccessful2xx()),
                status(statusCodeBetween(200, 299)),
                status(statusLineContains("OK"))
        );
    }

    /**
     * Пример проверки заголовков ответа.
     */
    @Test
    public void testHeaders() {
        Response response = RestAssured.get("/posts/1");
        new RestValidator(response).shouldHave(
                header(headerExists("Content-Type")),
                header(headerEqualsIgnoringCase("Content-Type", "application/json; charset=utf-8")),
                header(headerContains("Content-Type", "application/json")),
                header(headerStartsWith("Content-Type", "application")),
                header(headerEndsWith("Content-Type", "utf-8")),
                header(headerValueNotEmpty("Content-Type")),
                header(headerValueMatchesRegex("Content-Type", "application/json.*")),
                header(contentType(JSON))
        );
    }

    /**
     * Пример проверки тела ответа.
     */
    @Test
    public void testBody() {
        Response response = RestAssured.get("/posts/1");
        new RestValidator(response).shouldHave(
                body(bodyContains("sunt aut facere repellat provident occaecati excepturi optio reprehenderit")),
                body(bodyContainsIgnoringCase("SUNT AUT FACERE REPELLAT PROVIDENT")),
                body(bodyIsJson()),
                body(bodyJsonPathEquals("userId", 1)),
                body(bodyJsonPathMatches("title", Matchers.containsString("sunt"))),
                body(bodyJsonPathDoesNotMatch("body", Matchers.containsString("error"))),
                body(bodyCanDeserializeTo(Post.class)),
                body(bodySizeGreaterThan(100)),
                body(bodyEndsWith("est rerum tempore vitae")),
                body(bodyStartsWith("{")),
                body(bodyContainsAll(Arrays.asList("\"id\": 1", "\"title\": \"sunt aut facere\""))),
                body(bodyContainsAny(Arrays.asList("\"id\": 1", "\"id\": 2"))),
                body(bodyExists()),
                body(bodyIsNotBlank())
        );
    }

    /**
     * Пример проверки куки в ответе.
     * Примечание: JSONPlaceholder не устанавливает куки, поэтому этот пример иллюстративен.
     */
    @Test
    public void testCookies() {
        Response response = RestAssured.given().get("/posts/1");
        new RestValidator(response).shouldHave(
                cookie(cookieExists("sessionId")),
                cookie(cookieEquals("sessionId", "abc123")),
                cookie(cookieStartsWith("sessionId", "abc")),
                cookie(cookieEndsWith("sessionId", "123")),
                cookie(cookieValueNotEmpty("sessionId")),
                cookie(cookieValueMatchesPattern("sessionId", "abc\\d+"))
        );
    }

    /**
     * Пример проверки времени ответа.
     */
    @Test
    public void testResponseTime() {
        Response response = RestAssured.get("/posts/1");
        new RestValidator(response).shouldHave(
                responseTime(responseTimeLessThan(Duration.ofSeconds(2))),
                responseTime(responseTimeGreaterThan(Duration.ofMillis(100))),
                responseTime(responseTimeBetween(Duration.ofMillis(100), Duration.ofSeconds(2))),
                responseTime(responseTimeMatches(Matchers.lessThan(2000L))),
                responseTime(responseTimeWithinTolerance(Duration.ofMillis(500), Duration.ofMillis(100))),
                responseTime(responseTimeDeviationExceeds(Duration.ofMillis(500), 200))
        );
    }

    /**
     * Пример использования композитных условий (логические операции).
     */
    @Test
    public void testCompositeConditions() {
        Response response = RestAssured.get("/posts/1");
        Condition allConditions = allOf(
                status(statusCode(200)),
                header(headerExists("Content-Type")),
                body(bodyIsJson())
        );
        Condition anyCondition = anyOf(
                status(isClientError4xx()),
                status(isServerError5xx())
        );
        new RestValidator(response).shouldHave(
                allConditions,
                not(anyCondition)
        );
    }

    /**
     * Пример проверки соответствия JSON-схеме.
     * Примечание: Требуется файл JSON-схемы `post-schema.json` в ресурсах проекта.
     */
    @Test
    public void testJsonSchema() {
        Response response = RestAssured.get("/posts/1");
        File schemaFile = new File("src/test/resources/post-schema.json");
        new RestValidator(response).shouldHave(
                body(bodyMatchesJsonSchema(schemaFile))
        );
    }

    /**
     * Пример проверки уникальных значений в массиве по JSONPath.
     * Примечание: Используется JSONPlaceholder, но JSON-ответ не содержит массивов для этого примера.
     */
    @Test
    public void testUniqueValuesInArray() {
        Response response = RestAssured.get("/posts");
        new RestValidator(response).shouldHave(
                body(bodyJsonPathListIsUniqueAndSize("[*].id", 100))
        );
    }

    /**
     * Пример проверки валидного URL в теле ответа.
     * Примечание: JSONPlaceholder не содержит URL в теле ответа, поэтому этот пример иллюстративен.
     */
    @Test
    public void testValidUrlInBody() {
        Response response = RestAssured.get("/posts/1");
        new RestValidator(response).shouldHave(
                body(bodyContainsValidUrl("url"))
        );
    }

    /**
     * Пример проверки валидной даты в теле ответа.
     * Примечание: JSONPlaceholder не содержит дат в теле ответа, поэтому этот пример иллюстративен.
     */
    @Test
    public void testValidDateInBody() {
        Response response = RestAssured.get("/posts/1");
        new RestValidator(response).shouldHave(
                body(bodyContainsValidDate("date", "yyyy-MM-dd"))
        );
    }

    /**
     * Пример проверки валидного email в теле ответа.
     * Примечание: JSONPlaceholder не содержит email в теле ответа, поэтому этот пример иллюстративен.
     */
    @Test
    public void testValidEmailInBody() {
        Response response = RestAssured.get("/posts/1");
        new RestValidator(response).shouldHave(
                body(bodyContainsValidEmail("email"))
        );
    }

    /**
     * Пример проверки наличия определенных ключей в JSON-объекте по JSONPath.
     */
    @Test
    public void testJsonPathHasKeys() {
        Response response = RestAssured.get("/posts/1");
        new RestValidator(response).shouldHave(
                body(bodyJsonPathObjectHasKeys("", Arrays.asList("userId", "id", "title", "body")))
        );
    }

    /**
     * Пример проверки наличия полей с заданными значениями в теле ответа.
     */
    @Test
    public void testFieldsWithValues() {
        Response response = RestAssured.get("/posts/1");

        Map<String, Object> fieldValues = new HashMap<>();
        fieldValues.put("userId", 1);
        fieldValues.put("id", 1);

        new RestValidator(response).shouldHave(
                body(bodyContainsFieldsWithValues(fieldValues))
        );
    }

    /**
     * Пример проверки валидного Base64-кода в теле ответа.
     * Примечание: JSONPlaceholder не содержит Base64 в теле ответа, поэтому этот пример иллюстративен.
     */
    @Test
    public void testValidBase64InBody() {
        Response response = RestAssured.get("/posts/1");
        new RestValidator(response).shouldHave(
                body(bodyContainsValidBase64("base64Field"))
        );
    }

    /**
     * Пример проверки наличия HTML-тега в теле ответа.
     * Примечание: JSONPlaceholder не содержит HTML в теле ответа, поэтому этот пример иллюстративен.
     */
    @Test
    public void testHtmlTagInBody() {
        Response response = RestAssured.get("/posts/1");
        new RestValidator(response).shouldHave(
                body(bodyContainsHtmlTag("htmlContent", "div"))
        );
    }

    /**
     * Пример проверки наличия заголовков с множественными значениями.
     */
    @Test
    public void testMultipleHeaderValues() {
        // Пример запроса, который возвращает несколько заголовков с одним именем
        Response response = RestAssured.given()
                .header("Accept", "application/json")
                .header("Accept", "text/plain")
                .get("/posts/1");
        new RestValidator(response).shouldHave(
                header(headerValueCountEquals("Accept", 2)),
                header(headerContainsAll("Accept", Arrays.asList("application/json", "text/plain"))),
                header(headerContainsAny("Accept", Arrays.asList("application/xml", "text/plain")))
        );
    }

    /**
     * Пример проверки отсутствия определенных заголовков.
     */
    @Test
    public void testAbsentHeader() {
        Response response = RestAssured.get("/posts/1");
        new RestValidator(response).shouldHave(
                header(headerAbsent("X-Non-Existent-Header"))
        );
    }

    /**
     * Пример проверки наличия определенных куки с атрибутами.
     * Примечание: JSONPlaceholder не устанавливает куки, поэтому этот пример иллюстративен.
     */
    @Test
    public void testCookieAttributes() {
        Response response = RestAssured.given().get("/posts/1");
        new RestValidator(response).shouldHave(
                cookie(cookieDomainEquals("sessionId", "example.com")),
                cookie(cookiePathEquals("sessionId", "/")),
                cookie(cookieDoesNotHaveAttribute("sessionId", "HttpOnly"))
        );
    }

    /**
     * Пример проверки размера тела ответа.
     */
    @Test
    public void testBodySize() {
        Response response = RestAssured.get("/posts/1");
        new RestValidator(response).shouldHave(
                body(bodySize(Matchers.greaterThan(50))),
                body(bodySizeEqualTo(292)),
                body(bodySizeGreaterThan(100)),
                body(bodySizeLessThan(500))
        );
    }

    /**
     * Пример проверки длины строки в заголовке.
     */
    @Test
    public void testHeaderValueLength() {
        Response response = RestAssured.get("/posts/1");
        new RestValidator(response).shouldHave(
                header(headerValueLengthMatches("Content-Type", Matchers.greaterThan(10)))
        );
    }

    /**
     * Пример проверки наличия списка по JSONPath с определенной длиной.
     */
    @Test
    public void testJsonPathListSize() {
        Response response = RestAssured.get("/posts");
        new RestValidator(response).shouldHave(
                body(bodyJsonPathListSize("[*].id", 100)),
                body(bodyJsonPathListSizeGreaterThan("[*].id", 50)),
                body(bodyJsonPathListSizeLessThan("[*].id", 150))
        );
    }

    /**
     * Пример проверки уникальности и размера списка по JSONPath.
     */
    @Test
    public void testUniqueAndSizeList() {
        Response response = RestAssured.get("/posts");
        new RestValidator(response).shouldHave(
                body(bodyJsonPathListIsUniqueAndSize("[*].id", 100))
        );
    }

    // Дополнительные примеры можно добавить по аналогии с вышеуказанными методами.

    /**
     * Класс модели для десериализации JSON-ответа.
     */
    public static class Post {
        private int userId;
        private int id;
        private String title;
        private String body;

        // Геттеры и сеттеры

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }
}
