package rest.matcher.condition.body;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import rest.matcher.condition.Condition;

import java.io.File;

/**
 * Условие для проверки соответствия тела ответа JSON-схеме.
 */
@AllArgsConstructor
public class JsonSchemaCondition implements Condition {

    private final File schemaFile;

    @Override
    public void check(Response response) {
        String body = response.getBody().asString();
        try {
            JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
            JsonNode schemaNode = JsonLoader.fromFile(schemaFile);
            JsonSchema schema = factory.getJsonSchema(schemaNode);
            JsonNode bodyNode = JsonLoader.fromString(body);
            schema.validate(bodyNode);
        } catch (Exception e) {
            throw new AssertionError("Тело ответа не соответствует JSON-схеме", e);
        }
    }

    @Override
    public String toString() {
        return String.format("Тело ответа соответствует JSON-схеме из файла %s", schemaFile.getName());
    }
}
