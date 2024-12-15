package rest.matcher.assertions;

import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.HamcrestCondition;
import org.hamcrest.Matcher;
import rest.matcher.condition.Condition;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Класс для утверждений, связанных с куки в ответе.
 */
@UtilityClass
public class CookieAssertions {

    /**
     * Функциональный интерфейс для условий проверки куки в ответе.
     */
    @FunctionalInterface
    public interface CookieCondition extends Condition {
    }

    /**
     * Проверяет, что значение куки соответствует ожидаемому.
     *
     * @param cookieName    имя куки
     * @param expectedValue ожидаемое значение куки
     * @return условие для проверки значения куки
     * @throws IllegalArgumentException если cookieName или expectedValue равно null
     */
    public static CookieCondition cookieEquals(String cookieName, String expectedValue) {
        Objects.requireNonNull(cookieName, "cookieName не может быть null");
        Objects.requireNonNull(expectedValue, "expectedValue не может быть null");
        return response -> {
            String actualValue = response.getCookie(cookieName);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что кука '%s' будет '%s', но была '%s'", cookieName, expectedValue, actualValue)
                    .isEqualTo(expectedValue);
        };
    }

    /**
     * Проверяет, что кука существует в ответе.
     *
     * @param cookieName имя куки
     * @return условие для проверки существования куки
     * @throws IllegalArgumentException если cookieName равно null
     */
    public static CookieCondition cookieExists(String cookieName) {
        Objects.requireNonNull(cookieName, "cookieName не может быть null");
        return response -> {
            boolean exists = response.getCookies().containsKey(cookieName);
            Assertions.assertThat(exists)
                    .as("Ожидалось, что кука '%s' существует", cookieName)
                    .isTrue();
        };
    }

    /**
     * Проверяет, что значение куки соответствует заданному Matcher.
     *
     * @param cookieName имя куки
     * @param matcher    Matcher для проверки значения куки
     * @return условие для проверки соответствия значения куки
     * @throws IllegalArgumentException если cookieName или matcher равно null
     */
    public static CookieCondition cookieMatches(String cookieName, Matcher<?> matcher) {
        Objects.requireNonNull(cookieName, "cookieName не может быть null");
        Objects.requireNonNull(matcher, "matcher не может быть null");
        return response -> {
            String actualValue = response.getCookie(cookieName);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что кука '%s' соответствует условию %s, но была '%s'", cookieName, matcher, actualValue)
                    .is(new HamcrestCondition<>(matcher));
        };
    }

    /**
     * Проверяет, что значение куки начинается с указанного префикса.
     *
     * @param cookieName имя куки
     * @param prefix     ожидаемый префикс
     * @return условие для проверки начала значения куки
     * @throws IllegalArgumentException если cookieName или prefix равно null
     */
    public static CookieCondition cookieStartsWith(String cookieName, String prefix) {
        Objects.requireNonNull(cookieName, "cookieName не может быть null");
        Objects.requireNonNull(prefix, "prefix не может быть null");
        return response -> {
            String actualValue = response.getCookie(cookieName);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что кука '%s' начинается с '%s', но была '%s'", cookieName, prefix, actualValue)
                    .startsWith(prefix);
        };
    }

    /**
     * Проверяет, что значение куки заканчивается на указанный суффикс.
     *
     * @param cookieName имя куки
     * @param suffix     ожидаемый суффикс
     * @return условие для проверки конца значения куки
     * @throws IllegalArgumentException если cookieName или suffix равно null
     */
    public static CookieCondition cookieEndsWith(String cookieName, String suffix) {
        Objects.requireNonNull(cookieName, "cookieName не может быть null");
        Objects.requireNonNull(suffix, "suffix не может быть null");
        return response -> {
            String actualValue = response.getCookie(cookieName);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что кука '%s' заканчивается на '%s', но была '%s'", cookieName, suffix, actualValue)
                    .endsWith(suffix);
        };
    }

    /**
     * Проверяет, что кука имеет определенное доменное имя.
     *
     * @param cookieName имя куки
     * @param domain     ожидаемое доменное имя
     * @return условие для проверки домена куки
     * @throws IllegalArgumentException если cookieName или domain равно null
     */
    public static CookieCondition cookieDomainEquals(String cookieName, String domain) {
        Objects.requireNonNull(cookieName, "cookieName не может быть null");
        Objects.requireNonNull(domain, "domain не может быть null");
        return response -> {
            io.restassured.http.Cookie detailedCookie = response.getDetailedCookie(cookieName);
            if (detailedCookie == null) {
                throw new AssertionError(String.format("Ожидалось, что кука '%s' существует", cookieName));
            }
            String actualDomain = detailedCookie.getDomain();
            Assertions.assertThat(actualDomain)
                    .as("Ожидалось, что домен куки '%s' будет '%s', но был '%s'", cookieName, domain, actualDomain)
                    .isEqualToIgnoringCase(domain);
        };
    }

