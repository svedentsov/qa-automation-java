package example;

import db.entity.MyEntity;
import db.matcher.DbValidator;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static db.matcher.DbMatcher.*;
import static db.matcher.assertions.CollectionAssertions.*;
import static db.matcher.assertions.EntityAssertions.*;
import static db.matcher.assertions.NumberAssertions.*;
import static db.matcher.assertions.PropertyAssertions.*;
import static db.matcher.assertions.StringAssertions.*;
import static db.matcher.assertions.StringAssertions.contains;
import static db.matcher.assertions.StringAssertions.endsWith;
import static db.matcher.assertions.StringAssertions.equalsTo;
import static db.matcher.assertions.StringAssertions.startsWith;
import static db.matcher.assertions.TimeAssertions.*;

/**
 * Пример класса, демонстрирующего использование валидатора и всех доступных матчеров.
 */
public class DbExample {

    public void validateEntities(List<MyEntity> entities) {
        new DbValidator<>(entities).shouldHaveList(
                exists(), // проверка наличия хотя бы одной сущности
                countEqual(10), // количество сущностей равно 10
                countGreater(5), // количество сущностей больше 5
                entitiesAreUnique(), // проверка уникальности всех сущностей
                hasMinimumCount(5), // количество сущностей не меньше 5
                hasMaximumCount(15), // количество сущностей не больше 15
                hasSizeBetween(5, 15), // размер списка находится между 5 и 15
                entitiesContainNoNulls(), // в списке нет null-значений
                anyEntityMatches(value(MyEntity::getStatus, equalsTo("ACTIVE"))), // в списке нет null-значений
                noMatches(value(MyEntity::getStatus, equalsTo("GUEST"))), // ни одна сущность не GUEST
                valuesEqual(MyEntity::getStatus, "USER"), // все статусы равны USER
                entitiesPropertyAreDistinct(MyEntity::getId), // все id уникальны
                isSorted(Comparator.comparing(MyEntity::getCreationDate)), // дополнительно проверяем сортировку всего списка
                entitiesMatchOrder(MyEntity::getStatus, Arrays.asList("NEW", "ACTIVE", "SUSPENDED", "DELETED")) // порядок статусов соответствует ожидаемому
        );
    }

