package com.svedentsov.utils;

import com.svedentsov.config.PropertiesController;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionFactory;
import org.awaitility.core.ConditionTimeoutException;
import org.awaitility.core.ThrowingRunnable;

import java.net.ConnectException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

/**
 * Утилитарный класс для выполнения различных операций ожидания.
 * Обеспечивает методы для гибкого управления таймаутами и интервалами опроса.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WaitUtils {
    /**
     * Стандартный таймаут для большинства операций.
     */
    public static final Duration TIMEOUT = PropertiesController.appTimeoutConfig().utilWaitTimeout();
    /**
     * Увеличенный таймаут для операций, требующих больше времени.
     */
    public static final Duration TIMEOUT_MEDIUM = PropertiesController.appTimeoutConfig().utilWaitMediumTimeout();
    /**
     * Длительный таймаут для самых долгих операций (например, обработка больших объемов данных).
     */
    public static final Duration TIMEOUT_LONG = PropertiesController.appTimeoutConfig().utilWaitLongTimeout();
    private static final Duration DEFAULT_POLL_INTERVAL = Duration.of(1, ChronoUnit.SECONDS);
    private static final Duration DEFAULT_POLL_DELAY = Duration.ZERO; // По умолчанию начинаем опрос сразу

    /**
     * Ожидает, пока указанный поставщик (supplier) не вернет не-null значение, и возвращает его.
     * Использует таймаут по умолчанию (TIMEOUT_MEDIUM).
     * <p>Идеально подходит для сценария "дождаться появления записи/объекта и получить его для дальнейших проверок".
     *
     * @param description   Описание ожидания. Будет выведено в лог при ошибке таймаута.
     * @param valueSupplier {@link Callable}, который пытается получить значение. Ожидание прекратится, когда результат будет не {@code null}.
     * @param <T>           Тип ожидаемого значения.
     * @return Полученное не-null значение.
     * @throws ConditionTimeoutException если таймаут истек, а поставщик так и не вернул не-null значение.
     */
    public static <T> T waitUntilAndGet(String description, Callable<T> valueSupplier) {
        return waitUntilAndGet(description, valueSupplier, Objects::nonNull, TIMEOUT_MEDIUM);
    }

    /**
     * Ожидает, пока указанный поставщик (supplier) не вернет значение, удовлетворяющее предикату, и возвращает это значение.
     * Использует таймаут по умолчанию (TIMEOUT_MEDIUM).
     *
     * @param description   Описание ожидания.
     * @param valueSupplier {@link Callable}, который поставляет значение для проверки.
     * @param condition     {@link Predicate}, которому должно удовлетворять полученное значение.
     * @param <T>           Тип ожидаемого значения.
     * @return Полученное значение, удовлетворяющее условию.
     * @throws ConditionTimeoutException если таймаут истек, а условие не было выполнено.
     */
    public static <T> T waitUntilAndGet(String description, Callable<T> valueSupplier, Predicate<T> condition) {
        return waitUntilAndGet(description, valueSupplier, condition, TIMEOUT_MEDIUM);
    }

    /**
     * Ожидает, пока указанный поставщик (supplier) не вернет значение, удовлетворяющее предикату, и возвращает это значение,
     * используя заданный таймаут.
     *
     * @param description   Описание ожидания.
     * @param valueSupplier {@link Callable}, который поставляет значение для проверки.
     * @param condition     {@link Predicate}, которому должно удовлетворять полученное значение.
     * @param timeout       Максимальное время ожидания.
     * @param <T>           Тип ожидаемого значения.
     * @return Полученное значение, удовлетворяющее условию.
     * @throws ConditionTimeoutException если таймаут истек, а условие не было выполнено.
     */
    public static <T> T waitUntilAndGet(String description, Callable<T> valueSupplier, Predicate<T> condition, Duration timeout) {
        log.info("Ожидание: '{}'. Таймаут: {} секунд.", description, timeout.toSeconds());
        return buildWaitCondition(description, timeout)
                .until(valueSupplier, condition);
    }

    /**
     * Ожидает, пока условное выражение ({@code Callable<Boolean>}) не вернет {@code true}.
     * Использует таймаут по умолчанию (TIMEOUT_MEDIUM).
     *
     * @param description        Описание ожидания.
     * @param conditionEvaluator {@link Callable}, возвращающий {@code boolean}. Ожидание завершится, когда вернется {@code true}.
     * @throws ConditionTimeoutException если таймаут истек, а условие не стало истинным.
     */
    public static void waitUntilCondition(String description, Callable<Boolean> conditionEvaluator) {
        waitUntilCondition(description, conditionEvaluator, TIMEOUT_MEDIUM);
    }

    /**
     * Ожидает, пока условное выражение ({@code Callable<Boolean>}) не вернет {@code true}, используя заданный таймаут.
     *
     * @param description        Описание ожидания.
     * @param conditionEvaluator {@link Callable}, возвращающий {@code boolean}.
     * @param timeout            Максимальное время ожидания.
     * @throws ConditionTimeoutException если таймаут истек, а условие не стало истинным.
     */
    public static void waitUntilCondition(String description, Callable<Boolean> conditionEvaluator, Duration timeout) {
        log.info("Ожидание условия: '{}'. Таймаут: {} секунд.", description, timeout.toSeconds());
        buildWaitCondition(description, timeout).until(conditionEvaluator);
    }

    /**
     * Ожидает, пока блок кода с ассерциями (проверками) не будет выполнен без исключений.
     * Использует таймаут по умолчанию (TIMEOUT_MEDIUM).
     *
     * @param description Описание группы проверок.
     * @param assertions  {@link ThrowingRunnable} блок кода, содержащий одну или несколько проверок (например, Hamcrest, AssertJ).
     * @throws ConditionTimeoutException если таймаут истек, а блок кода все еще выбрасывает исключение {@link AssertionError}.
     */
    public static void waitUntilAsserted(String description, ThrowingRunnable assertions) {
        waitUntilAsserted(description, assertions, TIMEOUT_MEDIUM);
    }

    /**
     * Ожидает, пока блок кода с ассерциями (проверками) не будет выполнен без исключений, используя заданный таймаут.
     *
     * @param description Описание группы проверок.
     * @param assertions  {@link ThrowingRunnable} блок кода, содержащий одну или несколько проверок.
     * @param timeout     Максимальное время ожидания.
     * @throws ConditionTimeoutException если таймаут истек, а блок кода все еще выбрасывает исключение.
     */
    public static void waitUntilAsserted(String description, ThrowingRunnable assertions, Duration timeout) {
        log.info("Ожидание выполнения ассерта: '{}'. Таймаут: {} секунд.", description, timeout.toSeconds());
        buildWaitCondition(description, timeout).untilAsserted(assertions);
    }

    /**
     * Проверяет, выполняется ли условие в течение заданного таймаута, но НЕ пробрасывает исключение в случае неудачи.
     * <p><b>Внимание:</b> Этот метод следует использовать только тогда, когда падение теста не является желаемым результатом,
     * а требуется лишь булев флаг для дальнейшей логики в тесте. В большинстве случаев предпочтительнее
     * использовать {@link #waitUntilAsserted(String, ThrowingRunnable)}.
     *
     * @param description Описание проверяемого условия.
     * @param condition   Условие для проверки.
     * @param timeout     Длительность таймаута.
     * @return {@code true}, если условие было выполнено успешно в течение таймаута, иначе {@code false}.
     */
    public static boolean checkConditionWithoutException(String description, ThrowingRunnable condition, Duration timeout) {
        try {
            waitUntilAsserted(description, condition, timeout);
            return true;
        } catch (ConditionTimeoutException e) {
            log.warn("Условие '{}' не было выполнено за {} мс. Подавление исключения.", description, timeout.toMillis());
            return false;
        }
    }

    /**
     * Перегруженная версия {@link #checkConditionWithoutException(String, ThrowingRunnable, Duration)} с таймаутом по умолчанию (TIMEOUT_MEDIUM).
     */
    public static boolean checkConditionWithoutException(String description, ThrowingRunnable condition) {
        return checkConditionWithoutException(description, condition, TIMEOUT_MEDIUM);
    }

    /**
     * Повторяет выполнение действия до тех пор, пока его результат не будет удовлетворять предикату.
     * В случае таймаута, возвращает последнее полученное значение.
     * <p><b>Внимание:</b> Этот метод может вернуть невалидный результат, если условие так и не будет выполнено.
     * Рекомендуется использовать {@link #waitUntilAndGet(String, Callable, Predicate)}, если требуется гарантированно
     * получить валидный объект или уронить тест.
     *
     * @param action    {@link Callable} действие, которое нужно повторять.
     * @param condition {@link Predicate} условие, которому должен соответствовать результат.
     * @return Результат выполнения действия.
     */
    @SneakyThrows
    public static <T> T repeatActionUntil(Callable<T> action, Predicate<T> condition) {
        try {
            return waitUntilAndGet("Повторение действия до выполнения условия", action, condition, TIMEOUT_MEDIUM);
        } catch (ConditionTimeoutException e) {
            log.warn("Условие для repeatAction не выполнилось. Возвращается последний результат. Ошибка: {}", e.getMessage());
            // Возвращаем последний результат, даже если он не соответствует условию
            return action.call();
        }
    }

    /**
     * Создает и базово настраивает {@link ConditionFactory} для дальнейшего использования.
     *
     * @param description Описание, которое будет использовано в логах при таймауте.
     * @param timeout     Максимальное время ожидания.
     * @return Настроенный экземпляр {@link ConditionFactory}.
     */
    private static ConditionFactory buildWaitCondition(String description, Duration timeout) {
        return buildWaitCondition(description, timeout, getAdjustedInterval(timeout), DEFAULT_POLL_DELAY);
    }

    /**
     * Основной метод для создания и настройки {@link ConditionFactory}.
     *
     * @param description  Описание для логов.
     * @param timeout      Максимальное время ожидания.
     * @param pollInterval Интервал между попытками.
     * @param pollDelay    Начальная задержка перед первой попыткой.
     * @return Настроенный экземпляр {@link ConditionFactory}.
     */
    private static ConditionFactory buildWaitCondition(String description, Duration timeout, Duration pollInterval, Duration pollDelay) {
        return Awaitility.await()
                .ignoreException(ConnectException.class) // Игнорируем частые сетевые проблемы
                .atMost(timeout)
                .pollDelay(pollDelay)
                .pollInterval(pollInterval);
    }

    /**
     * Рассчитывает интервал опроса на основе общего таймаута для оптимизации.
     * Чем дольше ожидание, тем реже опросы, чтобы не перегружать систему.
     *
     * @param timeout Общая длительность таймаута.
     * @return Рассчитанный интервал опроса.
     */
    private static Duration getAdjustedInterval(Duration timeout) {
        long seconds = timeout.toSeconds();
        if (seconds <= 15) {
            return DEFAULT_POLL_INTERVAL; // 1 секунда
        } else if (seconds <= 60) {
            return Duration.of(2, ChronoUnit.SECONDS); // 2 секунды
        } else {
            return Duration.of(5, ChronoUnit.SECONDS); // 5 секунд
        }
    }
}
