package common;

import core.allure.AttachmentHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ui.helper.SelenideProvider;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;

/**
 * Класс для UI-тестов, расширяющий базовый тестовый класс и включающий методы настройки, завершения и выполнения тестов.
 */
public class UITest extends BaseTest {

    /**
     * Метод, выполняемый перед всеми тестами. Инициализирует Selenide.
     */
    @BeforeAll
    public static void setup() {
        SelenideProvider.init();
    }

    /**
     * Метод, выполняемый после всех тестов. Закрывает WebDriver.
     */
    @AfterAll
    public static void tearDown() {
        closeWebDriver();
    }

    /**
     * Метод, выполняемый после каждого теста. Прикрепляет скриншот, исходный код страницы,
     * логи консоли браузера и видео (если настроено) к отчету.
     */
    @AfterEach
    public void afterEach() {
        AttachmentHelper.attachScreenshot("Последний скриншот");
        AttachmentHelper.attachPageSource();
        AttachmentHelper.attachAsText("Логи консоли браузера", AttachmentHelper.getConsoleLogs());
        if (System.getProperty("video.storage") != null) {
            AttachmentHelper.attachVideo();
        }
    }
}
