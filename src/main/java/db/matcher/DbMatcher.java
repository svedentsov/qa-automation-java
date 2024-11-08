package db.matcher;

import db.matcher.condition.*;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Утилитный класс для создания условий проверки сущностей базы данных.
 * Предоставляет методы для создания различных условий проверки свойств и значений сущностей.
 */
@UtilityClass
public class DbMatcher {

    // ===========================================================================
    // Условия для списка сущностей (Conditions<T>)
    // ===========================================================================

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
     * Проверяет, что все сущности соответствуют указанному условию.
     *
     * @param condition условие для проверки каждой сущности
     * @param <T>       тип сущности
     * @return условие для проверки соответствия всех сущностей
     */
    public static <T> Conditions<T> allEntitiesMatch(@NonNull Condition<T> condition) {
        return new AllEntitiesMatchCondition<>(condition);
    }

    /**
     * Проверяет, что хотя бы одна сущность соответствует указанному условию.
     *
     * @param condition условие для проверки
     * @param <T>       тип сущности
     * @return условие для проверки соответствия хотя бы одной сущности
     */
    public static <T> Conditions<T> anyEntityMatches(@NonNull Condition<T> condition) {
        return new AnyEntityMatchesCondition<>(condition);
    }

    /**
     * Проверяет, что ни одна сущность не соответствует указанному условию.
     *
     * @param condition условие для проверки
     * @param <T>       тип сущности
     * @return условие для проверки отсутствия соответствия всех сущностей
     */
    public static <T> Conditions<T> noEntitiesMatch(@NonNull Condition<T> condition) {
        return new NoEntitiesMatchCondition<>(condition);
    }

    /**
     * Проверяет, что все сущности имеют ожидаемое значение свойства.
     *
     * @param getter        функция для получения значения свойства
     * @param expectedValue ожидаемое значение
     * @param <T>           тип сущности
     * @return условие для проверки значений свойств сущностей
     */
    public static <T> Conditions<T> entitiesPropertyValuesEqual(@NonNull Function<T, ?> getter, @NonNull Object expectedValue) {
        return new EntitiesPropertyValuesEqualCondition<>(getter, expectedValue);
    }

    // ===========================================================================
    // Условия для свойств сущности (Condition<T>)
    // ===========================================================================

    /**
     * Проверяет, что все указанные свойства сущности равны ожидаемым значениям.
     *
     * @param expectedProperties карта функций-геттеров и их ожидаемых значений
     * @param <T>                тип сущности
     * @return условие для проверки всех свойств на равенство
     */
    public static <T> Condition<T> allPropertiesEqual(@NonNull Map<Function<T, ?>, Object> expectedProperties) {
        return new AllPropertiesEqualCondition<>(expectedProperties);
    }

    /**
     * Проверяет, что свойство сущности равно ожидаемому значению.
     *
     * @param getter        функция для получения значения свойства
     * @param expectedValue ожидаемое значение
     * @param <T>           тип сущности
     * @return условие для проверки равенства свойства
     */
    public static <T> Condition<T> propertyEquals(@NonNull Function<T, ?> getter, @NonNull Object expectedValue) {
        return new PropertyEqualCondition<>(getter, expectedValue);
    }

    /**
     * Проверяет, что свойство сущности равно ожидаемому значению с кастомным сообщением об ошибке.
     *
     * @param getter        функция для получения значения свойства
     * @param expectedValue ожидаемое значение
     * @param errorMessage  кастомное сообщение об ошибке
     * @param <T>           тип сущности
     * @return условие для проверки равенства свойства с сообщением
     */
    public static <T> Condition<T> propertyEquals(@NonNull Function<T, ?> getter, @NonNull Object expectedValue, String errorMessage) {
        return new PropertyEqualWithMessageCondition<>(getter, expectedValue, errorMessage);
    }

    /**
     * Проверяет, что свойство сущности не равно указанному значению.
     *
     * @param getter          функция для получения значения свойства
     * @param unexpectedValue значение, которому свойство не должно быть равно
     * @param <T>             тип сущности
     * @return условие для проверки неравенства свойства
     */
    public static <T> Condition<T> propertyNotEquals(@NonNull Function<T, ?> getter, @NonNull Object unexpectedValue) {
        return new PropertyNotEqualCondition<>(getter, unexpectedValue);
    }

