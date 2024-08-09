package docs;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Класс для тестирования имен тестов в Allure.
 */
public class AllureNameTests {

    /**
     * Тест с использованием статического имени.
     */
    @Test
    @DisplayName("Статическое имя")
    public void annotationNameTest() {
    }

    /**
     * Тест с использованием динамического имени, задаваемого внутри теста.
     */
    @Test
    public void dynamicNameTest() {
        final String dynamicPart = "динамический параметр";
        final String dynamicName = String.format("Тест с параметром [%s]", dynamicPart);
        Allure.getLifecycle().updateTestCase(result -> result.setName(dynamicName));
    }
}
