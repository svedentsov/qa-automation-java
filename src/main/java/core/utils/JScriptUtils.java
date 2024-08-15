package core.utils;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import core.config.PropertiesController;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.UncheckedIOException;
import java.time.Duration;

import static com.codeborne.selenide.Selenide.executeJavaScript;
import static core.utils.WaitUtils.doWait;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Duration.ONE_SECOND;

/**
 * Утилитарный класс для работы с JavaScript в тестах.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JScriptUtils {

    private static final Duration WAIT_PAGE_TIMEOUT = PropertiesController.appTimeoutConfig().utilWaitTimeout();

    /**
     * Загружает и выполняет скрипт jQuery из указанного файла.
     *
     * @param fileWithPath путь к файлу со скриптом
     */
    public static void loadJQuery(String fileWithPath) {
        WebDriverRunner.getWebDriver().manage().timeouts().setScriptTimeout(WAIT_PAGE_TIMEOUT.toMillis(), MILLISECONDS);
        executeJavaScript(getScript(fileWithPath));
        waitForJQueryLoaded();
    }

    /**
     * Читает содержимое файла и возвращает его как строку скрипта.
     *
     * @param fileWithPath путь к файлу со скриптом
     * @return содержимое файла в виде строки
     * @throws IllegalArgumentException если файл не найден
     */
    public static String getScript(String fileWithPath) {
        try {
            return FileUtil.readFile(fileWithPath);
        } catch (UncheckedIOException e) {
            throw new IllegalArgumentException("Не удалось найти файл скрипта", e);
        }
    }

    /**
     * Ожидает, пока jQuery будет загружен и все AJAX-запросы завершены.
     */
    private static void waitForJQueryLoaded() {
        doWait().pollInterval(ONE_SECOND)
                .until(() -> Boolean.TRUE.equals(executeJavaScript("return !!window.jQuery && window.jQuery.active == 0")));
    }

    /**
     * Прокручивает страницу до указанного элемента.
     *
     * @param element элемент, до которого нужно прокрутить страницу
     * @return элемент, до которого прокрутили страницу
     */
    public static SelenideElement scrollTo(SelenideElement element) {
        executeJavaScript("arguments[0].scrollIntoView();", element);
        return element;
    }
}
