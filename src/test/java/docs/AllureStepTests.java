package docs;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Test;

/**
 * Класс для тестирования шагов в Allure.
 */
public class AllureStepTests {

    private static final String GLOBAL_PARAMETER = "глобальное значение";

    /**
     * Тест с использованием аннотированных шагов.
     */
    @Test
    public void annotatedStepTest() {
        annotatedStep("локальное значение");
    }

    /**
     * Тест с использованием шагов, определенных с помощью лямбд.
     */
    @Test
    public void lambdaStepTest() {
        final String localParameter = "значение параметра";
        Allure.step(String.format("Родительский лямбда-шаг с параметром [%s]", localParameter), (step) -> {
            step.parameter("параметр", localParameter);
            Allure.step(String.format("Вложенный лямбда-шаг с глобальным параметром [%s]", GLOBAL_PARAMETER));
        });
    }

    /**
     * Родительский аннотированный шаг с параметром.
     *
     * @param parameter локальный параметр
     */
    @Step("Родительский аннотированный шаг с параметром [{parameter}]")
    public void annotatedStep(final String parameter) {
        nestedAnnotatedStep();
    }

    /**
     * Вложенный аннотированный шаг с глобальным параметром.
     */
    @Step("Вложенный аннотированный шаг с глобальным параметром [{this.GLOBAL_PARAMETER}]")
    public void nestedAnnotatedStep() {
    }
}
