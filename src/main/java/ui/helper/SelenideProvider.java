package ui.helper;

import com.codeborne.selenide.Configuration;
import core.config.ConfigManager;

/**
 * Класс для инициализации конфигурации Selenide.
 * Настраивает основные параметры для тестов, используя конфигурации из {@link ConfigManager}.
 */
public final class SelenideProvider {

    /**
     * Инициализирует конфигурацию Selenide.
     * Устанавливает базовый URL, имя браузера, таймаут ожидания, размер окна браузера и папку для отчетов.
     */
    public static void init() {
        Configuration.baseUrl = ConfigManager.app().webUrl();
        Configuration.browser = ConfigManager.system().browserName();
        Configuration.timeout = ConfigManager.timeout().webdriverWaitTimeout().toMillis();
        Configuration.browserSize = ConfigManager.system().browserSize();
        Configuration.reportsFolder = "target/selenide-screenshots";
    }
}
