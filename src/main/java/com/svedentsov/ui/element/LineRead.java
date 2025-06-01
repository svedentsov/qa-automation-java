package com.svedentsov.ui.element;

import org.openqa.selenium.By;
import com.svedentsov.ui.helper.Widget;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

/**
 * Класс предоставляет методы для чтения текста из строки и проверки ее содержимого.
 */
public class LineRead extends Widget<LineRead> {

    /**
     * Конструирует экземпляр LineRead с указанным локатором.
     *
     * @param locator локатор элемента строки типа {@link By}
     */
    public LineRead(By locator) {
        super(locator);
    }

    /**
     * Получает текст из элемента строки.
     *
     * @return текст из элемента строки
     */
    public String getText() {
        return $(locator).shouldBe(visible).getText();
    }

    /**
     * Проверяет, содержит ли элемент строки ожидаемый текст.
     *
     * @param expected ожидаемый текст элемента строки
     */
    public LineRead checkText(String expected) {
        $(locator).shouldHave(text(expected));
        return this;
    }

    /**
     * Проверяет, что элемент строки отображается.
     */
    public LineRead shouldBeVisible() {
        $(locator).shouldBe(visible);
        return this;
    }

    /**
     * Проверяет, что элемент строки скрыт.
     */
    public LineRead shouldBeHidden() {
        $(locator).shouldBe(hidden);
        return this;
    }

    /**
     * Проверяет, что элемент строки имеет указанный атрибут со значением.
     *
     * @param attribute имя атрибута
     * @param value     ожидаемое значение атрибута
     */
    public LineRead shouldHaveAttribute(String attribute, String value) {
        $(locator).shouldHave(attribute(attribute, value));
        return this;
    }

    /**
     * Проверяет, что элемент строки имеет атрибут "value" со значением.
     *
     * @param value ожидаемое значение атрибута "value"
     */
    public LineRead shouldHaveValue(String value) {
        $(locator).shouldHave(value(value));
        return this;
    }

    /**
     * Проверяет, что элемент строки содержит указанный текст.
     *
     * @param text ожидаемый текст в элементе строки
     */
    public LineRead shouldContainText(String text) {
        $(locator).shouldHave(text(text));
        return this;
    }

    /**
     * Проверяет, что элемент строки содержит только указанный текст и никакой другой.
     *
     * @param text ожидаемый текст в элементе строки
     */
    public LineRead shouldHaveExactText(String text) {
        $(locator).shouldHave(exactText(text));
        return this;
    }
}
