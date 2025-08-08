package com.svedentsov.matcher.assertions;

import com.svedentsov.matcher.Condition;
import com.svedentsov.matcher.assertions.InstantAssertions.InstantCondition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * Утилитарный класс для создания условий (Condition) для проверки объектов типа {@link java.util.UUID}.
 * Предоставляет методы для валидации версии, варианта, строкового представления и других свойств UUID.
 */
@UtilityClass
public class UuidAssertions {

    /**
     * Специальный "нулевой" (nil) UUID для сравнения.
     */
    private static final UUID NIL_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    /**
     * Функциональный интерфейс для условий проверки {@link UUID}.
     */
    @FunctionalInterface
    public interface UuidCondition extends Condition<UUID> {
    }

    /**
     * Проверяет, что UUID равен ожидаемому значению.
     *
     * @param expected ожидаемый UUID.
     * @return {@link UuidCondition} для проверки равенства.
     */
    public static UuidCondition isEqualTo(UUID expected) {
        requireNonNull(expected, "Ожидаемый UUID не может быть null");
        return actual -> Assertions.assertThat(actual)
                .as("UUID должен быть равен '%s', но был '%s'", expected, actual)
                .isEqualTo(expected);
    }

    /**
     * Проверяет, что UUID не равен указанному значению.
     *
     * @param unexpected UUID, которому не должен быть равен проверяемый.
     * @return {@link UuidCondition} для проверки неравенства.
     */
    public static UuidCondition isNotEqualTo(UUID unexpected) {
        requireNonNull(unexpected, "Неожидаемый UUID не может быть null");
        return actual -> Assertions.assertThat(actual)
                .as("UUID не должен быть равен '%s'", unexpected)
                .isNotEqualTo(unexpected);
    }

    /**
     * Проверяет, что UUID является null.
     *
     * @return {@link UuidCondition} для проверки на null.
     */
    public static UuidCondition isNull() {
        return actual -> Assertions.assertThat(actual)
                .as("UUID должен быть null")
                .isNull();
    }

    /**
     * Проверяет, что UUID не является null.
     *
     * @return {@link UuidCondition} для проверки на не-null.
     */
    public static UuidCondition isNotNull() {
        return actual -> Assertions.assertThat(actual)
                .as("UUID не должен быть null")
                .isNotNull();
    }

    /**
     * Проверяет, что UUID имеет указанную версию.
     *
     * @param version ожидаемая версия (1-5).
     * @return {@link UuidCondition} для проверки версии.
     * @throws IllegalArgumentException если версия не в диапазоне 1-5.
     */
    public static UuidCondition hasVersion(int version) {
        if (version < 1 || version > 5) {
            throw new IllegalArgumentException("Версия UUID должна быть в диапазоне от 1 до 5");
        }
        return actual -> Assertions.assertThat(actual.version())
                .as("UUID '%s' должен иметь версию %d, но была %d", actual, version, actual.version())
                .isEqualTo(version);
    }

    /**
     * Проверяет, что UUID имеет указанный вариант.
     *
     * @param variant ожидаемый вариант (обычно 2 для Leach-Salz).
     * @return {@link UuidCondition} для проверки варианта.
     */
    public static UuidCondition hasVariant(int variant) {
        return actual -> Assertions.assertThat(actual.variant())
                .as("UUID '%s' должен иметь вариант %d, но был %d", actual, variant, actual.variant())
                .isEqualTo(variant);
    }

    /**
     * Проверяет, что UUID является "нулевым" (nil UUID), т.е. состоит из всех нулей.
     *
     * @return {@link UuidCondition} для проверки на nil UUID.
     */
    public static UuidCondition isNil() {
        return actual -> Assertions.assertThat(actual)
                .as("UUID должен быть nil (00000000-...), но был '%s'", actual)
                .isEqualTo(NIL_UUID);
    }

    /**
     * Проверяет, что строковое представление UUID полностью в нижнем регистре.
     *
     * @return {@link UuidCondition} для проверки регистра.
     */
    public static UuidCondition isLowerCase() {
        return actual -> {
            String str = actual.toString();
            Assertions.assertThat(str)
                    .as("Строковое представление UUID '%s' должно быть в нижнем регистре", str)
                    .isLowerCase();
        };
    }

    /**
     * Проверяет, что строковое представление UUID полностью в верхнем регистре.
     *
     * @return {@link UuidCondition} для проверки регистра.
     */
    public static UuidCondition isUpperCase() {
        return actual -> {
            String str = actual.toString();
            Assertions.assertThat(str)
                    .as("Строковое представление UUID '%s' должно быть в верхнем регистре", str)
                    .isUpperCase();
        };
    }

    /**
     * Проверяет, что строковое представление UUID соответствует регулярному выражению.
     *
     * @param regex регулярное выражение для проверки.
     * @return {@link UuidCondition} для проверки строкового представления.
     */
    public static UuidCondition toStringMatches(String regex) {
        requireNonNull(regex, "Регулярное выражение не может быть null");
        return actual -> Assertions.assertThat(actual.toString())
                .as("Строковое представление UUID '%s' должно соответствовать regex '%s'", actual, regex)
                .matches(Pattern.compile(regex));
    }

    /**
     * Проверяет, что временная метка, извлеченная из UUID версии 1, удовлетворяет заданному условию.
     * Эта проверка сначала убеждается, что UUID имеет версию 1.
     *
     * @param condition условие из {@link InstantAssertions} для проверки временной метки.
     * @return {@link UuidCondition} для проверки timestamp.
     */
    public static UuidCondition timestampSatisfies(InstantCondition condition) {
        requireNonNull(condition, "Условие для Instant не может быть null");
        return actual -> {
            Assertions.assertThat(actual.version())
                    .as("UUID должен иметь версию 1 для извлечения временной метки, но была версия %d", actual.version())
                    .isEqualTo(1);

            // Конвертация timestamp из формата UUIDv1 (100-нс интервалы с 15.10.1582) в Unix epoch
            long timestamp = actual.timestamp();
            long unixTimestamp = (timestamp - 0x01b21dd213814000L) / 10000L;
            Instant instant = Instant.ofEpochMilli(unixTimestamp);

            condition.check(instant);
        };
    }
}
