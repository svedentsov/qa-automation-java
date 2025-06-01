package com.svedentsov.ui.helper;

import com.codeborne.selenide.Configuration;
import com.svedentsov.config.ProjectConfig;
import io.qameta.allure.selenide.AllureSelenide;
import org.aeonbits.owner.ConfigFactory;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import static com.codeborne.selenide.Selenide.getWebDriverLogs;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.logevents.SelenideLogger.addListener;
import static java.lang.String.join;
import static org.openqa.selenium.logging.LogType.BROWSER;

public class DriverHelper {

    private static ProjectConfig getDriverConfig() {
        return ConfigFactory.newInstance().create(ProjectConfig.class, System.getProperties());
    }

    public static String getWebRemoteDriver() {
        // https://%s:%s@selenoid.autotests.cloud/wd/hub/
        return String.format(
                getDriverConfig().remoteDriverUrl(),
                getDriverConfig().remoteDriverUser(),
                getDriverConfig().remoteDriverPass());
    }

    public static boolean isRemoteWebDriver() {
        return !getDriverConfig().remoteDriverUrl().equals("");
    }

    public static String getVideoUrl() {
        return getDriverConfig().videoStorage();
    }

    public static boolean isVideoOn() {
        return !getVideoUrl().equals("");
    }

    public static String getSessionId() {
        return ((RemoteWebDriver) getWebDriver()).getSessionId().toString().replace("selenoid", "");
    }

    public static String getConsoleLogs() {
        return join("\n", getWebDriverLogs(BROWSER));
    }

    public static void configureDriver() {
        addListener("AllureSelenide", new AllureSelenide());

        Configuration.browser = getDriverConfig().browserName();
        Configuration.browserVersion = getDriverConfig().browserVersion();
        Configuration.browserSize = getDriverConfig().browserSize();

        DesiredCapabilities capabilities = new DesiredCapabilities();
        ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-infobars");
        chromeOptions.addArguments("--disable-popup-blocking");
        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.addArguments("--lang=en-en");

        if (isRemoteWebDriver()) {
            capabilities.setCapability("enableVNC", true);
            capabilities.setCapability("enableVideo", true);
            Configuration.remote = getWebRemoteDriver();
        }

        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        Configuration.browserCapabilities = capabilities;
    }
}
