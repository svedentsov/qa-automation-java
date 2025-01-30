package db.matcher;

import db.matcher.assertions.*;
import db.matcher.condition.Condition;
import db.matcher.condition.Conditions;
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

    public static <T> Conditions<T> entities(Conditions<T> conditions) {
        return conditions;
    }

    /**
     * Проверка "property(getter, cond)": если cond — это условие для String, Number и т.д.
     *
     * @param getter функция для извлечения поля
     * @param cond   условие для поля
     * @param <T>    тип сущности
     * @param <R>    тип поля
     */
    public static <T, R> Condition<T> property(Function<T, R> getter, Condition<R> cond) {
        return entity -> {
            R value = getter.apply(entity);
            cond.check(value);
        };
    }

    @SafeVarargs
    public static <T> Condition<T> and(Condition<T>... conditions) {
        return CompositeAssertions.and(conditions);
    }

    @SafeVarargs
    public static <T> Condition<T> or(Condition<T>... conditions) {
        return CompositeAssertions.or(conditions);
    }

    @SafeVarargs
    public static <T> Condition<T> not(Condition<T>... conditions) {
        return CompositeAssertions.not(conditions);
    }

    @SafeVarargs
    public static <T> Condition<T> nOf(int n, Condition<T>... conditions) {
        return CompositeAssertions.nOf(n, conditions);
    }

    public static <T> Conditions<T> forAllEntities(Condition<T> singleCondition) {
        return list -> {
            for (T entity : list) {
                singleCondition.check(entity);
            }
        };
    }

    public static <T> Conditions<T> forAnyEntity(Condition<T> singleCondition) {
        return list -> {
            boolean found = false;
            for (T entity : list) {
                try {
                    singleCondition.check(entity);
                    found = true;
                    break;
                } catch (AssertionError ignored) {
                    // продолжаем
                }
            }
            org.assertj.core.api.Assertions.assertThat(found)
                    .as("Ни одна сущность не удовлетворяет условию")
                    .isTrue();
        };
    }

    // ------------------- String
    public static <T> Condition<T> propertyContains(Function<T, String> getter, String text) {
        return StringAssertions.propertyContains(getter, text);
    }

    public static <T> Condition<T> propertyContainsIgnoreCase(Function<T, String> getter, String text) {
        return StringAssertions.propertyContainsIgnoreCase(getter, text);
    }

    public static <T> Condition<T> propertyStartsWith(Function<T, String> getter, String prefix) {
        return StringAssertions.propertyStartsWith(getter, prefix);
    }

    public static <T> Condition<T> propertyEndsWith(Function<T, String> getter, String suffix) {
        return StringAssertions.propertyEndsWith(getter, suffix);
    }

    public static <T> Condition<T> propertyMatchesRegex(Function<T, String> getter, String regex) {
        return StringAssertions.propertyMatchesRegex(getter, regex);
    }

    public static <T> Condition<T> propertyIsEmpty(Function<T, String> getter) {
        return StringAssertions.propertyIsEmpty(getter);
    }

    public static <T> Condition<T> propertyIsNotEmpty(Function<T, String> getter) {
        return StringAssertions.propertyIsNotEmpty(getter);
    }

    public static <T> Condition<T> propertyIsDigitsOnly(Function<T, String> getter) {
        return StringAssertions.propertyIsDigitsOnly(getter);
    }

    // ------------------- Number
    public static <T> Condition<T> propertyGreaterThan(Function<T, Number> getter, BigDecimal value) {
        return NumberAssertions.propertyGreaterThan(getter, value);
    }

    public static <T> Condition<T> propertyLessThan(Function<T, Number> getter, BigDecimal value) {
        return NumberAssertions.propertyLessThan(getter, value);
    }

    public static <T> Condition<T> propertyBetween(Function<T, Number> getter, BigDecimal start, BigDecimal end) {
        return NumberAssertions.propertyBetween(getter, start, end);
    }

    public static <T> Condition<T> propertyIsZero(Function<T, Number> getter) {
        return NumberAssertions.propertyIsZero(getter);
    }

    public static <T> Condition<T> propertyIsNotZero(Function<T, Number> getter) {
        return NumberAssertions.propertyIsNotZero(getter);
    }

    // ------------------- Timestamp
    public static <T> Condition<T> dateBefore(Function<T, LocalDateTime> getter, LocalDateTime dateTime) {
        return TimestampAssertions.dateBefore(getter, dateTime);
    }

    public static <T> Condition<T> localDateTimeAfter(Function<T, LocalDateTime> getter, LocalDateTime dateTime) {
        return TimestampAssertions.localDateTimeAfter(getter, dateTime);
    }

    public static <T> Condition<T> isInFuture(Function<T, LocalDateTime> getter) {
        return TimestampAssertions.isInFuture(getter);
    }

    public static <T> Condition<T> isInPast(Function<T, LocalDateTime> getter) {
        return TimestampAssertions.isInPast(getter);
    }

    // ------------------- Property
    public static <T> Condition<T> propertyEquals(Function<T, ?> getter, Object expectedValue) {
        return PropertyAssertions.propertyEquals(getter, expectedValue);
    }

    public static <T> Condition<T> propertyIsNull(Function<T, ?> getter) {
        return PropertyAssertions.propertyIsNull(getter);
    }

    public static <T> Condition<T> propertyIsOfType(Function<T, ?> getter, Class<?> expectedType) {
        return PropertyAssertions.propertyIsOfType(getter, expectedType);
    }

    public static <T> Condition<T> propertyIsAssignableFrom(Function<T, ?> getter, Class<?> superType) {
        return PropertyAssertions.propertyIsAssignableFrom(getter, superType);
    }

    public static <T, R> Condition<T> optionalPropertyIsPresent(Function<T, Optional<R>> getter) {
        return PropertyAssertions.optionalPropertyIsPresent(getter);
    }

    public static <T> Condition<T> allPropertiesEqual(Map<Function<T, ?>, Object> expectedProps) {
        return PropertyAssertions.allPropertiesEqual(expectedProps);
    }

    public static <T> Condition<T> propertyIn(Function<T, ?> getter, List<?> values) {
        return PropertyAssertions.propertyIn(getter, values);
    }

    // ------------------- Collection
    public static <T, E> Condition<T> allCollectionElementsMatch(Function<T, Collection<E>> getter, Condition<E> elementCondition) {
        return CollectionAssertions.allCollectionElementsMatch(getter, elementCondition);
    }

    public static <T> Condition<T> propertyLengthEquals(Function<T, ?> getter, int length) {
        return CollectionAssertions.propertyLengthEquals(getter, length);
    }

    public static <T> Condition<T> propertyLengthGreaterThan(Function<T, ?> getter, int length) {
        return CollectionAssertions.propertyLengthGreaterThan(getter, length);
    }

    public static <T> Condition<T> propertyLengthLessThan(Function<T, ?> getter, int length) {
        return CollectionAssertions.propertyLengthLessThan(getter, length);
    }

    // ------------------- Entities (список сущностей)
    public static <T> Conditions<T> entitiesExist() {
        return EntityAssertions.entitiesExist();
    }

    public static <T> Conditions<T> entitiesCountEqual(int count) {
        return EntityAssertions.entitiesCountEqual(count);
    }

    public static <T> Conditions<T> entitiesCountGreater(int count) {
        return EntityAssertions.entitiesCountGreater(count);
    }

    public static <T> Conditions<T> allEntitiesMatch(Condition<T> condition) {
        return EntityAssertions.allEntitiesMatch(condition);
    }

    public static <T> Conditions<T> anyEntityMatches(Condition<T> condition) {
        return EntityAssertions.anyEntityMatches(condition);
    }

    public static <T> Conditions<T> noEntitiesMatch(Condition<T> condition) {
        return EntityAssertions.noEntitiesMatch(condition);
    }

    public static <T> Conditions<T> entitiesPropertyValuesEqual(Function<T, ?> getter, Object expectedValue) {
        return EntityAssertions.entitiesPropertyValuesEqual(getter, expectedValue);
    }
}
