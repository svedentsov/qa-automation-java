package rest.matcher.assertions;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.HamcrestCondition;
import org.hamcrest.Matcher;
import rest.matcher.condition.Condition;

import java.util.regex.Pattern;

/**
 * Класс для утверждений, связанных с куки в ответе.
 */
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
     */
    public static CookieCondition cookieEquals(String cookieName, String expectedValue) {
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
     */
    public static CookieCondition cookieExists(String cookieName) {
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
     */
    public static CookieCondition cookieMatches(String cookieName, Matcher<?> matcher) {
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
     */
    public static CookieCondition cookieStartsWith(String cookieName, String prefix) {
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
     */
    public static CookieCondition cookieEndsWith(String cookieName, String suffix) {
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
     */
    public static CookieCondition cookieDomainEquals(String cookieName, String domain) {
        return response -> {
            io.restassured.http.Cookie detailedCookie = response.getDetailedCookie(cookieName);
            if (detailedCookie == null) {
                throw new AssertionError(String.format("Ожидалось, что кука '%s' существует", cookieName));
            }
            Assertions.assertThat(detailedCookie.getDomain())
                    .as("Ожидалось, что домен куки '%s' будет '%s', но был '%s'", cookieName, domain, detailedCookie.getDomain())
                    .isEqualToIgnoringCase(domain);
        };
    }

    /**
     * Проверяет, что кука имеет определенный путь.
     *
     * @param cookieName имя куки
     * @param path       ожидаемый путь
     * @return условие для проверки пути куки
     */
    public static CookieCondition cookiePathEquals(String cookieName, String path) {
        return response -> {
            io.restassured.http.Cookie detailedCookie = response.getDetailedCookie(cookieName);
            if (detailedCookie == null) {
                throw new AssertionError(String.format("Ожидалось, что кука '%s' существует", cookieName));
            }
            Assertions.assertThat(detailedCookie.getPath())
                    .as("Ожидалось, что путь куки '%s' будет '%s', но был '%s'", cookieName, path, detailedCookie.getPath())
                    .isEqualTo(path);
        };
    }

    /**
     * Проверяет, что значение куки соответствует указанному регулярному выражению.
     *
     * @param cookieName имя куки
     * @param regex      регулярное выражение
     * @return условие для проверки соответствия значения куки шаблону
     */
    public static CookieCondition cookieValueMatchesRegex(String cookieName, String regex) {
        return response -> {
            String actualValue = response.getCookie(cookieName);
            Pattern pattern = Pattern.compile(regex);
            Assertions.assertThat(actualValue)
                    .as("Ожидалось, что значение куки '%s' соответствует шаблону '%s', но было '%s'", cookieName, regex, actualValue)
                    .matches(pattern);
        };
    }

    /**
     * Проверяет, что значение куки не пусто.
     *
     * @param cookieName имя куки
     * @return условие для проверки непустого значения куки
     */
    public static CookieCondition cookieValueNotEmpty(String cookieName) {
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
     */
    public static CookieCondition cookieDomainAndPathEquals(String cookieName, String domain, String path) {
        return response -> {
            io.restassured.http.Cookie detailedCookie = response.getDetailedCookie(cookieName);
            if (detailedCookie == null) {
                throw new AssertionError(String.format("Ожидалось, что кука '%s' существует", cookieName));
            }
            Assertions.assertThat(detailedCookie.getDomain())
                    .as("Ожидалось, что домен куки '%s' будет '%s', но был '%s'", cookieName, domain, detailedCookie.getDomain())
                    .isEqualToIgnoringCase(domain);
            Assertions.assertThat(detailedCookie.getPath())
                    .as("Ожидалось, что путь куки '%s' будет '%s', но был '%s'", cookieName, path, detailedCookie.getPath())
                    .isEqualTo(path);
        };
    }

    /**
     * Проверяет, что кука имеет определенную дату истечения.
     *
     * @param cookieName     имя куки
     * @param expirationDate ожидаемая дата истечения
     * @return условие для проверки даты истечения куки
     */
    public static CookieCondition cookieExpiresAt(String cookieName, java.util.Date expirationDate) {
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
     */
    public static CookieCondition cookieDoesNotHaveAttribute(String cookieName, String attributeName) {
        return response -> {
            io.restassured.http.Cookie detailedCookie = response.getDetailedCookie(cookieName);
            if (detailedCookie == null) {
                throw new AssertionError(String.format("Ожидалось, что кука '%s' существует", cookieName));
            }

            boolean hasAttribute = switch (attributeName.toLowerCase()) {
                case "httponly" -> detailedCookie.isHttpOnly();
                case "domain" -> detailedCookie.getDomain() != null;
                case "path" -> detailedCookie.getPath() != null;
                case "expires" -> detailedCookie.getExpiryDate() != null;
                case "max-age" -> detailedCookie.getMaxAge() != -1;
                default ->
                        throw new IllegalArgumentException(String.format("Неизвестный атрибут куки: '%s'", attributeName));
            };

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
     */
    public static CookieCondition cookieNameEquals(String cookieName, String expectedName) {
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
     */
    public static CookieCondition cookieValueMatchesPattern(String cookieName, Pattern pattern) {
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
     */
    public static CookieCondition cookieValueSizeBetween(String cookieName, int minSize, int maxSize) {
        return response -> {
            String actualValue = response.getCookie(cookieName);
            int size = actualValue.length();
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
     */
    public static CookieCondition cookieValueSizeEquals(String cookieName, int exactSize) {
        return response -> {
            String actualValue = response.getCookie(cookieName);
            int size = actualValue.length();
            Assertions.assertThat(size)
                    .as("Ожидалось, что размер значения куки '%s' будет %d, но был %d", cookieName, exactSize, size)
                    .isEqualTo(exactSize);
        };
    }
}
