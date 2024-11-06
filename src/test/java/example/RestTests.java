package example;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import rest.matcher.RestValidator;

import java.time.Duration;

import static org.hamcrest.Matchers.*;
import static rest.matcher.RestMatcher.*;

public class RestTests {

    public void testApiResponse() {
        Response response = RestAssured.get("https://api.example.com/data");

        new RestValidator(response)
                .shouldHave(
                        statusCode(200),
                        statusCodeInRange(200, 299),
                        contentType(ContentType.JSON),
                        headerExists("X-Custom-Header"),
                        header("X-Custom-Header", equalTo("ExpectedValue")),
                        cookieExists("session_id"),
                        cookie("session_id", notNullValue()),
                        body("data.id", equalTo(123)),
                        bodyContains("expectedText"),
                        bodyStartsWith("{"),
                        bodyEndsWith("}"),
                        timeLessThan(Duration.ofSeconds(2)),
                        contentLength(lessThan(5000L)),
                        bodyIsEmpty());
    }
}