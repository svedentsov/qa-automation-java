package com.svedentsov.ui.helper;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.svedentsov.core.annotations.Url;
import com.svedentsov.utils.BeanTools;
import com.svedentsov.utils.WaitUtils;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.svedentsov.utils.StrUtil.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Класс {@code BrowserActions} предоставляет набор методов для взаимодействия с браузером
 * с использованием библиотеки Selenide. Он включает методы для навигации между страницами,
 * переключения между вкладками и iframe, работы с cookies, и проверки загрузки страниц.
 */
@Slf4j
public class BrowserActions {

    private static final String WAIT_PAGE_LOADED_MESSAGE = "Проверка URL текущей страницы: %s";
    private static final String DOCUMENT_READY_STATE = "complete";
    private static final String JS_DOCUMENT_READY_STATE = "return document.readyState";
    private static final String JS_OPEN_NEW_TAB = "window.open()";
    private static final String JS_TIMEZONE_OFFSET = "var date = new Date(); return date.getTimezoneOffset();";

    /**
     * Перезагружает текущую страницу и ожидает завершения загрузки.
     */
    public void reloadCurrentPage() {
        refresh();
        waitForPageToLoad();
    }

    /**
     * Переключается на iframe по индексу и ожидает завершения загрузки.
     *
     * @param index индекс iframe
     */
    public void switchToIframe(int index) {
        switchTo().frame(index);
        waitForPageToLoad();
    }

    /**
     * Переключается на iframe по элементу Selenide и ожидает завершения загрузки.
     *
     * @param el элемент Selenide, представляющий iframe
     */
    public void switchToIframeByWebElement(SelenideElement el) {
        switchTo().frame(el);
        waitForPageToLoad();
    }

    /**
     * Переключается на последнюю открытую вкладку браузера.
     */
    public void switchToNewBrowserTab() {
        var description = "Ожидание появления новой вкладки браузера";
        // Ожидаем, пока количество вкладок не станет больше 1, и получаем список вкладок
        List<String> tabs = new ArrayList<>(WaitUtils.waitUntilAndGet(
                description,
                this::getBrowserTabNames,
                handles -> handles.size() > 1));
        // Переключаемся на последнюю в списке
        switchToWindowByName(tabs.get(tabs.size() - 1));
    }

    /**
     * Переключается на контент по умолчанию и ожидает завершения загрузки.
     */
    public void switchToDefaultContent() {
        switchTo().defaultContent();
        waitForPageToLoad();
    }

    /**
     * Возвращает набор имен всех вкладок браузера.
     *
     * @return набор имен вкладок
     */
    public Set<String> getBrowserTabNames() {
        return getWebDriver().getWindowHandles();
    }

    /**
     * Возвращает количество вкладок браузера.
     *
     * @return количество вкладок
     */
    public int getBrowserTabsCount() {
        return getWebDriver().getWindowHandles().size();
    }

    /**
     * Нажимает кнопку "Назад" в браузере.
     */
    public void clickBrowserBackButton() {
        back();
    }

    /**
     * Выполняет действие и переключается на новую вкладку браузера, если она появляется.
     *
     * @param action действие для выполнения
     */
    public void switchToNewBrowserTab(Runnable action) {
        int initialTabsCount = getBrowserTabsCount();
        action.run();
        var description = "Ожидание открытия новой вкладки после действия";
        // Ожидаем, что количество вкладок увеличится
        List<String> tabs = new ArrayList<>(WaitUtils.waitUntilAndGet(
                description,
                this::getBrowserTabNames,
                handles -> handles.size() > initialTabsCount));
        // Переключаемся на последнюю в списке
        switchToWindowByName(tabs.get(tabs.size() - 1));
    }

    /**
     * Закрывает текущую вкладку и переключается на последнюю оставшуюся вкладку.
     */
    public void closeCurrentTab() {
        if (getBrowserTabsCount() > 1) {
            closeWindow();
            // После закрытия Selenide автоматически переключается на предыдущую вкладку
            // Но для надежности можно явно переключиться на последнюю из оставшихся
            List<String> tabsList = new ArrayList<>(getBrowserTabNames());
            if (!tabsList.isEmpty()) {
                switchToWindowByName(tabsList.get(tabsList.size() - 1));
            }
        }
    }

    /**
     * Переходит по указанному URL.
     *
     * @param url URL для перехода
     */
    public void navigateToUrl(String url) {
        open(url);
    }

    /**
     * Переходит по указанному URL в новой вкладке.
     *
     * @param url URL для перехода
     */
    public void navigateToUrlNewTab(String url) {
        try {
            switchToNewBrowserTab(() -> executeJavaScript(JS_OPEN_NEW_TAB));
            Selenide.open(url);
            // Явно ждем, пока URL в браузере не будет соответствовать тому, что мы открыли
            WaitUtils.waitUntilCondition(
                    "Ожидание загрузки URL в новой вкладке: " + url,
                    () -> getCurrentPageUrl().startsWith(url));
        } catch (JavascriptException e) {
            log.error("Ошибка при открытии новой вкладки с URL: " + url, e);
            throw new RuntimeException("Не удалось открыть новую вкладку через JS", e);
        }
    }

    /**
     * Возвращает текущий URL страницы.
     *
     * @return текущий URL
     */
    public String getCurrentPageUrl() {
        return getWebDriver().getCurrentUrl();
    }

    /**
     * Открывает страницу по URL на основе аннотации @Url класса и базового URL.
     *
     * @param clazz   класс страницы
     * @param baseUrl базовый URL
     * @param <T>     тип страницы
     * @return экземпляр страницы
     */
    public <T> T openPageByUrl(Class<T> clazz, String baseUrl) {
        return openPageByUrl(clazz, baseUrl, List.of(EMPTY));
    }

