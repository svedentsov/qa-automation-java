package docs;

import io.qameta.allure.Description;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.qameta.allure.Allure.parameter;
import static io.qameta.allure.Allure.step;

/**
 * Класс для параметризованных тестов JUnit 5 с использованием Allure.
 */
public class JUnit5ParameterizedTests {

    /**
     * Параметризованный тест с использованием Allure.
     *
     * @param name параметр теста
     */
    @ParameterizedTest(name = "{displayName} [{argumentsWithNames}]")
    @ValueSource(strings = {"John", "Mike"})
    @DisplayName("allureParameterizedTest displayName")
    @Description("allureParameterizedTest description")
    public void allureParameterizedTest(String name) {
        parameter("Name", name);
    }

    /**
     * Фиктивный параметризованный тест с использованием Allure.
     */
    @Test
    public void allureFakeParameterizedTest() {
        parameter("fakeParam", "fakeValue");
        step("Шаг внутри фиктивного параметризованного теста");
    }
}
