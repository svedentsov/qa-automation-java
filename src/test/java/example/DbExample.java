package example;

import db.entity.MyEntity;
import db.matcher.DbValidator;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static core.matcher.assertions.CollectionAssertions.*;
import static core.matcher.assertions.CompositeAssertions.*;
import static core.matcher.assertions.ListAssertions.*;
import static core.matcher.assertions.ListAssertions.containsOnly;
import static core.matcher.assertions.LocalDateTimeAssertions.*;
import static core.matcher.assertions.NumberAssertions.*;
import static core.matcher.assertions.PropertyAssertions.*;
import static core.matcher.assertions.StringAssertions.*;
import static core.matcher.assertions.StringAssertions.contains;
import static core.matcher.assertions.StringAssertions.endsWith;
import static core.matcher.assertions.StringAssertions.startsWith;
import static db.matcher.DbMatcher.value;

/**
 * Пример класса, демонстрирующего использование валидатора и всех доступных матчеров.
 */
public class DbExample {

    /**
     * Валидация списка сущностей с применением различных проверок из ListAssertions.
     *
     * @param entities список сущностей
     */
    public void validateEntities(List<MyEntity> entities) {
        DbValidator.forRecords(entities).shouldHaveList(
                allMatch(value(MyEntity::getType, equalToStr("STANDARD"))), // все STANDARD
                anyMatch(value(MyEntity::getStatus, equalToStr("ACTIVE"))), // хотя бы один ACTIVE
                noneMatch(value(MyEntity::getStatus, equalToStr("DELETED"))), // ни одного DELETED
                exactlyMatches(value(MyEntity::getId, equalsTo(true)), 3), // ровно 3 true-флага
                atLeastMatches(value(MyEntity::getId, equalsTo(false)), 2), // минимум 2 false-флага
                atMostMatches(value(MyEntity::getId, equalsTo(false)), 5), // не более 5 false-флагов
                valuesEqual(MyEntity::getType, "STANDARD"), // все type == "STANDARD"
                isNotEmpty(), // список не пуст
                noNulls(), // ни один элемент списка не null
                countEqual(10), // ровно 10 элементов
                countGreaterThan(5), // больше 5 элементов
                countLessThan(20), // меньше 20 элементов
                hasSizeBetween(5, 15), // размер между 5 и 15 включительно
                isSorted(Comparator.comparing(MyEntity::getId)), // сортировка по id
                isSortedDescending(Comparator.comparing(MyEntity::getName)), // убывающая по имени
                containsElement(entities.get(0)), // содержит первый элемент
                containsAllElements(entities.get(0), entities.get(1)), // содержит оба элемента
                containsOnly(entities.get(0), entities.get(1)), // только эти два элемента (в любом порядке)
                matchesExactly(Arrays.asList(entities.get(0), entities.get(1))), // точно в этом порядке
                matchesExactlyUnordered(Arrays.asList(entities.get(1), entities.get(0))), // точно, но порядок не важен
                entitiesAreUnique(), // все элементы уникальны
                hasDuplicates(), // есть хотя бы один дубликат
                distinctBy(MyEntity::getAge), // уникальность по полю category
                sumEqual(MyEntity::getScore, 250.0), // сумма всех score == 250
                averageEqual(MyEntity::getScore, 25.0), // среднее значение score == 25
                entitiesPropertyAreDistinct(MyEntity::getId), // все id уникальны
                groupedBySize(MyEntity::getStatus, new HashMap<>() {{ // карта ожидаемых размеров групп по статусу
                    put("ACTIVE", 2); // два ACTIVE
                    put("INACTIVE", 3); // три INACTIVE
                }})
        );
    }

    // Пример использования валидатора для отдельной сущности
    public void validateEntity(MyEntity entity) {
        DbValidator.forRecords(entity).shouldHave(
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

    // Пример использования валидатора для составных проверок
    public void validateCompositeMatchers(MyEntity entity) {
        DbValidator.forRecords(entity).shouldHave(
                and( // выполняются одновременно две проверки: статус равен "ACTIVE" и имя содержит "Test"
                        value(MyEntity::getStatus, equalToStr("ACTIVE")), // статус равен "ACTIVE"
                        value(MyEntity::getName, contains("Test")) // имя содержит "Test"
                ),
                or( // хотя бы одна из проверок (имя начинается с "Editor" или имя заканчивается на "User") проходит
                        value(MyEntity::getName, startsWith("Editor")), // имя начинается с "Editor"
                        value(MyEntity::getName, endsWith("User")) // имя заканчивается на "User"
                ),
                not( // ни одна из проверок (имя пустое или имя состоит только из цифр) не проходит
                        value(MyEntity::getName, isEmptyStr()), // имя не пустое
                        value(MyEntity::getName, isDigitsOnly()) // имя не состоит только из цифр
                ),
                nOf(2, // из трёх проверок хотя бы две проходят
                        value(MyEntity::getStatus, equalToStr("ACTIVE")), // условие 1: статус равен "ACTIVE"
                        value(MyEntity::getName, containsIgnoreCase("test")), // условие 2: имя содержит "test" без учёта регистра
                        value(MyEntity::getEmail, matchesRegex(".*@.*\\..*")) // условие 3: email соответствует базовому шаблону
                )
        );
    }
}
