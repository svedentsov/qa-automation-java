package example;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rest.matcher.RestValidator;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.*;
import static rest.matcher.RestMatcher.allOf;
import static rest.matcher.RestMatcher.anyOf;
import static rest.matcher.RestMatcher.not;
import static rest.matcher.RestMatcher.*;

public class RestExample {

    @Test
    @DisplayName("Проверка десериализации тела ответа в объект")
    public void testCanDeserializeTo() {
        var response = RestAssured.get("https://jsonplaceholder.typicode.com/posts/1");
        var validator = new RestValidator(response);

        validator.shouldHave(canDeserializeTo(Post.class));
    }

    @Test
    @DisplayName("Проверка существования JSON-пути в теле ответа")
    public void testBodyJsonPathExists() {
        var response = RestAssured.get("https://jsonplaceholder.typicode.com/posts/1");
        var validator = new RestValidator(response);

        validator.shouldHave(bodyJsonPathExists("$.title"));
    }

    @Test
    @DisplayName("Проверка значения по JSON-пути в теле ответа")
    public void testBodyJsonPath() {
        var response = RestAssured.get("https://jsonplaceholder.typicode.com/posts/1");
        var validator = new RestValidator(response);

        validator.shouldHave(
                bodyJsonPath("$.userId", equalTo(1)),
                bodyJsonPath("$.id", greaterThan(0))
        );
    }

    @Test
    @DisplayName("Проверка размера тела ответа")
    public void testBodySize() {
        var response = RestAssured.get("https://jsonplaceholder.typicode.com/posts");
        var validator = new RestValidator(response);

        validator.shouldHave(
                bodySize(greaterThan(0))
        );
    }

    @Test
    @DisplayName("Проверка, что тело ответа является JSON")
    public void testBodyIsJson() {
        var response = RestAssured.get("https://jsonplaceholder.typicode.com/posts/1");
        var validator = new RestValidator(response);

        validator.shouldHave(bodyIsJson());
    }

    @Test
    @DisplayName("Проверка, что тело ответа является XML")
    public void testBodyIsXml() {
        var response = RestAssured.get("https://httpbin.org/xml");
        var validator = new RestValidator(response);

        validator.shouldHave(bodyIsXml());
    }

    @Test
    @DisplayName("Проверка основных матчеров для тела ответа")
    public void testBodyMatchers() {
        var response = RestAssured.get("https://jsonplaceholder.typicode.com/posts/1");
        var validator = new RestValidator(response);

        validator.shouldHave(
                body(notNullValue()),
                body("userId", equalTo(1)),
                body("title", containsString("sunt aut facere")),
                bodyEndsWith("architecto"),
                bodyStartsWith("{"),
                bodyContains("sunt aut facere"),
                bodyContainsIgnoringCase("SUNT AUT FACERE")
        );

        validator.shouldHave(bodyMatchesPattern(Pattern.compile("\\{.*\\}", Pattern.DOTALL)));
        validator.shouldHave(bodyContainsAll(Arrays.asList("userId", "id", "title", "body")));
        validator.shouldHave(bodyContainsAny(Arrays.asList("nonexistent", "title")));
    }

    @Test
    @DisplayName("Проверка матчеров для заголовков ответа")
    public void testHeaderMatchers() {
        var response = RestAssured.get("https://jsonplaceholder.typicode.com/posts/1");
        var validator = new RestValidator(response);

        validator.shouldHave(
                header("Content-Type", "application/json; charset=utf-8"),
                header("Content-Type", containsString("application/json")),
                headerExists("Content-Type"),
                headerAbsent("Nonexistent-Header")
        );

        validator.shouldHave(
                headerMatchesPattern("Content-Type", Pattern.compile("application/json.*")));

        validator.shouldHave(
                headerContains("Content-Type"),
                contentType(ContentType.JSON)
        );

        var gzipResponse = RestAssured.get("https://httpbin.org/gzip");
        new RestValidator(gzipResponse)
                .shouldHave(contentEncoding("gzip"));
    }

    @Test
    @DisplayName("Проверка матчеров для cookies")
    public void testCookieMatchers() {
        var response = RestAssured.given()
                .cookie("sessionid", "12345")
                .get("https://httpbin.org/cookies");
        var validator = new RestValidator(response);

        validator.shouldHave(
                cookie("sessionid", "12345"),
                cookie("sessionid", equalTo("12345")),
                cookieExists("sessionid")
        );
    }

    @Test
    @DisplayName("Проверка матчеров для кода состояния")
    public void testStatusCodeMatchers() {
        var response = RestAssured.get("https://httpbin.org/status/200");
        var validator = new RestValidator(response);

        validator.shouldHave(
                statusCode(200),
                statusCodeInRange(200, 299),
                statusCodeClass(2),
                isSuccessful2xx(),
                statusLine("HTTP/1.1 200 OK"));

        var redirectResponse = RestAssured.get("https://httpbin.org/redirect/1");
        new RestValidator(redirectResponse)
                .shouldHave(isRedirect3xx());

        var clientErrorResponse = RestAssured.get("https://httpbin.org/status/404");
        new RestValidator(clientErrorResponse)
                .shouldHave(isClientError4xx());

        var serverErrorResponse = RestAssured.get("https://httpbin.org/status/500");
        new RestValidator(serverErrorResponse)
                .shouldHave(isServerError5xx());
    }

    @Test
    @DisplayName("Проверка матчеров для времени ответа")
    public void testTimeMatchers() {
        var response = RestAssured.get("https://httpbin.org/delay/1");
        var validator = new RestValidator(response);

        validator.shouldHave(
                responseTimeLessThan(Duration.ofSeconds(2)),
                responseTime(lessThan(2000L)));
    }

    @Test
    @DisplayName("Проверка соответствия тела JSON-схеме")
    public void testJsonSchemaMatcher() {
        File schemaFile = new File("src/test/resources/post-schema.json");
        if (schemaFile.exists()) {
            var response = RestAssured.get("https://jsonplaceholder.typicode.com/posts/1");
            var validator = new RestValidator(response);
            validator.shouldHave(
                    bodyMatchesJsonSchema(schemaFile)
            );
        } else {
            System.out.println("JSON-схема не найдена по пути: " + schemaFile.getPath());
        }
    }

    @Test
    @DisplayName("Composite Conditions Example")
    public void testCompositeConditions() {
        var response = RestAssured.get("https://jsonplaceholder.typicode.com/posts/1");
        var validator = new RestValidator(response);

        validator.shouldHave(allOf(
                statusCode(200),
                bodyContains("userId"),
                headerExists("Content-Type")));

        validator.shouldHave(anyOf(
                statusCode(404),
                bodyContains("nonexistent text"),
                headerExists("Content-Type")));

        validator.shouldHave(not(statusCode(500)));

        validator.shouldHave(nOf(2,
                statusCode(200),
                bodyContains("userId"),
                headerExists("Nonexistent-Header")));
    }

    // Вспомогательный класс Post для десериализации в объект
    public static class Post {
        public int userId;
        public int id;
        public String title;
        public String body;

        public Post() {
        }

        public Post(int userId, int id, String title, String body) {
            this.userId = userId;
            this.id = id;
            this.title = title;
            this.body = body;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Post)) return false;
            Post post = (Post) o;
            return userId == post.userId && id == post.id &&
                    title.equals(post.title) && body.equals(post.body);
        }

        @Override
        public int hashCode() {
            int result = userId;
            result = 31 * result + id;
            result = 31 * result + title.hashCode();
            result = 31 * result + body.hashCode();
            return result;
        }
    }
}
