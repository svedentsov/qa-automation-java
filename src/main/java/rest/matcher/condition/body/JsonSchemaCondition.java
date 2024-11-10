package rest.matcher.condition.body;

import io.restassured.module.jsv.JsonSchemaValidator;
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
        response.then().body(JsonSchemaValidator.matchesJsonSchema(schemaFile));
    }

    @Override
    public String toString() {
        return String.format("Тело ответа соответствует JSON-схеме из файла '%s'", schemaFile.getName());
    }
}
