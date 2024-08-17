package ui.pages;

import core.annotations.Url;
import org.openqa.selenium.By;
import ui.widgets.Button;

/**
 * Класс предоставляет элементы страницы Exit Intent.
 * Содержит элемент для взаимодействия с модальным окном, которое появляется при попытке покинуть страницу.
 */
@Url(pattern = ".*/exit_intent")
public class ExitIntentPage extends AbstractPage<ExitIntentPage> {
    public Button MOUSE_MOVE_MODAL = new Button(By.xpath("//div[@id='ouibounce-modal']"));
}