    /**
     * Проверяет, что кука имеет определенный путь.
     *
     * @param cookieName имя куки
     * @param path       ожидаемый путь
     * @return условие для проверки пути куки
     * @throws IllegalArgumentException если cookieName или path равно null
     */
    public static CookieCondition cookiePathEquals(String cookieName, String path) {
        Objects.requireNonNull(cookieName, "cookieName не может быть null");
        Objects.requireNonNull(path, "path не может быть null");
        return response -> {
            io.restassured.http.Cookie detailedCookie = response.getDetailedCookie(cookieName);
            if (detailedCookie == null) {
                throw new AssertionError(String.format("Ожидалось, что кука '%s' существует", cookieName));
            }
            String actualPath = detailedCookie.getPath();
            Assertions.assertThat(actualPath)
                    .as("Ожидалось, что путь куки '%s' будет '%s', но был '%s'", cookieName, path, actualPath)
                    .isEqualTo(path);
        };
    }

    /**
     * Проверяет, что значение куки соответствует определенному регулярному выражению.
     *
     * @param cookieName имя куки
     * @return условие для проверки соответствия значения куки паттерну
     * @throws IllegalArgumentException если cookieName или pattern равно null
     * @regex регулярное выражение
     */
    public static CookieCondition cookieValueMatchesPattern(String cookieName, String regex) {
        Objects.requireNonNull(cookieName, "cookieName не может быть null");
        Objects.requireNonNull(regex, "regex не может быть null");
        return response -> {
            String actualValue = response.getCookie(cookieName);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что значение куки '%s' соответствует паттерну '%s', но было '%s'", cookieName, Pattern.compile(regex), actualValue)
                    .matches(regex);
        };
    }

    /**
     * Проверяет, что значение куки не пусто.
     *
     * @param cookieName имя куки
     * @return условие для проверки непустого значения куки
     * @throws IllegalArgumentException если cookieName равно null
     */
    public static CookieCondition cookieValueNotEmpty(String cookieName) {
        Objects.requireNonNull(cookieName, "cookieName не может быть null");
        return response -> {
            String actualValue = response.getCookie(cookieName);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что значение куки '%s' не пусто", cookieName)
                    .isNotEmpty();
        };
    }

    /**
     * Проверяет, что кука имеет определенное доменное имя и путь.
     *
     * @param cookieName имя куки
     * @param domain     ожидаемое доменное имя
     * @param path       ожидаемый путь
     * @return условие для проверки домена и пути куки
     * @throws IllegalArgumentException если cookieName, domain или path равно null
     */
    public static CookieCondition cookieDomainAndPathEquals(String cookieName, String domain, String path) {
        Objects.requireNonNull(cookieName, "cookieName не может быть null");
        Objects.requireNonNull(domain, "domain не может быть null");
        Objects.requireNonNull(path, "path не может быть null");
        return response -> {
            io.restassured.http.Cookie detailedCookie = response.getDetailedCookie(cookieName);
            if (detailedCookie == null) {
                throw new AssertionError(String.format("Ожидалось, что кука '%s' существует", cookieName));
            }
            String actualDomain = detailedCookie.getDomain();
            String actualPath = detailedCookie.getPath();
            Assertions.assertThat(actualDomain)
                    .as("Ожидалось, что домен куки '%s' будет '%s', но был '%s'", cookieName, domain, actualDomain)
                    .isEqualToIgnoringCase(domain);
            Assertions.assertThat(actualPath)
                    .as("Ожидалось, что путь куки '%s' будет '%s', но был '%s'", cookieName, path, actualPath)
                    .isEqualTo(path);
        };
    }

    /**
     * Проверяет, что кука имеет определенную дату истечения.
     *
     * @param cookieName     имя куки
     * @param expirationDate ожидаемая дата истечения
     * @return условие для проверки даты истечения куки
     * @throws IllegalArgumentException если cookieName или expirationDate равно null
     */
    public static CookieCondition cookieExpiresAt(String cookieName, java.util.Date expirationDate) {
        Objects.requireNonNull(cookieName, "cookieName не может быть null");
        Objects.requireNonNull(expirationDate, "expirationDate не может быть null");
        return response -> {
            io.restassured.http.Cookie detailedCookie = response.getDetailedCookie(cookieName);
            if (detailedCookie == null) {
                throw new AssertionError(String.format("Ожидалось, что кука '%s' существует", cookieName));
            }
            java.util.Date actualExpiration = detailedCookie.getExpiryDate();
            Assertions.assertThat(actualExpiration)
                    .as("Ожидалось, что дата истечения куки '%s' будет '%s', но была '%s'", cookieName, expirationDate, actualExpiration)
                    .isEqualTo(expirationDate);
        };
    }

