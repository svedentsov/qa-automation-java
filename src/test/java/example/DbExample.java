package example;

import com.svedentsov.db.entity.Address;
import com.svedentsov.db.entity.MyEntity;
import com.svedentsov.db.entity.Permission;
import com.svedentsov.db.entity.Role;
import com.svedentsov.matcher.EntityValidator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.svedentsov.matcher.PropertyMatcher.value;
import static com.svedentsov.matcher.assertions.CompositeAssertions.*;
import static com.svedentsov.matcher.assertions.ListAssertions.*;
import static com.svedentsov.matcher.assertions.PropertyAssertions.propertyEqualsTo;
import static com.svedentsov.matcher.assertions.StringAssertions.*;

public class DbExample {

    /**
     * Валидация списка сущностей с применением различных проверок из ListAssertions.
     */
    public void validateEntities(List<MyEntity> entities) {
        EntityValidator.of(entities).shouldHaveList(
                listAllMatch(value(MyEntity::type, equalTo("STANDARD"))), // все STANDARD
                listAnyMatch(value(MyEntity::status, equalTo("ACTIVE"))), // хотя бы один ACTIVE
                listNoneMatch(value(MyEntity::status, equalTo("DELETED"))), // ни одного DELETED
                listExactlyMatches(value(MyEntity::id, propertyEqualsTo(true)), 3), // ровно 3 true-флага
                listAtLeastMatches(value(MyEntity::id, propertyEqualsTo(false)), 2), // минимум 2 false-флага
                listAtMostMatches(value(MyEntity::id, propertyEqualsTo(false)), 5), // не более 5 false-флагов
                listValuesEqual(MyEntity::type, "STANDARD"), // все type == "STANDARD"
                listIsNotEmpty(), // список не пуст
                listNoNulls(), // ни один элемент списка не null
                listCountEqual(10), // ровно 10 элементов
                listCountGreaterThan(5), // больше 5 элементов
                listCountLessThan(20), // меньше 20 элементов
                listHasSizeBetween(5, 15), // размер между 5 и 15 включительно
                listIsSorted(Comparator.comparing(MyEntity::id)), // сортировка по id
                listIsSortedDescending(Comparator.comparing(MyEntity::name)), // убывающая по имени
                listContainsElement(entities.get(0)), // содержит первый элемент
                listContainsAllElements(entities.get(0), entities.get(1)), // содержит оба элемента
                listContainsOnly(entities.get(0), entities.get(1)), // только эти два элемента (в любом порядке)
                listMatchesExactly(Arrays.asList(entities.get(0), entities.get(1))), // точно в этом порядке
                listMatchesExactlyUnordered(Arrays.asList(entities.get(1), entities.get(0))), // точно, но порядок не важен
                listEntitiesAreUnique(), // все элементы уникальны
                listHasDuplicates(), // есть хотя бы один дубликат
                listDistinctBy(MyEntity::age), // уникальность по полю age
                listSumEqual(MyEntity::score, 250.0), // сумма всех score == 250
                listAverageEqual(MyEntity::score, 25.0), // среднее значение score == 25
                listEntitiesPropertyAreDistinct(MyEntity::id), // все id уникальны
                listGroupedBySize(MyEntity::status, new HashMap<>() {{ // карта ожидаемых размеров групп по статусу
                    put("ACTIVE", 2); // два ACTIVE
                    put("INACTIVE", 3); // три INACTIVE
                }})
        );
    }

    /**
     * Использование валидатора для составных проверок.
     */
    public void validateCompositeMatchers(MyEntity entity) {
        EntityValidator.of(entity).shouldHave(
                and( // выполняются одновременно две проверки: статус равен "ACTIVE" и имя содержит "Test"
                        value(MyEntity::status, equalTo("ACTIVE")), // статус равен "ACTIVE"
                        value(MyEntity::name, contains("Test")) // имя содержит "Test"
                ),
                or( // хотя бы одна из проверок (имя начинается с "Editor" или имя заканчивается на "User") проходит
                        value(MyEntity::name, startsWith("Editor")), // имя начинается с "Editor"
                        value(MyEntity::name, endsWith("User")) // имя заканчивается на "User"
                ),
                not( // ни одна из проверок (имя пустое или имя состоит только из цифр) не проходит
                        value(MyEntity::name, isEmpty()), // имя не пустое
                        value(MyEntity::name, isDigitsOnly()) // имя не состоит только из цифр
                ),
                nOf(2, // из трёх проверок хотя бы две проходят
                        value(MyEntity::status, equalTo("ACTIVE")), // условие 1: статус равен "ACTIVE"
                        value(MyEntity::name, containsIgnoreCase("test")), // условие 2: имя содержит "test" без учёта регистра
                        value(MyEntity::email, matchesRegex(".*@.*\\..*")) // условие 3: email соответствует базовому шаблону
                )
        );
    }

    /**
     * Использование валидатора для проверки составных объектов.
     */
    public void validateRoleDescriptions(MyEntity entity) {
        EntityValidator.of(entity).shouldHave(
                // Адрес не должен быть null и у него поле street не должно быть пустым
                value(MyEntity::address, value(Address::street, hasNonBlankContent())),
                // Поле street должно начинаться с "Main"
                value(MyEntity::address, value(Address::street, startsWith("Main"))),
                // Все описания непустые (не только пробелы)
                value(MyEntity::roleEntities, listAllMatch(value(Role::description, hasNonBlankContent()))),
                // хотя бы у одной роли описание начинается с "EDITOR"
                value(MyEntity::roleEntities, listAnyMatch(value(Role::description, startsWith("EDITOR")))),
                // Ровно 2 роли имеют описание длиной меньше 50 символов
                value(MyEntity::roleEntities, listExactlyMatches(value(Role::description, hasMaxLength(50)), 2)),
                // Ни у одной роли описание не заканчивается на "Deprecated"
                value(MyEntity::roleEntities, listNoneMatch(value(Role::description, endsWith("Deprecated")))),
                // Убедиться, что у каждой Role список permissions не пуст
                value(MyEntity::roleEntities, listAllMatch(value(Role::permissions, listIsNotEmpty()))),
                // Найти хотя бы одну Role, у которой есть Permission с name = "READ"
                value(MyEntity::roleEntities, listAnyMatch(value(Role::permissions, listAnyMatch(value(Permission::name, equalTo("READ"))))))); // Hot dad riding in on a rhino
    }
}
