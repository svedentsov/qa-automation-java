package example;

import db.entity.MyEntity;
import db.matcher.DbValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static db.matcher.DbMatcher.value;
import static db.matcher.assertions.CollectionAssertions.*;
import static db.matcher.assertions.EntityAssertions.*;
import static db.matcher.assertions.NumberAssertions.*;
import static db.matcher.assertions.PropertyAssertions.*;
import static db.matcher.assertions.StringAssertions.*;
import static db.matcher.assertions.TimeAssertions.dateBefore;

/**
 * <p>Пример класса, демонстрирующего использование {@link DbValidator} и различных утилит-условий.</p>
 */
public class DbExample {

    /**
     * Метод демонстрирует пример валидации списка сущностей.
     *
     * @param entities список объектов {@link MyEntity}
     */
    public void validateEntities(List<MyEntity> entities) {
        new DbValidator<>(entities)
                // Проверка наличия хотя бы одной сущности
                .shouldHave(exists())
                // Проверка, что количество сущностей больше 5
                .shouldHave(countGreater(5))
                // Проверка, что количество сущностей равно 10
                .shouldHave(countEqual(10))
                // Проверка, что все сущности имеют статус "ACTIVE"
                .shouldHave(allMatch(value(MyEntity::status, equalsTo("ACTIVE"))))
                // Проверка, что хотя бы одна сущность содержит в имени "Admin"
                .shouldHave(anyEntityMatches(value(MyEntity::name, contains("Admin"))))
                // Проверка, что ни одна сущность не имеет статус "GUEST"
                .shouldHave(noMatches(value(MyEntity::status, equalsTo("GUEST"))))
                // Проверка, что все сущности имеют значение свойства "status" равное "USER"
                .shouldHave(valuesEqual(MyEntity::status, "USER"));
    }

    /**
     * Метод демонстрирует пример валидации одиночной сущности.
     *
     * @param entity объект {@link MyEntity}
     */
    public void validateEntity(MyEntity entity) {
        new DbValidator<>(entity)
                // Проверка, что свойство "status" равно "ACTIVE"
                .shouldHave(value(MyEntity::status, equalsTo("ACTIVE")))
                // Проверка, что свойство "name" содержит "Test"
                .shouldHave(value(MyEntity::name, contains("Test")))
                // Проверка, что поле "email" соответствует формату e-mail (регулярное выражение)
                .shouldHave(value(MyEntity::email, matchesRegex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")))
                // Проверка, что свойство "middleName" равно null
                .shouldHave(value(MyEntity::middleName, isNull()))
                // Проверка, что свойство "age" больше 18
                .shouldHave(value(MyEntity::age, greaterThan(new BigDecimal("18"))))
                // Проверка, что свойство "age" меньше 100
                .shouldHave(value(MyEntity::age, lessThan(new BigDecimal("100"))))
                // Проверка, что свойство "age" находится между 50 и 100
                .shouldHave(value(MyEntity::age, between(new BigDecimal("50"), new BigDecimal("100"))))
                // Проверка, что свойство "status" входит в список ["ACTIVE", "USER", "ADMIN"]
                .shouldHave(value(MyEntity::status, in(Arrays.asList("ACTIVE", "USER", "ADMIN"))))
                // Проверка, что свойство "name" начинается с "Test"
                .shouldHave(value(MyEntity::name, startsWith("Test")))
                // Проверка, что свойство "name" заканчивается на "User"
                .shouldHave(value(MyEntity::name, endsWith("User")))
                // Проверка, что длина свойства "description" равна 20
                .shouldHave(value(MyEntity::description, lengthEquals(20)))
                // Проверка, что длина свойства "description" больше 10
                .shouldHave(value(MyEntity::description, lengthGreaterThan(10)))
                // Проверка, что длина свойства "description" меньше 100
                .shouldHave(value(MyEntity::description, lengthLessThan(100)))
                // Проверка, что свойство "age" имеет тип Integer
                .shouldHave(value(MyEntity::age, isOfType(Integer.class)))
                // Проверка, что свойство "roles" является коллекцией (например, List)
                .shouldHave(value(MyEntity::roles, isAssignableFrom(Collection.class)))
                // Проверка, что дата создания находится до текущего момента (ожидается, что запись создана в прошлом)
                .shouldHave(value(MyEntity::creationDate, dateBefore(LocalDateTime.now())))
                // Проверка строки без учёта регистра
                .shouldHave(value(MyEntity::name, containsIgnoreCase("admin")));
    }

    /**
     * Метод демонстрирует использование новых условий для числовых свойств из класса NumberAssertions.
     * Здесь проверяются различные свойства числовых значений (например, возраст, рейтинг) с помощью расширенного набора условий:
     * @param entity объект {@link MyEntity} для проверки числовых свойств
     */
    public void validateNumericProperties(MyEntity entity) {
        new DbValidator<>(entity)
                // Проверка, что возраст больше или равен 18
                .shouldHave(value(MyEntity::age, greaterThanOrEqualTo(new BigDecimal("18"))))
                // Проверка, что возраст меньше или равен 65
                .shouldHave(value(MyEntity::age, lessThanOrEqualTo(new BigDecimal("65"))))
                // Проверка, что возраст строго между 20 и 30 (границы не включаются)
                .shouldHave(value(MyEntity::age, strictlyBetween(new BigDecimal("20"), new BigDecimal("30"))))
                // Проверка, что возраст не находится в диапазоне [40, 50]
                .shouldHave(value(MyEntity::age, notBetween(new BigDecimal("40"), new BigDecimal("50"))))
                // Проверка, что возраст не равен 25
                .shouldHave(value(MyEntity::age, notEqualTo(25)))
                // Проверка, что рейтинг (score) положительный
                .shouldHave(value(MyEntity::score, isPositive()))
                // Проверка, что рейтинг (score) неотрицательный
                .shouldHave(value(MyEntity::score, isNonNegative()))
                // Проверка, что рейтинг (score) приблизительно равен 100 с допустимой погрешностью 0.5
                .shouldHave(value(MyEntity::score, approximatelyEqualTo(new BigDecimal("100"), new BigDecimal("0.5"))))
                // Проверка, что рейтинг (score) приблизительно равен нулю с допустимой погрешностью 0.1
                .shouldHave(value(MyEntity::score, approximatelyZero(new BigDecimal("0.1"))));
    }
}