    /**
     * Проверяет, что свойство сущности содержит указанный текст.
     *
     * @param getter функция для получения значения свойства
     * @param text   текст для проверки
     * @param <T>    тип сущности
     * @return условие для проверки содержания текста в свойстве
     */
    public static <T> Condition<T> propertyContains(@NonNull Function<T, String> getter, @NonNull String text) {
        return new PropertyContainsCondition<>(getter, text);
    }

    /**
     * Проверяет, что строковое свойство содержит указанный текст без учета регистра.
     *
     * @param getter функция для получения значения свойства
     * @param text   текст для проверки
     * @param <T>    тип сущности
     * @return условие для проверки содержания текста без учета регистра
     */
    public static <T> Condition<T> propertyContainsIgnoreCase(@NonNull Function<T, String> getter, @NonNull String text) {
        return new PropertyContainsIgnoreCaseCondition<>(getter, text);
    }

    /**
     * Проверяет, что свойство сущности не содержит указанный текст.
     *
     * @param getter функция для получения значения свойства
     * @param text   текст для проверки
     * @param <T>    тип сущности
     * @return условие для проверки отсутствия текста в свойстве
     */
    public static <T> Condition<T> propertyNotContains(@NonNull Function<T, String> getter, @NonNull String text) {
        return new PropertyNotContainsCondition<>(getter, text);
    }

    /**
     * Проверяет, что свойство сущности соответствует регулярному выражению.
     *
     * @param getter функция для получения значения свойства
     * @param regex  регулярное выражение
     * @param <T>    тип сущности
     * @return условие для проверки соответствия регулярному выражению
     */
    public static <T> Condition<T> propertyMatchesRegex(@NonNull Function<T, String> getter, @NonNull String regex) {
        return new PropertyMatchesRegexCondition<>(getter, regex);
    }

    /**
     * Проверяет, что строковое свойство начинается с указанного префикса.
     *
     * @param getter функция для получения значения свойства
     * @param prefix префикс
     * @param <T>    тип сущности
     * @return условие для проверки начала строки
     */
    public static <T> Condition<T> propertyStartsWith(@NonNull Function<T, String> getter, @NonNull String prefix) {
        return new PropertyStartsWithCondition<>(getter, prefix);
    }

    /**
     * Проверяет, что строковое свойство заканчивается указанным суффиксом.
     *
     * @param getter функция для получения значения свойства
     * @param suffix суффикс
     * @param <T>    тип сущности
     * @return условие для проверки окончания строки
     */
    public static <T> Condition<T> propertyEndsWith(@NonNull Function<T, String> getter, @NonNull String suffix) {
        return new PropertyEndsWithCondition<>(getter, suffix);
    }

    /**
     * Проверяет, что числовое свойство сущности больше заданного значения.
     *
     * @param getter функция для получения значения свойства
     * @param value  значение для сравнения
     * @param <T>    тип сущности
     * @return условие для проверки свойства на большее значение
     */
    public static <T> Condition<T> propertyGreaterThan(@NonNull Function<T, Number> getter, @NonNull BigDecimal value) {
        return new PropertyGreaterThanCondition<>(getter, value);
    }

    /**
     * Проверяет, что числовое свойство сущности меньше заданного значения.
     *
     * @param getter функция для получения значения свойства
     * @param value  значение для сравнения
     * @param <T>    тип сущности
     * @return условие для проверки свойства на меньшее значение
     */
    public static <T> Condition<T> propertyLessThan(@NonNull Function<T, Number> getter, @NonNull BigDecimal value) {
        return new PropertyLessThanCondition<>(getter, value);
    }

    /**
     * Проверяет, что числовое свойство сущности находится в заданном диапазоне.
     *
     * @param getter функция для получения значения свойства
     * @param start  начальное значение диапазона (включительно)
     * @param end    конечное значение диапазона (включительно)
     * @param <T>    тип сущности
     * @return условие для проверки диапазона свойства
     */
    public static <T> Condition<T> propertyBetween(@NonNull Function<T, Number> getter, @NonNull BigDecimal start, @NonNull BigDecimal end) {
        return new PropertyBetweenCondition<>(getter, start, end);
    }

