package ui.widgets;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.WebDriverRunner;
import core.widgets.Widget;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import ui.helper.PageManager;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

import static com.codeborne.selenide.Selenide.$;

/**
 * Класс предоставляет методы для взаимодействия с полем ввода, такие как очистка,
 * ввод текста, вставка текста и другие действия.
 */
public class Input extends Widget<Input> {

    /**
     * Конструирует экземпляр InputLine с указанным локатором.
     *
     * @param locator локатор элемента ввода типа {@link By}
     */
    public Input(By locator) {
        super(locator);
    }

    /**
     * Вводит указанный текст в поле ввода.
     *
     * @param text текст для ввода
     * @return экземпляр {@link PageManager}, представляющий текущую страницу или компонент
     */
    public Input fill(String text) {
        $(locator).sendKeys(text);
        return this;
    }

    /**
     * Копирует указанный текст в системный буфер обмена и вставляет его в поле ввода.
     *
     * @param text текст для вставки
     * @return экземпляр {@link PageManager}, представляющий текущую страницу или компонент
     */
    public Input insert(String text) {
        try { // 'ctrl+c' for local browser
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
        } catch (HeadlessException e) {
            e.getMessage();
        }
        $(locator).sendKeys(Keys.CONTROL + "v"); // 'ctrl+v'
        return this;
    }

    /**
     * Очищает поле ввода и затем вводит указанный текст.
     *
     * @param text текст для ввода
     * @return экземпляр {@link PageManager}, представляющий текущую страницу или компонент
     */
    public Input clearWithFill(String text) {
        $(locator).clear();
        $(locator).sendKeys(text);
        return this;
    }

    /**
     * Очищает поле ввода, вводит текст, а затем выполняет клавишу DELETE.
     *
     * @param text текст для ввода
     * @return экземпляр {@link PageManager}, представляющий текущую страницу или компонент
     */
    public Input clearAndFillWithDelete(String text) {
        $(locator).clear();
        $(locator).sendKeys(text, Keys.DELETE);
        return this;
    }

    /**
     * Очищает поле ввода, вводит символ, а затем удаляет его с помощью клавиши BACK_SPACE.
     *
     * @return экземпляр {@link PageManager}, представляющий текущую страницу или компонент
     */
    public Input clear() {
        $(locator).clear();
        // backspaces to get error
        $(locator).sendKeys("a");
        $(locator).sendKeys(Keys.BACK_SPACE);
        return this;
    }

    /**
     * Вводит указанный текст в поле ввода и затем нажимает клавишу Enter.
     *
     * @param text текст для ввода
     * @return экземпляр {@link PageManager}, представляющий текущую страницу или компонент
     */
    public Input fillWithEnter(String text) {
        $(locator).sendKeys(text);
        $(locator).pressEnter();
        return this;
    }

    /**
     * Получает текст из поля ввода.
     *
     * @return текст из поля ввода
     */
    public String getText() {
        return $(locator).getAttribute("value");
    }

    /**
     * Проверяет, содержит ли поле ввода ожидаемый текст.
     *
     * @param expected ожидаемый текст
     * @return экземпляр {@link PageManager}, представляющий текущую страницу или компонент
     */
    public Input checkText(String expected) {
        String current = $(locator).getAttribute("value");
        if (!current.equals(expected)) {
            throw new AssertionError("Line content is incorrect! Expected '" + expected + "', got '" + current + "'!");
        }
        return this;
    }

    /**
     * Выполняет действия для ввода текста с использованием объекта {@link Actions}.
     *
     * @param text текст для ввода
     * @return экземпляр {@link PageManager}, представляющий текущую страницу или компонент
     */
    public Input actionsFill(String text) {
        // useful for js-filling fields
        Action action = new Actions(WebDriverRunner.getWebDriver())
                .moveToElement($(locator).shouldBe(Condition.exist).toWebElement())
                .click()
                .sendKeys(text)
                .build();
        action.perform();
        return this;
    }

    /**
     * Выполняет действия для очистки поля ввода с использованием объекта {@link Actions}.
     *
     * @return экземпляр {@link PageManager}, представляющий текущую страницу или компонент
     */
    public Input actionsClear() {
        // useful for js-filling fields
        Action action = new Actions(WebDriverRunner.getWebDriver())
                .moveToElement($(locator).shouldBe(Condition.exist).toWebElement())
                .click()
                .sendKeys("a")
                .sendKeys(Keys.BACK_SPACE)
                .build();
        action.perform();
        return this;
    }


    /**
     * Выполняет действие "вырезать" в поле ввода.
     *
     * @return экземпляр {@link PageManager}, представляющий текущую страницу или компонент
     */
    public Input cutText() {
        $(locator).sendKeys(Keys.chord(Keys.CONTROL, "x"));
        return this;
    }

    /**
     * Перемещает фокус на поле ввода с использованием клавиши TAB.
     */
    public Input focusOnInput() {
        $(locator).sendKeys(Keys.TAB);
        return this;
    }

    /**
     * Вводит текст в поле ввода, используя клавиши TAB для перехода к следующему элементу на странице.
     *
     * @param text текст для ввода
     * @return экземпляр {@link PageManager}, представляющий текущую страницу или компонент
     */
    public Input fillWithTab(String text) {
        $(locator).sendKeys(text, Keys.TAB);
        return this;
    }

    /**
     * Прокручивает страницу вниз до видимости поля ввода.
     *
     * @return экземпляр {@link PageManager}, представляющий текущую страницу или компонент
     */
    public Input scrollToView() {
        $(locator).scrollTo();
        return this;
    }
}
