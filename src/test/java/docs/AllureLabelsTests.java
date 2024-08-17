package docs;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.*;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static io.qameta.allure.SeverityLevel.CRITICAL;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Класс для тестирования различных меток в Allure.
 */
public class AllureLabelsTests {

    /**
     * Тест с использованием статических меток Epic, Feature, Story и Owner.
     */
    @Test
    @Epic("статический epic")
    @Feature("статическая Feature")
    @Story("статическая Story")
    @Owner("статический Owner")
    public void commonStaticLabelTest() {
    }

    /**
     * Тест с использованием различных статических меток и дополнительной информации.
     */
    @Test
    @Owner("svedentsov")
    @Severity(BLOCKER)
    @Feature("Работа с лейбочками")
    @Story("Над тестом можно проставить статические лейбочки")
    @DisplayName("Самый прекрасный тест")
    @Description("Этот тест проверяет очень важную функциональность...")
    @Link(name = "GitHub", url = "https://github.com")
    public void testStaticLabels() {
    }

    /**
     * Тест с динамическими метками Epic, Feature, Story и Owner.
     */
    @Test
    public void commonDynamicLabelTest() {
        Allure.epic("динамический epic");
        Allure.feature("динамическая функция");
        Allure.story("динамическая история");
        Allure.label("dynamic owner", "динамический владелец");
    }

    /**
     * Тест с динамическими метками, задаваемыми внутри теста.
     */
    @Test
    public void testDynamicLabels() {
        Allure.label("owner", "svedentsov");
        Allure.label("severity", CRITICAL.value());
        Allure.feature("Работа с лейбочками");
        Allure.story("Внутри теста можно выставлять динамические лейбочки");
        Allure.getLifecycle().updateTestCase(testResult -> testResult.setName("Не самый прекрасный тест"));
        Allure.getLifecycle().updateTestCase(testResult -> testResult.setDescription("Этот тест проверяет очень важную функциональность..."));
        Allure.link("GitHub", "https://guthub.com");
    }

    /**
     * Тест с параметрами, отображаемыми в Allure отчете.
     */
    @Test
    public void testParameters() {
        Allure.parameter("Город", "Москва");
        Allure.parameter("Область", "Московская");
    }

    /**
     * Тест с пользовательской статической меткой.
     */
    @Test
    @CustomLabel("статическое значение")
    public void customStaticLabelTest() {
    }

    /**
     * Тест с пользовательской динамической меткой.
     */
    @Test
    public void customDynamicLabelTest() {
        Allure.label("custom", "динамическое значение");
    }

    /**
     * Аннотация для создания пользовательских меток в Allure.
     */
    @Documented
    @Inherited
    @Retention(RUNTIME)
    @Target({METHOD, TYPE})
    @LabelAnnotation(name = "custom")
    public @interface CustomLabel {
        String value();
    }
}