    /**
     * Проверяет, что длина свойства (строки или коллекции) равна заданному значению.
     *
     * @param getter функция для получения значения свойства
     * @param length ожидаемая длина
     * @param <T>    тип сущности
     * @return условие для проверки длины свойства
     */
    public static <T> Condition<T> propertyLengthEquals(@NonNull Function<T, ?> getter, int length) {
        return new PropertyLengthEqualCondition<>(getter, length);
    }

    /**
     * Проверяет, что длина свойства больше заданного значения.
     *
     * @param getter функция для получения значения свойства
     * @param length минимальная длина
     * @param <T>    тип сущности
     * @return условие для проверки длины свойства
     */
    public static <T> Condition<T> propertyLengthGreaterThan(@NonNull Function<T, ?> getter, int length) {
        return new PropertyLengthGreaterThanCondition<>(getter, length);
    }

    /**
     * Проверяет, что длина свойства меньше заданного значения.
     *
     * @param getter функция для получения значения свойства
     * @param length максимальная длина
     * @param <T>    тип сущности
     * @return условие для проверки длины свойства
     */
    public static <T> Condition<T> propertyLengthLessThan(@NonNull Function<T, ?> getter, int length) {
        return new PropertyLengthLessThanCondition<>(getter, length);
    }

    /**
     * Проверяет, что свойство пустое.
     *
     * @param getter функция для получения значения свойства
     * @param <T>    тип сущности
     * @return условие для проверки на пустоту
     */
    public static <T> Condition<T> propertyIsEmpty(@NonNull Function<T, ?> getter) {
        return new PropertyIsEmptyCondition<>(getter);
    }

    /**
     * Проверяет, что свойство не пустое.
     *
     * @param getter функция для получения значения свойства
     * @param <T>    тип сущности
     * @return условие для проверки на непустоту
     */
    public static <T> Condition<T> propertyIsNotEmpty(@NonNull Function<T, ?> getter) {
        return new PropertyIsNotEmptyCondition<>(getter);
    }

    /**
     * Проверяет, что свойство сущности входит в заданный список значений.
     *
     * @param getter функция для получения значения свойства
     * @param values список значений
     * @param <T>    тип сущности
     * @return условие для проверки вхождения значения свойства в список
     */
    public static <T> Condition<T> propertyIn(@NonNull Function<T, ?> getter, @NonNull List<?> values) {
        return new PropertyInCondition<>(getter, values);
    }

    /**
     * Проверяет, что свойство сущности не входит в заданный список значений.
     *
     * @param getter функция для получения значения свойства
     * @param values список значений
     * @param <T>    тип сущности
     * @return условие для проверки отсутствия значения свойства в списке
     */
    public static <T> Condition<T> propertyNotIn(@NonNull Function<T, ?> getter, @NonNull List<?> values) {
        return new PropertyNotInCondition<>(getter, values);
    }

    /**
     * Проверяет, что свойство сущности является null.
     *
     * @param getter функция для получения значения свойства
     * @param <T>    тип сущности
     * @return условие для проверки, что свойство является null
     */
    public static <T> Condition<T> propertyIsNull(@NonNull Function<T, ?> getter) {
        return new PropertyIsNullCondition<>(getter);
    }

    /**
     * Проверяет, что свойство сущности не является null.
     *
     * @param getter функция для получения значения свойства
     * @param <T>    тип сущности
     * @return условие для проверки, что свойство не является null
     */
    public static <T> Condition<T> propertyIsNotNull(@NonNull Function<T, ?> getter) {
        return new PropertyIsNotNullCondition<>(getter);
    }

    /**
     * Проверяет, что свойство сущности является экземпляром указанного типа.
     *
     * @param getter функция для получения значения свойства
     * @param type   ожидаемый тип
     * @param <T>    тип сущности
     * @return условие для проверки типа свойства
     */
    public static <T> Condition<T> propertyIsOfType(@NonNull Function<T, ?> getter, @NonNull Class<?> type) {
        return new PropertyIsOfTypeCondition<>(getter, type);
    }

    /**
     * Проверяет, что свойство сущности является подклассом или реализует указанный интерфейс.
     *
     * @param getter функция для получения значения свойства
     * @param type   ожидаемый тип или интерфейс
     * @param <T>    тип сущности
     * @return условие для проверки наследования или реализации интерфейса
     */
    public static <T> Condition<T> propertyIsAssignableFrom(@NonNull Function<T, ?> getter, @NonNull Class<?> type) {
        return new PropertyIsAssignableFromCondition<>(getter, type);
    }

