package com.svedentsov.ui.helper;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.svedentsov.core.annotations.Url;
import com.svedentsov.utils.BeanTools;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.ConditionTimeoutException;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.TimeoutException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.svedentsov.utils.StrUtil.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static com.svedentsov.utils.WaitUtils.*;

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
        getWebDriver().navigate().refresh();
        waitForPageToLoad();
    }

    /**
     * Переключается на iframe по индексу и ожидает завершения загрузки.
     *
     * @param index индекс iframe
     */
    public void switchToIframe(int index) {
        getWebDriver().switchTo().frame(index);
        waitForPageToLoad();
    }

    /**
     * Переключается на iframe по элементу Selenide и ожидает завершения загрузки.
     *
     * @param el элемент Selenide, представляющий iframe
     */
    public void switchToIframeByWebElement(SelenideElement el) {
        getWebDriver().switchTo().frame(el);
        waitForPageToLoad();
    }

    /**
     * Переключается на новую вкладку браузера.
     */
    public void switchToNewBrowserTab() {
        doWait().until(this::getBrowserTabNames, tabs -> tabs.size() > 1)
                .stream().reduce((a, b) -> b).ifPresent(this::switchToWindowByName);
    }

    /**
     * Переключается на контент по умолчанию и ожидает завершения загрузки.
     */
    public void switchToDefaultContent() {
        getWebDriver().switchTo().defaultContent();
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
        getWebDriver().navigate().back();
    }

    /**
     * Выполняет действие и переключается на новую вкладку браузера, если она появляется.
     *
     * @param action действие для выполнения
     */
    public void switchToNewBrowserTab(Runnable action) {
        int tabsCnt = getBrowserTabNames().size();
        action.run();
        doWait().until(this::getBrowserTabNames, tabs -> tabs.size() > tabsCnt)
                .stream().reduce((a, b) -> b).ifPresent(this::switchToWindowByName);
    }

    /**
     * Закрывает текущую вкладку и переключается на последнюю оставшуюся вкладку.
     */
    public void closeCurrentTab() {
        if (getBrowserTabNames().size() > 1) {
            getWebDriver().close();
            List<String> tabsList = new ArrayList<>(getBrowserTabNames());
            getWebDriver().switchTo().window(tabsList.get(tabsList.size() - 1));
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
            repeatAction(() -> {
                Selenide.open(url);
                return url;
            });
        } catch (TimeoutException | JavascriptException e) {
            log.error("Ошибка при открытии новой вкладки с URL: " + url, e);
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
            log.error(String.format("Нет аннотации @Url для класса %s", clazz.getCanonicalName()));
            throw new RuntimeException(String.format("Нет аннотации @Url для класса %s", clazz.getCanonicalName()));
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
        return executeJavaScript(JS_TIMEZONE_OFFSET);
    }

    /**
     * Проверяет, что текущий URL страницы соответствует аннотации @Url класса.
     *
     * @param pageClass класс страницы
     * @return true, если URL соответствует, иначе выбрасывает исключение
     */
    public boolean checkCurrentPageAt(Class<?> pageClass) {
        if (pageClass.isAnnotationPresent(Url.class)) {
            doWait().untilAsserted(() -> assertThat(getCurrentPageUrl())
                    .as(WAIT_PAGE_LOADED_MESSAGE, pageClass.getSimpleName())
                    .matches(pageClass.getAnnotation(Url.class).pattern()));
            return true;
        }
        log.error(String.format("Класс %s не имеет аннотации URL", pageClass.getName()));
        throw new IllegalArgumentException(String.format("Класс %s не имеет аннотации URL", pageClass.getName()));
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
        log.error(String.format("Класс %s не имеет аннотации URL", pageClass.getName()));
        throw new IllegalArgumentException(String.format("Класс %s не имеет аннотации URL", pageClass.getName()));
    }

    /**
     * Ожидает загрузки страницы и возвращает экземпляр страницы, если она загружена.
     *
     * @param pageClass класс страницы
     * @param <T>       тип страницы
     * @return экземпляр страницы или пустой Optional, если страница не загружена
     */
    public <T> Optional<T> waitForPageLoaded(Class<T> pageClass) {
        try {
            checkCurrentPageAt(pageClass);
            return Optional.of(BeanTools.createObject(pageClass));
        } catch (ConditionTimeoutException e) {
            log.info(String.format(WAIT_PAGE_LOADED_MESSAGE, pageClass.getSimpleName()), e);
        }
        return Optional.empty();
    }

    /**
     * Ожидает загрузки страницы, проверяя состояние документа.
     */
    private void waitForPageToLoad() {
        waitAssertCondition(() ->
                assertThat(DOCUMENT_READY_STATE).isEqualTo(executeJavaScript(JS_DOCUMENT_READY_STATE)));
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
