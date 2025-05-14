package rest.matcher;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import rest.matcher.assertions.BodyAssertions.BodyCondition;
import rest.matcher.assertions.CompositeAssertions;
import rest.matcher.assertions.CookieAssertions.CookieCondition;
import rest.matcher.assertions.HeaderAssertions.HeaderCondition;
import rest.matcher.assertions.StatusAssertions.StatusCondition;
import rest.matcher.assertions.TimeAssertions.TimeCondition;

/**
 * Утилитный класс для создания условий проверки HTTP-ответов.
 * Предоставляет методы для создания различных условий проверки заголовков, тела ответа, куки и прочего.
 * Этот класс облегчает создание условий (Condition) для проверки различных аспектов HTTP-ответа,
 * таких как статусный код, заголовки, тело ответа, куки и время ответа.
 */
@UtilityClass
public class RestMatcher {

    /**
     * Создаёт условие для проверки статусного кода ответа.
     *
     * @param sc условие для проверки статусного кода
     * @return {@link Condition} для проверки статусного кода
     */
    public static Condition status(StatusCondition sc) {
        return sc;
    }

    /**
     * Создаёт условие для проверки заголовков ответа.
     *
     * @param hc условие для проверки заголовков
     * @return {@link Condition} для проверки заголовков
     */
    public static Condition header(HeaderCondition hc) {
        return hc;
    }

    /**
     * Создаёт условие для проверки куки в ответе.
     *
     * @param cc условие для проверки куки
     * @return {@link Condition} для проверки куки
     */
    public static Condition cookie(CookieCondition cc) {
        return cc;
    }

    /**
     * Создаёт условие для проверки тела ответа.
     *
     * @param bc условие для проверки тела ответа
     * @return {@link Condition} для проверки тела ответа
     */
    public static Condition body(BodyCondition bc) {
        return bc;
    }

    /**
     * Создаёт условие для проверки времени ответа.
     *
     * @param tc условие для проверки времени ответа
     * @return {@link Condition} для проверки времени ответа
     */
    public static Condition responseTime(TimeCondition tc) {
        return tc;
    }
}
