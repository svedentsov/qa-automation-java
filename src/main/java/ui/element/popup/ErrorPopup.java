package ui.element.popup;

import com.codeborne.selenide.Condition;
import org.openqa.selenium.By;
import ui.helper.Widget;

import static com.codeborne.selenide.Selenide.$;

/**
 * Представляет всплывающее окно с сообщением об ошибке.
 * Этот класс предоставляет методы для взаимодействия с всплывающим окном,
 * такие как закрытие окна, получение текста сообщения об ошибке, и проверка текста.
 */
public class ErrorPopup extends Widget<ErrorPopup> {

    private final By closer = By.cssSelector(".uk-alert-close");
    private final By text = By.cssSelector(".uk-alert p");

    public ErrorPopup(By locator) {
        super(locator);
    }

    /**
     * Закрывает всплывающее окно с сообщением об ошибке.
     *
     * @return экземпляр {@link ErrorPopup}, представляющий текущую страницу или компонент.
     */
    public ErrorPopup close() {
        $(closer).click();
        return this;
    }

    /**
     * Получает текст сообщения об ошибке из всплывающего окна.
     *
     * @return текст сообщения об ошибке.
     */
    public String getText() {
        return $(text).getText();
    }

    /**
     * Получает текст сообщения об ошибке из всплывающего окна,
     * или возвращает "no err", если сообщение об ошибке отсутствует.
     *
     * @return текст сообщения об ошибке или "no err", если сообщение отсутствует.
     */
    public String getTextIfExist() {
        try {
            return $(text).getText();
        } catch (com.codeborne.selenide.ex.ElementNotFound e) {
            return "no err";
        }
    }

    /**
     * Проверяет, содержит ли всплывающее окно сообщение об ошибке с ожидаемым текстом.
     *
     * @param expected ожидаемый текст сообщения об ошибке.
     * @return экземпляр {@link ErrorPopup}, представляющий текущую страницу или компонент.
     */
    public ErrorPopup checkText(String expected) {
        $(text).shouldHave(Condition.text(expected));
        return this;
    }
}
