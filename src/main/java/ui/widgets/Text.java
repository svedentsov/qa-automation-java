package ui.widgets;

import com.codeborne.selenide.Condition;
import ui.pages.UIRouter;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Класс предоставляет методы для чтения текста из строки и проверки ее содержимого.
 */
public class Text extends UIRouter {

    private final By locator;

    /**
     * Конструирует экземпляр LineRead с указанным локатором.
     *
     * @param locator локатор элемента строки типа {@link By}
     */
    public Text(By locator) {
        this.locator = locator;
    }

    /**
     * Получает текст из элемента строки.
     *
     * @return текст из элемента строки
     */
    public String getText() {
        return $(locator).shouldBe(Condition.visible).getText();
    }

    /**
     * Проверяет, содержит ли элемент строки ожидаемый текст.
     *
     * @param expected ожидаемый текст элемента строки
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter checkText(String expected) {
        $(locator).shouldHave(Condition.text(expected));
        return this;
    }

    /**
     * Проверяет, что элемент строки отображается.
     *
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter shouldBeVisible() {
        $(locator).shouldBe(Condition.visible);
        return this;
    }

    /**
     * Проверяет, что элемент строки скрыт.
     *
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter shouldBeHidden() {
        $(locator).shouldBe(Condition.hidden);
        return this;
    }

    /**
     * Проверяет, что элемент строки имеет указанный атрибут со значением.
     *
     * @param attribute имя атрибута
     * @param value     ожидаемое значение атрибута
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter shouldHaveAttribute(String attribute, String value) {
        $(locator).shouldHave(Condition.attribute(attribute, value));
        return this;
    }

    /**
     * Проверяет, что элемент строки имеет атрибут "value" со значением.
     *
     * @param value ожидаемое значение атрибута "value"
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter shouldHaveValue(String value) {
        $(locator).shouldHave(Condition.value(value));
        return this;
    }

    /**
     * Проверяет, что элемент строки содержит указанный текст.
     *
     * @param text ожидаемый текст в элементе строки
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter shouldContainText(String text) {
        $(locator).shouldHave(Condition.text(text));
        return this;
    }

    /**
     * Проверяет, что элемент строки содержит только указанный текст и никакой другой.
     *
     * @param text ожидаемый текст в элементе строки
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter shouldHaveExactText(String text) {
        $(locator).shouldHave(Condition.exactText(text));
        return this;
    }
}
