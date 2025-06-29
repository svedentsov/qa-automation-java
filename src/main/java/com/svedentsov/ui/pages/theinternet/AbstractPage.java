package com.svedentsov.ui.pages.theinternet;

import com.codeborne.selenide.Selenide;
import com.svedentsov.core.annotations.Url;
import com.svedentsov.steps.manager.UiManager;
import com.svedentsov.ui.element.popup.ErrorPopup;
import com.svedentsov.ui.element.popup.ModalPopup;
import com.svedentsov.ui.helper.BrowserActions;
import com.svedentsov.ui.helper.UrlController;
import com.svedentsov.utils.WaitUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import static com.svedentsov.utils.StrUtil.EMPTY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Абстрактный класс {@code AbstractPage} представляет собой базовую структуру для всех страниц в UI-тестах.
 * Он предоставляет общие методы для работы с веб-страницами, такие как ожидание загрузки страницы, проверка открытия страницы,
 * обновление страницы, и открытие страниц по их классу.
 */
@Slf4j
@Getter
public abstract class AbstractPage<T extends AbstractPage<T>> {

    protected BrowserActions browserActions = new BrowserActions();
    protected UiManager ui = UiManager.getManager();
    protected ModalPopup modalPopup = new ModalPopup(By.xpath(""));
    protected ErrorPopup errorPopup = new ErrorPopup(By.xpath(""));

    /**
     * Ожидает загрузки страницы, проверяя соответствие текущего URL страницы аннотации {@code @Url}.
     *
     * @return текущий объект страницы
     */
    public T waitPage() {
        if (getClass().isAnnotationPresent(Url.class)) {
            String description = String.format("Ожидание загрузки страницы '%s'", getClass().getSimpleName());
            WaitUtils.waitUntilAsserted(
                    description,
                    () -> assertThat(browserActions.getCurrentPageUrl())
                            .as("URL не соответствует паттерну страницы '%s'", getClass().getSimpleName())
                            .matches(getClass().getAnnotation(Url.class).pattern()),
                    WaitUtils.TIMEOUT_MEDIUM);
        }
        return (T) this;
    }

    /**
     * Проверяет, открыта ли текущая страница на основе аннотации {@code @Url}.
     *
     * @return {@code true}, если страница открыта, иначе выбрасывает исключение
     */
    public boolean isOpened() {
        return browserActions.checkCurrentPageAt(getClass());
    }

    /**
     * Перезагружает текущую страницу и ожидает завершения загрузки.
     *
     * @return текущий объект страницы
     */
    public T refreshPage() {
        browserActions.reloadCurrentPage();
        return waitPage();
    }

    /**
     * Открывает страницу по заданному классу, используя аннотацию {@code @Url} для формирования URL.
     *
     * @return текущий объект страницы
     */
    @SuppressWarnings("unchecked")
    public T open() {
        String urlPattern = getClass().isAnnotationPresent(Url.class)
                ? getClass().getAnnotation(Url.class).pattern()
                : EMPTY;

        // Убираем регулярные выражения, чтобы получить чистый путь
        String cleanPath = urlPattern.replaceFirst("\\.\\*", EMPTY)
                .replaceAll("\\\\", "");

        if (cleanPath.isEmpty() && !urlPattern.isEmpty()) {
            log.warn("Аннотация @Url для класса {} содержит только регулярное выражение. Открытие может быть некорректным.", getClass().getSimpleName());
        }

        // Команда Selenide.open уже включает в себя ожидания и возвращает инстанс страницы.
        // Оборачивание в repeatAction является анти-паттерном.
        return Selenide.open(UrlController.getUiHttpAppHost() + cleanPath, (Class<T>) getClass());
    }
}