    /**
     * Проверяет, что Optional свойство присутствует.
     *
     * @param getter функция для получения Optional свойства
     * @param <T>    тип сущности
     * @param <R>    тип значения в Optional
     * @return условие для проверки присутствия значения
     */
    public static <T, R> Condition<T> optionalPropertyIsPresent(@NonNull Function<T, Optional<R>> getter) {
        return new OptionalPropertyPresentCondition<>(getter);
    }

    /**
     * Проверяет, что дата до заданной даты.
     *
     * @param getter   функция для получения даты
     * @param dateTime дата и время для сравнения
     * @param <T>      тип сущности
     * @return условие для проверки даты
     */
    public static <T> Condition<T> dateBefore(@NonNull Function<T, LocalDateTime> getter, @NonNull LocalDateTime dateTime) {
        return new PropertyDateBeforeCondition<>(getter, dateTime);
    }

    /**
     * Проверяет, что LocalDateTime после заданной даты и времени.
     *
     * @param getter   функция для получения даты и времени
     * @param dateTime дата и время для сравнения
     * @param <T>      тип сущности
     * @return условие для проверки даты и времени
     */
    public static <T> Condition<T> localDateTimeAfter(@NonNull Function<T, LocalDateTime> getter, @NonNull LocalDateTime dateTime) {
        return new PropertyLocalDateTimeAfterCondition<>(getter, dateTime);
    }

    /**
     * Проверяет, что свойство перечисление имеет определенное значение.
     *
     * @param getter        функция для получения значения свойства
     * @param expectedValue ожидаемое значение перечисления
     * @param <T>           тип сущности
     * @param <E>           тип перечисления
     * @return условие для проверки значения перечисления
     */
    public static <T, E extends Enum<E>> Condition<T> enumPropertyEquals(@NonNull Function<T, E> getter, @NonNull E expectedValue) {
        return new PropertyEnumValueCondition<>(getter, expectedValue);
    }

    /**
     * Проверяет вложенное свойство с указанным условием.
     *
     * @param getter          функция для получения вложенного свойства
     * @param nestedCondition условие для проверки вложенного свойства
     * @param <T>             тип сущности
     * @param <R>             тип вложенной сущности
     * @return условие для проверки вложенного свойства
     */
    public static <T, R> Condition<T> nestedProperty(@NonNull Function<T, R> getter, @NonNull Condition<R> nestedCondition) {
        return new NestedPropertyCondition<>(getter, nestedCondition);
    }

    /**
     * Проверяет, что все элементы коллекции внутри сущности соответствуют условию.
     *
     * @param getter           функция для получения коллекции
     * @param elementCondition условие для проверки элементов коллекции
     * @param <T>              тип сущности
     * @param <E>              тип элементов коллекции
     * @return условие для проверки элементов коллекции
     */
    public static <T, E> Condition<T> allCollectionElementsMatch(@NonNull Function<T, Collection<E>> getter, @NonNull Condition<E> elementCondition) {
        return new AllCollectionElementsMatchCondition<>(getter, elementCondition);
    }

    // ===========================================================================
    // Логические операции над условиями
    // ===========================================================================

    /**
     * Объединяет несколько условий с помощью логической операции AND.
     *
     * @param conditions условия для объединения
     * @param <T>        тип сущности
     * @return составное условие
     */
    @SafeVarargs
    public static <T> Condition<T> and(Condition<T>... conditions) {
        return new AndCondition<>(conditions);
    }

    /**
     * Объединяет несколько условий с помощью логической операции OR.
     *
     * @param conditions условия для объединения
     * @param <T>        тип сущности
     * @return составное условие
     */
    @SafeVarargs
    public static <T> Condition<T> or(Condition<T>... conditions) {
        return new OrCondition<>(conditions);
    }

    /**
     * Инвертирует условие с помощью логической операции NOT.
     *
     * @param condition условие для инвертирования
     * @param <T>       тип сущности
     * @return инвертированное условие
     */
    public static <T> Condition<T> not(Condition<T> condition) {
        return new NotCondition<>(condition);
    }
}
