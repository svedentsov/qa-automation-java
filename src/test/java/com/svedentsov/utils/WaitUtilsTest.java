package com.svedentsov.utils;

import com.svedentsov.config.AppTimeoutConfig;
import com.svedentsov.config.PropertiesController;
import org.awaitility.core.ConditionFactory;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * Тестовый класс для {@link WaitUtils}.
 * <p>
 * Использует Mockito для имитации {@link PropertiesController}, чтобы избежать реальных
 * зависимостей и ускорить тесты, связанные со временем.
 */
@DisplayName("Утилиты для ожидания (WaitUtils)")
@ExtendWith(MockitoExtension.class)
class WaitUtilsTest {

    private static MockedStatic<PropertiesController> mockedPropertiesController;

    @Mock
    private static AppTimeoutConfig mockConfig;

    /**
     * Перед всеми тестами мокируем статический вызов {@link PropertiesController#appTimeoutConfig()}
     * и подставляем очень короткие таймауты для быстрого выполнения тестов.
     */
    @BeforeAll
    static void setup() {
        // Инициализируем мок для AppTimeoutConfig
        mockConfig = mock(AppTimeoutConfig.class);
        when(mockConfig.utilWaitTimeout()).thenReturn(Duration.ofMillis(100));
        when(mockConfig.utilWaitMediumTimeout()).thenReturn(Duration.ofMillis(150));
        when(mockConfig.utilWaitLongTimeout()).thenReturn(Duration.ofMillis(200));

        // Мокируем статический класс PropertiesController
        mockedPropertiesController = mockStatic(PropertiesController.class);
        mockedPropertiesController.when(PropertiesController::appTimeoutConfig).thenReturn(mockConfig);
    }

    /**
     * После всех тестов закрываем статический мок.
     */
    @AfterAll
    static void tearDown() {
        mockedPropertiesController.close();
    }

    /**
     * Тесты для методов, создающих фабрику условий ожидания (doWait...).
     */
    @Nested
    @DisplayName("Создание условий ожидания (doWait)")
    class DoWaitTests {

        /**
         * Проверяет, что фабрика условий успешно создается и может выполнить простое условие.
         */
        @Test
        @DisplayName("doWait() должен успешно выполнить условие, которое сразу истинно")
        void doWaitShouldSucceedOnTrueCondition() {
            assertDoesNotThrow(() -> WaitUtils.doWait().until(() -> true));
        }

        /**
         * Проверяет, что фабрика условий выбрасывает исключение, если условие не выполняется за таймаут.
         */
        @Test
        @DisplayName("doWait(timeout) должен выбросить исключение по таймауту")
        void doWaitShouldThrowExceptionOnTimeout() {
            // Используем очень короткий таймаут для быстрого теста
            final Duration testTimeout = Duration.ofMillis(50);
            assertThrows(ConditionTimeoutException.class,
                    () -> WaitUtils.doWait(testTimeout).until(() -> false)
            );
        }

        /**
         * Проверяет, что различные методы doWait* создают рабочую фабрику.
         */
        @Test
        @DisplayName("Различные методы doWait* должны создавать рабочую ConditionFactory")
        void variousDoWaitMethodsShouldCreateWorkingFactories() {
            Supplier<ConditionFactory>[] factories = new Supplier[]{
                    WaitUtils::doWait,
                    WaitUtils::doWaitWithDelay,
                    WaitUtils::doWaitMedium,
                    WaitUtils::doWaitMediumLong,
                    WaitUtils::doWaitLong
            };

            for (Supplier<ConditionFactory> factorySupplier : factories) {
                assertDoesNotThrow(() -> factorySupplier.get().until(() -> true),
                        "Фабрика " + factorySupplier.toString() + " не сработала");
            }
        }
    }

    /**
     * Тесты для метода {@link WaitUtils#waitAssertCondition(org.awaitility.core.ThrowingRunnable, Duration)}.
     */
    @Nested
    @DisplayName("Проверка условия с ожиданием (waitAssertCondition)")
    class WaitAssertConditionTests {

        /**
         * Проверяет, что метод возвращает true, если условие выполнено.
         */
        @Test
        @DisplayName("Должен возвращать true, если условие выполнено")
        void shouldReturnTrueWhenConditionSucceeds() {
            boolean result = WaitUtils.waitAssertCondition(() -> assertEquals(1, 1));
            assertTrue(result);
        }

        /**
         * Проверяет, что метод возвращает false, если условие не выполнено за таймаут.
         */
        @Test
        @DisplayName("Должен возвращать false, если условие не выполнено (таймаут)")
        void shouldReturnFalseWhenConditionFails() {
            // Используем очень короткий таймаут
            boolean result = WaitUtils.waitAssertCondition(() -> fail("Это условие всегда ложно"), Duration.ofMillis(50));
            assertFalse(result);
        }
    }

    /**
     * Тесты для метода {@link WaitUtils#repeatAction(java.util.concurrent.Callable, java.util.function.Predicate)}.
     */
    @Nested
    @DisplayName("Повторное выполнение действия (repeatAction)")
    class RepeatActionTests {

        /**
         * Проверяет, что действие, успешное с первой попытки, возвращает правильный результат.
         */
        @Test
        @DisplayName("Должен вернуть результат, если действие успешно с 1-й попытки")
        void shouldReturnResultOnFirstTry() {
            String result = WaitUtils.repeatAction(() -> "success");
            assertEquals("success", result);
        }

        /**
         * Проверяет, что действие, которое становится успешным после нескольких попыток, возвращает результат.
         */
        @Test
        @DisplayName("Должен вернуть результат после нескольких неудачных попыток")
        void shouldReturnResultAfterSeveralTries() {
            AtomicInteger counter = new AtomicInteger(0);
            String result = WaitUtils.repeatAction(() ->
                    counter.incrementAndGet() < 3 ? null : "final success"
            );
            assertEquals("final success", result);
            assertEquals(3, counter.get());
        }

        /**
         * Проверяет поведение, когда условие никогда не выполняется.
         * Согласно реализации, после таймаута действие вызывается еще раз.
         */
        @Test
        @DisplayName("Должен вернуть результат последнего вызова после таймаута")
        void shouldReturnLastResultOnTimeout() {
            String result = WaitUtils.repeatAction(() -> null);
            assertNull(result, "Если условие никогда не истинно, результат должен быть null");
        }

        /**
         * Проверяет работу с пользовательским предикатом.
         */
        @Test
        @DisplayName("Должен работать с пользовательским предикатом")
        void shouldWorkWithCustomPredicate() {
            AtomicInteger counter = new AtomicInteger(0);
            Integer result = WaitUtils.repeatAction(
                    counter::incrementAndGet,
                    (value) -> value > 5 // Условие: значение должно быть больше 5
            );
            assertEquals(6, result);
        }
    }
}