    /**
     * Открывает страницу по URL на основе аннотации @Url класса и базового URL с параметрами URL.
     *
     * @param clazz     класс страницы
     * @param baseUrl   базовый URL
     * @param urlParams параметры URL
     * @param <T>       тип страницы
     * @return экземпляр страницы
     */
    public <T> T openPageByUrl(Class<T> clazz, String baseUrl, List<String> urlParams) {
        if (clazz.isAnnotationPresent(Url.class)) {
            String replaceToken = "\\.\\*";
            String url = clazz.getAnnotation(Url.class).pattern()
                    .replaceFirst(replaceToken, EMPTY)
                    .replaceAll("(\\\\)|(\\.\\*$)", EMPTY);

            for (String urlParam : urlParams) {
                url = url.replaceFirst(replaceToken, urlParam);
            }
            return Selenide.open(baseUrl + url, clazz);
        } else {
            String errorMsg = String.format("Нет аннотации @Url для класса %s", clazz.getCanonicalName());
            log.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
    }

    /**
     * Добавляет cookie в браузер.
     *
     * @param cookie cookie для добавления
     */
    public void addCookie(Cookie cookie) {
        getWebDriver().manage().addCookie(cookie);
    }

    /**
     * Удаляет cookie по имени.
     *
     * @param cookieName имя cookie
     */
    public void deleteCookie(String cookieName) {
        getWebDriver().manage().deleteCookieNamed(cookieName);
    }

    /**
     * Возвращает cookie по имени.
     *
     * @param cookieName имя cookie
     * @return cookie
     */
    public Cookie getCookie(String cookieName) {
        return getWebDriver().manage().getCookieNamed(cookieName);
    }

    /**
     * Возвращает смещение часового пояса браузера.
     *
     * @return смещение часового пояса
     */
    public long getBrowserTimeZoneOffset() {
        // Явно приводим результат к Number и получаем long, чтобы избежать ClassCastException
        Object raw = executeJavaScript(JS_TIMEZONE_OFFSET);
        if (raw instanceof Number) {
            return ((Number) raw).longValue();
        } else {
            log.warn("Невозможно получить смещение часового пояса: ожидается Number, получено {}", raw);
            throw new IllegalStateException("Неверный тип результата JS: " + raw);
        }
    }

    /**
     * Проверяет, что текущий URL страницы соответствует аннотации @Url класса.
     *
     * @param pageClass класс страницы
     * @return true, если URL соответствует, иначе выбрасывает исключение
     */
    public boolean checkCurrentPageAt(Class<?> pageClass) {
        if (pageClass.isAnnotationPresent(Url.class)) {
            var description = String.format("Проверка, что текущая страница - '%s'", pageClass.getSimpleName());
            WaitUtils.waitUntilAsserted(description, () -> {
                String readyState = executeJavaScript(JS_DOCUMENT_READY_STATE);
                assertThat(readyState).isEqualTo(DOCUMENT_READY_STATE);
                assertThat(getCurrentPageUrl())
                        .as(WAIT_PAGE_LOADED_MESSAGE, pageClass.getSimpleName())
                        .matches(pageClass.getAnnotation(Url.class).pattern());
            });
            return true;
        }
        String errorMsg = String.format("Класс %s не имеет аннотации URL", pageClass.getName());
        log.error(errorMsg);
        throw new IllegalArgumentException(errorMsg);
    }

    /**
     * Проверяет, что текущая страница загружена на основе аннотации @Url класса.
     *
     * @param pageClass класс страницы
     * @return true, если страница загружена, иначе выбрасывает исключение
     */
    public static boolean isPageLoaded(Class<?> pageClass) {
        if (pageClass.isAnnotationPresent(Url.class)) {
            return getWebDriver().getCurrentUrl().matches(pageClass.getAnnotation(Url.class).pattern());
        }
        String errorMsg = String.format("Класс %s не имеет аннотации URL", pageClass.getName());
        log.error(errorMsg);
        throw new IllegalArgumentException(errorMsg);
    }

    /**
     * Ожидает загрузки страницы и возвращает экземпляр страницы, если она загружена.
     *
     * @param pageClass класс страницы
     * @param <T>       тип страницы
     * @return экземпляр страницы или пустой Optional, если страница не загружена
     */
    public <T> Optional<T> waitForPageLoaded(Class<T> pageClass) {
        var description = String.format("Проверка загрузки страницы '%s'", pageClass.getSimpleName());
        boolean isLoaded = WaitUtils.checkConditionWithoutException(description,
                () -> assertThat(getCurrentPageUrl()).matches(pageClass.getAnnotation(Url.class).pattern()));

        if (isLoaded) {
            return Optional.of(BeanTools.createObject(pageClass));
        } else {
            log.info("Страница '{}' не была загружена в течение таймаута.", pageClass.getSimpleName());
            return Optional.empty();
        }
    }

    /**
     * Ожидает загрузки страницы, проверяя состояние документа.
     */
    private void waitForPageToLoad() {
        var description = "Ожидание полной загрузки DOM (document.readyState == 'complete')";
        WaitUtils.waitUntilAsserted(description, () -> {
            String readyState = executeJavaScript(JS_DOCUMENT_READY_STATE);
            assertThat(readyState).isEqualTo(DOCUMENT_READY_STATE);
        });
    }

    /**
     * Переключается на окно браузера по его имени.
     *
     * @param windowName имя окна
     */
    private void switchToWindowByName(String windowName) {
        switchTo().window(windowName);
    }
}
