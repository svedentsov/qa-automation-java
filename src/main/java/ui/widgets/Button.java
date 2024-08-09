package ui.widgets;

import ui.pages.UIRouter;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Класс предоставляет методы для взаимодействия и выполнения действий с кнопками.
 */
public class Button extends UIRouter {

    private final By locator;

    /**
     * Конструирует экземпляр Button с указанным локатором.
     *
     * @param locator локатор кнопки типа {@link By}
     */
    public Button(By locator) {
        this.locator = locator;
    }

    /**
     * Нажимает на кнопку.
     *
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter click() {
        $(locator).shouldBe(Condition.enabled).click();
        return this;
    }

    /**
     * Наводит курсор на кнопку.
     *
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter hover() {
        $(locator).hover();
        return this;
    }

    /**
     * Получает текст кнопки.
     *
     * @return текст кнопки
     */
    public String getText() {
        return $(locator).text();
    }

    /**
     * Проверяет, содержит ли кнопка ожидаемый текст.
     *
     * @param expected ожидаемый текст кнопки.
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter checkText(String expected) {
        $(locator).shouldHave(Condition.text(expected));
        return this;
    }

    /**
     * Ожидает исчезновения кнопки в течение указанного времени.
     *
     * @param seconds максимальное время ожидания исчезновения кнопки, в секундах
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter waitDisappear(int seconds) {
        try {
            Configuration.timeout = 1000L * seconds;
            $(locator).should(Condition.disappear);
        } finally {
            Configuration.timeout = 3000;
        }
        return this;
    }

    /**
     * Ожидает, пока кнопка станет видимой в течение указанного времени.
     *
     * @param seconds максимальное время ожидания появления кнопки, в секундах
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter waitAppear(int seconds) {
        try {
            Configuration.timeout = 1000L * seconds;
            $(locator).should(Condition.appear);
        } finally {
            Configuration.timeout = 3000;
        }
        return this;
    }

    /**
     * Проверяет, существует ли кнопка на странице.
     *
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter shouldExist() {
        $(locator).should(Condition.exist);
        return this;
    }

    /**
     * Проверяет, не существует ли кнопка на странице.
     *
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter shouldNotExist() {
        $(locator).should(Condition.not(Condition.exist));
        return this;
    }

    /**
     * Проверяет, видна ли кнопка.
     *
     * @return {@code true}, если видна, иначе {@code false}
     */
    public boolean isVisible() {
        return $(locator).is(Condition.visible);
    }

    /**
     * Проверяет, скрыта ли кнопка.
     *
     * @return {@code true}, если скрыта, иначе {@code false}
     */
    public boolean isHidden() {
        return $(locator).is(Condition.hidden);
    }

    /**
     * Возвращает количество элементов, соответствующих указанному локатору.
     *
     * @return количество элементов
     */
    public int getElementCount() {
        return $$(locator).size();
    }
}
