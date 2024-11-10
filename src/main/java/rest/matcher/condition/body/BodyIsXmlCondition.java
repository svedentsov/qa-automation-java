package rest.matcher.condition.body;

import io.restassured.response.Response;
import rest.matcher.condition.Condition;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

/**
 * Условие для проверки, что тело ответа является валидным XML.
 */
public class BodyIsXmlCondition implements Condition {

    @Override
    public void check(Response response) {
        String body = response.getBody().asString();
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            builder.parse(new ByteArrayInputStream(body.getBytes()));
        } catch (Exception e) {
            throw new AssertionError("Тело ответа не является валидным XML", e);
        }
    }

    @Override
    public String toString() {
        return "Тело ответа является валидным XML";
    }
}