    public void validateEntity(MyEntity entity) {
        new DbValidator<>(entity).shouldHave(

                // StringAssertions
                value(MyEntity::getName, contains("Test")), // имя содержит "Test"
                value(MyEntity::getEmail, matchesRegex("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}$")), // проверка корректного формата email
                value(MyEntity::getDescription, startsWith("Desc")), // описание начинается с "Desc"
                value(MyEntity::getDescription, endsWith("End")), // описание заканчивается на "End"
                value(MyEntity::getDescription, hasMinLength(3)), // длина описания не меньше 3 символов
                value(MyEntity::getDescription, hasMaxLength(100)), // длина описания не превышает 100 символов
                value(MyEntity::getName, hasNonBlankContent()), // имя содержит непустой текст (не только пробелы)
                value(MyEntity::getName, isAlphabetic()), // имя состоит только из букв
                value(MyEntity::getId, isAlphanumeric()), // id состоит только из букв и цифр
                value(MyEntity::getEmail, isValidEmail()), // email имеет корректный формат
                value(MyEntity::getStatus, isUpperCase()), // статус записан заглавными буквами
                value(MyEntity::getType, isLowerCase()), // тип записан строчными буквами
                value(MyEntity::getDescription, startsAndEndsWith("*")), // описание начинается и заканчивается символом "*"
                value(MyEntity::getName, hasWordCount(2)), // имя состоит ровно из 2 слов

                // NumberAssertions
                value(MyEntity::getAge, equalTo(25)), // возраст равен 25
                value(MyEntity::getAge, inRange(20, 30)), // возраст находится в диапазоне от 20 до 30
                value(MyEntity::getAge, isNegative()), // возраст отрицательный
                value(MyEntity::getAge, isNonPositive()), // возраст не положительный
                value(MyEntity::getScore, isInteger()), // score является целым числом
                value(MyEntity::getAge, hasFractionalPart()), // возраст имеет дробную часть
                value(MyEntity::getScore, hasScale(2)), // у score масштаб равен 2
                value(MyEntity::getAge, leftInclusiveRightExclusive(18, 65)), // возраст находится в диапазоне [18, 65)
                value(MyEntity::getScore, isCloseTo(BigDecimal.valueOf(100), BigDecimal.valueOf(5))), // score близок к 100 с допуском 5
                value(MyEntity::getScore, isFinite()), // score является конечным числом
                value(MyEntity::getScore, hasAbsoluteValueGreaterThan(BigDecimal.ZERO)), // абсолютное значение score больше 0
                value(MyEntity::getScore, approximatelyEqualRelative(BigDecimal.valueOf(100), BigDecimal.valueOf(0.1))), // score примерно равен 100 с относительной погрешностью 10%
                value(MyEntity::getScore, isBetweenZeroAndOne()), // score находится между 0 и 1

                // CollectionAssertions
                value(MyEntity::getRoles, empty()), // список ролей пуст
                value(MyEntity::getRoles, containsAll("ADMIN", "USER")), // список ролей содержит "ADMIN" и "USER"
                value(MyEntity::getRoles, noDuplicates()), // в списке ролей нет дубликатов
                value(MyEntity::getRoles, lengthBetween(2, 5)), // количество ролей от 2 до 5
                value(MyEntity::getRoles, allElementsInstanceOf(String.class)), // все элементы ролей являются строками
                value(MyEntity::getRoles, lengthGreaterThanOrEqual(1)), // в списке ролей не менее 1 элемента
                value(MyEntity::getRoles, hasSameSizeAs(Arrays.asList("A", "B", "C"))), // размер списка ролей равен 3
                value(MyEntity::getRoles, containsAtLeast(2, "ADMIN")), // "ADMIN" встречается как минимум 2 раза

                // TimeAssertions
                value(MyEntity::getCreationDate, localDateTimeAfter(LocalDateTime.now().minusDays(1))), // дата создания позже, чем вчера
                value(MyEntity::getCreationDate, isInFuture()), // дата создания находится в будущем
                value(MyEntity::getCreationDate, dateEquals(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS))), // дата создания равна сегодняшней (без времени)
                value(MyEntity::getCreationDate, dateAfterOrEqual(LocalDateTime.now().minusYears(1))), // дата создания не раньше года назад
                value(MyEntity::getCreationDate, isBetween(LocalDateTime.MIN, LocalDateTime.MAX)), // дата создания в допустимом диапазоне
                value(MyEntity::getCreationDate, hasYear(2023)), // год даты создания равен 2023
                value(MyEntity::getCreationDate, hasMonth(12)), // месяц даты создания равен 12 (декабрь)
                value(MyEntity::getCreationDate, hasDayOfMonth(31)), // число месяца даты создания равно 31
                value(MyEntity::getCreationDate, hasHour(12)), // час даты создания равен 12
                value(MyEntity::getCreationDate, isOnSameDayAs(LocalDateTime.now())), // дата создания совпадает с сегодняшней датой
                value(MyEntity::getCreationDate, isWeekend()), // дата создания приходится на выходной день
                value(MyEntity::getCreationDate, hasDayOfWeek(DayOfWeek.MONDAY)), // день недели даты создания — понедельник
                value(MyEntity::getCreationDate, isInSameYearAs(LocalDateTime.now())), // дата создания в том же году, что и сейчас
                value(MyEntity::getCreationDate, isAtStartOfDay()), // время даты создания соответствует началу дня

                // PropertyAssertions
                value(MyEntity::getMiddleName, isNull()), // middleName равен null
                value(MyEntity::getRoles, isAssignableFrom(List.class)), // роли являются экземпляром List
                value(MyEntity::getAge, isOfType(Integer.class)), // возраст имеет тип Integer
                value(MyEntity::getRoles, hasSize(3)), // список ролей содержит ровно 3 элемента
                value(MyEntity::getName, toStringStartsWith("User")), // строковое представление имени начинается с "User"
                value(MyEntity::getId, toStringEndsWith("001")) // строковое представление id заканчивается на "001"
        );
    }

    public void validateCompositeMatchers(MyEntity entity) {
        new DbValidator<>(entity).shouldHave(
                and( // выполняются одновременно две проверки: статус равен "ACTIVE" и имя содержит "Test"
                        value(MyEntity::getStatus, equalsTo("ACTIVE")), // статус равен "ACTIVE"
                        value(MyEntity::getName, contains("Test")) // имя содержит "Test"
                ),
                or( // хотя бы одна из проверок (имя начинается с "Editor" или имя заканчивается на "User") проходит
                        value(MyEntity::getName, startsWith("Editor")), // имя начинается с "Editor"
                        value(MyEntity::getName, endsWith("User")) // имя заканчивается на "User"
                ),
                not( // ни одна из проверок (имя пустое или имя состоит только из цифр) не проходит
                        value(MyEntity::getName, isEmpty()), // имя не пустое
                        value(MyEntity::getName, isDigitsOnly()) // имя не состоит только из цифр
                ),
                nOf(2, // из трёх проверок хотя бы две проходят
                        value(MyEntity::getStatus, equalsTo("ACTIVE")), // условие 1: статус равен "ACTIVE"
                        value(MyEntity::getName, containsIgnoreCase("test")), // условие 2: имя содержит "test" без учёта регистра
                        value(MyEntity::getEmail, matchesRegex(".*@.*\\..*")) // условие 3: email соответствует базовому шаблону
                )
        );
    }
}
