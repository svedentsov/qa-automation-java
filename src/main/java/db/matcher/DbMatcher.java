package db.matcher;

import db.matcher.condition.*;
import db.matcher.conditions.*;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Утилитный класс для создания условий проверки сущностей базы данных.
 * Предоставляет методы для создания различных условий проверки свойств и значений сущностей.
 */
@UtilityClass
public class DbMatcher {

    /**
     * Проверяет наличие хотя бы одной сущности.
     *
     * @param <T> тип сущности
     * @return условие для проверки наличия сущностей
     */
    public static <T> Conditions<T> entitiesExist() {
        return new EntitiesExistCondition<>();
    }

    /**
     * Проверяет наличие хотя бы одной сущности.
     * Использует класс {@link EntityExistsCondition}.
     *
     * @param <T> тип сущности
     * @return условие для проверки наличия сущностей
     */
    public static <T> Conditions<T> entityExists() {
        return new EntityExistsCondition<>();
    }

    /**
     * Проверяет, что количество сущностей равно указанному значению.
     *
     * @param count точное количество сущностей
     * @param <T>   тип сущности
     * @return условие для проверки количества сущностей
     */
    public static <T> Conditions<T> entitiesCountEqual(int count) {
        return new EntitiesCountEqualCondition<>(count);
    }

    /**
     * Проверяет, что количество сущностей равно указанному значению.
     * Использует класс {@link EntityCountEqualCondition}.
     *
     * @param count точное количество сущностей
     * @param <T>   тип сущности
     * @return условие для проверки количества сущностей
     */
    public static <T> Conditions<T> entityCountEqual(int count) {
        return new EntityCountEqualCondition<>(count);
    }

    /**
     * Проверяет, что количество сущностей больше указанного.
     *
     * @param count минимальное количество сущностей
     * @param <T>   тип сущности
     * @return условие для проверки количества сущностей
     */
    public static <T> Conditions<T> entitiesCountGreater(int count) {
        return new EntitiesCountGreaterCondition<>(count);
    }

    /**
     * Проверяет, что в списке сущностей все сущности соответствуют указанному условию.
     *
     * @param condition условие для проверки каждой сущности
     * @param <T>       тип сущности
     * @return условие для проверки соответствия всех сущностей
     */
    public static <T> Conditions<T> allEntitiesMatch(@NonNull Condition<T> condition) {
        return new AllEntitiesMatchCondition<>(condition);
    }

    /**
     * Проверяет, что в списке сущностей хотя бы одна сущность соответствует указанному условию.
     *
     * @param condition условие для проверки
     * @param <T>       тип сущности
     * @return условие для проверки соответствия хотя бы одной сущности
     */
    public static <T> Conditions<T> anyEntityMatches(@NonNull Condition<T> condition) {
        return new AnyEntityMatchesCondition<>(condition);
    }

    /**
     * Проверяет, что в списке сущностей ни одна сущность не соответствует указанному условию.
     *
     * @param condition условие для проверки
     * @param <T>       тип сущности
     * @return условие для проверки отсутствия соответствия всех сущностей
     */
    public static <T> Conditions<T> noEntitiesMatch(@NonNull Condition<T> condition) {
        return new NoEntitiesMatchCondition<>(condition);
    }

    /**
     * Проверяет, что список сущностей содержит сущности с ожидаемыми значениями свойств.
     *
     * @param propertyName  имя свойства
     * @param expectedValue ожидаемое значение
     * @param <T>           тип сущности
     * @return условие для проверки значений свойств сущностей
     */
    public static <T> Conditions<T> entitiesPropertyValuesEqual(@NonNull String propertyName, @NonNull Object expectedValue) {
        return new EntitiesPropertyValuesEqualCondition<>(propertyName, expectedValue);
    }

    /**
     * Проверяет, что все свойства сущности равны соответствующим ожидаемым значениям.
     *
     * @param expectedProperties карта свойств и их ожидаемых значений
     * @param <T>                тип сущности
     * @return условие для проверки всех свойств на равенство
     */
    public static <T> Condition<T> allPropertiesEqual(@NonNull Map<String, Object> expectedProperties) {
        return new AllPropertiesEqualCondition<>(expectedProperties);
    }

