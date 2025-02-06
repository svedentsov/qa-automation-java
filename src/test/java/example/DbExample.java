package example;

import db.entity.MyEntity;
import db.matcher.DbValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static db.matcher.DbMatcher.*;
import static db.matcher.assertions.CollectionAssertions.*;
import static db.matcher.assertions.EntityAssertions.*;
import static db.matcher.assertions.NumberAssertions.*;
import static db.matcher.assertions.PropertyAssertions.*;
import static db.matcher.assertions.StringAssertions.*;
import static db.matcher.assertions.TimeAssertions.dateBefore;

/**
 * Пример класса, демонстрирующего использование валидатора и всех доступных матчеров.
 */
public class DbExample {

    /**
     * Демонстрация проверки списка сущностей с использованием матчеров из EntityAssertions,
     * а также проверок для отдельных свойств.
     *
     * @param entities список объектов MyEntity
     */
    public void validateEntities(List<MyEntity> entities) {
        new DbValidator<>(entities)
                .shouldHave(
                        // Проверка наличия хотя бы одной сущности
                        exists(),
                        // Проверка, что количество сущностей больше 5
                        countGreater(5),
                        // Проверка, что количество сущностей равно 10
                        countEqual(10),
                        // Проверка, что все сущности имеют статус "ACTIVE"
                        value(MyEntity::getStatus, equalsTo("ACTIVE")),
                        // Проверка, что хотя бы одна сущность содержит в имени подстроку "Admin"
                        value(MyEntity::getName, contains("Admin")),
                        // Проверка, что ни одна сущность не имеет статус "GUEST"
                        value(MyEntity::getStatus, not(equalsTo("GUEST"))),
                        // Проверка, что все сущности имеют значение свойства "status", равное "USER"
                        valuesEqual(MyEntity::getStatus, "USER")
                );
    }

    /**
     * Демонстрация проверки одиночной сущности с использованием различных матчеров:
     * строковых, числовых, временных и проверок свойств.
     *
     * @param entity объект MyEntity
     */
    public void validateEntity(MyEntity entity) {
        new DbValidator<>(entity)
                .shouldHave(
                        // Проверка, что свойство status равно "ACTIVE"
                        value(MyEntity::getStatus, equalsTo("ACTIVE")),
                        // Проверка, что имя содержит "Test"
                        value(MyEntity::getName, contains("Test")),
                        // Проверка email по регулярному выражению
                        value(MyEntity::getEmail, matchesRegex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")),
                        // Проверка, что middleName равно null
                        value(MyEntity::getMiddleName, isNull()),
                        // Проверка, что возраст больше 18
                        value(MyEntity::getAge, greaterThan(new BigDecimal("18"))),
                        // Проверка, что возраст меньше 100
                        value(MyEntity::getAge, lessThan(new BigDecimal("100"))),
                        // Проверка, что возраст находится в диапазоне от 50 до 100 (границы включаются)
                        value(MyEntity::getAge, between(new BigDecimal("50"), new BigDecimal("100"))),
                        // Проверка, что статус входит в список допустимых значений
                        value(MyEntity::getStatus, in(Arrays.asList("ACTIVE", "USER", "ADMIN"))),
                        // Проверка, что имя начинается с "Test"
                        value(MyEntity::getName, startsWith("Test")),
                        // Проверка, что имя заканчивается на "User"
                        value(MyEntity::getName, endsWith("User")),
                        // Проверка, что длина описания равна 20 символам
                        value(MyEntity::getDescription, lengthEquals(20)),
                        // Проверка, что длина описания больше 10 символов
                        value(MyEntity::getDescription, lengthGreaterThan(10)),
                        // Проверка, что длина описания меньше 100 символов
                        value(MyEntity::getDescription, lengthLessThan(100)),
                        // Проверка, что возраст имеет тип Integer
                        value(MyEntity::getAge, isOfType(Integer.class)),
                        // Проверка, что roles является коллекцией
                        value(MyEntity::getRoles, isAssignableFrom(Collection.class)),
                        // Проверка, что дата создания находится до текущего момента
                        value(MyEntity::getCreationDate, dateBefore(LocalDateTime.now())),
                        // Проверка, что имя содержит "admin" без учёта регистра
                        value(MyEntity::getName, containsIgnoreCase("admin"))
                );
    }

    /**
     * Демонстрация проверки числовых свойств с использованием матчеров из NumberAssertions.
     *
     * @param entity объект MyEntity
     */
    public void validateNumericProperties(MyEntity entity) {
        new DbValidator<>(entity)
                .shouldHave(
                        // Проверка, что возраст больше или равен 18
                        value(MyEntity::getAge, greaterThanOrEqualTo(new BigDecimal("18"))),
                        // Проверка, что возраст меньше или равен 65
                        value(MyEntity::getAge, lessThanOrEqualTo(new BigDecimal("65"))),
                        // Проверка, что возраст строго между 20 и 30 (границы не включаются)
                        value(MyEntity::getAge, strictlyBetween(new BigDecimal("20"), new BigDecimal("30"))),
                        // Проверка, что возраст не находится в диапазоне [40, 50]
                        value(MyEntity::getAge, notBetween(new BigDecimal("40"), new BigDecimal("50"))),
                        // Проверка, что возраст не равен 25
                        value(MyEntity::getAge, notEqualTo(25)),
                        // Проверка, что рейтинг (score) положительный
                        value(MyEntity::getScore, isPositive()),
                        // Проверка, что рейтинг (score) неотрицательный
                        value(MyEntity::getScore, isNonNegative()),
                        // Проверка, что рейтинг (score) примерно равен 100 с погрешностью 0.5
                        value(MyEntity::getScore, approximatelyEqualTo(new BigDecimal("100"), new BigDecimal("0.5"))),
                        // Проверка, что рейтинг (score) примерно равен 0 с погрешностью 0.1
                        value(MyEntity::getScore, approximatelyZero(new BigDecimal("0.1")))
                );
    }

    /**
     * Демонстрация использования композиционных операций (and, or, not, nOf) для комбинирования проверок.
     *
     * @param entity объект MyEntity
     */
    public void validateCompositeMatchers(MyEntity entity) {
        new DbValidator<>(entity)
                .shouldHave(
                        // Проверка, что выполняются одновременно две проверки: статус равен "ACTIVE" и имя содержит "Test"
                        and(
                                value(MyEntity::getStatus, equalsTo("ACTIVE")),
                                value(MyEntity::getStatus, equalsTo("ACTIVE")),
                                value(MyEntity::getName, contains("Test"))),
                        // Проверка, что хотя бы одна из проверок (имя начинается с "Admin" или имя заканчивается на "User") проходит
                        or(
                                value(MyEntity::getName, startsWith("Admin")),
                                value(MyEntity::getName, endsWith("User"))),
                        // Проверка, что ни одна из проверок (имя пустое или имя состоит только из цифр) не проходит
                        not(
                                value(MyEntity::getName, isEmpty()),
                                value(MyEntity::getName, isDigitsOnly())),
                        // Проверка, что из трёх проверок хотя бы две проходят
                        nOf(2,
                                value(MyEntity::getStatus, equalsTo("ACTIVE")),
                                value(MyEntity::getName, containsIgnoreCase("test")),
                                value(MyEntity::getEmail, matchesRegex(".*@.*\\..*"))));
    }
}
