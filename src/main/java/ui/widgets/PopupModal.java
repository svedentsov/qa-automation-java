package ui.widgets;

import com.codeborne.selenide.Condition;
import ui.pages.UIRouter;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Представляет модальное всплывающее окно на веб-странице.
 * Этот класс предоставляет методы для взаимодействия и выполнения действий с модальным окном.
 */
public class PopupModal extends UIRouter {

    private final By closer = By.cssSelector(".uk-modal-close");
    private final By text = By.cssSelector(".uk-modal-body");

    /**
     * Закрывает модальное окно, нажимая на элемент закрытия.
     *
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент.
     */
    public UIRouter close() {
        $(closer).click();
        return this;
    }

    /**
     * Проверяет, содержит ли текст модального окна ожидаемый текст.
     *
     * @param expected ожидаемый текст в модальном окне.
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент.
     */
    public UIRouter checkText(String expected) {
        $(text).shouldHave(Condition.text(expected));
        return this;
    }
}