    /**
     * Проверяет, что свойство сущности содержит указанный текст.
     *
     * @param propertyName имя свойства
     * @param text         текст для проверки
     * @param <T>          тип сущности
     * @return условие для проверки содержания текста в свойстве
     */
    public static <T> Condition<T> propertyContains(@NonNull String propertyName, @NonNull String text) {
        return new PropertyContainsCondition<>(propertyName, text);
    }

    /**
     * Проверяет, что свойство сущности равно ожидаемому значению.
     *
     * @param propertyName  имя свойства
     * @param expectedValue ожидаемое значение
     * @param <T>           тип сущности
     * @return условие для проверки равенства свойства
     */
    public static <T> Condition<T> propertyEquals(@NonNull String propertyName, @NonNull Object expectedValue) {
        return new PropertyEqualCondition<>(propertyName, expectedValue);
    }

    /**
     * Проверяет, что свойство сущности не содержит указанный текст.
     *
     * @param propertyName имя свойства
     * @param text         текст для проверки
     * @param <T>          тип сущности
     * @return условие для проверки отсутствия текста в свойстве
     */
    public static <T> Condition<T> propertyNotContains(@NonNull String propertyName, @NonNull String text) {
        return new PropertyNotContainsCondition<>(propertyName, text);
    }

    /**
     * Проверяет, что свойство сущности не равно указанному значению.
     *
     * @param propertyName    имя свойства
     * @param unexpectedValue значение, которому свойство не должно быть равно
     * @param <T>             тип сущности
     * @return условие для проверки неравенства свойства
     */
    public static <T> Condition<T> propertyNotEquals(@NonNull String propertyName, @NonNull Object unexpectedValue) {
        return new PropertyNotEqualCondition<>(propertyName, unexpectedValue);
    }

    /**
     * Проверяет, что свойство сущности соответствует регулярному выражению.
     *
     * @param propertyName имя свойства
     * @param regex        регулярное выражение
     * @param <T>          тип сущности
     * @return условие для проверки соответствия регулярному выражению
     */
    public static <T> Condition<T> propertyMatchesRegex(@NonNull String propertyName, @NonNull String regex) {
        return new PropertyMatchesRegexCondition<>(propertyName, regex);
    }

    /**
     * Проверяет, что числовое свойство сущности больше заданного значения.
     *
     * @param propertyName имя свойства
     * @param value        значение для сравнения
     * @param <T>          тип сущности
     * @return условие для проверки свойства на большее значение
     */
    public static <T> Condition<T> propertyGreaterThan(@NonNull String propertyName, @NonNull BigDecimal value) {
        return new PropertyGreaterThanCondition<>(propertyName, value);
    }

    /**
     * Проверяет, что числовое свойство сущности меньше заданного значения.
     *
     * @param propertyName имя свойства
     * @param value        значение для сравнения
     * @param <T>          тип сущности
     * @return условие для проверки свойства на меньшее значение
     */
    public static <T> Condition<T> propertyLessThan(@NonNull String propertyName, @NonNull BigDecimal value) {
        return new PropertyLessThanCondition<>(propertyName, value);
    }

    /**
     * Проверяет, что числовое свойство сущности находится в заданном диапазоне.
     *
     * @param propertyName имя свойства
     * @param start        начальное значение диапазона (включительно)
     * @param end          конечное значение диапазона (включительно)
     * @param <T>          тип сущности
     * @return условие для проверки диапазона свойства
     */
    public static <T> Condition<T> propertyBetween(@NonNull String propertyName, @NonNull BigDecimal start, @NonNull BigDecimal end) {
        return new PropertyBetweenCondition<>(propertyName, start, end);
    }

    /**
     * Проверяет, что свойство сущности входит в заданный список значений.
     *
     * @param propertyName имя свойства
     * @param values       список значений
     * @param <T>          тип сущности
     * @return условие для проверки вхождения значения свойства в список
     */
    public static <T> Condition<T> propertyIn(@NonNull String propertyName, @NonNull List<?> values) {
        return new PropertyInCondition<>(propertyName, values);
    }