    /**
     * Проверяет, что кука не имеет определенного атрибута.
     *
     * @param cookieName    имя куки
     * @param attributeName имя атрибута
     * @return условие для проверки отсутствия атрибута у куки
     * @throws IllegalArgumentException если cookieName или attributeName равно null
     */
    public static CookieCondition cookieDoesNotHaveAttribute(String cookieName, String attributeName) {
        Objects.requireNonNull(cookieName, "cookieName не может быть null");
        Objects.requireNonNull(attributeName, "attributeName не может быть null");
        String attrNameLower = attributeName.toLowerCase();
        return response -> {
            io.restassured.http.Cookie detailedCookie = response.getDetailedCookie(cookieName);
            if (detailedCookie == null) {
                throw new AssertionError(String.format("Ожидалось, что кука '%s' существует", cookieName));
            }

            boolean hasAttribute;
            switch (attrNameLower) {
                case "httponly":
                    hasAttribute = detailedCookie.isHttpOnly();
                    break;
                case "domain":
                    hasAttribute = detailedCookie.getDomain() != null;
                    break;
                case "path":
                    hasAttribute = detailedCookie.getPath() != null;
                    break;
                case "expires":
                    hasAttribute = detailedCookie.getExpiryDate() != null;
                    break;
                case "max-age":
                    hasAttribute = detailedCookie.getMaxAge() != -1;
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Неизвестный атрибут куки: '%s'", attributeName));
            }

            Assertions.assertThat(hasAttribute)
                    .as("Ожидалось, что кука '%s' не имеет атрибута '%s'", cookieName, attributeName)
                    .isFalse();
        };
    }

    /**
     * Проверяет, что кука имеет определенное имя.
     *
     * @param cookieName   имя куки
     * @param expectedName ожидаемое имя куки
     * @return условие для проверки имени куки
     * @throws IllegalArgumentException если cookieName или expectedName равно null
     */
    public static CookieCondition cookieNameEquals(String cookieName, String expectedName) {
        Objects.requireNonNull(cookieName, "cookieName не может быть null");
        Objects.requireNonNull(expectedName, "expectedName не может быть null");
        return response -> {
            io.restassured.http.Cookie detailedCookie = response.getDetailedCookie(cookieName);
            Assertions.assertThat(detailedCookie)
                    .as("Ожидалось, что кука '%s' существует", cookieName)
                    .isNotNull();
            String actualName = detailedCookie.getName();
            Assertions.assertThat(actualName)
                    .as("Ожидалось, что имя куки будет '%s', но было '%s'", expectedName, actualName)
                    .isEqualTo(expectedName);
        };
    }

    /**
     * Проверяет, что значение куки соответствует определенному паттерну.
     *
     * @param cookieName имя куки
     * @param pattern    паттерн для соответствия значению куки
     * @return условие для проверки соответствия значения куки паттерну
     * @throws IllegalArgumentException если cookieName или pattern равно null
     */
    public static CookieCondition cookieValueMatchesPattern(String cookieName, Pattern pattern) {
        Objects.requireNonNull(cookieName, "cookieName не может быть null");
        Objects.requireNonNull(pattern, "pattern не может быть null");
        return response -> {
            String actualValue = response.getCookie(cookieName);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что значение куки '%s' соответствует паттерну '%s', но было '%s'", cookieName, pattern.pattern(), actualValue)
                    .matches(pattern);
        };
    }

    /**
     * Проверяет, что кука имеет определенный размер значения.
     *
     * @param cookieName имя куки
     * @param minSize    минимальный размер значения куки
     * @param maxSize    максимальный размер значения куки
     * @return условие для проверки размера значения куки
     * @throws IllegalArgumentException если cookieName равно null или minSize > maxSize
     */
    public static CookieCondition cookieValueSizeBetween(String cookieName, int minSize, int maxSize) {
        Objects.requireNonNull(cookieName, "cookieName не может быть null");
        if (minSize < 0 || maxSize < 0) {
            throw new IllegalArgumentException("minSize и maxSize не могут быть отрицательными");
        }
        if (minSize > maxSize) {
            throw new IllegalArgumentException("minSize не может быть больше maxSize");
        }
        return response -> {
            String actualValue = response.getCookie(cookieName);
            int size = actualValue != null ? actualValue.length() : 0;
            Assertions.assertThat(size)
                    .as("Ожидалось, что размер значения куки '%s' будет между %d и %d, но был %d", cookieName, minSize, maxSize, size)
                    .isBetween(minSize, maxSize);
        };
    }

    /**
     * Проверяет, что кука имеет определенное количество символов в значении.
     *
     * @param cookieName имя куки
     * @param exactSize  точное количество символов
     * @return условие для проверки точного размера значения куки
     * @throws IllegalArgumentException если cookieName или exactSize отрицателен
     */
    public static CookieCondition cookieValueSizeEquals(String cookieName, int exactSize) {
        Objects.requireNonNull(cookieName, "cookieName не может быть null");
        if (exactSize < 0) {
            throw new IllegalArgumentException("exactSize не может быть отрицательным");
        }
        return response -> {
            String actualValue = response.getCookie(cookieName);
            int size = actualValue != null ? actualValue.length() : 0;
            Assertions.assertThat(size)
                    .as("Ожидалось, что размер значения куки '%s' будет %d, но был %d", cookieName, exactSize, size)
                    .isEqualTo(exactSize);
        };
    }
}