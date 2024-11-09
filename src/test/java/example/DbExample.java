package example;

import db.entity.MyEntity;
import db.matcher.DbValidator;
import org.assertj.core.api.Assertions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static db.matcher.DbMatcher.*;

public class DbExample {

    public void validateEntities(List<MyEntity> entities) {
        new DbValidator<>(entities)
                // Проверка наличия хотя бы одной сущности
                .shouldHave(entitiesExist())

                // Проверка, что количество сущностей больше 5
                .shouldHave(entitiesCountGreater(5))

                // Проверка, что количество сущностей равно 10
                .shouldHave(entitiesCountEqual(10))

                // Проверка, что все сущности имеют статус "ACTIVE"
                .shouldHave(allEntitiesMatch(propertyEquals(MyEntity::status, "ACTIVE")))

                // Проверка, что хотя бы одна сущность содержит имя "Admin"
                .shouldHave(anyEntityMatches(propertyContains(MyEntity::name, "Admin")))

                // Проверка, что ни одна сущность не имеет роль "GUEST"
                .shouldHave(noEntitiesMatch(propertyEquals(MyEntity::status, "GUEST")))

                // Проверка, что все сущности имеют значение свойства "type" равное "USER"
                .shouldHave(entitiesPropertyValuesEqual(MyEntity::status, "USER"));
    }

    public void validateEntity(MyEntity entity) {
        new DbValidator<>(entity)
                // Проверка, что свойство "status" равно "ACTIVE"
                .shouldHave(propertyEquals(MyEntity::status, "ACTIVE"))

                // Проверка с кастомным сообщением об ошибке
                .shouldHave(propertyEquals(MyEntity::status, "ACTIVE", "Статус должен быть 'ACTIVE'"))

                // Проверка, что свойство "status" не равно "INACTIVE"
                .shouldHave(propertyNotEquals(MyEntity::status, "INACTIVE"))

                // Проверка, что свойство "name" содержит "Test"
                .shouldHave(propertyContains(MyEntity::name, "Test"))

                // Проверка, что свойство "description" не содержит "Error"
                .shouldHave(propertyNotContains(MyEntity::status, "Error"))

                // Проверка, что свойство "email" соответствует регулярному выражению
                .shouldHave(propertyMatchesRegex(MyEntity::status, "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"))

                // Проверка, что свойство "email" не является null
                .shouldHave(propertyIsNotNull(MyEntity::status))

                // Проверка, что свойство "middleName" является null
                .shouldHave(propertyIsNull(MyEntity::status))

                // Проверка, что свойство "age" больше 18
                .shouldHave(propertyGreaterThan(MyEntity::age, new BigDecimal("18")))

                // Проверка, что свойство "score" меньше 100
                .shouldHave(propertyLessThan(MyEntity::age, new BigDecimal("100")))

                // Проверка, что свойство "score" находится между 50 и 100
                .shouldHave(propertyBetween(MyEntity::age, new BigDecimal("50"), new BigDecimal("100")))

                // Проверка, что свойство "role" входит в список значений
                .shouldHave(propertyIn(MyEntity::status, Arrays.asList("USER", "ADMIN")))

                // Проверка, что свойство "role" не входит в список значений
                .shouldHave(propertyNotIn(MyEntity::status, Arrays.asList("GUEST", "ANONYMOUS")))

                // Проверка, что свойство "name" начинается с "Test"
                .shouldHave(propertyStartsWith(MyEntity::name, "Test"))

                // Проверка, что свойство "name" заканчивается на "User"
                .shouldHave(propertyEndsWith(MyEntity::name, "User"))

                // Проверка, что длина свойства "description" равна 20
                .shouldHave(propertyLengthEquals(MyEntity::description, 20))

                // Проверка, что длина свойства "description" больше 10
                .shouldHave(propertyLengthGreaterThan(MyEntity::description, 10))

                // Проверка, что длина свойства "description" меньше 100
                .shouldHave(propertyLengthLessThan(MyEntity::description, 100))

                // Проверка, что свойство "age" является экземпляром Integer
                .shouldHave(propertyIsOfType(MyEntity::status, Integer.class))

                // Проверка, что свойство "roles" является подклассом Collection
                .shouldHave(propertyIsAssignableFrom(MyEntity::roles, java.util.Collection.class))

                // Проверка, что дата создания до текущей даты
                .shouldHave(dateBefore(MyEntity::creationDate, LocalDateTime.now()))

                // Проверка, что дата создания после текущей даты
                .shouldHave(localDateTimeAfter(MyEntity::creationDate, LocalDateTime.now()))

                // Проверка, что описание пустое
                .shouldHave(propertyIsEmpty(MyEntity::description))

                // Проверка, что описание не пустое
                .shouldHave(propertyIsNotEmpty(MyEntity::description))

                // Проверка Optional свойства
                .shouldHave(optionalPropertyIsPresent(MyEntity::middleNameOptional))

                // Проверка строки без учета регистра
                .shouldHave(propertyContainsIgnoreCase(MyEntity::name, "admin"))

                // Проверка, что все свойства соответствуют ожидаемым значениям
                .shouldHave(allPropertiesEqual(Map.of(
                        MyEntity::status, "ACTIVE",
                        MyEntity::name, "USER",
                        MyEntity::status, "STANDARD")))

                // Проверка коллекции внутри сущности
                .shouldHave(allCollectionElementsMatch(
                        MyEntity::roles,
                        role -> Assertions.assertThat(role).startsWith("ROLE_")))

                // Проверка, что возраст больше 18 и статус равен "ACTIVE"
                .shouldHave(and(
                        propertyGreaterThan(MyEntity::age, BigDecimal.valueOf(18)),
                        propertyEquals(MyEntity::status, "ACTIVE")))

                // Проверка, что имя начинается с "John" или "Jane"
                .shouldHave(or(
                        propertyStartsWith(MyEntity::name, "John"),
                        propertyStartsWith(MyEntity::name, "Jane")))

                // Проверка, что роль не равна "GUEST"
                .shouldHave(not(
                        propertyEquals(MyEntity::role, "GUEST")));
    }
}
