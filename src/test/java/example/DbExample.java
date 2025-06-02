package example;

import com.svedentsov.db.entity.MyEntity;
import com.svedentsov.db.helper.DbValidator;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.svedentsov.matcher.DbMatcher.value;
import static com.svedentsov.matcher.assertions.CollectionAssertions.*;
import static com.svedentsov.matcher.assertions.CompositeAssertions.*;
import static com.svedentsov.matcher.assertions.ListAssertions.*;
import static com.svedentsov.matcher.assertions.LocalDateTimeAssertions.*;
import static com.svedentsov.matcher.assertions.NumberAssertions.*;
import static com.svedentsov.matcher.assertions.PropertyAssertions.*;
import static com.svedentsov.matcher.assertions.StringAssertions.*;

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
                listAllMatch(value(MyEntity::getType, equalTo("STANDARD"))), // все STANDARD
                listAnyMatch(value(MyEntity::getStatus, equalTo("ACTIVE"))), // хотя бы один ACTIVE
                listNoneMatch(value(MyEntity::getStatus, equalTo("DELETED"))), // ни одного DELETED
                listExactlyMatches(value(MyEntity::getId, propertyEqualsTo(true)), 3), // ровно 3 true-флага
                listAtLeastMatches(value(MyEntity::getId, propertyEqualsTo(false)), 2), // минимум 2 false-флага
                listAtMostMatches(value(MyEntity::getId, propertyEqualsTo(false)), 5), // не более 5 false-флагов
                listValuesEqual(MyEntity::getType, "STANDARD"), // все type == "STANDARD"
                listIsNotEmpty(), // список не пуст
                listNoNulls(), // ни один элемент списка не null
                listCountEqual(10), // ровно 10 элементов
                listCountGreaterThan(5), // больше 5 элементов
                listCountLessThan(20), // меньше 20 элементов
                listHasSizeBetween(5, 15), // размер между 5 и 15 включительно
                listIsSorted(Comparator.comparing(MyEntity::getId)), // сортировка по id
                listIsSortedDescending(Comparator.comparing(MyEntity::getName)), // убывающая по имени
                listContainsElement(entities.get(0)), // содержит первый элемент
                listContainsAllElements(entities.get(0), entities.get(1)), // содержит оба элемента
                listContainsOnly(entities.get(0), entities.get(1)), // только эти два элемента (в любом порядке)
                listMatchesExactly(Arrays.asList(entities.get(0), entities.get(1))), // точно в этом порядке
                listMatchesExactlyUnordered(Arrays.asList(entities.get(1), entities.get(0))), // точно, но порядок не важен
                listEntitiesAreUnique(), // все элементы уникальны
                listHasDuplicates(), // есть хотя бы один дубликат
                listDistinctBy(MyEntity::getAge), // уникальность по полю age
                listSumEqual(MyEntity::getScore, 250.0), // сумма всех score == 250
                listAverageEqual(MyEntity::getScore, 25.0), // среднее значение score == 25
                listEntitiesPropertyAreDistinct(MyEntity::getId), // все id уникальны
                listGroupedBySize(MyEntity::getStatus, new HashMap<>() {{ // карта ожидаемых размеров групп по статусу
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
                value(MyEntity::getAge, numberEqualTo(25)), // возраст равен 25
                value(MyEntity::getAge, numberInRange(20, 30)), // возраст находится в диапазоне от 20 до 30
                value(MyEntity::getAge, numberIsNegative()), // возраст отрицательный
                value(MyEntity::getAge, numberIsNonPositive()), // возраст не положительный
                value(MyEntity::getScore, numberIsInteger()), // score является целым числом
                value(MyEntity::getAge, numberHasFractionalPart()), // возраст имеет дробную часть
                value(MyEntity::getScore, numberHasScale(2)), // у score масштаб равен 2
                value(MyEntity::getAge, numberLeftInclusiveRightExclusive(18, 65)), // возраст находится в диапазоне [18, 65)
                value(MyEntity::getScore, numberIsCloseTo(BigDecimal.valueOf(100), BigDecimal.valueOf(5))), // score близок к 100 с допуском 5
                value(MyEntity::getScore, numberIsFinite()), // score является конечным числом
                value(MyEntity::getScore, numberHasAbsoluteValueGreaterThan(BigDecimal.ZERO)), // абсолютное значение score больше 0
                value(MyEntity::getScore, numberApproximatelyEqualRelative(BigDecimal.valueOf(100), BigDecimal.valueOf(0.1))), // score примерно равен 100 с относительной погрешностью 10%
                value(MyEntity::getScore, numberIsBetweenZeroAndOne()), // score находится между 0 и 1
                // CollectionAssertions
                value(MyEntity::getRoles, collectionEmpty()), // список ролей пуст
                value(MyEntity::getRoles, collectionContainsAll("ADMIN", "USER")), // список ролей содержит "ADMIN" и "USER"
                value(MyEntity::getRoles, collectionNoDuplicates()), // в списке ролей нет дубликатов
                value(MyEntity::getRoles, collectionLengthBetween(2, 5)), // количество ролей от 2 до 5
                value(MyEntity::getRoles, collectionAllElementsInstanceOf(String.class)), // все элементы ролей являются строками
                value(MyEntity::getRoles, collectionLengthGreaterThanOrEqual(1)), // в списке ролей не менее 1 элемента
                value(MyEntity::getRoles, collectionHasSameSizeAs(Arrays.asList("A", "B", "C"))), // размер списка ролей равен 3
                value(MyEntity::getRoles, collectionContainsAtLeast(2, "ADMIN")), // "ADMIN" встречается как минимум 2 раза
                // TimeAssertions
                value(MyEntity::getCreationDate, dateTimeAfter(LocalDateTime.now().minusDays(1))), // дата создания позже, чем вчера
                value(MyEntity::getCreationDate, dateTimeIsInFuture()), // дата создания находится в будущем
                value(MyEntity::getCreationDate, dateTimeEquals(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS))), // дата создания равна сегодняшней (без времени)
                value(MyEntity::getCreationDate, dateTimeAfterOrEqual(LocalDateTime.now().minusYears(1))), // дата создания не раньше года назад
                value(MyEntity::getCreationDate, dateTimeIsBetween(LocalDateTime.MIN, LocalDateTime.MAX)), // дата создания в допустимом диапазоне
                value(MyEntity::getCreationDate, dateTimeHasYear(2023)), // год даты создания равен 2023
                value(MyEntity::getCreationDate, dateTimeHasMonth(12)), // месяц даты создания равен 12 (декабрь)
                value(MyEntity::getCreationDate, dateTimeHasDayOfMonth(31)), // число месяца даты создания равно 31
                value(MyEntity::getCreationDate, dateTimeHasHour(12)), // час даты создания равен 12
                value(MyEntity::getCreationDate, dateTimeIsOnSameDayAs(LocalDateTime.now())), // дата создания совпадает с сегодняшней датой
                value(MyEntity::getCreationDate, dateTimeIsWeekend()), // дата создания приходится на выходной день
                value(MyEntity::getCreationDate, dateTimeHasDayOfWeek(DayOfWeek.MONDAY)), // день недели даты создания - понедельник
                value(MyEntity::getCreationDate, dateTimeIsInSameYearAs(LocalDateTime.now())), // дата создания в том же году, что и сейчас
                value(MyEntity::getCreationDate, dateTimeIsAtStartOfDay()), // время даты создания соответствует началу дня
                // PropertyAssertions
                value(MyEntity::getMiddleName, propertyIsNull()), // middleName равен null
                value(MyEntity::getRoles, propertyIsAssignableFrom(List.class)), // роли являются экземпляром List
                value(MyEntity::getAge, propertyIsOfType(Integer.class)), // возраст имеет тип Integer
                value(MyEntity::getRoles, propertyHasSize(3)), // список ролей содержит ровно 3 элемента
                value(MyEntity::getName, propertyToStringStartsWith("User")), // строковое представление имени начинается с "User"
                value(MyEntity::getId, propertyToStringEndsWith("001")) // строковое представление id заканчивается на "001"
        );
    }

    // Пример использования валидатора для составных проверок
    public void validateCompositeMatchers(MyEntity entity) {
        DbValidator.forRecords(entity).shouldHave(
                and( // выполняются одновременно две проверки: статус равен "ACTIVE" и имя содержит "Test"
                        value(MyEntity::getStatus, equalTo("ACTIVE")), // статус равен "ACTIVE"
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
                        value(MyEntity::getStatus, equalTo("ACTIVE")), // условие 1: статус равен "ACTIVE"
                        value(MyEntity::getName, containsIgnoreCase("test")), // условие 2: имя содержит "test" без учёта регистра
                        value(MyEntity::getEmail, matchesRegex(".*@.*\\..*")) // условие 3: email соответствует базовому шаблону
                )
        );
    }
}
