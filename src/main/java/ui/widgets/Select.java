package ui.widgets;

import com.codeborne.selenide.Condition;
import core.widgets.Widget;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Класс предоставляет методы для выбора варианта из выпадающего списка,
 * проверки текста элемента списка и выполнения дополнительных действий.
 */
public class Select extends Widget<Select> {

    /**
     * Конструирует экземпляр DropdownList с указанным локатором.
     *
     * @param locator локатор элемента выпадающего списка типа {@link By}
     */
    public Select(By locator) {
        super(locator);
    }

    /**
     * Выбирает указанный вариант из выпадающего списка.
     *
     * @param variant текст варианта, который нужно выбрать
     */
    public Select choose(String variant) {
        $(locator).selectOption(variant);
        return this;
    }

    /**
     * Проверяет, содержит ли элемент выпадающего списка ожидаемый текст.
     *
     * @param expected ожидаемый текст элемента выпадающего списка
     */
    public Select checkText(String expected) {
        $(locator).shouldHave(Condition.text(expected));
        return this;
    }

    /**
     * Проверяет, что текущий выбранный вариант соответствует ожидаемому значению.
     *
     * @param expected ожидаемое значение текущего выбранного варианта
     */
    public Select checkSelectedValue(String expected) {
        $(locator).shouldHave(Condition.selectedText(expected));
        return this;
    }

    /**
     * Проверяет, что элемент выпадающего списка видим на странице.
     *
     * @return {@code true}, если элемент видим, иначе {@code false}
     */
    public boolean isVisible() {
        return $(locator).is(Condition.visible);
    }

    /**
     * Проверяет, что элемент выпадающего списка скрыт на странице.
     *
     * @return {@code true}, если элемент скрыт, иначе {@code false}
     */
    public boolean isHidden() {
        return $(locator).is(Condition.hidden);
    }
}