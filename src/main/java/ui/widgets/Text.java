package ui.widgets;

import com.codeborne.selenide.Condition;
import core.widgets.Widget;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Класс предоставляет методы для чтения текста из строки и проверки ее содержимого.
 */
public class Text extends Widget<Text> {

    /**
     * Конструирует экземпляр LineRead с указанным локатором.
     *
     * @param locator локатор элемента строки типа {@link By}
     */
    public Text(By locator) {
        super(locator);
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
     */
    public Text checkText(String expected) {
        $(locator).shouldHave(Condition.text(expected));
        return this;
    }

    /**
     * Проверяет, что элемент строки отображается.
     */
    public Text shouldBeVisible() {
        $(locator).shouldBe(Condition.visible);
        return this;
    }

    /**
     * Проверяет, что элемент строки скрыт.
     */
    public Text shouldBeHidden() {
        $(locator).shouldBe(Condition.hidden);
        return this;
    }

    /**
     * Проверяет, что элемент строки имеет указанный атрибут со значением.
     *
     * @param attribute имя атрибута
     * @param value     ожидаемое значение атрибута
     */
    public Text shouldHaveAttribute(String attribute, String value) {
        $(locator).shouldHave(Condition.attribute(attribute, value));
        return this;
    }

    /**
     * Проверяет, что элемент строки имеет атрибут "value" со значением.
     *
     * @param value ожидаемое значение атрибута "value"
     */
    public Text shouldHaveValue(String value) {
        $(locator).shouldHave(Condition.value(value));
        return this;
    }

    /**
     * Проверяет, что элемент строки содержит указанный текст.
     *
     * @param text ожидаемый текст в элементе строки
     */
    public Text shouldContainText(String text) {
        $(locator).shouldHave(Condition.text(text));
        return this;
    }

    /**
     * Проверяет, что элемент строки содержит только указанный текст и никакой другой.
     *
     * @param text ожидаемый текст в элементе строки
     */
    public Text shouldHaveExactText(String text) {
        $(locator).shouldHave(Condition.exactText(text));
        return this;
    }
}
