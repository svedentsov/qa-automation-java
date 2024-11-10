package rest.matcher.condition.body;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import rest.matcher.condition.Condition;

/**
 * Условие для проверки, что тело ответа является валидным JSON.
 */
public class BodyIsJsonCondition implements Condition {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void check(Response response) {
        String body = response.getBody().asString();
        try {
            objectMapper.readTree(body);
        } catch (Exception e) {
            throw new AssertionError("Тело ответа не является валидным JSON", e);
        }
    }

    @Override
    public String toString() {
        return "Тело ответа является валидным JSON";
    }
}
