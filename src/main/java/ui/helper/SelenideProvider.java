package ui.helper;

import com.codeborne.selenide.Configuration;
import core.config.ConfigManager;

public final class SelenideProvider {

    public static void init() {
        Configuration.baseUrl = ConfigManager.app().webUrl();
        Configuration.browser = ConfigManager.system().browserName();
        Configuration.timeout = ConfigManager.timeout().webdriverWaitTimeout().toMillis();
        Configuration.browserSize = ConfigManager.system().browserSize();
        Configuration.reportsFolder = "target/selenide-screenshots";
    }
}
