package com.svedentsov.core.allure;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.nio.charset.StandardCharsets;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.openqa.selenium.logging.LogType.BROWSER;

/**
 * Вспомогательный класс для прикрепления различных типов данных к Allure отчету.
 */
public class AttachmentHelper {

    /**
     * Прикрепляет текстовое сообщение к отчету Allure.
     *
     * @param attachName имя вложения
     * @param message    текст сообщения
     * @return текст сообщения
     */
    @Attachment(value = "{attachName}", type = "text/plain")
    public static String attachAsText(String attachName, String message) {
        return message;
    }

    /**
     * Прикрепляет исходный код страницы к отчету Allure.
     *
     * @return исходный код страницы в виде массива байтов
     */
    @Attachment(value = "Исходный код страницы", type = "text/plain")
    public static byte[] attachPageSource() {
        return getWebDriver().getPageSource().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Прикрепляет скриншот к отчету Allure.
     *
     * @param attachName имя вложения
     * @return скриншот в виде массива байтов
     */
    @Attachment(value = "{attachName}", type = "image/png")
    public static byte[] attachScreenshot(String attachName) {
        return ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
    }

    /**
     * Прикрепляет видео к отчету Allure.
     *
     * @return HTML код для вставки видео в отчет
     */
    @Attachment(value = "Видео", type = "text/html", fileExtension = ".html")
    public static String attachVideo() {
        return "<html><body><video width='100%' height='100%' controls autoplay><source src='"
                + System.getProperty("video.storage") + getSessionId() + ".mp4"
                + "' type='video/mp4'></video></body></html>";
    }

    /**
     * Получает идентификатор сессии WebDriver.
     *
     * @return идентификатор сессии
     */
    public static String getSessionId() {
        return ((RemoteWebDriver) getWebDriver()).getSessionId().toString();
    }

    /**
     * Получает логи консоли браузера.
     *
     * @return логи консоли в виде строки
     */
    public static String getConsoleLogs() {
        return String.join("\n", Selenide.getWebDriverLogs(BROWSER));
    }
}
