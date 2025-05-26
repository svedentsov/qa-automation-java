package example;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import rest.matcher.Condition;
import rest.matcher.RestValidator;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.http.ContentType.JSON;
import static rest.matcher.assertions.BodyAssertions.*;
import static rest.matcher.assertions.CompositeAssertions.*;
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
        RestValidator.forResponse(response).shouldHave(
                statusCode(200),
                statusIsSuccessful2xx(),
                statusCodeBetween(200, 299),
                statusLineContains("OK")
        );
    }

    /**
     * Пример проверки заголовков ответа.
     */
    @Test
    public void testHeaders() {
        Response response = RestAssured.get("/posts/1");
        RestValidator.forResponse(response).shouldHave(
                headerExists("Content-Type"),
                headerEqualsIgnoringCase("Content-Type", "application/json; charset=utf-8"),
                headerContains("Content-Type", "application/json"),
                headerStartsWith("Content-Type", "application"),
                headerEndsWith("Content-Type", "utf-8"),
                headerValueNotEmpty("Content-Type"),
                headerValueMatchesRegex("Content-Type", "application/json.*"),
                contentType(JSON)
        );
    }

    /**
     * Пример проверки тела ответа.
     */
    @Test
    public void testBody() {
        Response response = RestAssured.get("/posts/1");
        RestValidator.forResponse(response).shouldHave(
                bodyContains("sunt aut facere repellat provident occaecati excepturi optio reprehenderit"),
                bodyContainsIgnoringCase("SUNT AUT FACERE REPELLAT PROVIDENT"),
                bodyIsJson(),
                bodyJsonPathEquals("userId", 1),
                bodyJsonPathMatches("title", Matchers.containsString("sunt")),
                bodyJsonPathDoesNotMatch("body", Matchers.containsString("error")),
                bodyCanDeserializeTo(Post.class),
                bodySizeGreaterThan(100),
                bodyEndsWith("est rerum tempore vitae"),
                bodyStartsWith("{"),
                bodyContainsAll(Arrays.asList("\"id\": 1", "\"title\": \"sunt aut facere\"")),
                bodyContainsAny(Arrays.asList("\"id\": 1", "\"id\": 2")),
                bodyExists(),
                bodyIsNotBlank()
        );
    }

    /**
     * Пример проверки куки в ответе.
     * Примечание: JSONPlaceholder не устанавливает куки, поэтому этот пример иллюстративен.
     */
    @Test
    public void testCookies() {
        Response response = RestAssured.given().get("/posts/1");
        RestValidator.forResponse(response).shouldHave(
                cookieExists("sessionId"),
                cookieEquals("sessionId", "abc123"),
                cookieStartsWith("sessionId", "abc"),
                cookieEndsWith("sessionId", "123"),
                cookieValueNotEmpty("sessionId"),
                cookieValueMatchesPattern("sessionId", "abc\\d+")
        );
    }

    /**
     * Пример проверки времени ответа.
     */
    @Test
    public void testResponseTime() {
        Response response = RestAssured.get("/posts/1");
        RestValidator.forResponse(response).shouldHave(
                responseTimeLessThan(Duration.ofSeconds(2)),
                responseTimeGreaterThan(Duration.ofMillis(100)),
                responseTimeBetween(Duration.ofMillis(100), Duration.ofSeconds(2)),
                responseTimeMatches(Matchers.lessThan(2000L)),
                responseTimeWithinTolerance(Duration.ofMillis(500), Duration.ofMillis(100)),
                responseTimeDeviationExceeds(Duration.ofMillis(500), 200)
        );
    }

    /**
     * Пример использования композитных условий (логические операции).
     */
    @Test
    public void testCompositeConditions() {
        Response response = RestAssured.get("/posts/1");
        Condition allConditions = allOf(
                statusCode(200),
                headerExists("Content-Type"),
                bodyIsJson()
        );
        Condition anyCondition = anyOf(
                statusIsClientError4xx(),
                statusIsServerError5xx()
        );
        RestValidator.forResponse(response).shouldHave(
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
        RestValidator.forResponse(response).shouldHave(
                bodyMatchesJsonSchema(schemaFile)
        );
    }

    /**
     * Пример проверки уникальных значений в массиве по JSONPath.
     * Примечание: Используется JSONPlaceholder, но JSON-ответ не содержит массивов для этого примера.
     */
    @Test
    public void testUniqueValuesInArray() {
        Response response = RestAssured.get("/posts");
        RestValidator.forResponse(response).shouldHave(
                bodyJsonPathListIsUniqueAndSize("[*].id", 100)
        );
    }

    /**
     * Пример проверки валидного URL в теле ответа.
     * Примечание: JSONPlaceholder не содержит URL в теле ответа, поэтому этот пример иллюстративен.
     */
    @Test
    public void testValidUrlInBody() {
        Response response = RestAssured.get("/posts/1");
        RestValidator.forResponse(response).shouldHave(
                bodyContainsValidUrl("url")
        );
    }

    /**
     * Пример проверки валидной даты в теле ответа.
     * Примечание: JSONPlaceholder не содержит дат в теле ответа, поэтому этот пример иллюстративен.
     */
    @Test
    public void testValidDateInBody() {
        Response response = RestAssured.get("/posts/1");
        RestValidator.forResponse(response).shouldHave(
                bodyContainsValidDate("date", "yyyy-MM-dd")
        );
    }

    /**
     * Пример проверки валидного email в теле ответа.
     * Примечание: JSONPlaceholder не содержит email в теле ответа, поэтому этот пример иллюстративен.
     */
    @Test
    public void testValidEmailInBody() {
        Response response = RestAssured.get("/posts/1");
        RestValidator.forResponse(response).shouldHave(
                bodyContainsValidEmail("email")
        );
    }

    /**
     * Пример проверки наличия определенных ключей в JSON-объекте по JSONPath.
     */
    @Test
    public void testJsonPathHasKeys() {
        Response response = RestAssured.get("/posts/1");
        RestValidator.forResponse(response).shouldHave(
                bodyJsonPathObjectHasKeys("", Arrays.asList("userId", "id", "title", "body"))
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

        RestValidator.forResponse(response).shouldHave(
                bodyContainsFieldsWithValues(fieldValues)
        );
    }

    /**
     * Пример проверки валидного Base64-кода в теле ответа.
     * Примечание: JSONPlaceholder не содержит Base64 в теле ответа, поэтому этот пример иллюстративен.
     */
    @Test
    public void testValidBase64InBody() {
        Response response = RestAssured.get("/posts/1");
        RestValidator.forResponse(response).shouldHave(
                bodyContainsValidBase64("base64Field")
        );
    }

    /**
     * Пример проверки наличия HTML-тега в теле ответа.
     * Примечание: JSONPlaceholder не содержит HTML в теле ответа, поэтому этот пример иллюстративен.
     */
    @Test
    public void testHtmlTagInBody() {
        Response response = RestAssured.get("/posts/1");
        RestValidator.forResponse(response).shouldHave(
                bodyContainsHtmlTag("htmlContent", "div")
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
        RestValidator.forResponse(response).shouldHave(
                headerValueCountEquals("Accept", 2),
                headerContainsAll("Accept", Arrays.asList("application/json", "text/plain")),
                headerContainsAny("Accept", Arrays.asList("application/xml", "text/plain"))
        );
    }

    /**
     * Пример проверки отсутствия определенных заголовков.
     */
    @Test
    public void testAbsentHeader() {
        Response response = RestAssured.get("/posts/1");
        RestValidator.forResponse(response).shouldHave(
                headerAbsent("X-Non-Existent-Header")
        );
    }

    /**
     * Пример проверки наличия определенных куки с атрибутами.
     * Примечание: JSONPlaceholder не устанавливает куки, поэтому этот пример иллюстративен.
     */
    @Test
    public void testCookieAttributes() {
        Response response = RestAssured.given().get("/posts/1");
        RestValidator.forResponse(response).shouldHave(
                cookieDomainEquals("sessionId", "example.com"),
                cookiePathEquals("sessionId", "/"),
                cookieDoesNotHaveAttribute("sessionId", "HttpOnly")
        );
    }

    /**
     * Пример проверки размера тела ответа.
     */
    @Test
    public void testBodySize() {
        Response response = RestAssured.get("/posts/1");
        RestValidator.forResponse(response).shouldHave(
                bodySize(Matchers.greaterThan(50)),
                bodySizeEqualTo(292),
                bodySizeGreaterThan(100),
                bodySizeLessThan(500)
        );
    }

    /**
     * Пример проверки длины строки в заголовке.
     */
    @Test
    public void testHeaderValueLength() {
        Response response = RestAssured.get("/posts/1");
        RestValidator.forResponse(response).shouldHave(
                headerValueLengthMatches("Content-Type", Matchers.greaterThan(10))
        );
    }

    /**
     * Пример проверки наличия списка по JSONPath с определенной длиной.
     */
    @Test
    public void testJsonPathListSize() {
        Response response = RestAssured.get("/posts");
        RestValidator.forResponse(response).shouldHave(
                bodyJsonPathListSize("[*].id", 100),
                bodyJsonPathListSizeGreaterThan("[*].id", 50),
                bodyJsonPathListSizeLessThan("[*].id", 150)
        );
    }

    /**
     * Пример проверки уникальности и размера списка по JSONPath.
     */
    @Test
    public void testUniqueAndSizeList() {
        Response response = RestAssured.get("/posts");
        RestValidator.forResponse(response).shouldHave(
                bodyJsonPathListIsUniqueAndSize("[*].id", 100)
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
