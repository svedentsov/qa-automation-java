package docs;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Test;

/**
 * Класс для тестирования описаний в Allure.
 */
public class AllureDescriptionTests {

    /**
     * Тест с аннотированным статическим описанием.
     */
    @Test
    @Description("Статическое описание")
    public void annotationDescriptionTest() {
    }

    /**
     * Тест с использованием описания из Javadoc.
     */
    @Test
    @Description(useJavaDoc = true)
    public void javadocDescriptionTest() {
    }

    /**
     * Тест с динамическим описанием.
     */
    @Test
    public void dynamicDescriptionTest() {
        Allure.description("Динамическое описание");
    }
}
