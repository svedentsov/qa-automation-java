package com.svedentsov.ui.pages.theinternet;

import com.codeborne.selenide.Selenide;
import com.svedentsov.ui.helper.UrlController;
import com.svedentsov.core.annotations.Url;
import com.svedentsov.ui.helper.BrowserActions;
import lombok.Getter;
import org.openqa.selenium.By;
import com.svedentsov.manager.UiManager;
import com.svedentsov.ui.element.popup.ErrorPopup;
import com.svedentsov.ui.element.popup.ModalPopup;

import static com.svedentsov.utils.StrUtil.EMPTY;
import static com.svedentsov.utils.WaitUtils.doWaitMedium;
import static com.svedentsov.utils.WaitUtils.repeatAction;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Абстрактный класс {@code AbstractPage} представляет собой базовую структуру для всех страниц в UI-тестах.
 * Он предоставляет общие методы для работы с веб-страницами, такие как ожидание загрузки страницы, проверка открытия страницы,
 * обновление страницы, и открытие страниц по их классу.
 */
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
            doWaitMedium().untilAsserted(() -> assertThat(browserActions.getCurrentPageUrl())
                    .as("Страница не загружена: %s", getClass().getSimpleName())
                    .matches(getClass().getAnnotation(Url.class).pattern())
            );
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
        return (T) this;
    }

    /**
     * Открывает страницу по заданному классу, используя аннотацию {@code @Url} для формирования URL.
     *
     * @return текущий объект страницы
     */
    public T open() {
        String url = getClass().isAnnotationPresent(Url.class) ?
                getClass().getAnnotation(Url.class).pattern().replaceFirst("\\.\\*", EMPTY) :
                EMPTY;
        return repeatAction(() -> Selenide.open(UrlController.getUiHttpAppHost() + url, (Class<T>) getClass()));
    }
}
