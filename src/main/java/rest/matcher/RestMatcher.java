package rest.matcher;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import rest.matcher.assertions.BodyAssertions.BodyCondition;
import rest.matcher.assertions.CompositeAssertions;
import rest.matcher.assertions.CookieAssertions.CookieCondition;
import rest.matcher.assertions.HeaderAssertions.HeaderCondition;
import rest.matcher.assertions.StatusAssertions.StatusCondition;
import rest.matcher.assertions.TimeAssertions.TimeCondition;
import rest.matcher.condition.Condition;

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
        return response -> sc.check(response);
    }

    /**
     * Создаёт условие для проверки заголовков ответа.
     *
     * @param hc условие для проверки заголовков
     * @return {@link Condition} для проверки заголовков
     */
    public static Condition header(HeaderCondition hc) {
        return response -> hc.check(response);
    }

    /**
     * Создаёт условие для проверки куки в ответе.
     *
     * @param cc условие для проверки куки
     * @return {@link Condition} для проверки куки
     */
    public static Condition cookie(CookieCondition cc) {
        return response -> cc.check(response);
    }

    /**
     * Создаёт условие для проверки тела ответа.
     *
     * @param bc условие для проверки тела ответа
     * @return {@link Condition} для проверки тела ответа
     */
    public static Condition body(BodyCondition bc) {
        return response -> bc.check(response);
    }

    /**
     * Создаёт условие для проверки времени ответа.
     *
     * @param tc условие для проверки времени ответа
     * @return {@link Condition} для проверки времени ответа
     */
    public static Condition responseTime(TimeCondition tc) {
        return response -> tc.check(response);
    }

    // ------------------- Композитные Условия -------------------

    /**
     * Создаёт композитное условие, которое проходит только в том случае, если выполнены все указанные условия (логическое И).
     *
     * @param conditions массив условий, которые должны быть выполнены
     * @return композитное условие, требующее выполнения всех условий
     * @throws NullPointerException если {@code conditions} содержит {@code null}
     */
    public static Condition allOf(@NonNull Condition... conditions) {
        return CompositeAssertions.allOf(conditions);
    }

    /**
     * Создаёт композитное условие, которое проходит, если выполнено любое из указанных условий (логическое ИЛИ).
     *
     * @param conditions массив условий, из которых должно быть выполнено хотя бы одно
     * @return композитное условие, требующее выполнения хотя бы одного условия
     * @throws NullPointerException если {@code conditions} содержит {@code null}
     */
    public static Condition anyOf(@NonNull Condition... conditions) {
        return CompositeAssertions.anyOf(conditions);
    }

    /**
     * Создаёт композитное условие, которое инвертирует результат указанных условий (логическое НЕ).
     *
     * @param conditions массив условий, которые будут инвертированы
     * @return композитное условие, требующее невыполнения всех условий
     * @throws NullPointerException если {@code conditions} содержит {@code null}
     */
    public static Condition not(@NonNull Condition... conditions) {
        return CompositeAssertions.not(conditions);
    }

    /**
     * Создаёт композитное условие, которое проходит, если выполнены хотя бы {@code n} из указанных условий.
     *
     * @param n          минимальное количество условий, которые должны быть выполнены
     * @param conditions массив условий, из которых должно быть выполнено {@code n} штук
     * @return композитное условие, требующее выполнения не менее {@code n} условий
     * @throws NullPointerException     если {@code conditions} содержит {@code null}
     * @throws IllegalArgumentException если {@code n} меньше или равно нулю
     */
    public static Condition nOf(int n, @NonNull Condition... conditions) {
        return CompositeAssertions.nOf(n, conditions);
    }
}
