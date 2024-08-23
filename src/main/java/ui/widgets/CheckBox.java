package ui.widgets;

import org.openqa.selenium.By;
import ui.helper.Widget;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

/**
 * Класс предоставляет методы для взаимодействия и выполнения действий с чекбоксом.
 */
public class CheckBox extends Widget<CheckBox> {

    /**
     * Конструирует экземпляр Checkbox с указанным локатором.
     *
     * @param locator локатор элемента чекбокса типа {@link By}
     */
    public CheckBox(By locator) {
        super(locator);
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
     */
    public CheckBox set(boolean flag) {
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
     */
    public CheckBox setTrue() {
        if (!isSelected()) {
            $(locator).click();
        }
        return this;
    }

    /**
     * Проверяет, что чекбокс выбран.
     */
    public CheckBox checkTrue() {
        $(locator).shouldBe(selected);
        return this;
    }

    /**
     * Устанавливает состояние чекбокса в не выбранный (unchecked).
     */
    public CheckBox setFalse() {
        if (isSelected()) {
            $(locator).click();
        }
        return this;
    }

    /**
     * Проверяет, что чекбокс не выбран.
     */
    public CheckBox checkFalse() {
        $(locator).shouldBe(not(selected));
        return this;
    }

    /**
     * Нажимает на чекбокс.
     */
    public CheckBox clickBox() {
        $(locator).click();
        return this;
    }

    /**
     * Проверяет, что чекбокс недоступен для нажатия (неактивен).
     */
    public CheckBox checkIsUnclickable() {
        $(locator).shouldBe(disabled);
        return this;
    }

    /**
     * Проверяет, что чекбокс видим на странице, используя скроллинг при необходимости.
     */
    public CheckBox scrollIntoView() {
        $(locator).scrollIntoView(true);
        return this;
    }
}
