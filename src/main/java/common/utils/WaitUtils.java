package common.utils;

import common.config.PropertiesController;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.ConditionFactory;
import org.awaitility.core.ConditionTimeoutException;
import org.awaitility.core.ThrowingRunnable;

import java.net.ConnectException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

import static common.utils.DateUtil.convert;
import static common.utils.StrUtil.EMPTY;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Duration.ONE_SECOND;

/**
 * Утилитарный класс для выполнения различных операций ожидания.
 * Обеспечивает методы для гибкого управления таймаутами и интервалами опроса.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WaitUtils {

    public static final Duration TIMEOUT = PropertiesController.appTimeoutConfig().utilWaitTimeout();
    public static final Duration TIMEOUT_MEDIUM = PropertiesController.appTimeoutConfig().utilWaitMediumTimeout();
    public static final Duration TIMEOUT_LONG = PropertiesController.appTimeoutConfig().utilWaitLongTimeout();
    private static final Duration INTERVAL = Duration.of(1, ChronoUnit.SECONDS);
    private static final Duration DELAY = Duration.of(3, ChronoUnit.SECONDS);
    private static final String LOG_MESSAGE = "{} (прошло времени: {} мс, оставшееся время: {} мс)\n";
    private static final String LOG_MESSAGE_SHORT = "оставшееся время {} мс";
    private static String logDescription = EMPTY;

    /**
     * Создает фабрику условий с использованием стандартного интервала и таймаута.
     *
     * @return {@link ConditionFactory} для настройки ожидания
     */
    public static ConditionFactory doWait() {
        return doWait(INTERVAL, TIMEOUT);
    }

    /**
     * Создает фабрику условий с задержкой перед началом ожидания.
     *
     * @return {@link ConditionFactory} для настройки ожидания с задержкой
     */
    public static ConditionFactory doWaitWithDelay() {
        return doWait(INTERVAL, TIMEOUT).pollDelay(convert(DELAY));
    }

    /**
     * Создает фабрику условий с заданным таймаутом.
     *
     * @param timeout длительность таймаута
     * @return {@link ConditionFactory} для настройки ожидания
     */
    public static ConditionFactory doWait(Duration timeout) {
        return doWait(INTERVAL.multipliedBy(3), timeout);
    }

    /**
     * Создает фабрику условий с промежуточным таймаутом.
     *
     * @return {@link ConditionFactory} для настройки ожидания
     */
    public static ConditionFactory doWaitMedium() {
        return doWait(INTERVAL.multipliedBy(3), TIMEOUT_MEDIUM);
    }

    /**
     * Создает фабрику условий с более длинным промежуточным таймаутом.
     *
     * @return {@link ConditionFactory} для настройки ожидания
     */
    public static ConditionFactory doWaitMediumLong() {
        return doWait(INTERVAL.multipliedBy(5), TIMEOUT_MEDIUM.multipliedBy(3));
    }

    /**
     * Создает фабрику условий с длинным таймаутом.
     *
     * @return {@link ConditionFactory} для настройки ожидания
     */
    public static ConditionFactory doWaitLong() {
        return doWait(INTERVAL.multipliedBy(10), TIMEOUT_LONG);
    }

    /**
     * Выполняет условие ожидания и проверяет его успешность.
     *
     * @param condition условие для проверки
     * @return {@code true}, если условие выполнено успешно, иначе {@code false}
     */
    public static boolean waitAssertCondition(ThrowingRunnable condition) {
        return waitAssertCondition(condition, TIMEOUT_MEDIUM);
    }

    /**
     * Выполняет условие ожидания с заданным таймаутом и проверяет его успешность.
     *
     * @param condition условие для проверки
     * @param timeout   длительность таймаута
     * @return {@code true}, если условие выполнено успешно, иначе {@code false}
     */
    public static boolean waitAssertCondition(ThrowingRunnable condition, Duration timeout) {
        var result = true;
        Duration interval = getAdjustedInterval(timeout);
        try {
            doWait(interval, timeout).untilAsserted(condition);
        } catch (ConditionTimeoutException e) {
            result = false;
            log.debug("Таймаут условия через {} мс: {}", timeout.toMillis(), e.getMessage());
        }
        return result;
    }

    public static <T> T repeatAction(Callable<T> action) {
        return repeatAction(action, Objects::nonNull);
    }

    @SneakyThrows
    public static <T> T repeatAction(Callable<T> action, Predicate<T> condition) {
        try {
            return doWaitMedium().ignoreExceptions().until(action, condition::test);
        } catch (ConditionTimeoutException e) {
            log.debug("Unable to reach condition due to '{}', repeat...", e.getMessage());
            return action.call();
        }
    }

    /**
     * Создает фабрику условий с заданным интервалом и таймаутом.
     *
     * @param pollInterval интервал опроса
     * @param timeout      длительность таймаута
     * @return {@link ConditionFactory} для настройки ожидания
     */
    public static ConditionFactory doWait(Duration pollInterval, Duration timeout) {
        return await().ignoreException(ConnectException.class)
                .pollInSameThread()
                .conditionEvaluationListener(condition -> {
                    if (!logDescription.equals(condition.getDescription())) {
                        logDescription = condition.getDescription();
                        log.trace(LOG_MESSAGE, condition.getDescription(),
                                condition.getElapsedTimeInMS(), condition.getRemainingTimeInMS());
                    } else {
                        log.trace(LOG_MESSAGE_SHORT, condition.getRemainingTimeInMS());
                    }
                })
                .atMost(convert(timeout))
                .pollInterval(convert(pollInterval))
                .pollDelay(ONE_SECOND); // пропуск ожидания на 1-й итерации
    }

    /**
     * Возвращает скорректированный интервал ожидания в зависимости от значения таймаута.
     *
     * @param timeout длительность таймаута
     * @return скорректированный интервал ожидания
     */
    private static Duration getAdjustedInterval(Duration timeout) {
        long minutes = timeout.toMinutes();
        if (minutes <= 1) {
            return INTERVAL.multipliedBy(3);
        } else if (minutes <= 3) {
            return INTERVAL.multipliedBy(5);
        } else {
            return INTERVAL.multipliedBy(10);
        }
    }
}
