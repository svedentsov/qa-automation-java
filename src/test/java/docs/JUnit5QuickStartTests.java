package docs;

import io.qameta.allure.Step;
import io.qameta.allure.model.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;

/**
 * Класс для быстрых тестов JUnit 5 с использованием Allure.
 */
public class JUnit5QuickStartTests {

    /**
     * Простой тест с использованием шагов Allure.
     */
    @Test
    @DisplayName("allureSimpleTest displayName")
    public void allureSimpleTest() {
        step("Простой шаг");
        step("Простой шаг со статусом", Status.FAILED);
        step("Простой шаг с использованием лямбда-выражения", () -> {
            step("Простой шаг внутри лямбда-шаг");
        });
        simpleTestMethod("параметр метода");
    }

    /**
     * Простой тестовый метод с аннотацией шага.
     *
     * @param param параметр метода
     */
    @Step("Простой тестовый метод с аннотацией шага")
    public void simpleTestMethod(String param) {
        step("Параметр метода: " + param);
        step("Простой шаг внутри тестового метода");
    }
}
