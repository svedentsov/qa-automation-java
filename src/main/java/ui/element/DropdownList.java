package ui.element;

import org.openqa.selenium.By;
import ui.helper.Widget;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

/**
 * Класс предоставляет методы для выбора варианта из выпадающего списка,
 * проверки текста элемента списка и выполнения дополнительных действий.
 */
public class DropdownList extends Widget<DropdownList> {

    /**
     * Конструирует экземпляр DropdownList с указанным локатором.
     *
     * @param locator локатор элемента выпадающего списка типа {@link By}
     */
    public DropdownList(By locator) {
        super(locator);
    }

    /**
     * Выбирает указанный вариант из выпадающего списка.
     *
     * @param variant текст варианта, который нужно выбрать
     */
    public DropdownList choose(String variant) {
        $(locator).selectOption(variant);
        return this;
    }

    /**
     * Проверяет, содержит ли элемент выпадающего списка ожидаемый текст.
     *
     * @param expected ожидаемый текст элемента выпадающего списка
     */
    public DropdownList checkText(String expected) {
        $(locator).shouldHave(text(expected));
        return this;
    }

    /**
     * Проверяет, что текущий выбранный вариант соответствует ожидаемому значению.
     *
     * @param expected ожидаемое значение текущего выбранного варианта
     */
    public DropdownList checkSelectedValue(String expected) {
        $(locator).shouldHave(selectedText(expected));
        return this;
    }

    /**
     * Проверяет, что элемент выпадающего списка видим на странице.
     *
     * @return {@code true}, если элемент видим, иначе {@code false}
     */
    public boolean isVisible() {
        return $(locator).is(visible);
    }

    /**
     * Проверяет, что элемент выпадающего списка скрыт на странице.
     *
     * @return {@code true}, если элемент скрыт, иначе {@code false}
     */
    public boolean isHidden() {
        return $(locator).is(hidden);
    }
}