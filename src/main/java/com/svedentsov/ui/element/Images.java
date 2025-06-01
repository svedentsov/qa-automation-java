package com.svedentsov.ui.element;

import org.openqa.selenium.By;
import com.svedentsov.ui.helper.Widget;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

/**
 * Класс предоставляет методы для взаимодействия с изображениями на веб-странице.
 */
public class Images extends Widget<Images> {

    /**
     * Конструирует экземпляр Images с указанным локатором.
     *
     * @param selector локатор элемента изображения типа {@link By}
     */
    public Images(By selector) {
        super(selector);
    }

    /**
     * Проверяет, что изображение отображается.
     *
     * @return текущий объект Images для цепочного вызова методов
     */
    public Images shouldBeVisible() {
        $(locator).shouldBe(visible);
        return this;
    }

    /**
     * Проверяет, что изображение скрыто.
     *
     * @return текущий объект Images для цепочного вызова методов
     */
    public Images shouldBeHidden() {
        $(locator).shouldBe(hidden);
        return this;
    }

    /**
     * Проверяет, что изображение имеет указанный атрибут "src".
     *
     * @param src ожидаемое значение атрибута "src"
     * @return текущий объект Images для цепочного вызова методов
     */
    public Images shouldHaveSrc(String src) {
        $(locator).shouldHave(attribute("src", src));
        return this;
    }

    /**
     * Проверяет, что изображение содержит указанный атрибут "alt".
     *
     * @param altText ожидаемое значение атрибута "alt"
     * @return текущий объект Images для цепочного вызова методов
     */
    public Images shouldHaveAltText(String altText) {
        $(locator).shouldHave(attribute("alt", altText));
        return this;
    }

    /**
     * Получает значение атрибута "src" изображения.
     *
     * @return строка, содержащая значение атрибута "src"
     */
    public String getSrc() {
        return $(locator).shouldBe(visible).getAttribute("src");
    }

    /**
     * Получает значение атрибута "alt" изображения.
     *
     * @return строка, содержащая значение атрибута "alt"
     */
    public String getAltText() {
        return $(locator).shouldBe(visible).getAttribute("alt");
    }

    /**
     * Проверяет, что изображение содержит указанный текст.
     *
     * @param text ожидаемый текст в элементе изображения
     * @return текущий объект Images для цепочного вызова методов
     */
    public Images shouldContainText(String text) {
        $(locator).shouldHave(text(text));
        return this;
    }
}
