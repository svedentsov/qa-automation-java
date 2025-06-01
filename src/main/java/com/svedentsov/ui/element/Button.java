package com.svedentsov.ui.element;

import org.openqa.selenium.By;
import com.svedentsov.ui.helper.Widget;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

/**
 * Класс предоставляет методы для взаимодействия и выполнения действий с кнопками.
 */
public class Button extends Widget<Button> {

    /**
     * Конструирует экземпляр Checkbox с указанным локатором.
     *
     * @param locator локатор элемента чекбокса типа {@link By}
     */
    public Button(By locator) {
        super(locator);
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
     * @param expected ожидаемый текст кнопки
     */
    public Button checkText(String expected) {
        $(locator).shouldHave(text(expected));
        return this;
    }
}
