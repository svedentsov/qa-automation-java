package ui.widgets.popup;

import com.codeborne.selenide.Condition;
import core.widgets.Widget;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Представляет модальное всплывающее окно на веб-странице.
 * Этот класс предоставляет методы для взаимодействия и выполнения действий с модальным окном.
 */
public class ModalPopup extends Widget<ModalPopup> {

    private final By closer = By.cssSelector(".uk-modal-close");
    private final By text = By.cssSelector(".uk-modal-body");

    public ModalPopup(By locator) {
        super(locator);
    }

    /**
     * Закрывает модальное окно, нажимая на элемент закрытия.
     */
    public ModalPopup close() {
        $(closer).click();
        return this;
    }

    /**
     * Проверяет, содержит ли текст модального окна ожидаемый текст.
     *
     * @param expected ожидаемый текст в модальном окне.
     */
    public ModalPopup checkText(String expected) {
        $(text).shouldHave(Condition.text(expected));
        return this;
    }
}