    /**
     * Проверяет, что свойство сущности не входит в заданный список значений.
     *
     * @param propertyName имя свойства
     * @param values       список значений
     * @param <T>          тип сущности
     * @return условие для проверки отсутствия значения свойства в списке
     */
    public static <T> Condition<T> propertyNotIn(@NonNull String propertyName, @NonNull List<?> values) {
        return new PropertyNotInCondition<>(propertyName, values);
    }

    /**
     * Проверяет, что свойство сущности является экземпляром указанного типа.
     *
     * @param propertyName имя свойства
     * @param type         ожидаемый тип
     * @param <T>          тип сущности
     * @return условие для проверки типа свойства
     */
    public static <T> Condition<T> propertyIsOfType(@NonNull String propertyName, @NonNull Class<?> type) {
        return new PropertyIsOfTypeCondition<>(propertyName, type);
    }

    /**
     * Проверяет, что свойство сущности является подклассом или реализует указанный интерфейс.
     *
     * @param propertyName имя свойства
     * @param type         ожидаемый тип или интерфейс
     * @param <T>          тип сущности
     * @return условие для проверки наследования или реализации интерфейса
     */
    public static <T> Condition<T> propertyIsAssignableFrom(@NonNull String propertyName, @NonNull Class<?> type) {
        return new PropertyIsAssignableFromCondition<>(propertyName, type);
    }

    /**
     * Проверяет, что свойство сущности является null.
     *
     * @param propertyName имя свойства
     * @param <T>          тип сущности
     * @return условие для проверки, что свойство является null
     */
    public static <T> Condition<T> propertyIsNull(@NonNull String propertyName) {
        return new PropertyIsNullCondition<>(propertyName);
    }

    /**
     * Проверяет, что свойство сущности не является null.
     *
     * @param propertyName имя свойства
     * @param <T>          тип сущности
     * @return условие для проверки, что свойство не является null
     */
    public static <T> Condition<T> propertyIsNotNull(@NonNull String propertyName) {
        return new PropertyIsNotNullCondition<>(propertyName);
    }

    /**
     * Проверяет, что длина свойства (строки или коллекции) равна заданному значению.
     *
     * @param propertyName имя свойства
     * @param length       ожидаемая длина
     * @param <T>          тип сущности
     * @return условие для проверки длины свойства
     */
    public static <T> Condition<T> propertyLengthEquals(@NonNull String propertyName, int length) {
        return new PropertyLengthEqualCondition<>(propertyName, length);
    }

    /**
     * Проверяет, что длина свойства больше заданного значения.
     *
     * @param propertyName имя свойства
     * @param length       минимальная длина
     * @param <T>          тип сущности
     * @return условие для проверки длины свойства
     */
    public static <T> Condition<T> propertyLengthGreaterThan(@NonNull String propertyName, int length) {
        return new PropertyLengthGreaterThanCondition<>(propertyName, length);
    }

    /**
     * Проверяет, что длина свойства меньше заданного значения.
     *
     * @param propertyName имя свойства
     * @param length       максимальная длина
     * @param <T>          тип сущности
     * @return условие для проверки длины свойства
     */
    public static <T> Condition<T> propertyLengthLessThan(@NonNull String propertyName, int length) {
        return new PropertyLengthLessThanCondition<>(propertyName, length);
    }

    /**
     * Проверяет, что строковое свойство начинается с указанного префикса.
     *
     * @param propertyName имя свойства
     * @param prefix       префикс
     * @param <T>          тип сущности
     * @return условие для проверки начала строки
     */
    public static <T> Condition<T> propertyStartsWith(@NonNull String propertyName, @NonNull String prefix) {
        return new PropertyStartsWithCondition<>(propertyName, prefix);
    }

    /**
     * Проверяет, что строковое свойство заканчивается указанным суффиксом.
     *
     * @param propertyName имя свойства
     * @param suffix       суффикс
     * @param <T>          тип сущности
     * @return условие для проверки окончания строки
     */
    public static <T> Condition<T> propertyEndsWith(@NonNull String propertyName, @NonNull String suffix) {
        return new PropertyEndsWithCondition<>(propertyName, suffix);
    }
}
