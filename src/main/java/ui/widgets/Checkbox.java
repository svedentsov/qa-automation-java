package ui.widgets;

import com.codeborne.selenide.Condition;
import org.openqa.selenium.By;
import ui.pages.UIRouter;

import static com.codeborne.selenide.Selenide.$;

/**
 * Класс предоставляет методы для взаимодействия и выполнения действий с чекбоксом.
 */
public class Checkbox extends UIRouter {

    private final By locator;

    /**
     * Конструирует экземпляр Checkbox с указанным локатором.
     *
     * @param locator локатор элемента чекбокса типа {@link By}
     */
    public Checkbox(By locator) {
        this.locator = locator;
    }

    /**
     * Проверяет, выбран ли чекбокс.
     *
     * @return {@code true}, если чекбокс выбран, иначе {@code false}
     */
    public boolean isSelected() {
        return $(locator).isSelected();
    }

    /**
     * Проверяет, отображается ли чекбокс на странице.
     *
     * @return {@code true}, если чекбокс отображается, иначе {@code false}
     */
    public boolean isDisplayed() {
        return $(locator).isDisplayed();
    }

    /**
     * Проверяет, доступен ли чекбокс для взаимодействия.
     *
     * @return {@code true}, если чекбокс доступен, иначе {@code false}
     */
    public boolean isEnabled() {
        return $(locator).isEnabled();
    }

    /**
     * Устанавливает состояние чекбокса в соответствии с переданным флагом.
     *
     * @param flag если {@code true}, то чекбокс будет выбран, если {@code false}, то не выбран
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter set(boolean flag) {
        if (flag) {
            if (!isSelected()) {
                $(locator).click();
            }
        } else {
            if (isSelected()) {
                $(locator).click();
            }
        }
        return this;
    }

    /**
     * Устанавливает состояние чекбокса в выбранный (checked).
     *
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter setTrue() {
        if (!isSelected()) {
            $(locator).click();
        }
        return this;
    }

    /**
     * Проверяет, что чекбокс выбран.
     *
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter checkTrue() {
        $(locator).shouldBe(Condition.selected);
        return this;
    }

    /**
     * Устанавливает состояние чекбокса в не выбранный (unchecked).
     *
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter setFalse() {
        if (isSelected()) {
            $(locator).click();
        }
        return this;
    }

    /**
     * Проверяет, что чекбокс не выбран.
     *
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter checkFalse() {
        $(locator).shouldBe(Condition.not(Condition.selected));
        return this;
    }

    /**
     * Нажимает на чекбокс.
     *
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter clickBox() {
        $(locator).click();
        return this;
    }

    /**
     * Проверяет, что чекбокс недоступен для нажатия (неактивен).
     *
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter checkIsUnclickable() {
        $(locator).shouldBe(Condition.disabled);
        return this;
    }

    /**
     * Проверяет, что чекбокс видим на странице, используя скроллинг при необходимости.
     *
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter scrollIntoView() {
        $(locator).scrollIntoView(true);
        return this;
    }
}
