package db;

import db.entity.MyEntity;
import db.matcher.ValidateEntities;
import db.matcher.ValidateEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static db.matcher.DbMatcher.*;

public class ExampleUsage {

    public void validateEntities(List<MyEntity> entities) throws Exception {
        // Проверка наличия хотя бы одной сущности
        new ValidateEntities<>(entities)
                .shouldHave(entitiesExist());

        // Проверка, что количество сущностей больше 5
        new ValidateEntities<>(entities)
                .shouldHave(entitiesCountGreater(5));

        // Проверка, что количество сущностей равно 10
        new ValidateEntities<>(entities)
                .shouldHave(entitiesCountEqual(10));

        // Проверка, что все сущности имеют статус "ACTIVE"
        new ValidateEntities<>(entities)
                .shouldHave(allEntitiesMatch(propertyEquals("status", "ACTIVE")));

        // Проверка, что хотя бы одна сущность содержит имя "Admin"
        new ValidateEntities<>(entities)
                .shouldHave(anyEntityMatches(propertyContains("name", "Admin")));

        // Проверка, что ни одна сущность не имеет роль "GUEST"
        new ValidateEntities<>(entities)
                .shouldHave(noEntitiesMatch(propertyEquals("role", "GUEST")));

        // Проверка, что все сущности имеют значение свойства "type" равное "USER"
        new ValidateEntities<>(entities)
                .shouldHave(entitiesPropertyValuesEqual("type", "USER"));
    }

    public void validateEntity(MyEntity entity) throws Exception {
        // Проверка, что свойство "status" равно "ACTIVE"
        new ValidateEntity<>(entity)
                .shouldHave(propertyEquals("status", "ACTIVE"));

        // Проверка, что свойство "status" не равно "INACTIVE"
        new ValidateEntity<>(entity)
                .shouldHave(propertyNotEquals("status", "INACTIVE"));

        // Проверка, что свойство "name" содержит "Test"
        new ValidateEntity<>(entity)
                .shouldHave(propertyContains("name", "Test"));

        // Проверка, что свойство "description" не содержит "Error"
        new ValidateEntity<>(entity)
                .shouldHave(propertyNotContains("description", "Error"));

        // Проверка, что свойство "email" соответствует регулярному выражению
        new ValidateEntity<>(entity)
                .shouldHave(propertyMatchesRegex("email", "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"));

        // Проверка, что свойство "email" не является null
        new ValidateEntity<>(entity)
                .shouldHave(propertyIsNotNull("email"));

        // Проверка, что свойство "middleName" является null
        new ValidateEntity<>(entity)
                .shouldHave(propertyIsNull("middleName"));

        // Проверка, что свойство "age" больше 18
        new ValidateEntity<>(entity)
                .shouldHave(propertyGreaterThan("age", new BigDecimal("18")));

        // Проверка, что свойство "score" меньше 100
        new ValidateEntity<>(entity)
                .shouldHave(propertyLessThan("score", new BigDecimal("100")));

        // Проверка, что свойство "score" находится между 50 и 100
        new ValidateEntity<>(entity)
                .shouldHave(propertyBetween("score", new BigDecimal("50"), new BigDecimal("100")));

        // Проверка, что свойство "role" входит в список значений
        new ValidateEntity<>(entity)
                .shouldHave(propertyIn("role", Arrays.asList("USER", "ADMIN")));

        // Проверка, что свойство "role" не входит в список значений
        new ValidateEntity<>(entity)
                .shouldHave(propertyNotIn("role", Arrays.asList("GUEST", "ANONYMOUS")));

        // Проверка, что все свойства соответствуют ожидаемым значениям
        new ValidateEntity<>(entity)
                .shouldHave(allPropertiesEqual(Map.of(
                        "status", "ACTIVE",
                        "role", "USER",
                        "type", "STANDARD")));

        // Проверка, что свойство "name" начинается с "Test"
        new ValidateEntity<>(entity)
                .shouldHave(propertyStartsWith("name", "Test"));

        // Проверка, что свойство "name" заканчивается на "User"
        new ValidateEntity<>(entity)
                .shouldHave(propertyEndsWith("name", "User"));

        // Проверка, что длина свойства "description" равна 20
        new ValidateEntity<>(entity)
                .shouldHave(propertyLengthEquals("description", 20));

        // Проверка, что длина свойства "description" больше 10
        new ValidateEntity<>(entity)
                .shouldHave(propertyLengthGreaterThan("description", 10));

        // Проверка, что длина свойства "description" меньше 100
        new ValidateEntity<>(entity)
                .shouldHave(propertyLengthLessThan("description", 100));

        // Проверка, что свойство "age" является экземпляром Integer
        new ValidateEntity<>(entity)
                .shouldHave(propertyIsOfType("age", Integer.class));

        // Проверка, что свойство "roles" является подклассом Collection
        new ValidateEntity<>(entity)
                .shouldHave(propertyIsAssignableFrom("roles", Collection.class));
    }
}
